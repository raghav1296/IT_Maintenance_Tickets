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
public class MyownException extends Exception{
    
    public MyownException(String msg){
        super(msg);
    }
    
        
}

class MatException extends Exception{
    public MatException(String msg){
        super(msg);
    }
}
