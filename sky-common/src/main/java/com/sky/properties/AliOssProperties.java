package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {//配置属性类：读取配置文件->封装为java对象

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
