package com.sy.zhihai.similarity;

import com.sy.zhihai.base.User;
import com.sy.zhihai.model.DataModel;

import java.util.Set;


/**
 * 修正的欧氏相似计算类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:12:04
 */
public class AdjustedEuclideanSimilarity extends AbstractUserSimilarity{

	public AdjustedEuclideanSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 改进的欧氏相似度
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calculateEuclideanSimilarity(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		int user1Size=user1.size();
		int user2Size=user2.size();
		if(user1Size==0 || user2Size==0){
			return 0.0;
		}
		
		Set<String> interItems=user1Size>user2Size?user1.interSectionItem(user2):user2.interSectionItem(user1);
		int commonItems=interItems.size();//共同项目个数
		if(commonItems==0 || commonItems==user2Size)//如果共同项目等于user2的的项目，则返回0
			return 0.0;
		
		double sim=0.0;
		for(String itemID:interItems){
			sim+=Math.pow((user1.getItemRating(itemID)-user2.getItemRating(itemID)), 2);
		}
		sim=Math.sqrt(sim/(double)commonItems);//除以共同项目个数
		sim=1.0d-Math.tanh(sim);//相似度
		int maxCommonItems=Math.min(user1Size, user2Size);

		sim=sim*((double)commonItems/(double)maxCommonItems);
	
		return sim;
	}

	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateEuclideanSimilarity(userID1,userID2);
	}

}
