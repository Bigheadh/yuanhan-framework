package com.zhjs.saas.core.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-06-01
 * @modified:	2017-06-01
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD,ANNOTATION_TYPE})
@RequestMapping
@CrossOrigin
public @interface ApiMapping
{
	
	public static final String Value = "value";
	public static final String Service = "service";
	public static final String Version = "version";
	public static final String V = "v";

	/**
	 * Assign a name to this mapping.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used on both levels, a combined name is derived by concatenation
	 * with "#" as separator.
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="name")
	String name() default "";

	
	/**
	 * this parameter is an alias for {@code @RequestMapping(params="method=value")}.
	 * <p> for example,<br> 
	 * {@code @ApiMapping("app.service.action")} <br>
	 *  => <br>
	 * {@code @ApiMapping(service="app.service.action")} <br>
	 * OR <br>
	 *  {@code @RequestMapping(params="method=app.service.action")} 
	 * </p>
	 */
	@AliasFor(Service)
	String value() default "";
	
	
	/**
	 * description of the API
	 */
	String description() default "";
	

    /**
     * service version, multi versions from one particular same service. 
     * when a service has a upgrade version, and you want to keep the old version running.
     * then you can use version to separate different versions.
     *
     * @return the version number if there is any
     */
	@AliasFor("v")
	String version() default "";

    /**
     * service version, alias for version() parameter, multi versions from one particular same service. 
     * when a service has a upgrade version, and you want to keep the old version running.
     * then you can use version to separate different versions.
     *
     * @return the version number if there is any
     */
	@AliasFor("version")
	String v() default "";
	

	/**
	 * The primary mapping expressed by this annotation.
	 * <p>In a Servlet environment this is an alias for {@link #path}.
	 * For example {@code @RequestMapping("/foo")} is equivalent to
	 * {@code @RequestMapping(path="/foo")}.
	 * <p>In a Portlet environment this is the mapped portlet modes
	 * (i.e. "EDIT", "VIEW", "HELP" or any custom modes).
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 * 
	 * In a Servlet environment only: the path mapping URIs (e.g. "/myPath.do").
	 * Ant-style path patterns are also supported (e.g. "/myPath/*.do").
	 * At the method level, relative paths (e.g. "edit.do") are supported within
	 * the primary mapping expressed at the type level. Path mapping URIs may
	 * contain placeholders (e.g. "/${connect}")
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 * @see org.springframework.web.bind.annotation.ValueConstants#DEFAULT_NONE
	 * @since 4.2
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="path")
	String[] path() default {};

	/**
	 * The HTTP request methods to map to, narrowing the primary mapping:
	 * GET, POST, HEAD, OPTIONS, PUT, PATCH, DELETE, TRACE.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this HTTP method restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>Supported for Servlet environments as well as Portlet 2.0 environments.
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="method")
	RequestMethod[] method() default {};

	/**
	 * The parameters of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "myParam=myValue" style
	 * expressions, with a request only mapped if each such parameter is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "myParam!=myValue". "myParam" style expressions are also supported,
	 * with such parameters having to be present in the request (allowed to have
	 * any value). Finally, "!myParam" style expressions indicate that the
	 * specified parameter is <i>not</i> supposed to be present in the request.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this parameter restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>In a Servlet environment, parameter mappings are considered as restrictions
	 * that are enforced at the type level. The primary path mapping (i.e. the
	 * specified URI value) still has to uniquely identify the target handler, with
	 * parameter mappings simply expressing preconditions for invoking the handler.
	 * <p>In a Portlet environment, parameters are taken into account as mapping
	 * differentiators, i.e. the primary portlet mode mapping plus the parameter
	 * conditions uniquely identify the target handler. Different handlers may be
	 * mapped onto the same portlet mode, as long as their parameter mappings differ.
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="params")
	String[] params() default {};

	
	/**
	 * this parameter is an alias for {@code @RequestMapping(params="method=*")}.
	 * <p> for example,<br> 
	 * {@code @ApiMapping(service="app.service.action")} <br>
	 *  => <br>
	 *  {@code @RequestMapping(params="method=app.service.action")} 
	 * </p>
	 */
	@AliasFor("value")
	String service() default "";

	/**
	 * The headers of the mapped request, narrowing the primary mapping.
	 * <p>Same format for any environment: a sequence of "My-Header=myValue" style
	 * expressions, with a request only mapped if each such header is found
	 * to have the given value. Expressions can be negated by using the "!=" operator,
	 * as in "My-Header!=myValue". "My-Header" style expressions are also supported,
	 * with such headers having to be present in the request (allowed to have
	 * any value). Finally, "!My-Header" style expressions indicate that the
	 * specified header is <i>not</i> supposed to be present in the request.
	 * <p>Also supports media type wildcards (*), for headers such as Accept
	 * and Content-Type. For instance,
	 * <pre class="code">
	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
	 * </pre>
	 * will match requests with a Content-Type of "text/html", "text/plain", etc.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this header restriction (i.e. the type-level restriction
	 * gets checked before the handler method is even resolved).
	 * <p>Maps against HttpServletRequest headers in a Servlet environment,
	 * and against PortletRequest properties in a Portlet 2.0 environment.
	 * @see org.springframework.http.MediaType
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="headers")
	String[] headers() default {};

	/**
	 * The consumable media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Content-Type} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * </pre>
	 * Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with a {@code Content-Type} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this consumes restriction.
	 * @see org.springframework.http.MediaType
	 * @see javax.servlet.http.HttpServletRequest#getContentType()
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="consumes")
	String[] consumes() default {};

	/**
	 * The producible media types of the mapped request, narrowing the primary mapping.
	 * <p>The format is a single media type or a sequence of media types,
	 * with a request only mapped if the {@code Accept} matches one of these media types.
	 * Examples:
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = "application/json; charset=UTF-8"
	 * </pre>
	 * <p>It affects the actual content type written, for example to produce a JSON response
	 * with UTF-8 encoding, {@code "application/json; charset=UTF-8"} should be used.
	 * <p>Expressions can be negated by using the "!" operator, as in "!text/plain", which matches
	 * all requests with a {@code Accept} other than "text/plain".
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings override
	 * this produces restriction.
	 * @see org.springframework.http.MediaType
	 */
	@AliasFor(annotation=RequestMapping.class, attribute="produces")
	String[] produces() default {};
	

	@AliasFor(annotation=CrossOrigin.class)
	String[] origins() default {};

	/**
	 * List of request headers that can be used during the actual request.
	 * <p>This property controls the value of the pre-flight response's
	 * {@code Access-Control-Allow-Headers} header.
	 * {@code "*"}  means that all headers requested by the client are allowed.
	 * <p>If undefined, all requested headers are allowed.
	 */
	@AliasFor(annotation=CrossOrigin.class)
	String[] allowedHeaders() default {};

	/**
	 * List of response headers that the user-agent will allow the client to access.
	 * <p>This property controls the value of actual response's
	 * {@code Access-Control-Expose-Headers} header.
	 * <p>If undefined, an empty exposed header list is used.
	 */
	@AliasFor(annotation=CrossOrigin.class)
	String[] exposedHeaders() default {};

	/**
	 * Whether the browser should include any cookies associated with the
	 * domain of the request being annotated.
	 * <p>Set to {@code "false"} if such cookies should not included.
	 * An empty string ({@code ""}) means <em>undefined</em>.
	 * {@code "true"} means that the pre-flight response will include the header
	 * {@code Access-Control-Allow-Credentials=true}.
	 * <p>If undefined, credentials are allowed.
	 */
	@AliasFor(annotation=CrossOrigin.class)
	String allowCredentials() default "";

	/**
	 * The maximum age (in seconds) of the cache duration for pre-flight responses.
	 * <p>This property controls the value of the {@code Access-Control-Max-Age}
	 * header in the pre-flight response.
	 * <p>Setting this to a reasonable value can reduce the number of pre-flight
	 * request/response interactions required by the browser.
	 * A negative value means <em>undefined</em>.
	 * <p>If undefined, max age is set to {@code 1800} seconds (i.e., 30 minutes).
	 */
	@AliasFor(annotation=CrossOrigin.class)
	long maxAge() default -1;


}
