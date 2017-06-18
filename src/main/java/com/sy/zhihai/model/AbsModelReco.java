package com.sy.zhihai.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javolution.util.FastMap;

abstract class AbsModelReco implements ICommon {
    public FastMap<String, FastMap<String, Double>> ratingData = null;//用户物品评分矩阵，原始评分数据集(user,item,rating)

    /**
     * 加载原始数据
     * 数据为文本格式，每行的数据形式为：userid,itemid,rating
     *
     * @param dataPath
     */
    public void loadData(String dataPath) {
        System.out.println("loading data...");
        this.ratingData = new FastMap<String, FastMap<String, Double>>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath), "utf-8"));
            String line = null;
            FastMap<String, Double> itemRating = null;
            while ((line = br.readLine()) != null) {
                String[] dataRating = line.split(",");
                String userID = dataRating[0].trim();
                String itemID = dataRating[1].trim();
                double rating = Double.parseDouble(dataRating[2]);

                if (ratingData.containsKey(userID)) {
                    itemRating = ratingData.get(userID);
                    itemRating.put(itemID, rating);
                } else {
                    itemRating = new FastMap<String, Double>();
                    itemRating.put(itemID, rating);
                    ratingData.put(userID, itemRating);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
