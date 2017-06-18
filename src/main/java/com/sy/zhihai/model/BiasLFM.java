package com.sy.zhihai.model;

import java.util.Map.Entry;
import java.util.Scanner;


import javolution.util.FastMap;
import javolution.util.FastList;

/**
 * BiasLFM(bias latent factor model)带偏置项的隐语义推荐模型，加入三个偏置项（所有评分的平均，用户偏置项表示用户的评分习惯和物品没关系，
 * 物品偏置项表示物品接受的评分中和用户没关系的因素）矩阵分解,训练得到U,I矩阵,以及用户偏置项和物品偏置项
 * 对user-item评分矩阵进行分解为U、I矩阵，再利用随机梯度下降（函数值下降最快的方向）迭代求解出U,I矩阵，最后用U*I预测得出user对item的预测评分
 * 这里U矩阵是user对每个隐因子的偏好程度，I矩阵是item在每个隐因子中的分布
 *
 * @author yan.shi
 * @date： 日期：2017-1-9 时间：下午4:23:29
 */
public class BiasLFM extends AbsMF {

    private FastMap<String, Double> BU = null;//user的偏置项
    private FastMap<String, Double> BI = null;//item的偏置项
    private double sumMean = 0.0;//全体评分的平均

    public BiasLFM() {
    }

    public static void main(String[] args) {
        String dataPath = "resultData.txt";
        BiasLFM blfm = new BiasLFM();
        blfm.loadData(dataPath);
        blfm.initParam(30, 0.02, 0.01, 50);
        blfm.train();

        System.out.println("Input userID...");
        Scanner in = new Scanner(System.in);
        while (true) {
            String userID = in.nextLine();
            FastList<RecommendedItem> recommendedItems = blfm.calRecSingleUser(userID, 50);
            blfm.displayRecoItem(userID, recommendedItems);
            System.out.println("Input userID...");
        }

    }

    /**
     * 初始化F,α,λ,max_iter,U,I,BU,BI
     *
     * @param F        隐因子数目
     * @param α        学习速率
     * @param λ        正则化参数，以防过拟合
     * @param max_iter 迭代次数
     */
    @Override
    public void initParam(int F, double α, double λ, int max_iter) {
        // TODO Auto-generated method stub
        System.out.println("init... " + "F= " + F + "; " + "α= " + α + "; " + "λ= " + λ + "; " + "max_iter= " + max_iter + ";");
        this.F = F;
        this.α = α;
        this.λ = λ;
        this.max_iter = max_iter;
        this.U = new FastMap<String, Double[]>();
        this.I = new FastMap<String, Double[]>();
        this.BU = new FastMap<String, Double>();
        this.BI = new FastMap<String, Double>();
        int itemCount = 0;//所有user的item数量

        Double[] randomUValue = null;
        Double[] randomIValue = null;
        //对U,I矩阵随机初始化,BU,BU初始化为0
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
                for (int i = 0; i < F; i++) {
                    double rand = Math.random() / Math.sqrt(F);
                    randomIValue[i] = rand;
                }
                I.put(itemID, randomIValue);
            }
        }
        this.sumMean /= itemCount;
    }

    /**
     * 随机梯度下降训练U,I,BU,BI
     */
    @Override
    public void train() {
        // TODO Auto-generated method stub
        System.out.println("training U,I,BU,BI...");
        for (int step = 0; step < this.max_iter; step++) {
            System.out.println("第" + (step + 1) + "次迭代...");
            for (Entry<String, FastMap<String, Double>> entry : this.ratingData.entrySet()) {
                String userID = entry.getKey();
                for (Entry<String, Double> entry1 : entry.getValue().entrySet()) {
                    String itemID = entry1.getKey();
                    double pui = this.predictRating(userID, itemID);
                    double err = entry1.getValue() - pui;//根据当前参数计算误差

                    double bu = this.BU.get(userID);
                    bu += this.α * (err - this.λ * bu);
                    this.BU.put(userID, bu);
                    double bi = this.BI.get(itemID);
                    bi += this.α * (err - this.λ * bi);
                    this.BI.put(itemID, bi);

                    Double[] userValue = this.U.get(userID);
                    Double[] itemValue = this.I.get(itemID);
                    for (int i = 0; i < this.F; i++) {
                        double us = userValue[i];
                        double it = itemValue[i];
                        us += this.α * (err * it - this.λ * us);//后一项是来防止过拟合的正则化项，λ需要根据具体应用场景反复实验得到。损失函数的优化使用随机梯度下降算法
                        it += this.α * (err * us - this.λ * it);
                        userValue[i] = us;
                        itemValue[i] = it;
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
     * rating=U*I+sumMean+BU+BI
     *
     * @param userID
     * @param itemID
     * @return
     */
    @Override
    public double predictRating(String userID, String itemID) {
        // TODO Auto-generated method stub
        double p = 0.0;
        Double[] userValue = this.U.get(userID);
        Double[] itemValue = this.I.get(itemID);
        for (int i = 0; i < this.F; i++) {
            p += userValue[i] * itemValue[i];
        }
        p += this.BU.get(userID) + this.BI.get(itemID) + this.sumMean;
        return p;
    }

}
