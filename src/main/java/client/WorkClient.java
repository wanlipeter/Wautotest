package client;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class WorkClient {

    final BaseClient client ;
    private static final Properties STATIC_PROPERTIES = new Properties();
    static Logger logger = LoggerFactory.getLogger(WorkClient.class);

    public WorkClient(String user, String password){
        client = new BaseClient ( user, password );
    }

    public void logout (){
//        client.Logout ();
    }

    public String getToken(){
        return client.token;
    }

    //定义报警人信息
    private static List<String> AlertPerson = Arrays.asList ( "135****9714","135****9714","135****9714" );



    /**
     * 这里来定义接口信息，url、入参、出参解析
     */
    public Boolean getsomeapidone() {
        logger.info("此处开始业务请求。");
//        String url = host+"/api/media/recommendOrganization";
//        String data = "{\"organization\":\"1\"}";
//        JSONObject jsondata = JSONObject.parseObject ( data );
//        try {
//            JSONObject res = client.request (jsondata, null, "post", url);
//            if(res.getBoolean ( "success" )&&!res.getJSONArray ( "data" ).isEmpty ()){
//                return res.getBoolean ( "success" );
//            }
//            else {
//                DingTalkRobotUtil.sendMessageByTextToManyNumbers (dingtalkurl,"查询推荐机构列表失败，URL为 "+url+"response =" +res,atPerson,false );
//                Assert.fail ( "查询推荐机构列表失败，URL为 "+url+"response =" +res );
//            }
//        } catch (Exception e) {
//            DingTalkRobotUtil.sendMessageByTextToManyNumbers(dingtalkurl,e.getMessage (),atPerson , false);
//            e.printStackTrace ( );
//            Assert.fail (e.getMessage ());
//        }
        return null;
    }
}
