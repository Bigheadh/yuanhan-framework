package com.yuanhan.yuanhan.core.dao;

import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author:		yuanhan
 * @since:		2017-10-18
 * @modified:	2017-10-18
 * @version:
 */
public class CustomizedPhysicalNamingStrategy implements PhysicalNamingStrategy
{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private final static char UNDERSCORE = '_';

	private final static String Schema = "Schema";
	private final static String Table = "Table";
	private final static String Column = "Column";
	private final static String Sequence = "Sequence";
	private final static String Catalog = "Catalog";

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment)
	{
		return apply(name, jdbcEnvironment, Catalog);
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment)
	{
		return apply(name, jdbcEnvironment, Schema);
	}

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment)
	{
		return apply(name, jdbcEnvironment, Table);
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment)
	{
		return apply(name, jdbcEnvironment, Sequence);
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment)
	{
		return apply(name, jdbcEnvironment, Column);
	}

	private Identifier apply(Identifier name, JdbcEnvironment jdbcEnvironment, String type)
	{
		if(name==null) return null;
		StringBuilder builder = new StringBuilder(name.getText().replace('.', UNDERSCORE));
		for(int i=1; i<=builder.length()-1; i++)
		{
			if (isUnderscoreRequired(builder.charAt(i-1), builder.charAt(i)))
			{
				builder.insert(i++, UNDERSCORE);
			}
		}
		
		logger.debug("Mapping entity '{}' to {} '{}'", name, type, builder.toString().toLowerCase());
		return getIdentifier(builder.toString(), name.isQuoted(), jdbcEnvironment);
	}

	/**
	 * Get an the identifier for the specified details. By default this method
	 * will return an identifier with the name adapted based on the result of
	 * {@link #isCaseInsensitive(JdbcEnvironment)}
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param quoted
	 *            if the identifier is quoted
	 * @param jdbcEnvironment
	 *            the JDBC environment
	 * @return an identifier instance
	 */
	protected Identifier getIdentifier(String name, boolean quoted, JdbcEnvironment jdbcEnvironment)
	{
		if (isCaseInsensitive(jdbcEnvironment))
		{
			name = name.toLowerCase(Locale.ROOT);
		}
		return new Identifier(name, quoted);
	}

	/**
	 * Specify whether the database is case sensitive.
	 * 
	 * @param jdbcEnvironment
	 *            the JDBC environment which can be used to determine case
	 * @return true if the database is case insensitive sensitivity
	 */
	protected boolean isCaseInsensitive(JdbcEnvironment jdbcEnvironment)
	{
		return true;
	}

	private boolean isUnderscoreRequired(char before, char current)
	{
		// aaaa
		// aAaa
		// aAAa
		// AAAa
		// Aaaa
		return (Character.isLowerCase(before) && Character.isUpperCase(current));
	}

	public static void main(String[] ars)
	{
		CustomizedPhysicalNamingStrategy cu = new CustomizedPhysicalNamingStrategy();
		Identifier id = new Identifier("AaSsSSsSS", false);
		id = cu.toPhysicalColumnName(id, null);
		System.out.println(id);
	}
	
}
