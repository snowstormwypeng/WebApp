package Interface;

import java.util.Objects;

/**
 * Created by 王彦鹏 on 2017-11-17.
 */

public interface IAsynCallBackListener extends IEnjoy {
    /**
     * 数据响应成功
     *
     * @param sender 调度者
     */
    void onFinish(Object sender);

    /**
     * 数据请求失败
     * @param sender  调度者
     * @param e 异常对象
     */

    void onError(Object sender, Exception e);
}
