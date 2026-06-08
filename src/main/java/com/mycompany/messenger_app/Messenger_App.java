package com.mycompany.messenger_app;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import javax.swing.JOptionPane;


public class Messenger_App {
    
    static boolean running = true;

    public static void main(String[] args) {
        
        // Declaration of Scanner and ArrayList used for storing users
        Scanner scanner = new Scanner(System.in);
        ArrayList<Login> users = new ArrayList<>();
        int selection = -2;
        String[] mainOptions = {"Register user", "Log in", "Quit"};
    
        // Main menu reappears after each case unless program is terminated
        while(running) {
    
            selection = JOptionPane.showOptionDialog(null, "Main Menu",
                                                     "QuickChat",
                                                     JOptionPane.YES_NO_CANCEL_OPTION,
                                                     JOptionPane.PLAIN_MESSAGE,
                                                     null, mainOptions, -2);

            switch(selection) {
                case 0 -> {                   
                    String username = JOptionPane.showInputDialog(null, "Enter a username." +
                                                            "\n\nHint: Username must have an underscore(_), and cannot be longer than five characters.",
                                                            "Register User", JOptionPane.PLAIN_MESSAGE);
                    
                    if(username == null) {
                        break;
                    }
                        
                    // Validation of username (uses checkUsername method within the Login class)
                    if(Login.checkUsername(username)){
                        // Checks if username already exists in arraylist
                        if(users.stream().anyMatch(u -> u.getUsername().equals(username))){
                            JOptionPane.showMessageDialog(null, "Username already exists. Please choose a different username.",
                                                          "Register User", JOptionPane.WARNING_MESSAGE);
                            break;
                        }                       
                    }else{
                        // Prints helpful message using the registerUser method within the Login class
                        Login.registerUser("InvalidUsername");
                        break;
                    }                                       
                                            
                    String password = JOptionPane.showInputDialog(null, "Enter a password." +
                                                                  "\n\nHint: Password must have a capital letter, " +
                                                                  "a number, a special character, and must be at least eight characters long.",
                                                                  "Register User", JOptionPane.PLAIN_MESSAGE);
                    
                    if(password == null) {
                        break;
                    }
                    
                    // Validation of password (uses checkPasswordComplexity method within the Login class)
                    if(!Login.checkPasswordComplexity(password)){
                        // Prints helpful message using the registerUser method within the Login class
                        Login.registerUser("InvalidPassword");
                        break;
                    }
                    
                    String phoneNumber = JOptionPane.showInputDialog(null, "Enter your cell phone number." +
                                                                     "\n\nHint: Phone number must contain the South African country code(+27XXXXXXXXX).",
                                                                     "Register User", JOptionPane.PLAIN_MESSAGE);
                    
                    if(phoneNumber == null) {
                        break;
                    }
                                                          
                    // Validation of phone number (uses checkCellPhoneNumber method within the Login class)                    
                    if(!Login.checkCellPhoneNumber(phoneNumber)){
                        Login.registerUser("InvalidCellNum");
                        break;
                    }
                    
                    // User is added to the ArrayList and a message is displayed if registration is successful
                    users.add(new Login(username, password, phoneNumber));
                    Login.registerUser("RegisterSuccess");
                    
                    // User is prompted to log in immediately after registering (uses the promptLogin method)
                    JOptionPane.showMessageDialog(null, "Welcome, " + username + ". It is great to see you!\nRedirecting to login page...",
                                                  "QuickChat", JOptionPane.PLAIN_MESSAGE);
                    promptLogin(users);
                    break;
                }
                case 1 -> {
                    // Checks if the ArrayList is empty (no registered users), otherwise prompts login
                    if(users.isEmpty()){
                        JOptionPane.showMessageDialog(null, "No users have registered yet! Please register first.",
                                                      "Log in", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    
                    promptLogin(users);
                    break;
                }
                case 2, -1 -> {
                    // Terminates the program
                    JOptionPane.showMessageDialog(null, "Exiting... Come back soon!", "Quit",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    running = false;
                }
                default ->
                    // Invalid input error message
                    System.out.println("Invalid input.");
            }
        }
    }
    
    // Searches ArrayList for existing user with the given username and password
    private static void promptLogin(ArrayList<Login> users){
        String enteredUsername = JOptionPane.showInputDialog(null, "Enter your username.", "Log In", JOptionPane.PLAIN_MESSAGE);
        
        if(enteredUsername == null) {
            return;
        }
        
        String enteredPassword = JOptionPane.showInputDialog(null, "Enter your password.", "Log In", JOptionPane.PLAIN_MESSAGE);
        
        if(enteredPassword == null) {
            return;
        }
        
        Login user = users.stream()
                     .filter(u -> u.getUsername().equals(enteredUsername))
                     .findFirst().orElse(null);
        
        boolean loginStatus = user != null && user.loginUser(enteredPassword);
        // Prints message (using the returnLoginStatus method) if the user is found or details are incorrect
        JOptionPane.showMessageDialog(null, user != null ? user.returnLoginStatus(loginStatus) : "\nUser not found.",
                                      "Log In", JOptionPane.PLAIN_MESSAGE);
        
        Scanner scanner = new Scanner(System.in);
        // If login was successful, move on to messaging stage
        if(loginStatus) {
            startMessaging(scanner);           
        }
    }
    
    private static void startMessaging(Scanner scanner) {
        // User decides how many messages they want to send
        int numMessages = 0;
        while(numMessages <= 0) {          
            // Input validation
            try {
                numMessages = Integer.parseInt(JOptionPane
                                               .showInputDialog(null,
                                               "Welcome to QuickChat!\n\nHow many messages would you like to send?",
                                               "QuickChat", JOptionPane.PLAIN_MESSAGE));
                                
                if(numMessages <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a number greater than 0.", "QuickChat", JOptionPane.WARNING_MESSAGE);
                }
            }catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a whole number.", "QuickChat", JOptionPane.WARNING_MESSAGE);
            }
        }

        int messageCounter = 0;
        boolean chatRunning = true;
        String[] messageOptions = {"Send messages", "Show recently sent messages", "Quit"};
 
        // Program runs until the user chooses to quit
        outerLoop:
        while(chatRunning) {        
           int selection = JOptionPane.showOptionDialog(null, "Messaging Menu",
                                                        "QuickChat",
                                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        null, messageOptions, -2);

            switch(selection) {
                case 0 -> {
                    // Send user back to the menu if they've reached their message limit
                    if(messageCounter >= numMessages) {
                        JOptionPane.showMessageDialog(null, "You have reached your message limit of " + numMessages + "!",
                                                      "QuickChat", JOptionPane.WARNING_MESSAGE);
                        break;
                    }
 
                    // Send messages until the chosen limit
                    while(messageCounter < numMessages) {
                        messageCounter++;
 
                        // Get and validate recipient number
                        String recipient = "";
                        while(true) {
                            recipient = JOptionPane.showInputDialog(null, "Message " + messageCounter + " of " + numMessages +
                                                                    "\n\nEnter recipient cell number (e.g. +27XXXXXXXXX).",
                                                                    "QuickChat", JOptionPane.PLAIN_MESSAGE);
                            
                            if(recipient == null) {
                                messageCounter = 0;
                                continue outerLoop;
                            }
                            
                            String recipientCheck = Message.checkRecipientCell(recipient);                    
                            
                            if(recipientCheck.equals("Cell phone number successfully captured.")) {
                                JOptionPane.showMessageDialog(null, recipientCheck, "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }else {
                                JOptionPane.showMessageDialog(null, recipientCheck, "QuickChat", JOptionPane.WARNING_MESSAGE);
                            }
                        }
 
                        // Get and validate the message
                        String messageText = "";
                        while(true) {
                            messageText = JOptionPane.showInputDialog(null, "Message " + messageCounter + " of " + numMessages +
                                                                    "\n\nEnter your message (max 250 characters).",
                                                                    "QuickChat", JOptionPane.PLAIN_MESSAGE);
                            
                            if(messageText == null) {
                                messageCounter = 0;
                                continue outerLoop;
                            }
                            
                            String lengthCheck = Message.checkMessageLength(messageText);
                            
                            if(lengthCheck.equals("Message ready to send.")) {
                                JOptionPane.showMessageDialog(null, lengthCheck, "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }else {
                                JOptionPane.showMessageDialog(null, lengthCheck, "QuickChat", JOptionPane.WARNING_MESSAGE);
                            }
                        }
 
                        // Create the message object
                        Message msg = new Message(messageCounter, recipient, messageText);
 
                        System.out.println("\nMessage ID   : " + msg.getMessageID());
                        System.out.println("Message Hash : " + msg.getMessageHash());
 
                        // Ask what to do with the message
                        System.out.println("\nWhat would you like to do?");
                        System.out.println("1. Send Message");
                        System.out.println("2. Disregard Message");
                        System.out.println("3. Store Message to send later");
                        System.out.print("Choose: ");
 
                        int action = 0;
                        while(action < 1 || action > 3) {
                            try {
                                action = scanner.nextInt();
                                scanner.nextLine();
                                
                                if(action < 1 || action > 3) {
                                    System.out.println("Please enter 1, 2, or 3.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Please enter 1, 2, or 3.");
                            }
                        }
 
                        System.out.println(msg.sentMessage(action));
 
                        // Decrement the message counter if the message is disregarded
                        if(action == 2) {
                            messageCounter--;
                        }
                    }
 
                    // Show total number of messages sent
                    System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
                    
                    // Show summary of message details
                    if(Message.returnTotalMessages() > 0) {
                        System.out.println("\n" + Message.printMessages());
                    }
                }
                case 1 -> {
                    JOptionPane.showMessageDialog(null, "Coming soon...", "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                }
                case 2, -1 -> {
                    JOptionPane.showMessageDialog(null, "Exiting... Come back soon!", "Quit",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    
                    chatRunning = false;
                    running = false;
                }
                default -> {
                    System.out.println("Invalid input.");
                }
            }
        }
    }

    
    public static class Login {
        private String username;
        private String password;
        private String phoneNumber;
        
        // Constructor
        public Login(String username, String password, String phoneNumber){
            this.username = username;
            this.password = password;
            this.phoneNumber = phoneNumber;
        }
        
        // Returns stored usernames
        public String getUsername(){
            return username;
        }
        
        // Username validation logic
        public static boolean checkUsername(String username){
            return username.length() <= 5 && username.contains("_");
        }
        
        // Password validation logic
        public static boolean checkPasswordComplexity(String password){
            return password.length() >= 8 &&
                   password.matches(".*[A-Z].*") &&
                   password.matches(".*\\d.*") &&
                   password.matches(".*[!@#$%^&*,.?\":()|<>].*");
        }
        
        // Phone number validation logic
        public static boolean checkCellPhoneNumber(String phoneNumber){
            return phoneNumber.matches("\\+27[6-8]\\d{8}");
        }
        
        // Method that returns a message depending on registration success
        public static void registerUser(String registerCase){
            switch(registerCase){
                case "InvalidUsername" -> JOptionPane.showMessageDialog(null,
                                                                        "Username is not correctly formatted; please ensure that " +
                                                                        "your username contains an underscore and is no more than " +
                                                                        "five characters in length.", "Register User",
                                                                        JOptionPane.WARNING_MESSAGE);
                case "InvalidPassword" -> JOptionPane.showMessageDialog(null,
                                                                        "Password is not correctly formatted; please ensure that the " +
                                                                        "password contains at least eight characters, a capital letter, " +
                                                                        "a number, and a special character.", "Register User",
                                                                        JOptionPane.WARNING_MESSAGE);
                case "InvalidCellNum" -> JOptionPane.showMessageDialog(null,
                                                                       "Cell phone number incorrectly formatted " +
                                                                       "or does not contain international code.", "Register User",
                                                                       JOptionPane.WARNING_MESSAGE);
                case "RegisterSuccess" -> JOptionPane.showMessageDialog(null, "Registered successfully.", "Register User",
                                                                        JOptionPane.INFORMATION_MESSAGE);
                default -> System.out.println("Invalid case.");
            }
        }
        
        // Checks if password matches when logging in
        public boolean loginUser(String enteredPassword){
            return this.password.equals(enteredPassword);
        }
        
        // Returns a message if login is successful or not
        public String returnLoginStatus(boolean status){
            return status ? "\nLogin successful!" : "\nUsername or password incorrect. Please try again.";
        }
    }
   
    public static class Message {
        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;
        // Tracks messages sent and total count
        private static ArrayList<Message> sentMessages = new ArrayList<>();
        private static int totalMessagesSent = 0;
 
        // Constructor automatically generates a message ID and message hash using methods
        public Message(int messageNumber, String recipient, String messageText) {
            this.messageID = generateMessageID();
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.messageText = messageText;
            this.messageHash = createMessageHash();
        }
 
        // Generates message ID using Random
        private String generateMessageID() {
            Random random = new Random();
            
            long id = (long)(random.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
            
            return String.valueOf(id);
        }
 
        // Checks if the messagfe ID is more than 10 characters
        public boolean checkMessageID() {
            return messageID != null && messageID.length() <= 10;
        }
 
        // Validates recipient cell phone number
        public static String checkRecipientCell(String number) {
            if(number != null && number.startsWith("+") && number.length() <= 13) {
                return "Cell phone number successfully captured.";
            }else {
                return "Cell phone number is incorrectly formatted or does not contain an " +
                       "international code. Please correct the number and try again.";
            }
        }
 
        // Validates message length
        public static String checkMessageLength(String text) {
            if(text.length() <= 250) {
                return "Message ready to send.";
            }else {
                int excess = text.length() - 250;
                return "Message exceeds 250 characters by " + excess + "; please reduce the size.";

            }
        }
 
        // Generates the message hash
        public String createMessageHash() {
            String idPrefix = messageID.substring(0, 2);
            String[] words = messageText.trim().split("\\s+");
            
            String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
            String lastWord = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");
            
            return (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        }
 
        // Deals with the message choicea
        public String sentMessage(int choice) {
            switch (choice) {
                case 1 -> {
                    sentMessages.add(this);
                    totalMessagesSent++;
                    return "\nMessage successfully sent.";
                }
                case 2 -> {
                    return "\nPress 0 to delete the message.";
                }
                case 3 -> {
                    storeMessage();
                    return "\nMessage successfully stored.";
                }
                default -> {
                    return "\nInvalid option.";
                }
            }
        }
 
        // Returns the details of the messages sent
        public static String printMessages() {
            if(sentMessages.isEmpty()) {
                return "No messages sent yet.";
            }
 
            StringBuilder stringBuilder = new StringBuilder();
            
            for(Message m : sentMessages) {
                stringBuilder.append("-----------------------------\n");
                stringBuilder.append("Message ID   : ").append(m.messageID).append("\n");
                stringBuilder.append("Message Hash : ").append(m.messageHash).append("\n");
                stringBuilder.append("Recipient    : ").append(m.recipient).append("\n");
                stringBuilder.append("Message      : ").append(m.messageText).append("\n");
            }
            stringBuilder.append("-----------------------------");
            
            return stringBuilder.toString();
        }
 
        // Returns the total number of messages sent
        public static int returnTotalMessages() {
            return totalMessagesSent;
        }
 
        // Stores the message in a JSON file
        public void storeMessage() {
            StringBuilder jsonArray = new StringBuilder();
 
            // Load existing stored messages if file exists
            try {
                String existing = new String(Files.readAllBytes(Paths.get("stored_messages.json")));

                existing = existing.trim();
                
                if(existing.startsWith("[") && existing.endsWith("]")) {
                    existing = existing.substring(1, existing.length() - 1).trim();
                }
                
                jsonArray.append(existing);
            }catch (IOException e) {
                
            }
 
            // Builds a JSON object fo rthe message
            String entry = String.format("{\"messageID\":\"%s\",\"messageHash\":\"%s\",\"recipient\":\"%s\",\"message\":\"%s\",\"msgNumber\":%d}",
                                         messageID, messageHash, recipient, messageText.replace("\"", "\\\""), messageNumber);
 
            if(jsonArray.length() > 0) {
                jsonArray.append(",");
            }
            
            jsonArray.append(entry);
 
            try(FileWriter fileWriter = new FileWriter("stored_messages.json")) {
                fileWriter.write("[" + jsonArray + "]");
            }catch (IOException e) {
                System.out.println("Error saving message: " + e.getMessage());
            }
        }
 
        // Returns the message ID
        public String getMessageID() {
            return messageID; 
        }
        
        // Returns the message hash
        public String getMessageHash() {
            return messageHash;
        }
        
        // Returns the recipient number
        public String getRecipient() {
            return recipient;
        }
        
        // Returns the message text
        public String getMessageText() {
            return messageText;
        }
        
        // Returns the number of messages
        public int getMessageNumber() {
            return messageNumber;
        }
        
        // Returns the list of sent messages
        public static ArrayList<Message> getSentMessages() {
            return sentMessages;
        }
    }
}
