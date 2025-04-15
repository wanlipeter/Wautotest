package client;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.testng.Assert;
import utils.BaseUtil;
import utils.HttpDeleteWithBody;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BaseClient extends BaseUtil {
//    public static final Properties STATIC_PROPERTIES = new Properties();
    private static SSLContext sslContext = null;
    static Logger logger = LoggerFactory.getLogger(BaseClient.class);

    public BaseClient(String user, String password){
        if (user.isEmpty () || password.isEmpty ()){
            user = "defaultcredential";
            password = "defaultpasswordx";
        }
        dologin(user, password);
    }

    private static CloseableHttpClient getCustomHttpClient() {
        return HttpClients.custom()
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .setRetryHandler(myRetryHandler)
                .build();
    }

    private final CloseableHttpClient httpClient = getCustomHttpClient();
    String token = null;

    //定义http层级的重试
    static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        @Override
        public boolean retryRequest(
                IOException exception,
                int executionCount,
                HttpContext context) {
            if (executionCount >= 3) {
                // Do not retry if over max retry count
                return false;
            }
            //这里可以追加其他类型的异常，比如504
            if (exception instanceof UnknownHostException) {
                logger.info(String.format("Http请求遇到指定异常，重试一次", exception.getLocalizedMessage(),exception.getStackTrace()));

                // Timeout
                return true;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // Retry if the request is considered idempotent
                return true;
            }
            return false;
        }

    };

    private void dologin(String user, String pass){
        String credential = STATIC_PROPERTIES.getProperty ( user );
        String password = STATIC_PROPERTIES.getProperty ( pass );
        String loginUrl = STATIC_PROPERTIES.getProperty ( "loginUrl" );
        String loginHost = STATIC_PROPERTIES.getProperty ( "loginHost" );
        logger.info(String.format("开始登录测试账号：%s &&& 密码：%s", credential, password));
        logger.info(String.format("登录地址为：%s &&& 指定host为：%s", loginUrl, loginHost));

        HttpResponse response = null;
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("passwordx", password);
            jsonParam.put("authKey", credential);

            HttpPost httpPost = new HttpPost(loginUrl);
            StringEntity entity = new StringEntity(jsonParam.toJSONString());
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");//设置为 json数据
            httpPost.setEntity(entity);
            httpPost.setHeader("Cookie", "_sw_envid=123;");

            if(loginHost==null){
                response = httpClient.execute(httpPost);
            }else {
                response = httpClient.execute(HttpHost.create ( loginHost ),httpPost);
            }
            if (200 != response.getStatusLine().getStatusCode()) {
                //todo 这里可以接报警渠道

                Assert.fail("脚本登陆时接口返回非200 异常！");
            }
            try {
                String cookieString = response.getHeaders("Set-Cookie")[0].getValue();
                token = cookieString.substring(0, 43);
                logger.info("登录成功，获取到的token is ::" + token);
            } catch (Exception e) {
                //todo 这里可以接报警渠道

                Assert.fail("脚本登陆时解析返回数据异常！");
            };

        } catch (Exception e) {
            logger.error("[client.BaseClient]ERROR!", e);
            if (!(e instanceof SSLHandshakeException)) {
                //todo 这里可以接报警渠道

            } else {
                logger.error("SSLHandshakeException, detail", e);
            }
            Assert.fail("自动化脚本登录时异常！");
        }
    }

    public void Logout(){
        //待实现
    };

    public JSONObject request(Object data, Map<String, String> params, String type, String url)throws IOException{
        String requestUrl = STATIC_PROPERTIES.getProperty ( "requestUrl" );
        String requestHost = STATIC_PROPERTIES.getProperty ( "requestHost" );

        JSONObject body = new JSONObject (  );
        HttpResponse response = null;

        try {
            switch (type){
                case "get":
                    URI uri = null;
                    if(params != null){
                        uri = new URIBuilder(url).addParameters(paramsDictToList(params)).build();
                    }else {
                        uri = new URIBuilder (url).build();
                    }
                    HttpGet httpGet = new HttpGet(uri);
                    httpGet.setHeader("cookie", token);
                    if(requestHost==null){
                        response = httpClient.execute(httpGet);
                    }else {
                        response = httpClient.execute( HttpHost.create ( requestHost ), httpGet);
                    }
                    break;
                case "deletewithparams":
                    URI uri_for_delete = null;
                    if(params != null){
                        uri_for_delete = new URIBuilder (url).addParameters(paramsDictToList(params)).build();
                    }else {
                        uri_for_delete = new URIBuilder (url).build();
                    }
                    HttpDelete httpDelete = new HttpDelete (uri_for_delete);
                    httpDelete.setHeader("cookie", token);
                    if(requestHost==null){
                        response = httpClient.execute(httpDelete);
                    }else {
                        response = httpClient.execute(HttpHost.create ( requestHost ),httpDelete);
                    }
                    break;
                case "deletewithbody":
                    //使用我们重载的HttpDelete
                    HttpDeleteWithBody httpDelete1 = new HttpDeleteWithBody(url);
                    // 处理请求头
                    httpDelete1.setHeader ( "cookie", token );
                    // 处理请求体
                    StringEntity entity = new StringEntity(JSONObject.toJSONString(data), StandardCharsets.UTF_8);
                    entity.setContentEncoding( "UTF-8" );
                    entity.setContentType( "application/json" );//设置为 json数据
                    httpDelete1.setEntity(entity);
                    if(requestHost==null){
                        response = httpClient.execute(httpDelete1);
                    }else {
                        response = httpClient.execute(HttpHost.create ( requestHost ),httpDelete1);
                    }
                    break;
                case "post":
                    HttpPost httpPost = new HttpPost(url);
                    StringEntity postentity = new StringEntity(JSONObject.toJSONString(data), StandardCharsets.UTF_8 );
                    postentity.setContentEncoding("UTF-8");
                    postentity.setContentType("application/json");//设置为 json数据
                    httpPost.setEntity(postentity);
                    httpPost.setHeader("Cookie", token);
                    if(requestHost==null){
                        response = httpClient.execute (httpPost );
                    }else {
                        response = httpClient.execute ( HttpHost.create ( requestHost ),httpPost );
                    }
                    break;
                case "postwitharray":
                    HttpPost arrayhttpPost = new HttpPost(url);
                    StringEntity arraypostentity = new StringEntity(data.toString (), StandardCharsets.UTF_8 );
                    arraypostentity.setContentEncoding("UTF-8");
                    arraypostentity.setContentType("application/json");//设置为 json数据
                    arrayhttpPost.setEntity(arraypostentity);
                    arrayhttpPost.setHeader("Cookie", token);
                    if(requestHost==null){
                        response = httpClient.execute (arrayhttpPost );
                    }else {
                        response = httpClient.execute ( HttpHost.create ( requestHost ),arrayhttpPost );
                    }
                    break;
                case "put":
                    HttpPut httpput = new HttpPut(url);
                    StringEntity putentity = new StringEntity(JSONObject.toJSONString(data), StandardCharsets.UTF_8 );
                    putentity.setContentEncoding("UTF-8");
                    putentity.setContentType("application/json");//设置为 json数据
                    httpput.setEntity(putentity);
                    httpput.setHeader("Cookie", token);
                    if(requestHost==null){
                        response = httpClient.execute (httpput );
                    }else {
                        response = httpClient.execute ( HttpHost.create ( requestHost ),httpput );
                    }
                    break;
            }
            if (response.getStatusLine().getStatusCode() == 200) {
                String strResult = null;
                strResult = EntityUtils.toString(response.getEntity(), "utf-8");
                if ( strResult.isEmpty()){

                }
                body = JSONObject.parseObject(strResult);
            } else {

            }
        } catch (URISyntaxException | IOException | ParseException e) {
            e.printStackTrace ( );
            if (!(e instanceof SSLHandshakeException)) {

            } else {
                logger.error("SSLHandshakeException, detail", e);
            }
        }
        return body;
    }

    private static List<NameValuePair> paramsDictToList(Map<String, String> params) {
        List<NameValuePair> nvps = new ArrayList<>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        return nvps;
    }

}
