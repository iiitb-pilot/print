package io.mosip.print.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
@EnableSwagger2
@ConfigurationProperties("mosip.api")
@ConditionalOnProperty(name="mosip.api.swagger.enable", havingValue = "true", matchIfMissing = false)
public class PrintSwaggerConfig {
    private String version;
    private String title;
    private String description;
    private String basePackage;
    private String contactName;
    private String contactEmail;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2) {
        }
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.mosip.print.controller"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(true);
//                .globalResponseMessage(RequestMethod.POST, Arrays.asList(
//                        new ResponseMessageBuilder().code(500)
//                                .message("500 message").build(),
//                        new ResponseMessageBuilder().code(403)
//                                .message("Forbidden!!!!!").build(),
//                        new ResponseMessageBuilder().code(401)
//                                .message("Auth Failed").build()
//
//                ));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact(contactName, null, contactEmail))
                .build();
    }
}