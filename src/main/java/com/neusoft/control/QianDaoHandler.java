package com.neusoft.control;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neusoft.po.IntegralValue;
import com.neusoft.po.QianDao;
import com.neusoft.service.QianDaoService;
import com.neusoft.vo.UserVo;

 
@Controller
public class QianDaoHandler {

	@Autowired
	private QianDaoService qianDaoService;
	
	@RequestMapping(value="/test/QianDao/insertDateUserID")
	@ResponseBody
	public String insertDateUserID(HttpServletRequest request,String date) throws Exception{
		System.out.println("...........QianDaoHandler............insertDateUserID..........");
		HttpSession session = request.getSession();
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		QianDao qianDao = new QianDao();
		qianDao.setQid(qid);
		qianDao.setDate(date);
		qianDao.setUid(uid);	
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		Integer findnum = qianDaoService.findByUserIDQidDate(qianDao);		
		if(findnum==null){
			int insertnum = qianDaoService.insertDateUserID(qianDao);
			int updatenum = qianDaoService.addIntegralByUid(userVo);
			if(insertnum>0){
				return "{\"result\":true}";
			}	
		}
		return "{\"result\":false}";
	}

	
	@RequestMapping(value="/test/QianDao/findByUserIDQidDate")
	@ResponseBody
	public String findByUserIDQidDate(HttpServletRequest request,String date) throws Exception{
		System.out.println("...........QianDaoHandler.........findByUserIDQidDate.......");
		HttpSession session = request.getSession();
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		QianDao qianDao = new QianDao();
		qianDao.setQid(qid);
		qianDao.setDate(date);
		qianDao.setUid(uid);
		Integer findnum = qianDaoService.findByUserIDQidDate(qianDao);
		if(findnum==null){
			return "{\"result\":\"empty record\"}";
		}else{
			return "{\"result\":\"has record\"}";
		}
	}
	
//	@RequestMapping(value="/test/QianDao/addIntegralByUid")
//	@ResponseBody
//	public String addIntegralByUid(HttpServletRequest request) throws Exception{
//		System.out.println("..............QianDaoHandler..........addIntegralByUid...........");
//		HttpSession session = request.getSession();
//		int qid = 1;
//		int uid = 1;
////		int qid = (int) session.getAttribute("qid");
////		int uid = (int) session.getAttribute("uid");
//		UserVo userVo = new UserVo();
//		userVo.setQid(qid);
//		userVo.setUid(uid);
//		int updatenum = qianDaoService.addIntegralByUid(userVo);
//		if(updatenum>0){
//			return "{\"result\":true}";
//		}else{
//			return "{\"result\":false}";
//		}
//	}
	
	@RequestMapping(value="/test/QianDao/BuQianByDateUidQid")
	@ResponseBody
	public String BuQianByDateUidQid(HttpServletRequest request, String date) throws Exception{
		System.out.println("........QianDaoHandler........decreaseIntegralByUidQid............");
		HttpSession session = request.getSession();
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		QianDao qianDao = new QianDao();
		qianDao.setDate(date);
		qianDao.setUid(uid);
		qianDao.setQid(qid);
		Integer num = qianDaoService.findByUserIDQidDate(qianDao);
		//某天没有签到
		if(num == null){
			int integral = qianDaoService.findIntegralByUidQid(userVo);
			if(integral>qianDaoService.getBuIntegral(qid)){
				int insertnum = qianDaoService.insertDateUserID(qianDao);
				int updatenum = qianDaoService.addIntegralByUid(userVo);
				int integralnum = qianDaoService.decreaseIntegralByUidQid(userVo);
				return "{\"result\":\"success\"}";
			}else{
				return "{\"result\":\"not enough integral\"}";
			}
		}
		return "{\"result\":false}";
	}
	
	@RequestMapping(value="/test/QianDao/continueQianDao")
	@ResponseBody
	public int continueQianDao(HttpSession session, String date) throws Exception{
		System.out.println("............QianDaoHandler..........continueQianDao............");
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		QianDao qianDao = new QianDao();
		qianDao.setDate(date);
		qianDao.setUid(uid);
		qianDao.setQid(qid);
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		List<String> datelist = qianDaoService.findDateByUidQid(qianDao);
		ArrayList<Calendar> data1 = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		Date endDate = (Date) dateFormat.parse(date);
		calendar.setTime(endDate);
		data1.add(calendar);
		for(String currentdate : datelist){
			Date current = (Date) dateFormat.parse(currentdate);
			Calendar currentcalendar = Calendar.getInstance();
			currentcalendar.setTime(current);
			data1.add(currentcalendar);
		}
		Collections.sort(data1, new CalendarComparator());
//		for(Calendar calendar2 : data1){
//			System.out.println(calendar2);
//		}
//		return data1;
		int count =1;
		for(int i= data1.size()-1; i>0;i--){
			System.out.println((data1.get(i).getTimeInMillis() - data1.get(i-1).getTimeInMillis())/(1000*60*60*24));
			
				if((data1.get(i).getTimeInMillis() - data1.get(i-1).getTimeInMillis())/(1000*60*60*24)<=1){
					count ++;
				}else{					
					break;
				}
			
		}
		if(count>7 && count%7!=0){
			return count;
		}
		else if(count%7==0){
			if(qianDaoService.findByUserIDQidDate(qianDao)==null){
				int updatenum = qianDaoService.increaseIntegralByUidQid(userVo);
				return count;
			}else{
				return count;
			}
		}
		System.out.println(count);
		return count;
	}

	
	@RequestMapping(value="/test/QianDao/findIntegralByQidUid")
	@ResponseBody
	public int findIntegralByQidUid(HttpServletRequest request) throws Exception{
		System.out.println("........QianDaoHandler..........findIntegralByQidUid.........");
		HttpSession session = request.getSession();
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		int integral = qianDaoService.findIntegralByUidQid(userVo);
		return integral;
	}
	
	@RequestMapping(value="/test/QianDao/findDateAll")
	@ResponseBody
	public List<String> findDateAll(HttpServletRequest request) throws Exception{
		System.out.println(".......QianDaoHandler......findDateAll..........");
		HttpSession session = request.getSession();
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		List<String> lists = qianDaoService.findDateAll(userVo);
		return lists;
	}
	
	@RequestMapping(value="/test/QianDao/getIntegral")
	@ResponseBody
	public IntegralValue getIntegral(HttpSession	session) throws Exception{
		System.out.println(".........QianDaoHandler.........getIntegral............");
		int qid = (int)session.getAttribute("qid");
		IntegralValue integralValue = new IntegralValue();
		integralValue.setOneintegral(qianDaoService.getOneIntegral(qid));
		integralValue.setBuintegral(qianDaoService.getBuIntegral(qid));
		integralValue.setSevenintegral(qianDaoService.getSevenIntegral(qid));
		return integralValue;
	}
	
	@RequestMapping(value="/test/QianDao/updateIntegral")
	@ResponseBody
	public String updateIntegral(HttpSession session,Integer oneintegral, Integer buintegral, Integer sevenintegral) throws Exception{
		System.out.println("..........QianDaoHander.........updateIntegral..........");
		int qid = (int)session.getAttribute("qid");
		IntegralValue integralValue = new IntegralValue();
		integralValue.setOneintegral(oneintegral);
		integralValue.setBuintegral(buintegral);
		integralValue.setSevenintegral(sevenintegral);
		integralValue.setQid(qid);
		int updatenum = qianDaoService.updateIntegral(integralValue);
		if(updatenum>0){
			return "{\"result\":true}";
		}
		return "{\"result\":false}";
	}
	
	static class CalendarComparator implements Comparator{
		public int compare(Object object1, Object object2){
			Calendar c1 = (Calendar) object1;
			Calendar c2 = (Calendar) object2;
			return c1.compareTo(c2);
		}
	}
	
	@RequestMapping(value="/test/QianDao/increaseIntegralByUidQid")
	@ResponseBody
	public String increaseIntegralByUidQid(HttpSession session)throws Exception{
		System.out.println("..........QianDaoHandler.........increaseIntegralByUidQid.......");
		
		int qid = (int) session.getAttribute("qid");
		int uid = (int) session.getAttribute("uid");
		UserVo userVo = new UserVo();
		userVo.setQid(qid);
		userVo.setUid(uid);
		Integer updatenum = qianDaoService.increaseIntegralByUidQid(userVo);
		if(updatenum>0){
			return "{\"result\":true}";
		}
		return "{\"result\":false}";
	}

}
