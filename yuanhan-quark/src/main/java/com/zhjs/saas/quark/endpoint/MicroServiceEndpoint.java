package com.yuanhan.yuanhan.quark.endpoint;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import com.yuanhan.yuanhan.core.annotation.ApiMapping;
import com.yuanhan.yuanhan.core.annotation.support.ApiMappingHandlerMapping;
import com.yuanhan.yuanhan.core.util.StringUtil;


/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-12-07
 * @modified: 	2017-12-07
 * @version:
 */
@ConfigurationProperties(prefix="endpoints.microservice")
public class MicroServiceEndpoint extends AbstractEndpoint<Map<String,MicroServiceInfo>>
{
	
	private Map<String,MicroServiceInfo> serviceMapping = new ConcurrentHashMap<>();

	@Autowired
	private ApplicationContext applicationContext;

	public MicroServiceEndpoint()
	{
		super("microservice");
	}

	@Override
	public Map<String,MicroServiceInfo> invoke()
	{
		return serviceMapping;
	}

	@PostConstruct
	protected void initMappings()
	{
		if (applicationContext != null)
		{
			ApiMappingHandlerMapping apiMapping = applicationContext.getBean(ApiMappingHandlerMapping.class);
			Map<RequestMappingInfo,HandlerMethod> methods = apiMapping.getHandlerMethods();
			for (Entry<RequestMappingInfo,HandlerMethod> method : methods.entrySet())
			{
				HandlerMethod handler = method.getValue();
				ApiMapping api = handler.getMethodAnnotation(ApiMapping.class);
				if(api!=null)
				{
					MicroServiceInfo service = new MicroServiceInfo();
					service.setApplication(getEnvironment().getProperty("spring.application.name"));
					service.setService(api.value());
					service.setVersion(api.v());
					service.setIp(getEnvironment().getProperty("spring.cloud.client.ipAddress"));
					service.setPort(getEnvironment().getProperty("server.port", int.class, 8080));
					service.setBeanMethod(handler.toString());
					serviceMapping.put(StringUtil.isEmpty(api.v())?api.value():api.value()+"-"+api.v(), service);
				}
			}
		}
	}

}
