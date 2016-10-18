package com.sy.zhihai.similarity;

import com.sy.zhihai.base.User;
import com.sy.zhihai.model.DataModel;

import java.util.Set;



/**
 * 曼哈顿相似计算类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:10:10
 */
public class ManhattanSimilarity extends AbstractUserSimilarity{

	public ManhattanSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 曼哈顿相似度
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calculateManhattanSimilarity(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		int user1Size=user1.size();
		int user2Size=user2.size();
		if(user1Size==0 || user2Size==0)
			return 0.0;
		Set<String> interItems=user1Size>user2Size?user1.interSectionItem(user2):user2.interSectionItem(user1);
	    int commonItems=interItems.size();
		if(commonItems==0 || commonItems==user2Size)
	    	return 0.0;
	    double sim=0.0;
	   if(commonItems>this.commonItemCount)
	    for(String itemID:interItems){
	    	sim+=Math.abs(user1.getItemRating(itemID)-user2.getItemRating(itemID));
	    }
	    if(sim==0.0) return 0.0;
	    return 1.0-Math.tanh(sim);
	}

	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateManhattanSimilarity(userID1,userID2);
	}

}
