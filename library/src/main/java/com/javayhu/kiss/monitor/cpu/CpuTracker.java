package com.javayhu.kiss.monitor.cpu;

import android.content.Context;
import android.os.Process;

import com.javayhu.kiss.monitor.base.BaseTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 监控CPU占用
 * <p>
 * Created by javayhu on 1/20/17.
 */
public class CpuTracker extends BaseTracker {

    private static final String TAG = CpuTracker.class.getSimpleName();

    private Long mPreviousUserUsage = null;
    private Long mPreviousKernelUsage = null;
    private Long mPreviousTotalUptime = null;

    private ProcessStatReceiver mProcessStatReceiver;
    private SystemStatReceiver mSystemStatReceiver;

    public float mKernelPercentUsage = 0.0f;
    public float mUserPercentUsage = 0.0f;

    public CpuTracker(Context context) {
        super(context);
        int pid = Process.myPid();
        mProcessStatReceiver = new ProcessStatReceiver(pid);
        mSystemStatReceiver = new SystemStatReceiver();
    }

    @Override
    public boolean updateContent() {
        try {
            mSystemStatReceiver.update();
            mProcessStatReceiver.update();
        } catch (Exception e) {
            e.printStackTrace();
            mKernelPercentUsage = 0.0f;
            mUserPercentUsage = 0.0f;
            return false;
        }

        Long kernelCpuUsage = mProcessStatReceiver.getKernelCpuUsage();
        Long userCpuUsage = mProcessStatReceiver.getUserCpuUsage();
        Long totalUptime = mSystemStatReceiver.getTotalUptime();

        if (kernelCpuUsage != null && userCpuUsage != null && totalUptime != null) {
            if (mPreviousKernelUsage != null && mPreviousUserUsage != null && mPreviousTotalUptime != null) {
                long totalTimeDiff = totalUptime - mPreviousTotalUptime;
                if (totalTimeDiff > 0) {
                    float kernelPercentUsage = (float) (kernelCpuUsage - mPreviousKernelUsage) * 100.0f / (float) totalTimeDiff;
                    mKernelPercentUsage = Math.max(Math.min(kernelPercentUsage, 100.0f), 0.0f);
                    float userPercentUsage = (float) (userCpuUsage - mPreviousUserUsage) * 100.0f / (float) totalTimeDiff;
                    mUserPercentUsage = Math.max(Math.min(userPercentUsage, 100.0f), 0.0f);
                }
            }
            mPreviousKernelUsage = kernelCpuUsage;
            mPreviousUserUsage = userCpuUsage;
            mPreviousTotalUptime = totalUptime;
            return true;
        }

        return false;
    }

    /**
     * 读取进程的stat信息，获取user ticks和system ticks
     */
    class ProcessStatReceiver {

        private final int pid;
        private final File PROC_STAT_FILE;

        private Long myUserCpuTicks = null;
        private Long myKernelCpuTicks = null;

        public ProcessStatReceiver(int pid) {
            this.pid = pid;
            this.PROC_STAT_FILE = new File(String.format("/proc/%d/stat", pid));
        }

        public void update() throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(PROC_STAT_FILE));
            String line = reader.readLine();

            String[] tokens = line.split("\\s+");
            if (tokens.length >= 15) {
                // Refer to Linux proc man page for the contents at the specified indices.
                Integer myPid = Integer.parseInt(tokens[0]);
                if (pid != myPid) {
                    throw new Exception("invalid process id!");
                }
                myUserCpuTicks = Long.parseLong(tokens[13]);//utime
                myKernelCpuTicks = Long.parseLong(tokens[14]);//stime
            }
        }

        public Long getUserCpuUsage() {
            return myUserCpuTicks;
        }

        public Long getKernelCpuUsage() {
            return myKernelCpuTicks;
        }
    }

    /**
     * 读取cpu的stat信息，将所有的time加起来作为数据采样间隔时间内的tick
     */
    class SystemStatReceiver {

        private final File CPU_STAT_FILE = new File("/proc/stat");
        private Long myTotalUptime = null;

        public void update() throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(CPU_STAT_FILE));
            String line = reader.readLine();
            String[] tokens = line.split("\\s+");
            if (tokens.length < 11 || !tokens[0].equals("cpu")) {
                throw new Exception("invalid cpu stat file?");
            }
            long totalUptime = 0l;
            // Assuming total uptime is the sum of all given numerical values on the aggregated CPU line.
            for (int i = 1; i < tokens.length; ++i) {
                totalUptime += Long.parseLong(tokens[i]);
            }
            myTotalUptime = totalUptime;
        }

        public Long getTotalUptime() {
            return myTotalUptime;
        }
    }

}
