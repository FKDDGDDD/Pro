package com.neusoft.tools.cusBot;

import java.util.ArrayList;
import java.util.List;

public class EntityListUtil {

	public static List<List<Entity>> convergeEntity(List<String> ans) {
		List<List<Entity>> list = new ArrayList<>();
		List<Entity> entities = new ArrayList<>();
		//System.out.println("pairs: " + pairs.size());
		for(String str : ans) {
			if(str == null)
				continue;
			entities.clear();
			String[] strs = str.split("[|]");
			System.out.println("strs_size: " + strs.length);
			for(int i=0; i<strs.length; i++) {
				Entity e = new Entity();
				e.setType(strs[i]);
				System.out.print(strs[i] + "\t");
				entities.add(e);
			}
			list.add(clone(entities));
		}

		return list;
	}
	
	public static List<Entity> clone(List<Entity> list){
		List<Entity> clone = new ArrayList<>();
		for(Entity e : list) {
			Entity ee = e;
			clone.add(ee);
		}
		return clone;
	}

	public static void main(String[] args) {

		List<String> ans = new ArrayList<>();
		ans.add(null);
		ans.add("lesson_lname|");
		ans.add("lesson_lname|query_count|");
		System.out.println(ans.get(0));
		List<List<Entity>> result = EntityListUtil.convergeEntity(ans);
		for(List<Entity> e : result) {
			System.out.println(e.size());
		}
	}
}
