package com.sy.zhihai.model;

import java.util.Map.Entry;
import java.util.Scanner;

import javolution.util.FastMap;
import javolution.util.FastList;

/**
 * LFM(latent factor model)隐语义推荐模型，矩阵分解,训练得到U,I矩阵
 * 对user-item评分矩阵进行分解为U、I矩阵，再利用随机梯度下降（函数值下降最快的方向）迭代求解出U,I矩阵，最后用U*I预测得出user对item的预测评分
 * 这里U矩阵是user对每个隐因子的偏好程度，I矩阵是item在每个隐因子中的分布
 *
 * @author yan.shi
 * @date： 日期：2017-1-9 时间：下午4:23:29
 */
public class LFM extends AbsMF {

    public LFM() {
    }

    public static void main(String[] args) {
        String dataPath = "resultData.txt";
        LFM lfm = new LFM();
        lfm.loadData(dataPath);
        lfm.initParam(30, 0.02, 0.01, 50);
        lfm.train();

        System.out.println("Input userID...");
        Scanner in = new Scanner(System.in);
        while (true) {
            String userID = in.nextLine();
            FastList<RecommendedItem> recommendedItems = lfm.calRecSingleUser(userID, 50);
            lfm.displayRecoItem(userID, recommendedItems);
            System.out.println("Input userID...");
        }
    }


    /**
     * 初始化F,α,λ,max_iter,U,I
     *
     * @param F        隐因子数目
     * @param α        学习速率
     * @param λ        正则化参数，以防过拟合
     * @param max_iter 迭代次数
     */
    @Override
    public void initParam(int F, double α, double λ, int max_iter) {
        System.out.println("init... " + "F= " + F + "; " + "α= " + α + "; " + "λ= " + λ + "; " + "max_iter= " + max_iter + ";");
        this.F = F;
        this.α = α;
        this.λ = λ;
        this.max_iter = max_iter;
        this.U = new FastMap<String, Double[]>();
        this.I = new FastMap<String, Double[]>();

        String userID = null;
        Double[] randomUValue = null;
        Double[] randomIValue = null;
        //对U,I矩阵随机初始化
        for (Entry<String, FastMap<String, Double>> entry : ratingData.entrySet()) {
            userID = entry.getKey();
            randomUValue = new Double[F];
            for (int i = 0; i < F; i++) {
                double rand = Math.random() / Math.sqrt(F);//随机数填充初始化矩阵，并和1/sqrt(F)成正比
                randomUValue[i] = rand;
            }
            U.put(userID, randomUValue);
            for (String itemID : entry.getValue().keySet()) {
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
    }

    /**
     * 随机梯度下降训练U,I矩阵
     */
    @Override
    public void train() {
        System.out.println("training U,I...");
        for (int step = 0; step < this.max_iter; step++) {
            System.out.println("第" + (step + 1) + "次迭代...");
            for (Entry<String, FastMap<String, Double>> entry : this.ratingData.entrySet()) {
                String userID = entry.getKey();
                for (Entry<String, Double> entry1 : entry.getValue().entrySet()) {
                    String itemID = entry1.getKey();
                    double pui = this.predictRating(userID, itemID);
                    double err = entry1.getValue() - pui;//根据当前参数计算误差
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
     * rating=P*Q
     *
     * @param userID
     * @param itemID
     * @return
     */
    @Override
    public double predictRating(String userID, String itemID) {
        double p = 0.0;
        Double[] userValue = this.U.get(userID);
        Double[] itemValue = this.I.get(itemID);
        for (int i = 0; i < this.F; i++) {
            p += userValue[i] * itemValue[i];
        }
        return p;
    }

}
