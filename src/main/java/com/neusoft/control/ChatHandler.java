package com.neusoft.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.neusoft.po.User;
import com.neusoft.service.IntentService;
import com.neusoft.service.KBService;
import com.neusoft.tools.cusBot.AnswerGenerator;
import com.neusoft.tools.cusBot.ContextStore2;
import com.neusoft.tools.cusBot.LanguageUnderstanding;
import com.neusoft.tools.cusBot.LUISresponse;
import com.neusoft.tools.speechRecognition.SpeechRecog;

@Controller
public class ChatHandler {

	@Autowired
	private IntentService intentService;
	@Autowired
	private KBService kbService;
	
	private SpeechRecog sr = new SpeechRecog();
	private LanguageUnderstanding lu = new LanguageUnderstanding();
	private AnswerGenerator ag;
	private Map<Integer, ContextStore2> css = new HashMap<Integer, ContextStore2>();
	
	@RequestMapping(value="/FrontEnd/chat/reply")
	@ResponseBody
	public List<Object> reply(HttpServletRequest request) {
		System.out.println(".......ChatHandler............reply().....");
		System.out.println(request.getParameter("msg"));
		HttpSession session = request.getSession();
		int uid = (int) session.getAttribute("uid");
		System.out.println(uid);
//		String nickname = (String) session.getAttribute("nickname");
		
		// 造的数据
		User user = new User();
		user.setNickName("贤子");
		user.setUid(3);
		user.setQid(1);
		
		ag = new AnswerGenerator(intentService, kbService);		
		String msg = request.getParameter("msg");
		List<Object> ans = new ArrayList<>();
		try {
			msg = URLEncoder.encode(msg, "utf-8");
			// 调用语义理解器处理消息，得到意图跟实体
			LUISresponse response = lu.extractIntentAndEntities(msg);
			
			// 调用上下文管理器，得到更新后的response
			if(css.get(uid) == null) {
				System.out.println(uid + "\t初始化");
				ContextStore2 cs = new ContextStore2(intentService);
				css.put(uid, cs);
				cs.updateWithoutCheck(response);
			}
			else {
				css.get(uid).update(response);
			}
			// 调用答案生成器，生成答案
			ag.setLUISresponse(css.get(uid).getCurrentResponse());
			ans = ag.fillInTemplet(user);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ans;
	}
	
	@RequestMapping(value="/FrontEnd/chat/process")
	public void getSpeechFile(HttpServletRequest request, HttpServletResponse response, MultipartFile speech, String filename) throws Exception{
		System.out.println("...........Handler.........addImgToSwiperByQid.........");
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		byte[] b = speech.getBytes();
		//String fileName = filename + ".wav";
		String fileName = makeFile(b, filename);
		String text = new String(sr.recognize(fileName), "UTF-8");
		StringBuilder textBuilder = new StringBuilder(text);
		String text2 = textBuilder.substring(0,text.length()-1).toString();
		response.getWriter().println(text2);
	}

	
	/** 
     * 根据byte数组，生成文件 
	 * @throws IOException 
     */  
    protected String makeFile(byte[] bfile, String fileName) throws IOException {  
	        BufferedOutputStream bos = null;  
	        FileOutputStream fos = null;  
      		File file = null;  
      
            //File dir = new File(filePath);  
             file = File.createTempFile("fileName", ".wav");
             file.deleteOnExit();
          /*  if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+"\\"+fileName);  */
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
            bos.close();
            return file.getCanonicalPath();

    }  
	
	@RequestMapping(value="/FrontEnd/chat/getWelcome")
	@ResponseBody
	public String getWelcome(HttpServletRequest request){
		HttpSession session = request.getSession();
		int uid = (int) session.getAttribute("uid");
		System.out.println(uid);
//		String nickname = (String) session.getAttribute("nickname");
		
		// 造的数据
		User user = new User();
		user.setNickName("贤子");
		user.setUid(3);
		user.setQid(1);
		
		ag = new AnswerGenerator();
		String welcome = "";
		
		//取出欢迎模板集合，并随机选择一个模板
		List<String> welcomeList;
		try {
			welcomeList = intentService.getWelcome();
			Collections.shuffle(welcomeList);
			welcome = welcomeList.get(0);
			
			// 填注模板
			Object[] obj = {user.getNickName()};
			welcome = ag.fillIn(welcome, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return welcome;
	}
}