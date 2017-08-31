/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

/**
 *
 * @author rajaprabu.TRN
 */

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import java.io.*;

public class CreateFolder {
        
    Set<String> tuplelist = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);    
    HashMap<String , Integer> tuple = new HashMap<String , Integer>();
             
    CreateFolder(String m){      
     try{   
        File fileName=new File("category\\categorylist.csv");
        //File fileName=new File("walmart\\categorylist.csv");
       // @SuppressWarnings("resource")
	OutputStream os = (OutputStream) new FileOutputStream(fileName);
	String encoding = "UTF8";
	OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
	BufferedWriter bw = new BufferedWriter(osw); 
             
        System.out.println("opening excel");
       
	String filename = "dataset/"+m+".xls";
	WorkbookSettings ws = new WorkbookSettings();
	ws.setLocale(new Locale("en", "EN"));
	Workbook w = Workbook.getWorkbook(new File(filename),ws); 
	//Reference http://www.vogella.com/tutorials/JavaExcel/article.html	
	// Get the first sheet
	Sheet s = w.getSheet(0);				
	// Convert the contents of the cells         
           //for(int bit=1;bit<=16;bit++){  
		int bit=15;
       Cell[] row = null;
        
        for(int i=1;i<s.getRows();i++)
        {
        	row=s.getRow(i);
             String tuplename="#";
             String cat=(clean_folder(row[3].getContents())).toLowerCase();
             String applicationName=(clean_folder(row[5].getContents())).toLowerCase();
             String intype=(clean_folder(row[2].getContents())).toLowerCase();
             String sub=(clean_folder(row[4].getContents())).toLowerCase();
            
           if((bit & 1) == 1){
               tuplename=tuplename+"@"+cat;
           }
           if((bit>>1 & 1) == 1){
               tuplename=tuplename+"@"+applicationName;
           }
           if((bit>>2 & 1) == 1){
               tuplename=tuplename+"@"+intype;
           }
           if((bit>>3 & 1) == 1){
               tuplename=tuplename+"@"+sub;
           }
                     //System.out.println(tuplename);
            if(!(tuplelist.contains(tuplename))){
                //System.out.println(tuplename);
                tuplelist.add(tuplename);                              
            }        
        }
        
       Iterator itr=tuplelist.iterator();    
//System.out.println(tuplelist.size());
        while(itr.hasNext())
        {           
        	   tuple.put(itr.next().toString(), 0);
        }
        
        for(int i=1;i<s.getRows();i++)
        {
        		row=s.getRow(i);
        		String cat=(clean_folder(row[3].getContents())).toLowerCase();
        		String applicationName=(clean_folder(row[5].getContents())).toLowerCase();
        		String intype=(clean_folder(row[2].getContents())).toLowerCase();
            	String sub=(clean_folder(row[4].getContents())).toLowerCase();           
            	String tuplename="#"; 
            
           		if((bit & 1) == 1){
               		tuplename=tuplename+"@"+cat;
           		}
           		if((bit>>1 & 1) == 1){
               		tuplename=tuplename+"@"+applicationName;
           		}
           		if((bit>>2 & 1) == 1){
               		tuplename=tuplename+"@"+intype;
           		}
           		if((bit>>3 & 1) == 1){
               		tuplename=tuplename+"@"+sub;
           		}            
           		tuple.put(tuplename,tuple.get(tuplename)+1);       
          }   
        Map<String , Integer> map=SortByValue.sortcategory(tuple);
        Set set=map.entrySet();
        Iterator it=set.iterator();
        int top=0;
        
        while(it.hasNext() && top< 7)
        {            
            Map.Entry t=(Map.Entry)it.next();
            String foldername=t.getKey().toString();
            String count=t.getValue().toString();
            //System.out.println(t.getValue().toString());          
             foldername=foldername.replaceAll("#@", "");           
            //File thisDir=new File("walmart\\"+foldername);
            File thisDir=new File("category\\"+foldername);
                 if (!thisDir.exists())
                 {
                	 System.out.println("creating directory: " + thisDir.getName());
                 boolean result = false;
                    	try
                    		{
                        		thisDir.mkdir();
                        		result = true;
                    		} 
                    	catch(SecurityException se)
                    		{
                        		//handle it
                    		}     	   
                    	if(result)
                    		{    
                        		System.out.println("DIR created");  
                    		}
                 }           
            //foldername=foldername.replaceAll("@", "/");
            bw.write(foldername+","+count);
            bw.newLine();
            top++;            
        }        
        //bw.newLine();
        //bw.newLine();
       //}  
        bw.flush();
        bw.close();
        os.close();             
     }
     catch (UnsupportedEncodingException e) {
	System.err.println(e.toString());
     } catch (IOException e) {
 	System.err.println(e.toString());
     } catch (Exception e) {
	System.err.println(e.toString());
     }
        
    }  
    
     private static String clean_folder(String freeForm) {
		String cleaned = freeForm;
		cleaned = cleaned.replace('/', '-');
		cleaned = cleaned.replace('\\', '-');
		cleaned = cleaned.replace(':', '-');
		cleaned = cleaned.replace('?', '-');
		cleaned = cleaned.replace('*', '-');
		cleaned = cleaned.replace('"', '-');
		cleaned = cleaned.replace('<', '-');
		cleaned = cleaned.replace('>', '-');
        cleaned = cleaned.replace('|', '-');                
        return cleaned;
     }   
}
