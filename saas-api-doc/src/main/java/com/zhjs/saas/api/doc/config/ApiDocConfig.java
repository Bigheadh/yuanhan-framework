package com.zhjs.saas.api.doc.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.async.DeferredResult;

import com.zhjs.saas.api.doc.controller.ApiDocController;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2018-03-03
 * @modified: 	2018-03-03
 * @version:
 */
@Configuration
@EnableSwagger2
public class ApiDocConfig
{
	@Value("${saas.docs.platform-name}")
	private String title;
	@Value("${saas.docs.description}")
	private String description;
	@Value("${saas.docs.version}")
	private String version;
	@Value("${saas.docs.terms}")
	private String terms;
	@Value("${saas.docs.author}")
	private String author;
	@Value("${saas.docs.url}")
	private String url;
	@Value("${saas.docs.email}")
	private String email;
	@Value("${saas.docs.license}")
	private String license;
	@Value("${saas.docs.license-url}")
	private String licenseUrl;

	@Autowired
    private Environment env;

	@Bean
	public Docket ProductApi()
	{
		return new Docket(DocumentationType.SWAGGER_2).genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false).forCodeGeneration(false).pathMapping("/")
				.select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class)).build()
				.apiInfo(productApiInfo());
	}

	private ApiInfo productApiInfo()
	{
		ApiInfo apiInfo = new ApiInfo(title, description, version, terms, new Contact(author,url,email), license, licenseUrl,
									new ArrayList<VendorExtension>());
		return apiInfo;
	}
	
	
	public ApiDocController apiDocController()
	{
		return new ApiDocController();
	}

}
