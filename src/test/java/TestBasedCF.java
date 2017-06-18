import com.sy.zhihai.base.RecommendedItem;
import com.sy.zhihai.base.SimilarityUser;
import com.sy.zhihai.base.DataModel;
import com.sy.zhihai.base.Neighborhood;
import com.sy.zhihai.base.Recommender;
import com.sy.zhihai.similarity.AbstractUserSimilarity;
import com.sy.zhihai.similarity.PearsonSimilarity;

import java.io.File;
import java.util.List;

/**
 * Created by yanshi on 2017/6/18.
 */
public class TestBasedCF {
    public static void main(String[] args) {
        //用户偏好数据文件
        String dataFile = "resultData.txt";
        File file = new File(dataFile);
        //加载偏好数据
        DataModel dataModel = new DataModel(file);
        //相似计算方法
        AbstractUserSimilarity similarity = new PearsonSimilarity(dataModel);
        //similarity.setCommonItemCount(2);//设置共同评分项目个数阈值
        //最近邻(最相似的用户个数，相似度，数据)
        Neighborhood neighborhood = new Neighborhood(6, similarity, dataModel);
        //产生推荐计算(数据，相似度，最近邻)
        Recommender recommender = new Recommender(dataModel, similarity, neighborhood);
        //产生的推荐项目recommend(用户ID，推荐项目个数)
        List<RecommendedItem> recommendations = recommender.recommend(105, 5);

        System.out.println("最近邻数：   " + neighborhood.getTheNearestNeightborhood().size());
        for (SimilarityUser user : neighborhood.getTheNearestNeightborhood()) {
            System.out.println(user.getUserID() + "  " + user.getSimilarityValue());
        }
        System.out.println("推荐项目数：   " + recommendations.size());
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }

    }
}
