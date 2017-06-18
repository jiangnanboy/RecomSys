package com.sy.zhihai.similarity;


import com.sy.zhihai.base.User;
import com.sy.zhihai.base.DataModel;

/**
 * jaccard相似计算类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:13:20
 */
public class JaccardSimilarity extends AbstractUserSimilarity{
	
	public JaccardSimilarity(DataModel dataModel){
		super(dataModel);
	}
	
	
	/**
	 * 计算两个用户的jaccard相似度(交集/并集)
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calculateJaccardSimilarity(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		
		int user1Size=user1.size();
		int user2Size=user2.size();
		
		if(user1Size==0 || user2Size==0){
			return 0.0;
		}
		//两个用户相同评分的项目，交集
		int interSize=user1Size<user2Size?user2.interSectionSize(user1.getItemRatingsVector()):
			user1.interSectionSize(user2.getItemRatingsVector());
		
		if(interSize==0 || interSize==user2Size){
			return 0.0;
		}
		
		//并集
		int unionSize=user1Size+user2Size-interSize;
		
		return (double)interSize/(double)unionSize;
	}


	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateJaccardSimilarity(userID1,userID2);
		
	}
	
	
}
