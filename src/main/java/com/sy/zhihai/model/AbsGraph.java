package com.sy.zhihai.model;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 图推荐的抽象类
 *
 * @author yan.shi
 * @date： 日期：2017-5-12 时间：下午1:09:42
 */
abstract class AbsGraph extends AbsModelReco {
    public double alpha = 0.0;//游走概率
    public int max_iter = 0;//最大迭代次数

    /**
     * @param alpha    游走概率
     * @param max_iter 迭代次数
     */
    abstract void initParam(double alpha, int max_iter);

    @Override
    public FastMap<String, FastList<RecommendedItem>> calRecAllUsers(
            int recomCount) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FastList<RecommendedItem> calRecSingleUser(String userID,
                                                      int recomCount) {
        // TODO Auto-generated method stub
        return null;
    }

}
