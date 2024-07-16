package top.weixiansen574.fuckadjump;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XPosedMain implements IXposedHookLoadPackage {
    private static final String TAG = "FuckAdJump";
    private static final String LOG_FILE_NAME = "FuckAdJump.log";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String LOG_PATH = "/storage/emulated/0/Android/data/" + loadPackageParam.packageName + "/files/" + LOG_FILE_NAME;
        Boolean LOG_ENABLED = (new File(LOG_PATH)).exists();
        if (LOG_ENABLED) {
            Logger logger = Logger.getLogger(TAG);
            logger.addHandler(new FileHandler(LOG_PATH));
        }

        XposedHelpers.findAndHookMethod(Uri.class,"parse",String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String arg = (String) param.args[0];

                Log.e(TAG, arg);
                if (LOG_ENABLED) {
                    logger.info(arg);
                }

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
}
