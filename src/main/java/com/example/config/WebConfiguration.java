package com.example.config;

import com.example.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author Blockbuster
 * @date 2022/4/13 15:36:39 星期三
 */

@Slf4j
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始调用静态资源映射！");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展 mvc 框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层用 Jackson 将 java 对象转换为JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器加入到 MVC 框架的转换器集合 List<HttpMessageConverter<?>> 中
        converters.add(0,messageConverter);
    }
}
