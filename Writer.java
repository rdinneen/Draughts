import java.net.Socket;
import java.util.Random;
import java.io.IOException;
import java.io.ObjectOutputStream;;

/**
 * Writer class
 * @author rebec
 *
 */

public class Writer implements Runnable {
    private Socket socket;
    private Model model;
    private int clientID;
    public Packet packet;
    /**
     * Packet, assigned clientID, socket from client, model variables from server passed to constructor 
     */
    public Writer(Socket sock, int clientID, Model model, Packet packet) {
        this.socket = sock;
        this.packet = packet;
        this.clientID = clientID;
        this.model = model;
    }
    public void run() {
        try {
            ObjectOutputStream OoS = new ObjectOutputStream(socket.getOutputStream());
            while(true){
                packet.setID(this.clientID);
                OoS.writeObject(packet);
                System.out.println("Game state packet sent");
                OoS.flush();
                OoS.reset();
                Thread.sleep(200);
            }      
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}