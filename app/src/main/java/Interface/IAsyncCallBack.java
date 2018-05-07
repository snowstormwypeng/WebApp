package Interface;

import android.content.Intent;

/**
 * Created by 王彦鹏 on 2017-09-25.
 */

public interface IAsyncCallBack extends IEnjoy {
    Object Execute() throws Exception;
    void SetThrowErr(Throwable e);
    void ExcuteComplete(Object result) ;
}
