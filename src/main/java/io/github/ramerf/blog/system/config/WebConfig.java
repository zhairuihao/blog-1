package io.github.ramerf.blog.system.config;

import io.github.ramerf.blog.system.entity.Constant.ResourcePath;
import io.github.ramerf.blog.system.interceptor.OperateLogInterceptor;
import io.github.ramerf.blog.system.util.PathUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/** MVC配置, 配置视图解析器以及静态资源等 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler(ResourcePath.MANAGER + "/**")
        .addResourceLocations("file:" + PathUtil.getResourceBasePath());
    registry
        .addResourceHandler(ResourcePath.USER + "/**")
        .addResourceLocations("file:" + PathUtil.getResourceBasePath());
    registry
        .addResourceHandler(ResourcePath.DEFAULT + "/**")
        .addResourceLocations("file:" + PathUtil.getResourceBasePath());
    registry
        .addResourceHandler(ResourcePath.PUBLIC + "/**")
        .addResourceLocations("file:" + PathUtil.getResourceBasePath());
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new OperateLogInterceptor()).addPathPatterns("/manage/**");
  }
}
