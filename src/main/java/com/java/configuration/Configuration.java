package com.java.configuration;

import java.util.Locale;

public class Configuration implements java.io.Serializable
{
	public Global Global;
	public class Global
	{
		public Site Site;
		public class Site
		{
			public Locale DefaultLocale;
			public int BrandId;
			public String Environment;
		}
	}
	
	public Test Test;
	public class Test
	{
		public Common Common;
		public class Common
		{
			public Float Float;
			public Boolean Boolean;
			public Integer Integer;
			public String String;
			public String[] StringArray;
			public Integer[] IntArray;
		}
	}
}