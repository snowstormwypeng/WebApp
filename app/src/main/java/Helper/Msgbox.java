package Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.enjoy.R;

import java.util.ArrayList;
import java.util.List;

import Device.PhoneSound;
import Enums.MsgType;
import Interface.IAsynCallBackListener;

/**
 * 弹出框
 * Created by 王彦鹏 on 2017-09-04.
 */
public class Msgbox {

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
    public static boolean Show(final Context ctx, String title, String message,
                               MsgType msgType, final IAsynCallBackListener callback) {

        final AlertDialog.Builder msgDialog = new AlertDialog.Builder(ctx);
        msgDialog.setIcon(R.mipmap.ic_launcher);
        msgDialog.setTitle(title);
        msgDialog.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (callback!=null) {
                    callback.onFinish(this);
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
                PhoneSound.play(ctx,R.raw.hint_8);
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
                msgDialog.setView(et);
                msgDialog.setView(et);
                msgDialog.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
                PhoneSound.play(ctx,R.raw.hint_8);
                items.add(new ItemBean(R.drawable.icon_help,message));
                msgDialog.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
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
        msgDialog.setAdapter(adapter,null);
        msgDialog.setCancelable(false);

        msgDialog.show();
        return true;
    }
    public static boolean Show(Context ctx, String title,String message,MsgType msgType) {
        return Show(ctx,title, message,msgType, null);
    }
    public static boolean Show(Context ctx, String message) {

        return Show(ctx,ctx.getString(R.string.msgbox_show_hint) ,message,MsgType.msg_Hint, null);
    }
}
