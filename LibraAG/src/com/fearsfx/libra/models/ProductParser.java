package com.fearsfx.libra.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductParser implements Parser{

	@SuppressWarnings("unchecked")
	@Override
	public void parse(String str, Object obj) {
		// TODO Auto-generated method stub
		List<Product> prod = (List<Product>) obj;
		Product prods = null;
		
		String[] strSplits = str.toString().split("Product\\{");
		String[][] strSplits2 = new String[strSplits.length - 1][];
		String[][] strSplits3 = new String[strSplits.length - 1][];
		Map<String, String> info = new HashMap<String, String>();
		String[] actRes = new String[2];
		for(int i = 1; i < strSplits.length; i++) {
			strSplits2[i-1] = strSplits[i].split("\\}, ");
			if(i == strSplits.length - 1)
				strSplits2[i-1] = strSplits2[i-1][0].split("; \\}");
			strSplits3[i-1] = strSplits2[i-1][0].split("\\; ");
			for(String s : strSplits3[i-1]) {
				actRes = s.split("=");
				info.put(actRes[0], actRes[1]);
			}

			prods = new Product();
			prods.setId(Integer.parseInt(info.get("id")));
			prods.setBarcode(Integer.parseInt(info.get("barcode")));
			prods.setDescription(info.get("description"));
			prods.setGroup(info.get("group"));
			prods.setManufacturer(info.get("manufacturer"));
			prods.setName(info.get("name"));
			prods.setPicture(info.get("picture"));
			prods.setQuantity(Integer.parseInt(info.get("quantity")));
			prod.add(prods);
		}	
	}

}
