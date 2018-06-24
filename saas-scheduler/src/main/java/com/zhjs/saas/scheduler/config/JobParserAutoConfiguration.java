package com.zhjs.saas.scheduler.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2018-06-13
 * @modified: 	2018-06-13
 * @version:
 */
@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
public class JobParserAutoConfiguration
{

	@Autowired
	private ZookeeperProperties zookeeperProperties;

	/**
	 * 初始化Zookeeper注册中心
	 * 
	 * @param config
	 * @return
	 */
	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter zookeeperRegistryCenter()
	{
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(zookeeperProperties.getServerLists(),
				zookeeperProperties.getNamespace());
		zkConfig.setBaseSleepTimeMilliseconds(zookeeperProperties.getBaseSleepTimeMilliseconds());
		zkConfig.setConnectionTimeoutMilliseconds(zookeeperProperties.getConnectionTimeoutMilliseconds());
		zkConfig.setDigest(zookeeperProperties.getDigest());
		zkConfig.setMaxRetries(zookeeperProperties.getMaxRetries());
		zkConfig.setMaxSleepTimeMilliseconds(zookeeperProperties.getMaxSleepTimeMilliseconds());
		zkConfig.setSessionTimeoutMilliseconds(zookeeperProperties.getSessionTimeoutMilliseconds());
		return new ZookeeperRegistryCenter(zkConfig);
	}

	@Bean
	public JobConfParser jobConfParser()
	{
		return new JobConfParser();
	}

}
