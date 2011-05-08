/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import MessServer.interfaces.EventHandlerInterface;

/**
 *
 * @author deadlock
 */
public class Connection {
    private SocketChannel channel;
    private EventHandlerInterface eventhandler;
    private FileChannel fc;

    
    public Connection(SocketChannel channel, EventHandlerInterface eventhandler) {
        this.channel = channel;
        this.eventhandler = eventhandler;
    }

    public EventHandlerInterface getEventhandler() {
        return eventhandler;
    }

    public SocketChannel getChannel() {
        return channel;
    }



    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public void setFc(FileChannel fc) {
        this.fc = fc;
    }

    public FileChannel getFc() {
        return fc;
    }


    
    
    
}
