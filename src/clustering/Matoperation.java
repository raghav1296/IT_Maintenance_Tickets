/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import Jama.Matrix;

/**
 *
 * @author rajaprabu.TRN
 */


import java.util.*;
public class Matoperation {
    
    
     public static Matrix Multiplication(Matrix a,Matrix b) throws MatException{
        
        int r=a.getRowDimension();
        int c=b.getColumnDimension();
        int e=a.getColumnDimension();
        
        Matrix res=new Matrix(r,c);
        
        if(a.getColumnDimension() != b.getRowDimension())
            throw new MatException("Multiplication:First Matrix column is not equal to Second Matrix row");
       

        
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                double sum=0;
                for(int k=0;k<e;k++){
                    sum+=a.get(i,k)*b.get(k, j);
                }
                res.set(i,j,sum);
                
            }
        }
        
        return res;
    }
    
     
    public static void printMatrix(Matrix m) {
        
        for(int i=0;i<m.getRowDimension();i++){
            for(int j=0;j<m.getColumnDimension();j++){
                System.out.print(m.get(i, j)+" ");
            }
            System.out.println("");
        }
        
    }
    
    
    
    public static double forbenius(Matrix a, Matrix b) throws MatException{
        
        
        double cost=0;
        
        
        if(a.getColumnDimension() != b.getColumnDimension() || a.getRowDimension() != b.getRowDimension())
            throw new MatException("Forbenius: Two Matrix have Different Dimenstion");
        
        try{
        
        for(int i=0;i<a.getRowDimension();i++){
            for(int j=0;j<a.getColumnDimension();j++){
                
                cost+=Math.pow(a.get(i, j)-b.get(i, j),2);
                
            }
        }
        
        }
        catch(Exception e){
            System.out.println(e);
        }
        return cost;
        
    }
    
    
    
     public static Matrix initialize(int row,int col){
        
      double[][] u=new double[row][col];
      
       
       for(int i=0;i<row;i++){
           for(int j=0;j<col;j++){
               
               u[i][j]=Math.random();
           }
       }
       
       Matrix res=new Matrix(u);
       
       return res;
       
    }    
    
    public static Matrix correlationMatrix(Matrix A){
      
      Matrix res=new Matrix(A.getRowDimension(),A.getRowDimension());
      
      try{
          
      
        Matrix aat=Multiplication(A,A.transpose());
        
        double[] sss=new double[A.getRowDimension()];
        
        for(int i=0;i<A.getRowDimension();i++){
            
            double v=0;
            
            for(int j=0;j<A.getColumnDimension();j++){
                
                v+=Math.pow(A.get(i, j), 2);
            }
            
           if(v != 0) 
            sss[i]=Math.sqrt(v);
           else
               sss[i]=0;
        }
        
        
       
        
        for(int i=0;i<A.getRowDimension();i++){
            for(int j=0;j<A.getRowDimension();j++){
                
                double value;
                
                if(aat.get(i, j) ==0 || (sss[i]*sss[j])==0)
                    value=0;
                else
                   value=aat.get(i, j)/(sss[i]*sss[j]); 
                
                res.set(i, j,value );
            }
        }
        
        return res;
        
      }
      catch(MatException e){
          System.out.println(e);
          
      }
      
      return res;
     
    }  
    
    
    public static double klDivergence(Matrix a,Matrix b){
        
        double error=0;
        
        for(int i=0;i<a.getRowDimension();i++){
            for(int j=0;j<a.getColumnDimension();j++){
                
               double av=a.get(i, j);
               double bv=b.get(i, j);
               
               double e;
               
               if (av == 0 || bv == 0 )
                   e=bv-av;
               else
                    e = (av * Math.log10(av/bv))+bv-av;
               
               
               error+=e;
                
            }
        }
        
        return error;
    }
    
    public static double JaccardSimilarity(String ticket1, String ticket2){
        
        String[] fixedFields1=ticket1.split(",");
        String[] fixedFields2=ticket2.split(",");
        
        double intersection=0;
        double union=0;
        
        for(int i=0;i<fixedFields1.length;i++){
            
            if(fixedFields1[i].equalsIgnoreCase(fixedFields2[i])){
                intersection+=1;
                union+=1;
            }
            else
                union+=2;
            
        }
        
        return (intersection/union);
        
        
    }
    
    public static double CosineSimilarity(String ticket1, String ticket2){
        
        String[] form1=ticket1.split(" ");
        String[] form2=ticket2.split(" ");
        
        Set<String> f1= new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        Set<String> f2= new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        
        double similar=0;
        
        for(int i=0;i<form1.length;i++){
            if(!f1.contains(form1[i]))
                f1.add(form1[i]);
        }
        
        for(int i=0;i<form2.length;i++){
            if(!f2.contains(form2[i])){
                f2.add(form2[i]);
                if(f1.contains(form2[i]))
                    similar+=1;
            }
        }
        
        return (similar/(Math.sqrt(f1.size())*Math.sqrt(f2.size())));
        
    }
    
    public static Matrix merge(Matrix a,Matrix b, Matrix c){
        
        int row=a.getRowDimension();
        int col=a.getColumnDimension()+b.getColumnDimension()+c.getColumnDimension();
        
        Matrix res=new Matrix(row,col);
        
        for(int i=0;i<row;i++){
            int k=0;
            for(int j=0;j<a.getColumnDimension();j++){
                res.set(i, j+k, a.get(i, j));
            }
            k+=a.getColumnDimension();
            for(int j=0;j<b.getColumnDimension();j++){
                res.set(i, j+k, b.get(i, j));
            }
            k+=b.getColumnDimension();
            for(int j=0;j<c.getColumnDimension();j++){
                res.set(i, j+k, c.get(i, j));
            }
            
        }
        
        return res;
        
    }
    
     
     
}
