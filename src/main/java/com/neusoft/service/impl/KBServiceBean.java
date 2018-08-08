package com.neusoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.datasource.CustomerContextHolder;
import com.neusoft.mapper.KBMapper;
import com.neusoft.po.Lesson;
import com.neusoft.service.KBService;

@Service
public class KBServiceBean implements KBService {

	@Autowired
	private KBMapper kbMapper;
	
	@Override
	public List<Lesson> getLessonByLike(Map<String, Object> map) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE);
		//System.out.println(lname);
		List<Lesson> list = kbMapper.getLessonByLike(map);
		return list;
	}

	@Override
	public Integer selectUserNum(int qid) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE);
		
		return kbMapper.findUserNum(qid);
	}
	
	@Override
	public int seleceMaxLessonId(int qid) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE);
		
		return kbMapper.getMaxLessonId(qid);
	}

	@Override
	public Lesson getLessonById(int lid) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE);
		
		return kbMapper.getLessonById(lid);
	}
}
