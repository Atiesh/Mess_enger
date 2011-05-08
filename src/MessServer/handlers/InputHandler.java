/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer.handlers;

/**
 *
 * @author deadlock
 */
public interface InputHandler<E>  {
    boolean action(E msg);
}
