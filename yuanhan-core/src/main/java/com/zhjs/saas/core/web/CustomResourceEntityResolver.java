package com.yuanhan.yuanhan.core.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-07-28
 * @modified:	2017-07-28
 * @version:	
 */
public class CustomResourceEntityResolver implements EntityResolver
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DTD_EXTENSION = ".dtd";

	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
	{
		logger.trace("Trying to resolve XML entity with public ID [" + publicId + "] and system ID [" + systemId + "]");

		if (systemId != null && systemId.endsWith(DTD_EXTENSION))
		{
			int lastPathSeparator = systemId.lastIndexOf("/");
			String dtdFile = systemId.substring(lastPathSeparator + 1);
			logger.trace("Trying to locate [" + dtdFile + "] in jar");
			try
			{
				PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
				Resource[] rs = resolver.getResources("classpath*:/**/" + dtdFile);
				for (Resource res : rs)
				{
					InputSource source = new InputSource(res.getInputStream());
					source.setPublicId(publicId);
					source.setSystemId(systemId);
					logger.debug("Found beans DTD [" + systemId + "] in classpath: " + dtdFile);
					return source;
				}
			} catch (IOException ex)
			{
				logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in class path", ex);
			}
		}
		/*
		 * PathMatchingResourcePatternResolver pm = new
		 * PathMatchingResourcePatternResolver(); Resource[] rc =
		 * pm.getResources(pattern); for (int i = 0; rc != null && i <
		 * rc.length; i++) { Resource res = rc[i]; URL url = res.getURL();
		 * log.info("url="+url.toString()); }
		 */
		return null;
	}
}
