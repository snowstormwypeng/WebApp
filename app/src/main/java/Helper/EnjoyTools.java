package Helper;



/**
 * Created by 王彦鹏 on 2016/9/14.
 */
public class EnjoyTools  {
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    public static byte[] encrypt(byte[] InData, byte[] Key) throws Exception {
        return AESKeyModel.encrypt(InData, Key);
    }

    /**
     * 字节流转16进制字符串
     * @param inarray 字节流
     * @return 字符串
     */
    public static String ByteArrayToHexString(byte[] inarray) { // converts byte
        // arrays to string
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
    public static byte[] decrypt(byte[] InData, byte[] Key) throws Exception {
        if (InData.length % 16 != 0 || InData.length == 0) {
            return null;
        }
        byte[] ResData = new byte[InData.length];
        for (int i = 1; i <= InData.length / 16; i++) {
            byte[] in = new byte[16];
            System.arraycopy(InData, (i - 1) * 16, in, 0, 16);
            byte[] out=AESKeyModel.decrypt(in, Key);
            System.arraycopy(out, 0,ResData,(i - 1) * 16,16);
        }

        return ResData;
    }
    private  static byte toByte(char c)
    {
        byte b=(byte)"0123456789ABCDEF".indexOf(c);
        return b;
    }
    public static byte[] HexStrToBytes(String HexStr)
    {
        HexStr=HexStr.toUpperCase();
        int len=(HexStr.length()/2);
        byte[] result=new byte[len];
        char[] achar=HexStr.toCharArray();
        for(int i=0;i<len;i++)
        {
            int pos=i*2;
            result[i]=(byte)(toByte(achar[pos])<<4 | toByte(achar[pos+1]));
        }
        return result;
    }




}