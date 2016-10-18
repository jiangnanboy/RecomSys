package com.sy.zhihai.similarity;

import com.sy.zhihai.base.User;
import com.sy.zhihai.model.DataModel;

import java.util.Set;



/**
 * cosine相似计算类
 * @author yan.shi
 *@date： 日期：2016-10-18 时间：上午10:12:20
 */
public class CosineSimilarity extends AbstractUserSimilarity{

	public CosineSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 计算两个用户的余弦相似度
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calculateCosineSimilarity(String userID1, String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		
		int user1Size=user1.size();
		int user2Size=user2.size();
		
		if(user1Size==0 || user2Size==0){
			return 0.0;
		}
		int interSize=user1Size<user2Size?user2.interSectionSize(user1):
			user1.interSectionSize(user2);
		
		if(interSize==0 || interSize==user2Size)
			return 0.0;//如果共同项目与user2的项目相等，则返回0
		
		double innerProduct=user1Size>user2Size?calInnerProduct(userID1,userID2):calInnerProduct(userID2,userID1);
		
		return innerProduct/(Math.sqrt(user1.getUserVectorLength())*Math.sqrt(user2.getUserVectorLength()));
	}
	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateCosineSimilarity(userID1,userID2);
	}

}
