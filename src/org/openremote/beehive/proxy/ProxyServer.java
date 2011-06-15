package org.openremote.beehive.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

public class ProxyServer extends Thread {

   private static Logger logger = Logger.getLogger(ProxyServer.class);
   private Selector selector;
   private boolean halted = false;
   private Set<ProxyClient> clients = new HashSet<ProxyClient>();

   @Override
   public void run() {
      logger.info("Proxy server starting up");
      ServerSocketChannel server = null;
      try {
         server = ServerSocketChannel.open();
         server.configureBlocking(false);
         logger.info("Binding socket 10000");
         server.socket().bind(new InetSocketAddress(10000));
         selector = Selector.open();
         server.register(selector, SelectionKey.OP_ACCEPT);
         // we must check before we select because we can have been halted before we created the selector
         // so it would have been null and we couldn't call wakeup() on it
         if(halted){
            logger.info("Terminated before entering loop");
            return;
         }
         logger.info("Entering loop");
         while(true){
            logger.info("In select with "+clients.size()+" clients");
            selector.select();
            logger.info("Out of select with "+clients.size()+" clients");
            if(halted)
               break;
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while(keys.hasNext()){
               SelectionKey key = keys.next();
               keys.remove();
               if(!key.isValid())
                  continue;
               if(key.isAcceptable()){
                  logger.info("Accepting a client socket");
                  synchronized(clients){
                     if(halted){
                        // let's not create any more clients if we've been halted, otherwise they will miss the halt notification
                        break;
                     }
                     acceptConnection(server);
                  }
               }
            }
            
         }
         logger.info("Exited loop");
      } catch (IOException e) {
         logger.error("Server died", e);
      }finally{
         if(server != null)
            try {
               server.close();
            } catch (IOException e) {
            }
      }
   }
   
   private void acceptConnection(ServerSocketChannel server) {
      SocketChannel clientSocket;
      try {
         clientSocket = server.accept();
      } catch (IOException e) {
         logger.error("Failed to accept", e);
         return;
      }
      // this can happen if TCP told us we have data but its checksum then failed
      if(clientSocket == null)
         return;
      try{
         logger.info("Got a client socket");
         ProxyClient client = new ProxyClient(this, clientSocket);
         clients.add(client);
         logger.info("Starting client");
         client.start();
         logger.info("Client started");
      }catch(Exception x){
         // make sure we close this one if needed
         try {
            clientSocket.close();
         } catch (IOException e) {
            // ignore
         }
      }
   }

   public void halt() {
      synchronized(clients){
         halted = true;
         if(selector != null){
            selector.wakeup();
         }
         for(Proxy client : clients){
            client.halt();
         }
      }
   }

   public void unregister(Proxy proxyClient) {
      synchronized(clients){
         clients.remove(proxyClient);
      }
   }

   public static void main(String[] args){
      new ProxyServer().run();
   }
}
