package com.fly.web.controller;

import java.io.IOException;

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

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Controller
@RequestMapping("/grab")
public class GrabController {
	@Autowired
    private ShardedJedisPool shardedJedisPool;
	@RequestMapping("/go.do")
	@ResponseBody
	public String grab(String page) throws Throwable{
	    String url = "http://www.qiushibaike.com/text/page/"+page+"/";
	    String exp = "//*[@id=\"content-left\"]/div[*]/a[1]";
	    
	    ShardedJedis jedis = shardedJedisPool.getResource();

	    String html = null;
	    try {
	        Connection connect = Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
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
	        String[] ss=new String[nodeList.getLength()];
	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Node node = nodeList.item(i);
	            String s=node.getNodeValue() == null ? node.getTextContent().trim() : node.getNodeValue().trim();
	            ss[i]=HtmlUtils.htmlUnescape(s);
	        }
	        jedis.lpush("textList", ss);
	        System.out.println(ss);
	    }
	    jedis.close();
		return "ok";
	}
}
