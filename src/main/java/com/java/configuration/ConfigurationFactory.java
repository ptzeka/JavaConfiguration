package com.java.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigurationFactory {
	
	private static final Map<String,String> stringStringMap = new HashMap<String,String>();
	private static final Map<String,Integer> stringIntMap = new HashMap<String,Integer>();
	
	private static HashMap<Integer,HashMap<String, Object>> _configMap = new HashMap<Integer,HashMap<String,Object>>(); //keep as a hashmap so we can get by string path (in cast it is dynamic)
	private static HashMap<Integer,Configuration> _configurations = new HashMap<Integer,Configuration>();
	
	private static HashMap<Integer,ArrayList<ConfigItem>> _configs = new HashMap<Integer,ArrayList<ConfigItem>>();
	
	public static Configuration GetConfiguration(int brandId)
	{
		if(_configs.get(brandId) == null)
		{
			BuildConfiguration(brandId);
		}
		return _configurations.get(brandId);
	}
	
	public static void SetConfiguration(int brandId,ArrayList<ConfigItem> configs)
	{
		if(configs != null && configs.size() > 0)
		{
			if(_configs.get(brandId) != null)
			{
				//merge
				for(ConfigItem item : configs)
				{
					for(ConfigItem i : _configs.get(brandId))
					{
						if(i.Context.equals(item.Context) && i.SubContext.equals(item.SubContext) && i.ConfigKey.equals(item.ConfigKey))
						{
							_configs.get(brandId).remove(i);
							break;
						}
					}
					_configs.get(brandId).add(item);
				}
			}
			else
				_configs.put(brandId,configs);
			
			BuildConfiguration(brandId);
		}
	}
	
	public static void ResetCache(int brandId)
	{
		if(brandId == 0)
			_configurations.clear();
		else
			_configurations.remove(brandId);
	}
	
	private static ConfigItem getConfig(int brandId, String context, String subContext, String configKey)
	{
		if(_configs.get(brandId) != null)
		{
			for(ConfigItem item : _configs.get(brandId))
			{
				if(item.Context.equals(context) && item.SubContext.equals(subContext) && item.ConfigKey.equals(configKey))
				{
					return item;
				}
			}
		}
		return null;
	}
	
	private static String getKey(String context,String subContext,String configKey)
	{
		return  context +"." + subContext + "." + configKey;
	}
	
	private static void BuildConfiguration(int brandId)
	{
		try 
		{
			java.lang.reflect.Type stringStringMapType = ConfigurationFactory.class.getDeclaredField("stringStringMap").getGenericType();
			java.lang.reflect.Type stringIntMapType = ConfigurationFactory.class.getDeclaredField("stringIntMap").getGenericType();
			
			Configuration config = _configurations.get(brandId) == null ? new Configuration() : _configurations.get(0);
			
			_configMap.put(brandId, new HashMap<String, Object>());
			
			Class<?> contexts = config.getClass();
			if(contexts != null)
			{
				for(Class<?> context : contexts.getClasses())
				{																		
					String name = context.getName();
					name = name.substring(name.lastIndexOf('$')+1,name.length());
					
					Field contextField = (Field)config.getClass().getField(name);
					Object contextInstance = contextField.get(config);
					if(contextInstance == null)
					{
						contextInstance = context.getConstructors()[0].newInstance(config);
						contextField.set(config, contextInstance);
					}
					
					Class<?>[] subcontexts = context.getClasses();							
					
					if(subcontexts != null)
					{
						for(Class<?> subcontext : subcontexts)
						{									
							String subContextName = subcontext.getName();
							subContextName = subContextName.substring(subContextName.lastIndexOf('$')+1,subContextName.length());
							
							//init objects									
							Field subContextField = (Field)contextInstance.getClass().getField(subContextName);
							Object subContextInstance = subContextField.get(contextInstance);
							if(subContextInstance == null)
							{
								subContextInstance = subcontext.getConstructors()[0].newInstance(contextInstance);
								subContextField.set(contextInstance, subContextInstance);
							}
							
									
							Field[] fields = subContextInstance.getClass().getDeclaredFields();
							for(Field field : fields)
							{
								String fieldName = field.getName();
								String value = null;									
								String key = getKey(name,subContextName,fieldName);
								ConfigItem item = getConfig(brandId,name,subContextName,fieldName);											
								if(item != null)
									value = item.ConfigValue;
								
								if(value != null || field.get(subContextInstance) == null) //replace existing field with new value or instantiate null field
								{
									Class<?> fieldType = field.getType();
									if(fieldType.equals(int.class) || fieldType.equals(Integer.class))
									{
											int i = 0;
											if(value != null)
												try { i = Integer.parseInt(value); } catch(Exception e){};
											field.set(subContextInstance, i);
											_configMap.get(brandId).put(key, i);
									}
									else if(fieldType.equals(String.class))
									{
										field.set(subContextInstance, value == null ? "" : value);
										_configMap.get(brandId).put(key, value);
									}
									else if(fieldType.equals(boolean.class) || fieldType.equals(Boolean.class))
									{
										boolean bool = false;
										if(value != null)
											try { bool = Boolean.parseBoolean(value);} catch(Exception e){};
										field.set(subContextInstance, bool);
										_configMap.get(brandId).put(key, bool);
									}
									else if(fieldType.equals(double.class) || fieldType.equals(Double.class))
									{
											double dbl = 0.0;
											if(value != null)
												try { dbl = Double.parseDouble(value);} catch(Exception e){};
											field.set(subContextInstance, dbl);
											_configMap.get(brandId).put(key, dbl);
									}
									else if(fieldType.equals(java.util.Locale.class))
									{
										if(value != null)
										{
											String[] arr = value.toString().split("-");
											if(arr.length == 2)
											{
												Locale locale = new Locale.Builder().setLanguage(arr[0]).setRegion(arr[1]).build();
												field.set(subContextInstance, locale);
											}
										}
										else
										{
											field.set(subContextInstance, new Locale("en","US"));
										}
									}
									else if(fieldType.equals(float.class) || fieldType.equals(Float.class))
									{
										float flt = 0.0f;
										if(value != null)
											try { flt = Float.parseFloat(value);} catch(Exception e){};
										field.set(subContextInstance, flt);
										_configMap.get(brandId).put(key, flt);
									}	
									else if(field.getGenericType().equals(Integer[].class) || field.getGenericType().equals(int[].class))
									{
										
										ArrayList<Integer> list = new ArrayList<Integer>();
										if(value != null)
										{
											String[] arr = value.split(",");
											for(String s : arr)
											{
												s = s.trim();
												if(!s.equals(""))
												{
													int i = 0;
													try { i = Integer.parseInt(s);} catch(Exception e){};
													list.add(i);
												}
											}
										}
	
										field.set(subContextInstance, list.toArray(new Integer[list.size()]));
										_configMap.get(brandId).put(key, list);
									}
									else if(field.getGenericType().equals(String[].class))
									{
										ArrayList<String> list = new ArrayList<String>();
										if(value != null)
										{
											String[] arr = value.split(",");
											for(String s : arr)
											{
												list.add(s);
											}
										}
										field.set(subContextInstance, list.toArray(new String[list.size()]));
										_configMap.get(brandId).put(key, list);
									}
									else if(field.getGenericType().equals(Float[].class) || field.getGenericType().equals(float[].class) )
									{
										ArrayList<Float> list = new ArrayList<Float>();
										if(value != null)
										{
											String[] arr = value.split(",");
											for(String s : arr)
											{
												s = s.trim();
												if(!s.equals(""))
												{
													float i = 0.0f;
													try { i = Float.parseFloat(s);} catch(Exception e){};
													list.add(i);
												}
											}
										}
										field.set(subContextInstance, list.toArray(new Float[list.size()]));
										_configMap.get(brandId).put(key, list);
									}
									else if(field.getGenericType().equals(stringStringMapType))
									{								
										HashMap<String,String> map = new HashMap<String,String>();
										if(value != null)
										{
											String[] arr = value.split(",");
											for(String s : arr)
											{
												s = s.trim();
												if(!s.equals("") && s.contains(":"))
												{
													String[] parts = s.split(":");
													map.put(parts[0], parts[1]);
												}
											}
										}
										field.set(subContextInstance, map);
										_configMap.get(brandId).put(key, map);
									}
									else if(field.getGenericType().equals(stringIntMapType))
									{
										HashMap<String,Integer> map = new HashMap<String,Integer>();
										if(value != null)
										{
											String[] arr = value.split(",");
											for(String s : arr)
											{
												s = s.trim();
												if(!s.equals("") && s.contains(":"))
												{
													String[] parts = s.split(":");
													int theInt = 0;
													try
													{
														theInt = Integer.parseInt(parts[1]);
													}
													catch(Exception e)
													{
														e.printStackTrace();
													}
													map.put(parts[0], theInt);
												}
											}
										}
										field.set(subContextInstance, map);
										_configMap.get(brandId).put(key, map);
									}
								}
							} 
						}								
					}					
				}					
			}	
			_configurations.put(brandId,config);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
