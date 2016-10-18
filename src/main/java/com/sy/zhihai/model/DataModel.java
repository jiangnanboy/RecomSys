package com.sy.zhihai.model;


import com.sy.zhihai.base.Item;
import com.sy.zhihai.base.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 加载用户偏好数据
 * @author yan.shi
 *@date： 日期：2016-10-12 时间：上午10:14:48
 */
public class DataModel {
	/**存储项目数据，<项目id,项目类>**/
	private Map<String,Item> itemMap;
	/**存储用户数据,<用户id,用户类>**/
	private Map<String,User> userMap;
	
	public DataModel(File fileName){
		loadData(fileName);//调用，加载用户对项目的偏好数据
	}
	/**
	 * 加载用户项目评分数据
	 * @param fileName
	 */
	private void loadData(File fileName){
		System.out.println("loading data...");
		BufferedReader br=null;
		try{
			itemMap=new HashMap<String,Item>();
			userMap=new HashMap<String,User>();
			br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"utf-8"));
			String line=null;
			while((line=br.readLine())!=null){
				Item item=null;
				User user=null;
				String[] preferData=line.split(",");//userID,itemID,score
				double rating=Double.valueOf(preferData[2]);
				//加载user数据
				if(userMap.containsKey(preferData[0])){
					user=userMap.get(preferData[0]);
					user.putItemIdRating(preferData[1], rating);
					
				}else{
					user=new User(preferData[0]);
					user.putItemIdRating(preferData[1],rating);
					userMap.put(preferData[0], user);
				}
				//加载item数据
				if(itemMap.containsKey(preferData[1])){
					item=itemMap.get(preferData[1]);
					item.putUserIDRating(preferData[0],rating);
				}else{
					item=new Item(preferData[1]);
					item.putUserIDRating(preferData[0],rating);
					itemMap.put(preferData[1], item);
				}
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(br!=null)
				try{
					br.close();
				}catch(IOException e){
					e.printStackTrace();
				}
		}
		
	}
	
	/**
	 * 获取user数据
	 * @return
	 */
	public Map<String,User> getUserMap(){
		if(!userMap.isEmpty())
			return this.userMap;
			return null;
		
	}
	/**
	 * 获取item数据
	 * @return
	 */
	public Map<String,Item> getItemMap(){
		if(!itemMap.isEmpty())
			return this.itemMap;
			return null;
		
	}
	/**
	 * 返回此用户所有的项目及评分
	 * @param userID
	 * @return
	 */
	public User getThisUser(String userID){
		if(userMap.containsKey(userID)){
			return userMap.get(userID);
		}else{
			throw new IllegalArgumentException("没有此用户！");
		}
		
	}
	/**
	 * 返回此项目被所有用户有过行为的用户ID及评分
	 * @param itemID
	 * @return
	 */
	public Item getThisItem(String itemID){
		if(itemMap.containsKey(itemID)){
			return itemMap.get(itemID);
		}else{
			throw new IllegalArgumentException("没有此项目！");
		}
		
	}
	
}
