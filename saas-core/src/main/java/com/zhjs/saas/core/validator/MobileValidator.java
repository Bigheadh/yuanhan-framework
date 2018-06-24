package com.zhjs.saas.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.zhjs.saas.core.util.StringUtil;
import com.zhjs.saas.core.validator.annotation.Mobile;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-05-18
 * @modified:	2018-05-18
 * @version:	
 */

public class MobileValidator implements ConstraintValidator<Mobile, String>
{
	
	private Mobile mobile;

	@Override
	public void initialize(Mobile constraintAnnotation)
	{
		this.mobile = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
        boolean valid = StringUtil.isMobileNumer(value);
        if(!valid)
        {
        	context.disableDefaultConstraintViolation();
        	context.buildConstraintViolationWithTemplate(mobile.message())
        			.addConstraintViolation();
        }
        return valid;
	}

}
