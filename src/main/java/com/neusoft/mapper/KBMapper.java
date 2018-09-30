package com.neusoft.mapper;

import java.util.List;
import java.util.Map;

import com.neusoft.po.Lesson;

public interface KBMapper {

	public List<Lesson> getLessonByLike(Map<String, Object> map) throws Exception;

	public Integer findUserNum(int qid) throws Exception;
	
	public int getMaxLessonId(int qid) throws Exception;
	
	public Lesson getLessonById(int lid) throws Exception;
}
