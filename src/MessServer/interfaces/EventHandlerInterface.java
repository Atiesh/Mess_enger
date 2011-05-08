/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer.interfaces;

import java.nio.channels.SelectionKey;

/**
 *
 * @author deadlock
 */
public interface EventHandlerInterface {
    void onReadable(SelectionKey selectionKey);
    void onWriteable(SelectionKey selectionKey);

}
