package com.yuanhan.yuanhan.scheduler.processor;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2018-06-11
 * @modified: 	2018-06-11
 * @version:
 */
public abstract class BaseProcessor<I, O> implements ItemProcessor<I, O>
{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution)
	{
		this.stepExecution = stepExecution;
	}

	@Override
	final public O process(I input) throws Exception
	{
		JobParameters params = stepExecution.getJobParameters();
		ExecutionContext stepContext = stepExecution.getExecutionContext();

		return doProcess(input, params, stepContext);
	}

	public abstract O doProcess(I input, JobParameters params, ExecutionContext stepContext) throws Exception;
}
