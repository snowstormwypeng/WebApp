package com.example.enjoy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import BaseActivity.BaseActivity;

import com.example.updateapp.IDownProgress;
import com.example.updateapp.UpdateManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;

import android.Manifest;
import android.widget.ProgressBar;

import org.json.JSONObject;

import java.util.List;

import Entity.RequestPermissionsResult;
import Enums.MsgType;
import Factory.Factory;
import Helper.App;
import Helper.EnjoyTools;
import Helper.Msgbox;
import Interface.IAsynCallBackListener;
import Interface.ICard;
import Listener.IAsynListener;
import NetComm.CommProtocol;
import NetComm.INetComm;


/**
 * Created by 王彦鹏 on 2017-08-25.
 */


public class JsCallInterface extends ContextWrapper {
    public String mCallJsName="";
    public static  int webViewCount=0;
    private Handler mHandler = new Handler();
    private WebView mwebview;
    public boolean MultiModel=false;
    public int ReLoadWeb=0;
    public ICard card;
    private INetComm commProtocol = Factory.GetInstance(CommProtocol.class, null);


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
        super(ctx);
        mwebview=view;
    }

    /**
     * 获取软件版本号
     *
     * @return
     */
    public int getVersionCode()
    {
        return App.getVersionCode(this.getBaseContext());
    }

    @JavascriptInterface
    public void CloseParentActivity()
    {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MY_RECEIVER");
            ((Activity)this.getBaseContext()).sendBroadcast(intent);
        }
        catch (Exception e)
        {

        }
    }

    @JavascriptInterface
    public void Close()
    {
        try {
            ((Activity)this.getBaseContext()).setResult(ReLoadWeb);
            ((Activity)this.getBaseContext()).finish();
            ((Activity)this.getBaseContext()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        return App.getVersionName(this.getBaseContext());
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
            Intent intent = new Intent(this.getBaseContext(), a);
            this.getBaseContext().startActivity(intent);
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
        if (!((BaseActivity)this.getBaseContext()).CheckPermission(Manifest.permission.CAMERA)) {
            ((BaseActivity) this.getBaseContext()).RequestPermission(Manifest.permission.CAMERA, new IAsynCallBackListener() {
                @Override
                public void onFinish(Object sender) {
                    List<RequestPermissionsResult> list=(List<RequestPermissionsResult>)sender;
                    for ( RequestPermissionsResult permiss:list) {
                        if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.CAMERA")
                                && permiss.getRequestRes()==0) {
                            mCallJsName = CallJs;
                            Intent intent = new Intent(JsCallInterface.this.getBaseContext(), CaptureActivity.class);
                            ((Activity) JsCallInterface.this.getBaseContext()).startActivityForResult(intent, 111);
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
            Intent intent = new Intent(this.getBaseContext(), CaptureActivity.class);
            ((Activity) this.getBaseContext()).startActivityForResult(intent, 111);
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

    /**
     * 检查软件升级
     */
    public void UpdateVerify(String appName, Integer appVer, final IAsynListener callback){
        String url ="http://up.yingjiayun.com/AppUpdate/UpdateVerify?appName="+appName+"&CurrentVer="+appVer;
        try {
            commProtocol.Get(url, new IAsynListener() {
                @Override
                public void onFinish(Object sender, Object data) {
                    try {
                        String ResStr = data.toString();
                        JSONObject jsonObject = new JSONObject(ResStr);
                        callback.onFinish(sender,jsonObject);
                    }catch (Exception e){
                        e.printStackTrace();
                        callback.onError(sender,e);
                    }
                }

                @Override
                public void onError(Object sender, Exception e) {
                    callback.onError(sender,e);
                }
            });
        }catch (Exception e){
            callback.onError(this, e);
        }
    }

    public void UpdateVerify(){
        String appName = App.getAppName(this.getBaseContext());
        int versionCode = App.getVersionCode(this.getBaseContext());
        UpdateVerify(appName, versionCode, new IAsynListener() {
            @Override
            public void onFinish(Object sender, Object data) {
                final JSONObject jsonObject = (JSONObject) data;
                Message msg = new Message();
                msg.obj = new IAsynListener() {
                    @Override
                    public void onFinish(Object sender, Object data) {
                        try {
                            if(jsonObject.getInt("Result")==0){
//                                final AlertDialog dialog=Msgbox.ShowDialog(getBaseContext(), R.layout.update_dialog);
//                                dialog.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                        final AlertDialog dialog = Msgbox.ShowDialog(getBaseContext(), R.layout.progressdialog_view);
//                                        Message msg = new Message();
//                                        msg.arg1=1;
//                                        msg.obj = new IAsynListener() {
//                                            @Override
//                                            public void onFinish(Object sender, Object data) {
//                                                try {
//                                                    UpdateManager.DownFile(getBaseContext(), jsonObject.getJSONObject("JsonData").getString("DownUrl"),
//                                                            true, new IDownProgress() {
//                                                                @Override
//                                                                public void DownProgress(int fileSize, int downLoad, String localFileName) {
//                                                                    ProgressBar progressBar = dialog.findViewById(R.id.update_progress);
//                                                                    progressBar.setMax(fileSize);
//                                                                    progressBar.setProgress(downLoad);
//                                                                    filename = localFileName;
//                                                                    if(downLoad >= fileSize){
//                                                                        dialog.dismiss();
//                                                                    }
//                                                                }
//                                                            });
//                                                    view.setEnabled(true);
//                                                }catch (Exception e){
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onError(Object sender, Exception e) {
//
//                                            }
//                                        };
//                                        dialog.setCanceledOnTouchOutside(false);
//                                        dialog.setCancelable(false);
//                                        mHandler.sendMessage(msg);
//                                        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                UpdateManager.DownCancel(filename);
//                                                dialog.dismiss();
//                                                view.setEnabled(true);
//                                            }
//                                        });
//                                    }
//                                });
//                                dialog.setCanceledOnTouchOutside(false);
//                                dialog.setCancelable(false);
//                                dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                        view.setEnabled(true);
//                                    }
//                                });
                            }else {
//                                Msgbox.Show(getBaseContext(), "已是最新版本，不用升级。",
//                                        MsgType.msg_Hint, new IAsynListener() {
//                                            @Override
//                                            public void onFinish(Object sender, Object data) {
//                                                view.setEnabled(true);
//                                            }
//
//                                            @Override
//                                            public void onError(Object sender, Exception e) {
//
//                                            }
//                                        });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Object sender, Exception e) {

                    }
                };
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(Object sender, Exception e) {

            }
        });
    }

    @JavascriptInterface
     public void UpdateApp(final String url, final String des)
    {
        try {
            if (!((BaseActivity)this.getBaseContext()).CheckPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                ((BaseActivity)this.getBaseContext()).RequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new IAsynCallBackListener() {
                    @Override
                    public void onFinish(Object sender) {
                        List<RequestPermissionsResult> list = (List<RequestPermissionsResult>) sender;
                        for (RequestPermissionsResult permiss : list) {
                            if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")
                                    && permiss.getRequestRes() == 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    // 检查软件更新
                                    UpdateManager.DownFile(JsCallInterface.this.getBaseContext(), url, true, new IDownProgress() {
                                        @Override
                                        public void DownProgress(int i, int i1, String s) {

                                        }
                                    });
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
                    UpdateManager.DownFile(JsCallInterface.this.getBaseContext(), url, true, new IDownProgress() {
                        @Override
                        public void DownProgress(int i, int i1, String s) {

                        }
                    });
                }
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                if (!((BaseActivity) this.getBaseContext()).CheckPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)) {
                    ((BaseActivity) this.getBaseContext()).RequestPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, new IAsynCallBackListener() {

                        @Override
                        public void onFinish(Object sender) {
                            List<RequestPermissionsResult> list = (List<RequestPermissionsResult>) sender;
                            for (RequestPermissionsResult permiss : list) {
                                if (permiss.getPermissionsName().equalsIgnoreCase("android.permission.MOUNT_UNMOUNT_FILESYSTEMS")
                                        && permiss.getRequestRes() == 0) {
                                    // 检查软件更新
                                    UpdateManager.DownFile(JsCallInterface.this.getBaseContext(), url, true, new IDownProgress() {
                                        @Override
                                        public void DownProgress(int i, int i1, String s) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onError(Object sender, Exception e) {

                        }
                    });
                } else {
                    // 检查软件更新
                    UpdateManager.DownFile(JsCallInterface.this.getBaseContext(), url, true, new IDownProgress() {
                        @Override
                        public void DownProgress(int i, int i1, String s) {

                        }
                    });
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
                Msgbox.Show(this.getBaseContext(),"请重新刷卡！");
                return 1;
            }
            if (!card.ExistsCard()) {
                Msgbox.Show(this.getBaseContext(), "请贴卡后再操作");
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
            Msgbox.Show(this.getBaseContext(),  E.getMessage());
        }
        return 1;
    }
    @JavascriptInterface
    public boolean AuthentWtiteKey(int SectorNo,String KeyStr) {
        String str = "";
        if (card==null)
        {
            Msgbox.Show(this.getBaseContext(), "请重新刷卡！");
            return false;
        }
        try {
            if (!card.ExistsCard()) {
                Msgbox.Show(this.getBaseContext(),  "请贴卡后再操作");
                return false;
            }
            byte[] key= EnjoyTools.HexStrToBytes(KeyStr);
            return card.AuthentWruteKey(SectorNo,key);
        } catch (Exception E) {
            Msgbox.Show(this.getBaseContext(), E.getMessage());
        }
        return false;
    }

    @JavascriptInterface
    public String ReadSector(int SectorNo,String KeyStr) throws Exception {
        if (card==null)
        {
            Msgbox.Show(this.getBaseContext(), "请重新刷卡！");
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
            Msgbox.Show(this.getBaseContext(), "请重新刷卡！");
            return "";
        }
        if (!card.ExistsCard()) {
            Msgbox.Show(this.getBaseContext(),  "请贴卡后再操作");
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
