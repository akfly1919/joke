package com.fly.web.controller;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Controller
@RequestMapping("/getdata")
public class GetDataController {
	@Autowired
	private ShardedJedisPool shardedJedisPool;
	
	int pageSize=10;

	@ResponseBody
	@RequestMapping(value = "/hi.do")
	public String hi(String page) {
		ShardedJedis jedis = shardedJedisPool.getResource();
		long size=jedis.llen("textList");
		Random r=new Random();
		int p=0;
		if(page==null||"0".equals(page)){
			p=r.nextInt((int)(size/pageSize));
			if(p==0){
				p=1;
			}
		}else{
			p=Integer.valueOf(page);
		}
		long begin = 0l+(p-1)*pageSize;
		long end = begin+pageSize-1;
		List<String> texts = jedis.lrange("textList", begin, end);
		System.out.println(texts.size());
		return JSON.toJSONString(texts);
	}
}
