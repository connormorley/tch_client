package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import loggers.LogObject;
import loggers.LtA;

/*	Created by:		Connor Morley
 * 	Title:			TCrunch Client Database Interface Controller
 *  Version update:	2.3
 *  Notes:			Class is responsible for all interactions between the client application and the established MySQL database. 
 *  
 *  References:		N/A
 */

public class DatabaseController {
	
  private static String address = null;
  private static Connection conn = null;
  private static Statement stmt = null;
  private static ResultSet res = null;
  private static ResultSet res1 = null;
  public static Random random = new Random(System.currentTimeMillis());
  static LtA logA = new LogObject();
  
  public static void SQLConnect(){
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager
          .getConnection(address);
    }
    catch(Exception e)
    {
    	logA.doLog("SQL" , "[SQL]Connection information issue, either driver or address : " + e.toString(), "Critical");
        throw new RuntimeException();
    }
  }

public static void execCustom(String query) {
	SQLConnect();
	try {
		stmt = conn.createStatement();
		stmt.executeUpdate(query);
	} catch (SQLException e) {
		logA.doLog("SQL", "[SQL]Query error while retrieving custom dataset \nError is : " + e.toString(), "Critical");
		e.printStackTrace();
		close();
		throw new RuntimeException(e);
	} 
		close();
	

}

	public static boolean checkEntriesExists() {
		SQLConnect();
		try {
			stmt = conn.createStatement();
			res = stmt.executeQuery("select * from wordlist;");
			if (!res.isBeforeFirst()) {
				close();
				return false;
			}
		} catch (SQLException e) {
			logA.doLog("SQL", "[SQL]Query error while retrieving custom dataset \nError is : " + e.toString(),
					"Critical");
			e.printStackTrace();
			close();
			throw new RuntimeException(e);
		}
		close();
		return true;
}

	public static void uploadWordlist(ArrayList<String> wordlist) {
		SQLConnect();
		String query = "insert ignore into wordlist (password) values ";
		try {
			stmt = conn.createStatement();
			
			boolean firstLoop = true;
			int counter = 0;
			for(String entry: wordlist)
			{
				if(entry.contains("\\")) // Replace all forward slashes with excepted forward slashes
					entry = entry.replaceAll("\\\\", "\\\\\\\\");
				if(entry.contains("\"")) // Replace all quotations with excepted quotations
					entry = entry.replaceAll("\"", "\\\\\"");
				if(entry.contains("'")) // Replace all quotations with excepted quotations
					entry = entry.replaceAll("'", "\\\\\'");
				
				if(firstLoop)
				{
					query = new StringBuilder().append(query).append("('" + entry + "') ").toString();
					firstLoop = false;
				}
				else
				query = new StringBuilder().append(query).append(",('" + entry + "') ").toString();
				counter++;
				if(counter == 10000)
				{
					stmt.execute(query + ";");
					query = "insert ignore into wordlist (password) values ";
					firstLoop = true;
					counter = 0;
				}
			}
		} catch (SQLException e) {
			logA.doLog("SQL", "[SQL]Query error while retrieving custom dataset \nError is : " + e.toString(),
					"Critical");
			e.printStackTrace();
			System.out.println(query);
			close();
			throw new RuntimeException(e);
		}
		close();

	}

  public static void close() {
    try {
      if (res != null) {
        res.close();
      }

      if (stmt != null) {
        stmt.close();
      }

      if (conn != null) {
        conn.close();
      }
    } catch (Exception e) {
    	logA.doLog("SQL" , "[SQL]SQL connection has failed to close! \nError is : " + e.toString(), "Critical");

    }
  }
  
  public static void setAddress(String submittedAddress)
  {
	  address = submittedAddress;
  }
  
  

} 
