package com.neusoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.datasource.CustomerContextHolder;
import com.neusoft.mapper.IntentMapper;
import com.neusoft.service.IntentService;

@Service
public class IntentServiceBean implements IntentService {

	@Autowired
	private IntentMapper intentMapper;
	
	@Override
	public int getCount() throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE2);
		return intentMapper.getIntentCount();
	}

	@Override
	public List<String> getTemplet(String intent, String entity_list) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE2);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("intent", intent);
		if(entity_list != "")
			map.put("entity_list", entity_list);
		List<String> templet = intentMapper.getTemplet(map);
		
		return templet;
	}

	@Override
	public List<String> getAnsList(String intent) throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE2);
		return intentMapper.getAnsList(intent);
	}

	@Override
	public List<String> getWelcome() throws Exception {
		CustomerContextHolder.setCustomerType(CustomerContextHolder.DATA_SOURCE2);
		return intentMapper.getWelcome();
	}
}
