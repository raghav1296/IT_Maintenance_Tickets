/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 *
 * @author rajaprabu.TRN
 */

/*

    In this class we are performing the Optimization of the result produced by SVD.
    
    It is done by removing the eigenvalue which is less than 0.001 present in the S matrix
    and also removing corresponding columns in U matrix
    and also removing corresponding rows in V matix.


    

*/

public class Optimizedsvd {
    
    double[][] u1;
    double[][] v1;
    double[] s1;
    double[][] s_m;
    double[] optimized_s;
    
    Matrix u,v,s;
    
    boolean[] zero;
    
    int r_u,r_v,c_u,c_v,l_s;
    
    int row_u,row_v,col_u,col_v,len_s;

    int czero=0;//count the zero singular value;
    
    Matrix vt;
    
    public Optimizedsvd(Matrix m){
       
        SingularValueDecomposition svd = new SingularValueDecomposition(m); //performing the svd in package
        
        //System.out.println("after normal svd");
    
        vt=svd.getV().transpose();
        
        r_u=svd.getU().getRowDimension();
        c_u=svd.getU().getColumnDimension();
        r_v=vt.getRowDimension();
        c_v=vt.getColumnDimension();
        l_s=svd.getSingularValues().length;
        
       // System.out.println("rowU:"+r_u+"colU:"+c_u+"rowV:"+r_v+"colV:"+c_v+"l_s:"+l_s);
        
       //  System.out.println("after normal svd");
        s1=svd.getSingularValues();
        zero=new boolean[l_s];
        
        for(int i=0;i<l_s;i++){
            if(s1[i] <= 0.001){
                //System.out.println("False");
                zero[i]=true;
                czero++;
            }
            else{
                //System.out.println("True");
                zero[i]=false;
            }
        }
        
        
        //row_u=r_u;
        //col_u=c_u-czero;
        //row_v=r_v-czero;
        //col_v=c_v;
        len_s=l_s-czero;
        
      /*  //s1=new double[svd.getSingularValues().length];
       u1=new double[row_u][col_u];
       v1=new double[row_v][col_v];
       int k=0;
       for(int j=0;j<c_u;j++){
           if(!zero[j]){
               for(int i=0;i<r_u;i++){
                   u1[i][k]=svd.getU().get(i, j);
               }
               k++;
           }
       }
        System.out.println("after normal svd");
       k=0;
       for(int i=0;i<r_v;i++){
           if(!zero[i]){
               for(int j=0;j<c_v;j++){
                   v1[k][j]=vt.get(i, j);
               }
               k++;
           }
       }
       k=0;
        System.out.println("after normal svd");
       s_m = new double[len_s][len_s];
       
       optimized_s=new double[len_s];
       
       for(int i=0;i<l_s;i++)
           if(!zero[i])
               optimized_s[k++]=s1[i];
       
       for(int i=0;i<len_s;i++){
           s_m[i][i]=optimized_s[i];
       }
       
        System.out.println("after normal svd");
       
       
        
        u=new Matrix(u1);
        v=new Matrix(v1);
        s=new Matrix(s_m);
         System.out.println("after normal svd");*/
    }
    
    /*
    public Matrix getU(){
        return u;
        
    }
    
    public Matrix getV(){
        return v;
        
    }
    
    public Matrix getS(){
        return s;
        
    }
    
    public double[] singularValue(){
        return optimized_s;
    }*/
    
    public int kvalue(){
        return len_s;
    }
    
    
}
