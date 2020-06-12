
import java.net.*;
import java.util.ArrayList;
import java.io.*;

/**
 * Server class
 * @author Rebecca
 *
 */
public class Server {
	
    private static int nClients = 1;
    private static ArrayList<ClientThread> listsOfClients;
    private static Model model;
    private static Packet packet;
    public static class ClientThread extends Thread {
        final Socket client;
        private int clientID;
        private Thread writeThread;
        private ObjectInputStream OiS;
        
        
        
        /**
         * Client, clientID passed to constructor
         * @param client
         * @param clientID
         */
        public ClientThread(Socket client, int clientID) {
            this.client = client;
            this.clientID = clientID;
        }
        
        /**
         * Initial packet passes Model to client, assigns clientID, 
         * 
         */

        public void run() {
            
            try {
                packet = new Packet(model);
                Thread writeThread = new Thread(new Writer(this.client, this.clientID, model, packet));
                writeThread.start();
                OiS = new ObjectInputStream(client.getInputStream());
                while (true) {
                    Object object = OiS.readObject();
                    System.out.println("Packet reading in progress");
                    
                    if (object instanceof Move) {
                        
                            Move move = (Move) OiS.readObject();
                            model.lock();
                            try {
                                model.makeMove(move.fromRow, move.fromCol, move.toRow, move.toCol);
                               
                                if (move.isJump() && model.getJumpsFrom(model.getActivePlayer(), move.toRow, move.toCol) != null) {
                                    Move[] jumps = model.getJumpsFrom(model.getActivePlayer(), move.toRow, move.toCol) ;
                                    for (Move jump: jumps) {
                                        System.out.println(jump);
                                    }
                                    if (jumps != null) {
                                        model.setCanJump(true);
                                    }
                                } else {
                                    model.setCanJump(false);
                                    model.switchPlayer();
                                }
                            } finally {
                                model.unlock();
                            }

                        }

                    if (object.equals("newGame")) {
                        model.startGame();
                    }
                    
                    if (object.equals("resigned")) {
                        model.setResigned(true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}  
    /**
     * Initiates server, awaits connection from client
     * @throws IOException
     */
    public void runServer() throws IOException {
        
        ServerSocket server = new ServerSocket(8765);
            
            model = new Model();
            model.startGame();
            listsOfClients = new ArrayList<ClientThread>();
            System.out.println("Started server, awaiting clients");
            while(true) {
                try {
                    if (nClients > 2) {
                        break;
                    }

                    Socket client = server.accept();
                    System.out.println("Client number: " + nClients + " has now joined.");
                    ClientThread clientThread = new ClientThread(client, nClients);
                    clientThread.start();
                    listsOfClients.add(clientThread);
                    nClients++;
            } catch (IOException e) {
                e.printStackTrace();
            } 
        } 
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            new Server().runServer();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        

    }
} 