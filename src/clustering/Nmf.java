/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import Jama.Matrix;
import java.sql.Timestamp;

/**
 *
 * @author rajaprabu.TRN
 */

class NormalizeNmf{
    
    Matrix x,u,v;
    
    NormalizeNmf(Matrix original ,Matrix umatrix ,Matrix vmatrix){
        
        x=original;
        u=umatrix;
        v=vmatrix;
    }
    
    public void newnorm(){
        
        /*
        
            The following algorithms implemented from
            Learing Topics in Short Texts By Non-negative matrix Factorization
            on Term Correlation Matrix
        By Xiaohui yan, Jiafeng Guo , Shenghua liu , Xueqi Cheng , Yanfeng Wang.
        
        */
        
        
        
        try{
            
            Matrix vu=Matoperation.Multiplication(v, u);
            Matrix vu_inv=vu.inverse();
            Matrix xu=Matoperation.Multiplication(x, u);
            
            Matrix res=Matoperation.Multiplication(xu, vu_inv);
            
            for(int i=0;i<u.getRowDimension();i++){
                for(int j=0;j<u.getColumnDimension();j++){
                    if(res.get(i, j)>0)
                        u.set(i, j, res.get(i, j));
                    else
                        u.set(i,j,0);
                }
            }
            
            v=u.transpose();
            
            
        }
        catch(MatException e){
            System.out.println(e);
        }
        catch(Exception e1){
            System.out.println(e1);
        }
    }
    
    public void normnmf(){
        
        
    try{    
        Matrix ut=u.transpose();
        Matrix utx=Matoperation.Multiplication(ut,x);
        Matrix utuv=Matoperation.Multiplication(ut,Matoperation.Multiplication(u,v));
        
        for(int i=0;i<v.getRowDimension();i++){
            for(int j=0;j<v.getColumnDimension();j++){
               if(v.get(i,j) != 0 && utx.get(i,j) != 0 && utuv.get(i, j) != 0) 
                v.set(i, j, v.get(i, j)*utx.get(i, j)/utuv.get(i, j));
               else
                   v.set(i, j, 0);
            }
        }
        
        Matrix vt=v.transpose();
        Matrix xvt=Matoperation.Multiplication(x,vt);
        Matrix uvvt=Matoperation.Multiplication(u,Matoperation.Multiplication(v,vt));
        
        for(int i=0;i<u.getRowDimension();i++){
            for(int j=0;j<u.getColumnDimension();j++){
               if(u.get(i, j)!=0 && xvt.get(i, j) != 0 && uvvt.get(i, j) != 0) 
                u.set(i,j,u.get(i, j)*xvt.get(i, j)/uvvt.get(i, j));
               else
                   u.set(i,j,0);
            }
        }
        
        Matrix eta=new Matrix(v.getRowDimension(),v.getColumnDimension());
        
        utuv=Matoperation.Multiplication(ut,Matoperation.Multiplication(u,v));
        
        for(int i=0;i<eta.getRowDimension();i++){
            for(int j=0;j<eta.getColumnDimension();j++){
               if(v.get(i,j) != 0 && utuv.get(i,j) != 0) 
                eta.set(i, j, v.get(i, j)/utuv.get(i, j));
               else
                   eta.set(i,j,0);
            }
        }
        
        for(int i=0;i<v.getRowDimension();i++){
            for(int j=0;j<v.getColumnDimension();j++){
                
                v.set(i, j, v.get(i, j)+(eta.get(i, j)*(utx.get(i, j)-utuv.get(i, j))));
            }
        }
        
    }
    catch(MatException e){
        System.out.println(e);
    }
    catch(Exception e1){
        System.out.println(e1);
    }
        
    }
    
    public void normSecondNmf(){
        
     try{    
        /*
        Matrix uv=Matoperation.Multiplication(u, v);
        Matrix ut=u.transpose();
        Matrix inv=new Matrix(ut.getRowDimension(),ut.getRowDimension());
        
        for(int i=0;i<ut.getRowDimension();i++){
            for(int j=0;j<ut.getRowDimension();j++){
                
                if(i == j)
                    inv.set(i, j, 1);
                else
                    inv.set(i, j, 0);
            }
        }
        
        Matrix invut=Matoperation.Multiplication(inv, ut);
        
        for(int i=0;i<v.getRowDimension();i++){
            for(int j=0;j<v.getColumnDimension();j++){
                if(uv.get(i, j)!=0 && ut.get(i, j)!=0 && invut.get(i, j)!=0 && v.get(i, j)!=0){
                    
                    double value=v.get(i, j)*(((x.get(i, j)/uv.get(i, j))*ut.get(i, j))/invut.get(i, j));
                    
                    v.set(i, j, value);
                    
                }
                else{
                    v.set(i,j,0);
                }
            }
        }
        
        */
        
        /*
                The following algorithms are implemented from 
                Algorithms for Non-Negative Matrix factorization
                By Daniel D lee and H. Sebastian seung.
                pls refer to this paper.
        
        */
        
        
        Matrix ut=u.transpose();
        Matrix utx=Matoperation.Multiplication(ut,x);
        Matrix utuv=Matoperation.Multiplication(ut,Matoperation.Multiplication(u,v));
        
        for(int i=0;i<v.getRowDimension();i++){
            for(int j=0;j<v.getColumnDimension();j++){
               if(v.get(i,j) != 0 && utx.get(i,j) != 0 && utuv.get(i, j) != 0) 
                v.set(i, j, v.get(i, j)*utx.get(i, j)/utuv.get(i, j));
               else
                   v.set(i, j, 0);
            }
        }
        
                
        Matrix eta=new Matrix(v.getRowDimension(),v.getColumnDimension());
        
        utuv=Matoperation.Multiplication(ut,Matoperation.Multiplication(u,v));
        
        for(int i=0;i<eta.getRowDimension();i++){
            for(int j=0;j<eta.getColumnDimension();j++){
               if(v.get(i,j) != 0 && utuv.get(i,j) != 0) 
                eta.set(i, j, v.get(i, j)/utuv.get(i, j));
               else
                   eta.set(i,j,0);
            }
        }
        
        for(int i=0;i<v.getRowDimension();i++){
            for(int j=0;j<v.getColumnDimension();j++){
                
                v.set(i, j, v.get(i, j)+(eta.get(i, j)*(utx.get(i, j)-utuv.get(i, j))));
            }
        }
        
    }
    catch(MatException e){
        System.out.println(e);
    }
    catch(Exception e1){
        System.out.println(e1);
    }
        
        
    }
    
    public Matrix getX(){
        
        return x;        
    }
    
    
    public Matrix getU(){
        return u;
    }

    public Matrix getV(){
        return v;
    }

    
}

public class Nmf {
    
    
    Matrix x,u,v;
    
    Nmf(Matrix X,int topics){
        
       int row=X.getRowDimension();
       int col=X.getColumnDimension();
       
      Matrix uint=Matoperation.initialize(row,topics);       
             
      // NormalizeNmf n=new NormalizeNmf(X,Matoperation.initialize(row,topics),Matoperation.initialize(topics,col));
        NormalizeNmf n=new NormalizeNmf(X,uint,uint.transpose());
       double error=0.001;
       double l_er;
       int it;
       int count=0;
       double[] er=new double[5];
        
       Timestamp intitime=new Timestamp(System.currentTimeMillis());
       long initial=intitime.getTime();
        
        
        
        
        for(it=0;it<1000;it++){
            
          try{    
            
           // n.normnmf();
            n.newnorm();
            x=n.getX();
            u=n.getU();
            v=n.getV();
            
            //l_erkl=Matoperation.klDivergence(x,Matoperation.Multiplication(u,v));
            l_er=Matoperation.forbenius(x,Matoperation.Multiplication(u,v));
            //System.out.println("==================================================");
            //System.out.println("iteration :"+it+"  error:"+l_er);
            //System.out.println("kl divergence: error:"+l_erkl);
              
            //System.out.println("==================================================");
            
            Timestamp f=new Timestamp(System.currentTimeMillis()); 
            long fin=f.getTime();
            
            long diff_time=(fin-initial)/1000;
            
            if(l_er < error || diff_time>60)
                break;
            
            if(count < 4){
                
               er[count++]=l_er;
            }
            else{
                er[count]=l_er;
                double sum=0;
                for(int l=0;l<4;l++)
                    sum+=er[l]-er[l+1];
                double avg=sum/4;
                if(avg < 0.0001){
                    break;
                }
                for(int l=0;l<4;l++)
                    er[l]=er[l+1];
            }
            
          }
          catch(MatException e){
            System.out.println(e);
          }
            
        }
    
        
       
       
    }
    
    
    Nmf(Matrix X,Matrix U,int topics){
        
       int row=X.getRowDimension();
       int col=X.getColumnDimension();
       
             
             
       NormalizeNmf n=new NormalizeNmf(X,U,Matoperation.initialize(topics,col));
        
       double error=0.001;
       double l_er;
       int it;
       int count=0;
       double[] er=new double[5];
        
       Timestamp intitime=new Timestamp(System.currentTimeMillis());
       long initial=intitime.getTime();
        
        
        
        
        for(it=0;it<1000;it++){
            
          try{    
            
            n.normSecondNmf();
            
            x=n.getX();
            u=n.getU();
            v=n.getV();
            
            //l_erkl=Matoperation.klDivergence(x,Matoperation.Multiplication(u,v));
            l_er=Matoperation.forbenius(x,Matoperation.Multiplication(u,v));
           // System.out.println("==================================================");
            //System.out.println("iteration :"+it+"  error:"+l_er);
            //System.out.println("kl divergence: error:"+l_erkl);
              
            //System.out.println("==================================================");
            
            Timestamp f=new Timestamp(System.currentTimeMillis()); 
            long fin=f.getTime();
            
            long diff_time=(fin-initial)/1000;
            
            if(l_er < error || diff_time>60)
                break;
            
            if(count < 4){
                
               er[count++]=l_er;
            }
            else{
                er[count]=l_er;
                double sum=0;
                for(int l=0;l<4;l++)
                    sum+=er[l]-er[l+1];
                double avg=sum/4;
                if(avg < 0.0001){
                    break;
                }
                for(int l=0;l<4;l++)
                    er[l]=er[l+1];
            }
            
          }
          catch(MatException e){
            System.out.println(e);
          }
            
        }
       
        
    }
    
    
    public Matrix getX(){
        
        return x;        
    }
    
    
    public Matrix getU(){
        return u;
    }

    public Matrix getV(){
        return v;
    }
    

   
}
    

