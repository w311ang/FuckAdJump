package top.weixiansen574.fuckadjump;

import android.net.Uri;
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
    private static final String TAG = "FuckAdJump";
    private static final String LOG_FILE_NAME = "FuckAdJump.log";
    private String packageName;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        packageName = loadPackageParam.packageName;

        XposedHelpers.findAndHookMethod(Uri.class,"parse",String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String arg = (String) param.args[0];

                Log.e(TAG, arg);
                logToFile(arg);

                if (arg == null){
                    return;
                }
                if (arg.startsWith("http")){
                    return;
                }
                if (arg.startsWith("openapp.jdmobile")){
                    XposedBridge.log("已拦截跳转京东！");
                    param.args[0]= "";
                }
                if (arg.startsWith("tbopen")){
                    XposedBridge.log("已拦截跳转淘宝！");
                    param.args[0]= "";
                }
            }
        });
    }

    private void logToFile(String message) {
        File file = new File("/storage/emulated/0/Android/data/" + packageName + "/files/", LOG_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write((message + "\n").getBytes());
        } catch (FileNotFoundException e) {
            Log.e("[" + TAG + "] Error writing log to file", e);
        } catch (IOException e) {
            XposedBridge.log("[" + TAG + "] Error writing log to file", e);
        }
    }
}
