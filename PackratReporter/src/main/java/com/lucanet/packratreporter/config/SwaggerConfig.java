package com.lucanet.packratreporter.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket getDocket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(Predicates.not(PathSelectors.regex("/error")))
        .build()
        .apiInfo(getApiInfo())
        .useDefaultResponseMessages(false)
        .globalResponseMessage(RequestMethod.GET,
            Collections.singletonList(
                new ResponseMessageBuilder()
                    .code(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .message("An internal error occurred")
                    .build()
            ));
  }

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder()
        .title("Packrat REST API")
        .description("REST API documentation for the Packrat Collector")
        .contact(new Contact("Severn Everett", "https://www.lucanet.com", "severne@lucanet.com"))
        .license("Apache License Version 2.0")
        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
        .build();
  }
}
