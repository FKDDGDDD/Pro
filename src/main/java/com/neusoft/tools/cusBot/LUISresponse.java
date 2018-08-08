package com.neusoft.tools.cusBot;

import java.util.List;

public class LUISresponse {

	private TopScoringIntent topScoringIntent;
	
	private List<Entity> entities;
	
	public TopScoringIntent getTopScoringIntent() {
		return topScoringIntent;
	}
	
	public void setTopScoringIntent(TopScoringIntent topScoringIntent) {
		this.topScoringIntent = topScoringIntent;
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
