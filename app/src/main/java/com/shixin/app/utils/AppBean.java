package com.shixin.app.utils;

import android.graphics.drawable.Drawable;


/**
 * AppBean
 *
 * @author LiuPeiming
 *         Created at 2018/4/27 17:03
 */

public class AppBean {

    private Drawable appIcon;
    private String appName;
    private double appSize;
    private boolean isSd = false;
    public boolean isSystem = false;
    private String appPackageName;
    public String sourceDir;
    public String appVersion;

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    private String apkPath;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public double getAppSize() {
        return appSize;
    }

    public void setAppSize(double appSize) {
        this.appSize = appSize;
    }

    public boolean isSd() {
        return isSd;
    }

    public void setSd(boolean sd) {
        isSd = sd;
    }

//    public boolean isSystem() {
//        return isSystem;
//    }

//    public void setSystem(boolean system) {
//        isSystem = system;
//    }

}