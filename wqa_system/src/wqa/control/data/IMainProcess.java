/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.control.data;

/**
 *
 * @author chejf
 */
public interface IMainProcess<T> {

    //0-100        
    public void SetValue(float pecent);
    
    public void Finish(T result);
}
