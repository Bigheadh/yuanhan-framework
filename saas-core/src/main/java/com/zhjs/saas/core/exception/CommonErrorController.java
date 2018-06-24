package com.zhjs.saas.core.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.zhjs.saas.core.annotation.InitMethod;
import com.zhjs.saas.core.annotation.RestComponent;
import com.zhjs.saas.core.config.PropertyConfig;
import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;
import com.zhjs.saas.core.util.MessageUtil;
import com.zhjs.saas.core.util.StringUtil;

/**
 * 
 * @author: 	Jackie Wang
 * @since: 		2018-02-22
 * @modified: 	2018-02-22
 * @version:
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestComponent(namespace="${server.error.path:${error.path:/error}}")
public class CommonErrorController extends AbstractErrorController
{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${server.error.path:${error.path:/error}}")
    private static String errorPath = "/error";
	
	private Environment env;
	
	private String[] tracePackages;
	
	@InitMethod
	public void init()
	{
		String[] packs = env.getProperty(PropertyConfig.Exception_Trace_Package, String.class, "").split(",");
		tracePackages = new String[packs.length];
		for(int i=0; i<packs.length; i++)
			tracePackages[i] = StringUtil.trim(packs[i]);
	}

	public CommonErrorController(ErrorAttributes errorAttributes)
	{
		super(errorAttributes);
	}

	public CommonErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers)
	{
		super(errorAttributes, errorViewResolvers);
	}

	@Override
	public String getErrorPath() {
		return errorPath;
	}

    @RequestMapping
    @ResponseBody
    public ExceptionResponse error(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
    	if(e instanceof BaseException)
    		return handle(request, response, (BaseException)e);

    	BaseException t = new BaseException(BaseException.Not_Classified_Error, e.getMessage(), e);
		if( env.getProperty(PropertyConfig.Exception_Global_Trace, Boolean.class, false) || requestErrorTrace(request))
			parsingStackTrace(t, e);
		return handle(request, response, t);		
    }

    @RequestMapping(produces = "text/html")
    public ModelAndView handleHtml(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return null;
    }
	
	public ExceptionResponse handle(HttpServletRequest request, HttpServletResponse response, BaseException e) throws Exception
	{
		ExceptionResponse eReturn = new ExceptionResponse();
		eReturn.setErrorCode(e.getErrorCode());
		eReturn.setTraceID(e.getTraceID());
		
		if( (e.getErrorModel()==null || !e.getErrorModel().containsKey(BaseException.CauseKey))
			&& (requestErrorTrace(request) || env.getProperty(PropertyConfig.Exception_Global_Trace, Boolean.class, false)) )
			parsingStackTrace(e, e);		
		eReturn.setData(e.getErrorModel());
		
		String msg = MessageUtil.getMessage(e.getErrorCode(), e.getArguments());
		if(StringUtil.isBlank(e.getErrorMsg()))
			eReturn.setMessage(msg);
		
		logger.error(msg, e);
		return eReturn;
	}
	
	protected boolean requestErrorTrace(HttpServletRequest request)
	{
		return false;
	}

	/**
	 * @param t
	 * @param e
	 */
	protected void parsingStackTrace(BaseException t, Exception e)
	{
		List<String> causeTrace = new ArrayList<>();
		StackTraceElement[] stackTrace = e.getStackTrace();
		StackTraceElement first = stackTrace[0];
		for(StackTraceElement element : stackTrace)
		{
			String trace = element.toString();
			if(element==first)
			{
				causeTrace.add(trace);
				continue;
			}			
			for(String pack : this.tracePackages)
				if(trace.startsWith(pack))
					causeTrace.add(trace);
		}
		t.addErrorValue(BaseException.CauseKey, causeTrace);
		t.addErrorValue(BaseException.MessageKey, e.getMessage());
	}
	

}
