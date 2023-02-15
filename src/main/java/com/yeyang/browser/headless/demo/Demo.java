package com.yeyang.browser.headless.demo;

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
                "demo", ElementRenderListener::renderEvent);
        System.out.println(path);
    }




}
