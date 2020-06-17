package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.enjoy.R;

import java.util.ArrayList;
import java.util.List;

import Device.PhoneSound;
import Enums.MsgType;
import Listener.IAsynListener;


/**
 * 弹出框
 * Created by 王彦鹏 on 2017-09-04.
 */
public class Msgbox {
    private static AlertDialog.Builder tipDialog;

    private static class CustomAdapter extends BaseAdapter {

        private List<ItemBean> items;
        private LayoutInflater inflater;
        private ImageView image;
        private TextView text;

        public CustomAdapter(List<ItemBean> items, Context context) {
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }



        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                view=inflater.inflate(R.layout.custom_adapter,null);
                image= (ImageView) view.findViewById(R.id.id_image);
                text= (TextView) view.findViewById(R.id.id_text);
            }
            image.setImageResource(items.get(i).getImageId());
            text.setText(items.get(i).getMessage());
            return view;
        }
    }
    private static class ItemBean{
        private int imageId;
        private String message;

        public ItemBean(int imageId, String message) {
            this.imageId = imageId;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public int getImageId() {
            return imageId;
        }

        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static boolean JSPrompt(Context ctx, String message,
                                   String defaultValue, final JsPromptResult result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("对话框").setMessage(message);
        final EditText et = new EditText(ctx);
        et.setSingleLine();
        et.setText(defaultValue);
        builder.setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.confirm(et.getText().toString());
                        }
                    }

                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.cancel();
                        }
                    }
                });

        // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {

                return true;
            }
        });

        // 禁止响应按back键的事件
        // builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        return true;
        // return super.onJsPrompt(view, url, message, defaultValue,
        // result);
    }

    public static boolean JSConfirm(Context ctx, String message, final JsResult result) {
        new AlertDialog.Builder(ctx)
                .setTitle("询问")
                .setIcon(android.R.drawable.ic_menu_help)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (result!=null) {
                            result.confirm();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (result!=null) {
                            result.cancel();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
        //result.confirm();
        return true;
    }

    public static boolean JSAlert(Context ctx, String message, final JsResult result) {
        new AlertDialog.Builder(ctx)
                .setTitle("提示")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (result!=null) {
                            result.confirm();
                        }
                    }
                })
                .setCancelable(false)
                .create().show();
        //result.confirm();
        return true;
    }
    public static void Show(final Context ctx, String title, String message,
                            MsgType msgType, final IAsynListener OKcallback,
                            final IAsynListener Cancelcallback) {

        CloseTipDialog();
        tipDialog = new AlertDialog.Builder(ctx);
        tipDialog.setIcon(R.mipmap.ic_launcher);
        tipDialog.setTitle(title);
        tipDialog.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (OKcallback!=null) {
                    OKcallback.onFinish(tipDialog,"");
                }
            }
        });

        List<ItemBean> items=new ArrayList<>();
        switch (msgType)
        {
            case msg_Error:
            {
                PhoneSound.play(ctx, R.raw.hint_2);
                items.add(new ItemBean(R.drawable.icon_error,message));
                break;
            }
            case msg_Succeed:
            {
                PhoneSound.play(ctx,R.raw.hint_1);
                items.add(new ItemBean(R.drawable.icon_success,message));
                break;
            }
            case msg_Input:
            {
                PhoneSound.play(ctx,R.raw.hint_10);
                //items.add(new ItemBean(R.drawable.icon_hint,message));
                final EditText et = new EditText(ctx);
                et.setSingleLine();
                et.setText(message);
                tipDialog.setView(et);
                tipDialog.setView(et);
                tipDialog.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //msgDialog.d
                    }
                });
                break;
            }
            case msg_warning: {
                PhoneSound.play(ctx, R.raw.hint_3);
                items.add(new ItemBean(R.drawable.icon_warning,message));
                break;
            }
            case msg_Query:
            {
                PhoneSound.play(ctx,R.raw.hint_1);
                items.add(new ItemBean(R.drawable.icon_help,message));
                tipDialog.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //msgDialog.d
                    }
                });
                break;
            }
            default:{
                PhoneSound.play(ctx,R.raw.hint_1);
                items.add(new ItemBean(R.drawable.icon_hint,message));
                break;
            }
        }
        //msgDialog.setMessage(message);
        CustomAdapter adapter=new CustomAdapter(items,ctx);
        tipDialog.setAdapter(adapter,null);
        tipDialog.setCancelable(false);

        tipDialog.show();



    }
    public static void Show(final Context ctx, String message,
                            MsgType msgType, final IAsynListener OKcallback) {
        Show(ctx,"提示",message,msgType,OKcallback,null);
    }
    public static void Show(Context ctx, String title, String message, MsgType msgType) {
        Show(ctx,title, message,msgType, null,null);
    }
    public static void Show(Context ctx, String message) {
        Show(ctx,"提示" ,message, MsgType.msg_Hint, null,null);
    }

    public static boolean CloseTipDialog(){
        return true;
    }

    public static void hideBottomUIMenu(final Window window) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = window.getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = window.getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            decorView.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener()
                    {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            hideBottomUIMenu(window);
                            //Toast.makeText(MainActivity.this,"隐藏虚拟按钮栏", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
    }
    public  static AlertDialog ShowDialog(Context ctx,int resId)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.show();
        Window window =alertDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setContentView(resId);
        /*hideBottomUIMenu(window);*/
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        // 去除四角黑色背景
        window.setBackgroundDrawable(new BitmapDrawable());
        // 设置周围的暗色系数
        params.dimAmount = 0.5f;
        window.setAttributes(params);
        return alertDialog;
    }

}
