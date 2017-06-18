package com.sy.zhihai.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;

//import org.elasticsearch.action.get.GetResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 隐语义推荐的抽象类
 *
 * @author yan.shi
 * @date： 日期：2017-5-4 时间：下午1:46:15
 */
public abstract class AbsMF extends AbsModelReco {

    protected FastMap<String, Double[]> U = null;//U矩阵：user对每个隐因子的偏好程度
    protected FastMap<String, Double[]> I = null;//I矩阵：item在每个隐因子中的分布


    protected int F = 0;//隐因子数
    protected double α = 0.0;//梯度下降学习速率，步长
    protected double λ = 0.0;//正则化参数，防止过拟合
    protected int max_iter = 0;//迭代求解次数


    /**
     * 返回所有用户的推荐数据
     *
     * @param recomCount 推荐项目个数
     * @return
     */
    public FastMap<String, FastList<RecommendedItem>> calRecAllUsers(int recomCount) {
        Queue<RecommendedItem> topRecoItems = null;
        FastList<RecommendedItem> recommendedItems = null;
        FastMap<String, FastList<RecommendedItem>> recomItemToUser = new FastMap<String, FastList<RecommendedItem>>();
        for (String userID : this.U.keySet()) {
            boolean full = false;
            double lowestTopValue = Double.NEGATIVE_INFINITY;
            topRecoItems = new PriorityQueue<RecommendedItem>(recomCount + 1, Collections.reverseOrder());
            FastMap<String, Double> itemRating = this.ratingData.get(userID);

            for (String itemID : this.I.keySet()) {
                if (itemRating.containsKey(itemID))
                    continue;
                double rating = predictRating(userID, itemID);
                if (rating < 0) continue;
                if (!full || rating > lowestTopValue) {
                    topRecoItems.add(new RecommendedItem(itemID, rating));
                    if (full) {
                        topRecoItems.poll();
                    } else if (topRecoItems.size() > recomCount) {
                        full = true;
                        topRecoItems.poll();
                    }
                    lowestTopValue = topRecoItems.peek().getPredicRating();
                }
            }
            int size = topRecoItems.size();
            recommendedItems = new FastList<RecommendedItem>(size);
            recommendedItems.addAll(topRecoItems);
            Collections.sort(recommendedItems);
            recomItemToUser.put(userID, recommendedItems);
        }
        return recomItemToUser;
    }

    /**
     * 返回单个用户的推荐数据
     *
     * @param userID
     * @param recomCount 推荐项目个数
     * @return
     */
    public FastList<RecommendedItem> calRecSingleUser(String userID, int recomCount) {
        Queue<RecommendedItem> topRecoItems = new PriorityQueue<RecommendedItem>(recomCount + 1, Collections.reverseOrder());
        FastList<RecommendedItem> recommendedItems = null;
        boolean full = false;
        double lowestTopValue = Double.NEGATIVE_INFINITY;
        FastMap<String, Double> itemRating = this.ratingData.get(userID);
        for (String itemID : this.I.keySet()) {
            if (itemRating.containsKey(itemID))
                continue;
            double rating = this.predictRating(userID, itemID);//预测值
            if (rating < 0) continue;
            if (!full || rating > lowestTopValue) {
                topRecoItems.add(new RecommendedItem(itemID, rating));
                if (full) {
                    topRecoItems.poll();
                } else if (topRecoItems.size() > recomCount) {
                    full = true;
                    topRecoItems.poll();
                }
                lowestTopValue = topRecoItems.peek().getPredicRating();
            }
        }
        int size = topRecoItems.size();
        recommendedItems = new FastList<RecommendedItem>(size);
        recommendedItems.addAll(topRecoItems);
        Collections.sort(recommendedItems);
        return recommendedItems;
    }

    /**
     * 显示推荐的项目名及分数
     *
     * @param userID
     * @param recommendedItems
     */
    public void displayRecoItem(String userID, FastList<RecommendedItem> recommendedItems) {
        System.out.println("userID: " + userID);
        for (RecommendedItem item : recommendedItems) {
            System.out.println(item.getItemID() + "  " + item.getPredicRating());
        }
    }

    /**
     * 显示单个用户推荐结果
     * @param userID
     * @param recommendedItems
     */
    /*public void displayRecoItem(String userID, FastList<RecommendedItem> recommendedItems){
        UserProfile user1 = new UserProfile();
    	NewsSearcher.computeUserProfile(userID, null, user1);
    	System.out.printf("uid:%s,%s", userID,user1.toString());
    	HashSet<String> topics = user1.alltopics;
    	System.out.println("最近的阅读文章：");
    	int nearReadCount=this.ratingData.get(userID).size();//最近阅读文章数目
    	System.out.println("阅读几篇："+nearReadCount);
    	for(Entry<String, Double> Entry:this.ratingData.get(userID).entrySet())
    	{
    		String newsID=Entry.getKey();
    		GetResponse gr = NewsSearcher.client.prepareGet("nnews","nnews",newsID).setFields(new String[]{"title","tstamp","l1tags"}).execute().actionGet();
    		String title = "";
    		String tstamp = "";
    		String l1tags = "";
    		try
    		{
    			title = (String)gr.field("title").getValue();
    			tstamp = gr.field("tstamp").getValue()+"";
    			l1tags = (String)gr.field("l1tags").getValue();
    		}catch(Exception e){}
    		System.out.printf("(%s,%s,%s)\n",title, tstamp,l1tags);
    	}
		System.out.println("推荐项目如下："+recommendedItems.size());
		for(RecommendedItem item:recommendedItems){
			String newsid = item.getItemID();
			GetResponse gr = NewsSearcher.client.prepareGet("nnews","nnews",newsid).setFields(new String[]{"title","tstamp","l1tags"}).execute().actionGet();
			String title = "";
			String tstamp = "";
			String l1tags = "";
			boolean hittopic  = false;
			try
			{
				title = (String)gr.field("title").getValue();
				tstamp = gr.field("tstamp").getValue()+"";
				l1tags = (String)gr.field("l1tags").getValue();
				String[] temptopics = l1tags.split(" ");
				for(String s: temptopics)
				{
					if(topics.contains(s))
						{
						hittopic = true;
						break;
						}
				}
			}catch(Exception e){}
			System.out.printf("(%s,%s,%s %s,\t%f,%s,%s)\n", item.getItemID(),newsid ,title, tstamp,item.getPredicRating(),l1tags,hittopic+"");
	       System.out.println(item);			
		}
	}*/

    /**
     * 初始化各个参数
     *
     * @param F        隐因子数目
     * @param α        学习速率
     * @param λ        正则化参数，防过拟合
     * @param max_iter 最大迭代次数
     */
    public abstract void initParam(int F, double α, double λ, int max_iter);

    /**
     * 训练各个参数
     */
    public abstract void train();

    /**
     * userID对itemID的预测评分
     *
     * @param userID
     * @param itemID
     * @return
     */
    public abstract double predictRating(String userID, String itemID);

}
