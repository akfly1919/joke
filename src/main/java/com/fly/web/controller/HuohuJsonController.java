package com.fly.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
@Controller
@RequestMapping("/hhjson")
public class HuohuJsonController {
	@Autowired
    private ShardedJedisPool shardedJedisPool;
	@RequestMapping("/new.do")
	@ResponseBody
	public String grab(String page) throws Throwable{
	    
	    ShardedJedis jedis = shardedJedisPool.getResource();

		return "ok";
	}
}
class Xiaohua{
	String note;
	String homepage;
	String contact;
	String create_time;
	
	List<Item> items=new ArrayList<Item>();
}

class Item{
	String title;
	String url;
}
