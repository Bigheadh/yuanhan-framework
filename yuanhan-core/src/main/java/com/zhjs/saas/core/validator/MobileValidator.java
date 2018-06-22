package com.yuanhan.yuanhan.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.yuanhan.yuanhan.core.util.StringUtil;
import com.yuanhan.yuanhan.core.validator.annotation.Mobile;

/**
 * 
 * @author:		yuanhan
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
