package com.zhjs.saas.scheduler.config;

import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.JobScope;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.PlatformTransactionManager;

import com.zhjs.saas.core.util.Assert;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2018-06-11
 * @modified: 	2018-06-11
 * @version:
 */
public abstract class AbstractSchedulerConfig implements ImportAware
{

	private BatchConfigurer configurer;

	@Autowired
	private DataSource defaultDataSource;

	@Bean
	public JobBuilderFactory jobBuilders() throws Exception
	{
		return new JobBuilderFactory(jobRepository());
	}

	@Bean
	public StepBuilderFactory stepBuilders() throws Exception
	{
		return new StepBuilderFactory(jobRepository(), transactionManager());
	}

	@Bean
	public abstract JobRepository jobRepository() throws Exception;

	@Bean
	public abstract JobLauncher jobLauncher() throws Exception;

	@Bean
	public abstract JobExplorer jobExplorer() throws Exception;

	@Bean
	public JobRegistry jobRegistry() throws Exception
	{
		return new MapJobRegistry();
	}

	@Bean
	public abstract PlatformTransactionManager transactionManager() throws Exception;

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata)
	{
		AnnotationAttributes enabled = AnnotationAttributes
				.fromMap(importMetadata.getAnnotationAttributes(EnableBatchProcessing.class.getName(), false));
		Assert.notNull(enabled,
				"@EnableBatchProcessing is not present on importing class " + importMetadata.getClassName());
	}

	protected BatchConfigurer getConfigurer(Collection<BatchConfigurer> configurers) throws Exception
	{
		if (this.configurer != null)
		{
			return this.configurer;
		}
		if (configurers == null || configurers.isEmpty())
		{
			if (defaultDataSource == null)
			{
				throw new IllegalStateException("Data source can not be null!!!");
			}

			DefaultBatchConfigurer configurer = new DefaultBatchConfigurer(defaultDataSource);
			configurer.initialize();
			this.configurer = configurer;
			return configurer;
		}
		if (configurers.size() > 1)
		{
			throw new IllegalStateException(
					"To use a custom BatchConfigurer the context must contain precisely one, found "
							+ configurers.size());
		}
		this.configurer = configurers.iterator().next();
		return this.configurer;
	}

}

@Configuration
class ScopeConfiguration
{

	private StepScope stepScope = new StepScope();

	private JobScope jobScope = new JobScope();

	@Bean
	public StepScope stepScope()
	{
		stepScope.setAutoProxy(false);
		return stepScope;
	}

	@Bean
	public JobScope jobScope()
	{
		jobScope.setAutoProxy(false);
		return jobScope;
	}

}
