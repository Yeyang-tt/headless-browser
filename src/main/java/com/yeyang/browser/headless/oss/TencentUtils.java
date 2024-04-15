package com.yeyang.browser.headless.oss;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicSessionCredentials;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.SneakyThrows;

import java.io.File;

/**
 * 腾讯Utils
 *
 * @author Yeyang
 * @date 2024/04/15
 */
public class TencentUtils {

    public COSClient createCOSClient() {
        // 1 传入获取到的临时密钥 (tmpSecretId, tmpSecretKey, sessionToken)
        String tmpSecretId = "SECRETID";
        String tmpSecretKey = "SECRETKEY";
        String sessionToken = "TOKEN";
        BasicSessionCredentials cred = new BasicSessionCredentials(tmpSecretId, tmpSecretKey, sessionToken);
        // 2 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分
        Region region = new Region("COS_REGION"); // COS_REGION 参数：配置成存储桶 bucket 的实际地域，例如 ap-beijing，更多 COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端
        return new COSClient(cred, clientConfig);
    }

    @SneakyThrows
    public Bucket createBucket(COSClient cosClient) {
        String bucket = "examplebucket-1250000000"; // 存储桶名称，格式：BucketName-APPID
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
        // 设置 bucket 的权限为 Private(私有读写)、其他可选有 PublicRead（公有读私有写）、PublicReadWrite（公有读写）
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        return cosClient.createBucket(createBucketRequest);
    }

    public PutObjectResult uploadFile(COSClient cosClient, String localFilePath) {
        // 指定要上传的文件
        File localFile = new File(localFilePath);
        // 指定文件将要存放的存储桶
        String bucketName = "examplebucket-1250000000";
        // 指定文件上传到 COS 上的路径，即对象键。例如对象键为 folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
        String key = "folder/picture.jpg";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        return cosClient.putObject(putObjectRequest);
    }
}
