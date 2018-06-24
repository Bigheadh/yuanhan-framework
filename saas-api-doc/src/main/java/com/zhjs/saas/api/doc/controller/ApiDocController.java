package com.zhjs.saas.api.doc.controller;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponents;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.zhjs.saas.core.annotation.ApiMapping;
import com.zhjs.saas.core.annotation.RestComponent;
import com.zhjs.saas.core.util.WebUtil;
import com.zhjs.saas.core.web.AbstractController;

import io.swagger.models.Swagger;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

/**
 * 
 * @author: Jackie Wang
 * @since: 2018-03-05
 * @modified: 2018-03-05
 * @version:
 */
@RestComponent(namespace = "${saas.docs.path}")
@ApiIgnore
public class ApiDocController extends AbstractController
{

	@Value("${springfox.documentation.swagger.v2.host:DEFAULT}")
	private String hostNameOverride;
	private DocumentationCache documentationCache;
	private ServiceModelToSwagger2Mapper mapper;
	private JsonSerializer jsonSerializer;

	
	@ApiMapping("docs.api.list")
	public ResponseEntity<Json> getDocumentation(@RequestParam(value = "group", required = false) String swaggerGroup,
			HttpServletRequest servletRequest)
	{
		String groupName = Optional.fromNullable(swaggerGroup).or(Docket.DEFAULT_GROUP_NAME);
		Documentation documentation = documentationCache.documentationByGroup(groupName);
		if (documentation == null)
		{
			return new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
		}
		Swagger swagger = mapper.mapDocumentation(documentation);
		UriComponents uriComponents = WebUtil.componentsFrom(servletRequest, swagger.getBasePath());
		swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
		if (isNullOrEmpty(swagger.getHost()))
		{
			swagger.host(hostName(uriComponents));
		}
		return new ResponseEntity<Json>(jsonSerializer.toJson(swagger), HttpStatus.OK);
	}

	private String hostName(UriComponents uriComponents)
	{
		if ("DEFAULT".equals(hostNameOverride))
		{
			String host = uriComponents.getHost();
			int port = uriComponents.getPort();
			if (port > -1)
			{
				return String.format("%s:%d", host, port);
			}
			return host;
		}
		return hostNameOverride;
	}

}
