package com.zhjs.saas.core.dao.generator;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-02-23
 * @modified:	2018-02-23
 * @version:	
 */
public abstract class GenerationType
{

    /**
     * Indicates that the persistence provider must assign
     * primary keys for the entity using an underlying
     * database table to ensure uniqueness.
     */
	public static final String TABLE = "TABLE";

    /**
     * Indicates that the persistence provider must assign
     * primary keys for the entity using a database sequence.
     */
	public static final String SEQUENCE = "SEQUENCE";

    /**
     * Indicates that the persistence provider must assign
     * primary keys for the entity using a database identity column.
     */
	public static final String IDENTITY = "IDENTITY";

    /**
     * Indicates that the persistence provider should pick an
     * appropriate strategy for the particular database. The
     * <code>AUTO</code> generation strategy may expect a database
     * resource to exist, or it may attempt to create one. A vendor
     * may provide documentation on how to create such resources
     * in the event that it does not support schema generation
     * or cannot create the schema resource at runtime.
     */
	public static final String AUTO = "AUTO";
    
    /**
     * SnowFlakeId
     */
	public static final String SnowFlake = "SnowFlake";
}
