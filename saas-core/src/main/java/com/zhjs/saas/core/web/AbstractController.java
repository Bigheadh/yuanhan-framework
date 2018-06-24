package com.zhjs.saas.core.web;

import static com.zhjs.saas.core.config.PropertyConfig.Global_DateTimeFormat;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.web.bind.WebDataBinder;
import org.xml.sax.InputSource;

import com.zhjs.saas.core.logger.Logger;
import com.zhjs.saas.core.logger.LoggerFactory;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-25
 * @modified:	2017-05-25
 * @version:	
 */
public abstract class AbstractController
{
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment env;

    //@InitBinder
	public void initBinder(WebDataBinder binder)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(env.getProperty(Global_DateTimeFormat));
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(java.sql.Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(java.sql.Time.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(java.sql.Timestamp.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Charset.class, new CharsetEditor());
		binder.registerCustomEditor(Class.class, new ClassEditor());
		binder.registerCustomEditor(Class[].class, new ClassArrayEditor());
		binder.registerCustomEditor(Currency.class, new CurrencyEditor());
		binder.registerCustomEditor(File.class, new FileEditor());
		binder.registerCustomEditor(InputStream.class, new InputStreamEditor());
		binder.registerCustomEditor(InputSource.class, new InputSourceEditor());
		binder.registerCustomEditor(Locale.class, new LocaleEditor());

		binder.registerCustomEditor(Pattern.class, new PatternEditor());
		binder.registerCustomEditor(Properties.class, new PropertiesEditor());
		binder.registerCustomEditor(Reader.class, new ReaderEditor());
		binder.registerCustomEditor(Resource[].class, new ResourceArrayPropertyEditor());
		binder.registerCustomEditor(TimeZone.class, new TimeZoneEditor());
		binder.registerCustomEditor(URI.class, new URIEditor());
		binder.registerCustomEditor(URL.class, new URLEditor());
		binder.registerCustomEditor(UUID.class, new UUIDEditor());

		// Default instances of collection editors.
		// Can be overridden by registering custom instances of those as custom editors.
		binder.registerCustomEditor(Collection.class, new CustomCollectionEditor(Collection.class));
		binder.registerCustomEditor(Set.class, new CustomCollectionEditor(Set.class));
		binder.registerCustomEditor(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
		binder.registerCustomEditor(List.class, new CustomCollectionEditor(List.class));
		binder.registerCustomEditor(Map.class, new CustomMapEditor(Map.class));
		binder.registerCustomEditor(SortedMap.class, new CustomMapEditor(SortedMap.class));

		// Default editors for primitive arrays.
		binder.registerCustomEditor(byte[].class, new ByteArrayPropertyEditor());
		binder.registerCustomEditor(char[].class, new CharArrayPropertyEditor());

		// The JDK does not contain a default editor for char!
		binder.registerCustomEditor(char.class, new CharacterEditor(false));
		binder.registerCustomEditor(Character.class, new CharacterEditor(true));

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		binder.registerCustomEditor(boolean.class, new CustomBooleanEditor(false));
		binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor(true));

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		binder.registerCustomEditor(byte.class, new CustomNumberEditor(Byte.class, false));
		binder.registerCustomEditor(Byte.class, new CustomNumberEditor(Byte.class, true));
		binder.registerCustomEditor(short.class, new CustomNumberEditor(Short.class, false));
		binder.registerCustomEditor(Short.class, new CustomNumberEditor(Short.class, true));
		binder.registerCustomEditor(int.class, new CustomNumberEditor(Integer.class, false));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
		binder.registerCustomEditor(long.class, new CustomNumberEditor(Long.class, false));
		binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(float.class, new CustomNumberEditor(Float.class, false));
		binder.registerCustomEditor(Float.class, new CustomNumberEditor(Float.class, true));
		binder.registerCustomEditor(double.class, new CustomNumberEditor(Double.class, false));
		binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		binder.registerCustomEditor(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));

		// Only register config value editors if explicitly requested.
		StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
		binder.registerCustomEditor(String[].class, sae);
		binder.registerCustomEditor(short[].class, sae);
		binder.registerCustomEditor(int[].class, sae);
		binder.registerCustomEditor(long[].class, sae);
		
	}
}
