package com.neusoft.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisTools {
	public static String RedisToFile(Jedis redis) throws IOException{
		File tempFile = File.createTempFile("temp", ".txt");
		tempFile.deleteOnExit();		
		String rate = redis.get("rate");
		FileWriter writter = new FileWriter(tempFile);
		writter.write(rate);
		writter.flush();
		writter.close();
		return tempFile.getCanonicalPath();
	}	
	public static void AppendToRedis(Jedis redis,String str){
		redis.append("rate", str);
	}
}
