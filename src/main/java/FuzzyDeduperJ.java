import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Randy
 * Date: 9/4/12
 * Time: 8:36 PM
 */
public class FuzzyDeduperJ implements Deduper{

    private Set<String> candidates; //Assume input was already deduped. This allows us to use the input values as unique keys for a Set.
    private HashMap<String, Set<String>> phonemeMap = new HashMap<String, Set<String>>();
    private int granularity;

    /**
     * Split input into segments
     * todo: Phonemer could be a separate class?
     * @param size
     * @param input
     * @return
     */
    public List<String> makePhonemes(String input, int size){
        List<String> phonemeList = new ArrayList<String>();
        int i=0;
        while(i+size  <= input.length()) {
            phonemeList.add(input.substring(i,i+size));
            i++;
        }
        return phonemeList;
    }

    public List<String> makeAllPhonemes(String input, int startingSize){   //todo: Should be a Set
        List<String> phonemeList = new ArrayList<String>();
        for(int i=startingSize; i<=input.length() && i<=10; i++){           //todo: configurable
            if(input.length()<startingSize) phonemeList.add(input);         //make an exception if startingsize>length
            else phonemeList.addAll(makePhonemes(input, i));
        }
        return phonemeList;
    }

    /**
     * Compile list of candidates
     * @param candidateList
     */
    public FuzzyDeduperJ(Set<String> candidateList, int granularity){
        if(candidateList == null || candidateList.size()<1) throw new IllegalArgumentException();
        this.candidates = candidateList;
        this.granularity = granularity;
        for(String candidate : candidates) {
            candidate = stripIllegalChars(candidate);
            List<String> phonemes = makeAllPhonemes(candidate, granularity);
            upsertPhonemes(phonemeMap, candidate, phonemes);    //Create map of phonemes
        }
    }

    private String stripIllegalChars(String candidate) {
//        return candidate.replaceAll(" ","");  //todo: would need a way to map this back to input data
        return candidate;
    }

    private void upsertPhonemes(Map<String, Set<String>> m, String candidate, List<String> phonemeList) {
        for(String phoneme: phonemeList){
            if(m.containsKey(phoneme)){    //Phoneme present, add candidate to set
                m.get(phoneme).add(candidate);
            } else {
                Set<String> c = new HashSet<String>();
                c.add(candidate);
                m.put(phoneme,c);
            }
        }
    }

    //todo: remove
    public HashMap<String, Set<String>> dumpPhonemes(){
        return phonemeMap;
    }

//    @Override
//    public Pair<String, String> nextCandidate() {
//        return null;  //To change body of implemented methods use File | Settings | File Templates.
//    }

    @Override
    public Map<Pair<String,String>,Double> candidatesByRank() {
        Map< Pair<String,String>, Double> scores = new HashMap< Pair<String,String>, Double>();
        for(String phoneme: phonemeMap.keySet()){
            Set<String> words = phonemeMap.get(phoneme);
            double score = score(phoneme.length(), words.size());
            if(score>500){   //Don't bother to count the small stuff //todo: configurable
                Set<Pair<String,String>> wordPairs = makePairs(words);
                //Increment score
                for(Pair<String,String> p: wordPairs){
                    double discountedScore = discountScore(score, p);
                    if(scores.containsKey(p)) {
                        scores.put(p, scores.get(p) + discountedScore);
                    }
                    else scores.put(p, discountedScore);
                }
            }
        }

        return MapUtil.sortByValue(scores);
    }

    private Set<Pair<String,String>> makePairs(Set<String> wordList){
        Set<Pair<String,String>> wordPairs = new HashSet<Pair<String,String>>();
        Set<String> words = new HashSet<String>();
        if(wordList.size()<2 || wordList.size()>100) return wordPairs;//>100, too common?
        words.addAll(wordList);
        while(!words.isEmpty()){                       //todo: this is horribly inefficient
            Iterator i = words.iterator();
            String word = (String)i.next();
            words.remove(word);
            for(String otherWord: words){
                wordPairs.add(new Pair<String,String>(word, otherWord));
            }
        }
        return wordPairs;
    }

    private double score(int phonemeLength, int numberOfPartners) {
        //inversely related to num of partners, directly related to length
        return Math.pow(phonemeLength, 3) / Math.pow((double) numberOfPartners, (.1));   //todo: configurable
    }
    //check whether phonemes that differ are more or less common
    private double discountScore(double score, Pair<String, String> p) {
        HashMap<String, Set<String>> pairPhonemeMap = new HashMap<String, Set<String>>();
        upsertPhonemes(pairPhonemeMap,p.getLeft(), makeAllPhonemes(p.getLeft(), this.granularity));
        upsertPhonemes(pairPhonemeMap,p.getRight(), makeAllPhonemes(p.getRight(),this.granularity));
        double accScore = 0;
        for(Map.Entry<String, Set<String>> entry: pairPhonemeMap.entrySet()){
            if(entry.getValue().size()==1){
                accScore += dScore(entry.getKey().length(), phonemeMap.get(entry.getKey()).size());//Score the mismatched phonemes
            }
        }
        return score / accScore;
    }

    private double dScore(int phonemeLength, int numberOfPartners) {
        double s=  Math.pow(phonemeLength, 5) / Math.pow((double) numberOfPartners, (.8));
//        double inv = Math.pow(s, -1);
        return s;
    }

}
