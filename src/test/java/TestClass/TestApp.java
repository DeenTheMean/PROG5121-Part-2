package TestClass;

import com.mycompany.messenger_app.Messenger_App.Login;
import com.mycompany.messenger_app.Messenger_App.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Deen
 */
public class TestApp {
    
    // ========== Username format tests ==========

    @Test
    public void testUsernameCorrectFormat() {
        assertTrue(Login.checkUsername("kyl_1"));
    }
    
    @Test
    public void testUsernameIncorrectFormat() {
        assertFalse(Login.checkUsername("kyle!!!!!!!"));
    }
    
    // ========== Password format tests ==========  

    @Test
    public void testPasswordCorrectFormat() {
        assertTrue(Login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }
    
    @Test
    public void testPasswordIncorrectFormat() {
        assertFalse(Login.checkPasswordComplexity("password"));
    }
    
    // ========== Cellphone number format tests
         
    @Test
    public void testCellNoCorrectFormat() {
        assertTrue(Login.checkCellPhoneNumber("+27838968976"));
    }
    
    @Test
    public void testCellNoIncorrectFormat() {
        assertFalse(Login.checkCellPhoneNumber("08966553"));
    }
    
    // ========== Login tests ==========
    
    @Test
    public void testLoginSuccess() {
        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(user.loginUser("Ch&&sec@ke99!"));
    }
    
    @Test
    public void testLoginFailure() {
        Login user = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(user.loginUser("password"));
    }
    
    // ========== Message length tests ==========
    
    @Test
    public void testMessageLengthSuccess() {
        // If message is within the 250 character limit
        String result = Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", result);
    }
 
    @Test
    public void testMessageLengthFailure() {
        // If message exceeds 250 character limit
        String longMessage = "A".repeat(260);
        String result = Message.checkMessageLength(longMessage);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.", result);
    }
 
    // ========== Recipient cell phone number tests ==========
 
    @Test
    public void testRecipientCellSuccess() {
        // If phone number is correctly formatted
        String result = Message.checkRecipientCell("+27718693002");
        assertEquals("Cell phone number successfully captured.", result);
    }
 
    @Test
    public void testRecipientCellFailure() {
        // If phone number is incorrectly formatted
        String result = Message.checkRecipientCell("08575975889");
        assertEquals("Cell phone number is incorrectly formatted or does not contain an " +
                     "international code. Please correct the number and try again.", result);
    }
 
    // ========== Message hash tests ==========
 
    @Test
    public void testMessageHashCorrectFormat() {
        // If hash is correctly formatted
        Message msg = new Message(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        
        String hash = msg.getMessageHash();
 
        // Hash must be all uppercase
        assertEquals(hash.toUpperCase(), hash);
 
        // Hash must end with HITONIGHT
        assertTrue(hash.endsWith(":HITONIGHT"),
            "Expected hash to end with ':HITONIGHT' but got: " + hash);
 
        // Hash must contain the message number
        assertTrue(hash.contains(":1:"),
            "Expected hash to contain ':1:' but got: " + hash);
    }
 
    @Test
    public void testMessageHashAllMessagesInLoop() {
        // Tests hashes for multiple messages in a loop
        String[] messages = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };
        String[] recipients = { "+27718693002", "+27831234567" };
 
        for(int i = 0; i < messages.length; i++) {
            Message msg = new Message(i + 1, recipients[i], messages[i]);
            
            String hash = msg.getMessageHash();
 
            assertEquals(hash.toUpperCase(), hash,
                "Hash for message " + (i + 1) + " should be uppercase.");
 
            // Every hash must have 3 seperated parts
            String[] parts = hash.split(":");
            assertEquals(3, parts.length,
                "Hash for message " + (i + 1) + " should have 3 parts separated by ':'.");
        }
    }
 
    // ========== Message ID test ==========
 
    @Test
    public void testMessageIDIsGenerated() {
        // If message ID is generated
        Message msg = new Message(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        
        assertNotNull(msg.getMessageID(), "Message ID should not be null.");
        System.out.println("Message ID generated: " + msg.getMessageID());
    }
 
    // ========== Sent message tests ==========
 
    @Test
    public void testSentMessageSend() {
        // If the message is sent
        Message msg = new Message(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        
        String result = msg.sentMessage(0);
        assertEquals("Message successfully sent.", result);
        assertEquals(1, Message.returnTotalMessages());
    }
 
    @Test
    public void testSentMessageDisregard() {
        // If the message is deletted 
        Message msg = new Message(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        
        String result = msg.sentMessage(1);
        assertEquals("Message disregarded.", result);
    }
 
    @Test
    public void testSentMessageStore() {
        // If the message is stored
        Message msg = new Message(1, "+27718693002", "Hi Mike, can you join us for dinner tonight?");
        
        String result = msg.sentMessage(2);
        assertEquals("Message successfully stored.", result);
    }  
}
