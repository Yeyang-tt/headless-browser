package com.yeyang.browser.headless.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * pdf工具栏
 */
@Slf4j
@UtilityClass
public class PdfConvertUtil {

    /**
     * 默认路径
     */
    private final String DEFAULT_PATH = "/opt/pdf/temp";

    /**
     * libreoffice路径
     */
    private final String LIBRE_OFFICE_PATH = "/opt/libreoffice24.8";

    public String toFile(InputStream inputStream, String suffix) {
        if (inputStream == null || StrUtil.isEmpty(suffix)) {
            return null;
        }
        try {
            String fileName = String.format("%s.%s", IdUtil.fastSimpleUUID(), suffix);
            log.debug("PDF工具-源文件：{}", fileName);
            String filePath = workPath(fileName);
            FileUtil.writeFromStream(inputStream, new File(filePath));
            return fileName;
        } catch (IORuntimeException e) {
            log.error("PDF工具-文件转存失败：", e);
        }
        return null;
    }

    /**
     * 转pdf
     *
     * @param fileName 文件名
     * @return {@link String }
     */
    public String toPdf(String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            return null;
        }

        OfficeManager officeManager = LocalOfficeManager.builder()
                .officeHome(LIBRE_OFFICE_PATH)
                .build();

        try {
            long start = System.currentTimeMillis();
            String targetFileName = String.format("%s.pdf", FileUtil.getPrefix(fileName));
            String source = workPath(fileName);
            String target = workPath(targetFileName);
            log.debug("PDF工具-转换PDF-源文件：{},目标文件:{} ", source, target);

            officeManager.start();
            LocalConverter.builder()
                    .officeManager(officeManager)
                    .build()
                    .convert(new File(source))
                    .to(new File(target))
                    .execute();

            //
            // // docx文件单独处理
            // if (Objects.equals("docx", FileUtil.getSuffix(fileName)) || Objects.equals("doc", FileUtil.getSuffix(fileName))) {
            // 	Document doc = new Document(source);
            // 	doc.save(workPath(target, targetFileName), SaveFormat.PDF);
            // } else {
            // 	// 执行命令
            //  // source /zbcloud/pdf/temp/1.pptx
            //  // target /zbcloud/pdf/temp 不需要文件名
            // 	String command = CommandUtil.pdfCommand(source, target);
            // 	if (StrUtil.isEmpty(command)) {
            // 		log.debug("PDF工具-转换PDF-错误指令：{}：{}：{}", command, source, target);
            // 	}
            // 	CommandUtil.execAuto(command);
            // 	log.debug("PDF工具-转换PDF-转化命令：{}：{}：{}", command, source, target);
            // }

            long end = System.currentTimeMillis();
            log.debug("PDF工具-转换PDF-转换耗时(毫秒)：{}", (end - start));
            return targetFileName;
        } catch (Exception e) {
            log.error("PDF工具-转换PDF-转换失败：", e);
        } finally {
            try {
                officeManager.stop();
            } catch (OfficeException e) {
                log.error("PDF工具-转换PDF-转换失败-释放资源失败：", e);
            }
        }
        return null;
    }

    public List<String> toPng(String fileName, int dpi) {
        return toImg(fileName, dpi, "png");
    }

    public List<String> toJpg(String fileName, int dpi) {
        return toImg(fileName, dpi, "jpg");
    }

    /**
     * PDF文件转图片
     *
     * @param fileName 文件名
     * @param dpi      dpi越大转换后越清晰，相对转换速度越慢 dpi为96,100,105,120,150,200中,105显示效果较为清晰,体积稳定,dpi越高图片体积越大,一般电脑显示分辨率为96
     * @param imgType  img类型 支持png jpg
     * @return {@link List }<{@link String }>
     */
    public List<String> toImg(String fileName, int dpi, String imgType) {
        if (StrUtil.isBlank(imgType)) {
            imgType = "jpg";
        }
        if (dpi == 0) {
            dpi = 150;
        }
        try {
            String filePath = workPath(fileName);
            log.debug("PDF工具-转换{}-源文件：{}", imgType, fileName);
            PDDocument pdDocument = PDDocument.load(new FileInputStream(filePath));
            /* dpi越大转换后越清晰，相对转换速度越慢 */
            int pages = pdDocument.getNumberOfPages();
            if (pages <= 0) {
                return List.of();
            }

            PDFRenderer renderer = new PDFRenderer(pdDocument);
            String prefix = FileUtil.getPrefix(fileName);
            List<String> pngList = new ArrayList<>();
            for (int i = 0; i < pages; i++) {
                long start = System.currentTimeMillis();
                String pngFileName = String.format("%s_%s.%s", prefix, i + 1, imgType);
                BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                ImageIO.write(image, imgType, new File(workPath(pngFileName)));
                pngList.add(pngFileName);
                long end = System.currentTimeMillis();
                log.debug("PDF工具-转换{}-第{}张：{};耗时：{}", imgType, i + 1, pngFileName, end - start);
            }
            log.debug("PDF工具-转换{}-转换结果：{}", imgType, pngList);

            return pngList;
        } catch (IOException e) {
            log.error("PDF工具-转换{}-转换失败", imgType, e);
        }
        return List.of();
    }

    /**
     * 工作目录
     *
     * @param fileName 文件名
     * @return {@link String }
     */
    public String workPath(String fileName) {
        return String.format("%s/%s", workPath(), fileName);
    }

    /**
     * 工作路径
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return {@link String }
     */
    public String workPath(String filePath, String fileName) {
        return String.format("%s/%s", filePath, fileName);
    }

    /**
     * 工作目录
     *
     * @return {@link String }
     */
    public String workPath() {
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            return String.format("C:%s", DEFAULT_PATH);
        }
        return DEFAULT_PATH;
    }

    /**
     * 递归删除文件
     *
     * @param directory 目录
     */
    public void removeDir(File directory) {
        if (directory == null) {
            return;
        }
        // 检查目录是否存在
        if (!directory.exists() || !directory.isDirectory()) {
            log.debug("指定的路径不是一个有效的目录:{}", directory.getName());
            return;
        }
        // 获取目录下的所有文件
        File[] files = directory.listFiles();

        // 检查是否有文件
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归删除子目录
                    removeDir(file);
                }
                if (file.isFile()) {
                    boolean deleted = file.delete();
                    log.debug("删除文件结果:{},文件:{}", deleted, file.getName());
                }
            }
        }

        // 删除当前目录
        boolean deleted = directory.delete();
        log.debug("删除文件夹结果:{},文件:{}", deleted, directory.getName());
    }
}
