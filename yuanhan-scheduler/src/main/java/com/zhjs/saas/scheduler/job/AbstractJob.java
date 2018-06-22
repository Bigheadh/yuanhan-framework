package com.yuanhan.yuanhan.scheduler.job;


import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

import com.yuanhan.yuanhan.core.exception.BaseException;
import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-06-14
 * @modified:	2018-06-14
 * @version:	
 */
public abstract class AbstractJob implements SimpleJob
{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(ShardingContext shardingContext)
	{
		logger.info("作业 {}[{}] 启动执行...", shardingContext.getJobName(), shardingContext.getTaskId());
		try
		{
			doExecute(shardingContext);
		}
		catch (BaseException e)
		{
			logger.error("作业 {}[{}] 执行时发生异常！", shardingContext.getJobName(), shardingContext.getTaskId());
			throw new RuntimeException(e);
		}
	}

	public abstract <T> T doExecute(ShardingContext shardingContext) throws BaseException;
}
