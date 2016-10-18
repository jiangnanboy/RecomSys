package com.sy.zhihai.similarity;

import com.sy.zhihai.base.SimilarityUser;
import com.sy.zhihai.base.User;
import com.sy.zhihai.model.DataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * 相似计算的抽象类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:11:17
 */
public abstract class AbstractUserSimilarity{
	protected DataModel dataModel;
	List<SimilarityUser> nearestUsers;//存储最近邻
	int commonItemCount=0;//共同评分的项目数，作为阈值
	public AbstractUserSimilarity(DataModel dataModel){
		this.dataModel=dataModel;
	}
	public void setCommonItemCount(int commonItem){
		this.commonItemCount=commonItem;
	}
	/**计算最近邻居
	 * (non-Javadoc)
	 * @see com.roboo.sy.zhihai.similarity.AbstractSimilarity#cal_NearestUsers(String, int)
	 */
	public void cal_NearestUsers(String userID,int nearestNum){
		nearestUsers=new ArrayList<SimilarityUser>();
		Map<String,Double> simUsersValueMap=new HashMap<String,Double>();//存储用户及相似度
		for(String userID2:dataModel.getUserMap().keySet()){
			if(!userID.equals(userID2)){
				double simValue=calSimilarity(userID,userID2);
				if(simValue>0.0){
					simUsersValueMap.put(userID2, simValue);
				}
			}
		}
		
		List<Entry<String, Double>> list_Data=new ArrayList<Entry<String,Double>>
		(simUsersValueMap.entrySet());
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
				SimilarityUser simUser=new SimilarityUser(entry.getKey(),entry.getValue());
				nearestUsers.add(simUser);
				nearestNum--;
				if(nearestNum==0) break;
			}
		}
		
	}
	
	/**
	 * 得到最近邻用户
	 * @return List<SimilarityUser>
	 */
	public List<SimilarityUser> getNearestSimilarityUser(){
		return this.nearestUsers;
	}
	
	/**
	 * 返回该用户所有未评过分的项目id
	 * @return
	 */
	public Set<String> getAllUnRatingItem(String userID){
		Set<String> allUnRatingItem=new HashSet<String>();//存放该用户未评过分的项目id
		User user1=dataModel.getThisUser(userID);
	    for(SimilarityUser similarityUser:nearestUsers){
	    	User user2=dataModel.getThisUser(similarityUser.getUserID());
	    	for(String itemID:user2.getAllItemID()){
	    		if(!user1.containItemID(itemID)){
	    			if(allUnRatingItem.contains(itemID)) continue;
	    			allUnRatingItem.add(itemID);
	    		}
	    	}
	    }
		
		return allUnRatingItem;
	}


	public abstract double calSimilarity(String userID1, String userID2);
	
	/**
	 * 两个向量内积
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calInnerProduct(String userID1, String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		Set<String> interItems=user1.interSectionItem(user2);//获得交集
		if(interItems.size()==0) return 0.0;
		double innerProduct=0.0;//内积
		for(String itemID:interItems){
			innerProduct+=user1.getItemRating(itemID)*user2.getItemRating(itemID);
		}
		return innerProduct;
	}
	/**
	 * 减去均值的内积
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calInnerProductMean(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		Set<String> interItems=user1.interSectionItem(user2);//获得交集
		double innerProduct=0.0;
		for(String itemID:interItems){
			innerProduct+=((user1.getItemRating(itemID)-user1.getItemMeanRating())*(user2.getItemRating(itemID)-user2.getItemMeanRating()));
		}
		return innerProduct;
	}
	
}
