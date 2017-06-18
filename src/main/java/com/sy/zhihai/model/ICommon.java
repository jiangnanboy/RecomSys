package com.sy.zhihai.model;

import javolution.util.FastList;
import javolution.util.FastMap;

interface ICommon {

    /**
     * 加载数据
     *
     * @param dataPath
     */
    public void loadData(String dataPath);

    /**
     * 所有用户的推荐数据
     *
     * @param recomCount 推荐项目个数
     * @return
     */
    public FastMap<String, FastList<RecommendedItem>> calRecAllUsers(int recomCount);

    /**
     * 单个用户的推荐数据
     *
     * @param userID
     * @param recomCount 推荐项目个数
     * @return
     */
    public FastList<RecommendedItem> calRecSingleUser(String userID, int recomCount);

}
