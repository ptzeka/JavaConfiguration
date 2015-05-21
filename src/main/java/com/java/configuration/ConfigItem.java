package com.java.configuration;

public class ConfigItem {
	
	public ConfigItem(String context, String subContext, String configKey)
	{
		Context = context;
		Context = subContext;
		ConfigKey = configKey;
		ConfigValue = "";
	}
	public ConfigItem(String context, String subContext, String configKey,String configValue)
	{
		Context = context;
		Context = subContext;
		ConfigKey = configKey;
		ConfigValue = configValue;
	}
	public ConfigItem()
	{
		Context = "";
		Context = "";
		ConfigKey = "";
		ConfigValue = "";
	}
	public String Context;
	public String SubContext;
	public String ConfigKey;
	public String ConfigValue;
}
