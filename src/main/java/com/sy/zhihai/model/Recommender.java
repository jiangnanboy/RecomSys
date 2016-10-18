package com.sy.zhihai.model;

import com.sy.zhihai.base.RecommendedItem;
import com.sy.zhihai.base.SimilarityUser;
import com.sy.zhihai.base.User;
import com.sy.zhihai.similarity.AbstractUserSimilarity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;



/**
 * 产生推荐项目类
 * @author yan.shi
 *@date： 日期：2016-10-14 时间：下午3:45:22
 */
public class Recommender {
	//数据
	DataModel dataModel;
	//相似计算类
	AbstractUserSimilarity userSimilarity;
	//近邻类
	Neighborhood neighborhood;
	//推荐的项目及评分
	List<RecommendedItem> recommendedItems;
	public Recommender(DataModel dataModel,AbstractUserSimilarity userSimilarity,Neighborhood neighborhood){
		this.dataModel=dataModel;
		this.userSimilarity=userSimilarity;
		this.neighborhood=neighborhood;
	}
	/**
	 * 产生推荐项目
	 * @param userID 为此用户推荐
	 * @param recomCount 推荐项目的个数
	 */
	public List<RecommendedItem> recommend(int userID,int recomCount){
		recommendedItems=new ArrayList<RecommendedItem>();
		neighborhood.cal_TheNearestNeighborhood(String.valueOf(userID));//计算最近邻
		predictRating(String.valueOf(userID),recomCount);
		return recommendedItems;
	}
	
	/**
	 * 对userID未评过分的项目进行预测打分
	 * 标签预测打分公式：R(u1,i)=mean(ru1)+(sum(sim(u1,u2)(R(u1,i)-mean(ru2))))/(sum(sim(u1,u2))
	 * @param userID
	 * @param recomCount
	 */
	private void predictRating(String userID,int recomCount){
		Map<String,Double> predictItemRating=new HashMap<String,Double>();//存放项目id及预测分值
		User thisUser=dataModel.getThisUser(userID);//获取该userID用户类
		double thisUserMeanRating=thisUser.getItemMeanRating();//该用户的评分均值
		Set<String> unRatingItems=userSimilarity.getAllUnRatingItem(userID);//所有该用户未评过分的项目id
		
		List<SimilarityUser> similarityUser=neighborhood.getTheNearestNeightborhood();//得到该用户最近邻居
		
		for(String unItem:unRatingItems){
			double simValueRatingMean=0.0;//分子部分
			double simValueSum=0.0;//分母部分
			for(SimilarityUser simUser:similarityUser){
				User user2=dataModel.getThisUser(simUser.getUserID());
				if(user2.containItemID(unItem)){
					simValueRatingMean+=simUser.getSimilarityValue()*(user2.getItemRating(unItem)-user2.getItemMeanRating());
					simValueSum+=simUser.getSimilarityValue();
				}
			}
			if(simValueSum>0){
				double itemRating=thisUserMeanRating+simValueRatingMean/simValueSum;
				predictItemRating.put(unItem, itemRating);
			}
		}
		
		//排序
		List<Entry<String, Double>> list_Data=new ArrayList<Entry<String,Double>>
		(predictItemRating.entrySet());
		Collections.sort(list_Data,new Comparator<Entry<String, Double>>(){
			
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				// TODO Auto-generated method stub
				if(o1.getValue()>o2.getValue()){
					return -1;
				}else if(o1.getValue()<o2.getValue()){
					return 1;
				}else{
					return 0;
				}
			}
			
		});
		//将前nearestNum个最相似用户存入nearestUsers中
		if(list_Data.size()>0){
			for(Entry<String, Double> entry:list_Data){
				RecommendedItem recItem=new RecommendedItem(entry.getKey(),entry.getValue());
				recommendedItems.add(recItem);
				recomCount--;
				if(recomCount==0) break;
			}
		}
		
	}
	
}

