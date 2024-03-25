package com.yeyang.browser.headless.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


/**
 * 操作oss数据存取等的工具类
 *
 * @author Yeyang
 * @date 2024/03/25
 */
@UtilityClass
public class OssUtil {

    private OSS oss;

    /**
     * 默认的oss桶位置
     */
    public final String fixedBucket = "bucketName-123";

    static {
        oss = new OSSClientBuilder().build("endpoint", "accessKeyId", "accessKeySecret");
    }

    /**
     * 上传字符串
     *
     * @param objectName oss文件全路径名-默认是桶位置加全路径名，如桶位置为ossbucket，全路径名为upload/string/str.json，则数据会放到ossbucket下的upload/string包中，文件名为str.json
     * @param content    上传内容
     */
    public void uploadString(String objectName, String content) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, new ByteArrayInputStream(content.getBytes()));
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传字符串
     *
     * @param bucketName 文件存在oss的桶名称
     * @param objectName 文件在oss中的全路径
     * @param content    上传内容
     */
    public void uploadString(String bucketName, String objectName, String content) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传文件
     *
     * @param objectName 文件在oss中的全路径
     * @param filePath   上传文件的本地路径
     */
    public void uploadFile(String objectName, String filePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, new File(filePath));
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传文件
     *
     * @param bucketName 文件所在oss桶名称
     * @param objectName 文件在oss中的全路径
     * @param filePath   上传文件的本地路径
     */
    public void uploadFile(String bucketName, String objectName, String filePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File(filePath));
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传multi文件
     *
     * @param objectName    文件全路径
     * @param multipartFile 上传的文件
     */
    public void uploadMultipartFile(String objectName, MultipartFile multipartFile) {
        byte[] bytes = new byte[0];
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, inputStream);
        oss.putObject(putObjectRequest);
    }

    /**
     * multi方式上传文件
     *
     * @param bucketName    oss中桶名称
     * @param objectName    文件在oss中的全路径
     * @param multipartFile 上传的文件
     */
    public void uploadMultipartFile(String bucketName, String objectName, MultipartFile multipartFile) {
        byte[] bytes = new byte[0];
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传流数据
     *
     * @param objectName  数据在oss中的全路径
     * @param inputStream 流数据内容
     */
    public void uploadStream(String objectName, InputStream inputStream) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(fixedBucket, objectName, inputStream);
        oss.putObject(putObjectRequest);
    }

    /**
     * 上传流数据
     *
     * @param bucketName  oss桶名称
     * @param objectName  数据所在oss中的全路径
     * @param inputStream 流数据
     */
    public void uploadStream(String bucketName, String objectName, InputStream inputStream) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
        oss.putObject(putObjectRequest);
    }

    /**
     * 下载字符串数据
     *
     * @param objectName 数据所在oss的全路径
     * @return 字符串
     * @throws IOException
     */
    public String downloadString(String objectName) throws IOException {
        OSSObject ossObject = oss.getObject(fixedBucket, objectName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            } else {
                stringBuilder.append(line);
            }
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    /**
     * 下载字符串数据
     *
     * @param bucketName oss桶位置
     * @param objectName 数据所在oss中的全路径
     * @return 返回字符串
     * @throws IOException
     */
    public String downloadString(String bucketName, String objectName) throws IOException {
        OSSObject ossObject = oss.getObject(bucketName, objectName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null) {
                break;
            } else {
                stringBuilder.append(line);
            }
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    /**
     * 下载文件
     *
     * @param objectName 数据所在oss全路径
     * @param fileName   文件名
     */
    public void downloadFile(String objectName, String fileName) {
        oss.getObject(new GetObjectRequest(fixedBucket, objectName), new File(fileName));
    }

    /**
     * 下载文件
     *
     * @param bucketName 文件所在oss桶名称
     * @param objectName 文件所在oss中的全路径
     * @param fileName   文件名
     */
    public void downloadFile(String bucketName, String objectName, String fileName) {
        oss.getObject(new GetObjectRequest(bucketName, objectName), new File(fileName));
    }

    /**
     * 删除数据
     *
     * @param objectName 删除文件的全路径
     */
    public void delete(String objectName) {
        oss.deleteObject(fixedBucket, objectName);
    }

    /**
     * 删除数据
     *
     * @param bucketName 桶名称
     * @param objectName 删除文件的全路径
     */
    public void delete(String bucketName, String objectName) {
        oss.deleteObject(bucketName, objectName);
    }

    /**
     * 检查文件是否存在
     *
     * @param objectName 文件所在oss中的全路径
     * @return 返回是否
     */
    public boolean checkExist(String objectName) {
        boolean exist = oss.doesObjectExist(fixedBucket, objectName);
        return exist;
    }

    /**
     * 检查文件是否存在
     *
     * @param bucketName 桶名称
     * @param objectName 文件所在oss中的全路径
     * @return 返回是否
     */
    public boolean checkExist(String bucketName, String objectName) {
        boolean exist = oss.doesObjectExist(bucketName, objectName);
        return exist;
    }

    // 如果需要使用异步调用则在需要的方法上加上@Async注解即可
}

