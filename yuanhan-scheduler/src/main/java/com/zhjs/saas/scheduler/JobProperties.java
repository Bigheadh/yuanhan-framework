package com.yuanhan.yuanhan.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-06-14
 * @modified:	2018-06-14
 * @version:	
 */
@Setter
@Getter
public class JobProperties
{
	/**
	 * 自定义异常处理类
	 * @return
	 */
	@JsonProperty("job_exception_handler")
	private String jobExceptionHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";
	
	/**
	 * 自定义业务处理线程池
	 * @return
	 */
	@JsonProperty("executor_service_handler")
	private String executorServiceHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";
	

}
