package com.phase1;

import java.sql.*;

import com.DBConnection.DBConnector;

public class Utility {
  Connection con;
  Statement s,s1;	
  ResultSet rs , rs1;
  Utility(){
     con = DBConnector.con;    
     
	}
	public void getData() {
		try{
		s = con.createStatement();
		s1 = con.createStatement();
		System.out.println("Transactions Table --");
		rs = s.executeQuery("select * from main_transactions");
		rs1 = s1.executeQuery("select * from profit");
		System.out.println("TID  | ITEMS  |  Frequence");
		while(rs.next())
		{
			System.out.print(rs.getString(1)+"\t");
			System.out.print(rs.getString(2)+"\t");
			System.out.println(rs.getInt(3));
		}
		System.out.println("profit Table --");
		while(rs1.next())
		{
			System.out.print(rs1.getString(1)+"\t");
			System.out.println(rs1.getInt(2)+"\t");

		}
		}
		catch(Exception e){
			e.printStackTrace();
		 }
		}
	/**
	 * 
	 */
	void calculateTU(){
	    try {
	    	String t = "temp";
	      	String temp = "create view "+t+" as select TID,t.ITEM,FREQUENCY,PROFIT from main_transactions t,profit p where t.item = p.item";
	        Statement s,s1;
	    	ResultSet rs;
	    	PreparedStatement ps ;
	    	s= con.createStatement();
	    	s1= con.createStatement();
	        s1.executeUpdate(temp);
	    	rs = s.executeQuery("select TID , sum(frequency * profit) from temp group by TID");	
	        	         
	        System.out.print("\n---- Transaction Utility ----\n");
		    System.out.print("TID  | TU \n");
	    	
		    while(rs.next()){
	           String TID = rs.getString(1);
	           int TU = rs.getInt(2);
	           ps = con.prepareStatement("insert into transaction_utility values(?,?)");
	           ps.setString(1, TID);
	           ps.setInt(2, TU);
	           ps.executeUpdate();
	           System.out.println(" ." + TID + " -"+ TU);
	    	}
	    	} catch (SQLException e) {
		
			e.printStackTrace();
		}
	    	
	}

	void calculateRTU(){
	    try {
	    	
	    //  	String createView = "create view transactions_view as select * from main_transactions";
	    
	    	String createTable = "create table transactions_view as select * from main_transactions";
	      	String deleteItems = "delete from transactions_view where item IN (SELECT item FROM twu WHERE twu<50)";
	      	String updateView = "create view temp2 as select TID,t.ITEM,FREQUENCY,PROFIT from transactions_view t,profit p where t.item = p.item";
	        Statement s1,s2,s3;
	    	ResultSet rs;
	    	PreparedStatement ps ;
	    	
	    	s=con.createStatement();
	    	s1= con.createStatement();
	    	s2= con.createStatement();
	    	s3= con.createStatement();
	    	
	    	s.executeUpdate(createTable);
            s1.executeUpdate(deleteItems); 	        
	        s2.executeUpdate(updateView);
	        rs = s3.executeQuery("select TID , sum(frequency * profit) from temp2 group by TID;");	
	        System.out.print("\n---- Reorganized Transaction Utility ----\n");
	        System.out.print("TID  | RTU \n"); 	         
	    	while(rs.next()){
	           String TID = rs.getString(1);
	           int RTU = rs.getInt(2);
	           ps = con.prepareStatement("insert into RTU values(?,?)");
	           ps.setString(1, TID);
	           ps.setInt(2, RTU);
	           ps.executeUpdate();
	           System.out.println("#" + TID + " -"+ RTU);
	    	}
	    	} catch (SQLException e) {
		
			e.printStackTrace();
		}
	    	
	}

	
	void truncateTables(){
    try {
		Statement s= con.createStatement();
		Statement s1= con.createStatement();
		Statement s2= con.createStatement();
		Statement s3= con.createStatement();
		Statement s4= con.createStatement();
		Statement s5= con.createStatement();
		
		s.executeUpdate("truncate table transaction_utility"); 
	 	s1.execute("drop view IF EXISTS temp");
		s2.executeUpdate("truncate table twu");
        s3.execute("drop table IF EXISTS transactions_view");
        s4.execute("drop view IF EXISTS temp2");
    	s5.executeUpdate("truncate table RTU");
    	
    } catch (SQLException e) {
		e.printStackTrace();
	}
	}
	
	void calculateTWU() throws SQLException
	{
		String item;
		int TWU;
		Connection con=DBConnector.con;
		Statement st1 = null,st2;
		ResultSet rs1,rs2 = null;
		st1=con.createStatement();
		st2=con.createStatement();
		
		rs1=st1.executeQuery("select item from profit");
	    System.out.print("\n----Transaction Weighted Utility ----\n");
        System.out.print("TID  | TWU \n");
		while(rs1.next())
		{
		String s=rs1.getString(1);
		rs2=st2.executeQuery("select sum(TU) from transaction_utility where TID in (select  TID from main_transactions where(item='"+s+"'));");
	
		rs2.next();
		System.out.println(rs2.getInt(1));	
		
		PreparedStatement ps=con.prepareStatement("insert into TWU values(?,?)");
		ps.setString(1, s);
		ps.setInt(2, rs2.getInt(1));
		ps.executeUpdate();
		}
	}
	
   /**
 * @param args
 * @throws SQLException
 */
/**
 * @param args
 * @throws SQLException
 */
public static void main(String[] args) throws SQLException {
      Utility u = new Utility();
      Node<Integer> node = new Node<Integer>(10,"T1");
      Node<Integer> child = new Node<Integer>(20,"T2");
      Node<Integer> child2 = new Node<Integer>(30,"T3");
      Node<Integer> child3 = new Node<Integer>(40,"T4");
      node.addChild(child);
      node.addChild(child2);
      node.addChild(child3);
      Tree<Integer> t =new  Tree<Integer>();
      t.setRootElement(node);
      t.toList();
	  String s = t.toString();
	  System.out.print(s);
      u.getData();
	  u.truncateTables();
	  u.calculateTU();
	  u.calculateTWU();
      u.calculateRTU();
	}

}
