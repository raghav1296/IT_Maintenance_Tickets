/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import Jama.*;
import java.util.*;


/**
 *
 * @author rajaprabu.TRN
 */
public class Topstring {
    
    
    Topstring(Matrix A,String[] words){
        
      for(int i=0;i<A.getRowDimension() && i<10;i++){
          
         HashMap<String,Double> hmap = new HashMap<String,Double>();
        
        for(int j=0;j<A.getColumnDimension();j++){
            hmap.put(words[j],A.get(i, j));
        }
        
                  
         Map<String,Double> map = sortByValues(hmap); 
         System.out.println("After Sorting:");
         Set set2 = map.entrySet();
         
                
         Iterator iterator2 = set2.iterator();
        
         int j=0;
        
         while(iterator2.hasNext() && j<10) {
           Map.Entry me2 = (Map.Entry)iterator2.next();
           System.out.print(me2.getKey() + ": ");
           System.out.println(me2.getValue());
           j++;
         }
         
         System.out.println("=============================================");
      }
            

    }

    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               
                return( ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue())== -1?1:-1);
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       
       
       for (Iterator it = list.iterator(); it.hasNext();) {
           
           
              Map.Entry entry = (Map.Entry) it.next();
             
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
    }
    
}
