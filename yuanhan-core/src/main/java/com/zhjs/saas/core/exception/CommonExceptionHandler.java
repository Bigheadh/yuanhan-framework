package com.yuanhan.yuanhan.core.exception;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.yuanhan.yuanhan.core.config.PropertyConfig;
import com.yuanhan.yuanhan.core.util.ArrayUtil;
import com.yuanhan.yuanhan.core.util.MessageUtil;
import com.yuanhan.yuanhan.core.util.StringUtil;

/**
 * 
 * @author: 	yuanhan
 * @since: 		2017-05-17
 * @modified: 	2017-05-17
 * @version:
 */
@RestControllerAdvice
public class CommonExceptionHandler
{
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final static String HTTP_STATUS_PREFIX = "reqeust.http.status.";
	
	@Autowired
	private Environment env;
	
	private String[] tracePackages;
	
	@PostConstruct
	public void init()
	{
		String[] packs = env.getProperty(PropertyConfig.Exception_Trace_Package, String.class, "").split(",");
		tracePackages = new String[packs.length];
		for(int i=0; i<packs.length; i++)
			tracePackages[i] = StringUtil.trim(packs[i]);
	}

	@ExceptionHandler(BaseException.class)
	@ResponseStatus
	public ExceptionResponse handle(HttpServletRequest request, HttpServletResponse response, BaseException e) throws Exception
	{
		return handle(request, response, e, false);
	}
	
	public ExceptionResponse handle(HttpServletRequest request, HttpServletResponse response, BaseException e, boolean sentStatus) throws Exception
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
		
		if(!sentStatus)
			sendServerError(e, request, response);
		
		logger.error(msg, e);
		return eReturn;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus
	public ExceptionResponse handle(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception e) throws Exception
	{
		BaseException ex = resolveException(request, response, handler, e);
		return handle(request, response, ex, true);
	}
	
	protected BaseException resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
	{
		try
		{
			BaseException t;
			if (e instanceof HttpRequestMethodNotSupportedException) {
				t = handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) e, request,
						response, handler);
			}
			else if (e instanceof HttpMediaTypeNotSupportedException) {
				t = handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) e, request, response,
						handler);
			}
			else if (e instanceof HttpMediaTypeNotAcceptableException) {
				t = handleHttpMediaTypeNotAcceptable((HttpMediaTypeNotAcceptableException) e, request, response,
						handler);
			}
			else if (e instanceof MissingPathVariableException) {
				t = handleMissingPathVariable((MissingPathVariableException) e, request,
						response, handler);
			}
			else if (e instanceof MissingServletRequestParameterException) {
				t = handleMissingServletRequestParameter((MissingServletRequestParameterException) e, request,
						response, handler);
			}
			else if (e instanceof ServletRequestBindingException) {
				t = handleServletRequestBindingException((ServletRequestBindingException) e, request, response,
						handler);
			}
			else if (e instanceof ConversionNotSupportedException) {
				t = handleConversionNotSupported((ConversionNotSupportedException) e, request, response, handler);
			}
			else if (e instanceof TypeMismatchException) {
				t = handleTypeMismatch((TypeMismatchException) e, request, response, handler);
			}
			else if (e instanceof HttpMessageNotReadableException) {
				t = handleHttpMessageNotReadable((HttpMessageNotReadableException) e, request, response, handler);
			}
			else if (e instanceof HttpMessageNotWritableException) {
				t = handleHttpMessageNotWritable((HttpMessageNotWritableException) e, request, response, handler);
			}
			else if (e instanceof MethodArgumentNotValidException) {
				t = handleMethodArgumentNotValidException((MethodArgumentNotValidException) e, request, response,
						handler);
			}
			else if (e instanceof MissingServletRequestPartException) {
				t = handleMissingServletRequestPartException((MissingServletRequestPartException) e, request,
						response, handler);
			}
			else if (e instanceof BindException) {
				t = handleBindException((BindException) e, request, response, handler);
			}
			else if (e instanceof NoHandlerFoundException) {
				t = handleNoHandlerFoundException((NoHandlerFoundException) e, request, response, handler);
			}
			else if (e instanceof AsyncRequestTimeoutException) {
				t = handleAsyncRequestTimeoutException(
						(AsyncRequestTimeoutException) e, request, response, handler);
			}
			else if (e instanceof EmptyResultDataAccessException) {
				sendServerError(e, request, response);
				t = new DaoException(DaoException.Empty_Data, e);
			}
			else if (e instanceof DataAccessException || e instanceof SQLException || e instanceof HibernateException) {
				sendServerError(e, request, response);
				t = new DaoException(e);
			}
			else
			{
				t = new BaseException(BaseException.Not_Classified_Error, e.getMessage(), e);
				/*StringWriter stackTrace = new StringWriter();
				e.printStackTrace(new PrintWriter(stackTrace));
				t.addErrorValue(BaseException.CauseKey, stackTrace);*/
				if( env.getProperty(PropertyConfig.Exception_Global_Trace, Boolean.class, false) || requestErrorTrace(request))
					parsingStackTrace(t, e);

				sendServerError(e, request, response);
			}
			
			return t;
		}
		catch(Exception handlerException)
		{
			logger.warn("Handling of [" + e.getClass().getName() + "] resulted in Exception", handlerException);
			return new BaseException(BaseException.Not_Classified_Error, e.getMessage(), e);
		}
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
		if(ArrayUtil.isEmpty(stackTrace))
			return;
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

	/**
	 * Handle the case where no request handler method was found for the particular HTTP request method.
	 * <p>The default implementation logs a warning, sends an HTTP 405 error, sets the "Allow" header,
	 * and returns an empty {@code ModelAndView}. Alternatively, a fallback view could be chosen,
	 * or the HttpRequestMethodNotSupportedException could be rethrown as-is.
	 * @param ex the HttpRequestMethodNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		logger.warn(ex.getMessage());
		String[] supportedMethods = ex.getSupportedMethods();
		if (supportedMethods != null) {
			response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
		}
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex.getMessage());
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_METHOD_NOT_ALLOWED, ex);
	}

	/**
	 * Handle the case where no {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}
	 * were found for the PUT or POSTed content.
	 * <p>The default implementation sends an HTTP 415 error, sets the "Accept" header,
	 * and returns an empty {@code ModelAndView}. Alternatively, a fallback view could
	 * be chosen, or the HttpMediaTypeNotSupportedException could be rethrown as-is.
	 * @param ex the HttpMediaTypeNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
		if (!CollectionUtils.isEmpty(mediaTypes)) {
			response.setHeader("Accept", MediaType.toString(mediaTypes));
		}
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, ex);
	}

	/**
	 * Handle the case where no {@linkplain org.springframework.http.converter.HttpMessageConverter message converters}
	 * were found that were acceptable for the client (expressed via the {@code Accept} header.
	 * <p>The default implementation sends an HTTP 406 error and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotAcceptableException
	 * could be rethrown as-is.
	 * @param ex the HttpMediaTypeNotAcceptableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_NOT_ACCEPTABLE, ex);
	}

	/**
	 * Handle the case when a declared path variable does not match any extracted URI variable.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the MissingPathVariableException
	 * could be rethrown as-is.
	 * @param ex the MissingPathVariableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.2
	 */
	protected BaseException handleMissingPathVariable(MissingPathVariableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
	}

	/**
	 * Handle the case when a required parameter is missing.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the MissingServletRequestParameterException
	 * could be rethrown as-is.
	 * @param ex the MissingServletRequestParameterException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case when an unrecoverable binding exception occurs - e.g. required header, required cookie.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the exception could be rethrown as-is.
	 * @param ex the exception to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case when a {@link org.springframework.web.bind.WebDataBinder} conversion cannot occur.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
	 * @param ex the ConversionNotSupportedException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to convert request element: " + ex);
		}
		sendServerError(ex, request, response);
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
	}

	/**
	 * Handle the case when a {@link org.springframework.web.bind.WebDataBinder} conversion error occurs.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the TypeMismatchException could be rethrown as-is.
	 * @param ex the TypeMismatchException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleTypeMismatch(TypeMismatchException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to bind request element: " + ex);
		}
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case where a {@linkplain org.springframework.http.converter.HttpMessageConverter message converter}
	 * cannot read from a HTTP request.
	 * <p>The default implementation sends an HTTP 400 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
	 * rethrown as-is.
	 * @param ex the HttpMessageNotReadableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to read HTTP message: " + ex);
		}
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case where a {@linkplain org.springframework.http.converter.HttpMessageConverter message converter}
	 * cannot write to a HTTP request.
	 * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
	 * Alternatively, a fallback view could be chosen, or the HttpMediaTypeNotSupportedException could be
	 * rethrown as-is.
	 * @param ex the HttpMessageNotWritableException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (logger.isWarnEnabled()) {
			logger.warn("Failed to write HTTP message: " + ex);
		}
		sendServerError(ex, request, response);
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
	}

	/**
	 * Handle the case where an argument annotated with {@code @Valid} such as
	 * an {@link RequestBody} or {@link RequestPart} argument fails validation.
	 * An HTTP 400 error is sent back to the client.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

 		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case where an {@linkplain RequestPart @RequestPart}, a {@link MultipartFile},
	 * or a {@code javax.servlet.http.Part} argument is required but is missing.
	 * An HTTP 400 error is sent back to the client.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleMissingServletRequestPartException(MissingServletRequestPartException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case where an {@linkplain ModelAttribute @ModelAttribute} method
	 * argument has binding or validation errors and is not followed by another
	 * method argument of type {@link BindingResult}.
	 * By default, an HTTP 400 error is sent back to the client.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 */
	protected BaseException handleBindException(BindException ex, HttpServletRequest request,
			HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		HttpRequestException e = new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_BAD_REQUEST, ex);
		e.setArguments(ex.getMessage());
		return e;
	}

	/**
	 * Handle the case where no handler was found during the dispatch.
	 * <p>The default implementation sends an HTTP 404 error and returns an empty
	 * {@code ModelAndView}. Alternatively, a fallback view could be chosen,
	 * or the NoHandlerFoundException could be rethrown as-is.
	 * @param ex the NoHandlerFoundException to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.0
	 */
	protected BaseException handleNoHandlerFoundException(NoHandlerFoundException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_NOT_FOUND, ex);
	}

	/**
	 * Handle the case where an async request timed out.
	 * <p>The default implementation sends an HTTP 503 error.
	 * @param ex the {@link AsyncRequestTimeoutException }to be handled
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen
	 * at the time of the exception (for example, if multipart resolution failed)
	 * @return an empty BaseException indicating the exception was handled
	 * @throws IOException potentially thrown from response.sendError()
	 * @since 4.2.8
	 */
	protected BaseException handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
			HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

		if (!response.isCommitted()) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
		}
		else if (logger.isErrorEnabled()) {
			logger.error("Async timeout for " + request.getMethod() + " [" + request.getRequestURI() + "]");
		}
		return new HttpRequestException(HTTP_STATUS_PREFIX+HttpServletResponse.SC_SERVICE_UNAVAILABLE, ex);
	}


	/**
	 * Invoked to send a server error. Sets the status to 500 and also sets the
	 * request attribute "javax.servlet.error.exception" to the Exception.
	 */
	protected void sendServerError(Exception ex, HttpServletRequest request, HttpServletResponse response)
			throws IOException {


		request.setAttribute("javax.servlet.error.exception", ex);
		//response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}


}
