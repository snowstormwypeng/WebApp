package Device;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;


/**
 * Created by Administrator on 2017-09-02.
 */

public class DeviceInfo {
    /**
     * 获取手机 Imemi 号
     * @return
     */
    public static String GetImei(Context ctx) {

        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }
        catch (Exception e)
        {
            //Intent intent = new Intent(ctx,CaptureActivity.class);
            //ctx.startActivity(intent);
        }
        return "";
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static String GetOsVer()
    {
        return Build.VERSION.RELEASE;
    }
    /**
     * 获取系统版本号
     * @return
     */
    public static String GetOsVerCode()
    {
        return Build.VERSION.SDK;
    }
}
