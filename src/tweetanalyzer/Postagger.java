/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tweetanalyzer;

import java.io.FileInputStream;
import java.io.InputStream;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 *
 * @author USER
 */
public class Postagger {
    public static void main(String[] args){
        try (InputStream modelIn = new FileInputStream("en-pos-maxent.bin")){
            POSModel model = new POSModel(modelIn);
            POSTaggerME tagger = new POSTaggerME(model);
            String sent[] = new String[]{"Most", "large", "cities", "in", "the", "US", "had",
                                 "morning", "and", "afternoon", "newspapers", "."};		  
            String tags[] = tagger.tag(sent);
            for (String s:tags){
                System.out.println(s);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
