package com.java.home;

import com.java.configuration.Config;

/**
 * Hello world!
 *
 */
public class main 
{
    public static void main( String[] args )
    {
    	
    	System.out.println(Config.Current().Global.Site.BrandId);
    	System.out.println(Config.Current().Global.Site.DefaultLocale);
    	System.out.println(Config.Current().Global.Site.Environment);
    	System.out.println(Config.Current().Test.Common.String);
    	System.out.println(Config.Current().Test.Common.Boolean);
    	System.out.println(Config.Current().Test.Common.Integer);
    	if(Config.Current().Test.Common.IntArray != null)
    	{
	    	for(int i : Config.Current().Test.Common.IntArray)
	    	{
	    		System.out.print(i + ",");
	    	}
	    	System.out.println("");
    	}
    	if(Config.Current().Test.Common.StringArray != null)
    	{
	    	for(String s : Config.Current().Test.Common.StringArray)
	    	{
	    		System.out.print(s + ",");
	    	}
	    	System.out.println("");
    	}
        
    }
}
