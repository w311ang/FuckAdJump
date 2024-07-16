package top.weixiansen574.fuckadjump;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPosedMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // 拦截Uri.parse方法
        XposedHelpers.findAndHookMethod(Uri.class, "parse", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String arg = (String) param.args[0];

                XposedBridge.log(arg);

                if (arg == null) {
                    return;
                }
                if (arg.startsWith("http")) {
                    return;
                }
                if (arg.startsWith("openapp.jdmobile")) {
                    XposedBridge.log("尝试跳转京东！");
                    showDialog(param, (Activity) param.thisObject, "检测到跳转京东，是否继续？");
                }
                if (arg.startsWith("tbopen")) {
                    XposedBridge.log("尝试跳转淘宝！");
                    showDialog(param, (Activity) param.thisObject, "检测到跳转淘宝，是否继续？");
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
                        XposedBridge.log("尝试跳转京东！");
                        showDialog(param, (Activity) param.thisObject, "检测到跳转京东，是否继续？");
                    }
                    if (uriString.startsWith("tbopen")) {
                        XposedBridge.log("尝试跳转淘宝！");
                        showDialog(param, (Activity) param.thisObject, "检测到跳转淘宝，是否继续？");
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
                    XposedBridge.log("尝试跳转京东！");
                    showDialog(param, (Activity) param.thisObject, "检测到跳转京东，是否继续？");
                }
                if (url.startsWith("tbopen")) {
                    XposedBridge.log("尝试跳转淘宝！");
                    showDialog(param, (Activity) param.thisObject, "检测到跳转淘宝，是否继续？");
                }
            }
        });
    }

    private void showDialog(final XC_MethodHook.MethodHookParam param, final Activity activity, String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity)
                        .setTitle("跳转提示")
                        .setMessage(message)
                        .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                param.setResult(null);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
}
