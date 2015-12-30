package com.unique.collection.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.unique.collection.batch.MainBatch;

public class ConnectDB {

	private Connection conn;

	final static Logger logger = Logger.getLogger(ConnectDB.class);

	public ConnectDB() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{

		String driver = MainBatch.resourceBundle.getString("driver");
		String url = MainBatch.resourceBundle.getString("url");
		String dbuser = MainBatch.resourceBundle.getString("dbuser");
		String dbpassword = MainBatch.resourceBundle.getString("dbpassword");
		Class.forName(driver).newInstance();
		this.conn = (Connection)DriverManager.getConnection(url,dbuser,dbpassword);

	}

	public Connection getConn() throws Exception {
		try {
			if(conn == null || conn.isClosed()){
				new ConnectDB();
			}
		} catch (SQLException e) {
			logger.info("SQLException:  " + e.getMessage() );
			logger.info("SQLState:  " + e.getSQLState());
			logger.info("VendorError:  " + e.getErrorCode());
		}
		return conn;
	}

	public void closeConn(){
		try {
			if(conn != null || !conn.isClosed()){
				conn.close();
			}
		} catch (SQLException e) {
			logger.info("SQLException:  " + e.getMessage() );
			logger.info("SQLState:  " + e.getSQLState());
			logger.info("VendorError:  " + e.getErrorCode());
		}
	}

}
