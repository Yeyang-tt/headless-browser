package com.yeyang.browser.headless.util;

import cn.hutool.core.util.StrUtil;
import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.core.browser.Browser;
import com.ruiyun.jvppeteer.core.browser.BrowserFetcher;
import com.ruiyun.jvppeteer.core.page.Page;
import com.ruiyun.jvppeteer.options.LaunchOptions;
import com.ruiyun.jvppeteer.options.LaunchOptionsBuilder;
import com.ruiyun.jvppeteer.options.PDFOptions;
import com.ruiyun.jvppeteer.protocol.DOM.Margin;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * pdf跑龙套
 * PDF工具类
 *
 * @author Yeyang
 */
public class PdfUtils {

    static {
        BrowserFetcher.downloadURLs.get("chrome").put("host", "https://npmmirror.com/mirrors");
    }

    /**
     * 无头浏览器下载PDF文件
     *
     * @param url      url
     * @param path     路径
     * @param fileName 文件名
     * @param listener 渲染监听器
     * @return {@link String}
     */
    @SneakyThrows
    public static String downloadPdf(String url, String path, String fileName, Consumer<Page> listener) {
        // 默认页边距
        Margin margin = new Margin();
        margin.setTop("40");

        return downloadPdf(url, path, fileName, listener, margin);
    }

    /**
     * 无头浏览器下载PDF文件
     *
     * @param url      url
     * @param path     路径
     * @param fileName 文件名称
     * @param listener 渲染监听器
     * @param margin   页边距
     * @return {@link String}
     */
    @SneakyThrows
    public static String downloadPdf(String url, String path, String fileName, Consumer<Page> listener, Margin margin) {
        if (StrUtil.isBlank(url) || StrUtil.isBlank(path) || StrUtil.isBlank(fileName)) {
            throw new RuntimeException("Parameter is null or empty");
        }

        Browser browser = null;
        Page page = null;
        try {
            // 驱动自动下载，第一次下载后不会再下载
            BrowserFetcher.downloadIfNotExist(null);
            List<String> args = Arrays.asList("--no-sandbox", "--disable-setuid-sandbox");

            // 生成pdf必须在无厘头模式下才能生效
            LaunchOptions options = new LaunchOptionsBuilder()
                    .withArgs(args)
                    .withHeadless(true)
                    .build();

            browser = Puppeteer.launch(options);
            page = browser.newPage();
            page.goTo(url);

            // 渲染监听器,可以实现监听逻辑
            listener.accept(page);

            // 文件路径
            String filePath = String.format("%s/%s.pdf", path, fileName);

            PDFOptions pdfOptions = new PDFOptions();
            pdfOptions.setMargin(margin);
            pdfOptions.setPath(filePath);
            page.pdf(pdfOptions);

            return filePath;
        } catch (InterruptedException | IOException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            if (page != null) {
                page.close();
            }
            if (browser != null) {
                browser.close();
            }
        }
    }

    /**
     * 日期路径
     *
     * @return {@link String}
     */
    public static String datePath() {
        LocalDate now = LocalDate.now();
        return String.format("/%s/%s/%s", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }
}
