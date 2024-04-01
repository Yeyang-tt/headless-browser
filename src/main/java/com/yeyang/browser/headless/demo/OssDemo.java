package com.yeyang.browser.headless.demo;

import com.yeyang.browser.headless.util.OssUtil;
import com.yeyang.browser.headless.util.PdfUtils;

public class OssDemo {

    private static void test1() {
        String url = "http://www.baidu.com";
        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo", x -> {
                });
        System.out.println(path);

        OssUtil.uploadFile("public", "demo", path);
    }
}
