package com.neusoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neusoft.mapper.CouponMapper;
import com.neusoft.po.Coupon;
import com.neusoft.po.User_coupon;
import com.neusoft.service.CouponService;
import com.neusoft.vo.CouponCondition;
@Service
public class CouponServiceBean implements CouponService {
	@Autowired
	private CouponMapper couponMapper;
	
	public List<Coupon> selectCouponByPage(int qid,Integer uid, Integer start, Integer length, String category, String status)
			throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("qid", qid);
		map.put("uid", uid);
		map.put("start", start);
		map.put("length", length);
		map.put("c_category", category);
		map.put("status", status);
		
	    return	couponMapper.findCouponByPage(map);
	}
	
	@Override
	public List<Coupon> selectCouponList(int qid, int start, int length, String category, String status)
			throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("qid", qid);
		map.put("start", start);
		map.put("length", length);
		map.put("c_category", category);
		map.put("c_status", status);
		return couponMapper.findCouponList(map);
	}
	
	@Override
	public boolean addUserCoupon(User_coupon uc) throws Exception {
		int result =couponMapper.addUserCoupon(uc);
		if(result>0){
			return true;
		}
		return false;
	}
	@Override
	public boolean updateUserInte(int uid, int num) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("denum", num);
		int result = couponMapper.updateUserInte(map);
		if(result>0)
			return true;
		return false;
	}
	
	public boolean addExchangeCoupon(User_coupon uc) throws Exception {
		int add = couponMapper.addUserCoupon(uc);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("uid", uc.getU_id());
		int num = couponMapper.getCouponById(uc.getC_id()).getChan_integral();
		map.put("denum", num);
		int update = couponMapper.updateUserInte(map);
		if(add>0 && update>0)
			return true;
		return false;
	}
	
	public boolean insertCoupon(Coupon coupon) throws Exception {
		int result = couponMapper.insertCoupon(coupon);
		if(result>0)
			return true;
		else
			return false;
	}
	
	@Override
	public List<Coupon> selectCouponByCondition(CouponCondition condition, int qid,int start,int length, String field ,String order) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		
		map.put("qid", qid);
		map.put("condition", condition);
		map.put("start", start);
		map.put("length", length);
		if(field!=null)
			field=field.toLowerCase();
		if(order!=null)
			order=order.toLowerCase();
		map.put("field", field);
		map.put("order", order);
		return couponMapper.findCouponByCondition(map);
	}
	@Override
	public int selectCountByCondition(int qid, CouponCondition condition) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("qid",qid);
		map.put("condition", condition);
		
		return couponMapper.countCouponByCondition(map);
	}
	@Override
	public boolean updateCouponDown(int c_id) throws Exception {
		int result = couponMapper.downCoupon(c_id);
		if(result>0)
			return true;
		return false;
	}
	@Override
	public Coupon selectCouponById(int c_id) throws Exception {
		return couponMapper.getCouponById(c_id);
	}
	@Override
	public boolean updateCoupon(int c_id, int totalnum, int chan_integral) throws Exception {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("c_id",c_id	);
		map.put("totalnum", totalnum);
		map.put("chan_integral", chan_integral);
		int result = couponMapper.updateCoupon(map);
		
		if(result>0)
			return true;
		return false;
	}
	
}
