package Class;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import java.io.IOException;
import Interface.ICard;


/**
 * Created by 王彦鹏 on 2016/9/20.
 */
public class M1Card extends NfcCard implements ICard {
    private MifareClassic  nfc_C;

    @Override
    public void SetIntent(Context ctx,Intent intent) throws Exception {
        super.SetIntent(ctx,intent);
        if (intent==null) {
            return;
        }
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        CardIdArray = tag.getId();
        nfc_C = MifareClassic.get(tag);
        nfc_C.connect();
        if (nfc_C.isConnected())
        {
            CheckCard();
        }

    }


    /**
     * 获取卡
     * @return
     */
    public byte[] GetCardId()
    {
        return CardIdArray;
    }

    @Override
    public long GetCardNo() {
        BinaryType bt=new BinaryType(4,int.class);
        bt.setData(CardIdArray);
        return bt.getInt();
    }

    @Override
    public int GetSectorCount()
    {
        return nfc_C.getSectorCount();
    }

    @Override
    public int GetBlockCount()
    {
        return nfc_C.getBlockCount();
    }

    @Override
    public boolean AuthentReadKey(int SectorNo,byte[] Key) throws Exception {
        return nfc_C.authenticateSectorWithKeyA(SectorNo, Key);
    }
    @Override
    public boolean AuthentWruteKey(int SectorNo,byte[] Key) throws Exception {
        return nfc_C.authenticateSectorWithKeyB(SectorNo,Key);
    }

    private byte[] nfcReadBlock(byte Block) throws IOException {
        synchronized (syncobj) {
            return nfc_C.readBlock(Block);
        }

    }
    @Override
    public boolean ExistsCard() {
        boolean flag = false;
        if (nfc_C != null) {
            try {
//                if (!nfc_C.isConnected()) {
//                    nfc_C.connect();
//                }
                return nfc_C.isConnected();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return flag;
    }

    public byte[] ReadSector(int SectorNo,byte[] readkey) throws IOException {
        synchronized (syncobj) {
            boolean auth = nfc_C.authenticateSectorWithKeyA(SectorNo, readkey);
            if (!auth) {
                return null;
            }
            byte[] Data = new byte[48];
            byte[] data0 = nfc_C.readBlock(SectorNo * 4);
            byte[] data1 = nfc_C.readBlock(SectorNo * 4 + 1);
            byte[] data2 = nfc_C.readBlock(SectorNo * 4 + 2);
            for (int i = 0; i < 16; i++) {
                Data[i] = data0[i];
            }

            for (int i = 0; i < 16; i++) {
                Data[16 + i] = data1[i];
            }
            for (int i = 0; i < 16; i++) {
                Data[32 + i] = data2[i];
            }
            return Data;
        }
    }
    @Override
    public byte[] ReadBlock(int BlockNo,byte[] readkey) {
        synchronized (syncobj) {
            try {
                boolean auth = AuthentReadKey(BlockNo / 4, readkey);
                if (!auth) {
                    return null;
                }

                byte[] Data = nfcReadBlock((byte) BlockNo);
                return Data;
            } catch (Exception E) {
                return null;
            }
        }
    }

    public int WriteSector(int SectorNo,byte[] Data,byte[] writekey) throws Exception {
        synchronized (syncobj) {
            boolean auth = nfc_C.authenticateSectorWithKeyA(SectorNo, writekey);
            if (!auth) {
                return 1;
            }
            byte[] buf = new byte[16];
            for (int i = 0; i < 3; i++) {
                System.arraycopy(Data, i * 16, buf, 0, buf.length);
                nfc_C.writeBlock(SectorNo * 4 + i, buf);
            }
            return 0;
        }
    }

    public int WriteBlock(int BlockNo,byte[] Data,byte[] writekey) throws Exception {
        synchronized (syncobj) {
            boolean auth = nfc_C.authenticateSectorWithKeyA(BlockNo / 4, writekey);
            if (!auth) {
                return 1;
            }
            nfc_C.writeBlock(BlockNo, Data);

            return 0;
        }
    }

}
