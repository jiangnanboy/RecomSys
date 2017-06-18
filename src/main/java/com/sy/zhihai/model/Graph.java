package com.sy.zhihai.model;

import java.util.Map;
import java.util.HashMap;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * personalRank二分图推荐模型
 * <p/>
 * 图中顶点的相关性：
 * (1).两个顶点之间的路径数
 * (2).两个顶点之间的路径长度
 * (3).两个顶点之间的路径经过的顶点
 * <p/>
 * 相关性高的一对顶点一般具有如下特征：
 * (1).两个顶点之间有很多路径相连
 * (2).连接两个顶点之间的路径长度都比较短
 * (3).连接两个顶点之间的路径不会经过出度比较大的顶点
 *
 * @author yan.shi
 * @date： 日期：2017-1-13 时间：上午10:43:51
 */
public class Graph extends AbsGraph {

    private Map<String, Double> rank = null;//存储迭代值

    public Graph() {
    }

    @Override
    public void initParam(double alpha, int max_iter) {
        // TODO Auto-generated method stub
        this.rank = new FastMap<String, Double>();
        this.alpha = alpha;
        this.max_iter = max_iter;
        this.ratingData = new FastMap<String, FastMap<String, Double>>();
    }

    public void personalRank(String ID) {
        for (Map.Entry<String, FastMap<String, Double>> entry : this.ratingData.entrySet()) {
            this.rank.put(entry.getKey(), 0.0);
        }
        this.rank.put(ID, 1.0);//从当前节点开始

        Map<String, Double> tmp = null;
        //开始迭代
        for (int i = 0; i < this.max_iter; i++) {
            System.out.println("第  " + (i + 1) + "次迭代!");
            tmp = new HashMap<String, Double>();
            for (String key : this.ratingData.keySet()) {
                tmp.put(key, 0.0);
            }
            //节点i与它的所有出边结点(相连结点)
            for (Map.Entry<String, FastMap<String, Double>> entry : this.ratingData.entrySet()) {
                String id = entry.getKey();
                int len = entry.getValue().size();//出度
                //取节点i的出边的节点j以及边E(i,j)的权重wij, 边的权重都为1，归一化之后就上1/len，这里没用到wij
                for (String outKey : entry.getValue().keySet()) {//id的所有出结点
                    //id是outKey的其中一条入边的首节点，因此需要遍历图找到outKey的入边的首节点，
                    //这个遍历过程就是此处的2层for循环，一次遍历就是一次游走
                    double v = tmp.get(outKey) + this.alpha * this.rank.get(id) / len;//均匀游走
                    tmp.put(outKey, v);//outKey的值

                    //if(outKey.equals(ID)){
                    //double value=tmp.get(ID)+(1.0-this.alpha);
                    //tmp.put(outKey, value);
                    //}
                }
            }
            //我们每次游走都是从ID节点出发，因此ID节点的权重需要加上(1 - alpha)
            double value = tmp.get(ID) + (1.0 - this.alpha);
            tmp.put(ID, value);
            this.rank = tmp;
        }
    }

    public void print() {
        System.out.println("结果输出：");
        for (Map.Entry<String, Double> entry : this.rank.entrySet()) {
            System.out.println(entry.getKey() + "   " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.initParam(0.8, 50);
        /*
         'A' : {'a' : 1, 'c' : 1},
         'B' : {'a' : 1, 'b' : 1, 'c':1, 'd':1},
         'C' : {'c' : 1, 'd' : 1},
         'a' : {'A' : 1, 'B' : 1},
         'b' : {'B' : 1},
         'c' : {'A' : 1, 'B' : 1, 'C':1},
         'd' : {'B' : 1, 'C' : 1}
         */

        FastMap<String, Double> userA = new FastMap<String, Double>();
        userA.put("a", 1.0);
        userA.put("c", 1.0);
        graph.ratingData.put("A", userA);

        FastMap<String, Double> userB = new FastMap<String, Double>();
        userB.put("a", 1.0);
        userB.put("b", 1.0);
        userB.put("c", 1.0);
        userB.put("d", 1.0);
        graph.ratingData.put("B", userB);

        FastMap<String, Double> userC = new FastMap<String, Double>();
        userC.put("c", 1.0);
        userC.put("d", 1.0);
        graph.ratingData.put("C", userC);

        FastMap<String, Double> itemA = new FastMap<String, Double>();
        itemA.put("A", 1.0);
        itemA.put("B", 1.0);
        graph.ratingData.put("a", itemA);

        FastMap<String, Double> itemB = new FastMap<String, Double>();
        itemB.put("B", 1.0);
        graph.ratingData.put("b", itemB);

        FastMap<String, Double> itemC = new FastMap<String, Double>();
        itemC.put("A", 1.0);
        itemC.put("B", 1.0);
        itemC.put("C", 1.0);
        graph.ratingData.put("c", itemC);

        FastMap<String, Double> itemD = new FastMap<String, Double>();
        itemD.put("B", 1.0);
        itemD.put("C", 1.0);
        graph.ratingData.put("d", itemD);

        graph.personalRank("b");
        graph.print();
    }

}
