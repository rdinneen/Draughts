import java.net.*;
import java.io.*;

/**
 * Main class for client program
 * @author Rebecca
 *
 */

public class Client {
    private static Model model; 
    private static Packet packet;
    
    /**
     * Main method initialises connection to server, creates input/output streams
     * @param args
     */

    public static void main(String[] args) {
        Socket connection = null;
        GUI view = null;
        int clientID;
        ObjectInputStream OiS; 
        ObjectOutputStream OoS;

        try {
            connection = new Socket("127.0.0.1", 8765);
            OiS = new ObjectInputStream(connection.getInputStream());
            OoS = new ObjectOutputStream(connection.getOutputStream());
            packet = (Packet) OiS.readObject();
            clientID = packet.getID();
            model = packet.getModel();
            view = new GUI(model, clientID, OoS);
            System.out.println("GUI Launched - client:" + clientID+ " ready");
            while (true) {
                System.out.println("Client: "+ clientID + " is reading packet"); 
                packet = (Packet) OiS.readObject();
                view.updateModel(packet.getModel());
                view.updateBoard();
             }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}