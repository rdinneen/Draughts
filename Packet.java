import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Packet implements Serializable {
    /**
     * Packet establishes communication between server/server, holds status codes for updates
     * @author Rebecca
     */
    private static final long serialVersionUID = 1L;

    private int clientID;
    private Model model;
    private boolean moveMade;
    private int statusCode;
    final Lock lock = new ReentrantLock();
    public Packet(Model model) {
        this.model = model;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isMoveMade() {
        return moveMade;
    }

    public void setMoveMade(boolean moveMade) {
        this.moveMade = moveMade;
    }

    public int getID() {
        return this.clientID;
    }
    public void setID(int id) {
        this.clientID = id;
    }
    public void setModel(Model model) {
            this.model = model;
        
    }
    public Model getModel() {
        return this.model;
    }
    public void lock() {
        lock.lock();
    }
    public void unlock() {
        lock.unlock();
    }
}