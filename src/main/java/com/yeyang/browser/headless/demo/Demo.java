package com.yeyang.browser.headless.demo;

import com.ruiyun.jvppeteer.protocol.DOM.Margin;
import com.yeyang.browser.headless.listener.ElementRenderListener;
import com.yeyang.browser.headless.util.PdfUtils;

/**
 * @author Yeyang
 */
public class Demo {

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        String url = "http://www.baidu.com";
        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo", x -> {});
        System.out.println(path);
    }

    private static void test2() {
        String url = "";

        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo2", ElementRenderListener::renderEvent);
        System.out.println(path);
    }

    private static void test3() {
        String url = "https://nacos.io/zh-cn/index.html";

        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo3", ElementRenderListener::renderEvent);
        System.out.println(path);
    }


    /**
     * 自定义页边距
     */
    private static void test4() {
        String url = "https://nacos.io/zh-cn/index.html";

        //页边距
        Margin margin = new Margin();
        margin.setTop("10");
        margin.setBottom("10");
        margin.setLeft("10");
        margin.setRight("10");

        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo4", ElementRenderListener::renderEvent,
                margin);
        System.out.println(path);
    }
}
