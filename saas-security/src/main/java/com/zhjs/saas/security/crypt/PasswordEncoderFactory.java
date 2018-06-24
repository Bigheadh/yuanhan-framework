package com.zhjs.saas.security.crypt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2018-03-01
 * @modified:	2018-03-01
 * @version:	
 */
public class PasswordEncoderFactory
{
	private static Map<String,PasswordEncoder> factory = new HashMap<>();
	
	public static final String BCrypt = "BCrypt";
	public static final String SCrypt = "SCrypt";
	
	static {
		factory.put(BCrypt, new BCryptPasswordEncoder());
	}
	
	public static PasswordEncoder getBCEncoder()
	{
		return factory.get(BCrypt);
	}
	

}
