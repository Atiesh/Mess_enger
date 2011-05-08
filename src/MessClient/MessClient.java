/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *
 * @author deadlock
 */
public class MessClient extends Thread {
    public static final int BUFF_SIZE = 512;
    public static void main(String[] args) throws IOException, InterruptedException{
        if(args.length < 2){
            System.out.println("Usage: NIOClient <filename on server> <local filename>");
            return;
        }
        SocketChannel sock = SocketChannel.open();
        sock.connect(new InetSocketAddress("localhost",6999));
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder encoder = charset.newEncoder();
        sock.configureBlocking(false);
        String msg = args[0];
        CharBuffer cbuff = CharBuffer.wrap(msg);
        ByteBuffer buff = encoder.encode(cbuff);
        sock.write(buff);
        buff.clear();
        sleep(1000);
        buff = ByteBuffer.allocate(BUFF_SIZE);
        FileChannel fc = new FileOutputStream(args[1]).getChannel();
        
        System.err.println("Downloading");
        int ile = sock.read(buff);
        buff.flip();
        
        long size = buff.getLong();
        
        
        long sum = ile-8;
        System.err.println("Size: "+size);
        System.err.println("Sum: "+sum);
        fc.write(buff);
        buff.clear();
        while(sum < size){
            ile = sock.read(buff);
            if(ile > 0){
                buff.flip();
                System.err.print(".");
                fc.write(buff);
                buff.clear();
                sum += ile;
            }
        }
        System.err.println("Downloaded : OK");
        fc.close();
        buff.clear();
        sleep(1000);
        System.err.println("Quitting.");
        msg = "QUIT";
        cbuff = CharBuffer.wrap(msg);
        buff = encoder.encode(cbuff);
        sock.write(buff);
       
        sleep(3000);
        System.err.println("Quit : OK");
        sock.close();
        
    }
}
