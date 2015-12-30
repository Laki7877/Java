package com.unique.collection.batch;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.unique.collection.utils.ConnectDB;

public class MainBatch {

	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

	public static void main(String[] args){

		String input = System.console().readLine("Are you sure you want to continue...? (Y/N): ");
		if(null != input && input.equalsIgnoreCase("y")){
			ConnectDB db = null;
			PreparedStatement prst = null;
			ResultSet rs = null;

			try{
				db = new ConnectDB();
				String quert_select_primary_warehose = resourceBundle.getString("quert.select.primary.warehose");
				prst = db.getConn().prepareStatement(quert_select_primary_warehose);
				db.getConn().setAutoCommit(false);
				rs = prst.executeQuery();
				int wareHouseId = 0;
				if(rs!=null){
					while(rs.next()){
						wareHouseId = rs.getInt("warehouse_id");
					}
				}
				prst.close();
				rs.close();
				if(wareHouseId != 0){
					String query_insert_warehose_product = resourceBundle.getString("query.insert.warehose.product");
					prst = db.getConn().prepareStatement(query_insert_warehose_product);
					prst.setInt(1, wareHouseId);
					System.out.println("Total row inserted: "  + prst.executeUpdate());
					db.getConn().commit();
					System.out.println("Update database successful");
				}
			}catch (Exception ex){
				ex.printStackTrace();
				try {
					db.getConn().rollback();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println(e.getMessage());
				}
				System.out.println(ex.getMessage());
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
		}
		System.out.println("Good bye and have a nice day :)");
		System.console().readLine("Press any key to exit.....");
	}

}
