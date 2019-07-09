package com.releaseshub.gradle.plugin.common;


import com.jdroid.java.utils.StringUtils;
import com.jdroid.java.utils.TypeUtils;

import org.gradle.api.Project;

import java.util.List;


public class PropertyResolver {
	
	private Project project;
	
	public PropertyResolver(Project project) {
		this.project = project;
	}
	
	private Object getProp(String propertyName) {
		return getProp(propertyName, null);
	}
	
	private Object getProp(String propertyName, Object defaultValue) {
		if (project.hasProperty(propertyName)) {
			return project.property(propertyName);
		} else if (System.getenv().containsKey(propertyName)) {
			return System.getenv(propertyName);
		} else {
			return defaultValue;
		}
	}
	
	public Boolean hasProp(String propertyName) {
		return project.hasProperty(propertyName) || System.getenv().containsKey(propertyName);
	}
	
	public Boolean getBooleanProp(String propertyName) {
		return getBooleanProp(propertyName, null);
	}
	
	public Boolean getBooleanProp(String propertyName, Boolean defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return TypeUtils.getBoolean(value.toString());
		}
	}
	
	public String getStringProp(String propertyName) {
		return getStringProp(propertyName, null);
	}
	
	public String getStringProp(String propertyName, String defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return value.toString();
		}
	}
	
	public Integer getIntegerProp(String propertyName) {
		return getIntegerProp(propertyName, null);
	}
	
	public Integer getIntegerProp(String propertyName, Integer defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value.toString());
		}
	}
	
	public Long getLongProp(String propertyName) {
		return getLongProp(propertyName, null);
	}
	
	public Long getLongProp(String propertyName, Long defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value.toString());
		}
	}
	
	public Double getDoubleProp(String propertyName) {
		return getDoubleProp(propertyName, null);
	}
	
	public Double getDoubleProp(String propertyName, Double defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return Double.parseDouble(value.toString());
		}
	}
	
	public List<String> getStringListProp(String propertyName) {
		return getStringListProp(propertyName, null);
	}
	
	public List<String> getStringListProp(String propertyName, List<String> defaultValue) {
		Object value = getProp(propertyName);
		if (value == null) {
			return defaultValue;
		} else {
			return value instanceof List ? (List)value : StringUtils.splitWithCommaSeparator(value.toString());
		}
	}
}
