import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Collections;

public class ServerModelTest {
    private ServerModel model;

    /**
     * Before each test, we initialize model to be 
     * a new ServerModel (with all new, empty state)
     */
    @BeforeEach
    public void setUp() {
        // We initialize a fresh ServerModel for each test
        model = new ServerModel();
    }

    /** 
     * Here is an example test that checks the functionality of your 
     * changeNickname error handling. Each line has commentary directly above
     * it which you can use as a framework for the remainder of your tests.
     */
    @Test
    public void testInvalidNickname() {
        // A user must be registered before their nickname can be changed, 
        // so we first register a user with an arbitrarily chosen id of 0.
        model.registerUser(0);

        // We manually create a Command that appropriately tests the case 
        // we are checking. In this case, we create a NicknameCommand whose 
        // new Nickname is invalid.
        Command command = new NicknameCommand(0, "User0", "!nv@l!d!");

        // We manually create the expected Broadcast using the Broadcast 
        // factory methods. In this case, we create an error Broadcast with 
        // our command and an INVALID_NAME error.
        Broadcast expected = Broadcast.error(
            command, ServerResponse.INVALID_NAME
        );

        // We then get the actual Broadcast returned by the method we are 
        // trying to test. In this case, we use the updateServerModel method 
        // of the NicknameCommand.
        Broadcast actual = command.updateServerModel(model);

        // The first assertEquals call tests whether the method returns 
        // the appropriate Broadcast.
        assertEquals(expected, actual, "Broadcast");

        // We also want to test whether the state has been correctly
        // changed.In this case, the state that would be affected is 
        // the user's Collection.
        Collection<String> users = model.getRegisteredUsers();

        // We now check to see if our command updated the state 
        // appropriately. In this case, we first ensure that no 
        // additional users have been added.
        assertEquals(1, users.size(), "Number of registered users");

        // We then check if the username was updated to an invalid value 
        // (it should not have been).
        assertTrue(users.contains("User0"), "Old nickname still registered");

        // Finally, we check that the id 0 is still associated with the old, 
        // unchanged nickname.
        assertEquals(
            "User0", model.getNickname(0), 
            "User with id 0 nickname unchanged"
        );
    }

    /*
     * Your TAs will be manually grading the tests you write in this file.
     * Don't forget to test both the public methods you have added to your
     * ServerModel class as well as the behavior of the server in different
     * scenarios.
     * You might find it helpful to take a look at the tests we have already
     * provided you with in ChannelsMessagesTest, ConnectionNicknamesTest,
     * and InviteOnlyTest.
     */

  
    
    //own tests here
    
    @Test
    public void testRegisterUser() {
        model.registerUser(01); 
        model.registerUser(02);
        model.registerUser(03);
        model.registerUser(04);
        
        Command command = new NicknameCommand(01, "User01", "nickname01"); 
        
        //test getRegisteredUsers() especially size to ensure state remained unchanged after command
        Collection<String> registeredUsers = model.getRegisteredUsers(); 
        assertEquals(4, registeredUsers.size(), "Number of registered users"); 
        
        //test isValidName()
        Broadcast expected = Broadcast.okay(command, Collections.singleton("User01")); 
        Broadcast actual = command.updateServerModel(model); 
        assertEquals(expected, actual, "this name is valid"); 
        
        //test getNickname()
        assertEquals(expected, actual, "User with id 01 nickname retrieved");
        
        //throw error if subsequent user tries to register with existing nickname
        Command nickname = new NicknameCommand(02, "User2", "User02"); 
        nickname.updateServerModel(model); 
        Broadcast expected02 = Broadcast.error(nickname, ServerResponse.NAME_ALREADY_IN_USE);
        Broadcast actual02 = nickname.updateServerModel(model); 
        assertEquals(expected02, actual02, "can't register with existing nickname"); 
        
        //check that the id 02 is still associated with the old, unchanged nickname
        assertEquals("User02", model.getNickname(02), "User with id 02 nickname unchanged");
    }
    
   
    @Test
    public void testDeregisterUser() {
        model.registerUser(01); 
        model.deregisterUser(01);
        model.registerUser(02);
        //return null if you try to access a deRegistered user
        Command command = new NicknameCommand(02, "User02", "ValidUser02");
        Command command1 = new NicknameCommand(01, "User01", "ValidUser01");
        command.updateServerModel(model);
        command1.updateServerModel(model);
        assertNull(null, model.getNickname(01));
       
        //test that a deRegistered user can't be invited into a non existent channel 
        model.createChannel("TestChannelOwnerIs2", model.getNickname(02));
        Command command2 = new InviteCommand(02, model.getNickname(02), "TestChannelOwnerIs02", 
                                            "ValidUser02");
        Broadcast expected = Broadcast.error(command2, ServerResponse.NO_SUCH_CHANNEL); 
        Broadcast actual = command2.updateServerModel(model);
        assertEquals(expected, actual, "Broadcast deregistered user");
    }
    
    @Test
    public void testDeregisterUserOwner() {
        model.registerUser(03);
        model.registerUser(04); 
        
        Command create = new CreateCommand(03, "User03", "deregistered", false); 
        Command create1 = new CreateCommand(04, "User04", "optimal", false); 
        create.updateServerModel(model);
        create1.updateServerModel(model);
        
        Command join = new JoinCommand(04, "User04", "deregistered"); 
        join.updateServerModel(model);
        
        model.deregisterUser(03); 
        //check if the channels exist if owner's deregistered
        assertTrue(model.getChannels().contains("optimal"), "optimal channel still exists");
        assertTrue(model.getChannels().contains("deregistered"), "deregistered channel DNE");
        
    }
 
    @Test
    public void testJoin() {
        //test joining a non-existent channel 
        model.registerUser(05); 
        model.registerUser(06); 
        
        Command create = new CreateCommand(05, "User05", "patrick", true); 
        Command join = new JoinCommand(06, "User06", "spongebob"); 
        create.updateServerModel(model);
        
        Broadcast expected = Broadcast.error(join, ServerResponse.NO_SUCH_CHANNEL);
        Broadcast actual = join.updateServerModel(model); 
        assertEquals(expected, actual, "Broadcast can't join nonexistent channel"); 
    }
    
    @Test
    public void testMessage() {
      //test sending a message to a channel user isn't a part of
        model.registerUser(1); 
        model.registerUser(2); 
    
        Command create = new CreateCommand(1, "User1", "Channel1", false);
        create.updateServerModel(model); 
        Command message = new MessageCommand(2, "User2", "Channel1", "hi"); 
        
        Broadcast expected = Broadcast.error(message, ServerResponse.USER_NOT_IN_CHANNEL); 
        Broadcast actual = message.updateServerModel(model);
        assertEquals(expected, actual, " Broadcast: Message:can't send message to channel not in"); 
        
    }
    
    @Test
    public void testLeave() {
        //test user leaving channel they're not part of
        model.registerUser(1); 
        model.registerUser(2);
        
        Command create = new CreateCommand(1, "User1", "cr8dChannel", false); 
        Command leave = new LeaveCommand(2, "User2", "cr8dChannel");
        create.updateServerModel(model); 
        
        Broadcast expected = Broadcast.error(leave, ServerResponse.USER_NOT_IN_CHANNEL);
        Broadcast actual = leave.updateServerModel(model); 
        assertEquals(expected, actual, "Broadcast leave: user not in channel");
        
        //test user leaving non existent channel
        model.registerUser(3); 
        model.registerUser(4); 
        
        Command create3 = new CreateCommand(3, "User3", "chanl", false); 
        Command join4 = new JoinCommand(4, "User4", "chanl"); 
        create3.updateServerModel(model); 
        join4.updateServerModel(model);
        
        Command leave3 = new LeaveCommand(3, "User3", "nonexistent"); 
        Broadcast expected3 = Broadcast.error(leave3, ServerResponse.NO_SUCH_CHANNEL);
        Broadcast actual3 = leave3.updateServerModel(model); 
        assertEquals(expected3, actual3, "Broadcast leave: can't leave nonexistent channel"); 
        
    }
    
    @Test
    public void testInvite() { 
      //test inviting a nonexistent user
        model.registerUser(3); 
     
        Command create3 = new CreateCommand(3, "User3", "Channel3", true); 
        create3.updateServerModel(model); 
        Command invite3 = new InviteCommand(3, "User3", "nonexistent", "User4"); 
      
        Broadcast expected3 = Broadcast.error(invite3, ServerResponse.NO_SUCH_USER);
        Broadcast actual3 = invite3.updateServerModel(model); 
        assertEquals(expected3, actual3, "Broadcast invite: nonexistent channel"); 
   
    }
    

    
    @Test
    public void testKick() {
        //test when a user that isn't the owner tries to kick
        model.registerUser(1); 
        model.registerUser(2);
       
        Command create = new CreateCommand(1, "User1", "Channel1", true); 
        Command join = new JoinCommand(2, "User2", "Channel1"); 
        create.updateServerModel(model);
        join.updateServerModel(model);
        Command kick = new KickCommand(2, "User2", "Channel1", "User1"); 
       
        Broadcast expected = Broadcast.error(kick, ServerResponse.USER_NOT_OWNER); 
        Broadcast actual = kick.updateServerModel(model); 
        assertEquals(expected, actual, "Broadcast kick");
       
       //test kicking a user that doesn't sexist 
        model.registerUser(3); 
        model.registerUser(4); 
       
        Command create3 = new CreateCommand(3, "User3", "channel3", false);
        Command join3 = new JoinCommand(4, "User4", "channel3"); 
        create3.updateServerModel(model);
        join3.updateServerModel(model);
        Command kick3 = new KickCommand(3, "User3", "channel3", "User7");
       
        Broadcast expected3 = Broadcast.error(kick3, ServerResponse.NO_SUCH_USER);
        Broadcast actual3 = kick3.updateServerModel(model); 
        assertEquals(expected3, actual3, "Broadcast kick 3: nonexistent kickee");
       
        
    }
    
    //extraneous getter tests
    @Test
    public void testGetUserId() {
        //should still return the correct userID even when the nickname is changed
        model.registerUser(11); 
        Command command = new NicknameCommand(11, "User11", "validUser11"); 
        command.updateServerModel(model); 
        assertEquals(11, model.getUserId("validUser11"), "get same userID"); 
    }
    
    @Test
    public void testGetNickname() {
        model.registerUser(22); 
        Command command = new NicknameCommand(22, "validUser22", "User22");
        command.updateServerModel(model); 
        assertEquals("User22", model.getNickname(22), "User with id 22 nickname retrieved");
    }
    
}
