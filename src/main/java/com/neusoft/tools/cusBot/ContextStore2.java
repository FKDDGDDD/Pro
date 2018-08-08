package com.neusoft.tools.cusBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neusoft.service.IntentService;

public class ContextStore2 {

	private IntentService intentService;
	// 当前意图
	private String currIntent;
	// 标明当前意图是否为None
	private boolean noneIntent;
	// 存储了每轮回话传入的实体
	private List<List<Entity>> entityList = new ArrayList<>();
	// 上下文查找得到的最终entity集合
	private List<Entity> result;
	// 记录了各门课程换一批的次数
	private Map<String, Integer> queryMap = new HashMap<>();
	// 记录了某分部各门课程换一批的次数
	
	public ContextStore2(IntentService intentService) {
		this.intentService = intentService;
	}
	
	/**
	 * 处理第一条消息
	 * @param response
	 */
	public void updateWithoutCheck(LUISresponse response) {
		this.currIntent = response.getTopScoringIntent().getIntent();
		this.entityList.add(response.getEntities());
		this.result = response.getEntities();
		String entity = "";
		if(currIntent.equals("获取推荐")) {
			if(result.size() == 1) {
				// 实体值（比如java、实训中心等）
				entity = result.get(0).getEntity();
			}
			// 查询分部下的课程
			else if(result.size() == 2) {
				entity = result.get(0).getEntity()+"_"+result.get(1).getEntity();
			}
			updateCount(entity);
		}
	}
	
	/**
	 * 处理第二条及以后消息
	 * @param response 消息的意图和实体
	 */
	public void update(LUISresponse response) {
		String nextIntent = response.getTopScoringIntent().getIntent();
		List<Entity> nextEntities = response.getEntities();
		result = nextEntities;
		if(entityList.size()>0 && entityList.get(entityList.size()-1).size()>0) {
			System.out.println(entityList.get(entityList.size()-1).get(0).getEntity());
		}
		if(result.size()>0)
			System.out.println(result.get(0).getEntity());
		switch(nextIntent) {
		// 不改变当前意图，该轮对话为之前消息的补充
		case "补充":
			int size = entityList.size();
			
			for(int i=size-1; i>=0; i--) {
				List<Entity> list = entityList.get(i);
				int listSize = list.size();
				if(listSize>0) {
					mixEntities(list);
					break;
				}	
			}
			
			// 更新map
			String map_key = result.get(0).getEntity();
			for(int i=1; i<result.size()-1; i++)
				map_key += "_"+ result.get(i).getEntity();
			queryMap.put(map_key, 1);
			break;
		case "获取推荐":
			currIntent = nextIntent;
			String entity = "";
			// 按照课程名或者分部查询课程
			if(result.size() == 1) {
				// 实体值（比如java、实训中心等）
				entity = result.get(0).getEntity();
			}
			// 查询分部下的课程
			else if(result.size() == 2) {
				entity = result.get(0).getEntity()+"_"+result.get(1).getEntity();
			}
			updateCount(entity);
			System.out.println("??result_size: "+result.size());
			break;
		case "换一批":
			currIntent = "获取推荐";

			for(int i=entityList.size()-1; i>=0; i--) {
				List<Entity> list = entityList.get(i);
				int entitySize = list.size();
				String key = list.get(0).getEntity();
				// 按课程名或分部查课程
				if(entitySize == 2) {
					result.addAll(entityList.get(i));
					System.out.println("&&"+key);
					updateCountWithRefresh(key);
					break;
				}
				// 查分部下课程
				else if(entitySize==3) {
					key += "_" + list.get(1).getEntity();
					System.out.println("?"+key+"?");
					result.addAll(entityList.get(i));
					updateCountWithRefresh(key);
					break;
				}
			}
			break;
		case "None":
			noneIntent = true;
			break;
		default:
			currIntent = nextIntent;
			break;
		}
		
		entityList.add(result);
		try {
			System.out.println("result: " + result.size());
			List<List<Entity>> ansList = EntityListUtil.convergeEntity(intentService.getAnsList(currIntent));
			int i = entityList.size()-1;
			while(i>0) {
				i--;
				if(contains(ansList, result)) {
					i++;
					System.out.println("!!!\t" + i);
					if(result.size()>=3)
						System.out.println("*count: " + result.get(2).getEntity());
					break;
				}
				result.addAll(entityList.get(i));
				if(i==0) {
					if(contains(ansList, result))
						i++;
				}
			}
			if(i<=0) {
				result.clear();
			}
			if(entityList.size()>0 && entityList.get(entityList.size()-1).size()>0) {
				System.out.println("update之后"+entityList.get(entityList.size()-1).get(0).getEntity());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 返回经处理后的当前意图和实体
	 * @return
	 */
	public LUISresponse getCurrentResponse() {
		LUISresponse response = new LUISresponse();
		if(noneIntent) {
			response.setTopScoringIntent(new TopScoringIntent("None"));
			noneIntent = false;
			result.clear();
		}
		else
			response.setTopScoringIntent(new TopScoringIntent(currIntent));
		response.setEntities(result);
		System.out.println("@"+response.getTopScoringIntent().getIntent()+"@");
		System.out.println("#"+result.size()+"#");
		return response;
	}
	
	/**
	 * 合并两个实体List，相同的实体后者替代前者
	 * @param prev
	 * @param curr
	 */
	public void mixEntities(List<Entity> prev) {
		List<Entity> list = new ArrayList<>();
		List<Entity> list_add = new ArrayList<>();
		for(Entity e : prev)
			list.add(e);

		for(Entity e : result) {
			int index = contains(list, e);
			if(index != -1) {
				list.remove(index);
				list.add(index, e);
			}
			else {
				list_add.add(e);
			}
		}
		list.addAll(0, list_add);
		
		if(list.size()>=3)
		if(list.get(0).getType().equals("lesson_lname") && list.get(1).getType().equals("address_branch")) {
			Entity temp = list.get(0);
			list.remove(0);
			list.add(0, list.get(0));
			list.remove(1);
			list.add(1, temp);
		}
		
		result = list;
		result.get(result.size()-1).setEntity(String.valueOf(1));
	}
	
	protected int contains(List<Entity> list, Entity entity) {
		int index = -1;
		for(Entity e : list) {
			if(e.getType().equals(entity.getType())) {
				index = list.indexOf(e);
				break;
			}
		}
		System.out.println(index);
		return index;
	}
	
	/**
	 * 更新查询次数
	 * @param key
	 */
	public void updateCount(String key) {
		Entity e = new Entity();
		e.setType("query_count");
		if(queryMap.get(key) != null) {
			int count = queryMap.get(key);
			e.setEntity(String.valueOf(count));
			queryMap.put(key, count+1);
		}
		else {
			queryMap.put(key, 1);
			e.setEntity(String.valueOf(1));
		}
		result.add(e);
	}
	
	/**
	 * 换一批的更新查询次数
	 * @param key
	 */
	public void updateCountWithRefresh(String key) {
		int count = queryMap.get(key);
		queryMap.put(key, count+1);
		// 更新result中最后一个query_count实体的值
		result.get(result.size()-1).setEntity(String.valueOf(count+1));
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
	
	public static boolean equals(List<Entity> l1, List<Entity> l2) {
		boolean isEqual = true;
		if(l1.size() != l2.size())
			isEqual = false;
		else {
			Collections.sort(l1, new Comparator<Entity>() {
				@Override
				public int compare(Entity o1, Entity o2) {
					return o1.getType().compareTo(o2.getType());
				}				
			});
			Collections.sort(l2, new Comparator<Entity>() {
				@Override
				public int compare(Entity o1, Entity o2) {
					return o1.getType().compareTo(o2.getType());
				}
			});
			
			for(int i=0; i<l1.size(); i++) {
				if(!l1.get(i).getType().equals(l2.get(i).getType())) {
					isEqual = false;
					break;
				}
			}
		}
		return isEqual;
	}
	
	public String getIntent() {
		return currIntent;
	}
	public void setIntent(String intent) {
		this.currIntent = intent;
	}

	public List<List<Entity>> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<List<Entity>> entityList) {
		this.entityList = entityList;
	}

//	public static void main(String[] args) {
//		Entity e1 = new Entity();
//		e1.setType("address_branch");
//		e1.setEntity("1");
//		
//		Entity e4 = new Entity();
//		e4.setType("lesson_lname");
//		e4.setEntity("1");
//		
//		Entity e2 = new Entity();
//		e2.setType("query_count");
//		List<Entity> list = new ArrayList<>();
//		list.add(e1);
//		list.add(e4);
//		list.add(e2);
//		Entity e3 = new Entity();
//		e3.setType("lesson_lname");
//		e3.setEntity("2");
//		List<Entity> list2 = new ArrayList<>();
//		list2.add(e3);
//		List<Entity> result = mixEntities(list, list2);
//		for(Entity e : result)
//			System.out.print(e.getType()+"+"+e.getEntity() + "\t");
//	}
}
