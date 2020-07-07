package com.easefun.polyv.livecommon.ui.window;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

/**
 * 基础activity
 */
public class PLVBaseActivity extends AppCompatActivity {
    // <editor-fold defaultstate="collapsed" desc="成员变量">
    private final static int APP_STATUS_KILLED = 0; // 表示应用是被杀死后在启动的
    private final static int APP_STATUS_RUNNING = 1; // 表示应用时正常的启动流程
    private static int APP_STATUS = APP_STATUS_KILLED; // 记录App的启动状态
    protected boolean isCreateSuccess;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="处理异常启动时的相关方法">
    private String getLaunchActivityName() {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(getPackageName());
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(resolveIntent, 0);
        if (resolveInfos != null)
            for (ResolveInfo resolveInfo : resolveInfos) {
                return resolveInfo.activityInfo.name;
            }
        return null;
    }

    private int getTaskActivityCount() {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        if (am == null)
            return -1;
        try {
            // getBusinessProtocol the info from the currently running task
            List<ActivityManager.RunningTaskInfo> taskInfos = am.getRunningTasks(1);
            if (taskInfos != null)
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfos) {
                    return taskInfo.numActivities;
                }
        } catch (Exception e) {
        }
        return -1;
    }

    public boolean restartApp() {
        try {
            Intent intent = new Intent(this, Class.forName(getLaunchActivityName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Activity方法">
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
            savedInstanceState.putParcelable("android:fragments", null);
        }
        super.onCreate(savedInstanceState);
        isCreateSuccess = false;
        boolean launchActivityItBaseActivity = false;
        try {
            launchActivityItBaseActivity = getLaunchActivityName() != null && PLVBaseActivity.class.isAssignableFrom(Class.forName(getLaunchActivityName()));//父/等
        } catch (Exception e) {
        }
        if (!launchActivityItBaseActivity || (getClass().getName().equals(getLaunchActivityName()) && getTaskActivityCount() < 2)) {
            APP_STATUS = APP_STATUS_RUNNING;
        }
        if (APP_STATUS == APP_STATUS_KILLED && restartApp()) { // 非正常启动流程，直接重新初始化应用界面
            return;
        }
        isCreateSuccess = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isCreateSuccess = false;
    }

    //新增的findViewById()方法，用于兼容support 25
    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) super.findViewById(id);
    }
    // </editor-fold>
}
