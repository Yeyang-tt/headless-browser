package com.yeyang.browser.headless.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.xslf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件转图片工具栏
 *
 * @author Yeyang
 * @date 2024/08/15
 */
@Slf4j
@UtilityClass
public class FileToImgUtil {

    /**
     * 默认字体
     */
    public final String DEFAULT_FONT = "宋体";

    /**
     * 自动识别
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @param suffix      后缀
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> autoImg(String bucketName, String dir, InputStream inputStream, String suffix) throws Exception {
        if (StrUtil.isEmpty(suffix)) {
            return List.of();
        }
        return switch (suffix) {
            case "docx", "doc" -> wordToImg(bucketName, dir, inputStream);
            case "pdf" -> pdfToImg(bucketName, dir, inputStream);
            case "txt" -> txtToImg(bucketName, dir, inputStream);
            case "pptx" -> pptxToImg(bucketName, dir, inputStream);
            case "ppt" -> pptToImg(bucketName, dir, inputStream);
            default -> List.of();
        };
    }

    /**
     * 将pdf转成图片
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> pdfToImg(String bucketName, String dir, InputStream inputStream) throws Exception {
        List<String> result = new ArrayList<>();
        PDDocument doc = PDDocument.load(inputStream);
        PDFRenderer renderer = new PDFRenderer(doc);
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // Windows native DPI
            BufferedImage image = renderer.renderImageWithDPI(i, 144);
            ImageIO.write(image, "PNG", outputStream);

            // 租户ID/服务编码/文件名
            String fileName = getFileName(dir);
            // SpringUtil.getBean(FileTemplate.class).putObject(bucketName, fileName, new ByteArrayInputStream(outputStream.toByteArray()));
            result.add(fileName);
        }
        doc.close();
        return result;
    }

    /**
     * txt转成转成图片
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> txtToImg(String bucketName, String dir, InputStream inputStream) throws Exception {
        return wordToImg(bucketName, dir, inputStream);
    }

    /**
     * 将word转成图片
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> wordToImg(String bucketName, String dir, InputStream inputStream) throws Exception {
        List<String> result = new ArrayList<>();
        Document doc = new Document(inputStream);
        for (int i = 0; i < doc.getPageCount(); i++) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document extractedPage = doc.extractPages(i, 1);
            extractedPage.save(outputStream, SaveFormat.PNG);
            // 租户ID/服务编码/文件名
            String fileName = getFileName(dir);
            result.add(fileName);
        }
        return result;
    }

    /**
     * 将pptx转成图片
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> pptxToImg(String bucketName, String dir, InputStream inputStream) throws Exception {
        XMLSlideShow pptx = new XMLSlideShow(inputStream);
        List<XSLFSlide> slides = pptx.getSlides();
        // 处理默认字体
        if (CollUtil.isNotEmpty(slides)) {
            slides.forEach(slide -> {
                pptxFontHandle(slide.getShapes());
            });
        }
        List<String> result = pptToImgWrite(bucketName, dir, (int) pptx.getPageSize().getWidth(), (int) pptx.getPageSize().getHeight(), slides);
        pptx.close();
        return result;
    }

    /**
     * 将ppt转成图片
     *
     * @param bucketName  存储桶名称
     * @param dir         dir
     * @param inputStream 输入流
     * @return {@link List }<{@link String }>
     * @throws Exception 例外
     */
    public List<String> pptToImg(String bucketName, String dir, InputStream inputStream) throws Exception {
        HSLFSlideShow ppt = new HSLFSlideShow(inputStream);
        List<HSLFSlide> slides = ppt.getSlides();
        // 处理默认字体
        if (CollUtil.isNotEmpty(slides)) {
            slides.forEach(slide -> {
                pptFontHandle(slide.getShapes());
            });
        }
        List<String> result = pptToImgWrite(bucketName, dir, (int) ppt.getPageSize().getWidth(), (int) ppt.getPageSize().getHeight(), slides);
        ppt.close();
        return result;
    }

    /**
     * ppt转img
     *
     * @param bucketName 存储桶名称
     * @param dir        dir
     * @param width      宽度
     * @param height     高度
     * @param slides     幻灯片
     * @return {@link List }<{@link String }>
     * @throws IOException IOException
     */
    private List<String> pptToImgWrite(String bucketName, String dir, int width, int height, List<? extends Slide> slides) throws IOException {
        List<String> result = new ArrayList<>();
        // 遍历每一页幻灯片，将其绘制到长图片上
        for (Slide slide : slides) {
            // 创建临时图片，用于绘制单页幻灯片内容
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            slide.draw(graphics);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);

            // 租户ID/服务编码/文件名
            String fileName = getFileName(dir);
            result.add(fileName);
            graphics.dispose();
        }
        return result;
    }

    /**
     * 获取文件名
     *
     * @param dir dir
     * @return {@link String }
     */
    private String getFileName(String dir) {
        return String.format("%s/%s%s", dir, UUID.fastUUID().toString(true), ".png");
    }

    /**
     * pptx字体处理
     *
     * @param shapes 形状
     */
    private void pptxFontHandle(List<XSLFShape> shapes) {
        for (XSLFShape shape : shapes) {
            if (shape instanceof XSLFGroupShape groupShape) {
                // 对XSLFGroupShape进行递归处理
                pptxFontHandle(groupShape.getShapes());
            } else if (shape instanceof XSLFTextShape xslfTextShape) {
                List<XSLFTextParagraph> textParagraphs = xslfTextShape.getTextParagraphs();
                if (CollUtil.isEmpty(textParagraphs)) {
                    return;
                }
                textParagraphs.forEach(textParagraph -> {
                    List<XSLFTextRun> textRuns = textParagraph.getTextRuns();
                    if (CollUtil.isEmpty(textRuns)) {
                        return;
                    }
                    textRuns.forEach(textRun -> {
                        String fontFamily = textRun.getFontFamily();
                        if (StrUtil.isBlank(fontFamily)) {
                            textRun.setFontFamily(DEFAULT_FONT);
                            log.warn("pptx replace default font [text]:{}", textRun.getRawText());
                        }
                    });
                });
            } else {
                log.warn("pptx unknown shape:{}", shape.getClass());
            }
        }
    }

    /**
     * ppt字体处理
     *
     * @param shapes 形状
     */
    private void pptFontHandle(List<HSLFShape> shapes) {
        for (HSLFShape shape : shapes) {
            if (shape instanceof HSLFGroupShape groupShape) {
                // 对HSLFShape进行递归处理
                pptFontHandle(groupShape.getShapes());
            } else if (shape instanceof HSLFTextShape textShape) {
                List<HSLFTextParagraph> textParagraphs = textShape.getTextParagraphs();
                if (CollUtil.isEmpty(textParagraphs)) {
                    return;
                }
                textParagraphs.forEach(textParagraph -> {
                    List<HSLFTextRun> textRuns = textParagraph.getTextRuns();
                    if (CollUtil.isEmpty(textRuns)) {
                        return;
                    }
                    textRuns.forEach(textRun -> {
                        String fontFamily = textRun.getFontFamily();
                        textRun.setFontFamily("宋体");
                        if (StrUtil.isBlank(fontFamily)) {
                            textRun.setFontFamily(DEFAULT_FONT);
                            log.warn("ppt replace default font [text]:{}", textRun.getRawText());
                        }
                    });
                });
            } else {
                log.warn("ppt unknown shape:{}", shape.getClass());
            }
        }
    }

    /**
     * pdf字体处理
     * 未经过验证
     *
     * @param pages 页
     * @throws Exception 例外
     */
    private void pdfFontHandle(PDPageTree pages) throws Exception {
        if (pages == null) {
            return;
        }
        for (PDPage page : pages) {
            PDResources resources = page.getResources();
            if (resources == null || resources.getFontNames() == null) {
                return;
            }
            for (COSName fontName : resources.getFontNames()) {
                PDFont font = resources.getFont(fontName);
                if (font == null || StrUtil.isBlank(font.getName())) {
                    resources.put(fontName, PDType1Font.SYMBOL);
                }
            }
        }
    }
}