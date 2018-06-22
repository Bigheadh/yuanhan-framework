package com.yuanhan.yuanhan.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-05-19
 * @modified:	2017-05-19
 * @version:	
 */
public class NamedParameterObject {

	private String originalInput;

	private List<String> parameterNames = new ArrayList<String>();
	private List<int[]> parameterIndexes = new ArrayList<int[]>();

	private int namedParameterCount;

	private int unnamedParameterCount;

	private int totalParameterCount;

	NamedParameterObject(String originalInput) {
		this.originalInput = originalInput;
	}

	String getOriginalInput() {
		return this.originalInput;
	}


	void addNamedParameter(String parameterName, int startIndex, int endIndex) {
		this.parameterNames.add(parameterName);
		this.parameterIndexes.add(new int[] {startIndex, endIndex});
	}

	List<String> getParameterNames() {
		return this.parameterNames;
	}

	int[] getParameterIndexes(int parameterPosition) {
		return (int[]) this.parameterIndexes.get(parameterPosition);
	}

	void setNamedParameterCount(int namedParameterCount) {
		this.namedParameterCount = namedParameterCount;
	}

	int getNamedParameterCount() {
		return this.namedParameterCount;
	}

	void setUnnamedParameterCount(int unnamedParameterCount) {
		this.unnamedParameterCount = unnamedParameterCount;
	}

	int getUnnamedParameterCount() {
		return this.unnamedParameterCount;
	}

	void setTotalParameterCount(int totalParameterCount) {
		this.totalParameterCount = totalParameterCount;
	}

	int getTotalParameterCount() {
		return this.totalParameterCount;
	}

	public String toString() {
		return this.originalInput;
	}

}
