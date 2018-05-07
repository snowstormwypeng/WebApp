package Helper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by 王彦鹏 on 2017-09-08.
 */

public class App {
    /**
     * 获取版本号 如：V1.0.0.0
     * @param ctx
     * @return
     */
    public static String getVersionName(Context ctx)
    {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            return  pi.versionName;
        }
        catch (Exception e)
        {
            return "V1.0.0.0";
        }
    }
    /**
     * 获取版本号 如：1000
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx)
    {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            return  pi.versionCode;
        }
        catch (Exception e)
        {
            return 0;
        }
    }
}
