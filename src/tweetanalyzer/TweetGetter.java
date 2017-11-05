package tweetanalyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import opennlp.tools.tokenize.SimpleTokenizer;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetGetter {
      
    public static String outname = "tweet.txt"; 
    
    public static void main(String[] args) {
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("");
        cb.setOAuthConsumerSecret("");
        cb.setOAuthAccessToken("");
        cb.setOAuthAccessTokenSecret("");

        Twitter twitter = new TwitterFactory(cb.build()).getInstance();

        int pageno = 1;
        String user = "";
        List statuses = new ArrayList();

        while (true) {
            try {
                int size = statuses.size(); 
                Paging page = new Paging(pageno++, 100);
                statuses.addAll(twitter.getUserTimeline(user, page));
                if (statuses.size() == size)
                break;
            } catch(TwitterException e) {
                e.printStackTrace();
            }
        }
        
        List<String> strings = new ArrayList<>();
        
        //output to external file
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outname));
            for (int i=0;i<statuses.size();i++){
                Status s = (Status) statuses.get(i);
                
                strings.add(s.getText());
                //System.out.print(s.getText() +"\n");
                out.write(s.getText() +"\n");
            }
            //out.write("Total: "+statuses.size()+"\n");
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }        
        
    }
}
