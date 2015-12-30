package com.unique.collection.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.unique.collection.utils.ConnectDB;

public class MainBatch {

	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

	public static void main(String[] args){

		String fromDate = System.console().readLine("From date: ");
		String toDate =  System.console().readLine("To date: ");
		String orderNumber = System.console().readLine("Order Number: ");
		ConnectDB db = null;
		PreparedStatement prst = null;
		ResultSet rs = null;
		try{
			db = new ConnectDB();
			String query = resourceBundle.getString("query");
			boolean isCheck = false;
			if(StringUtils.isNotEmpty(fromDate)
					|| StringUtils.isNotEmpty(toDate)
					|| StringUtils.isNotEmpty(orderNumber))
			{
				query += " WHERE";
			}
			if(StringUtils.isNotEmpty(fromDate))
			{
				query += " DATE(O.created_at) >= '" + fromDate + "'";
				isCheck = true;
			}
			if(StringUtils.isNotEmpty(toDate)){
				if(isCheck){
					query += " AND";
				}
				query += " DATE(O.created_at) <= '" + toDate + "'";
				isCheck = true;
			}
			if(StringUtils.isNotEmpty(orderNumber)){
				if(isCheck){
					query += " AND";
				}
				query += " increment_id = " + orderNumber;
			}
			System.out.println(query);
			prst = db.getConn().prepareStatement(query);
			rs = prst.executeQuery();
			if(rs!=null){
				List<String> lines = new ArrayList<String>();
				String header = "Date,Order,Customer,SKU,Product,Supplier,Quantity,Price,Cost,Location,Status,Sell Man";
				lines.add(header);
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				while(rs.next()){
					String line = StringUtils.EMPTY;
					line += sf.format(rs.getDate("ORDER_DATE"));
					line += "," + rs.getString("ORDER_NUMBER");
					line += "," + rs.getString("CUS_NAME");
					line += "," + rs.getString("SKU");
					line += "," + rs.getString("PRO_NAME");
					line += "," + rs.getString("SUP_NAME");
					line += "," + rs.getDouble("QTY");
					line += "," + rs.getDouble("PRICE");
					line += "," + rs.getDouble("COST");
					line += "," + rs.getString("LOCATION");
					line += "," + rs.getString("ORDER_STATUS");
					line += "," + rs.getString("SELL_MAN");
					lines.add(line);
				}
				SimpleDateFormat sfTime = new SimpleDateFormat("yyyy-MM-dd HHmmss");
				//				URL location = MainBatch.class.getProtectionDomain().getCodeSource().getLocation();
				File file = new File("Sale Report "+sfTime.format(new Date())+".csv");
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				for (String string : lines) {
					fos.write(string.getBytes());
					fos.write("\n".getBytes());
				}
				fos.flush();
				fos.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { 
					System.out.println(sqlEx.getMessage());
				} 
				rs = null;
			}
			if (prst != null) {
				try {
					prst.close();
				} catch (SQLException sqlEx) { 
					System.out.println(sqlEx.getMessage());
				} 
				prst = null;
			}
			if(db != null){
				db.closeConn();
			}
		}



		//		String input = System.console().readLine("Are you sure you want to continue...? (Y/N): ");
		//		if(null != input && input.equalsIgnoreCase("y")){
		//			ConnectDB db = null;
		//			PreparedStatement prst = null;
		//			ResultSet rs = null;
		//
		//			try{
		//				db = new ConnectDB();
		//				String query = resourceBundle.getString("query");
		//				prst = db.getConn().prepareStatement(quert_select_primary_warehose);
		//				db.getConn().setAutoCommit(false);
		//				rs = prst.executeQuery();
		//				int wareHouseId = 0;
		//				if(rs!=null){
		//					while(rs.next()){
		//						wareHouseId = rs.getInt("warehouse_id");
		//					}
		//				}
		//				prst.close();
		//				rs.close();
		//				if(wareHouseId != 0){
		//					String query_insert_warehose_product = resourceBundle.getString("query.insert.warehose.product");
		//					prst = db.getConn().prepareStatement(query_insert_warehose_product);
		//					prst.setInt(1, wareHouseId);
		//					System.out.println("Total row inserted: "  + prst.executeUpdate());
		//					db.getConn().commit();
		//					System.out.println("Update database successful");
		//				}
		//			}catch (Exception ex){
		//				ex.printStackTrace();
		//				try {
		//					db.getConn().rollback();
		//				} catch (SQLException e) {
		//					e.printStackTrace();
		//					System.out.println(e.getMessage());
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//					System.out.println(e.getMessage());
		//				}
		//				System.out.println(ex.getMessage());
		//			}finally{
		//				if (rs != null) {
		//					try {
		//						rs.close();
		//					} catch (SQLException sqlEx) { 
		//						System.out.println(sqlEx.getMessage());
		//					} 
		//					rs = null;
		//				}
		//				if (prst != null) {
		//					try {
		//						prst.close();
		//					} catch (SQLException sqlEx) { 
		//						System.out.println(sqlEx.getMessage());
		//					} 
		//					prst = null;
		//				}
		//				if(db != null){
		//					db.closeConn();
		//				}
		//			}
		//		}
		//		System.out.println("Good bye and have a nice day :)");
		//		System.console().readLine("Press any key to exit.....");
	}

}
