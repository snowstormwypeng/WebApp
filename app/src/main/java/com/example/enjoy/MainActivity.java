package com.example.enjoy;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

//import com.uuzuche.lib_zxing.activity.CaptureActivity;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import BaseActivity.BaseActivity;
import Helper.EnjoyTools;
import Helper.Msgbox;
import Interface.IBrushCardEvent;

import static android.widget.Toast.makeText;


public class MainActivity extends BaseActivity implements IBrushCardEvent {
    private WebView mWebView;
    private int touchFlag=1;
    /**
     * 多View模式
     */
    public static List<String> urlList;

    float xDown, yDown, xUp, yUp;
    private Handler mHandler = new Handler();
    private JsCallInterface jsCall;
    /**
     * 点击退出时间
     */
    private long firstTime = 0;

    @Override
    public void BrushIn(Intent intent) {
        jsCall.card=this.card;
        jsCall.CallJs("BrushCard",EnjoyTools.ByteArrayToHexString(card.GetCardId()));
    }

    @Override
    public void BrushOut(Intent intent) {
        jsCall.CallJs("ClearInfo","");
    }

    /**
     * 检查升级
     */
    public void CheckUpdate()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        CheckUpdate();
        initWebView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 111: {
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        jsCall.CallJs(jsCall.mCallJsName, result);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case 0:
            {
                switch (resultCode)
                {
                    case 1: {
                        mWebView.reload();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }

                break;
            }
            default:
            {
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    }
                    else
                    {
                        if (jsCall.webViewCount<=1) {
                            long secondTime = System.currentTimeMillis();
                            if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出
                                makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                                firstTime = secondTime;//更新firstTime
                                return true;
                            } else {                                                    //两次按键小于2秒时，退出应用
                                onBackPressed();
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                                System.exit(0);
                            }
                        }
                        urlList.remove(urlList.indexOf(mWebView.getUrl()));
                        jsCall.webViewCount-=1;
                        onBackPressed();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        setResult(jsCall.ReLoadWeb);
                        finish();
                    }

                //unregisterReceiver(receiver);
                return true;
            }
            return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (touchFlag != 1) {
            return super.dispatchTouchEvent( event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            xDown = event.getX();
            yDown = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            xUp = event.getX();
            yUp = event.getY();
            if (xUp - xDown < -400) {
                // 向左滑动
                //Toast.makeText(this, "向左滑动，滑动位移"+(xUp-xDown), Toast.LENGTH_SHORT);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity.class);
                String s = mWebView.getUrl();
                int i = urlList.indexOf(s);
                if (urlList.size() >= i + 2) {
                    intent.putExtra("url", urlList.get(i + 1));
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_left);
                }
            } else if (xUp - xDown > 400) {
                // 向右滑动
                //Toast.makeText(this, "向右滑动，滑动位移"+(xUp-xDown), Toast.LENGTH_SHORT);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        }
        return super.dispatchTouchEvent( event);
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.WebView);
        jsCall = new JsCallInterface(MainActivity.this,mWebView);
        WebSettings webSettings = mWebView.getSettings();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        mWebView.setWebContentsDebuggingEnabled(true);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //webSettings.setUserAgentString("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36 Edge/15.15063");

        mWebView.addJavascriptInterface(jsCall, "external");
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (getIntent().getStringExtra("url") !=null)
        {
            mWebView.loadUrl(getIntent().getStringExtra("url"));
        }
        else {
            mWebView.loadUrl(getString(R.string.loadurl));
            urlList= new ArrayList<String>();
            urlList.add(mWebView.getUrl());
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                return Msgbox.JSAlert(MainActivity.this,message,result);
            }

            @Override
            public void onCloseWindow(WebView window) {
                //TODO something
                super.onCloseWindow(window);
            }


            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog,
                                          boolean isUserGesture, Message resultMsg) {

                //TODO something
                WebView newWebView = new WebView(view.getContext());
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        jsCall.webViewCount+=1;
                        Intent intent=new Intent(MainActivity.this, MainActivity.class);
                        urlList.add(url);
                        intent.putExtra("url",url);
                        startActivityForResult(intent,0);

                        overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_from_left);
                        return false;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport)resultMsg.obj;
                transport.setWebView(newWebView);    //此webview可以是一般新创建的
                resultMsg.sendToTarget();
                return true;
            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (jsCall.MultiModel) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MainActivity.class);
                    String s = mWebView.getUrl();
                    int i = urlList.indexOf(s);
                    if (urlList.size() >= i + 2) {
                        if (!urlList.get(i + 1).equals(url)) {
                            for (int j = urlList.size() - 1; j >= i + 1; j--) {
                                urlList.remove(j);
                            }
                            urlList.add(url);
                        }
                    } else {
                        urlList.add(url);
                    }
                    jsCall.webViewCount+=1;
                    intent.putExtra("url", url);
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_from_left);

                }
                else {
                    mWebView.loadUrl(url);
                }
                return true;

            }


        });
    }
}
