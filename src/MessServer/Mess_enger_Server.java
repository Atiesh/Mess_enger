/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer;

import java.nio.channels.SelectionKey;
import MessServer.handlers.InputHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import MessServer.interfaces.EventHandlerInterface;

/**
 *
 * @author deadlock
 */
public class Mess_enger_Server implements Runnable{
    private volatile boolean isRunning = true;
    private ServerSocketChannel serverChannel = null;
    private final ConsoleParser serverCli = new ConsoleParser(this);
   
    private Dispatcher mainDispatcher;
    private final EventHandlerInterface eventHandler = new IMEventHandler();
        
    public Mess_enger_Server() throws IOException {
        serverCli.addHandler(new ShutdownHandler());
        mainDispatcher = new Dispatcher();

        System.err.println("NIOServer started: OK");
        Logger.getLogger(Mess_enger_Server.class.getName()).log(Level.FINE, "Server Up.");
    }

    ConsoleParser getServerCli() {
        return serverCli;
    }
    boolean isRunning(){
        return this.isRunning;
    }
    
    @Override
    public void run() {
        
        try {
             serverChannel = ServerSocketChannel.open();
             serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", Config.SERVER_PORT));
             
        } catch (IOException ex) {
            Logger.getLogger(Mess_enger_Server.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        (new Thread(mainDispatcher)).start();
        while(isRunning && serverChannel != null){
            try {
                SocketChannel channel = serverChannel.accept();
                System.err.println("Connection incoming.");
                channel.configureBlocking(false);
                mainDispatcher.register(channel,new Connection(channel, eventHandler));
                
            } catch (IOException ex) {
                // exception is fine
                // Logger.getLogger(SimpleNioServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * @param filenames
     */
    public static void main(String[] args) {
        System.err.println("NIOServer starting.");
        try {
            Mess_enger_Server ns = new Mess_enger_Server();
            (new Thread(ns)).start();
            ConsoleParser cli = ns.getServerCli();
            Scanner in = new Scanner(System.in);
            while(ns.isRunning() && in.hasNext()){
                cli.action(in.next());
            }
            in.close();
        } catch (IOException ex) {
            //Logger.getLogger(SimpleNioServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    
    /**
     * Inner Classes
     */
    
    private class ShutdownHandler implements InputHandler<String>{

        @Override
        public boolean action(String msg) {
            System.err.println("Inputmsg: "+msg);
            String msgs[] = msg.toUpperCase().split(" ");
            if(msgs[0].equals("QUIT")){
                isRunning = false;
                mainDispatcher.stop();
                try {
                    serverChannel.close();
                } catch (IOException ex) {
                    // exception is fine
                }
                return true;
            }
            return false;
        }
        
    }
    private class IMEventHandler implements EventHandlerInterface{
        private ByteBuffer MainByteBuffer;
        private final CharsetDecoder decoderForCurrentCharset = Config.DEFAULT_CHARSET.newDecoder();
        

        public IMEventHandler() {
            MainByteBuffer = ByteBuffer.allocate(Config.SERVER_BUFF_SIZE);
        }

        @Override
        public void onReadable(SelectionKey selectedKey) {
            Connection attachedConnection = (Connection) selectedKey.attachment();
            
            SocketChannel channel = attachedConnection.getChannel();
            MainByteBuffer.clear();
            
            try {
                int numberOfBytesRead = channel.read(MainByteBuffer);
                
                if(numberOfBytesRead > 0){
                    MainByteBuffer.flip();
                    // decode string
                    CharBuffer inputDataBuffer = decoderForCurrentCharset.decode(MainByteBuffer);
                    String inputString = inputDataBuffer.toString();
                    if(inputString.equals("QUIT")){
                        System.err.println("Quitting.");
                        closeConnection(selectedKey);
                    }
                                       
                }
            } catch (IOException ex) {
                Logger.getLogger(Mess_enger_Server.class.getName()).log(Level.SEVERE, null, ex);
                closeConnection(selectedKey);
            }
            
        }

        @Override
        public void onWriteable(SelectionKey selectedKey) {
            Connection con = (Connection) selectedKey.attachment();
            SocketChannel channel = con.getChannel();
            MainByteBuffer.clear();
            
            throw new UnsupportedOperationException("Not implemented YET!");
            
        }

        private void closeConnection(SelectionKey selectedKey) {
            selectedKey.attach(null);
            selectedKey.cancel();            
        }
   
    }
}