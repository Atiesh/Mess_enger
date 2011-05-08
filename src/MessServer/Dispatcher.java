/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer;

import MessServer.interfaces.DispatchInterface;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author deadlock
 */
public class Dispatcher implements DispatchInterface {
    private final Selector _selector;
    private final Object guard = new Object();
    private boolean isRunning = true;
    public Dispatcher() throws IOException {
        _selector = Selector.open();
    }
    
    @Override
    public void run() {
        while(isRunning){
            try {
                
                _selector.select();
                
                synchronized(guard){
                    Iterator<SelectionKey> it = _selector.selectedKeys().iterator();
                    while(it.hasNext()){
                        SelectionKey selectedKey = it.next();
                        it.remove();
                        
                        if(selectedKey.isValid() && selectedKey.isReadable()){
                            Connection connection = (Connection) selectedKey.attachment();
                            connection.getEventhandler().onReadable(selectedKey);
                        }
                        
                        if(selectedKey.isValid() && selectedKey.isWritable()){
                            selectedKey.interestOps(SelectionKey.OP_READ);
                            Connection connection = (Connection) selectedKey.attachment();
                            connection.getEventhandler().onWriteable(selectedKey);
                        }
                       
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void register(SelectableChannel channel, Connection connection)  {
        try {
            synchronized(guard){
                _selector.wakeup();
                channel.register(_selector, SelectionKey.OP_READ, connection);
            }
        } catch (ClosedChannelException ex) {
            Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void stop() {
        isRunning = false;
        try {
            _selector.wakeup();
            _selector.close();
        } catch (IOException ex) {
            Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void announceNeedForWriting(SelectionKey key) {
        synchronized(guard){
            _selector.wakeup();
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void announceNeedForReading(SelectionKey key) {
        synchronized(guard){
            _selector.wakeup();
            key.interestOps(SelectionKey.OP_READ);
        }
    }
    
}
