package com.neusoft.tools.cusBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neusoft.po.Lesson;
import com.neusoft.po.User;
import com.neusoft.service.IntentService;
import com.neusoft.service.KBService;
import com.neusoft.tools.recommender.RecController;

public class AnswerGenerator {
	
	private LUISresponse response;
	private String intent;
	private List<Entity> entities;
	private IntentService intentService;
	private KBService kbService;
	private RecController recController;
	
	public AnswerGenerator() {
		
	}
	
	public AnswerGenerator(IntentService intentService, KBService kbservice) {
		this.intentService = intentService;
		this.kbService = kbservice;
	}
	
	public void setLUISresponse(LUISresponse response) {
		this.response = response;
		this.intent = response.getTopScoringIntent().getIntent();
		this.entities = response.getEntities();
	}
	
	/**
	 * 将查询结果注入到模板，即替换模板中的?
	 * @return
	 */
	public String fillIn(String templet, Object[] obj) {
		int count = 0;
		StringBuilder str = new StringBuilder(templet);
		for(int i=0; i<str.length(); i++) {
			if(str.charAt(i) == '?') {
				str.replace(i, i+1, obj[count].toString());
				count++;
			}
		}
		return str.toString();
	}
	
	/**
	 * 根据意图以及实体数量找到对应答案模板
	 * @param response
	 * @return
	 */
	public String getTemplet() {
		//int entity_num = entities.size();
		String entity_list = "";
		for(Entity e : entities)
			entity_list +=  e.getType()+"|";
		String templet = "";
		try {
			List<String> templet_list = intentService.getTemplet(intent, entity_list);
			Collections.shuffle(templet_list);
			templet = templet_list.get(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return templet;
	}
	
	/**
	 * 填注答案模板
	 * @return
	 */
	public List<Object> fillInTemplet(User user){
		List<Object> answers = new ArrayList<>();
		String templet = getTemplet();
		int num = entities.size();
		
		switch(intent) {
			case "获取推荐":
				if(num == 0) {
					answers.add(templet);
				} 
				else {
					try {
						answers.add("-1");
						//第一个实体，可能是java，实训中心
						String firstEntity = entities.get(0).getEntity();
						String firstEntityType = entities.get(0).getType();
						int count = 1;
						
						// 填注模板
						Object[] entity = {firstEntity,null};
						if(num>=3)
							//分部地址
							entity[1] = entities.get(1).getEntity();
						answers.add(fillIn(templet,entity));
						
						// 从数据库中模糊查询得到相应课程List，根据实体类型赋值map
						Map<String, Object> map = new HashMap<>();
						switch(firstEntityType) {
						case "lesson_lname":
							if(num==2)
								count = Integer.parseInt(entities.get(1).getEntity());
							map.put("lname", firstEntity);
							break;
						case "address_branch":
							if(num==2) {
								map.put("branch", firstEntity);
								count = Integer.parseInt(entities.get(1).getEntity());
							}
							else {
								map.put("branch", firstEntity);
								map.put("lname", entities.get(1).getEntity());
								count = Integer.parseInt(entities.get(2).getEntity());
							}
							break;
						}
						
						// 根据map储存的条件，模糊查询得到对应课程List，并将课程id抽取出来生成一个id的List
						List<Lesson> fuzzyItems = kbService.getLessonByLike(map);
						List<Integer> fuzzy = new ArrayList<>();
						for(Lesson l : fuzzyItems)
							fuzzy.add(l.getLid());
						
						// 实例化一个推荐控制器，并根据模糊课程List和用户id得到推荐List
						recController = new RecController("F:\\Tomcat 7.0\\webapps\\upload\\rating.txt", kbService.selectUserNum(user.getQid()), 
								kbService.seleceMaxLessonId(user.getQid()), count);
						List<Integer> rec = recController.getRecByFuzzyItems(fuzzy, user.getUid());
						
						// 将推荐课程id的List转换成课程List，并将其加入回复
						List<Lesson> rec_final = new ArrayList<>();
						for(Integer i : rec) {
							rec_final.add(kbService.getLessonById(i));
						}
						answers.addAll(rec_final);
						
						// 如果最后获得的结果为空，则返回对应信息
						if(answers.size() == 2) {
							answers.clear();
							switch(num) {
							case 1:
								answers.add("亲，我们现在还未开设"+entity[0]+"课程，实在是不好意思呢~");
								break;
							case 2:
								if(firstEntityType.equals("lesson_lname"))
									answers.add("亲，我们开设的"+entity[0]+"课程都推荐给您了呢，您可以翻看上面的记录哦~");
								else if(entities.get(1).getEntity().equals("1"))
									answers.add("亲，"+entity[0]+"分部现在还未开设课程哦，实在是不好意思呢~");
								else 
									answers.add("亲，"+entity[0]+"分部开设的课程都推荐给您了呢，您可以翻看上面的记录哦~");
								break;
							case 3:
								if(entities.get(2).getEntity().equals("1"))
									answers.add("亲，"+entity[0]+"分部目前还未开设"+entity[1]+"课程哦，十分抱歉~");
								else
									answers.add("亲，"+entity[0]+"分部的"+entity[1]+"课程都推荐给您了呢，您可以翻看上面的记录哦~");
								break;
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case "None":
				answers.add(templet);
				break;
		}
		
		return answers;
	}
	
	public static void main(String[] args) {
		AnswerGenerator ag = new AnswerGenerator();
		Object[] obj = {"a","bbb","dd"};
		System.out.println(ag.fillIn("12?48?5", obj));
	}
}
