package com.sy.zhihai.base;

import com.sy.zhihai.base.SimilarityUser;
import com.sy.zhihai.similarity.AbstractUserSimilarity;

import java.util.List;

public class Neighborhood {
    //最近邻用户数
    int nearestNum;
    //相似计算方法类
    AbstractUserSimilarity userSimilarity;
    //数据模型
    DataModel dataModel;

    public Neighborhood(int nearestNum, AbstractUserSimilarity userSimilarity, DataModel dataModel) {
        this.nearestNum = nearestNum;
        this.userSimilarity = userSimilarity;
        this.dataModel = dataModel;
    }

    /**
     * 获取userID的simUserSNum个最近邻
     *
     * @param userID
     * @return
     */
    public void cal_TheNearestNeighborhood(String userID) {
        userSimilarity.cal_NearestUsers(userID, nearestNum);//计算userID的最近邻
    }

    public List<SimilarityUser> getTheNearestNeightborhood() {
        return userSimilarity.getNearestSimilarityUser();
    }
}
