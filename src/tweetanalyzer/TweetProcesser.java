package tweetanalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class TweetProcesser {
    static String inname = "tweet.txt";  
    public static void main(String[] args){
        List<String> strings = new ArrayList<>();
        POSTaggerME tagger = null;
        try{
            Scanner in = new Scanner(new BufferedReader(new FileReader(inname)));
            while (in.hasNextLine()){
                String s = in.nextLine();
                strings.add(s);
            }
            in.close();
            InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
            POSModel model = new POSModel(modelIn);
            tagger = new POSTaggerME(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<String,Integer> freqs = new HashMap<>();
        
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;  
        //tokenize
        for (String sentence : strings) {
            String tokens[] = tokenizer.tokenize(sentence);
            for (String token : tokens) {
                token = token.toLowerCase();
                if (freqs.containsKey(token)) {
                    freqs.put(token, freqs.get(token) + 1);
                } else {
                    freqs.put(token, 1);
                }
            }
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(freqs.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry1.getValue().compareTo(entry2.getValue());
            }
        });

        //String tags[] = tagger.tag(sent);
        
        //output the freqs
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String temp[] = {pair.getKey().toString()};
            String tag = tagger.tag(temp)[0];
            if ((tag.startsWith("NN")
                    &&pair.getKey().toString().length()>2
                    &&!pair.getKey().toString().equals("https")
                )
                    ||(tag.startsWith("VB")) /*
                    ||(tag.startsWith("PRP"))
                    ||(tag.startsWith("FW"))
                    ||(tag.startsWith("CC"))
                    ||(tag.startsWith("RB"))
                    ||(tag.startsWith("WP"))
                    ||(tag.startsWith("WRB"))
                    ||(tag.startsWith("JJ"))*/
                )
                System.out.println(pair.getKey() + " = " + pair.getValue() + " = " + tag);
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
