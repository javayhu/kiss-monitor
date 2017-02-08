package com.javayhu.kiss.monitor.network;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.javayhu.kiss.monitor.base.BaseTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 网络监控
 * <p>
 * 注意：网络数据是根据uid来统计的，所以单应用多进程的情况下得到的是多个进程的网络数据总和
 * TODO: 这个数据和Android Studio Monitor中的数据有偏差，可能是代码或者统计方式有问题
 * <p>
 * 第一种方式是查询文件/proc/uid_stat/<uid>/udp_snd 和 /proc/uid_stat/<uid>/udp_rcv，这个方法拿到的是udp的流量
 * 第二种方式是通过TrafficStats类提供的getUidTxBytes(int uid)方法，该方法号称是获取到指定uid发送流量的总和，但实测情况是只有tcp层的流量
 * <p>
 * Created by javayhu on 1/22/17.
 */
public class NetworkTracker extends BaseTracker {

    private static final String TAG = NetworkTracker.class.getSimpleName();

    public float mRxRate = 0.0f;
    public float mTxRate = 0.0f;

    private boolean isFirstTime;
    private long mLastRxBytes = 0l;
    private long mLastTxBytes = 0l;
    private double mLastUptime;//上次数据采集时的uptime
    private NetworkStatReceiver mNetworkStatReceiver;

    public NetworkTracker(Context context) {
        super(context);
        isFirstTime = true;
        mNetworkStatReceiver = new NetworkStatReceiver(getUid());
    }

    //获取uid，这种方式和Process.myUid的结果是一样的
    private int getUid() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            //noinspection WrongConstant
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean updateContent() {
        int interval = getCpuInterval();
        if (interval > 0) {
            try {
                mNetworkStatReceiver.update();
            } catch (Exception e) {
                e.printStackTrace();
                mRxRate = 0.0f;
                mTxRate = 0.0f;
                return false;
            }

            if (isFirstTime) {
                mLastRxBytes = mNetworkStatReceiver.getRxBytes();
                mLastTxBytes = mNetworkStatReceiver.getTxBytes();
                mRxRate = 0.0f;
                mTxRate = 0.0f;
                isFirstTime = false;
                return true;
            }

            float rxBytesIncreased = 0.0f;
            float txBytesIncreased = 0.0f;
            long rxBytesInTotal = mNetworkStatReceiver.getRxBytes();
            if (rxBytesInTotal > mLastRxBytes) {
                rxBytesIncreased = rxBytesInTotal - mLastRxBytes;
                mLastRxBytes = rxBytesInTotal;
            }

            long txBytesInTotal = mNetworkStatReceiver.getTxBytes();
            if (txBytesInTotal > mLastTxBytes) {
                txBytesIncreased = txBytesInTotal - mLastTxBytes;
                mLastTxBytes = txBytesInTotal;
            }

            mRxRate = rxBytesIncreased / 1024.f / interval;
            mTxRate = txBytesIncreased / 1024.f / interval;
            return true;
        }
        return false;
    }

    /**
     * 获取自上次获取数据到现在经过的时间间隔
     */
    private int getCpuInterval() {
        int interval = 0;
        try {
            FileReader fileReader = new FileReader("/proc/uptime");
            BufferedReader bufferedReader = new BufferedReader(fileReader, 512);
            String line = bufferedReader.readLine();
            if (line != null) {
                String[] parts = line.split("\\s+");
                double uptime = Double.parseDouble(parts[0]);
                if (mLastUptime > 0) {
                    interval = (int) (uptime - mLastUptime);
                }
                mLastUptime = uptime;
                return interval;
            }
            bufferedReader.close();
            fileReader.close();
            return 0;
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 读取网络传输数据量大小
     */
    class NetworkStatReceiver {

        private final int INDEX_OF_UID = 3;
        private final int INDEX_OF_RX_BYTES = 5;
        private final int INDEX_OF_TX_BYTES = 7;

        private final int myUid;
        private final String LINE_SPLIT_REGEX = "[ \t\r\n\f]";
        private final File NETWORK_STAT_FILE = new File("/proc/net/xt_qtaguid/stats");

        private long myRxBytes;
        private long myTxBytes;

        NetworkStatReceiver(int uid) {
            myUid = uid;
        }

        public void update() throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(NETWORK_STAT_FILE));
            String line = null;

            while ((line = reader.readLine()) != null) {
                // Line starting with idx is the header.
                if (line.startsWith("idx")) {
                    continue;
                }
                if (line.contains("No such file")) {
                    throw new Exception("no such file " + NETWORK_STAT_FILE.getAbsolutePath());
                }
                if (!line.contains(String.valueOf(myUid))) {
                    continue;
                }
                String[] values = line.split(LINE_SPLIT_REGEX);
                if (values.length < INDEX_OF_TX_BYTES) {
                    continue;
                }
                // Gets the network usages belonging to the uid.
                try {
                    int lineUid = Integer.parseInt(values[INDEX_OF_UID]);
                    if (myUid == lineUid) {
                        int tempRxBytes = Integer.parseInt(values[INDEX_OF_RX_BYTES]);
                        int tempTxBytes = Integer.parseInt(values[INDEX_OF_TX_BYTES]);
                        if (tempRxBytes < 0 || tempTxBytes < 0) {
                            Log.w(TAG, String.format("Negative rxBytes %1$d and/or txBytes %2$d in %3$s", tempRxBytes, tempTxBytes, line));
                            continue;
                        }
                        myRxBytes += tempRxBytes;
                        myTxBytes += tempTxBytes;
                    }
                } catch (NumberFormatException e) {
                    throw new Exception(String.format("Expected int value, instead got uid %1$s, rxBytes %2$s, txBytes %3$s", values[INDEX_OF_UID],
                            values[INDEX_OF_RX_BYTES], values[INDEX_OF_TX_BYTES]));
                }
            }
        }

        public long getRxBytes() {
            return myRxBytes;
        }

        public long getTxBytes() {
            return myTxBytes;
        }
    }

}
