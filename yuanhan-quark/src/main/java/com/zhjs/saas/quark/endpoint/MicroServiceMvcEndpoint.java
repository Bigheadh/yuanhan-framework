package com.yuanhan.yuanhan.quark.endpoint;

import java.util.Map;

import org.springframework.boot.actuate.endpoint.mvc.ActuatorMediaTypes;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yuanhan.yuanhan.core.annotation.ApiMapping;


/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-12-08
 * @modified: 	2017-12-08
 * @version:
 */
@ConfigurationProperties(prefix = "endpoints.microservice")
public class MicroServiceMvcEndpoint extends EndpointMvcAdapter
{

	private final MicroServiceEndpoint delegate;

	public MicroServiceMvcEndpoint(MicroServiceEndpoint delegate)
	{
		super(delegate);
		this.delegate = delegate;
	}


	@ApiMapping(path="/{name:.*}", method=RequestMethod.GET,
			produces={ ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	@HypermediaDisabled
	public Object get(@PathVariable String name)
	{
		if (!this.delegate.isEnabled())
		{
			// Shouldn't happen - MVC endpoint shouldn't be registered when delegate's
			// disabled
			return getDisabledResponse();
		}
		Map<String, MicroServiceInfo> infos = this.delegate.invoke();
		return (infos == null ? ResponseEntity.notFound().build() : infos);
	}

	@ApiMapping(path="/{name:.*}", method=RequestMethod.POST,
			consumes = { ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,MediaType.APPLICATION_JSON_VALUE },
			produces={ ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	@HypermediaDisabled
	public Object set(@PathVariable String name, @RequestBody Map<String, String> configuration)
	{
		if (!this.delegate.isEnabled())
		{
			// Shouldn't happen - MVC endpoint shouldn't be registered when delegate's
			// disabled
			return getDisabledResponse();
		}
		return ResponseEntity.ok().build();
	}
}
