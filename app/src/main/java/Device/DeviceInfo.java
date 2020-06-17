package Device;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;


/**
 * Created by Administrator on 2017-09-02.
 */

public class DeviceInfo {
    /**
     * 获取手机 Imemi 号
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String GetImei(Context ctx) {

        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return tm.getImei();
            }
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
