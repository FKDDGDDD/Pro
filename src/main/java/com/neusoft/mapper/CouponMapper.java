package com.neusoft.mapper;

import java.util.List;
import java.util.Map;

import com.neusoft.po.Coupon;
import com.neusoft.po.User_coupon;
import com.neusoft.vo.CouponCondition;

public interface CouponMapper {
	/*
	 * 前端
	 */
	//int start, num , status , qid
	public List<Coupon> findCouponByPage(Map<String, Object> map) throws Exception;
	
	public List<Coupon> findCouponList(Map<String, Object> map) throws Exception;
	//更新user_coupon
	public int addUserCoupon(User_coupon uc) throws Exception;
	//扣除一个人的积分
	public int updateUserInte(Map<String,Object> map) throws Exception;
	//更新某人的优惠券为已使用
	public int updateUseCoupon(int id) throws Exception;
	
	
	/*
	 * 后台
	 */
	//int qid , startdate, enddate , c_catagory, c_status, decrease , use_codition , chan_integral ,  totalNum
	public int insertCoupon(Coupon coupon) throws Exception;
	
	public Coupon getCouponById(int c_id) throws Exception;
	//排序查找
	//public List<Coupon> findSortCoupon(Map<String,Object> map) throws Exception;
	//qid
	public List<Coupon> findCouponByCondition(Map<String, Object> map) throws Exception;
	//找到某一条件下的优惠券数量
	public int countCouponByCondition(Map<String , Object> map) throws Exception;
	//下架某个优惠券
	public int downCoupon(int c_id) throws Exception;
	//更新某个优惠券qid ,totalnum , chan_integral
	public int updateCoupon(Map<String, Object> map) throws Exception;
	
	
}
