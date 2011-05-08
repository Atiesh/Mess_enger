/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MessServer;

import MessServer.handlers.InputHandler;
import java.util.ArrayList;
import java.util.List;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;



/**
 *
 * @author deadlock
 */
public class ConsoleParser {
    private final Mess_enger_Server _server;
    private final List<InputHandler<String>> _handlers = new ArrayList<InputHandler<String>>();
    public ConsoleParser(Mess_enger_Server _server) {
        this._server = _server;
    }
    public int addHandler(InputHandler handler){
        _handlers.add(handler);
        return _handlers.indexOf(handler);
    }
    public void removeHandler(int handlerId){
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    public int action(String trigger){
        int counter = 0;
        for(InputHandler handler : _handlers){
            if(handler.action(trigger)) counter++;
        }
        return counter;
    }
}
