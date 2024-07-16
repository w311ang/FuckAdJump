package top.weixiansen574.fuckadjump;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPosedMain implements IXposedHookLoadPackage {
    private static final String TAG = "XPosedMain";
    private static final String LOG_FILE_NAME = "hook_log.txt";
    private Context appContext;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 获取应用的上下文
        Object activityThread = XposedHelpers.callStaticMethod(
                XposedHelpers.findClass("android.app.ActivityThread", null),
                "currentActivityThread"
        );
        appContext = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
        packageName = loadPackageParam.packageName;

        XposedBridge.log("h65");
        Log.d("fuckad", "hhubhj");
        logToFile("hhubhj");
        // 拦截Uri.parse方法
        XposedHelpers.findAndHookMethod(Uri.class, "parse", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String arg = (String) param.args[0];

                XposedBridge.log("hiii");
                Log.d("fuckad", "hhuj");
                logToFile("hhubbhhj");
                XposedBridge.log(arg);

                if (arg == null) {
                    return;
                }
                if (arg.startsWith("http")) {
                    return;
                }
                if (arg.startsWith("openapp.jdmobile")) {
                    XposedBridge.log("已拦截跳转京东！");
                    param.args[0] = "";
                }
                if (arg.startsWith("tbopen")) {
                    XposedBridge.log("已拦截跳转淘宝！");
                    param.args[0] = "";
                }
            }
        });

        // 拦截Activity的startActivity方法
        XposedHelpers.findAndHookMethod("android.app.Activity", loadPackageParam.classLoader, "startActivity", Intent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Intent intent = (Intent) param.args[0];
                Uri uri = intent.getData();
                if (uri != null) {
                    String uriString = uri.toString();
                    XposedBridge.log(uriString);
                    if (uriString.startsWith("openapp.jdmobile")) {
                        XposedBridge.log("已拦截跳转京东！");
                        param.setResult(null);
                    }
                    if (uriString.startsWith("tbopen")) {
                        XposedBridge.log("已拦截跳转淘宝！");
                        param.setResult(null);
                    }
                }
            }
        });

        // 拦截WebView的loadUrl方法
        XposedHelpers.findAndHookMethod(WebView.class, "loadUrl", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String url = (String) param.args[0];
                XposedBridge.log(url);
                if (url.startsWith("openapp.jdmobile")) {
                    XposedBridge.log("已拦截跳转京东！");
                    param.setResult(null);
                }
                if (url.startsWith("tbopen")) {
                    XposedBridge.log("已拦截跳转淘宝！");
                    param.setResult(null);
                }
            }
        });
    }

    private void logToFile(String message) {
        File file = new File("/storage/emulated/0/Android/data/", packageName, "files/", LOG_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write((message + "\n").getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error writing log to file", e);
        }
    }
}
