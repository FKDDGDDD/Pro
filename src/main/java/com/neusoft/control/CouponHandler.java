package com.neusoft.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.neusoft.po.Coupon;
import com.neusoft.po.User_coupon;
import com.neusoft.service.CouponService;
import com.neusoft.vo.CouponCondition;

@Controller
public class CouponHandler {
	@Autowired
	private CouponService couponService;
	/*
	 * 前端操作
	 */
	//	在个人卡包里查找
	@RequestMapping(value="/coupon/getCouponList")
	@ResponseBody
	public List<Coupon> selectPersonCouponByPage(Integer code,Integer from,Integer count,HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession();
		int qid = (int)session.getAttribute("qid");
		int uid = (int)session.getAttribute("uid");
		String category=null;
		switch(code){
			case 0: category = null;
					break;
			case 1: category = "JAVA";
					break;
			case 2: category = "HTML5";
					break;
			case 3: category = "C++";
					break;
			case 4: category = "Python";
					break;
			case 5: category = "ALL";
					break;
		}
		List<Coupon> list =  couponService.selectCouponByPage(qid,uid, from, count, category, "未使用");
		
		return list;
	}
	//在商城中查找优惠券
	@RequestMapping(value = "/coupon/getCouponStore")
	@ResponseBody
	public List<Coupon> selectCouponByPage(Integer code,Integer from,Integer count,HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession();
		int qid = (int)session.getAttribute("qid");
		String category=null;
		switch(code){
			case 0: category = null;
					break;
			case 1: category = "JAVA";
					break;
			case 2: category = "HTML5";
					break;
			case 3: category = "C++";
					break;
			case 4: category = "Python";
					break;
			case 5: category = "ALL";
					break;
		}
		List<Coupon> list =  couponService.selectCouponList(qid, from, count, category, "可兑换");
		System.out.print(from+"*************");
		
		return list;
	}
	//兑换优惠券
	@RequestMapping(value = "/coupon/exchangeCoupon")
	@ResponseBody
	public boolean exchangeCoupon(User_coupon uc,HttpSession session) throws Exception{
		int uid = (int)session.getAttribute("uid");
		uc.setU_id(uid);
		uc.setStatus("未使用");
		if(couponService.addExchangeCoupon(uc))
			return true;
		return false;
	}
	
	/*
	 * 后台操作
	 */
	
	@RequestMapping(value="/BackEnd/coupon/getCoupons")
	@ResponseBody
	public Map<String,Object> selectCouponByCondition(HttpSession session,Integer page,Integer limit,
			CouponCondition condition, String field ,String order) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		int qid = (int)session.getAttribute("qid");
		//计算start和length
		page = page==null?1:page;
		int start = (page-1)*limit;
		int count = couponService.selectCountByCondition(qid, condition);
		List<Coupon> list = couponService.selectCouponByCondition(condition, qid, start, limit,field,order);
		JSONArray array = JSONArray.parseArray(JSON.toJSONString(list));
		map.put("code", 0);
		map.put("data", array);
		map.put("msg", "");
		map.put("count", count);
		return map;
	}
	
	@RequestMapping(value="/BackEnd/coupon/getSortCoupons")
	@ResponseBody
	public boolean selectSortCoupons(Integer page,Integer limit,CouponCondition condition,String field,String order) throws Exception{
		Gson gson = new Gson();
		String con = gson.toJson(condition);
		System.out.println(con);
		System.out.println(field+"    "+order);
		
		return false;
	}
	//下架商品
	@RequestMapping(value="/BackEnd/coupon/downCoupon")
	@ResponseBody
	public boolean downCoupon(Integer c_id) throws Exception{
		boolean result = couponService.updateCouponDown(c_id);
		return result;
	}
	
	//修改某个商品的totalnum和chan_integral
	@RequestMapping(value="/BackEnd/coupon/updateCoupon")
	@ResponseBody
	public Map<String,Object> updateCoupon(int c_id,int totalnum , int chan_integral) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		
		boolean result=false;
		Coupon old = couponService.selectCouponById(c_id);
		int remain = totalnum-old.getTotalnum()+old.getRemain();

		//如果totalnum小于已发布出去的数量
		if(remain<0){
			result=false;	
			map.put("remain", old.getRemain());
			map.put("totalnum", old.getTotalnum());
			map.put("chan_integral", old.getChan_integral());
		}else{
			result=true;
			result = couponService.updateCoupon(c_id, totalnum, chan_integral);			
			map.put("remain", remain);
			map.put("totalnum", totalnum);
			map.put("chan_integral", chan_integral);
		}
		map.put("msg", result);
		return map;
	}
	
	@RequestMapping(value="/BackEnd/coupon/insert")
	@ResponseBody
	public boolean addCoupon(Coupon coupon,HttpSession session) throws Exception{
		//Map<String,Object> map = new HashMap<String, Object>();
		int qid = (int)session.getAttribute("qid");
		coupon.setQid(qid);
		boolean result = couponService.insertCoupon(coupon);
		
		return result;
	}
}
