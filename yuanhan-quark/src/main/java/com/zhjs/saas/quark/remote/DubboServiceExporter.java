package com.yuanhan.yuanhan.quark.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.support.RemoteExporter;

import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.ServiceBean;
import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-11-08
 * @modified: 	2017-11-08
 * @version:
 */
public class DubboServiceExporter extends RemoteExporter implements InitializingBean, DisposableBean, ApplicationContextAware
{

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final Set<ServiceConfig<?>> serviceConfigs = new ConcurrentHashSet<ServiceConfig<?>>();

    private ApplicationContext context;    
    private Service[] services;

	public void destroy() throws Exception
	{
		for (ServiceConfig<?> serviceConfig : serviceConfigs)
		{
			try
			{
				serviceConfig.unexport();
			} 
			catch (Throwable e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		for(Service service : services)
		{			
			ServiceBean<Object> serviceConfig = new ServiceBean<Object>();
			serviceConfig.setApplicationContext(context);
			if (service.registry()!=null && service.registry().length>0) {
				List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
				for (String registryId : service.registry()) {
					if (registryId!=null && registryId.length()>0) {
						registryConfigs.add((RegistryConfig)context.getBean(registryId, RegistryConfig.class));
					}
				}
				serviceConfig.setRegistries(registryConfigs);
			}
			if (service.provider()!=null && service.provider().length()>0) {
				serviceConfig.setProvider((ProviderConfig)context.getBean(service.provider(),ProviderConfig.class));
			}
			if (service.monitor()!=null && service.monitor().length()>0) {
				serviceConfig.setMonitor((MonitorConfig)context.getBean(service.monitor(), MonitorConfig.class));
			}
			if (service.application()!=null && service.application().length()>0) {
				serviceConfig.setApplication((ApplicationConfig)context.getBean(service.application(), ApplicationConfig.class));
			}
			if (service.module()!=null && service.module().length()>0) {
				serviceConfig.setModule((ModuleConfig)context.getBean(service.module(), ModuleConfig.class));
			}
			if (service.protocol()!=null && service.protocol().length>0) {
				List<ProtocolConfig> protocolConfigs = new ArrayList<ProtocolConfig>();
				for (String protocolId : service.protocol()) {
					if (protocolId!=null && protocolId.length()>0) {
						protocolConfigs.add((ProtocolConfig)context.getBean(protocolId, ProtocolConfig.class));
					}
				}
				serviceConfig.setProtocols(protocolConfigs);
			}
			try {
				serviceConfig.afterPropertiesSet();
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
			serviceConfig.setInterface(getServiceInterface());
			serviceConfig.setRef(getService());
			serviceConfigs.add(serviceConfig);
			serviceConfig.export();
		}
	}

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

	/**
	 * @return the services
	 */
	public Service[] getServices()
	{
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(Service[] services)
	{
		this.services = services;
	}

}
