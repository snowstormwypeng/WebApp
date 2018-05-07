package Interface;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by 王彦鹏 on 2017-09-06.
 */

public interface ICard {
    boolean ExistsCard();
    byte[] GetCardId();
    long GetCardNo();
    void  SetIntent(Context ctx, Intent intent)  throws Exception;
    void SetBrushEvent(IBrushCardEvent event);
    byte[] ReadSector(int SectorNo, byte[] readkey) throws Exception;
    byte[] ReadBlock(int BlockNo, byte[] readkey) throws Exception;
    int WriteSector(int SectorNo, byte[] Data, byte[] writekey) throws  Exception;
    int WriteBlock(int BlockNo, byte[] Data, byte[] writekey) throws  Exception;
    boolean AuthentReadKey(int SectorNo,byte[] Key) throws Exception;
    boolean AuthentWruteKey(int SectorNo,byte[] Key) throws Exception;
    int GetSectorCount();

    int GetBlockCount();

}
