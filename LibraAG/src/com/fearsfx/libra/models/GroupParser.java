package com.fearsfx.libra.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupParser implements Parser {

	@SuppressWarnings("unchecked")
	@Override
	public void parse(String str, Object obj) {
		// TODO Auto-generated method stub
		List<Group> groups = (List<Group>) obj;
		Group group = null;

		String[] strSplits = str.toString().split("Group\\{");
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
			group = new Group();
			group.setId(Integer.parseInt(info.get("id")));
			group.setName(info.get("name"));
			groups.add(group);
		}
	}

}
