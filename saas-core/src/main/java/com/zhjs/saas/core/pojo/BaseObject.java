package com.zhjs.saas.core.pojo;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-05-18
 * @modified:	2017-05-18
 * @version:	
 */
@Getter
@Setter
@TypeDefs({
    @TypeDef(name="string-array", typeClass=StringArrayType.class),
    @TypeDef(name="int-array", typeClass=IntArrayType.class)
})
@MappedSuperclass
public abstract class BaseObject implements Serializable {
	
	private static final long serialVersionUID = -3171606573299156035L;
	
	@Transient
	private transient int recordCount;

	public String toString()
	{
		// TODO reference by self should be filter
		return JSON.toJSONString(this,
						SerializerFeature.DisableCircularReferenceDetect,
						SerializerFeature.WriteDateUseDateFormat,
						SerializerFeature.SkipTransientField,
						SerializerFeature.SortField);
	}
	
	public String toJSONString()
	{
		// TODO reference by self should be filter
		return this.toString();
	}

	public boolean equals(Object obj)
	{
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	public int hashCode()
	{
		return HashCodeBuilder.reflectionHashCode(this);
	}


}
