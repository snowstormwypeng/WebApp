package Interface;

import org.json.JSONObject;

import Entity.BaseEnity;

/**
 * Created by Administrator on 2017/9/28.
 */

public interface IHttp {
     String  Get(String url) throws Exception;
     String Post(String url, String postData) throws Exception;
     JSONObject CallWebService(String url, String IName, BaseEnity... entity) throws Exception;
}
