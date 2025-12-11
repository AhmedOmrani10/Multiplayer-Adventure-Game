package network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
// MANAGES ALL NETWORK OPERATIONS FOR A PLAYER

public class NetworkManager {
    private int playerId;
    private int port;
    private List<PeerConnection> peers;
    private ServerSocket serverSocket;
    private LamportClock lamportClock;
    private RequestQueue requestQueue;
    private boolean running;
    private GameStateCallback callback;
 // Constructor: initializes a player in the network
    // Sets up Lamport clock, request queue, peers list, and callback
    public NetworkManager(int playerId, int port, GameStateCallback callback) {
        this.playerId = playerId;
        this.port = port;
        this.callback = callback;
        this.peers = new CopyOnWriteArrayList<>();
        this.lamportClock = new LamportClock();
        this.requestQueue = new RequestQueue();
        this.running = true;
    }
    // Starts the server socket to accept incoming connections
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        //listens for incoming connections
        new Thread(this::acceptConnections).start();
        System.out.println("Player " + playerId + " started on port " + port);
    }
    // Connects to a peer by IP and port and sends an introduction message

    public void connectToPeer(String host, int peerPort, int peerId) {
        try {
        	//connect to peer 
            Socket socket = new Socket(host, peerPort);
            PeerConnection peer = new PeerConnection(socket, peerId, this);
            peers.add(peer);
            //continuously listen for messages
            peer.start();
            
            // Send introduction
            Message intro = new Message(MessageType.CONNECT, playerId, lamportClock.increment(), null);
            peer.sendMessage(intro);
            
            System.out.println("Connected to peer " + peerId);
        } catch (IOException e) {
            System.err.println("Failed to connect to peer: " + e.getMessage());
        }
    }
    
    private void acceptConnections() {
        while (running) {
            try {
            	// Wait for a new player (peer) to connect. 
                // This line blocks until a connection is made.
                Socket socket = serverSocket.accept();
                // The -1 means we don’t know the peer's ID yet; it will be set later.
                PeerConnection peer = new PeerConnection(socket, -1, this);
                peers.add(peer);
             // Start listening to messages from this peer
                peer.start();
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void requestCriticalSection(CriticalSectionTask task) {
        int timestamp = lamportClock.increment();
        Request request = new Request(playerId, timestamp);
        requestQueue.addRequest(request);
        
        // Broadcast REQUEST to all peers
        Message msg = new Message(MessageType.REQUEST, playerId, timestamp, null);
        broadcastMessage(msg);
        
        // Wait for replies from all peers
        new Thread(() -> {
            waitForReplies(request);
            // Execute critical section
            task.execute();
            // Release critical section
            releaseCriticalSection();
        }).start();
    }
    
    private void waitForReplies(Request request) {
        while (!canEnterCriticalSection(request)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private boolean canEnterCriticalSection(Request request) {
        // Check if we have replies from all peers
        if (request.getRepliesCount() < peers.size()) {
            return false;
        }
        // Check if our request has the earliest timestamp
        return requestQueue.isEarliest(request);
    }
    
    private void releaseCriticalSection() {
        requestQueue.removeRequest(playerId);
        Message msg = new Message(MessageType.RELEASE, playerId, lamportClock.increment(), null);
        broadcastMessage(msg);
    }
    //7
    public void handleMessage(Message msg, PeerConnection sender) {
    	// Update local Lamport clock based on the timestamp of the received message
        // Ensures all events in the network are ordered consistently
        lamportClock.update(msg.getTimestamp());
        
        switch (msg.getType()) {
            case CONNECT:
            	 // A new peer just connected to us
                // Set their ID in our PeerConnection object
                sender.setPeerId(msg.getSenderId());
                System.out.println("Peer " + msg.getSenderId() + " connected");
                break;
                
            case REQUEST:
            	// Another peer wants to enter the critical section
                // 1. Add their request to our local queue
                Request req = new Request(msg.getSenderId(), msg.getTimestamp());
                requestQueue.addRequest(req);
            	// 2. Send a REPLY back to grant permission
                Message reply = new Message(MessageType.REPLY, playerId, lamportClock.increment(), null);
                sender.sendMessage(reply);
                break;
                
            case REPLY:
            	// Received a REPLY from a peer for our critical section request
                // Mark that this peer has granted us permission
                requestQueue.addReply(msg.getSenderId());
                break;
                
            case RELEASE:
            	// A peer has finished executing their critical section
                // Remove their request from our queue so others can enter
                requestQueue.removeRequest(msg.getSenderId());
                break;
                
            case GAME_STATE:
            	 // A peer sent an updated game state (e.g., player moved)
                // Notify the game logic/UI through the callback
                if (callback != null) {
                    callback.onGameStateUpdate(msg.getData());
                }
                break;
        }
    }
    
    public void broadcastGameState(String gameState) {
        Message msg = new Message(MessageType.GAME_STATE, playerId, lamportClock.increment(), gameState);
        broadcastMessage(msg);
    }
    
    private void broadcastMessage(Message msg) {
        for (PeerConnection peer : peers) {
            peer.sendMessage(msg);
        }
    }
    
    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            for (PeerConnection peer : peers) {
                peer.close();
            }
        } catch (IOException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public interface GameStateCallback {
        void onGameStateUpdate(String data);
    }
    
    public interface CriticalSectionTask {
        void execute();
    }
}
//1
class LamportClock {
    private int time;
    
    public LamportClock() {
        this.time = 0;
    }
    //synchronized makes sure only one thread executes the method at a time.
    public synchronized int increment() {
        return ++time;
    }
    
    public synchronized void update(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
    }
    
    public synchronized int getTime() {
        return time;
    }
}

//2
//////////////////////////////////////////////////////////////////
//Represents a request by a player to enter the critical section
//Used in Lamport's distributed mutual exclusion algorithm
//The Comparable<Request> interface is used so that Request objects can be compared to each other
////////////////////////////////////////////////////////////////
class Request implements Comparable<Request> {
    private int processId; // ID of the player/process making the request
    private int timestamp;// Lamport timestamp of the request
    private Set<Integer> replies; // Set of peers that have replied to this request
    
    public Request(int processId, int timestamp) {
        this.processId = processId;
        this.timestamp = timestamp;
        //hashmap No duplicates are allowed
        this.replies = ConcurrentHashMap.newKeySet();// Thread-safe set for replies
    }
    
    public void addReply(int peerId) {
        replies.add(peerId);
    }
    
    public int getRepliesCount() {
        return replies.size();
    }
    
    public int getProcessId() {
        return processId;
    }
    
    public int getTimestamp() {
        return timestamp;
    }
    
    @Override
    public int compareTo(Request other) {
        if (this.timestamp != other.timestamp) {
            return Integer.compare(this.timestamp, other.timestamp);
        }
        return Integer.compare(this.processId, other.processId);
    }
}
//3
class RequestQueue {
    // Priority queue to keep requests sorted by timestamp (earliest first)
    private PriorityQueue<Request> queue;
    // Map for fast access to requests by process ID
    private Map<Integer, Request> requestMap;
    
    public RequestQueue() {
        this.queue = new PriorityQueue<>();// Add to priority queue (sorted by timestamp & processId)
        this.requestMap = new ConcurrentHashMap<>();// Add to map for fast lookup
    
    }
    
    public synchronized void addRequest(Request request) {
        queue.add(request);
        requestMap.put(request.getProcessId(), request);
    }
    
    public synchronized void removeRequest(int processId) {
        Request req = requestMap.remove(processId);
        if (req != null) {
            queue.remove(req);
        }
    }
    
    public synchronized void addReply(int peerId) {
        for (Request req : requestMap.values()) {
            req.addReply(peerId);
        }
    }
    
    public synchronized boolean isEarliest(Request request) {
        return !queue.isEmpty() && queue.peek().equals(request);
    }
}
//4
enum MessageType {
    CONNECT, // A peer is connecting to the network
    REQUEST,// A peer requests access to the critical section
    REPLY,// A peer replies to a request granting permission
    RELEASE, // A peer releases the critical section
    GAME_STATE // A peer sends updated game state information
}
//5
//Implements Serializable so it can be sent over ObjectOutputStream
class Message implements Serializable {
    private MessageType type;
    private int senderId;
    private int timestamp;
    private String data;
    
    public Message(MessageType type, int senderId, int timestamp, String data) {
        this.type = type;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.data = data;
    }
    
    public MessageType getType() { return type; }
    public int getSenderId() { return senderId; }
    public int getTimestamp() { return timestamp; }
    public String getData() { return data; }
}
//6
//Represents a network connection to a single peer
//Handles sending and receiving messages over a socket
class PeerConnection {
    private Socket socket;// Socket for communication with the peer
    private int peerId;
    private NetworkManager manager;
    private ObjectOutputStream out;// Stream to send messages to the peer
    private ObjectInputStream in;// Stream to receive messages from the peer
    private boolean running;
    
    public PeerConnection(Socket socket, int peerId, NetworkManager manager) throws IOException {
        this.socket = socket;
        this.peerId = peerId;
        this.manager = manager;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.running = true;
    }
 // Starts a new thread to continuously listen for incoming messages
    public void start() {
        new Thread(this::receiveMessages).start();
    }
    
    private void receiveMessages() {
        while (running) {
            try {
                Message msg = (Message) in.readObject();
                manager.handleMessage(msg, this);//go to 7
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    System.err.println("Error receiving message: " + e.getMessage());
                    running = false;
                }
            }
        }
    }
    
    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();//forces everything you wrote to be sent immediately instead of waiting.
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    public void setPeerId(int peerId) {
        this.peerId = peerId;
    }
    
    public int getPeerId() {
        return peerId;
    }
    
    public void close() throws IOException {
        running = false;
        socket.close();
    }
}