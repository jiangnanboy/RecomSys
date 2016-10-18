package com.sy.zhihai.base;

/**
 * 需要推荐的项目id及对其的预测评分
 * @author yan.shi
 *@date： 日期：2016-10-13 时间：上午10:46:04
 */
public class RecommendedItem {
	//推荐的项目id
	String itemID;
	//对该项目的预测评分
	double predictRating;
	
	public RecommendedItem(String itemID,double predictRating){
		this.itemID=itemID;
		this.predictRating=predictRating;
	}
	
	/**
	 * 得到推荐的项目id
	 * @return
	 */
	public String getItemID(){
		return this.itemID;
	}
	/**
	 * 得到推荐的项目的预测评分
	 * @return
	 */
	public double getPredicRating(){
		return this.predictRating;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RecommendedItem [itemID=" + itemID + ", predictRating="
				+ predictRating + "]";
	}
	
}
