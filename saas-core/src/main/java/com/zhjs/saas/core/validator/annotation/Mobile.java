package com.zhjs.saas.core.validator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import com.zhjs.saas.core.validator.MobileValidator;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-05-18
 * @modified:	2018-05-18
 * @version:	
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
@ReportAsSingleViolation
@Constraint(validatedBy=MobileValidator.class)
public @interface Mobile
{

    String message() default "{validation.mobile.format.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
