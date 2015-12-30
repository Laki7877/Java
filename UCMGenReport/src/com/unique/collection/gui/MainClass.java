package com.unique.collection.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.unique.collection.bean.Supplier;
import com.unique.collection.utils.ConnectDB;


public class MainClass{

	public static ResourceBundle resourceBundle = ResourceBundle.getBundle("config");

	public static void main(String args[]){
		initializedGUI();
	}

	private static void initializedGUI() {
		
		JPanel salePanel = initiateSaleTab();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Sale Report", salePanel);
		tabbedPane.addTab("Inventory Report", new JPanel());
		
		JFrame frame = new JFrame("Unique Collection Generate Report");
		frame.add(tabbedPane);
		frame.setSize(600, 500);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
	
	private static JPanel initiateSaleTab(){
		final UtilDateModel fromModel = new UtilDateModel();
		fromModel.setSelected(false);
		JDatePanelImpl datePanel = new JDatePanelImpl(fromModel, new Properties());
		JDatePickerImpl fromDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		final UtilDateModel toModel = new UtilDateModel();
		toModel.setSelected(false);
		datePanel = new JDatePanelImpl(toModel, new Properties());
		JDatePickerImpl toDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		

		final JTextField orderNumber = new JTextField();
		orderNumber.setPreferredSize(new Dimension(450, 30));
		
		JScrollPane scrStatus = new JScrollPane();
		List<String> statusList = getStatusList();
		final JList<String> jStatusList = new JList<String>(statusList.toArray(new String[statusList.size()]));
		scrStatus.setPreferredSize(new Dimension(220, 150));
		jStatusList.setSelectedIndices(new int[]{2,3,4});
		scrStatus.setViewportView(jStatusList);
		
		
		JScrollPane scrSupplier = new JScrollPane();
		List<Supplier> supplierList = getSupplierList();
		
		final JList<Supplier> jSupplierList = new JList<Supplier>(supplierList.toArray(new Supplier[supplierList.size()]));
		jSupplierList.setSelectionInterval(0, supplierList.size()-1);
		scrSupplier.setPreferredSize(new Dimension(220, 150));
		scrSupplier.setViewportView(jSupplierList);
		
		final JLabel pathFile = new JLabel();
		pathFile.setPreferredSize(new Dimension(500, 20));
		JButton btnGen = new JButton("Generate");
		btnGen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> stauslist = jStatusList.getSelectedValuesList();
				List<Supplier> supplierList = jSupplierList.getSelectedValuesList();
				pathFile.setText(generateReport(fromModel.getValue(), toModel.getValue(), orderNumber.getText(),stauslist,supplierList));
			}
		});

		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("From Date: "));
		panel.add(fromDatePicker);
		panel.add(new JLabel("To Date: "));
		panel.add(toDatePicker);
		panel.add(new JLabel("Order Number: "));
		panel.add(orderNumber);
		panel.add(new JLabel("Status: "));
	    panel.add(scrStatus);
	    
	    panel.add(new JLabel("Supplier: "));
	    panel.add(scrSupplier);
	    
	    
	    panel.add(new JLabel("Output: "));
		panel.add(pathFile);
		btnGen.setPreferredSize(new Dimension(550, 30));
		panel.add(btnGen);
		
		panel.add(new JLabel("Example order number: 100000100                                                                                           "));
		panel.add(new JLabel("Example order number: 100000100-100000105                                                                     "));
		panel.add(new JLabel("Example order number: 100000100,100000102,100000103,100000104,100000105   "));
		panel.add(new JLabel("Example order number: 100000100-100000105,100000120,100000130-100000140   "));
		return panel;
	}

	private static List<Supplier> getSupplierList() {
		
		List<Supplier> supplierList = new ArrayList<Supplier>();
		ConnectDB db = null;
		PreparedStatement prst = null;
		ResultSet rs = null;
		boolean exit = false;
		try{
			db = new ConnectDB();
			String supplierQuery = resourceBundle.getString("supplier.query");
			prst = db.getConn().prepareStatement(supplierQuery);
			rs = prst.executeQuery();
			if(rs!=null){
				while(rs.next()){
					Supplier supplier = new Supplier();
					supplier.setSupplierId(rs.getInt("SUP_ID"));
					supplier.setSupplierName(rs.getString("SUP_NAM"));
					supplierList.add(supplier);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot connect to database");
			exit = true;
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
			if(exit){
				System.exit(-1);
			}
		}
		return supplierList;
	}

	private static List<String> getStatusList() {
		List<String> statusList = new ArrayList<String>();
		ConnectDB db = null;
		PreparedStatement prst = null;
		ResultSet rs = null;
		boolean exit = false;
		try{
			db = new ConnectDB();
			String statusQuery = resourceBundle.getString("order.status.query");
			prst = db.getConn().prepareStatement(statusQuery);
			rs = prst.executeQuery();
			if(rs!=null){
				String line = StringUtils.EMPTY;
				while(rs.next()){
					line = rs.getString("ORDER_STATUS");
					statusList.add(line);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Cannot connect to database");
			exit = true;
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
			if(exit){
				System.exit(-1);
			}
		}
		return statusList;
	}

	protected static String generateReport(Date from, Date to, String orderNumber,List<String> statusList,List<Supplier> supplierList) {
		String filePath = StringUtils.EMPTY;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate = StringUtils.EMPTY;
		if(from != null){
			fromDate = sf.format(from);
		}
		String toDate = StringUtils.EMPTY;
		if(to != null){
			toDate = sf.format(to);
		}
		String status = StringUtils.EMPTY;
		if(statusList != null && statusList.size() > 0){
			status += "(";
			for(int i=0;i<statusList.size();i++){
				status += "'";
				status += statusList.get(i);
				status += "'";
				if((i+1) != statusList.size()){
					status += ",";
				}
			}
			status += ") ";
		}
		String supplier = StringUtils.EMPTY;
		if(supplierList != null && supplierList.size() > 0){
			supplier += "(";
			for(int i=0;i<supplierList.size();i++){
				supplier += supplierList.get(i).getSupplierId();
				if((i+1) != supplierList.size()){
					supplier += ",";
				}
			}
			supplier += ") ";
		}
		
		String order = StringUtils.EMPTY;
		if(StringUtils.isNotEmpty(orderNumber)){
			orderNumber = orderNumber.trim();
			order += "(";
			String[] splitComma = orderNumber.split(",");
			for(int i=0;i<splitComma.length;i++){
				String[] splitSlash = splitComma[i].split("-");
				if(splitSlash.length > 1){
					int start = Integer.parseInt(splitSlash[0].trim());
					int end = Integer.parseInt(splitSlash[1].trim());
					for(int j=start;j<=end;j++){
						order += "'" + j + "'";
						order += ",";
					}
				}else{
					order += "'" + splitComma[i].trim() + "'";
					order += ",";
				}
			}
			while(order.endsWith(",")){
				order = order.substring(0, order.length()-1);
			}
			order += ")";
		}
		
		ConnectDB db = null;
		PreparedStatement prst = null;
		ResultSet rs = null;
		try{
			
			String mainQuery = resourceBundle.getString("main.query");
			if(StringUtils.isNotEmpty(fromDate)){
				mainQuery += " AND DATE(O.created_at) >= '" + fromDate + "'";
			}
			if(StringUtils.isNotEmpty(toDate)){
				mainQuery += " AND DATE(O.created_at) <= '" + toDate + "'";
			}
			if(StringUtils.isNotEmpty(order)){
				mainQuery += " AND O.increment_id in " + order;
			}
			if(StringUtils.isNotEmpty(status)){
				mainQuery += " AND O.status in " + status;
			}
			if(StringUtils.isNotEmpty(supplier)){
				mainQuery += " AND ( S.supplier_id IS NULL OR S.supplier_id in " + supplier + ") ";
			}
			String groupBy = resourceBundle.getString("main.group.by");
			mainQuery += " " + groupBy;
			String orderBy = resourceBundle.getString("main.order.by");
			mainQuery += " " + orderBy;
			db = new ConnectDB();
			prst = db.getConn().prepareStatement(mainQuery);
			rs = prst.executeQuery();
			if(rs!=null){
				List<String> lines = new ArrayList<String>();
				String header = "Date,Order,Customer,SKU,Product,Supplier,Quantity,Price,Cost,Location,Status,Sell Man";
				lines.add(header);
				String line = null;
				while(rs.next()){
					
					String orderDate = sf.format(rs.getDate("ORDER_DATE"));
					String orderNum = rs.getString("ORDER_NUMBER");
					String cusName = rs.getString("CUS_NAME");
					String sku =  rs.getString("SKU");
					String proName = rs.getString("PRO_NAME");
					String supName = rs.getString("SUP_NAME");
					String location = rs.getString("LOCATION");
					String orderStatus = rs.getString("ORDER_STATUS");
					String sellMan = rs.getString("SELL_MAN");
					
					double qty = rs.getDouble("QTY");
					double cost = qty * rs.getDouble("COST");
					double price = rs.getDouble("PRICE");
					
					while(price == 0){
						if(rs.next()){
							if(orderNum.equals(rs.getString("ORDER_NUMBER"))
									&& sku.equals(rs.getString("SKU"))
									&& qty == rs.getDouble("QTY")){
								price = rs.getDouble("PRICE");
							}else{
								rs.previous();
								break;
							}
						}else{
							break;
						}
					}
					
					price *=  qty;
					line = StringUtils.EMPTY;
					line += orderDate;
					line += "," + orderNum;
					line += "," + cusName;
					line += "," + sku;
					line += "," + proName;
					line += "," + supName;
					line += "," + qty;
					line += "," + price;
					line += "," + cost;
					line += "," + location;
					line += "," + orderStatus;
					line += "," + sellMan;
					lines.add(line);
				}
				SimpleDateFormat sfTime = new SimpleDateFormat("yyyy-MM-dd HHmmss");
				File file = new File("Sale Report "+sfTime.format(new Date())+".csv");
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				for (String string : lines) {
					fos.write(string.getBytes());
					fos.write("\n".getBytes());
				}
				fos.flush();
				fos.close();
				filePath = file.getAbsolutePath();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			filePath = "<Invalid query>";
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
		return filePath;
	}

}
