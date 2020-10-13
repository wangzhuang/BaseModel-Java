package com.king.basemodel.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.webkit.WebView;

import androidx.multidex.MultiDex;

import com.king.basemodel.R;
import com.king.basemodel.common.GlobalKt;
import com.king.basemodel.utils.OsUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePalApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;




public class AppContext extends LitePalApplication {
    public static final String TAG = "AppContext";

    private static AppContext instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initWebView();
        GlobalKt.setAppCtx(this);
        if (!isMainProccess()) {
            return;
        }
        initLog();
        initBugly();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    /**
     * 初始化内存泄漏检测工具LeakCanary
     */
//    private void initLeakCanary() {
//        if (true) {//控制是否开启LeakCanary
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }
//    }


    //Android 9 以上部分机型webViev 报错
    private void initWebView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            String processName = getProcessName(this);
            if (!"com.cwsk.twowheeler".equals(processName)){//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName);}
        }
    }
    public String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }



    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .tag(getString(R.string.app_name))
                .methodCount(0)
                .methodOffset(1)
                .build()));
        Logger.addLogAdapter(new DiskLogAdapter(CsvFormatStrategy.newBuilder().tag(getString(R.string.app_name)).build()));
    }

    private void initBugly() {
        Context context = getApplicationContext();
        String packageName = context.getPackageName();
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        CrashReport.initCrashReport(context, "9476e1807a", true, strategy);
    }

    // 方法超过64K 分包 巨坑
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }


    private boolean isMainProccess() {
        // 如果应用中采用多进程方式，oncreate方法会执行多次，根据不同的进程名字进行不同的初始化，
        String processName = OsUtils.getProcessName(this,
                android.os.Process.myPid());
        return !(processName == null | !processName.equals(getPackageName()));
    }
}
