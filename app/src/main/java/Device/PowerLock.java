package Device;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

/**
 * Created by 王彦鹏 on 2017-11-17.
 */

public class PowerLock {
    public static void wakeAndUnlock(Context ctx, boolean lock) {
        KeyguardManager km = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        if (lock) {
            wl.acquire();
        } else {
            kl.reenableKeyguard();
        }
        //释放
        wl.release();
    }
}
