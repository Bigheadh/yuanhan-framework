package com.yuanhan.yuanhan.core.dao.generator;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.core.env.Environment;

import com.yuanhan.yuanhan.core.annotation.BeanComponent;
import com.yuanhan.yuanhan.core.config.PropertyConfig;
import com.yuanhan.yuanhan.core.util.ApplicationContextUtil;

/**
 * 
 * @author:		yuanhan
 * @since:		2018-02-23
 * @modified:	2018-02-23
 * @version:	
 */
@BeanComponent
@Transactional
public class IdGenerator implements IdentifierGenerator, Configurable
{
    @Resource
    private SessionFactory sessionFactory;
    public long workerId;
    public long dataCenterId;

    public SnowFlakeId snowFlakeIdWorker;

    
	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException
	{
        return snowFlakeIdWorker.nextId();
	}


	@Override
	public void configure(Type type, Properties props, ServiceRegistry serviceRegistry) throws MappingException
	{
		Environment env = ApplicationContextUtil.getApplicationContext().getEnvironment();
		dataCenterId = env.getProperty(PropertyConfig.DataCenter_ID, long.class, 0l);
		workerId = env.getProperty(PropertyConfig.Worker_ID, long.class, 0l);
		snowFlakeIdWorker = new SnowFlakeId(workerId, dataCenterId);
	}


	/**
	 * @return the workerId
	 */
	public long getWorkerId()
	{
		return workerId;
	}


	/**
	 * @return the dataCenterId
	 */
	public long getDataCenterId()
	{
		return dataCenterId;
	}

}
