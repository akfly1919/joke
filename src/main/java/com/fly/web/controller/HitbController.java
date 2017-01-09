package com.fly.web.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.HttpUtils;
import com.fly.Response;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Controller
@RequestMapping("/hitb")
public class HitbController {
    @Autowired
    private ShardedJedisPool shardedJedisPool;
    @RequestMapping(value="/urltran.do")
    public String urltran(String url) throws Throwable{
    	long begTime=System.currentTimeMillis();
        System.out.println("urltran url:"+url);
        String s="";
        CloseableHttpClient httpClient = HttpUtils.getHttpClient(5000);
        for(int i=0;i<1;i++){
        Response r = null;
        CloseableHttpResponse response = null;
        try {
            ShardedJedis jedis = shardedJedisPool.getResource();
            String cookie=jedis.get("mycookie");
            System.out.println("urltran cookie:"+cookie);
            jedis.close();
            String turl="http://pub.alimama.com/urltrans/urltrans.json?"
            		+ "siteid=3406996&adzoneid=22076161&promotionURL="+java.net.URLEncoder.encode(url,"utf-8")+""
            				+ "&t=1480063247548&pvid=&_tb_token_=DJWghTHg67q&_input_charset=utf-8";
            System.out.println("urltran turl:"+turl);
            HttpGet httpPost = new HttpGet(turl);
            try {
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
                httpPost.addHeader("Cookie",
                        cookie);
                response = httpClient.execute(httpPost);
                r = new Response();
                r.setStatusCode(response.getStatusLine().getStatusCode());
                if (response.getStatusLine().getStatusCode() == 200) {
                    r.setResponseString(EntityUtils.toString(response.getEntity()));
                    s = r.getResponseString();
                    System.out.println("urltran return s:"+s);
                    JSONObject jsonObj = JSON.parseObject(s);
                    JSONObject jsonObj1 = JSON.parseObject(jsonObj.get("data").toString());
                    return "redirect:"+jsonObj1.get("shortLinkUrl").toString();
                }else{
                    throw new Throwable("111");
                }

            } catch (Exception e) {
                throw e;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        }
        System.out.println("totalTime:"+(System.currentTimeMillis()-begTime));
        return "redirect:http://joke.uhdog.com";
    }
}
