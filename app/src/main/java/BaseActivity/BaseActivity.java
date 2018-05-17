package BaseActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import Entity.RequestPermissionsResult;
import Interface.IAsynCallBackListener;

/**
 * Created by 王彦鹏 on 2017-12-11.
 */

public class BaseActivity extends NFCActivity {
    private IAsynCallBackListener callBackListener;
    public void RequestPermission(String pname, final IAsynCallBackListener callback)
    {
        //StackTraceElement[] s = Thread.currentThread().getStackTrace();
        //String methodName = s[3].getMethodName();

        //Log.i("MethodRecord", methodName + "." + methodName);
        callBackListener=callback;
        /// Manifest.permission.READ_CONTACTS
        if (ContextCompat.checkSelfPermission(this,pname) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{pname}, 0);
        }else{

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
    }
    public boolean CheckPermission(String pname)
    {
        return  ContextCompat.checkSelfPermission(this,pname) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length >0 ) {
                    List<RequestPermissionsResult> permissionsResultList=new ArrayList<RequestPermissionsResult>();
                    RequestPermissionsResult res=new RequestPermissionsResult();
                    for(int i=0;i<permissions.length;i++) {
                        res.setRequestRes(grantResults[i]);
                        res.setPermissionsName(permissions[i]);
                        permissionsResultList.add(res);
                    }
                    callBackListener.onFinish(permissionsResultList);
                } else {


                }
                return;
            }
        }
    }
}
