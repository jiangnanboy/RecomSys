package com.sy.zhihai.base;

/**
 * 相似项目类
 * @author yan.shi
 *@date： 日期：2016-10-13 时间：下午1:37:40
 */
public class SimilarityItem {
	String itemID;
	double similarityValue;
	public SimilarityItem(String itemID,double value){
		this.itemID=itemID;
		this.similarityValue=value;
	}
	/**
	 * 得到项目id
	 * @return
	 */
	public String getItemID(){
		return this.itemID;
	}
	/**
	 * 得到项目的相似值
	 * @return
	 */
	public double getSimilarityValue(){
		return this.similarityValue;
	}
}
