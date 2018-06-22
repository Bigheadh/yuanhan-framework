package com.yuanhan.yuanhan.core.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import com.yuanhan.yuanhan.core.dao.sql.SQLBuilder;
import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-22
 * @modified:	2017-05-22
 * @version:	
 */
@NoRepositoryBean
public interface CommonRepository<T extends BaseObject, ID extends Serializable> extends JpaRepository<T,ID>
{
	/**
	 * Saves a given entity and then evict it by hibernateTemplate.
	 * entity instance is clear without hibernate session.
	 * 
	 * @param entity without hibernate session
	 * @return the saved entity
	 */
	<S extends T> S save(S entity);

	/**
	 * Saves an entity and flushes changes instantly, and then evict it by hibernateTemplate.
	 * 
	 * @param entity without hibernate session
	 * @return the saved entity
	 */
	<S extends T> S saveAndFlush(S entity);
	
	/**
	 * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
	 * entity instance completely.
	 * 
	 * @param entity with hibernate session
	 * @return the saved entity
	 */
	<S extends T> S persist(S entity);

	/**
	 * Saves an entity and flushes changes instantly.
	 * 
	 * @param entity with hibernate session
	 * @return the saved entity
	 */
	<S extends T> S persistAndFlush(S entity);

	/**
	 * Retrieves an entity by its id and then evict it by hibernateTemplate.
	 * entity instance is clear without hibernate session.
	 * 
	 * @param id must not be {@literal null}.
	 * @return the entity without hibernate session or {@literal null} if none found
	 * @throws IllegalArgumentException if {@code id} is {@literal null}
	 */
	T get(ID id);
	
	public <E extends BaseObject> List<E> queryScript(SQLBuilder builder, Class<E> clazz, Object paramObject); 
	
	public JdbcTemplate getJdbcTemplate();
	
	public CommonNamedParameterJdbcTemplate getNamedJdbcTemplate();
	
	public <R extends CommonRepository<O,?>, O extends BaseObject> R commonDao(Class<O> type);
	
	public <R extends CommonRepository<O,KEY>, O extends BaseObject, KEY extends Serializable> R commonDao(Class<O> type, Class<KEY> keyType);
}
