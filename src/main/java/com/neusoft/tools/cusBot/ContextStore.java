package com.neusoft.tools.cusBot;

import java.util.ArrayList;
import java.util.List;

import com.neusoft.service.IntentService;

public class ContextStore {

	private IntentService intentService;
	private String currIntent;
	private List<Entity> entities;
	// 上下文查找得到的最终entity集合
	private List<Entity> result;
	
	public ContextStore(IntentService intentService) {
		this.intentService = intentService;
	}
	
	public void update(LUISresponse response) {
		String nextIntent = response.getTopScoringIntent().getIntent();
		List<Entity> nextEntities = response.getEntities();
		result = nextEntities;
		List<Entity> cloneEntities = new ArrayList<>();
		// 克隆实体
		for(Entity e : nextEntities) {
			Entity ee = new Entity();
			ee.setType(e.getType());
			cloneEntities.add(ee);
		}
		// 当新对话意图发生改变时，更新意图
		if(!nextIntent.equals("None")) {
			currIntent = nextIntent;
		}
		entities.addAll(nextEntities);
		
//		try {
//			
//			List<List<Entity>> list = EntityListUtil.convergeEntity(intentService.getPair(currIntent));
//			int size = entities.size();
//			int size_next = nextEntities.size();
//			int i = 0;
//			while(!contains(list, cloneEntities)) {
//				Entity ee = new Entity();
//				ee.setType(entities.get(size-size_next-1-i).getType());
//				cloneEntities.add(ee);
//				i++;
//			}
//			
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}
	
	public boolean contains(List<List<Entity>> list, List<Entity> entities) {
		boolean isContain = false;
		for(List<Entity> e : list) {
			if(equals(e,entities)) {
				isContain = true;
				break;
			}
		}
		
		return isContain;
	}
	
	public boolean equals(List<Entity> l1, List<Entity> l2) {
		boolean isEqual = true;
		if(l1.size() != l2.size())
			isEqual = false;
		else {
			for(Entity e : l1) {
				if(!l2.contains(e)) {
					isEqual = false;
					break;
				}
			}
			for(Entity e : l2) {
				if(!l1.contains(e)) {
					isEqual = false;
					break;
				}
			}
		}
		return isEqual;
	}
	
	public void updateWithoutCheck(LUISresponse response) {
		this.currIntent = response.getTopScoringIntent().getIntent();
		this.entities = response.getEntities();
		this.result = response.getEntities();
	}
	
	public LUISresponse getCurrentResponse() {
		LUISresponse response = new LUISresponse();
		response.setTopScoringIntent(new TopScoringIntent(currIntent));
		response.setEntities(result);
		return response;
	}
	
	public String getIntent() {
		return currIntent;
	}
	public void setIntent(String intent) {
		this.currIntent = intent;
	}
	public List<Entity> getEntities() {
		return entities;
	}
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
}
