package com.col.sp.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConventionConvertor {
	
	public static void main(String[] args){
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("C:\\Users\\laki7877\\Desktop\\COL-SP_Data Modal V2\\model.sql"));
			List<String> newLine = new ArrayList<String>();
			while ((sCurrentLine = br.readLine()) != null) {
				int start = sCurrentLine.indexOf('[');
				int end = sCurrentLine.indexOf(']');
				
				if(start == -1 || end == -1){
					//newLine.add(sCurrentLine);
					System.out.println(sCurrentLine);
					continue;
				}
				
				String oldSting = sCurrentLine;
				String oldReplace = sCurrentLine.substring(start + 1, end);
				String tmp = sCurrentLine.substring(start + 1, end);
				if(tmp.equals("dbo")){
					start = sCurrentLine.indexOf('[', end+1);
					end = sCurrentLine.indexOf(']', end+1);
					tmp = sCurrentLine.substring(start + 1, end);
					oldReplace = sCurrentLine.substring(start + 1, end);
				}
				String firstChar = "" + tmp.charAt(0);
				tmp = firstChar.toUpperCase() + tmp.substring(1, tmp.length());
				
				while(tmp.indexOf('_') != -1){
					String[] split = tmp.split("_", 2);
					firstChar = "" + split[1].charAt(0);
					tmp = split[0] + firstChar.toUpperCase() + split[1].substring(1, split[1].length());
				}
				oldSting = oldSting.replaceAll(oldReplace, tmp);
				
				
				System.out.println(oldSting);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
