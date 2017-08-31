/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.util.*;


/**
 *
 * @author rajaprabu.TRN
 */



public class Opencsvfile {
    
    static int instances;
    static int features;
    
    static double[][] data;
    ArrayList<String> word;
    
    
    public int getInstances(){
        
        return instances;
    }
    
    public int getFeatures(){
        return features;
        
    }
    
    public double[][] getData(){
        return data;
    }
    
    public  String[] wordlist(){
        
        String[] wlist=new String[word.size()];
        
        for(int i=0;i<word.size();i++){
            wlist[i]=word.get(i);
        }
        
        return wlist;
    }
    
    
   // public static void main(String[] args) throws FileNotFoundException{
        // TODO code application logic here
   
    Opencsvfile(String path) throws FileNotFoundException {
        
        
        Scanner s=new Scanner(new File(path+"TFIDF.csv"));
        ArrayList<String> li=new ArrayList<String>();
        while(s.hasNext()){
            String row=s.next();
            li.add(row);
            
        }
        instances=li.size();
        String[] temp=li.get(0).split(",");
        features=temp.length-1;
       
        //create a 2d- array of double 
        
        data=new double[instances][features];
        
        
        for(int i=0;i<instances;i++){
            
            String[] row=li.get(i).split(",");
            for(int j=1;j<row.length;j++){
                data[i][j-1]=Double.parseDouble(row[j]);
            }
        }
           
        Scanner key=new Scanner(new File(path+"ListofKeywords.csv"));
        
        
        word=new ArrayList<>();
        
       
        
        while(key.hasNext()){
            String row=key.next();
            word.add(row);
            
        }
        
        
        
    
    }
    
    
    
}
