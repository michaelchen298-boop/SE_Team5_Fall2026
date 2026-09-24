// handles user input and updates model on events
//  delegates to model by calling corresponding methods
//  needs a model object
// forwards model data to view for display
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class Controller {

    @FXML
    private Label label;

    private Model model;
    
    @FXML
    private TextField idTextField;
    
    @FXML
    private TextField codenameTextField;
    
    private static final int TRANSMIT_PORT = 7500;
    private static final int RECEIVE_PORT = 7501;

    private DatagramSocket transmitSocket;
    private DatagramSocket receiveSocket;
    // change to correct adress later
    private String networkAddress = "127.0.0.1";
    private volatile boolean listening = false;

    public void update() {
        // Implement the logic to update the controller state
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded
    }
    // UDP getter and setter
    public void setNetworkAddress(String networkAddress) {
    this.networkAddress = networkAddress;
    }
    public String getNetworkAddress() {
    return networkAddress;
    }
    
    // UDP socket methods
    public void setupUdpSockets() {
    try {
        // We don't bind this to a specific port because it is only
        // responsible for sending packets.
        transmitSocket = new DatagramSocket();

        // By not specifying an IP address, Java binds to the wildcard
        // address, allowing packets from any local network interface.
        receiveSocket = new DatagramSocket(RECEIVE_PORT);

        System.out.println("UDP sockets successfully created.");
        System.out.println("Transmitting to " + networkAddress + ":" + TRANSMIT_PORT);
        System.out.println("Listening on port " + RECEIVE_PORT);

        } catch (SocketException e) {
        System.err.println("Could not create UDP sockets: "
                + e.getMessage());
            }
    }

    public void transmit(int equipmentId) {
    try {
        String message = Integer.toString(equipmentId);
        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        InetAddress address = InetAddress.getByName(networkAddress);

        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                address,
                TRANSMIT_PORT
        );

        transmitSocket.send(packet);

        System.out.println("Sent: " + equipmentId);

        } catch (IOException e) {
        System.err.println("Error transmitting UDP packet: "
                + e.getMessage());
        }
}

    // listening thread for recieving packets via UDP sockets
    public void startListening() {
    if (receiveSocket == null) {
        System.err.println("UDP sockets have not been set up.");
        return;
    }

    listening = true;

    Thread receiveThread = new Thread(() -> {
        byte[] buffer = new byte[1024];

        while (listening) {
            try {
                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length
                );

                receiveSocket.receive(packet);

                String message = new String(
                        packet.getData(),
                        0,
                        packet.getLength(),
                        StandardCharsets.UTF_8
                );

                System.out.println("Received: " + message);

                processReceivedData(message);

            } catch (SocketException e) {
                if (listening) {
                    System.err.println(
                            "Socket error: " + e.getMessage()
                    );
                }
            } catch (IOException e) {
                System.err.println(
                        "Error receiving UDP packet: " + e.getMessage()
                );
            }
        }
    });

    receiveThread.setDaemon(true);
    receiveThread.start();
}

    // close sockets
    public void closeUdpSockets() {
    listening = false;

    if (receiveSocket != null && !receiveSocket.isClosed()) {
        receiveSocket.close();
        }

    if (transmitSocket != null && !transmitSocket.isClosed()) {
        transmitSocket.close();
        }
    }
    @FXML
    private void handleButtonAction(ActionEvent event) {
        label.setText("Hello World!");
    }
    //added a connecting method for updating the players codename functionality - michael c
    @FXML
    private void handleUpdatePlayerAction(ActionEvent event) {
        try {
           //get the new codename first
            int playerId = Integer.parseInt(idTextField.getText());
            String newCodename = codenameTextField.getText();
    
           //calls the updateplayer method in model
            boolean success = model.updatePlayer(playerId, newCodename);
    
          
            if (success) {
                label.setText("Success! Player " + playerId + " updated to: " + newCodename);
                System.out.println("Player ID " + playerId + " updated successfully.");
            } else {
                label.setText("Update failed. Player ID may not exist.");
            }
    
        } catch (NumberFormatException e) {
            label.setText("Error: Player ID must be a valid number.");
        }
    }
}
