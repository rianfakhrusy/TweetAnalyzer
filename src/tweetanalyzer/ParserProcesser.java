package tweetanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class ParserProcesser {
    static String inname = "test.txt";  
    static String outname = "importantWords.csv";
    public static void main(String[] args){
        List<String> strings = new ArrayList<>();
        POSTaggerME tagger = null;
        
        int rttimes = 0;
        try{
            Scanner in = new Scanner(new BufferedReader(new FileReader(inname)));
            while (in.hasNextLine()){
                String s = in.nextLine();
                //remove other people tweets
                int jstidx = s.indexOf("JST");
                if (jstidx>=0){
                    int rtidx = s.indexOf("RT ");
                    if (rtidx>=0){
                        s = s.substring(0,rtidx);
                        rttimes += 1;
                    }
                    //remove account mentions
                    strings.add(s.replaceAll("@_\\p{L}+", "").replaceAll("@\\p{L}+", "")
                            .substring(s.indexOf("|")+1));
                }
            }
            in.close();
            InputStream modelIn = new FileInputStream("en-pos-maxent.bin");
            POSModel model = new POSModel(modelIn);
            tagger = new POSTaggerME(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Total retweet: " + rttimes + " times");
        
        
        Map<String,Integer> freqs = new HashMap<>();
        
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;  
        //tokenize
        for (String sentence : strings) {
            //spilt a tweet into tokens
            String tokens[] = tokenizer.tokenize(sentence);
            
            //remove duplicate words in sentences
            ArrayList<String> tokensList = new ArrayList<>();
            Collections.addAll(tokensList, tokens);
            Stream<String> tokensStream = tokensList.stream().map(s -> s.toUpperCase())
                  .distinct();
            String[] tokensArray = tokensStream.toArray(String[]::new);
            
            //count frequency
            for (String token : tokensArray) {
                token = token.toLowerCase();
                if (freqs.containsKey(token)) {
                    freqs.put(token, freqs.get(token) + 1);
                } else {
                    freqs.put(token, 1);
                }
            }
        }
        //sort based on freq
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(freqs.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });
        
        
        
        //output the freqs
        Iterator it = entries.iterator();
        int data = 500;
        boolean stop = false;
        
        while (it.hasNext()) {
            data--;
            Map.Entry pair = (Map.Entry)it.next();
            String temp[] = {pair.getKey().toString()};
            String tag = tagger.tag(temp)[0]; //tag every words
            
            System.out.println("Is this word meaningful (y/n)? " + pair.getKey());
            Scanner in = new Scanner(System.in);
            String ans = in.next();
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(outname, true));
                out.write(pair.getKey() + ",");
                out.write(pair.getValue() + ",");
                out.write(tag.charAt(0) + ",");
                out.write(tag.length()>1?tag.charAt(1):'O');
                out.write(",");
                out.write(tag.length()==3?tag.charAt(2):'O');
                out.write(",");
                if (ans.toLowerCase().equals("y")){
                    out.write("yes");
                } else if (ans.toLowerCase().equals("n")){
                    out.write("no"); 
                } else if (ans.toLowerCase().equals("q")){
                    stop = true;
                }
                out.write("\n");
                out.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        /*    
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String temp[] = {pair.getKey().toString()};
            String tag = tagger.tag(temp)[0]; //tag every words
            
           
            if ((tag.startsWith("NN")
                    &&pair.getKey().toString().length()>2
                    &&!pair.getKey().toString().equals("https")
                )
                    ||(tag.startsWith("VB")) 
                    ||(tag.startsWith("PRP"))
                    ||(tag.startsWith("FW"))
                    ||(tag.startsWith("CC"))
                    ||(tag.startsWith("RB"))
                    ||(tag.startsWith("WP"))
                    ||(tag.startsWith("WRB"))
                    ||(tag.startsWith("JJ"))
                )
                System.out.println(pair.getKey() + " = " + pair.getValue() + " = " + tag);
            */
            if (stop==true) break;
            if (data==0) break;
            it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
