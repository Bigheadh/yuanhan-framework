package com.yuanhan.yuanhan.scheduler.config;

import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-06-11
 * @modified:	2018-06-11
 * @version:	
 */
public class SimpleSchedulerConfig extends AbstractSchedulerConfig
{

	/* (non-Javadoc)
	 * @see com.yuanhan.yuanhan.scheduler.config.AbstractSchedulerConfig#jobRepository()
	 */
	@Override
	public JobRepository jobRepository() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yuanhan.yuanhan.scheduler.config.AbstractSchedulerConfig#jobLauncher()
	 */
	@Override
	public JobLauncher jobLauncher() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yuanhan.yuanhan.scheduler.config.AbstractSchedulerConfig#jobExplorer()
	 */
	@Override
	public JobExplorer jobExplorer() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yuanhan.yuanhan.scheduler.config.AbstractSchedulerConfig#transactionManager()
	 */
	@Override
	public PlatformTransactionManager transactionManager() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
