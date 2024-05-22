package com.yeyang.browser.headless.demo;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.Bucket;
import com.yeyang.browser.headless.oss.TencentUtils;
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

    private static void test2() {
        String url = "http://www.baidu.com";
        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo", x -> {
                });
        System.out.println(path);

        COSClient cosClient = TencentUtils.createCOSClient();
        Bucket bucket = TencentUtils.createBucket(cosClient);
        TencentUtils.uploadFile(cosClient, path);
    }


    private static void test3() {
        String url = "https://movie.douban.com";
        String path = PdfUtils.downloadPdf(url,
                "/tmp/pdf",
                "demo", x -> {
                });
        System.out.println(path);

        OssUtil.uploadFile("public", "demo", path);
    }
}
