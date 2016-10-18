package com.sy.zhihai.base;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * 项目类
 * @author yan.shi
 *@date： 日期：2016-10-11 时间：上午10:52:38
 */
public class Item {
	
	/**项目id**/
	private String id;
	/**每个用户id对该项目的评分**/
	private Map<String, Double> ratingsByUser;
	
	public Item(String id){
		this.id=id;
		ratingsByUser=new HashMap<String,Double>();
	}
	/**
	 * 获取项目id
	 * @return
	 */
	public String getItemID(){
		return id;
	}
	/**
	 * 获得所有的userID
	 * @return
	 */
	public Set<String> getAllUserID(){
		return ratingsByUser.keySet();
	}
	/**
	 * 将用户id及评分存入ratingsByUser中
	 * @param userID
	 * @param rating
	 */
	public void putUserIDRating(String userID,double rating){
		this.ratingsByUser.put(userID, rating);
	}
	
	/**
	 * 返回此项目的所有用户及评分
	 * @return
	 */
	public Map<String,Double> getUserRatingsVector(){
		return this.ratingsByUser;
	}
	/**
	 * 返回该user的评分
	 * @param userID
	 * @return
	 */
	public double getUserRating(String userID){
		return ratingsByUser.get(userID);
	}
	/**
	 * 是否包含该user
	 * @param userID
	 * @return
	 */
	public boolean containUserID(String userID){
		return ratingsByUser.containsKey(userID);
	}
	/**
	 * 返回对此项目评过分的用户数
	 * @return
	 */
	public int size(){
		return this.ratingsByUser.size();
	}
	/**
	 * 计算两个项目的用户交集
	 * @param theSameUserRating
	 * @return
	 */
	public int intersectionSize(Map<String,Double> theSameUserRating){
		int interSize=0;
		for(String userID:theSameUserRating.keySet()){
				if(this.containUserID(userID)){
					interSize++;
				}
		}
		return interSize;
	}
	/**
	 * 计算两个项目的用户交集
	 * @param item
	 * @return
	 */
	public int intersectionSize(Item item){
		int interSize=0;
		for(String userID:item.getAllUserID()){
			if(this.containUserID(userID)){
				interSize++;
			}
		}
		return interSize;
	}
	/**
	 * 返回该项目被所有用户评分和的均值
	 * @return
	 */
	public double getUserMeanRating(){
		double meanRating=0.0;
		for(Map.Entry<String, Double> entry:ratingsByUser.entrySet()){
			meanRating+=entry.getValue();
		}
		meanRating/=(double)ratingsByUser.size();
		return meanRating;
	}

}
