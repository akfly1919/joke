package com.fly.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class WordFilter {

    /**
     * 检验敏感词，并高亮显示
     * 
     * @param text
     * @return
     */
    public static String checkWord(String text, String senWord) {
        if (senWord==null || "".equals(senWord)) {
            return text;
        }
        Pattern p = null; // 正则表达式
        Matcher m = null; // 操作的字符串
        String sRegex = senWord;// 敏感词

        p = Pattern.compile(sRegex, Pattern.CASE_INSENSITIVE);
        m = p.matcher(text);
        String s = "";
        String sTmp = null;
        StringBuffer sb = new StringBuffer();
        List<String> eSenWord = new ArrayList<String>();// 存放html标签和纯文本中同时出现的关键词
        while (m.find()) {
            sTmp = m.group();
            if (isHtml(text, sTmp)) {// 判断纯文本中是否有关键词，纯文本中有，则必高亮
                if (!isIgnore(text, sTmp)) {// 判断html中是否有关键词，html中没有这直接加高亮
                    s = "<font color=\"red\" style=\"background:yellow;\">" + sTmp + "</font>";
                    m.appendReplacement(sb, s);
                } else {// html中有关键词，则另处理
                    if (!eSenWord.contains(sTmp)) {
                        eSenWord.add(sTmp);
                    }
                }
            } else {
                s = sTmp;
                m.appendReplacement(sb, s);
            }
        }
        m.appendTail(sb);
        String result = sb.toString();
    
        return result;
    }

    /**
     * 判断敏感词是否是html标签中的文字
     * 
     * @param text
     * @param sTmp
     * @return
     */
    private static boolean isHtml(String htmlText, String sTmp) {
        boolean flag = false;

        Pattern p = null; // 正则表达式
        Matcher m = null; // 操作的字符串
        String sRegex = htmlToText(htmlText);

        p = Pattern.compile(sTmp, Pattern.CASE_INSENSITIVE);
        m = p.matcher(sRegex);
        while (m.find()) {
            flag = true;
            break;
        }
        return flag;

    }

    /**
     * 去掉html中标签
     * 
     * @param htmlText
     * @return
     */
    private static String htmlToText(String text) {

        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

        Pattern p_html1;
        Matcher m_html1;

        // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
        String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";

        // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
        String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";

        // 定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";
        String regEx_html1 = "<[^>]+";
        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        m_script = p_script.matcher(text);
        text = m_script.replaceAll(""); // 过滤script标签

        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(text);
        text = m_style.replaceAll(""); // 过滤style标签

        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(text);
        text = m_html.replaceAll(""); // 过滤html标签

        p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
        m_html1 = p_html1.matcher(text);
        text = m_html1.replaceAll(""); // 过滤html标签

        return text;// 返回文本字符串

    }

    /**
     * 统计敏感词出现次数
     * 
     * @param text
     * @param subString
     * @return
     */
    private static int countSubStr(String text, String subString) {
        int count = 0;
        int index = -1;
        while (true) {
            index = text.indexOf(subString, index + 1);
            if (index >= 0) {
                count++;
            } else {
                break;
            }
            System.out.println(count);
        }
        return count;
    }

    /**
     * 判断敏感词是否忽略
     * 
     * 若敏感词在原文与纯文本中出现次数不同，则忽略掉
     * 
     * @param text
     * @param senWord
     * @return
     */
    public static boolean isIgnore(String text, String senWord) {
        boolean flag = true;
        String tt = htmlToText(text);
        if (countSubStr(text, senWord) == countSubStr(htmlToText(text), senWord)) {
            flag = false;
        }
        return flag;
    }

    public static String eFilter(String text, String senWord) {
        int i = 1;
        String sen = senWord;
        String temp = text;
        List<String> apart = new ArrayList<String>();// 存放解析后的字符串
        List<String> loc = new ArrayList<String>();// 存放需要加高亮的已经加序号好的关键词
        StringBuilder is = new StringBuilder();
        int index = 0;
        // 原文解析存放apart（所有关键词已带序号），并得到关键词加序号的镜像iss
        while (true) {
            index = temp.indexOf(sen);
            if (index >= 0) {
                apart.add(temp.substring(0, temp.indexOf(sen)));
                apart.add(sen + i);
                is.append(temp.substring(0, index + sen.length())).append(i + ",");
                i++;
            } else {
                is.append(temp);
                apart.add(temp);
                break;
            }
            temp = temp.substring(index + sen.length(), temp.length());
        }

        String iss = is.toString();
        String temp2 = WordFilter.htmlToText(iss);
        int index2 = 0;
        int index3 = 0;
        // 通过iss取到需要加高亮的关键词（已经加序号），并放入loc
        while (true) {
            index2 = temp2.indexOf(sen);
            index3 = temp2.indexOf(",", index2);
            if (index2 >= 0) {
                loc.add(temp2.substring(index2, index3));
            } else {
                break;
            }
            temp2 = temp2.substring(index2 + sen.length(), temp2.length());
        }
        StringBuilder re = new StringBuilder();
        int n1 = 0;
        // 替换apart中的已加序号的关键词，如果在loc中（需高亮的）加高亮，否则替换成原关键词（不需高亮的把序号去掉）
        for (String s : apart) {
            if (!s.contains(sen)) {
                re.append(s);
            } else if (s.contains(s) && !loc.contains(s)) {
                re.append(sen);
            } else {
                re.append("<font color=\"red\" style=\"background:yellow;\">" + sen + "</font>");
            }
        }
        return re.toString();
    }

    public static void main(String[] args) {
        String text ="asdf";
        String senWord = "a";
        WordFilter.checkWord(text,senWord);
    }
}
