package com.fearsfx.libra.models;

import java.util.HashMap;
import java.util.Map;

public class UserParser implements Parser{
	@Override
	public void parse(String str, Object obj) {
		// TODO Auto-generated method stub
		User usr = (User) obj;
		
		String[] strSplits = str.split("\\{");
		strSplits = strSplits[1].split("\\}");
		strSplits = strSplits[0].split("\\; ");
		Map<String, String> info = new HashMap<String, String>();
		String[] actRes = null;
		for(String s : strSplits){
			actRes = s.split("=");
			info.put(actRes[0], actRes[1]);
		}
		usr.setAddress(info.get("address"));
		usr.setFirstName(info.get("firstName"));
		usr.setLastName(info.get("lastName"));
		usr.setPassword(info.get("password"));
		usr.setPhone(info.get("phone"));
		usr.setRole(info.get("role"));
		usr.setUsername(info.get("username"));
		usr.setId(Integer.parseInt(info.get("id")));
	}
}
