package com.neusoft.service;

import java.util.List;

import com.neusoft.po.Coupon;
import com.neusoft.po.User_coupon;
import com.neusoft.vo.CouponCondition;

public interface CouponService {
	
	public List<Coupon> selectCouponByPage(int qid,Integer uid,Integer start , Integer length, String category , String status) throws Exception;
	
	public List<Coupon> selectCouponList(int qid,int start , int length, String category , String status) throws Exception;
	
	public boolean addUserCoupon(User_coupon uc) throws Exception;
	
	public boolean updateUserInte(int uid,int num) throws Exception;
	
	public boolean addExchangeCoupon(User_coupon uc) throws Exception;
	
	public boolean insertCoupon(Coupon coupon) throws Exception;
	//通过条件查找相应页数的Coupon
	public List<Coupon> selectCouponByCondition(CouponCondition condition ,int qid,int start,int length, String field ,String order) throws Exception;
	//排序查找
	//public List<Coupon> selectSortCoupon(CouponCondition condition , int qid ,int start , int length , String field ,String order) throws Exception;
	//找到某一条件的coupon数目
	public int selectCountByCondition(int qid,CouponCondition condition) throws Exception;
	//下架某一款产品
	public boolean updateCouponDown(int c_id) throws Exception;
	//修改某个产品的totalnum,chan_integral
	public boolean updateCoupon(int c_id,int totalnum,int chan_integral) throws Exception;
	//通过id找Coupon
	public Coupon selectCouponById(int c_id) throws Exception;
		
}
