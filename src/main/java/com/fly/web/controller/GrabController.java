package com.fly.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Controller
@RequestMapping("/grab")
public class GrabController {
	@Autowired
	private ShardedJedisPool shardedJedisPool;

	@RequestMapping("/go.do")
	@ResponseBody
	public String grab(String page) throws Throwable {
		String url = "http://www.qiushibaike.com/text/page/" + page + "/";
		String exp = "//*[@id=\"content-left\"]/div[*]/a[1]";

		ShardedJedis jedis = shardedJedisPool.getResource();

		String html = null;
		try {
			Connection connect = Jsoup.connect(url).header("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			html = connect.get().body().html();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HtmlCleaner hc = new HtmlCleaner();
		TagNode tn = hc.clean(html);
		Document dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
		XPath xPath = XPathFactory.newInstance().newXPath();
		Object result;
		result = xPath.evaluate(exp, dom, XPathConstants.NODESET);
		if (result instanceof NodeList) {
			NodeList nodeList = (NodeList) result;
			System.out.println(nodeList.getLength());
			String[] ss = new String[nodeList.getLength()];
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String s = node.getNodeValue() == null ? node.getTextContent().trim() : node.getNodeValue().trim();
				ss[i] = HtmlUtils.htmlUnescape(s);
			}
			jedis.lpush("textList", ss);
		}
		jedis.close();
		return "ok";
	}

	@RequestMapping("/goChenren.do")
	@ResponseBody
	public String grabChenren(String page) throws Throwable {

		ShardedJedis jedis = shardedJedisPool.getResource();

		String url = "http://www.laifudao.com/wangwen/chengrenxiaohua_"+page+".htm";
		String expHeader="//article/header/h1/a";
		String expContent="//article/div/section[1]/p";
		String html = null;
		try {
			Connection connect = Jsoup.connect(url).header("User-Agent",
					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			html = connect.get().body().html();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("---------------------");
		HtmlCleaner hc = new HtmlCleaner();
		TagNode tn = hc.clean(html);
		Document dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
		XPath xPath = XPathFactory.newInstance().newXPath();
		Object result;
		result = xPath.evaluate(expHeader, dom, XPathConstants.NODESET);
		List<xh> xhs=new ArrayList<xh>();
		if (result instanceof NodeList) {
			NodeList nodeList = (NodeList) result;
			System.out.println(nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String s = node.getNodeValue() == null ? node.getTextContent().trim() : node.getNodeValue().trim();
				xh x=new xh();
				x.setHeader(HtmlUtils.htmlUnescape(s));
				xhs.add(x);
			}
		}
		Object result1;
		result1 = xPath.evaluate(expContent, dom, XPathConstants.NODESET);
		if (result1 instanceof NodeList) {
			NodeList nodeList = (NodeList) result1;
			System.out.println(nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				String s = node.getNodeValue() == null ? node.getTextContent().trim() : node.getNodeValue().trim();
				xh x=xhs.get(i);
				x.setContext(HtmlUtils.htmlUnescape(s));
				jedis.lpush("chenrenList", JSON.toJSONString(x));
			}
		}
		
		jedis.close();
		return "ok";
	}

	public static void main(String[] args) throws Throwable {
		
	}
	
	 class xh implements Serializable{
		private String header;
		
		private String context;

		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public xh(String header, String context) {
			this.header = header;
			this.context = context;
		}
		public xh(){}

		@Override
		public String toString() {
			return "xh [header=" + header + ", context=" + context + "]";
		}
	}
}
