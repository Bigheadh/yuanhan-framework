package com.yuanhan.yuanhan.scheduler.reader;

import java.util.List;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2018-06-11
 * @modified: 	2018-06-11
 * @version:
 */
public abstract class BaseItemReader<T> implements ItemReader<T>
{
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution)
	{
		this.stepExecution = stepExecution;
	}
	

	/**
	 * Reads a piece of input data and advance to the next one. Implementations
	 * <strong>must</strong> return <code>null</code> at the end of the input
	 * data set. In a transactional setting, caller might get the same item
	 * twice from successive calls (or otherwise), if the first call was in a
	 * transaction that rolled back.
	 * 
	 * @throws ParseException if there is a problem parsing the current record
	 * (but the next one may still be valid)
	 * @throws NonTransientResourceException if there is a fatal exception in
	 * the underlying resource. After throwing this exception implementations
	 * should endeavour to return null from subsequent calls to read.
	 * @throws UnexpectedInputException if there is an uncategorised problem
	 * with the input data. Assume potentially transient, so subsequent calls to
	 * read might succeed.
	 * @throws Exception if an there is a non-specific error.
	 * @return T the item to be processed
	 */
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
	{
		JobParameters params = stepExecution.getJobParameters();
		ExecutionContext stepContext = stepExecution.getExecutionContext();
		
		return doRead(params, stepContext);
	}
	
	public abstract T doRead(JobParameters params, ExecutionContext stepContext) 
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;
	

	/**
	 * STEP参数保存
	 * 
	 * @param exitStatus
	 */
	public void saveStepParameter(String key, String Value)
	{
		this.stepExecution.getJobExecution().getExecutionContext().putString(key, Value);
	}

	/**
	 * STEP参数保存
	 * 
	 * @param exitStatus
	 */
	public void saveStepParameter(String key, long Value)
	{
		this.stepExecution.getJobExecution().getExecutionContext().putLong(key, Value);
	}

	/**
	 * STEP参数保存
	 * 
	 * @param exitStatus
	 */
	public void saveStepParameter(String key, int Value)
	{
		this.stepExecution.getJobExecution().getExecutionContext().putInt(key, Value);
	}

	/**
	 * STEP参数保存
	 * 
	 * @param exitStatus
	 */
	public void saveStepParameter(String key, List<Object> value)
	{
		this.stepExecution.getJobExecution().getExecutionContext().put(key, value);
	}

	/**
	 * STEP参数保存
	 * 
	 * @param exitStatus
	 */
	public void saveStepParameter(String key, Object value)
	{
		this.stepExecution.getJobExecution().getExecutionContext().put(key, value);
	}

	/**
	 * STEP参数取得
	 * 
	 * @param exitStatus
	 */
	public Object getStepParameter(String key)
	{
		return this.stepExecution.getJobExecution().getExecutionContext().get(key);
	}

	/**
	 * STEP参数取得
	 * 
	 * @param exitStatus
	 */
	public String getStringStepParameter(String key)
	{
		return this.stepExecution.getJobExecution().getExecutionContext().getString(key);
	}

	/**
	 * STEP参数取得
	 * 
	 * @param exitStatus
	 */
	public long getLongStepParameter(String key)
	{
		return this.stepExecution.getJobExecution().getExecutionContext().getLong(key);
	}

	/**
	 * STEP参数取得
	 * 
	 * @param exitStatus
	 */
	public int getIntStepParameter(String key)
	{
		return this.stepExecution.getJobExecution().getExecutionContext().getInt(key);
	}

	/**
	 * STEP参数递增(+1)
	 * 
	 * @param exitStatus
	 */
	public void addLongStepParameter(String key)
	{
		this.addLongStepParameter(key, 1);
	}

	/**
	 * STEP参数递增
	 * 
	 * @param exitStatus
	 */
	public void addLongStepParameter(String key, long cnt)
	{
		long value = this.getLongStepParameter(key);
		value = value + cnt;
		this.saveStepParameter(key, value);
	}

	/**
	 * STEP参数递增(+1)
	 * 
	 * @param exitStatus
	 */
	public void addIntStepParameter(String key)
	{
		this.addIntStepParameter(key, 1);
	}

	/**
	 * STEP参数递增
	 * 
	 * @param exitStatus
	 */
	public void addIntStepParameter(String key, int cnt)
	{
		int value = this.getIntStepParameter(key);
		value = value + cnt;
		this.saveStepParameter(key, value);
	}
}
