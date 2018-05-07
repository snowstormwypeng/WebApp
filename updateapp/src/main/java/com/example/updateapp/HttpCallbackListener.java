package com.example.updateapp;

/**
 * Created by 王彦鹏 on 2016-12-28.
 */

public interface HttpCallbackListener {
    /**
     * 数据响应成功
     *
     * @param response
     *            返回数据
     */
    void onFinish(String response);

    /**
     * 数据请求失败
     *
     * @param e
     *            返回错误信息
     */
    void onError(Exception e);
}
