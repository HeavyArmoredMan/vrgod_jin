package io.virtualapp.home.models;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.lody.virtual.remote.InstalledAppInfo;

/**
 * @author Lody
 */
public class PackageAppData implements AppData {

    public String packageName;
    public String name;
    public Drawable icon;
    public boolean fastOpen;
    public boolean isFirstOpen;
    public boolean isLoading;
    public boolean isConverted;

    public AppInfo mAppInfo;

    public AppInfo getAppInfo(){
        return mAppInfo;
    }

    public PackageAppData(Context context, InstalledAppInfo installedAppInfo) {
        this.packageName = installedAppInfo.packageName;
        this.isFirstOpen = !installedAppInfo.isLaunched(0);
        loadData(context, installedAppInfo.getApplicationInfo(installedAppInfo.getInstalledUsers()[0]));
        this.isConverted = true;
    }
    public PackageAppData(Context context,AppInfo installedAppInfo) {
        this.packageName = installedAppInfo.packageName;
        this.isFirstOpen = false;
        this.name = installedAppInfo.name.toString();
        this.icon = installedAppInfo.icon;
        this.isConverted = false;
        this.mAppInfo = installedAppInfo;
    }

    private void loadData(Context context, ApplicationInfo appInfo) {
        if (appInfo == null) {
            return;
        }
        PackageManager pm = context.getPackageManager();
        try {
            CharSequence sequence = appInfo.loadLabel(pm);
            if (sequence != null) {
                name = sequence.toString();
            }
            icon = appInfo.loadIcon(pm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean isFirstOpen() {
        return isFirstOpen;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean canReorder() {
        return true;
    }

    @Override
    public boolean canLaunch() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean canCreateShortcut() {
        return true;
    }
}
