package com.yuanhan.yuanhan.core.config;

import static com.yuanhan.yuanhan.core.config.PropertyConfig.Jpa_DefaultConfig_Enable;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.yuanhan.yuanhan.core.dao.CommonRepositoryFactoryBean;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-05-21
 * @modified:	2018-05-21
 * @version:	
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableJpaRepositories(repositoryFactoryBeanClass=CommonRepositoryFactoryBean.class, basePackages="${yuanhan.jpa.repository.package:com}")
@Import({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnProperty(name=Jpa_DefaultConfig_Enable, havingValue="true", matchIfMissing=true)
public class JpaDefaultConfiguration
{
	
	@Bean
	 public SessionFactory sessionFactory(@Qualifier("entityManagerFactory") EntityManagerFactory emf) {
		/*MutableIdentifierGeneratorFactory idGenFactory =  context.getBean(MutableIdentifierGeneratorFactory.class);
		idGenFactory.register(GenerationType.SnowFlake, IdGenerator.class);*/
	     return emf.unwrap(SessionFactory.class);
	 }

}
