package Helper;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import Annotation.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import Entity.BaseEnity;
import Interface.IAsynCallBackListener;
import Interface.IHttp;

/**
 * Created by 王彦鹏 on 2017-09-02.
 */

public class Http implements IHttp {

    /**
     * 用Get方式请求服务器
     *
     * @param url         url 地址
     * @param contentType 请求数据类型
     * @return 服务器返回的数据
     */
    @NonNull
    private static String httpMethod(String url, String contentType, String method, String postData) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL murl = new URL(url);
            //1.得到HttpURLConnection实例化对象
            conn = (HttpURLConnection) murl.openConnection();
            //2.设置请求信息（请求方式... ...）
            //设置请求方式和响应时间
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Content-Length", String.valueOf(postData.getBytes().length));
            conn.setRequestProperty("encoding", "UTF-8"); //可以指定编码
            conn.setConnectTimeout(5000);
            //不使用缓存
            conn.setUseCaches(false);
            // 设置可取
            conn.setDoInput(true);
            // 设置可读
            conn.setDoOutput(true);
            if (method.toUpperCase() == "POST") {
                //4.向服务器写入数据
                conn.getOutputStream().write(postData.getBytes());
            }
            //3.读取响应
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                // 创建高效流对象
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                // 创建StringBuilder对象存储数据
                StringBuilder response = new StringBuilder();
                String line;// 一次读取一行
                while ((line = reader.readLine()) != null) {
                    response.append(line);// 得到的数据存入StringBuilder
                }
                return response.toString();
            } else {
                System.out.println("请求失败！");
                throw new Exception("请求失败");
            }
        } catch (TimeoutException e)
        {
            System.out.println(String.format("连接服务器[%s]超时",url));
            throw new Exception("请求超时");
        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
            return "";
        } finally {
            //4.释放资源
            if (conn != null) {
                //关闭连接 即设置 http.keepAlive = false;
                conn.disconnect();
            }
        }
    }

    /**
     * 用Get方式请求服务器
     *
     * @param url         url 地址
     * @param callback    异步回调地址，当不为null时为异步调用
     * @param contentType 请求数据类型
     * @return 服务器返回的数据
     */
    public static String Get(final String url,
                             final String contentType,
                             final IAsynCallBackListener callback) throws Exception {
        if (callback == null) {
            return httpMethod(url, contentType, "GET", "");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String httpResponse = httpMethod(url, contentType, "GET", "");
                        // 回调onFinish()方法
                        callback.onFinish(httpResponse);

                    } catch (Exception e) {
                        // 回调onError()方法
                        callback.onError(this,e);
                    }
                }
            }).start();
            return "";
        }

    }

    /**
     * 用Get方式请求服务器
     *
     * @param url 网络地址
     * @return
     */
    public  String Get(final String url) {
        final Object synObj = new Object();
        final String[] res = {""};
        Thread getthread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    try {
                        res[0] = Get(url, "application/x-www-form-urlencoded", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    synObj.notify();
                }
            }
        });
        getthread.start();

        try {
            //Thread.sleep(1);
            synchronized (synObj) {
                synObj.wait();
                return res[0];
            }
            //return res[0];
        } catch (Exception e) {
            return res[0];
        }
    }


    /**
     * 用 Post 方式请求服务器
     *
     * @param url         网络地址
     * @param postData    提交数据
     * @param callback    回调地址
     * @param contentType 请求数据类型
     * @return 服务器返回数据
     */
    public static String Post(final String url,
                              final String postData,
                              final IAsynCallBackListener callback,
                              final String contentType) throws Exception {
        if (callback == null) {
            return httpMethod(url, contentType, "POST", postData);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String httpResponse = httpMethod(url, contentType, "POST", postData);
                        // 回调onFinish()方法
                        callback.onFinish(httpResponse);

                    } catch (Exception e) {
                        // 回调onError()方法
                        callback.onError(this,e);
                    }
                }
            }).start();
            return "";
        }
    }

    /**
     * 用 Post 方式请求服务器
     *
     * @param url      网络地址
     * @param postData 提交数据
     * @return
     */
    public  String Post(final String url,
                              final String postData) {
        final Object synObj = new Object();
        final String[] res = {""};
        Thread getthread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    try {
                        res[0] = Post(url, postData,null,"application/x-www-form-urlencoded");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    synObj.notify();
                }
            }
        });
        getthread.start();

        try {
            //Thread.sleep(1);
            synchronized (synObj) {
                synObj.wait();
                return res[0];
            }
            //return res[0];
        } catch (Exception e) {
            return res[0];
        }

    }

    /**
     * 用 Post 方式请求服务器
     *
     * @param url      网络地址
     * @param postData 提交数据
     * @return
     */
    public static String Post(final String url,
                              final String postData,
                              final String contentType) {
        final Object synObj = new Object();
        final String[] res = {""};
        Thread getthread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (synObj) {
                    try {
                        res[0] = Post(url, postData,null,contentType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    synObj.notify();
                }
            }
        });
        getthread.start();

        try {
            //Thread.sleep(1);
            synchronized (synObj) {
                synObj.wait();
                return res[0];
            }
            //return res[0];
        } catch (Exception e) {
            return res[0];
        }
    }

    private static JSONObject LoadNodeChild(Node node) throws JSONException {
        JSONObject json = new JSONObject();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node subnode=node.getChildNodes().item(i);
            String key =subnode.getNodeName();
            Object value = null;
            if (subnode.hasChildNodes()&& (!subnode.getChildNodes().item(0).getNodeName().equals("#text"))) {
                value = LoadNodeChild(subnode);
            } else {
                value = subnode.getTextContent();
            }
            json.put(key, value);
        }
        return json;
    }

    private static String GetPropertyName(Field f)
    {
        PropertyName pname = f.getAnnotation(PropertyName.class);
        if (pname==null)
        {
            return f.getName();
        }
        else
        {
            return pname.value();
        }
    }

    private static String GetKeyValue(BaseEnity enity) throws Exception {
        String values = "";
        Field[] field = enity.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组

        for (int j = 0; j < field.length; j++) { // 遍历所有属性
            String name = field[j].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法

            try {
                Method m = enity.getClass().getMethod("get" + name);
                Object instance = m.invoke(enity);
                if (instance.getClass().getName().equals("BaseEnity")) {
                    values += "<" + GetPropertyName(field[j]) + ">" + GetKeyValue((BaseEnity) instance) + "</" + GetPropertyName(field[j]) + ">\n";
                } else {
                    values += "<" + GetPropertyName(field[j]) + ">" + instance.toString() + "</" + GetPropertyName(field[j]) + ">\n";
                }
            }
            catch (Exception e)
            {

            }
        }
        return values;
    }
    /**
     * 调用WebService接口
     *
     * @param IName
     * @param args
     * @return
     */
    public  JSONObject CallWebService(String url, String IName, BaseEnity... args)
            throws Exception {
        String pdata = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <soap:Body>\n" +
                "<%s xmlns=\"http://tempuri.org/\">\n";
        if (args[0]!=null) {
            for (BaseEnity entity:args ) {
                pdata+=GetKeyValue(entity);
            }

        }
        pdata += "</%s>\n" +
                "  </soap:Body>\n" +
                "</soap:Envelope>";
        pdata = String.format(pdata, IName, IName);

        String resxml = Post(url, pdata,"text/xml");
        DocumentBuilderFactory xml = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = xml.newDocumentBuilder();
        Document dom = builder.parse(new InputSource(new ByteArrayInputStream(resxml.getBytes("utf-8"))));

        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName(IName + "Response");//查找所有person节点
        Node response = items.item(0);
        JSONObject resJson = LoadNodeChild(response);
        return resJson;
    }
}
