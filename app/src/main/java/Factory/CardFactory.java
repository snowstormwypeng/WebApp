package Factory;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;

import java.util.ArrayList;

import Enums.MsgType;
import Helper.Msgbox;
import Interface.ICard;
import Class.*;

/**
 * Created by 王彦鹏 on 2017-12-12.
 */

public class CardFactory {
    public static ICard GetCardInstance(Context ctx, Tag tag,Intent intent) {
        String[] techList = tag.getTechList();
        ArrayList<String> list = new ArrayList<String>();
        for (String string : techList) {
            list.add(string);
            System.out.println("tech=" + string);
        }
        try {
            if (list.contains("android.nfc.tech.IsoDep")) {
                ICard cpuCard = Factory.GetInstance(CpuCard.class, null);
                cpuCard.SetIntent(ctx, intent);
                return cpuCard;
            } else if (list.contains("android.nfc.tech.MifareClassic")) {

                ICard m1Card = Factory.GetInstance(M1Card.class, null);
                m1Card.SetIntent(ctx, intent);
                return m1Card;

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
