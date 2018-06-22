package com.yuanhan.yuanhan.core.dao;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

import com.yuanhan.yuanhan.core.logger.Logger;
import com.yuanhan.yuanhan.core.logger.LoggerFactory;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-18
 * @modified:	2017-10-18
 * @version:	
 */
public class CustomizeImplicitNamingStrategy extends SpringImplicitNamingStrategy
{
	private static final long serialVersionUID = 1L;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
		// JPA states we should use the following as default:
		//     "The property or field name"
		// aka:
		//     The unqualified attribute path.
		Identifier name = toIdentifier( transformAttributePath( source.getAttributePath() ), source.getBuildingContext() );
		logger.debug("ImplicitNamingStrategy / BasicColumnName -> \n\t" + name);
		return name;
	}

}
