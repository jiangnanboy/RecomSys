package com.sy.zhihai.base;

/**
 * 相似用户类
 * @author yan.shi
 *@date： 日期：2016-10-13 时间：下午1:36:09
 */
public class SimilarityUser {
	//相似用户的id
	String userID;
	//用户的相似值
	double similarityValue;
	public SimilarityUser(String userID,double value){
		this.userID=userID;
		this.similarityValue=value;
	}
	/**
	 * 得到用户id
	 * @return
	 */
	public String getUserID(){
		return this.userID;
	}
	/**
	 * 得到相似用户值
	 * @return
	 */
	public double getSimilarityValue(){
		return this.similarityValue;
	}
	
}
