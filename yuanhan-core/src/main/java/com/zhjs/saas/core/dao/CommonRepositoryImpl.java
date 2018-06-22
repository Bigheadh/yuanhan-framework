package com.yuanhan.yuanhan.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.yuanhan.yuanhan.core.dao.sql.SQLBuilder;
import com.yuanhan.yuanhan.core.pojo.BaseObject;
import com.yuanhan.yuanhan.core.util.AopUtil;
import com.yuanhan.yuanhan.core.util.Assert;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-05
 * @modified:	2017-10-05
 * @version:	
 */
public class CommonRepositoryImpl<T extends BaseObject, ID extends Serializable>
		extends SimpleJpaRepository<T, ID> implements CommonRepository<T, ID>
{

	private JdbcTemplate jdbcTemplate;
	private CommonNamedParameterJdbcTemplate namedJdbcTemplate;
	private HibernateTemplate hibernateTemplate;
	private EntityManager em;
	private BeanFactory beanFactory;

	protected void initJdbcTemplate()
	{
		DataSource ds = beanFactory.getBean(DataSource.class);
		Assert.notNull(ds, "DataSource must not be null");
		this.jdbcTemplate = new JdbcTemplate(ds);
		this.namedJdbcTemplate = new CommonNamedParameterJdbcTemplate(this.jdbcTemplate);
	}
	
	/**
	 * Create a HibernateTemplate for the given SessionFactory.
	 * Only invoked if populating the DAO with a SessionFactory reference!
	 * <p>Can be overridden in subclasses to provide a HibernateTemplate instance
	 * with different configuration, or a custom HibernateTemplate subclass.
	 * @param sessionFactory the Hibernate SessionFactory to create a HibernateTemplate for
	 * @return the new HibernateTemplate instance
	 * @see #setSessionFactory
	 */
	protected void initHibernateTemplate() {
		this.hibernateTemplate = new HibernateTemplate(beanFactory.getBean(SessionFactory.class));
	}
	
	/**
	 * Return the HibernateTemplate for this DAO,
	 * pre-initialized with the sessionFactory or set explicitly.
	 */
	public HibernateTemplate getHibernateTemplate() {
	  return this.hibernateTemplate;
	}
	
	/**
	 * Return the JdbcTemplate for this DAO,
	 * pre-initialized with the DataSource or set explicitly.
	 */
	public JdbcTemplate getJdbcTemplate() {
	  return this.jdbcTemplate;
	}

	/**
	 * @return the namedJdbcTemplate
	 */
	public CommonNamedParameterJdbcTemplate getNamedJdbcTemplate() {
		return namedJdbcTemplate;
	}

	/**
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
	 * 
	 * @param entityInformation must not be {@literal null}.
	 * @param entityManager must not be {@literal null}.
	 */
	public CommonRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager, BeanFactory beanFactory) {
		super(entityInformation, entityManager);
		this.em = entityManager;
		this.beanFactory = beanFactory;
		initJdbcTemplate();
		initHibernateTemplate();
	}

	/**
	 * Creates a new {@link CommonRepositoryImpl} to manage objects of the given domain type.
	 * 
	 * @param domainClass must not be {@literal null}.
	 * @param em must not be {@literal null}.
	 */
	public CommonRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager), entityManager);
		this.em = entityManager;
		initJdbcTemplate();
	}

	public T get(ID id) {
		T t = super.findOne(id);
		//this.getHibernateTemplate().getSessionFactory().getCurrentSession().evict(t);
		if(t!=null)
			this.em.detach(t);
		return t;
	}

	@Transactional
	public <S extends T> S persistAndFlush(S entity) {
		entity = super.saveAndFlush(entity);
		return entity;
	}

	@Transactional
	public <S extends T> S persist(S entity) {
		entity = super.save(entity);
		return entity;
	}

	@Override
	@Transactional
	public <S extends T> S save(S entity) {
		entity = super.save(entity);
//		if(entity!=null)
//			this.em.detach(entity);
		return entity;
	}

	@Override
	@Transactional
	public <S extends T> S saveAndFlush(S entity) {
		entity = super.saveAndFlush(entity);
		//this.getHibernateTemplate().evict(entity);
		if(entity!=null)
			this.em.detach(entity);
		return entity;
	}

	@Override
	public <E extends BaseObject> List<E> queryScript(SQLBuilder builder, Class<E> classType, Object paramObject)
	{
		return this.getNamedJdbcTemplate().query(builder.toSQL(), new BeanPropertySqlParameterSource(paramObject), new BeanPropertyRowMapper<E>(classType));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <R extends CommonRepository<O,KEY>, O extends BaseObject, KEY extends Serializable> R commonDao(Class<O> type, Class<KEY> keyType)
	{
		R r = null;
		Map<String,CommonRepository> beans = ((ListableBeanFactory)beanFactory).getBeansOfType(CommonRepository.class);
		for(Entry<String,CommonRepository> entry : beans.entrySet())
		{
			AdvisedSupport advised = AopUtil.getAdvised(entry.getValue());
			Type t = ((ParameterizedType)advised.getProxiedInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
			if(type.equals(t))
				r = (R)entry.getValue();
		}
		Assert.notNull(r, "Can't find any repository interface class for "+type.getName());
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public <R extends CommonRepository<O,?>, O extends BaseObject> R commonDao(Class<O> type)
	{
		Class<?> keyClass = String.class;
		Field[] fields = type.getDeclaredFields();
		for(Field field : fields)
		{
			if(field.isAnnotationPresent(Id.class))
			{
				keyClass = field.getType();
				break;
			}
		}
		R r = (R)commonDao(type, (Class<Serializable>)keyClass);
		Assert.notNull(r, "Can't find any repository interface class for "+type.getName());
		return r;
	}

}
