package com.example.stocktradingsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class WebConfig {

    @Value("${app.http.proxy.enabled:false}")
    private boolean proxyEnabled;

    @Value("${app.http.proxy.host:127.0.0.1}")
    private String proxyHost;

    @Value("${app.http.proxy.port:7890}")
    private int proxyPort;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        if (proxyEnabled) {
            factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)));
        }
        RestTemplate restTemplate = new RestTemplate(factory);
        // 东方财富会屏蔽 Java 默认的 User-Agent，设置浏览器 UA 绕过
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)");
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
