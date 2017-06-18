import java.util.Scanner;

import javolution.util.FastList;

import com.sy.zhihai.model.AbsMF;
import com.sy.zhihai.model.LFM;
import com.sy.zhihai.model.BiasLFM;
import com.sy.zhihai.model.SVDPP;
import com.sy.zhihai.model.RecommendedItem;

/**
 * Created by yanshi on 2017/6/18.
 */
public class TestModelCF {
    public static void main(String[] args) {
        String dataPath = "C:/Users/yan.shi/Desktop/工作/2017.2.6/test/usernews_ranks.txt";
        int type = 2;
        switch (type) {
            case 1:
                System.out.println("Method LFM");
                testLFM(dataPath);
                break;
            case 2:
                System.out.println("Method BiasLFM");
                testBiasLFM(dataPath);
                break;
            case 3:
                System.out.println("Method SVDPP");
                testSVDPP(dataPath);
                break;
            default:
                break;
        }
    }

    public static void testLFM(String dataPath) {
        AbsMF lfm = new LFM();
        test(lfm, dataPath);
    }

    public static void testBiasLFM(String dataPath) {
        AbsMF blfm = new BiasLFM();
        test(blfm, dataPath);
    }

    public static void testSVDPP(String dataPath) {
        AbsMF svdpp = new SVDPP();
        test(svdpp, dataPath);
    }

    private static void test(AbsMF mf, String dataPath) {
        mf.loadData(dataPath);
        mf.initParam(30, 0.02, 0.01, 50);
        mf.train();

        System.out.println("Input userID...");
        Scanner in = new Scanner(System.in);
        while (true) {
            String userID = in.nextLine();
            FastList<RecommendedItem> recommendedItems = mf.calRecSingleUser(userID, 50);
            mf.displayRecoItem(userID, recommendedItems);
            System.out.println("Input userID...");
        }
    }
}
