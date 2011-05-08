/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer.interfaces;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import MessServer.Connection;

/**
 *
 * @author deadlock
 */
public interface DispatchInterface extends Runnable {
    void register(SelectableChannel chan, Connection con) ;
    void announceNeedForWriting(SelectionKey key);
    void announceNeedForReading(SelectionKey key);
    void stop();
}
