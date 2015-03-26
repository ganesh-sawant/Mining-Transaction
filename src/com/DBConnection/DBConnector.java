package com.DBConnection;

import java.sql.*;



public class DBConnector {
	public static Connection con;
	
	static
	{
		try
		{	
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost/abc","root","root");
			System.out.println("connection successful");		
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception..");
		}
	}  
	
	}
