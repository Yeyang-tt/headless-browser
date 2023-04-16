### Headless Browser(无头浏览器) PDF文件下载工具
#### 参考资料：[jvppeteer](https://github.com/fanyong920/jvppeteer)
####
___
### 1.Maven依赖
#### 无头浏览器依赖
```xml
<dependency>
    <groupId>io.github.fanyong920</groupId>
    <artifactId>jvppeteer</artifactId>
    <version>1.1.5</version>
</dependency>
```
### 2.Docker镜像
#### 1.使用./docker/Dockerfile 构建镜像
```shell
docker build -t java8-chrome -f ./docker/Dockerfile .
```
#### 2.可以基于 java8-chrome 构建新的容器
```dockerfile
FROM java8-chrome
ENV TZ=Asia/Shanghai
WORKDIR /opt/project
ADD app.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "app.jar"]
```