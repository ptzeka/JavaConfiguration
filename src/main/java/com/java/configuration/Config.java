package com.java.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

public class Config {
	
	private static HashMap<Integer,Boolean> _loaded = new HashMap<Integer,Boolean>();
	
	public static Configuration Current()
	{
		return Current(0);
	}
	//brandId in case we have a project that brand Id changes at run time
	public static Configuration Current(int brandId)
	{
		if(_loaded.get(brandId) == null)
		{
			// load configuration from the resource folder
			ConfigurationFactory.SetConfiguration(brandId, GetProperties(brandId));
			// load configuration from the resource folder
			// to implement: get a list of ConfigItems from DB or a different source
		}
		return ConfigurationFactory.GetConfiguration(brandId);
	}
	
	private static ArrayList<ConfigItem> GetProperties(int brandId) 
	{
		ArrayList<ConfigItem> list = new ArrayList<ConfigItem>();
		String key = "";
		if(brandId > 0)
			key = "configuration." + brandId + ".properties";
		else 
			key = "configuration.properties";
		
		InputStream configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(key);
		try {
			if(configFile != null)
			{
				 Properties p = new Properties();
		         p.load(configFile);
		         configFile.close();
		         
		         for(Entry<Object, Object> e : p.entrySet()) {

		        	 String[] str = ((String)e.getKey()).split("\\.");

		        	 if(str.length == 3)
		        	 {
		        		 ConfigItem item = new ConfigItem();
		        		 item.Context = str[0];
		        		 item.SubContext = str[1];
		        		 item.ConfigKey = str[2];
		        		 item.ConfigValue = e.getValue().toString();
		        		 list.add(item);
		        	 }	        	
		         }
			}
	         
		} catch (IOException e) {
			
		}
		finally
		{
			try {
				if(configFile != null)
					configFile.close();
			} catch (IOException e) {
			}
		}
        
        return list;
         
	}
	
	public static void ResetCache()
	{
		ResetCache(0); //0 for all
	}
	public static void ResetCache(int brandId)
	{
		ConfigurationFactory.ResetCache(brandId);
	}
}
