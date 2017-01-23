/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.distributed;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class NetworkMessageBuffer {
    Hashtable<String, LinkedBlockingQueue> mbuffer;
    static final int MAXQUEUE = 5; //Max input buffer size by process

    private static class Holder {
        static final NetworkMessageBuffer INSTANCE = new NetworkMessageBuffer();
    }

    private NetworkMessageBuffer() {
        mbuffer = new Hashtable<>();
    }

    /**
     *
     * @return
     */
    public static NetworkMessageBuffer getInstance() {
        return Holder.INSTANCE;
    }

    /**
     *
     * @param pid
     */
    public void createBuffer(String pid){
        mbuffer.put(pid, new LinkedBlockingQueue());
    }
    
    /**
     *
     * @param pid
     * @param msg
     */
    public void putMessage(String pid, String[] msg) {
        mbuffer.get(pid).add(msg);
    }

    // Called by Consumer

    /**
     *
     * @param pid
     * @return
     */
    public String[] getMessage(String pid) {
        return (String[]) (mbuffer.get(pid).poll());
    }

}
