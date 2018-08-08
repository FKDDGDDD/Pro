package com.neusoft.service;

import java.util.List;

public interface IntentService {

	public int getCount() throws Exception;
	
	public List<String> getTemplet(String intent, String entity_list) throws Exception;

	public List<String> getAnsList(String intent) throws Exception;

	public List<String> getWelcome() throws Exception;
}
