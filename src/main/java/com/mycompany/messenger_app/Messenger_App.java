package com.mycompany.messenger_app;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


public class Messenger_App {
    
    static boolean running = true;

    public static void main(String[] args) {
        
        // Declaration of Scanner and ArrayList used for storing users
        Scanner scanner = new Scanner(System.in);
        ArrayList<Login> users = new ArrayList<>();
           
        System.out.println("============ Registration ============");
    
        // Main menu reappears after each case unless program is terminated
        while(running){
            System.out.println("1. Register User");
            System.out.println("2. Log in");
            System.out.println("3. Quit");

            System.out.print("Select an option (1, 2, 3): ");
            int selection = scanner.nextInt();
            scanner.nextLine();

            switch(selection){
                case 1 -> {
                    System.out.print("\nEnter a username: ");
                    String username = scanner.nextLine();
                    
                    // Validation of username (uses checkUsername method within the Login class)
                    if(Login.checkUsername(username)){
                        // Checks if username already exists in arraylist
                        if(users.stream().anyMatch(u -> u.getUsername().equals(username))){
                            System.out.println("Username already exists. Please choose a different username.");
                            break;
                        }
                        
                        System.out.println("Username successfully captured.");
                    }else{
                        // Prints helpful message using the registerUser method within the Login class
                        System.out.println(Login.registerUser("InvalidUsername"));
                        break;
                    }                                       
                                            
                    System.out.print("\nEnter a password: ");
                    String password = scanner.nextLine();
                    
                    // Validation of password (uses checkPasswordComplexity method within the Login class)
                    if(Login.checkPasswordComplexity(password)){
                        System.out.println("Password successfully captured.");
                    }else{
                        // Prints helpful message using the registerUser method within the Login class
                        System.out.println(Login.registerUser("InvalidPassword"));
                        break;
                    }
                                         
                    System.out.print("\nEnter your cell phone number (+27XXXXXXXXX): ");
                    String phoneNumber = scanner.nextLine();
                    
                    // Validation of phone number (uses checkCellPhoneNumber method within the Login class)                    
                    if(Login.checkCellPhoneNumber(phoneNumber)){
                        System.out.println("Cell phone number successfully added.");
                    }else{
                        System.out.println("Cell phone number incorrectly formatted " +
                                           "or does not contain international code.");
                        break;
                    }
                    
                    // User is added to the ArrayList and a message is displayed if registration is successful
                    users.add(new Login(username, password, phoneNumber));
                    System.out.println(Login.registerUser("RegisterSuccess"));
                    
                    // User is prompted to log in immediately after registering (uses the promptLogin method)
                    System.out.println("\nWelcome " + username + " it is great to see you.");
                    System.out.println("Redirecting to login page...\n");
                    promptLogin(scanner, users);
                    break;
                }
                case 2 -> {
                    // Checks if the ArrayList is empty (no registered users), otherwise prompts login
                    if(users.isEmpty()){
                        System.out.println("\nNo users have registered yet! Please register first.\n");
                        break;
                    }
                    
                    promptLogin(scanner, users);
                    break;
                }
                case 3 -> {
                    // Terminates the program
                    System.out.println("\nExiting... Come back soon!");
                    scanner.close();
                    running = false;
                }
                default ->
                    // Invalid input error message
                    System.out.println("\nPlease enter a valid number (1, 2, 3)");
            }
        }
    }
    
    // Searches ArrayList for existing user with the given username and password
    private static void promptLogin(Scanner scanner, ArrayList<Login> users){
        System.out.print("Enter username: ");
        String enteredUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();
        
        Login user = users.stream()
                  .filter(u -> u.getUsername().equals(enteredUsername))
                  .findFirst()
                  .orElse(null);
        
        boolean loginStatus = user != null && user.loginUser(enteredPassword);
        // Prints message (using the returnLoginStatus method) if the user is found or details are incorrect
        System.out.println(user != null ? user.returnLoginStatus(loginStatus) : "\nUser not found.");
        
        // If login was successful, move on to messaging stage
        if(loginStatus) {
            startMessaging(scanner);           
        }
    }
    
    private static void startMessaging(Scanner scanner) {
        
        System.out.println("\nWelcome to QuickChat.");
 
        // User decides how many messages they want to send
        int numMessages = 0;
        while(numMessages <= 0) {
            System.out.print("\nHow many messages would you like to send?: ");
            
            // Input validation
            try {
                numMessages = scanner.nextInt();
                
                if(numMessages <= 0){
                    System.out.println("Please enter a number greater than 0.");
                }
            }catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        int messageCounter = 0;
        boolean chatRunning = true;
 
        // Program runs until the user chooses to quit
        while(chatRunning) {
            System.out.println("\n============ QuickChat Menu ============");
            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            
            System.out.print("Select an option (1, 2, 3): ");
            int selection = scanner.nextInt();
            scanner.nextLine();

            switch(selection) {
                case 1 -> {
                    // Send user back to the menu if they've reached their message limit
                    if(messageCounter >= numMessages) {
                        System.out.println("\nYou have reached your message limit of " + numMessages + "!");
                        break;
                    }
 
                    // Send messages until the chosen limit
                    while(messageCounter < numMessages) {
                        messageCounter++;
                        System.out.println("\n====== Message " + messageCounter + " of " + numMessages + " ======");
 
                        // Get and validate recipient number
                        String recipient = "";
                        while(true) {
                            System.out.print("Enter recipient cell number (+27XXXXXXXXX): ");
                            recipient = scanner.nextLine();
                            
                            String recipientCheck = Message.checkRecipientCell(recipient);
                            System.out.println(recipientCheck);
                            
                            if(recipientCheck.equals("Cell phone number successfully captured.")) {
                                break;
                            }
                        }
 
                        // Get and validate the message
                        String messageText = "";
                        while(true) {
                            System.out.print("\nEnter your message (max 250 characters): ");
                            messageText = scanner.nextLine();
                            
                            String lengthCheck = Message.checkMessageLength(messageText);
                            
                            if(lengthCheck.equals("Message ready to send.")) {
                                System.out.println("Message ready to send.");
                                break;
                            }else {
                                System.out.println(lengthCheck);
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
                case 2 -> {
                    System.out.println("\nComing Soon.");
                }
                case 3 -> {
                    System.out.println("\nExiting... Come back soon!");
                    scanner.close();
                    
                    chatRunning = false;
                    running = false;
                }
                default -> {
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
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
        public static String registerUser(String registerCase){
            return switch(registerCase){
                case "InvalidUsername" -> "Username is not correctly formatted; please ensure that " +
                                          "your username contains an underscore and is no more than " +
                                          "five characters in length.";
                case "InvalidPassword" -> "Password is not correctly formatted; please ensure that the " +
                                          "password contains at least eight characters, a capital letter, " +
                                          "a number, and a special character.";
                case "RegisterSuccess" -> "\nRegistered successfully.";
                default -> "Invalid case.";
            };
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
