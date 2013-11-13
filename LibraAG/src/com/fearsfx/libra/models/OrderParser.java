package com.fearsfx.libra.models;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class OrderParser implements Parser {

	@SuppressWarnings("unchecked")
	@Override
	public void parse(String str, Object obj) {
		// TODO Auto-generated method stub
		List<Order> orders = (List<Order>) obj;
		Order order = null;
		
		String[] strSplits = str.toString().split("Order\\{");
		String[][] strSplits2 = new String[strSplits.length + 1][];
		Map<String, String> info = new HashMap<String, String>();
		String[] actRes = new String[2];
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(int i = 1; i < strSplits.length; i++) {
			strSplits2[i-1] = strSplits[i].split("\\; ");
			for(int j = 0; j < strSplits2[i-1].length - 1; j++) {
				actRes = strSplits2[i-1][j].split("=");
				info.put(actRes[0], actRes[1]);
			}
			
			order = new Order();
			order.setId(Integer.parseInt(info.get("id")));
			order.setUserId(Integer.parseInt(info.get("userId")));
			order.setNote(info.get("note"));
			order.setStatus(info.get("status"));
			try {
				order.setDate(sdf.parse(info.get("date")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			orders.add(order);
		}	
	}

}
