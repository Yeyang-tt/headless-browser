package com.yeyang.browser.headless.listener;

import com.ruiyun.jvppeteer.core.page.ElementHandle;
import com.ruiyun.jvppeteer.core.page.Page;
import lombok.SneakyThrows;

import java.util.Objects;

/**
 * 元素侦听器
 *
 * @author Yeyang
 */
public class ElementRenderListener {

    @SneakyThrows
    public static void renderEvent(Page page) {
        //监听页面元素，finish-render-complete
        //监听页面渲染完成,渲染完成后下载，如果没有则会30s超时
        ElementHandle elementHandle = page.waitForSelector("#finish-render-complete");
        if (!Objects.equals(elementHandle.getProperty("textContent").jsonValue(), "success")) {
            throw new RuntimeException("Unable to get page resources");
        }
    }
}
