
package com.fly.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
@Controller
@RequestMapping("/hhjson")
public class HuohuJsonController {
	@Autowired
    private ShardedJedisPool shardedJedisPool;
	@RequestMapping("/new1.do")
	@ResponseBody
	public String grab(String page) throws Throwable{
	    
	    ShardedJedis jedis = shardedJedisPool.getResource();
	    Xiaohua xh=new Xiaohua();
	    List<String> texts = jedis.lrange("textList", 0, 6);
	    int j=0;
	    for(String text:texts){
	    	Item i=new Item();
	    	i.setTitle(text.substring(0,10));
	    	i.setUrl("http://joke.uhdog.com/detail.html?id="+(j++));
	    	xh.addItem(i);
	    }
	    xh.setNote("文档编码：UTF-8，联系电话13810017902");
	    xh.setContact("13810017902");
	    xh.setHomepage("http://joke.uhdog.com");
	    xh.setCreate_time((new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
	    jedis.close();
		return JSON.toJSONString(xh);
	}
	@RequestMapping("/new.do")
	@ResponseBody
	public String new1(String page) throws Throwable{
	    
	    ShardedJedis jedis = shardedJedisPool.getResource();
	    Xiaohua xh=new Xiaohua();
	    List<String> texts = jedis.lrange("chenrenList", 0, 6);
	    int j=0;
	    for(String text:texts){
	    	Item i=new Item();
	    	Map<String,String> x=JSON.parseObject(text, Map.class);
	    	i.setTitle(x.get("header"));
	    	i.setUrl("http://joke.uhdog.com/detail2.html?id="+(j++));
	    	xh.addItem(i);
	    }
	    xh.setNote("文档编码：UTF-8，联系电话13810017902");
	    xh.setContact("13810017902");
	    xh.setHomepage("http://joke.uhdog.com");
	    xh.setCreate_time((new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
	    jedis.close();
		return JSON.toJSONString(xh);
	}
}

class Xiaohua{
	String note;
	String homepage;
	String contact;
	String create_time;
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	
	
	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}



	List<Item> items=new ArrayList<Item>();
	
	public void addItem(Item e){
		items.add(e);
	}
}

class Item{
	String title;
	String url;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
