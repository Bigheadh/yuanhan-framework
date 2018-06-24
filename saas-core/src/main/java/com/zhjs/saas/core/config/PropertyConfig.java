package com.zhjs.saas.core.config;

/**
 * 
 * @author:		Jackie Wang 
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

	
	
	public final static String DataCenter_ID = "saas.dc.id";
	
	public final static String Worker_ID = "saas.worker.id";
	

	public final static String Auto_CommonConfig_Enable = "saas.auto-common.enable";
	public final static String Jpa_DefaultConfig_Enable = "saas.jpa.default-config.enable";
	
	public final static String Logger_Factory = "saas.logger.factory";
	
	public final static String Exception_Global_Trace = "saas.exception.trace.global";
	public final static String Exception_Trace_Package = "saas.exception.trace.packages";

	public final static String SessionFactory_Enable = "saas.session-factory.enable";
	
	public final static String Fastjson_Enable = "saas.fastjson.enable";
	
	public final static String Fastjson_Filter_Exclude = "saas.fastjson.filter.exclude";
	
	public final static String Global_DateFormat = "saas.global.date-format";
	public final static String Global_DateTimeFormat = "saas.global.datetime-format";
	
	public final static String SQL_File_Encoding = "saas.sql.encoding";
	
	public final static String SQL_Path_Prefix = "saas.sql.path.prefix";
	
	public final static String SQL_Comment = "saas.sql.comment";
	
	public final static String Generic_CRUD_Enable = "saas.generic.crud.enable";
	public final static String Generic_CRUD_Path = "saas.generic.crud.namespace";
	
	public final static String LoadBalance_All_Enable = "saas.remote.rest-template.loadbalance.enbale-all";
	
	public final static String Remote_Gateway = "saas.remote.api.gateway.url";

}
