package com.yuanhan.yuanhan.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author: 	yuanhan
 * @since: 		2017-05-10
 * @modified: 	2017-05-10
 * @version:
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class StringUtil extends StringUtils
{

	/**
	 * Private constructor to prevent instantiation.
	 */
	private StringUtil(){}

	public final static char[] metacharacters = { '\\', '[', ']', '(', ')', '{', '}', '-', '^', '$', '.', '?', '*', '+',
			'|' };

	public final static char DELIMITER = '|';

	public final static String Delimiter = "|";
	
	
	/**
	 * check if it is a valid mobile number format
	 */
	private final static String MobileNumber = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
    private final static Pattern pattern = Pattern.compile(MobileNumber);
	public static boolean isMobileNumer(String mobile)
	{
		return pattern.matcher(mobile).matches();
	}

	public static String encodeMetacharacter(String s)
	{
		if (isNotBlank(s))
		{
			for (int i = 0; i < metacharacters.length; i++)
			{
				char c = metacharacters[i];
				if (c == '\\')
					s = replace(s, String.valueOf(c), "\\\\");
				else
					s = replace(s, String.valueOf(c), "\\Q" + c + "\\E");
			}
		}
		return s;
	}

	public static boolean containChinese(String str)
	{
		Pattern p = Pattern.compile("[\u4e00-\u9fcc]");
		Matcher m = p.matcher(str);
		if (m.find())
		{
			return true;
		}
		return false;
	}
	
	public static boolean isChinese(char c) {  
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);  
        if (sc == Character.UnicodeScript.HAN) {  
            return true;  
        }  
        return false;  
    }

	public static String filterChinese(String str)
	{
		// 用于返回结果
		String result = str;
		boolean flag = containChinese(str);
		if (flag)
		{// 包含中文
			// 用于拼接过滤中文后的字符
			StringBuffer sb = new StringBuffer();
			// 用于校验是否为中文
			boolean flag2 = false;
			// 用于临时存储单字符
			char chinese = 0;
			// 5.去除掉文件名中的中文
			// 将字符串转换成char[]
			char[] charArray = str.toCharArray();
			// 过滤到中文及中文字符
			for (int i = 0; i < charArray.length; i++)
			{
				chinese = charArray[i];
				flag2 = isChinese(chinese);
				if (!flag2)
				{// 不是中日韩文字及标点符号
					sb.append(chinese);
				}
			}
			result = sb.toString();
		}
		return result;
	}
	
	
    /**
     * 匹配是否为数字
     * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
     * @return
     * @author yutao
     * @date 2017年11月14日下午7:41:22
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    
    public static void main(String[] args)
    {
    	//System.out.println(isNumeric("-19162431.1254"));
    	/*
		//System.out.println(escapeXml("kisdfj '<>lsdkfsd\n"));
		Arrays.asList(splitWithNull("s  |  as", '|')).forEach(System.out::println);
		splitAsListWithMap(
				":Currency|USD:USD|GBP:GBP|RMB:RMB|EUR:EUR|AUD:AUD|CAD:CAD|CHF:CHF|JPY:JPY|HKD:HKD|NZD:NZD|SGD:SGD|NTD:NTD|Other:Other");
		*/
    	System.out.println(isMobileNumer("13448887777"));
	}

	public static String copyToString(InputStream in, Charset charset) throws IOException
	{
		if (in == null)
		{
			return "";
		}

		StringBuilder out = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] buffer = new char[4096];
		int bytesRead = -1;
		while ((bytesRead = reader.read(buffer)) != -1)
		{
			out.append(buffer, 0, bytesRead);
		}
		return out.toString();
	}

	/**
	 * Convenience method to return a String array as a '|' delimited
	 * 
	 * @param arr
	 *            the array to display
	 * @return the delimited String
	 */
	public static String gmArrayToString(Object[] arr)
	{
		return arrayToDelimitedString(arr, DELIMITER);
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for <code>toString()</code> implementations.
	 * 
	 * @param arr
	 *            the array to display
	 * @param delimiter
	 *            the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, char delimiter)
	{
		if (ObjectUtils.isEmpty(arr))
		{
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			if (i > 0)
			{
				sb.append(delimiter);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static List<KeyValue> splitAsList(String source)
	{
		List<KeyValue> list = new ArrayList<KeyValue>();

		if (!isEmpty(source))
		{
			String[] oneD = split(source, DELIMITER);
			if (oneD != null)
			{
				for (int i = 0; i < oneD.length; i++)
				{
					String[] twoD = split(oneD[i], ':');
					if (twoD != null && twoD.length == 2)
					{
						KeyValue obj = new KeyValue();
						obj.setKey(twoD[0]);
						obj.setValue(twoD[1]);
						list.add(obj);
					}
				}
			}
		}

		return list;
	}

	/**
	 * 
	 * @param source
	 * @return List<Map<String,String>>
	 */
	public static List<Map<String, String>> splitAsListWithMap(String source)
	{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (!isEmpty(source))
		{
			String[] oneD = split(source, DELIMITER);
			if (oneD != null)
			{
				for (int i = 0; i < oneD.length; i++)
				{
					String[] twoD = splitPreserveAllTokens(oneD[i], ':');
					if (twoD != null && twoD.length == 2)
					{
						Map<String, String> map = new HashMap<String, String>();
						map.put("key", twoD[0]);
						map.put("value", twoD[1]);
						list.add(map);
					}
				}
			}
		}
		return list;
	}

	// paser - replace all tag w/- tostr in str
	public static String parser(String str, String tag, String tostr)
	{
		if (str == null || str.indexOf(tag) == -1)
		{
			return str;
		}
		if (tag.equals(tostr))
		{
			return str;
		}
		StringBuffer temp = new StringBuffer("");
		int taglen = tag.length();
		int lastPos = 0;
		int pos = str.indexOf(tag);
		while (pos != -1)
		{
			temp.append(str.substring(lastPos, pos)).append(tostr);
			lastPos = pos + taglen;
			pos = str.indexOf(tag, lastPos);
		}
		temp.append(str.substring(lastPos));
		return temp.toString();
	}

	/**
	 * perform the logic for the split. as we set the preserveAllTokens flag
	 * false, there for we wont keep the null tokens. e.g.
	 * StringUtils.splitAsList("a..b.c", '.') = ["a", "b", "c"]
	 * 
	 * @param str
	 *            the String to parse, may be <code>null</code>
	 * @param separatorChar
	 *            the separate character preserveAllTokens if <code>true</code>,
	 *            adjacent separators are treated as empty token separators; if
	 *            <code>false</code>, adjacent separators are treated as one
	 *            separator.
	 * @return a list of parsed Strings, <code>null</code> if null String input
	 */
	public static List<String> splitAsList(String str, char separatorChar)
	{
		boolean preserveAllTokens = false;
		if (str == null)
		{
			return null;
		}
		int len = str.length();
		if (len == 0)
		{
			return null;
		}
		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		boolean match = false;
		boolean lastMatch = false;
		while (i < len)
		{
			if (str.charAt(i) == separatorChar)
			{
				if (match || preserveAllTokens)
				{
					list.add(str.substring(start, i));
					match = false;
					lastMatch = true;
				}
				start = ++i;
				continue;
			}
			lastMatch = false;
			match = true;
			i++;
		}
		if (match || (preserveAllTokens && lastMatch))
		{
			list.add(str.substring(start, i));
		}
		return list;
	}

	/**
	 * transfer a list to String as "id1|id2|id3|id4"
	 * 
	 * @param source
	 *            List<Long>
	 * 
	 * @return String : "id1|id2|id3|id4...idn"
	 * 
	 */
	public static String seperatedListToString(List<Long> list)
	{
		StringBuilder sb = new StringBuilder();
		if (list != null && list.size() > 0)
		{
			for (Long id : list)
			{
				sb.append(id.toString());
				sb.append(DELIMITER);
			}
			sb.deleteCharAt(sb.length() - 1);// remove last "|"
		}
		return sb.toString();
	}

	/**
	 * split a String as "id1|id2|id3|id4" into a List<Long> whose elements is
	 * Long
	 * 
	 * @param source
	 *            String : "id1|id2|id3|id4...idn"
	 * @return List List<Long>
	 * 
	 * @author ben
	 * @modified Jackie
	 */
	public static List<Long> splitAsIDList(String source)
	{
		List<Long> idList = new ArrayList<Long>();
		if (!isEmpty(source))
		{
			String[] array = split(source, DELIMITER);
			if (array != null)
			{
				for (int i = 0; i < array.length; i++)
				{
					Long id = NumberUtils.createLong(array[i]);
					idList.add(id);
				}
			}
		}
		return idList;
	}

	/**
	 * split a String as "id1|id2|id3|id4" into a List<Long> whose elements is
	 * Long
	 * 
	 * @param source
	 *            String : "id1|id2|id3|id4...idn"
	 * @return List List<Long>
	 * 
	 * @author ben
	 * @modified Jackie
	 */
	public static List<Long> splitAsIDList(String source, char separatorChar)
	{
		List<Long> idList = new ArrayList<Long>();
		if (!isEmpty(source))
		{
			String[] array = split(source, separatorChar);
			if (array != null)
			{
				for (int i = 0; i < array.length; i++)
				{
					Long id = NumberUtils.createLong(array[i]);
					idList.add(id);
				}
			}
		}
		return idList;
	}

	/**
	 * 
	 * transfer a String array into a List<Long> whose elements is Long
	 * 
	 * @param source
	 *            String[] : { "id1", "id2", "id3", "id4"}
	 * @return List List<Long>
	 * 
	 * @author ben
	 */
	public static List transferIntoIDList(String[] arrayID)
	{
		if (arrayID == null || arrayID.length < 1)
		{
			return null;
		}
		List idList = new ArrayList(arrayID.length);
		for (int i = 0; i < arrayID.length; i++)
		{
			Long id = NumberUtils.createLong(arrayID[i]);
			idList.add(id);
		}
		return idList;
	}

	/**
	 * 
	 * split a String as "id1|id2|id3|id4" into a List<Integer> whose elements
	 * is Integer
	 * 
	 * @param source
	 *            String : "id1|id2|id3|id4...idn"
	 * @return List List<Integer>
	 * 
	 * @author ben
	 * @modified Jackie
	 */
	public static List splitAsIntList(String source)
	{
		List idList = new ArrayList();
		if (!isEmpty(source))
		{
			String[] array = split(source, DELIMITER);
			if (array != null)
			{
				for (int i = 0; i < array.length; i++)
				{
					Integer id = NumberUtils.createInteger(array[i]);
					idList.add(id);
				}
			}
		}
		return idList;
	}

	/**
	 * 
	 * split a String as "key1|key2|key3|key4" , then build a Map as
	 * "<id1,Boolean.True>, <id2,Boolean.True>...", for the use of Checkbox
	 * group loading.(indicated which are selected.)
	 * 
	 * @param source
	 *            String : "id1|id2|id3|id4...idn"
	 * @return Map Map<String, Boolean.TRUE>
	 * 
	 * @author ben
	 */
	public static Map<String, Boolean> splitAsSelectedMap(String source)
	{
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		if (!isEmpty(source))
		{
			String[] array = split(source, DELIMITER);
			if (array != null)
			{
				for (int i = 0; i < array.length; i++)
				{
					map.put(array[i], Boolean.TRUE);
				}
			}
		}
		return map;
	}

	/**
	 * 
	 * split a string as 2D arrary DB references: buying.lead.preference=\
	 * 1:Allow all members to send reply to me\ |2:Only Paid / Trust members can
	 * send reply to me\ |3:Only Paid members can send reply to me
	 * 
	 * 
	 * @param source
	 *            String : "key1:value1|key2:value2|key3:value3...|keyn:valuen"
	 * @return Map map
	 * @author ben
	 */
	public static Map<String, String> splitAsMap(String source)
	{
		SortedMap<String, String> map = new TreeMap<String, String>();
		if (!isEmpty(source))
		{
			String[] oneD = split(source, DELIMITER);
			if (oneD != null)
			{
				for (int i = 0; i < oneD.length; i++)
				{
					String[] twoD = split(oneD[i], ':');
					if (twoD != null && twoD.length == 2)
					{
						map.put(twoD[0], twoD[1]);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * Trims tokens and omits empty tokens.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * 
	 * @param str
	 *            the String to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as String (each of those
	 *            characters is individually considered as delimiter).
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters)
	{
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * Trim leading whitespace from the given String.
	 * 
	 * @param str
	 *            the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeadingWhitespace(String str)
	{
		if (isNotEmpty(str))
		{
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && (Character.isWhitespace(buf.charAt(0)) || (int) buf.charAt(0) == 160))
		{
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurences of the supplied leading character from the given
	 * String.
	 * 
	 * @param str
	 *            the String to check
	 * @param leadingCharacter
	 *            the leading character to be trimmed
	 * @return the trimmed String
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter)
	{
		if (isNotEmpty(str))
		{
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && buf.charAt(0) == leadingCharacter)
		{
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * <p>
	 * The given delimiters string is supposed to consist of any number of
	 * delimiter characters. Each of those characters can be used to separate
	 * tokens. A delimiter is always a single character; for multi-character
	 * delimiters, consider using <code>delimitedListToStringArray</code>
	 * 
	 * @param str
	 *            the String to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as String (each of those
	 *            characters is individually considered as delimiter)
	 * @param trimTokens
	 *            trim the tokens via String's <code>trim</code>
	 * @param ignoreEmptyTokens
	 *            omit empty tokens from the result array (only applies to
	 *            tokens that are empty after trimming; StringTokenizer will not
	 *            consider subsequent delimiters as token in the first place).
	 * @return an array of the tokens (<code>null</code> if the input String was
	 *         <code>null</code>)
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim()
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens)
	{

		if (str == null)
		{
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List tokens = new ArrayList();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			if (trimTokens)
			{
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0)
			{
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * Turn given source String array into sorted array.
	 * 
	 * @param array
	 *            the source array
	 * @return the sorted array (never <code>null</code>)
	 */
	public static String[] sortStringArray(String[] array)
	{
		if (ObjectUtils.isEmpty(array))
		{
			return new String[0];
		}
		Arrays.sort(array);
		return array;
	}

	/**
	 * Copy the given Collection into a String array. The Collection must
	 * contain String elements only.
	 * 
	 * @param collection
	 *            the Collection to copy
	 * @return the String array (<code>null</code> if the passed-in Collection
	 *         was <code>null</code>)
	 */
	public static String[] toStringArray(Collection collection)
	{
		if (collection == null)
		{
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	/**
	 * Copy the given Enumeration into a String array. The Enumeration must
	 * contain String elements only.
	 * 
	 * @param enumeration
	 *            the Enumeration to copy
	 * @return the String array (<code>null</code> if the passed-in Enumeration
	 *         was <code>null</code>)
	 */
	public static String[] toStringArray(Enumeration enumeration)
	{
		if (enumeration == null)
		{
			return null;
		}
		List list = Collections.list(enumeration);
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * bind - replacement marker in tmpl is &lt;!--#KEY#--&gt; where 'KEY' is
	 * the key in the replacement Hashtable<br>
	 * if object in hashtable is not String, toString() is called
	 * 
	 * @param tmpl
	 *            template file
	 * @param replacement
	 *            the replacement hashtable for KEY value
	 * @return String of html of the template tmpl
	 */
	public static String bind(String tmpl, Map replacement)
	{
		return bind(tmpl, replacement, false, "<!--#", "#-->");
	}

	public static String bindKeepUnbindedTag(String tmpl, Map replacement)
	{
		return bind(tmpl, replacement, true, "<!--#", "#-->");
	}

	public static String bind(String tmpl, Map replacement, boolean keepUnbindedTag, String begTag, String endTag)
	{
		if (isEmpty(tmpl) || replacement == null || replacement.size() == 0)
			return tmpl;
		StringBuffer outStrBuf = new StringBuffer();
		int idxfrom = tmpl.indexOf(begTag, 0);
		boolean goReplace = (idxfrom != -1);
		// no begin marker found
		if (!goReplace)
			return tmpl;
		outStrBuf.append(tmpl.substring(0, idxfrom));
		String key = null;
		String val = null;
		Object tmp_obj = null;
		while (goReplace)
		{
			int idxto = tmpl.indexOf(endTag, idxfrom);
			int idxto_length = endTag.length();
			if (idxto != -1)
			{
				key = (String) tmpl.substring(idxfrom + 5, idxto);
				val = keepUnbindedTag ? null : ""; // set val to null if you
				// want to keep <!--#var#-->
				// tag in html
				tmp_obj = replacement.get((String) key);
				if (tmp_obj instanceof String)
				{
					val = (String) tmp_obj;
				}
				else
				{
					if (tmp_obj != null)
						val = tmp_obj.toString();
				} // if
				if (val != null)
					outStrBuf.append(val);
				else
				{
					outStrBuf.append(begTag);
					outStrBuf.append(key);
					outStrBuf.append(endTag);
				}
				idxfrom = tmpl.indexOf(begTag, idxto);
				if (idxfrom != -1)
				{
					outStrBuf.append(tmpl.substring(idxto + idxto_length, idxfrom));
				}
				else
				{
					outStrBuf.append(tmpl.substring(idxto + idxto_length, tmpl.length()));
					goReplace = false;
				}
			}
			else
			{
				outStrBuf.append(tmpl.substring(idxfrom, tmpl.length()));
				goReplace = false;
			}
		}
		return outStrBuf.toString();
	}

	/*
	 * encode a string as new encoding
	 */
	public static String encode(String s, String encoding)
	{
		if (s == null)
		{
			return null;
		}
		int len = s.length();
		if (len == 0)
		{
			return "";
		}
		try
		{
			byte[] buf = new byte[len];
			for (int i = 0; i < len; i++)
			{
				buf[i] = (byte) s.charAt(i);
			}
			return new String(buf, encoding);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println("encoding : " + encoding);
			return s;
		}
	}

	/**
	 * 1.extract all valid urls to format required hyperlink <A> w/-
	 * target=_blank. 2.format email 3.replace all '\n' to '<br>
	 * '
	 * 
	 * @param str
	 * @return
	 */
	public static String rewriteString(String str)
	{
		if (isBlank(str))
			return str;
		StringBuffer sb = new StringBuffer();
		List pos = new ArrayList();
		List values = new ArrayList();
		Pattern regexEmail = Pattern.compile("(\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)?");
		Pattern regexUrl1 = Pattern
				.compile("(href=('|\")?)?((http[s]?|ftp)+://)?(@)?(www.)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");

		Matcher matcherMail = regexEmail.matcher(str);
		while (matcherMail.find())
		{ //replace mail
			if (isNotBlank(matcherMail.group(0)))
			{
				pos.add(matcherMail.start());
				pos.add(matcherMail.end());
				values.add("<a href=\"mailto:" + matcherMail.group(0) + "\">" + matcherMail.group(0) + "</a>");
			}

		}

		for (int i = 0; i < values.size(); i++)
		{
			if (i == 0)
			{
				sb.append(str.substring(0, ((Integer) pos.get(0)).intValue()));
				sb.append(values.get(0));
			}
			else
			{
				sb.append(str.substring(((Integer) pos.get(i * 2 - 1)).intValue(),
						((Integer) pos.get(i * 2)).intValue()));
				sb.append(values.get(i));
			}
			if (i + 1 == values.size())
				sb.append(str.substring(((Integer) pos.get(i * 2 + 1)).intValue(), str.length()));

		}

		//for url
		if (isNotBlank(sb.toString()))
			str = sb.toString();
		Matcher matcherUrl1 = regexUrl1.matcher(str);
		pos = new ArrayList();
		values = new ArrayList();
		sb = new StringBuffer();
		while (matcherUrl1.find())
		{ //replace url
			if (isNotBlank(matcherUrl1.group(0)) && isBlank(matcherUrl1.group(1)) && isBlank(matcherUrl1.group(5)))
			{ //check if 'href=' is existing and if '@' is existing
				pos.add(matcherUrl1.start());
				pos.add(matcherUrl1.end());

				if (isNotBlank(matcherUrl1.group(3)))
				{
					//check if 'http://' is exist
					values.add("<a href=\"" + matcherUrl1.group(0) + "\" target=\"_blank\">" + matcherUrl1.group(0)
							+ "</a>");
				}
				else
				{
					if (isNotBlank(matcherUrl1.group(6)))
					{
						values.add("<a href=\"http://" + matcherUrl1.group(0) + "\" target=\"_blank\">"
								+ matcherUrl1.group(0) + "</a>");
					}
					else
					{
						values.add(matcherUrl1.group(0)); //do nothing
					}

				}
			}
		}
		for (int i = 0; i < values.size(); i++)
		{
			if (i == 0)
			{
				sb.append(str.substring(0, ((Integer) pos.get(0)).intValue()));
				sb.append(values.get(0));
			}
			else
			{
				sb.append(str.substring(((Integer) pos.get(i * 2 - 1)).intValue(),
						((Integer) pos.get(i * 2)).intValue()));
				sb.append(values.get(i));
			}
			if (i + 1 == values.size())
				sb.append(str.substring(((Integer) pos.get(i * 2 + 1)).intValue(), str.length()));
		}
		if (isNotBlank(sb.toString()))
			return replace(sb.toString(), "\n", "<br>");
		return replace(str, "\n", "<br>");
	}

	final static String EMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.(?:[A-Z]{2}|com|org|net|gov|mil|biz|info|mobi|name|aero|jobs|museum)$";

	/**
	 * check if the string a valid email
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkEmail(String str)
	{
		Pattern regex = Pattern.compile(EMAIL, Pattern.CASE_INSENSITIVE);
		Matcher matcherMail = regex.matcher(str);
		return matcherMail.matches();
	}

	/**
	 * check if the string included html tag
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkHTML(String str)
	{

		Pattern regex = Pattern.compile("<.+>");

		Matcher matcherMail = regex.matcher(str);
		return matcherMail.find();
	}

	final static String HTML_TAGS = "p|br|hr|b|center|div|h[1-6]{1}|font|table|tr|td|th|span|style|a|img|body|html";

	/**
	 * check if there are any html tags
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkHTMLTag(String str)
	{
		if (isBlank(str))
			return false;
		String reg11 = "<[/]?(" + HTML_TAGS + "){1}\\s*[/]?>{1}";
		String reg21 = "<[/]?(" + HTML_TAGS + "){1}\\s+[^<>]*\\s*>{1}";
		return Pattern.compile(reg11, Pattern.CASE_INSENSITIVE).matcher(str).find()
				|| Pattern.compile(reg21, Pattern.CASE_INSENSITIVE).matcher(str).find();
	}

	/**
	 * Replace html tag and multiple space to single space
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceTag(String str)
	{
		if (isBlank(str))
			return str;
		String ret = removeStyleCode(str);

		String reg11 = "<[/]?(" + HTML_TAGS + "){1}\\s*[/]?>{1}";
		String reg12 = "<[/]?(" + HTML_TAGS.toUpperCase() + "){1}\\s*[/]?>{1}";
		String reg21 = "<[/]?(" + HTML_TAGS + "){1}\\s+[^<>]*\\s*>{1}";
		String reg22 = "<[/]?(" + HTML_TAGS.toUpperCase() + "){1}\\s+[^<>]*\\s*>{1}";
		String reg3 = "[\\t\\r\\f\\v]{1,}";
		ret = ret.replaceAll(reg11, " ").replaceAll(reg12, " ");
		ret = ret.replaceAll(reg21, " ").replaceAll(reg22, " ");
		ret = ret.replaceAll(reg3, " ");

		return ret;
	}

	public static String removeStyleCode(String str)
	{
		String reg = "<style[^<>]*>.*</style>";
		String regU = "<STYLE[^<>]*>.*</STYLE>";
		return str.replaceAll(reg, "").replaceAll(regU, "");
	}

	public static String removedMSOfficeTag(String str)
	{
		if (StringUtils.isBlank(str))
			return str;
		return str.replaceAll("<!--\\[.+\\]-->", "");
	}

	final public static String lpad(int num, int length, char chr)
	{
		String str = String.valueOf(num);
		if (str.length() > length)
		{
			return str.substring(0, length - 1);
		}
		for (int i = str.length(); i < length; i++)
			str = chr + str;
		return str;
	}

	final public static String rpad(int num, int length, char chr)
	{
		String str = String.valueOf(num);
		if (str.length() > length)
		{
			return str.substring(0, length - 1);
		}
		for (int i = str.length(); i < length; i++)
			str += chr;
		return str;
	}

	/**
	 * abbreviate the a String for certain limit of word count
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param wordLimit
	 *            the limit of the word count
	 * @return
	 */
	public static String abbreviateWords(String str, int wordLimit)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(str);
		int wordLimitOffset = bi.next(wordLimit * 2);
		return wordLimitOffset != BreakIterator.DONE ? substring(str, 0, wordLimitOffset) : str;
	}

	/**
	 * replace all non-alphabetical characters to underscore
	 * 
	 * @param instr
	 *            - string to examine
	 * @return - converted string
	 */
	final public static String underscore(String instr)
	{
		if (isEmpty(instr))
			return trimToNull(instr);
		return instr.replaceAll("[^a-zA-Z0-9]+", "_");
	}

	/**
	 * check whether these two strings are equal after trim or not
	 * 
	 * @param s1
	 *            - first string to check
	 * @param s2
	 *            - second string to check
	 * @return true if they are same
	 */
	public static boolean equalsIgnoreSpace(String s1, String s2)
	{
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null || s2 == null)
			return false;
		return s1.trim().equals(s2.trim());
	}

	/**
	 * check whether these two strings are equal after trim and ignore case or
	 * not
	 * 
	 * @param s1
	 *            - first string to check
	 * @param s2
	 *            - second string to check
	 * @return true if they are same
	 */
	public static boolean equalsIgnoreCaseAndSpace(String s1, String s2)
	{
		if (s1 == null && s2 == null)
			return true;
		if (s1 == null || s2 == null)
			return false;
		return s1.trim().equalsIgnoreCase(s2.trim());
	}

	/**
	 * check whether these two characters are equal or not
	 * 
	 * @param c1
	 *            - first character to check
	 * @param c2
	 *            - second character to check
	 * @return true if they are same
	 */
	public static boolean equals(Character c1, Character c2)
	{
		if (c1 == null && c2 == null)
			return true;
		if (c1 == null || c2 == null)
			return false;
		return c1.charValue() == c2.charValue();
	}

	/**
	 * abbreviate the a String for certain limit of char count
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param charLimit
	 *            the limit of the char count
	 * @return a triming version of the String, which be tailed with " ...", and
	 *         the total char count won't exceed the charLimit
	 */
	public static String abbreviate(String str, int charLimit)
	{
		if (charLimit < 4)
		{
			throw new IllegalArgumentException("Minimum abbreviation width is 4");
		}
		if (str == null || str.length() == 0)
		{
			return str;
		}
		if (charLimit >= str.length())
		{
			return str;
		}
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(str);
		int targetOffset = bi.preceding(charLimit - 4);
		String s = substring(str, 0, targetOffset);
		return s + (s.charAt(s.length() - 1) == ' ' ? "" : " ") + "...";
		//return s.charAt(s.length() - 1) == ' '	? s + "..." : s + " ...";
	}

	/**
	 * cut the incoming string to desired length
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @param charLimit
	 *            the limit of the char count
	 * @return a triming version of the String, which be tailed with " ...", and
	 *         the total char count won't exceed the charLimit
	 */
	public static String truncate(String str, int len)
	{
		return truncate(str, len, null);
	}

	public static String truncate(String str, int len, String tail)
	{
		if (len >= defaultString(str, "").length())
			return str;
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(str);
		if (isEmpty(tail) || tail.length() > len - 1)
			return substring(str, 0, bi.preceding(len));
		else
		{
			str = substring(str, 0, bi.preceding(len - tail.length() - 1));
			return str + (str.charAt(str.length() - 1) == ' ' ? "" : " ") + tail;
		}
	}

	/**
	 * convert the incoming string array into a delimited string
	 * 
	 * @param strary
	 *            - string array to be converted
	 * @param delimit
	 *            - string delimitor, if omitted, comma is default
	 * @return - delimited string
	 */
	public static String toString(String[] strary)
	{
		return toString(strary, ",");
	}

	public static String toString(String[] strary, String delimit)
	{
		// string array is empty
		if (strary == null || strary.length == 0)
			return null;
		// convert to string
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strary.length; i++)
		{
			String str = trimToNull(strary[i]);
			if (isNotEmpty(str))
			{
				if (sb.length() > 0)
					sb.append(delimit);
				sb.append(str);
			}
		}
		return sb.toString();
	}

	public static String patchKeywordSearch(String keyword)
	{
		if (keyword == null)
			return null;
		String[] arr = split(keyword, " ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] == null)
				continue;
			if (sb.length() > 0)
				sb.append(' ');
			sb.append(arr.length);
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * Check if 2 words are similar
	 * 
	 * @param word1
	 * @param word2
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean isWordSimilar(String word1, String word2)
	{
		if (isBlank(word1) && isNotBlank(word2))
		{
			return false;
		}
		if (isNotBlank(word1) && isBlank(word2))
		{
			return false;
		}
		if (isBlank(word1) && isBlank(word2))
		{
			return true;
		}
		if (equalsIgnoreCase(word1, word2))
		{
			return true;
		}
		word1 = lowerCase(word1);
		word2 = lowerCase(word2);
		return getLevenshteinDistance(word1, word2) <= 1;
	}

	private static final int HIGHEST_SPECIAL = '>';
	private static char[][] specialCharactersRepresentation = new char[HIGHEST_SPECIAL + 1][];
	static
	{
		specialCharactersRepresentation['&'] = "&amp;".toCharArray();
		specialCharactersRepresentation['<'] = "&lt;".toCharArray();
		specialCharactersRepresentation['>'] = "&gt;".toCharArray();
		specialCharactersRepresentation['"'] = "&#034;".toCharArray();
		specialCharactersRepresentation['\''] = "&#039;".toCharArray();
	}

	/**
	 * Performs the following substring replacements (to facilitate output to
	 * XML/HTML pages):
	 *
	 * & -> &amp; < -> &lt; > -> &gt; " -> &#034; ' -> &#039;
	 *
	 * See also OutSupport.writeEscapedXml().
	 */
	public static String escapeXml(String buffer)
	{
		if (isBlank(buffer))
			return buffer;
		//System.out.print("call excapeXml: "+buffer);
		int start = 0;
		int length = buffer.length();
		char[] arrayBuffer = buffer.toCharArray();
		StringBuffer escapedBuffer = null;

		for (int i = 0; i < length; i++)
		{
			char c = arrayBuffer[i];
			if (c <= HIGHEST_SPECIAL)
			{
				char[] escaped = specialCharactersRepresentation[c];
				if (escaped != null)
				{
					// create StringBuffer to hold escaped xml string
					if (start == 0)
					{
						escapedBuffer = new StringBuffer(length + 5);
					}
					// add unescaped portion
					if (start < i)
					{
						escapedBuffer.append(arrayBuffer, start, i - start);
					}
					start = i + 1;
					// add escaped xml
					escapedBuffer.append(escaped);
				}
			}
		}
		// no xml escaping was necessary
		if (start == 0)
		{
			return buffer;
		}
		// add rest of unescaped portion
		if (start < length)
		{
			escapedBuffer.append(arrayBuffer, start, length - start);
		}
		return escapedBuffer.toString();
	}

	/**
	 * format string
	 * 
	 * @param s
	 * @return
	 */
	public static String camelCaps(String s)
	{
		if (StringUtils.isBlank(s))
			return s;
		s = s.replaceAll(",(\\w+)", ", $1");
		s = s.replaceAll("\n|(\r\n)", " #@!@# ");
		String[] a = StringUtils.split(s, ' ');
		List l = new ArrayList();
		for (int i = 0; i < a.length; i++)
		{
			String t = a[i];
			l.add(Character.toUpperCase(t.charAt(0)) + StringUtils.substring(t, 1).toLowerCase());
		}
		String rtn = StringUtils.join(l.iterator(), ' ');
		rtn = rtn.replaceAll(" #@!@# ", "\n");
		return rtn;
	}

	/**
	 * Convenience method to convert a CSV string list to a set, with
	 * predictable-iteration-order.
	 * 
	 * @param str
	 * @return
	 */
	public static Set commaDelimitedListToPredictableSet(String str)
	{
		Set set = new LinkedHashSet();
		String[] tokens = org.springframework.util.StringUtils.commaDelimitedListToStringArray(str);
		for (int i = 0; i < tokens.length; i++)
		{
			set.add(tokens[i]);
		}
		return set;
	}

	public static String[] splitWithNull(String src, char chr)
	{
		if (src == null || src.length() == 0)
			return null;

		char[] chArr = src.toCharArray();
		//StringBuffer sb = new StringBuffer();
		Set set = new HashSet();
		int size = 1;
		int lastMatchIndex = -1;
		for (int i = 0; i < chArr.length; i++)
		{
			if (chArr[i] == chr)
			{
				if (i - 1 == lastMatchIndex)
				{
					//sb.append(size-1);
					//sb.append(chr);
					set.add(size - 1);
				}
				if (i == chArr.length - 1)
					set.add(size);
				lastMatchIndex = i;
				size++;
			}
		}
		//sb.deleteCharAt(sb.length()-1);
		//String[] index = split(sb.toString(), chr);		
		String[] array = new String[size];
		String[] srcArr = split(src, chr);
		int idx = 0;
		for (int i = 0; i < size; i++)
		{
			if (set.contains(i))
				array[i] = null;
			else
			{
				array[i] = srcArr[idx];
				idx++;
			}
		}
		return array;
	}

	/**
	 * split a properites string as a map
	 * 
	 * @param str
	 *            "1\:Mr|2\:Mrs|3\:Ms|4\:Dr"
	 * @return
	 */
	public static Map splitPropertiesToPredictableMap(String source)
	{
		Map map = new LinkedHashMap<String, String>();
		if (!isEmpty(source))
		{
			String[] oneD = split(source, DELIMITER);
			if (oneD != null)
			{
				for (int i = 0; i < oneD.length; i++)
				{
					String[] twoD = split(oneD[i], ':');
					if (twoD != null && twoD.length == 2)
					{
						map.put(twoD[0], twoD[1]);
					}
				}
			}
		}
		return map;
	}

	public static String transferToJS(String source)
	{
		return source.replaceAll("\n|(\r\n)", "\\\\n").replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"")
				.replaceAll("<script", "&lt;script").replaceAll("<SCRIPT", "&lt;SCRIPT")
				.replaceAll("</SCRIPT>", "&lt;/SCRIPT&gt;").replaceAll("</script>", "&lt;/script&gt;");
	}

	public static String transferToHtml(String source)
	{
		return source.replaceAll("\n|(\r\n)", "<br/>").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
				.replaceAll("&amp;", "&").replaceAll("&nbsp;", " ");
		//.replaceAll("&#034;", "\"")
		//.replaceAll("&#039;", "\'");
	}

	public static String replaceAll(String src, String regex, String replacement, boolean caseSensitive)
	{
		if (!caseSensitive)
			return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(src).replaceAll(replacement);

		return src.replaceAll(regex, replacement);
	}

	/**
	 * give an email, return its encrypted type. if given chembo@qq.com return
	 * c****o@qq.com
	 */
	public static String encryptEmail(String email)
	{
		String replaceCode = "";
		for (int i = 1; i < email.indexOf('@') - 1; i++)
			replaceCode += "*";
		email = StringUtil.overlay(email, replaceCode, 1, email.indexOf('@') - 1);
		return email;
	}

	/**
	 * Convert the array into a delimited string with seperator
	 */
	public static String join(Object[] array, String seperator)
	{
		if (array == null)
			return null;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < array.length; i++)
		{
			result.append(array[i]);
			if (i != array.length - 1)
			{
				result.append(seperator);
			}
		}
		return result.toString();
	}

	/**
	 * Convert the list into a delimited string with seperator
	 */
	public static String join(List list, String seperator)
	{
		return join(list.toArray(new Object[0]), seperator);
	}

}
