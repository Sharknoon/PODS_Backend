package pixyel_backend.connection.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import pixyel_backend.Log;

/**
 *
 * @author Josua Frank
 */
public class SocketServer implements Runnable {

    private static ServerSocket SERVER;
    private static final ExecutorService CLIENTTHREADPOOL = Executors.newCachedThreadPool();

    private static final HashMap<Integer, SocketClient> CONNECTEDCLIENTS = new HashMap<>();//All online clients
    private static final HashMap<String, SocketClient> LOGGEDINCLIENTS = new HashMap<>();//All logged in clients

    /**
     * Here the server is going to be started.
     *
     */
    public static void start() {
        Executors.newSingleThreadExecutor().submit(new SocketServer());
    }

    /**
     * Removes the client from the list of connected clients
     *
     * @param client
     * @param socketHashcode The Hash code of the socket as key of the map
     */
    public static void disconnect(SocketClient client, int socketHashcode) {
        LOGGEDINCLIENTS.remove(client.getName());
        if (!CONNECTEDCLIENTS.containsKey(socketHashcode)) {
            Log.logError("Client " + client.getName() + " has not been connected through the normal connection process!!!!!!", SocketServer.class);
            onClientDisconnected(client);
        } else {
            onClientDisconnected(CONNECTEDCLIENTS.get(socketHashcode));
            CONNECTEDCLIENTS.remove(socketHashcode);
        }
    }

    /**
     * Searches infinietly for clients, when a client is connected, it gets its
     * own thread
     */
    private static void listenForClients() {
        boolean loop = true;
        Socket socket;
        while (loop) {
            try {
                socket = SERVER.accept();
                //SERVER.
                //socket.setSoTimeout(5000);
                SocketClient client = new SocketClient(socket);
                CONNECTEDCLIENTS.put(socket.hashCode(), client);
                CLIENTTHREADPOOL.submit(client);
                onClientConnected(client);
            } catch (Exception e) {
                Log.logError("IO Error occured during the setup of the connection to the client: " + e, SocketServer.class);
                loop = false;
            }
        }
    }

    /**
     * Should be called right after a client has logged in
     *
     * @param client The new SocketClient which has logged in
     */
    public static void removePossibleDoubleClients(SocketClient client) {
        String clientName = client.getName();
        if (LOGGEDINCLIENTS.containsKey(clientName)) {
            Log.logInfo("Removing old client from " + clientName, SocketServer.class);
            LOGGEDINCLIENTS.get(clientName).disconnect(true);
        }
        LOGGEDINCLIENTS.put(clientName, client);
    }

    /**
     * Stops the server
     *
     */
    public static void stopServer() {
        if (SERVER != null) {
            try {
                onServerClosing();
                CLIENTTHREADPOOL.shutdown();
                SERVER.close();
                System.exit(0);
            } catch (IOException e) {
                Log.logError("Socket could not be closed: " + e.getMessage(), SocketServer.class);
            }
        }
    }

    /**
     * Startes the server, use {@code Connection.start()} !!
     */
    @Override
    public void run() {
        try {
            SERVER = new ServerSocket(7331, 5000);
        } catch (java.net.BindException e) {
            Log.logError("Adress already binded, is there an existing server running?: " + e.getMessage(), SocketServer.class);
            Log.logError("Shutting down this server to prevent double servers!", SocketServer.class);
            System.exit(0);
            return;
        } catch (IOException ex) {
            Log.logError("Server could not be started: " + ex.getMessage(), SocketServer.class);
            System.exit(0);
            return;
        }
        onServerStarted();
        listenForClients();
    }

    /**
     * Calls this Method just before its closing its socket
     */
    private static void onServerClosing() {
        Log.logInfo("Shutting down the Server...", SocketServer.class);
    }

    /**
     * Calls this Method right after it initialized iteself but before its
     * accepting new clients
     */
    private static void onServerStarted() {
        Log.logInfo("Socketserver reachable on " + SERVER.getLocalSocketAddress(), SocketServer.class);
    }

    /**
     * Calls this method right after the socket connection from a new client is
     * established, the client runs in a seperate thread
     *
     * @param client The client which is connected
     */
    private static void onClientConnected(SocketClient client) {
        Log.logInfo("Client " + client.getName() + " connected", SocketServer.class);
    }

    /**
     * Calls this method just before its closing the socket of the client
     *
     * @param client The client which is going to be disconnected
     */
    private static void onClientDisconnected(SocketClient client) {
        Log.logInfo("Client " + client.getName() + " disconnected", SocketServer.class);
    }

}
