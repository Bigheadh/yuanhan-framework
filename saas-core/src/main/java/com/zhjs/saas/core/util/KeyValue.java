package com.zhjs.saas.core.util;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */

@SuppressWarnings("rawtypes")
public class KeyValue implements Comparable, Serializable {

	private static final long serialVersionUID = -4348371499608974164L;

	private String key;
	private Object value;
	private Object extra1;
	private Object extra2;
	private Object extra3;
	private Object extra4;

	/**
	 * 
	 */
	public KeyValue() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param key
	 * @param value
	 */
	public KeyValue(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.getKey().compareToIgnoreCase(((KeyValue) o).getKey());
	}
	
	public String toString()
	{
		ToStringBuilder sb = new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE);
		sb.append("key:    ", this.getKey());
		sb.append("value:  ", this.getValue());
		if(this.getExtra1()!=null)
			sb.append("extra1: ", this.getExtra1());
		if(this.getExtra2()!=null)
			sb.append("extra2: ", this.getExtra2());
		if(this.getExtra3()!=null)
			sb.append("extra3: ", this.getExtra3());
		if(this.getExtra4()!=null)
			sb.append("extra4: ", this.getExtra4());
		return sb.toString();
	}

	/**
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public final void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public final Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public final void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the extra1
	 */
	public final Object getExtra1() {
		return extra1;
	}

	/**
	 * @param extra1 the extra1 to set
	 */
	public final void setExtra1(Object extra1) {
		this.extra1 = extra1;
	}

	/**
	 * @return the extra2
	 */
	public final Object getExtra2() {
		return extra2;
	}

	/**
	 * @param extra2 the extra2 to set
	 */
	public final void setExtra2(Object extra2) {
		this.extra2 = extra2;
	}

	/**
	 * @return the extra3
	 */
	public final Object getExtra3() {
		return extra3;
	}

	/**
	 * @param extra3 the extra3 to set
	 */
	public final void setExtra3(Object extra3) {
		this.extra3 = extra3;
	}

	/**
	 * @return the extra4
	 */
	public final Object getExtra4() {
		return extra4;
	}

	/**
	 * @param extra4 the extra4 to set
	 */
	public final void setExtra4(Object extra4) {
		this.extra4 = extra4;
	}

}
