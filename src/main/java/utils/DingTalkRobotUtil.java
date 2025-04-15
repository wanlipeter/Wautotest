package utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.util.List;

public class DingTalkRobotUtil {

    public static void sendMessage(String webHookTokenUrl, String content) throws IOException {

        HttpClient httpclient = HttpClients.createDefault();

        HttpPost httppost = new HttpPost(webHookTokenUrl);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");


        JSONObject textContent = new JSONObject();
        textContent.put("content", content);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "text");
        jsonObject.put("text", textContent);

        String textMsg = jsonObject.toJSONString();
        StringEntity se = new StringEntity(textMsg, "utf-8");
        httppost.setEntity(se);

        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println(result);
        }

    }

    public static void sendMessage(String content, String... webHookTokenUrl) throws IOException {
        for (String url : webHookTokenUrl) {
            sendMessage(url, content);
        }
    }

    public static void sendMessageByLink(String webHookTokenUrl, String text, String title, String messageUrl, String picUrl) throws Exception {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(webHookTokenUrl);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        JSONObject linkContent = new JSONObject();
        linkContent.put("msgtype", "link");
        JSONObject link = new JSONObject();
        link.put("text", text);
        link.put("title", title);
        link.put("picUrl", picUrl);
        link.put("messageUrl", messageUrl);
        linkContent.put("link", link);
        StringEntity se = new StringEntity(linkContent.toJSONString(), "utf-8");
        httppost.setEntity(se);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("-------已发送报警信息到钉钉群，注意查看--------");
        }
    }

    public static void sendMessageByTextToManyNumbers(String webHookTokenUrl, String content, List<String> atNumber, Boolean isAtAll) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(webHookTokenUrl);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        JSONObject textContent = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject at = new JSONObject();
        JSONArray atMobiles = new JSONArray();
        for(String value:atNumber){
            atMobiles.add(value);
        }
//        atMobiles.add(atNumber);
        textContent.put("msgtype", "text");
        text.put("content", content);
        textContent.put("text", text);
        textContent.put("at", at);
        at.put("isAtAll", isAtAll);
        at.put("atMobiles", atMobiles);
        textContent.put("at", at);
        StringEntity stringEntity = new StringEntity(textContent.toJSONString(), "utf-8");
        httppost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        System.out.println("-------已发送报警信息到钉钉群，注意查看--------");
    }

    public static void sendMessageByTextToOne(String webHookTokenUrl, String content, String atNumber, Boolean isAtAll) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(webHookTokenUrl);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        JSONObject textContent = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject at = new JSONObject();
        JSONArray atMobiles = new JSONArray();
        atMobiles.add(atNumber);
//        atMobiles.add(atNumber);
        textContent.put("msgtype", "text");
        text.put("content", content);
        textContent.put("text", text);
        textContent.put("at", at);
        at.put("isAtAll", isAtAll);
        at.put("atMobiles", atMobiles);
        textContent.put("at", at);
        StringEntity stringEntity = new StringEntity(textContent.toJSONString(), "utf-8");
        httppost.setEntity(stringEntity);
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            System.out.println("-------已发送报警信息到钉钉群，注意查看--------");
        }
    }

}
