package tweetanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class WordCloud {
    static String inname = "importantWords.csv";  
    static String outname = "wordCloud.txt";
    public static void main(String[] args){
        try{
            Scanner in = new Scanner(new BufferedReader(new FileReader(inname)));
            while (in.hasNextLine()){
                String[] s = in.nextLine().split(",");
                if (s[5].equals("yes")){
                    try {
                        BufferedWriter out = new BufferedWriter(new FileWriter(outname,true));
                        out.write(s[1] + "\t" + s[0] + "\n");
                        out.close();
                    } catch (Exception e){
                        e.printStackTrace();;
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
