import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Randy
 * Date: 9/4/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {

//        HashSet<String> sampleList = new HashSet<String>();
//        sampleList.add("one");
//        sampleList.add("onetwo");
//        sampleList.add("network");
//        sampleList.add("new york");

        long startTime = System.currentTimeMillis();

        FuzzyDeduperJ fd = new FuzzyDeduperJ((new CSVReader("input.csv")).getNames(), 5);
        int i=0;
        Map<Pair<String,String>,Double> ranks =  fd.candidatesByRank();

        long endTime = System.currentTimeMillis();

        Iterator it = ranks.keySet().iterator();
        while(it.hasNext() && i<300){
            i++;
            Pair p = (Pair)it.next();
            System.out.println("(" + i + ")  " + ranks.get(p) + "   " + p.getLeft() + " == " + p.getRight());
        }

        System.out.println("Runtime: " + (endTime-startTime));
    }
}
