package Class;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import Interface.IBrushCardEvent;

/**
 * Created by 王彦鹏 on 2017-09-30.
 */

public class NfcCard {
    protected byte[] CardIdArray;
    protected Tag tag;
    protected Object syncobj = new Object();
    protected Intent intent;
    protected Context ctx;
    protected IBrushCardEvent brushvent;
    private boolean CardExist=false;

    private Handler handle = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                if (ExistsCard()) {

                    if (brushvent != null && !CardExist) {
                        Log.d("NFC", String.format("卡移入，处理对象：%s", brushvent.getClass().getName()));
                        CardExist = true;
                        brushvent.BrushIn(intent);
                    }

                } else {

                    if (brushvent != null && CardExist) {
                        Log.d("NFC", String.format("卡移出，处理对象：%s", brushvent.getClass().getName()));
                        CardExist = false;
                        brushvent.BrushOut(intent);
                    }

                }
            }
            catch (Exception e)
            {
                Log.d("NFC",e.toString());
            }
        }

    };

    public boolean ExistsCard() {
        return true;
    }

     public void SetIntent(Context ctx, Intent intent) throws Exception {
        if (intent==null) {
            return;
        }
        this.intent = intent;

        this.ctx = ctx;
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        CardIdArray = tag.getId();


    }

    protected void CheckCard() {
        Thread Connectedthread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    synchronized (syncobj) {
                        Message msg = new Message();
                        handle.sendMessage(msg);
                        //syncobj.notify();
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Connectedthread.start();
    }

    public void SetBrushEvent(IBrushCardEvent event) {
        synchronized (syncobj) {
            brushvent = event;
        }
    }
}

