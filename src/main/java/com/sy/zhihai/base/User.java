package com.sy.zhihai.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户类
 * @author yan.shi
 *@date： 日期：2016-10-11 时间：上午10:52:26
 */
public class User {
	
	/**用户id**/
	private String id;
	/**项目id及用户对其的评分**/
	private Map<String,Double> ratingsByItem;
	
	public User(String id){
		this.id=id;
		ratingsByItem=new HashMap<String,Double>();
	}
	/**
	 * 获取用户id
	 * @return
	 */
	public  String getUserID(){
		return id;
	}
	/**
	 * 得到用户评分向量模长
	 * @return
	 */
	public double getUserVectorLength(){
		double vectorLength=0.0;
		for(double value:ratingsByItem.values()){
			vectorLength+=Math.pow(value, 2);
		}
		return vectorLength;
	}
	/**
	 * 用户评分减去所有项目评分均值的模长
	 * @return
	 */
	public double getUserVectorMeanLength(){
		double vectorLength=0.0;
		double meanRating=getItemMeanRating();
		for(double value:ratingsByItem.values()){
			vectorLength+=Math.pow((value-meanRating), 2);
		}
		return vectorLength;
	}
	/**
	 * 获得所有的itemID
	 * @return
	 */
	public Set<String> getAllItemID(){
		return ratingsByItem.keySet();
	}
	/**
	 * 将项目id及评分存入ratingsByItem中
	 * @param itemID
	 * @param rating
	 */
	public void putItemIdRating(String itemID,double rating){
		this.ratingsByItem.put(itemID, rating);
	}
	/**
	 * 返回此用户评分过的所有项目及评分
	 * @return
	 */
	public Map<String,Double> getItemRatingsVector(){
		return this.ratingsByItem;
	}
	/**
	 * 返回该item的评分
	 * @param itemID
	 * @return
	 */
	public double getItemRating(String itemID){
		return ratingsByItem.get(itemID);
	}
	/**
	 * 是否包含该item
	 * @param itemID
	 * @return
	 */
	public boolean containItemID(String itemID){
		return ratingsByItem.containsKey(itemID);
	}
	/**
	 * 返回此用户评过分的项目数
	 * @return
	 */
	public int size(){
		return this.ratingsByItem.size();
	}
	/**
	 * 计算两个用户的项目交集
	 * @param theSameItemRating
	 * @return
	 */
	public int interSectionSize(Map<String,Double> theSameItemRating){
		int interSize=0;
		for(String itemID:theSameItemRating.keySet()){
				if(this.containItemID(itemID)){
					interSize++;
				}
		}
		return interSize;
	}
	/**
	 * 计算两个用户的项目交集
	 * @param user
	 * @return int
	 */
	public int interSectionSize(User user){
		int interSize=0;
		for(String itemID:user.getAllItemID()){
			if(this.containItemID(itemID)){
				interSize++;
			}
		}
		return interSize;
	}
	/**
	 * 计算两个用户的交集项目
	 * @param user
	 * @return
	 */
	public Set<String> interSectionItem(User user){
		Set<String> interItem=new HashSet<String>();
		for(String itemID:user.getAllItemID()){
			if(this.containItemID(itemID)){
				interItem.add(itemID);
			}
		}
		return interItem;
	}
	/**
	 * 得到该用户所有项目评分和的均值
	 * @return
	 */
	public double getItemMeanRating(){
		double meanRating=0.0;
		for(Map.Entry<String, Double> entry:ratingsByItem.entrySet()){
			meanRating+=entry.getValue();
		}
		meanRating/=(double)ratingsByItem.size();
		return meanRating;
	}
	
}
