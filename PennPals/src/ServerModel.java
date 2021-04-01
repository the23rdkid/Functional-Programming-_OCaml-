import java.util.*;


/**
 * The {@code ServerModel} is the class responsible for tracking the
 * state of the server, including its current users and the channels
 * they are in.
 * This class is used by subclasses of {@link Command} to:
 *     1. handle commands from clients, and
 *     2. handle commands from {@link ServerBackend} to coordinate 
 *        client connection/disconnection. 
 */
public final class ServerModel implements ServerModelApi {
    

    /**
     * Constructs a {@code ServerModel} and initializes any
     * collections needed for modeling the server state.
     */
    //collections to be used as private fields 
    private Map<Integer, String> registeredUsers; 
    private Map<String, Channel> channels; 
 
    //initialize collections to be used in constructor
    public ServerModel() {
        registeredUsers = new TreeMap<>(); 
        channels = new TreeMap<>(); 
       
    }


    //==========================================================================
    // Client connection handlers
    //==========================================================================

    /**
     * Informs the model that a client has connected to the server
     * with the given user ID. The model should update its state so
     * that it can identify this user during later interactions. The
     * newly connected user will not yet have had the chance to set a
     * nickname, and so the model should provide a default nickname
     * for the user.  Any user who is registered with the server
     * (without being later deregistered) should appear in the output
     * of {@link #getRegisteredUsers()}.
     *
     * @param userId The unique ID created by the backend to represent this user
     * @return A {@link Broadcast} to the user with their new nickname
     */
    public Broadcast registerUser(int userId) {
        String nickname = generateUniqueNickname();
        registeredUsers.put(userId, nickname);
        return Broadcast.connected(nickname);
    }

    /**
     * Generates a unique nickname of the form "UserX", where X is the
     * smallest non-negative integer that yields a unique nickname for a user.
     * @return the generated nickname
     */
    private String generateUniqueNickname() {
        int suffix = 0;
        String nickname;
        Collection<String> existingUsers = getRegisteredUsers();
        do {
            nickname = "User" + suffix++;
        } while (existingUsers != null && existingUsers.contains(nickname));
        return nickname;
    }

    /**
     * Determines if a given nickname is valid or invalid (contains at least
     * one alphanumeric character, and no non-alphanumeric characters).
     * @param name The channel or nickname string to validate
     * @return true if the string is a valid name
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Informs the model that the client with the given user ID has
     * disconnected from the server.  After a user ID is deregistered,
     * the server backend is free to reassign this user ID to an
     * entirely different client; as such, the model should remove all
     * state of the user associated with the deregistered user ID. The
     * behavior of this method if the given user ID is not registered
     * with the model is undefined.  Any user who is deregistered
     * (without later being registered) should not appear in the
     * output of {@link #getRegisteredUsers()}.
     *
     * @param userId The unique ID of the user to deregister
     * @return A {@link Broadcast} instructing clients to remove the
     * user from all channels
     */
    public Broadcast deregisterUser(int userId) {
        //get nickname of the user to be deregistered
        String nickname = registeredUsers.get(userId);
        //remove the user from the set of registeredUsers
        registeredUsers.remove(userId);
        Set<String> otherUsersInChannel = (Set)getOthersInChannel(nickname);
        
        //remove user from channels user was in 
        for (Map.Entry<String, Channel> mapEntry : channels.entrySet()) {
            Channel chanl = mapEntry.getValue();
            if (chanl.getUsers().contains(nickname)) {
                chanl.removeUser(nickname);
            } 
            //delete channel if the user was the owner 
            if (chanl.getOwner().equals(nickname)) {
                channels.remove(chanl); 
            }
        }
        //notify other channel users of the disconnection
        return Broadcast.disconnected(nickname, otherUsersInChannel);
    }


    //==========================================================================
    // Server model queries
    // These functions provide helpful ways to test the state of your model.
    // You may also use them in your implementation.
    //==========================================================================

    /**
     * Gets the user ID currently associated with the given
     * nickname. The returned ID is -1 if the nickname is not
     * currently in use.
     *
     * @param nickname The nickname for which to get the associated user ID
     * @return The user ID of the user with the argued nickname if
     * such a user exists, otherwise -1
     */
    public int getUserId(String nickname) {
        for (Map.Entry<Integer, String> mapEntry : registeredUsers.entrySet()) {
            if (nickname.contentEquals(mapEntry.getValue())) {
                int userId = mapEntry.getKey(); 
                return userId; 
            }
        }
        return -1;
    }

    /**
     * Gets the nickname currently associated with the given user
     * ID. The returned nickname is null if the user ID is not
     * currently in use.
     *
     * @param userId The user ID for which to get the associated
     *        nickname
     * @return The nickname of the user with the argued user ID if
     *          such a user exists, otherwise null
     */
    public String getNickname(int userId) {
        //Return nickname corresponding to ID i.e val in map K,V 
        for (Map.Entry<Integer, String> mapEntry : registeredUsers.entrySet()) {
            if (userId == mapEntry.getKey()) {
                String nickname = mapEntry.getValue();
                return nickname; 
            }
        }
        return null;
    }

    /**
     * Gets a collection of the nicknames of all users who are
     * registered with the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of registered user nicknames
     */
    //It's a set because users shouldn't have duplicates
    public Collection<String> getRegisteredUsers() {
        Collection<String> getRegisteredUsersColl = new TreeSet<>(registeredUsers.values());
        return getRegisteredUsersColl;
    }

    /**
     * Gets a collection of the names of all the channels that are
     * present on the server. Changes to the returned collection
     * should not affect the server state.
     * 
     * This method is provided for testing.
     *
     * @return The collection of channel names
     */
    public Collection<String> getChannels() {
        Collection<String> gotChannels = new TreeSet<>(channels.keySet());
        return gotChannels; 
    }

    /**
     * Gets a collection of the nicknames of all the users in a given
     * channel. The collection is empty if no channel with the given
     * name exists. Modifications to the returned collection should
     * not affect the server state.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get member nicknames
     * @return The collection of user nicknames in the argued channel
     */
    public Collection<String> getUsersInChannel(String channelName) {
        Set<String> gotUsersInChannel = channels.get(channelName).getUsers(); 
        Collection<String> users = new TreeSet<>(gotUsersInChannel);
        return users;
    }

    /**
     * Gets the nickname of the owner of the given channel. The result
     * is {@code null} if no channel with the given name exists.
     *
     * This method is provided for testing.
     *
     * @param channelName The channel for which to get the owner nickname
     * @return The nickname of the channel owner if such a channel
     * exists, otherwise null
     */
    public String getOwner(String channelName) {
       
        return channels.get(channelName).getOwner();
    }
    
    //get all other users in channels the user is in exclusive of the user
    public Collection<String> getOthersInChannel(String nickname) {
        Set<String> othersInChannel = new TreeSet<>();
        for (Map.Entry<String, Channel> mapEntry : channels.entrySet()) {
            boolean hasNicknameInMap = mapEntry.getValue().getUsers().contains(nickname);
            if (hasNicknameInMap) {
                Set<String> usersInMap = mapEntry.getValue().getUsers();
                othersInChannel.addAll(usersInMap);
            }
        }
        //exclude user when returning other users in the channel
        othersInChannel.remove(nickname); 
        return othersInChannel;
    }
    
    //get the channels user is in given just user nickname
    public Collection<Channel> getChannelGivenNickname(String nickname) {
        Set<Channel> channelsUserIsIn = new TreeSet<>();
        for (Map.Entry<String, Channel> mapEntry : channels.entrySet()) {
            Collection<String> usersInChanl = mapEntry.getValue().getUsers();
            boolean hasNickname = usersInChanl.contains(nickname);
            if (hasNickname) {
                channelsUserIsIn.add(mapEntry.getValue());
            }
        }
         
        return channelsUserIsIn;
    }
    

    
    //change users Nickname 
    public void changeNickname(int id, String currNickname) {
        String prevNickname = registeredUsers.get(id);
        registeredUsers.put(id, currNickname); 
        for (Channel chanl : channels.values()) {
            Set<String> usersInChanl = chanl.getUsers(); 
            if (usersInChanl.contains(prevNickname)) {
                //if the user was the owner, change chanl's owner's name
                String owner = chanl.getOwner(); 
                if (owner.equals(prevNickname)) {
                    chanl.setOwner(currNickname);
                //actually change the nickname
                    chanl.removeUser(prevNickname);
                    chanl.addUser(currNickname);
               
                }
            }
        }
    } 
    
    //channel creation 
    public void createChannel(String channelName, String owner) {
        Channel newChannel = new Channel(owner);
        //add channel to map of existing channels 
        channels.put(channelName, newChannel); 
        newChannel.addUser(owner);
    }
    
    //getting a specified channel in the map given channel's name
    public Channel getSpecifiedChannel(String channelName) {
        return channels.get(channelName);
    }
   
    //add users to a channel when they request to join a public channel
    public void joinChannel(Channel channel, String nickname) {
        channel.addUser(nickname);
    }
    
    //allow user to leave a channel - consider if owner or regular user
    public void leaveChannel(String channelName, String nickname) {
        String getChannelName = channels.get(channelName).getOwner(); 
        if (nickname.equals(getChannelName)) {
            channels.remove(channelName); 
        } else {
            channels.get(channelName).removeUser(nickname);
        }
    }

}
