package org.cbioportal.web.config;

import org.cbioportal.model.ClinicalDataCount;
import org.cbioportal.web.config.annotation.InternalApi;
import org.cbioportal.web.config.annotation.PublicApi;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;

import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.classmate.TypeResolver;

@Configuration
@EnableSwagger2
@PropertySource("classpath:springfox.properties")
public class SwaggerConfig {

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(PublicApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo());
    }

    @Bean
    public Docket internalApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("internal")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(InternalApi.class))
            .build()
            .useDefaultResponseMessages(false)
            .protocols(new HashSet<>(Arrays.asList("http", "https")))
            .apiInfo(apiInfo())
            .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Map.class, String.class,
            typeResolver.resolve(Map.class, String.class, typeResolver.resolve(List.class, ClinicalDataCount.class))), 
            typeResolver.resolve(Map.class, String.class, WildcardType.class), Ordered.HIGHEST_PRECEDENCE))
            .additionalModels(typeResolver.resolve(ClinicalDataCount.class));
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .displayRequestDuration(true)
            .validatorUrl("")
            .build();
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
            "cBioPortal web API [Beta]",
            "A web service for supplying JSON formatted data to cBioPortal clients. " +
                "Please note that this API is currently in beta and subject to change.",
            "1.0 (beta)",
            null,
            new Contact("cBioPortal", "http://www.cbioportal.org", "cbioportal@googlegroups.com"),
            "License",
            "https://github.com/cBioPortal/cbioportal/blob/master/LICENSE", Collections.emptyList());
        return apiInfo;
    }
}
