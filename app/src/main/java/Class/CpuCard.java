package Class;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import java.io.IOException;

import Helper.RSAUtils;
import Interface.ICard;


/**
 * Created by 王彦鹏 on 2017-03-31.
 */

public class CpuCard extends NfcCard implements ICard {
    public IsoDep Nfc_Cpu;
    private byte[] CardIdArray;
    private byte[] RsaPubKey=new byte[67];

    public byte[] SendData(byte[] Data) throws IOException {
        return Nfc_Cpu.transceive(Data);
    }

    @Override
    public boolean ExistsCard() {
        boolean flag = false;
        if (Nfc_Cpu != null) {
            try {
                if (!Nfc_Cpu.isConnected()) {
                    Nfc_Cpu.connect();
                }
                return Nfc_Cpu.isConnected();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return flag;
    }

    @Override
    public byte[] GetCardId() {
        return CardIdArray;
    }

    @Override
    public long GetCardNo() {
        BinaryType bt=new BinaryType(4,int.class);
        bt.setData(CardIdArray);
        return bt.getInt();
    }

    private void InitPubKey(byte[] CardID)
    {
        RsaPubKey=new byte[]{
                (byte)0xc4,(byte)0xd2,(byte)0x0b,(byte)0x56,(byte)0xb5,(byte)0x93,(byte)0xe7,(byte)0x84,
                (byte)0x3b,(byte)0x40,(byte)0xe2,(byte)0x87,(byte)0xa8,(byte)0x19,(byte)0x8a,(byte)0x9f,
                (byte)0x5d,(byte)0x7d,(byte)0xf5,(byte)0x6a,(byte)0xec,(byte)0x2f,(byte)0x7e,(byte)0x79,
                (byte)0xd2,(byte)0xd8,(byte)0xe7,(byte)0xd8,(byte)0x10,(byte)0x7e,(byte)0x1b,(byte)0x86,
                (byte)0x28,(byte)0x68,(byte)0x53,(byte)0x45,(byte)0xbd,(byte)0x4b,(byte)0x37,(byte)0x9e,
                (byte)0x75,(byte)0xd2,(byte)0xaf,(byte)0x82,(byte)0xc8,(byte)0x7c,(byte)0x5d,(byte)0xf0,
                (byte)0xce,(byte)0xc3,(byte)0x53,(byte)0x7a,(byte)0x04,(byte)0x21,(byte)0xf1,(byte)0xd7,
                (byte)0xa0,(byte)0xc8,(byte)0xc2,(byte)0xed,(byte)0x83,(byte)0x68,(byte)0xff,(byte)0x11,
                (byte)0x01,(byte)0x00,(byte)0x01
        };
    }

    @Override
    public void SetIntent(Context ctx, Intent intent) throws Exception {
        super.SetIntent(ctx,intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Nfc_Cpu= IsoDep.get(tag);
        CardIdArray = tag.getId();
        Nfc_Cpu.connect();
        InitPubKey(CardIdArray);
    }

    @Override
    public byte[] ReadSector(int SectorNo, byte[] readkey) throws IOException {
        return new byte[0];
    }

    @Override
    public byte[] ReadBlock(int BlockNo, byte[] readkey) throws IOException {
        byte[] cmd=new byte[]{00,(byte)0xA4,0x00,0x00,0x02,0x10,0x01};
        byte[] data=Nfc_Cpu.transceive(cmd);
        if ((data[data.length-2] &0xff) ==0x90 && (data[data.length-1] & 0xff)==0x00)
        {
            cmd=new byte[]{0x00,(byte)0xB0,(byte)0x85,0x00,0x40};
            data=Nfc_Cpu.transceive(cmd);
            byte[] inbuf=new byte[data.length-2];
            System.arraycopy(data,0,inbuf,0,inbuf.length);
            data= RSAUtils.decryptData(inbuf,RsaPubKey);

            return data;
        }
        else {
            return data;
        }
    }

    @Override
    public int WriteSector(int SectorNo, byte[] Data, byte[] writekey) throws IOException {
        return 0;
    }

    @Override
    public int WriteBlock(int BlockNo, byte[] Data, byte[] writekey) throws IOException {
        return 0;
    }

    @Override
    public boolean AuthentReadKey(int SectorNo, byte[] Key) throws Exception {
        return false;
    }

    @Override
    public boolean AuthentWruteKey(int SectorNo, byte[] Key) throws Exception {
        return false;
    }

    @Override
    public int GetSectorCount() {
        return 0;
    }

    @Override
    public int GetBlockCount() {
        return 0;
    }
}
