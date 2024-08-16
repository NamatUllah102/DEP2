/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapplication;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author Admin
 */
class SignUP extends JFrame
{
     private JTextField usernameField, fatherNameField, dobField, passwordField;
        private JButton prevButton, confirmButton;
      public SignUP()
        {
             setTitle("Create User");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(6, 2));

            add(new JLabel("Username:"));
            usernameField = new JTextField();
            add(usernameField);

            add(new JLabel("Father Name:"));
            fatherNameField = new JTextField();
            add(fatherNameField);

            add(new JLabel("Date of Birth:"));
            dobField = new JTextField();
            add(dobField);

            add(new JLabel("Password:"));
            passwordField = new JTextField();
            add(passwordField);

            prevButton = new JButton("Prev");
            confirmButton = new JButton("Confirm");

            add(prevButton);
            add(confirmButton);
             prevButton.addActionListener(e -> {
                dispose();
                new ChatApplication();
            });
              confirmButton.addActionListener(e -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("userData.txt", true))) {
                    writer.write(usernameField.getText() + "," + fatherNameField.getText() + "," + dobField.getText() + "," + passwordField.getText());
                    writer.newLine();
                    writer.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dispose();
                new ChatApplication();
            });

            setVisible(true);

        }
}

class Signin extends JFrame
{
     private JTextField passwordField;
        private JButton prevButton, confirm;
   public Signin()
    {
         setTitle("Signin");
            setSize(500, 500);
             setLayout(new GridLayout(3, 2));

            add(new JLabel("Password:"));
            passwordField = new JTextField();
            add(passwordField);

            prevButton = new JButton("Prev");
            confirm = new JButton("Confirm");
 add(prevButton);
               add(confirm);
           
         

            prevButton.addActionListener(e -> {
                dispose();
                new ChatApplication();
            });
            
             confirm.addActionListener(e -> {
                String inputPassword = passwordField.getText();
                try (BufferedReader reader = new BufferedReader(new FileReader("userData.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts[3].equals(inputPassword)) {
                          setVisible(false);
           new Others();
                          
                        }
                     
                    else
                        {
                    JOptionPane.showMessageDialog(this, "Password does not match.");
                        }
                                                    
                    }
                   
                   
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

                          
        
        
           setVisible(true);
    }
}


class Others {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet(); // To keep track of all client writers
    private static boolean runningAsServer = true; // Flag to indicate whether to run as server or client

  Others()
{
 if (runningAsServer) {
            startServer();
        } else {
            startClient("localhost", PORT); // Replace "localhost" with the server address if needed
        }
}
    

    private static void startServer() {
        System.out.println("Chat server started...");
        ExecutorService pool = Executors.newFixedThreadPool(10); // Thread pool to handle multiple clients

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept new client connection
                pool.execute(new ClientHandler(clientSocket)); // Handle each client in a separate thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out); // Add client writer to the set
                }

                String message;
                while ((message = in.readLine()) != null) { // Read messages from client
                    System.out.println("Received: " + message);
                    broadcast(message); // Broadcast message to all clients
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out); // Remove client writer from the set
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message); // Send message to all clients
                }
            }
        }
    }

    private static void startClient(String serverAddress, int serverPort) {
        try {
            Socket socket = new Socket(serverAddress, serverPort); // Connect to the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Start a thread to listen for incoming messages
            new Thread(new IncomingMessageListener(in)).start();

            Scanner scanner = new Scanner(System.in);
            String message;

            System.out.println("Connected to server. Type your messages:");

            // Read messages from user and send them
            while (true) {
                message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) { // Exit condition
                    break;
                }
                out.println(message); // Send message to server
            }

            socket.close(); // Close connection
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class IncomingMessageListener implements Runnable {
        private BufferedReader in;

        public IncomingMessageListener(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message); // Print received messages
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

 class DeleteFrame extends JFrame {
        private JTextField passwordField;
        private JButton prevButton, confirmButton;

        public DeleteFrame() {
            setTitle("Delete User");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(3, 2));

            add(new JLabel("Password:"));
            passwordField = new JTextField();
            add(passwordField);

            prevButton = new JButton("Prev");
            confirmButton = new JButton("Confirm");

            add(prevButton);
            add(confirmButton);

            prevButton.addActionListener(e -> {
                dispose();
                new ChatApplication();
            });

            confirmButton.addActionListener(e -> {
                String inputPassword = passwordField.getText();
                java.util.List<String> remainingLines = new ArrayList<>();
                boolean passwordMatched = false;

                try (BufferedReader reader = new BufferedReader(new FileReader("userData.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts[3].equals(inputPassword)) {
                            passwordMatched = true;
                        } else {
                            remainingLines.add(line);
                        }
                    }

                    if (passwordMatched) {
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter("userData.txt"))) {
                            for (String remainingLine : remainingLines) {
                                writer.write(remainingLine);
                                writer.newLine();
                            }
                        }
                        JOptionPane.showMessageDialog(this, "User information deleted.");
                        dispose();
                        new ChatApplication();
                    } else {
                        JOptionPane.showMessageDialog(this, "Password does not match.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            setVisible(true);

          
        }
    }

        

public class ChatApplication {
 public ChatApplication()
 {
      JFrame jf=new JFrame ("Chat Application");
        
        JLabel jl=new JLabel("Welcome to our chat Application");
        jl.setBounds(150, 40, 400, 40);
        jf.add(jl);
        
        JButton signUp=new JButton("SignUp");
        signUp.setBounds(60, 300, 100, 40);
        jf.add(signUp);
        signUp.addActionListener(e -> {

jf.setVisible(false);
                new SignUP();
            });
        
        
           JButton signin=new JButton("Signin");
        signin.setBounds(300, 300, 100, 40);
        jf.add(signin);
        
        signin.addActionListener(e -> {

jf.setVisible(false);
                new Signin();
            });
        
         JButton delete=new JButton("Delete Account");
        delete.setBounds(180, 400, 150, 40);
        jf.add(delete);
        
        delete.addActionListener(e -> {

jf.setVisible(false);
                new DeleteFrame();
            });
        
        jf.setSize(500, 500);
        jf.setLayout(null);
        jf.setVisible(true);
 }
    public static void main(String[] args) {
       new ChatApplication();
        
    }
    
}

