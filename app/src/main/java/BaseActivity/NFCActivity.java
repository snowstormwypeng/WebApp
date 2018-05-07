package BaseActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import Factory.CardFactory;


import Enums.MsgType;
import Helper.Msgbox;
import Interface.IAsynCallBackListener;
import Interface.IBrushCardEvent;
import Interface.ICard;


/**
 * Created by 王彦鹏 on 2017-09-15.
 */

public class NFCActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[][] mTechList;
    protected NfcAdapter nfcAdapter;
    protected ICard card;

    protected int nfcAdapterInitialize() {
        //自定义的函数
        //尝试去获取设备默认的NfcAdapter(NFC适配器)对象，由于手机中一般只有一个NFC设备，所以我们这里获取默认的即可。
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (BuildConfig.APPLICATION_ID.equals("enjoy.webnfc")) {
            //判断nfcAdapter是否为空，若为空则手机不支持NFC设备
            if (nfcAdapter == null) {
                //Toast.makeText(this, "不支持nfc", Toast.LENGTH_SHORT).show();
                Msgbox.Show(this, "提示", "系统不支持NFC，系统停止。", MsgType.msg_Error, new IAsynCallBackListener() {
                    @Override
                    public void onFinish(Object response) {
                        finish();
                    }

                    @Override
                    public void onError(Object sender, Exception e) {

                    }
                });
                return 0;
            } else {//若不为空，则判断NFC是否开启
                if (!nfcAdapter.isEnabled()) {
                    Toast.makeText(this, "请开启 NFC 功能，否则该系统不能使用。", Toast.LENGTH_SHORT).show();
                    //我们将跳转到设置页面去开启NFC
                    startActivityForResult(new Intent(Settings.ACTION_NFC_SETTINGS), 1);
                    return 0;
                } else {

                }
            }
        }
        return 1;
    }
    private void nfcInitialization(){
        //该参数的作用是指定用哪个Activity来处理标签
        //参数1：上下文
        // 参数二 不使用了——0，
        // 参数三：一个意图用来存储信息，这里会根据这个意图来调用指定Activity
        //FLAG_ACTIVITY_SINGLE_TOP：指定Activity不能被重复创建
        // 参数四：对参数的操作标志
        pendingIntent= PendingIntent.getActivity(
                this,0,
                new Intent(this,getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                ,0);
        //创建意图过滤器，指定该前台调度系统拦截那些类型的标签
        //这里说明IntentFilter不是很熟悉的童鞋先去查一下这里的资料
        //在这里我们现在*/*表示拦截所有标签
        IntentFilter intentFilter=new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            intentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        //将我们的意图放入到数组中(在我的案例中，intentFilter1其实是没有使用的)
        IntentFilter[] intentFilters = new IntentFilter[]{intentFilter};
        //指定过滤标签，这里填入null就好
        mTechList=null;
    }

    @Override
    protected void onPause() {
        Log.d("Activity 休眠",getClass().getName());
        if (card!=null) {
            card.SetBrushEvent(null);
        }
        super.onPause();
        if(nfcAdapter!=null)
            nfcAdapter.disableForegroundDispatch(this);
    }
    //在Activity显示的时候，我们让NFC前台调度系统处于打开状态
    @Override
    protected void onResume() {
        Log.d("Activity 启动",getClass().getName());
        if (this instanceof IBrushCardEvent) {
            if (card!=null) {
                card.SetBrushEvent((IBrushCardEvent) this);
            }
        }
        super.onResume();
        Log.w("nfclive","resume"+nfcAdapter);
        if(nfcAdapter!=null)
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,
                    intentFilters,mTechList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nfcAdapterInitialize();
        nfcInitialization();

    }

    protected String GetExceptionMsg(Throwable e)
    {
        if (e.getCause()!=null)
        {
            return GetExceptionMsg(e.getCause());
        }
        else
        {
            return e.getMessage();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //这里我们去得到tag对象
        String Str="";
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag!=null) {
            card= CardFactory.GetCardInstance(this,tag,intent);
            if (card==null)
            {
                    Msgbox.Show(this, "提示", "该系统的NFC不支持IC卡操作。", MsgType.msg_Error);

            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
    }
    @Override
    protected void onDestroy()
    {
        try {
            card.SetBrushEvent(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            PublicDefine.enjoyCard.SetBreshEvent(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onDestroy();
    }



}
