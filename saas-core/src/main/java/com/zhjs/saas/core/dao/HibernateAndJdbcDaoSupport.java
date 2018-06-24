package com.zhjs.saas.core.dao;


import java.sql.Connection;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.orm.hibernate5.HibernateTemplate;

/**
 * this class extends from DaoSupport to mix both hibernate and jdbc support together
 * you only need to call getHibernateTemplate() or getJdbcTemplate(), then you can code
 * with your choice.
 * 
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public abstract class HibernateAndJdbcDaoSupport extends DaoSupport{
	
	protected boolean requireSessionFactory = true;
	
	public void setRequireSessionFactory(boolean requireSessionFactory) {
		this.requireSessionFactory = requireSessionFactory;
	}

	private SessionFactory sessionFactory;

	private DataSource dataSource;
	
	public void initDataSourceAndSessionFactory()
	{
		if(requireSessionFactory)
			setSessionFactory(sessionFactory);
		setDataSource(dataSource);
		checkDaoConfigAfterSetting();
	}
	
	public void initSessionFactory()
	{
		setSessionFactory(sessionFactory);
	}
	
	public void initDataSource()
	{
		setDataSource(dataSource);
		checkDaoConfigAfterSetting();
	}

	protected Logger logger = LoggerFactory.getLogger(getClass());	

	protected final void checkDaoConfig() {}

	protected final void checkDaoConfigAfterSetting() {
		if (requireSessionFactory && this.hibernateTemplate == null) {
			throw new IllegalArgumentException("'sessionFactory' or 'hibernateTemplate' is required");
		}
		if (this.jdbcTemplate == null) {
			throw new IllegalArgumentException("'dataSource' or 'jdbcTemplate' is required");
		}
		if (this.namedJdbcTemplate == null) {
			throw new IllegalArgumentException("'dataSource' or 'namedJdbcTemplate' is required");
		}
	}


	/**
	 *  Spring Hibernate support
	 */
	private HibernateTemplate hibernateTemplate;


	/**
	 * Set the Hibernate SessionFactory to be used by this DAO.
	 * Will automatically create a HibernateTemplate for the given SessionFactory.
	 * @see #createHibernateTemplate
	 * @see #setHibernateTemplate
	 */
	public final void setSessionFactory(SessionFactory sessionFactory) {
		if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
			this.hibernateTemplate = createHibernateTemplate(sessionFactory);
		}
	}

	/**
	 * Return the Hibernate SessionFactory used by this DAO.
	 */
	public final SessionFactory getSessionFactory() {
		return (this.hibernateTemplate != null ? this.hibernateTemplate.getSessionFactory() : null);
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
	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		return new HibernateTemplate(sessionFactory);
	}

	/**
	 * Set the HibernateTemplate for this DAO explicitly,
	 * as an alternative to specifying a SessionFactory.
	 * @see #setSessionFactory
	 */
	public final void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * Return the HibernateTemplate for this DAO,
	 * pre-initialized with the SessionFactory or set explicitly.
	 * <p><b>Note: The returned HibernateTemplate is a shared instance.</b>
	 * You may introspect its configuration, but not modify the configuration
	 * (other than from within an {@link #initDao} implementation).
	 * Consider creating a custom HibernateTemplate instance via
	 * <code>new HibernateTemplate(getSessionFactory())</code>, in which
	 * case you're allowed to customize the settings on the resulting instance.
	 */
	public final HibernateTemplate getHibernateTemplate() {
	  return this.hibernateTemplate;
	}	


	/**
	 * Conveniently obtain the current Hibernate Session.
	 * @return the Hibernate Session
	 * @throws DataAccessResourceFailureException if the Session couldn't be created
	 * @see SessionFactory#getCurrentSession()
	 */
	protected final Session currentSession() throws DataAccessResourceFailureException {
		return getSessionFactory().getCurrentSession();
	}
	
	
	/**
	 *  Spring JDBC support
	 */
	private JdbcTemplate jdbcTemplate;


	/**
	 * Set the JDBC DataSource to be used by this DAO.
	 */
	public final void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null || dataSource != this.jdbcTemplate.getDataSource()) {
			this.jdbcTemplate = createJdbcTemplate(dataSource);
			initTemplateConfig();
		}
		if (this.namedJdbcTemplate == null) {
			this.namedJdbcTemplate = createNamedJdbcTemplate(dataSource);
			initNamedTemplateConfig();
		}
	}

	/**
	 * Return the JDBC DataSource used by this DAO.
	 */
	public final DataSource getDataSource() {
		return (this.jdbcTemplate != null ? this.jdbcTemplate.getDataSource() : null);
	}

	/**
	 * Create a JdbcTemplate for the given DataSource.
	 * Only invoked if populating the DAO with a DataSource reference!
	 * <p>Can be overridden in subclasses to provide a JdbcTemplate instance
	 * with different configuration, or a custom JdbcTemplate subclass.
	 * @param dataSource the JDBC DataSource to create a JdbcTemplate for
	 * @return the new JdbcTemplate instance
	 * @see #setDataSource
	 */
	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * Set the JdbcTemplate for this DAO explicitly,
	 * as an alternative to specifying a DataSource.
	 */
	public final void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		initTemplateConfig();
	}

	/**
	 * Return the JdbcTemplate for this DAO,
	 * pre-initialized with the DataSource or set explicitly.
	 */
	public final JdbcTemplate getJdbcTemplate() {
	  return this.jdbcTemplate;
	}

	/**
	 * Initialize the template-based configuration of this DAO.
	 * Called after a new JdbcTemplate has been set, either directly
	 * or through a DataSource.
	 * <p>This implementation is empty. Subclasses may override this
	 * to configure further objects based on the JdbcTemplate.
	 * @see #getJdbcTemplate()
	 */
	protected void initTemplateConfig() {
	}

	/**
	 * Return the SQLExceptionTranslator of this DAO's JdbcTemplate,
	 * for translating SQLExceptions in custom JDBC access code.
	 * @see org.springframework.jdbc.core.JdbcTemplate#getExceptionTranslator()
	 */
	protected final SQLExceptionTranslator getExceptionTranslator() {
		return getJdbcTemplate().getExceptionTranslator();
	}

	/**
	 * Get a JDBC Connection, either from the current transaction or a new one.
	 * @return the JDBC Connection
	 * @throws CannotGetJdbcConnectionException if the attempt to get a Connection failed
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#getConnection(javax.sql.DataSource)
	 */
	protected final Connection getConnection() throws CannotGetJdbcConnectionException {
		return DataSourceUtils.getConnection(getDataSource());
	}

	/**
	 * Close the given JDBC Connection, created via this DAO's DataSource,
	 * if it isn't bound to the thread.
	 * @param con Connection to close
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#releaseConnection
	 */
	protected final void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, getDataSource());
	}
	
	
	
	private CommonNamedParameterJdbcTemplate namedJdbcTemplate;

	/**
	 * @return the namedJdbcTemplate
	 */
	public final CommonNamedParameterJdbcTemplate getNamedJdbcTemplate() {
		return namedJdbcTemplate;
	}

	/**
	 * @param namedJdbcTemplate the namedJdbcTemplate to set
	 */
	public final void setNamedJdbcTemplate(CommonNamedParameterJdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = namedJdbcTemplate;
	}

	/**
	 * Create a NamedParameterJdbcTemplate for the given DataSource.
	 * Only invoked if populating the DAO with a DataSource reference!
	 * <p>Can be overridden in subclasses to provide a JdbcTemplate instance
	 * with different configuration, or a custom JdbcTemplate subclass.
	 * @param dataSource the JDBC DataSource to create a JdbcTemplate in
	 * @return the new NamedParameterJdbcTemplate instance
	 * @see #setDataSource
	 */
	protected CommonNamedParameterJdbcTemplate createNamedJdbcTemplate(DataSource dataSource) {
		return new CommonNamedParameterJdbcTemplate(dataSource);
	}

	protected void initNamedTemplateConfig() {
	}


}
