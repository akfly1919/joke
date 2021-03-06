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

	int pageSize = 10;

	@ResponseBody
	@RequestMapping(value = "/hi.do")
	public String hi(String page) {
		System.out.println("GetDataController.hi" + page);
		try {
			ShardedJedis jedis = shardedJedisPool.getResource();
			long size = jedis.llen("textList");
			Random r = new Random();
			int p = 0;
			if (page == null || "0".equals(page)) {
				p = r.nextInt((int) (size / pageSize));
				if (p == 0) {
					p = 1;
				}
			} else {
				p = Integer.valueOf(page);
			}
			long begin = 0l + (p - 1) * pageSize;
			long end = begin + pageSize - 1;
			System.out.println("GetDataController.hi begin=" + begin + ",end=" + end);
			List<String> texts = jedis.lrange("textList", begin, end);
			System.out.println("GetDataController.hi size:" + texts!=null?texts.size():0);
			jedis.close();
			return JSON.toJSONString(texts);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/go.do")
	public String go(String id) {
		try {
			ShardedJedis jedis = shardedJedisPool.getResource();
			long p = 0;
			if (id == null) {
				p = 0;
			} else {
				try {
					p = Integer.valueOf(id);
				} catch (Throwable a) {
					p = 0;
				}
			}
			jedis.close();
			String text = jedis.lindex("textList", p);
			return JSON.toJSONString(text);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/hiChenren.do")
	public String hiChenren(String page) {
		System.out.println("GetDataController.hi" + page);
		try {
			ShardedJedis jedis = shardedJedisPool.getResource();
			long size = jedis.llen("chenrenList");
			Random r = new Random();
			int p = 0;
			if (page == null || "0".equals(page)) {
				p = r.nextInt((int) (size / pageSize));
				if (p == 0) {
					p = 1;
				}
			} else {
				p = Integer.valueOf(page);
			}
			long begin = 0l + (p - 1) * pageSize;
			long end = begin + pageSize - 1;
			System.out.println("GetDataController.hi begin=" + begin + ",end=" + end);
			List<String> texts = jedis.lrange("chenrenList", begin, end);
			System.out.println("GetDataController.hi " + texts!=null?texts.size():0);
			jedis.close();
			return JSON.toJSONString(texts);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}

	@ResponseBody
	@RequestMapping(value = "/goChenren.do")
	public String goChenren(String id) {
		try {
			ShardedJedis jedis = shardedJedisPool.getResource();
			long p = 0;
			if (id == null) {
				p = 0;
			} else {
				try {
					p = Integer.valueOf(id);
				} catch (Throwable a) {
					p = 0;
				}
			}
			jedis.close();
			String text = jedis.lindex("chenrenList", p);
			return JSON.toJSONString(text);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return "";
	}
}
