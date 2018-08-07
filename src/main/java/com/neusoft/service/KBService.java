package com.neusoft.service;

import java.util.List;
import java.util.Map;

import com.neusoft.po.Lesson;

public interface KBService {

	//public List<Lesson> getLessonByLike(String lname) throws Exception;

	public List<Lesson> getLessonByLike(Map<String, Object> map) throws Exception;
	
	public Integer selectUserNum(int qid) throws Exception;
	
	public int seleceMaxLessonId(int qid) throws Exception;
	
	public Lesson getLessonById(int lid) throws Exception;
}
