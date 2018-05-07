package enjoyerp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.updateapp.UpdateManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

import java.util.List;

import BaseActivity.BaseActivity;
import Entity.RequestPermissionsResult;
import Helper.App;
import Helper.EnjoyTools;
import Helper.Msgbox;
import Interface.IAsynCallBackListener;
import Interface.ICard;


/**
 * Created by 王彦鹏 on 2017-08-25.
 */


public class JsCallInterface  {
    public String mCallJsName="";
    private  Context    mContext;
    public static  int webViewCount=0;
    private Handler mHandler = new Handler();
    private WebView mwebview;
    public boolean MultiModel=false;
    public int ReLoadWeb=0;
    public ICard card;


    /**
     * 卡移入事件回调接口
     */
    public String CallCardInName="";

    /**
     * 消息传递
     */
    public String CallMsg="";


    public void CallJs(final String MName, final String Param)
    {
        if (MName!="") {
            System.out.println("CallName:" + MName + "  Param:" + Param);
            mHandler.post(new Runnable() {
                public void run() {
                    mwebview.loadUrl("javascript:" + MName + "('" + Param + "' );");
                }
            });
        }
        else
        {
            System.out.println("未产生Js回调，接口不存在");
        }
    }

    public JsCallInterface(Context ctx,WebView view)
    {
        mContext=ctx;
        mwebview=view;

    }

    /**
     * 获取软件版本号
     *
     * @return
     */
    public int getVersionCode()
    {
        return App.getVersionCode(mContext);
    }

    @JavascriptInterface
    public void CloseParentActivity()
    {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MY_RECEIVER");
            ((Activity)mContext).sendBroadcast(intent);
        }
        catch (Exception e)
        {

        }
    }

    @JavascriptInterface
    public void Close()
    {
        try {
            ((Activity)mContext).setResult(ReLoadWeb);
            ((Activity)mContext).finish();
            ((Activity)mContext).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            MainActivity.urlList.remove(MainActivity.urlList.indexOf(mwebview.getUrl()));
            webViewCount-=1;
        }
        catch (Exception e)
        {

        }
    }
    @JavascriptInterface
    public String GetAppVer()
    {
        return App.getVersionName(mContext);
    }

    @JavascriptInterface
    public int GetAppVersion()
    {
        try {
            // 检查软件更新
            int versionCode = getVersionCode();
            return versionCode;
        }
        catch (Exception E)
        {
            return 2017062218;
        }
    }
    /**
     * 启动Activity
     * @param Aname
     */
    @JavascriptInterface
    public void CallActivity(String Aname) {
        try {
            Class a = Class.forName(Aname);
            Intent intent = new Intent(mContext, a);
            mContext.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 扫描二维码
     * @return 返回二维码信息字符串
     */
    @JavascriptInterface
    public void ScanQrcode(final String CallJs)
    {
        if (!((BaseActivity)mContext).CheckPermission(Manifest.permission.CAMERA)) {
            ((BaseActivity) mContext).RequestPermission(Manifest.permission.CAMERA, new IAsynCallBackListener() {
                @Override
                public void onFinish(Object sender) {
                    List<RequestPermissionsResult> list=(List<RequestPermissionsResult>)sender;
                    for ( RequestPermissionsResult permiss:list) {
                        if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.CAMERA")
                                && permiss.getRequestRes()==0) {
                            mCallJsName = CallJs;
                            Intent intent = new Intent(mContext, CaptureActivity.class);
                            ((Activity) mContext).startActivityForResult(intent, 111);
                        }
                    }
                }

                @Override
                public void onError(Object sender, Exception e) {

                }
            });
        }
        else {
            mCallJsName = CallJs;
            Intent intent = new Intent(mContext, CaptureActivity.class);
            ((Activity) mContext).startActivityForResult(intent, 111);
        }
    }

    /**
     * 设置多View模式
     * @param model（true 多View,每一个链接为一个View，否则为单View,所有的页面都在一个View上。
     */
    @JavascriptInterface
    public void SetMultiModel(boolean model)
    {
        MultiModel=model;
    }

    @JavascriptInterface
     public void UpdateApp(final String url, final String des)
    {
        try {
            if (!((BaseActivity)mContext).CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ((BaseActivity)mContext).RequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new IAsynCallBackListener() {
                    @Override
                    public void onFinish(Object sender) {
                        List<RequestPermissionsResult> list = (List<RequestPermissionsResult>) sender;
                        for (RequestPermissionsResult permiss : list) {
                            if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")
                                    && permiss.getRequestRes() == 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    // 检查软件更新
                                    UpdateManager manager = new UpdateManager(mContext);
                                    manager.DownApp(url, des);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Object sender, Exception e) {

                    }
                });
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    UpdateManager manager = new UpdateManager(mContext);
                    manager.DownApp(url, des);
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                if (!((BaseActivity) mContext).CheckPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
                    ((BaseActivity) mContext).RequestPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, new IAsynCallBackListener() {

                        @Override
                        public void onFinish(Object sender) {
                            List<RequestPermissionsResult> list = (List<RequestPermissionsResult>) sender;
                            for (RequestPermissionsResult permiss : list) {
                                if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
                                        && permiss.getRequestRes() == 0) {
                                    // 检查软件更新
                                    UpdateManager manager = new UpdateManager(mContext);
                                    manager.DownApp(url, des);
                                }
                            }
                        }

                        @Override
                        public void onError(Object sender, Exception e) {

                        }
                    });
                } else {
                    // 检查软件更新
                    UpdateManager manager = new UpdateManager(mContext);
                    manager.DownApp(url, des);
                }
            }
        }
        catch (Exception E)
        {

        }
    }
    @JavascriptInterface
    public void SetReLoadWeb(int flag)
    {
        ReLoadWeb=flag;
    }

    @JavascriptInterface
    public int AuthentReadKey(int SectorNo,String KeyStr) {
        String str = "";
        try {
            if (card==null)
            {
                Msgbox.Show(mContext,"请重新刷卡！");
                return 1;
            }
            if (!card.ExistsCard()) {
                Msgbox.Show(mContext, "请贴卡后再操作");
                return 1;
            }
            byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
            if(card.AuthentReadKey(SectorNo,key))
            {
                return 0;
            }
            else
            {
                return 3;
            }
        } catch (Exception E) {
            Msgbox.Show(mContext,  E.getMessage());
        }
        return 1;
    }
    @JavascriptInterface
    public boolean AuthentWtiteKey(int SectorNo,String KeyStr) {
        String str = "";
        if (card==null)
        {
            Msgbox.Show(mContext, "请重新刷卡！");
            return false;
        }
        try {
            if (!card.ExistsCard()) {
                Msgbox.Show(mContext,  "请贴卡后再操作");
                return false;
            }
            byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
            return card.AuthentWruteKey(SectorNo,key);
        } catch (Exception E) {
            Msgbox.Show(mContext, E.getMessage());
        }
        return false;
    }

    @JavascriptInterface
    public String ReadSector(int SectorNo,String KeyStr) throws Exception {
        if (card==null)
        {
            Msgbox.Show(mContext, "请重新刷卡！");
            return "";
        }
        byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
        byte[] result=(card).ReadSector(SectorNo,key);
        return EnjoyTools.ByteArrayToHexString(result);
    }

    @JavascriptInterface
    public String ReadBlock(int BlockNo,String KeyStr) throws Exception {
        if (card==null)
        {
            Msgbox.Show(mContext, "请重新刷卡！");
            return "";
        }
        if (!card.ExistsCard()) {
            Msgbox.Show(mContext,  "请贴卡后再操作");
            return "";
        }
        byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
        byte[] result=card.ReadBlock(BlockNo,key);
        if (result==null)
        {
            return "读卡失败";
        }
        else {
            return EnjoyTools.ByteArrayToHexString(result);
        }
    }

    @JavascriptInterface
    public int WriteSector(int SectorNo,String Data,String KeyStr) throws Exception {
        byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
        byte[] writedata= EnjoyTools.HexStrToBytes(Data);
        return (card).WriteSector(SectorNo,writedata,key);
    }

    @JavascriptInterface
    public int WriteBlock(int BlockNo,String Data,String KeyStr) throws Exception {
        byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
        byte[] writedata= EnjoyTools.HexStrToBytes(Data);
        return (card).WriteBlock(BlockNo,writedata,key);
    }

    @JavascriptInterface
    public int GetSectorCount()
    {
        try {
            return card.GetSectorCount();
        }
        catch (Exception e)
        {
            return 1;
        }
    }

    @JavascriptInterface
    public int GetBlockCount()
    {
        return card.GetBlockCount();
    }

}
