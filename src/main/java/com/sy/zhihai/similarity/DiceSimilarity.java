package com.sy.zhihai.similarity;


import com.sy.zhihai.base.User;
import com.sy.zhihai.model.DataModel;

/**
 * dice相似度是(2(A^B)/(|A|+|B|));
 * @author yan.shi
 *@date： 日期：2016-10-17 时间：下午2:32:04
 */
public class DiceSimilarity extends AbstractUserSimilarity{

	public DiceSimilarity(DataModel dataModel) {
		super(dataModel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 计算相似度
	 * @param userID1
	 * @param userID2
	 * @return
	 */
	public double calculateDiceSimilarity(String userID1,String userID2){
		User user1=dataModel.getThisUser(userID1);
		User user2=dataModel.getThisUser(userID2);
		int user1Size=user1.size();
		int user2Size=user2.size();
		if(user1Size==0 || user2Size==0){
			return 0.0;
		}
		int commonItems=user1Size>user2Size?user2.interSectionSize(user1):
			user1.interSectionSize(user2);
		if(commonItems==0 || commonItems==user2Size)
			return 0.0;
		return (double)2*commonItems/(double)(user1Size+user2Size);
	}

	@Override
	public double calSimilarity(String userID1, String userID2) {
		// TODO Auto-generated method stub
		return calculateDiceSimilarity(userID1,userID2);
	}

}
