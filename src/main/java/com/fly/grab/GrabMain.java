package com.fly.grab;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.web.util.HtmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GrabMain {
	public static void main(String[] args) throws IOException, XPatherException, ParserConfigurationException, XPathExpressionException {
		System.out.println(HtmlUtils.htmlUnescape("傍晚公园的长椅上，两个老人紧紧的依偎在一起，落日余晖，这一刻显得那么安详，神圣。过了好一会儿，老太太推了推老头子说：天儿不早了，我得回去了，不然我家那口子又得着急了。 卧槽，这特么剧情不按套路发展&hellip;&hellip;心好累～～"));
	    String url = "http://www.qiushibaike.com/text/page/2/";
	    String exp = "//*[@id=\"content-left\"]/div[*]/a[1]";

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
	        for (int i = 0; i < nodeList.getLength(); i++) {
	            Node node = nodeList.item(i);
	            System.out.println(node.getNodeValue() == null ? node.getTextContent().trim() : node.getNodeValue().trim());
	        }
	    }
	}

}
