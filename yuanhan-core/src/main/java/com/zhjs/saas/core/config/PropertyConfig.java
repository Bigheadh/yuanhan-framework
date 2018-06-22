package com.yuanhan.yuanhan.core.config;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public abstract class PropertyConfig
{
	/**
	 * properties holder bean name
	 */
	public final static String PropertyBeanName = "propertiesHolder";

	
	
	public final static String DataCenter_ID = "yuanhan.dc.id";
	
	public final static String Worker_ID = "yuanhan.worker.id";
	

	public final static String Auto_CommonConfig_Enable = "yuanhan.auto-common.enable";
	public final static String Jpa_DefaultConfig_Enable = "yuanhan.jpa.default-config.enable";
	
	public final static String Logger_Factory = "yuanhan.logger.factory";
	
	public final static String Exception_Global_Trace = "yuanhan.exception.trace.global";
	public final static String Exception_Trace_Package = "yuanhan.exception.trace.packages";

	public final static String SessionFactory_Enable = "yuanhan.session-factory.enable";
	
	public final static String Fastjson_Enable = "yuanhan.fastjson.enable";
	
	public final static String Fastjson_Filter_Exclude = "yuanhan.fastjson.filter.exclude";
	
	public final static String Global_DateFormat = "yuanhan.global.date-format";
	public final static String Global_DateTimeFormat = "yuanhan.global.datetime-format";
	
	public final static String SQL_File_Encoding = "yuanhan.sql.encoding";
	
	public final static String SQL_Path_Prefix = "yuanhan.sql.path.prefix";
	
	public final static String SQL_Comment = "yuanhan.sql.comment";
	
	public final static String Generic_CRUD_Enable = "yuanhan.generic.crud.enable";
	public final static String Generic_CRUD_Path = "yuanhan.generic.crud.namespace";
	
	public final static String LoadBalance_All_Enable = "yuanhan.remote.rest-template.loadbalance.enbale-all";
	
	public final static String Remote_Gateway = "yuanhan.remote.api.gateway.url";

}
