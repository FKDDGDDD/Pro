package com.neusoft.mapper;

import java.util.List;
import java.util.Map;

public interface IntentMapper {
	
	public int getIntentCount() throws Exception;
	
	public List<String> getTemplet(Map<String, Object> map) throws Exception;

	public List<String> getAnsList(String intent) throws Exception;

	public List<String> getWelcome() throws Exception;
}
