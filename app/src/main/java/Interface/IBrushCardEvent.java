package Interface;

import android.content.Intent;

/**
 * Created by 王彦鹏 on 2017-09-19.
 */

public interface IBrushCardEvent extends IEnjoy {
    /**
     * 卡移入事件
     * @param intent
     */
    void BrushIn(Intent intent);

    /**
     * 卡移出事件
     * @param intent
     */
    void BrushOut(Intent intent);
}
