package com.sy.zhihai.model;

import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.FastMap;


/**
 * 融合了BiasLFM以及用户的历史评分行为
 * 结合了邻域和LFM
 *
 * @author yan.shi
 * @date： 日期：2017-4-27 时间：下午1:50:33
 */
public class SVDPP extends AbsMF {

    private FastMap<String, Double> BU = null;//user的偏置项
    private FastMap<String, Double> BI = null;//item的偏置项
    private FastMap<String, Double[]> Y = null;
    private double sumMean = 0.0;//全体评分的平均

    public SVDPP() {
    }

    public static void main(String[] args) {
        String dataPath = "resultData.txt";
        SVDPP svdpp = new SVDPP();
        svdpp.loadData(dataPath);
        svdpp.initParam(30, 0.02, 0.01, 50);
        svdpp.train();

        System.out.println("Input userID...");
        Scanner in = new Scanner(System.in);
        while (true) {
            String userID = in.nextLine();
            FastList<RecommendedItem> recommendedItems = svdpp.calRecSingleUser(userID, 50);
            svdpp.displayRecoItem(userID, recommendedItems);
            System.out.println("Input userID...");
        }
    }


    /**
     * 初始化F,α,λ,max_iter,U,I,BU,BI,Y
     *
     * @param F        隐因子数目
     * @param α        学习速率
     * @param λ        正则化参数，以防过拟合
     * @param max_iter 迭代次数
     */
    @Override
    public void initParam(int F, double α, double λ, int max_iter) {
        System.out.println("init U,I,BU,BI...");
        this.F = F;
        this.α = α;
        this.λ = λ;
        this.max_iter = max_iter;
        this.U = new FastMap<String, Double[]>();
        this.I = new FastMap<String, Double[]>();
        this.BU = new FastMap<String, Double>();
        this.BI = new FastMap<String, Double>();
        this.Y = new FastMap<String, Double[]>();
        int itemCount = 0;//所有user对有过行为的item总数

        Double[] randomUValue = null;
        Double[] randomIValue = null;
        Double[] randomYValue = null;
        //对U,I,Y矩阵随机初始化
        for (Entry<String, FastMap<String, Double>> entry : ratingData.entrySet()) {
            String userID = entry.getKey();
            this.BU.put(userID, 0.0);
            itemCount += entry.getValue().size();
            randomUValue = new Double[F];
            for (int i = 0; i < F; i++) {
                double rand = Math.random() / Math.sqrt(F);//随机数填充初始化矩阵，并和1/sqrt(F)成正比
                randomUValue[i] = rand;
            }
            U.put(userID, randomUValue);

            for (Entry<String, Double> entryItem : entry.getValue().entrySet()) {
                this.sumMean += entryItem.getValue();
                String itemID = entryItem.getKey();
                this.BI.put(itemID, 0.0);
                if (I.containsKey(itemID))
                    continue;
                randomIValue = new Double[F];
                randomYValue = new Double[F];
                for (int i = 0; i < F; i++) {
                    double randI = Math.random() / Math.sqrt(F);
                    randomIValue[i] = randI;
                    double randY = Math.random() / Math.sqrt(F);
                    randomYValue[i] = randY;
                }
                I.put(itemID, randomIValue);
                Y.put(itemID, randomYValue);
            }
        }
        this.sumMean /= itemCount;
    }

    /**
     * 随机梯度下降训练U,I,BU,BI,Y
     */
    @Override
    public void train() {
        System.out.println("training U,I,BU,BI,Y...");
        for (int step = 0; step < this.max_iter; step++) {
            System.out.println("第" + (step + 1) + "次迭代...");
            for (Entry<String, FastMap<String, Double>> entry : this.ratingData.entrySet()) {
                double[] z_Item = new double[this.F];//此用户历史数据的隐偏好之和
                for (String item : entry.getValue().keySet()) {
                    Double[] Y_Item = this.Y.get(item);
                    for (int f = 0; f < this.F; f++) {
                        z_Item[f] += Y_Item[f];
                    }
                }
                double itemLength_Sqrt = 1.0 / Math.sqrt(1.0 * entry.getValue().size());
                double[] s = new double[this.F];

                String userID = entry.getKey();
                for (Entry<String, Double> itemRatingEntry : entry.getValue().entrySet()) {
                    String itemID = itemRatingEntry.getKey();
                    double pui = this.predictRating(userID, itemID);
                    double err = itemRatingEntry.getValue() - pui;//根据当前参数计算误差(真实值-预测值)
                    double bu = this.BU.get(userID);
                    bu += this.α * (err - this.λ * bu);
                    this.BU.put(userID, bu);
                    double bi = this.BI.get(itemID);
                    bi += this.α * (err - this.λ * bi);
                    this.BI.put(itemID, bi);
                    Double[] userValue = this.U.get(userID);
                    Double[] itemValue = this.I.get(itemID);
                    for (int i = 0; i < this.F; i++) {
                        s[i] += itemValue[i] * err;
                        double us = userValue[i];
                        double it = itemValue[i];
                        us += this.α * (err * it - this.λ * us);//后一项是来防止过拟合的正则化项，λ需要根据具体应用场景反复实验得到。损失函数的优化使用随机梯度下降算法
                        it += this.α * (err * (us + z_Item[i] * itemLength_Sqrt) - this.λ * it);
                        userValue[i] = us;
                        itemValue[i] = it;
                    }
                }

                for (String item : entry.getValue().keySet()) {
                    Double[] Y_Item = this.Y.get(item);
                    for (int f = 0; f < this.F; f++) {
                        double y = Y_Item[f];
                        y += this.α * (s[f] * itemLength_Sqrt - this.λ * y);
                        Y_Item[f] = y;
                    }
                }
            }
            this.α *= 0.9;//每次迭代步长要逐步缩小
        }
    }

    /**
     * userID对itemID的评分
     * U每行表示该用户对各个隐因子的偏好程度
     * I每列表示该物品在各个隐患因子中的概率分布
     * rating=(U+Y/sqrt(sum(ui)))*I+sumMean+BU+BI
     *
     * @param userID
     * @param itemID
     * @return
     */
    @Override
    public double predictRating(String userID, String itemID) {
        double[] z_Item = new double[this.F];
        Map<String, Double> ratingItem = this.ratingData.get(userID);
        for (String item : ratingItem.keySet()) {
            Double[] Y_Item = this.Y.get(item);
            for (int f = 0; f < this.F; f++)
                z_Item[f] += Y_Item[f];
        }
        double p = 0.0;
        Double[] userValue = this.U.get(userID);
        Double[] itemValue = this.I.get(itemID);
        for (int i = 0; i < this.F; i++) {
            double rating = userValue[i] + z_Item[i] / Math.sqrt(1.0 * ratingItem.size());
            p += rating * itemValue[i];
        }
        p += this.BU.get(userID) + this.BI.get(itemID) + this.sumMean;
        return p;
    }

}
