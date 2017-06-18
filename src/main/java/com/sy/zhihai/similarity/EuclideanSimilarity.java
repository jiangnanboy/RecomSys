package com.sy.zhihai.similarity;

import com.sy.zhihai.base.User;
import com.sy.zhihai.base.DataModel;

import java.util.Set;



/**
 * 欧氏相似度计算类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:13:00
 */
public class EuclideanSimilarity extends AbstractUserSimilarity{

	public EuclideanSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 欧氏相似度的改进
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
		int commonItems=interItems.size();//共同项目数
		if(commonItems==0)
			return 0.0;
		if(commonItems==user2Size) 
			return 0.0;//如果共同项目与user2的项目相等，则返回0
		
		double sim=0.0;
		//if(commonItems>this.commonItemCount){//共同评分的项目数大于一个阈值
		for(String itemID:interItems){
			sim+=Math.pow((user1.getItemRating(itemID)-user2.getItemRating(itemID)), 2);
		}
		sim=Math.sqrt(sim/(double)commonItems);//除以共同项目个数
		sim=1.0d-Math.tanh(sim);//相似度
		//}
		return sim;
		
	}
	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateEuclideanSimilarity(userID1,userID2);
	}

}
