package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import loggers.LogObject;
import loggers.LtA;

/*	Created by:		Connor Morley
 * 	Title:			TCrunch Client Start Controller
 *  Version update:	2.1
 *  Notes:			Class is used in conjunction with the system properties file for the applicaiton. Takes the user specified input of 
 *  				the properties file and applies it to system components. Checks are also made that enough information is provided
 *  				and that certain information provided is accurate.  
 *  
 *  References:		N/A
 */

public class StartController {
	
	private static Map<String, String> settings = new HashMap<String, String>();
	static LtA logA = new LogObject();

	public static void readSettingsFile(){
		File file = null;
		if(System.getProperty("os.name").contains("Windows"))
		file = new File(".\\settings.properties");
		if(System.getProperty("os.name").contains("Linux"))
		file = new File("./settings.properties");
		
		
		try {
			InputStream input = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(input);
			settings.put("ServerAddress", prop.getProperty("Server_Address"));
			settings.put("ServerPort", prop.getProperty("Server_Port"));
			settings.put("useDatabase", prop.getProperty("Use_Database"));
			if(settings.get("useDatabase").equals("no"))
			{
				if(settings.size() != 3)
				{
					System.out.println("Settings file is incorrect size, check options.");
					System.exit(0);
				}
			}
			else
			{
				settings.put("mySQLHOST", prop.getProperty("mySQL_Host"));
				settings.put("mySQLPort", prop.getProperty("mySQL_Port"));
				settings.put("mySQLDB", prop.getProperty("mySQL_DB"));
				settings.put("mySQLUser", prop.getProperty("mySQL_Username"));
				settings.put("mySQLPass", prop.getProperty("mySQL_Password"));
				if(settings.size() != 8)
				{
					System.out.println("Settings file is incorrect size, check options.");
					System.exit(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		configureSettings();
		return;
	}
	
	private static void configureSettings()
	{
		TransmissionController.ipAddress = settings.get("ServerAddress") + ":" + settings.get("ServerPort");
		if(settings.get("useDatabase").equals("yes"))
		{
			String add = "jdbc:mysql://"+settings.get("mySQLHOST")+":"+settings.get("mySQLPort")+"/"+settings.get("mySQLDB")+"?user="+settings.get("mySQLUser")+"&password="+settings.get("mySQLPass");
			DatabaseController.setAddress(add);
			checkValidity();
		}
		return;
	}
	
	public static void checkValidity()
    {
    	try{
    		DatabaseController.SQLConnect();
    		DatabaseController.close();
    	}
    	catch(Exception e)
    	{
    		logA.doLog("Start" , "[Start]There was an error with the SQL configuration or address, please confirm details.", "Critical");
    		throw new RuntimeException(e);
    	}
    }
	
}
