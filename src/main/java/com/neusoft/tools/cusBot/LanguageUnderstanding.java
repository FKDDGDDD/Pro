package com.neusoft.tools.cusBot;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("deprecation")
public class LanguageUnderstanding {

	@SuppressWarnings({ "resource" })
	public LUISresponse extractIntentAndEntities(String str) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		
		// 调用LUIS的api，获得意图和实体
		String url = "https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/6244b2d3-3d21-4c78-8ce2-7d3c6a2c4b8f?"
				+ "subscription-key=e65143a9d63445778eb087a937689791&verbose=true&timezoneOffset=0&q=" + str;		
        HttpGet getMethod = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(getMethod);
        HttpEntity response_entity = httpResponse.getEntity();
        String response = EntityUtils.toString(response_entity,"utf-8");
        
        // 利用fastjson解析成自定义response对象
        LUISresponse luisResponse = JSON.parseObject(response, LUISresponse.class);
//		System.out.println("intent: " + luisResponse.getTopScoringIntent().getIntent());
//		for(Entity e : luisResponse.getEntities()) {
//			System.out.println("entity: " + e.getEntity() + "\t|\ttype: " + e.getType());
//		}
		return luisResponse;
	}

//	public static void main(String[] args) throws Exception {
//		CusBot bot = new CusBot();
//		bot.test();
////		LUISresponse response = bot.extractIntentAndEntities("我对java很感兴趣");
////		AnswerGenerator ag = new AnswerGenerator();
////		ag.setLUISresponse(response);
////		List<String> ans = ag.fillInTemplet();
////		for(String s : ans) {
////			System.out.println(s);
////		}
////		try {
////			bot.extractIntentAndEntities("恒大名都的人工智能导论课多少钱");
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//		//bot.testDB();
//		//System.out.println(ag.getTemplet());
//	}

}
