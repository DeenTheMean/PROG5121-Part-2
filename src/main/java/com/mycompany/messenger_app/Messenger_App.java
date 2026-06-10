package com.mycompany.messenger_app;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import javax.swing.JOptionPane;


public class Messenger_App {  
    // As long as this value is true, the program will continue to run
    static boolean running = true;
    
    static ArrayList<Message> sentMessages = new ArrayList<>();
    static ArrayList<Message> disregardedMessages = new ArrayList<>();
    static ArrayList<Message> storedMessages = new ArrayList<>();

    public static void main(String[] args) {      
        // Declaration of ArrayList used for storing user
        ArrayList<Login> users = new ArrayList<>();
        // Selection is initialised as -2 because it's outside the
        // range of numbers it could become (-1, 0, 1, 2).
        int selection = -2;
        // Array that stores the options on the main menu
        String[] mainOptions = {"Register user", "Log in", "Quit"};
    
        // Main menu reappears after each case unless program is terminated
        while(running) {
            // JOptionPane used to display the main menu of the program.
            // Parent component: null because there is no parent component
            // Message text: "Main Menu"
            // Title tex: "QuickChat"
            // Button layout: DEFAULT_OPTION allows us to supply our own button options
            // Message type: the dialog box is plain
            // Icon: null = no custom icon
            // Options: changes the text on the buttons according to the array mainOptions
            // Initial value: -2
            selection = JOptionPane.showOptionDialog(null, "Welcome to QuickChat!",
                                                     "QuickChat",
                                                     JOptionPane.DEFAULT_OPTION,
                                                     JOptionPane.PLAIN_MESSAGE,
                                                     null, mainOptions, -2);

            switch(selection) {
                case 0 -> {              
                    // showInputDialog used to obtain user input
                    String username = JOptionPane.showInputDialog(null, "Enter a username." +
                                                            "\n\nHint: Username must have an underscore(_), and cannot be longer than five characters.",
                                                            "Register User", JOptionPane.PLAIN_MESSAGE);
                    
                    // If the user clicks "Cancel" or the "X" button, they return to the main menu
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
                             
                    // Input for the password is obtained in the same way as the username
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
                    
                    // Input for the phone number is obtained in the same way as the username and password
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
                    JOptionPane.showMessageDialog(null, "Welcome, " + username + ". It is great to see you!\n\nRedirecting to login page...",
                                                  "QuickChat", JOptionPane.PLAIN_MESSAGE);
                    promptLogin(users);
                }
                case 1 -> {
                    // Checks if the ArrayList is empty (no registered users), otherwise prompts login
                    if(users.isEmpty()){
                        JOptionPane.showMessageDialog(null, "No users have registered yet! Please register first.",
                                                      "Log in", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    
                    promptLogin(users);
                }
                case 2, -1 -> { // -1 is the value returned when the "X" button is clicked
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
       
        // If login was successful, move on to messaging stage
        if(loginStatus) {
            startMessaging();           
        }
    }
    
    private static void startMessaging() {
        // The messaging portion of the app will continue to run as long as this value is true
        boolean chatRunning = true;
        // Array that stores the options for the secondary menu
        String[] messageOptions = {"Send messages", "Stored messages", "Quit"};
 
        // Program runs until the user chooses to quit
        outerLoop: // This label is used to refer to this specific while-loop when you want to jump back to the start of it
        while(chatRunning) {        
           int selection = JOptionPane.showOptionDialog(null, "What would you like to do?",
                                                        "QuickChat",
                                                        JOptionPane.DEFAULT_OPTION,
                                                        JOptionPane.PLAIN_MESSAGE,
                                                        null, messageOptions, -2);

            switch(selection) {
                case 0 -> {
                    // User decides how many messages they want to send
                    int numMessages = 0;
                    // Counts how many messages have been sent
                    int messageCounter = 0;
        
                    while(numMessages <= 0) {          
                        // Input validation
                        try {
                            numMessages = Integer.parseInt(JOptionPane
                                                           .showInputDialog(null,
                                                           "How many messages would you like to send?",
                                                           "QuickChat", JOptionPane.PLAIN_MESSAGE));

                            if(numMessages <= 0) {
                                JOptionPane.showMessageDialog(null, "Please enter a number greater than 0.", "QuickChat", JOptionPane.WARNING_MESSAGE);
                            }
                        }catch(NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Please enter a whole number.", "QuickChat", JOptionPane.WARNING_MESSAGE);
                        }
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
                            
                            // If the user cancels this action, they are sent back to the previous menu and 
                            // the message counter resets.
                            if(recipient == null) {
                                messageCounter = 0;
                                continue outerLoop;
                            }
                            
                            // Phone number is validated using the checkRecipientCell method
                            String recipientCheck = Message.checkRecipientCell(recipient);                    
                            
                            // Show message depending on the outcome of the validation
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
                            
                            // Phone number is validated using the checkRecipientCell method
                            String lengthCheck = Message.checkMessageLength(messageText);
                            
                            // Same as the recipient phone number validation
                            if(lengthCheck.equals("Message ready to send.")) {
                                JOptionPane.showMessageDialog(null, lengthCheck, "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }else {
                                JOptionPane.showMessageDialog(null, lengthCheck, "QuickChat", JOptionPane.WARNING_MESSAGE);
                            }
                        }
 
                        // Create the message object
                        Message msg = new Message(messageCounter, recipient, messageText);
                        // Array that stores the options for what to do with the message
                        String[] messageOptions2 = {"Send message", "Disregard message", "Store message to send later"};

                        // Message hash and message ID are displayed in this dialog box
                        int action = JOptionPane.showOptionDialog(null, "Message ID: " + msg.getMessageID() +
                                                                  "\nMessage Hash: " + msg.getMessageHash() +  
                                                                  "\n\nWhat would you like to do?", "QuickChat",
                                                                  JOptionPane.DEFAULT_OPTION,
                                                                  JOptionPane.PLAIN_MESSAGE,
                                                                  null, messageOptions2, -2);

                        // If the "X" button is clicked, return to the previous menu
                        if(action == -1) {
                            messageCounter = 0;
                            continue outerLoop;
                        }
                        
                        // Displays a message depending on what the user chooses
                        JOptionPane.showMessageDialog(null, msg.sentMessage(action), "QuickChat", JOptionPane.INFORMATION_MESSAGE);
 
                        // Decrement the message counter if the message is disregarded
                        if(action == 1) {
                            messageCounter--;
                        }
                    }
                                      
                    // Show summary of message details
                    JOptionPane.showMessageDialog(null, "Total messages sent: " +
                                                  Message.returnTotalMessages() + "\n" +
                                                  Message.printSentMessages(),
                                                  "QuickChat", JOptionPane.INFORMATION_MESSAGE);
                    
                }
                case 1 -> {
                    if (!storedMessages.isEmpty()) {
                        boolean storedMessagesRunning = true;

                        while (storedMessagesRunning) {
                            String[] storedMessageOptions = {"Show longest message", "Search for a message",
                                                             "Filter by recipient", "Delete a message",
                                                             "Cancel"};

                            int storedMessageAction = JOptionPane.showOptionDialog(null, Message.printStoredMessages(),
                                                                                   "Stored Messages",
                                                                                   JOptionPane.DEFAULT_OPTION,
                                                                                   JOptionPane.PLAIN_MESSAGE,
                                                                                   null, storedMessageOptions, -2);

                            switch (storedMessageAction) {
                                case 0 -> {
                                    Message.displayLongestStoredMessage();
                                }
                                case 1 -> {
                                    Message.searchMessageID();
                                }
                                case 2 -> {
                                    Message.searchRecipientMessages();
                                }
                                case 3 -> {
                                    Message.deleteStoredMessage();
                                }
                                case 4, -1 -> {
                                    storedMessagesRunning = false;
                                    continue outerLoop;
                                }
                                default -> {
                                    // Invalid input error message.
                                    System.out.println("Invalid input.");
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "You haven't stored any messages yet.",
                                                      "Stored Messages", JOptionPane.INFORMATION_MESSAGE);
                        continue outerLoop;
                    }
                }
                case 2, -1 -> {
                    // Terminates the program
                    JOptionPane.showMessageDialog(null, "Exiting... Come back soon!", "Quit",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    
                    chatRunning = false;
                    running = false;
                }
                default -> {
                    // Invalid input error message.
                    System.out.println("Invalid input.");
                }
            }
        }
    }
    
    // Class that handles the login process
    public static class Login {
        private String username;
        private String password;
        private static String phoneNumber;
        
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
        
        // Method that displays a message depending on registration success
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
        
        public static String getPhoneNumber() {
            return phoneNumber;
        }
    }
   
    public static class Message {
        private String messageID;
        private int messageNumber;
        private String recipient;
        private String messageText;
        private String messageHash;     
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
            if(number.matches("\\+27[6-8]\\d{8}")) {
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
                case 0 -> {
                    sentMessages.add(this);
                    totalMessagesSent++;
                    return "Message successfully sent.";
                }
                case 1 -> {
                    disregardedMessages.add(this);
                    return "Message disregarded.";
                }
                case 2 -> {
                    storedMessages.add(this);
                    storeMessage();
                    return "Message successfully stored.";
                }
                default -> {
                    return "Invalid input.";
                }
            }
        }
 
        // Returns the details of the messages sent
        public static String printSentMessages() {
            if (sentMessages.isEmpty()) {
                return "No messages sent yet.";
            }
 
            StringBuilder stringBuilder = new StringBuilder();
            
            for (Message m : sentMessages) {
                stringBuilder.append("-----------------------------\n");
                stringBuilder.append("Message ID: ").append(m.messageID).append("\n");
                stringBuilder.append("Message Hash: ").append(m.messageHash).append("\n");
                stringBuilder.append("Recipient: ").append(m.recipient).append("\n");
                stringBuilder.append("Message: ").append(m.messageText).append("\n");
            }
            stringBuilder.append("-----------------------------");
            
            return stringBuilder.toString();
        }
        
        public static String printStoredMessages() {
            StringBuilder stringBuilder = new StringBuilder();
            
            for (Message m : storedMessages) {
                stringBuilder.append("-----------------------------\n");
                stringBuilder.append("Message ID: ").append(m.messageID).append("\n");
                stringBuilder.append("Message Hash: ").append(m.messageHash).append("\n");
                stringBuilder.append("Sender: ").append(Login.getPhoneNumber()).append("\n");
                stringBuilder.append("Recipient: ").append(m.recipient).append("\n");
                stringBuilder.append("Message: ").append(m.messageText).append("\n");
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
        
        public static void displayLongestStoredMessage() {
            String longestMessage = "";
            
            for (Message m : storedMessages) {
                if (m.messageText.length() > longestMessage.length()) {
                    longestMessage = m.messageText;
                }
            }
            
            JOptionPane.showMessageDialog(null, "The longest stored message is:\n" +
                                          longestMessage, "Stored Messages", JOptionPane.PLAIN_MESSAGE);
        }
        
        public static void searchMessageID() {
            
        }
        
        public static void searchRecipientMessages() {
            
        }
        
        public static void deleteStoredMessage() {
            
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