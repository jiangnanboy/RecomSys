package com.sy.zhihai.similarity;

import com.sy.zhihai.base.User;
import com.sy.zhihai.base.DataModel;

import java.util.Set;



/**
 * 皮儿逊相关计算类
 * @author yan.shi
 *@date： 日期：2016-10-17 时间：下午5:28:11
 */
public class PearsonSimilarity extends AbstractUserSimilarity{

	public PearsonSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	
	public double calculatePearsonSimilarity(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		int user1Size=user1.size();
		int user2Size=user2.size();
		if(user1Size==0 || user2Size==0){
			return 0.0;
		}
		//两个用户共同评过分的项目交集
		Set<String> interItems=user1Size>user2Size?user1.interSectionItem(user2):user2.interSectionItem(user1);
		int commonItems=interItems.size();//共同项目个数
		if(commonItems==0 || commonItems==user2Size){
			return 0.0;
		}
		//内积
		double innerProduct=user1Size>user2Size?calInnerProduct(userID1,userID2):calInnerProduct(userID2,userID1);
		double user1Sum=0.0;
		double user1Square=0.0;
		double user2Sum=0.0;
		double user2Square=0.0;
		for(String itemID:interItems){
			double user1ItemRating=user1.getItemRating(itemID);
			double user2ItemRating=user2.getItemRating(itemID);
			user1Sum+=user1ItemRating;
			user1Square+=Math.pow(user1ItemRating, 2);
			user2Sum+=user2ItemRating;
			user2Square+=Math.pow(user2ItemRating, 2);
		}
		double denominator=Math.sqrt(user1Square-Math.pow(user1Sum, 2)/(double)commonItems)*Math.sqrt(user2Square-Math.pow(user2Sum, 2)/(double)commonItems);
		double numerator=innerProduct-user1Sum*user2Sum/(double)commonItems;
		double sim=0.0;
		if(denominator!=0.0)
			sim=numerator/denominator;
		return sim;
	}

	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculatePearsonSimilarity(userID1,userID2);
	}

}
