import java.util.Collection;
import java.util.Collections; 
import java.util.Set; 
import java.util.TreeSet; 
/**
 * Represents a command string sent from a client to the server, after it has
 * been parsed into a more convenient form. The {@code Command} abstract class
 * has a concrete subclass corresponding to each of the possible commands that
 * can be issued by a client. The protocol specification contains more
 * information about the expected behavior of various commands.
 */
public abstract class Command {

    /**
     * The server-assigned ID of the user who sent the {@code Command}.
     */
    private final int senderId;

    /**
     * The current nickname in use by the sender of the {@code Command}.
     */
    private final String sender;

    /**
     * Constructor, initializes the private fields of the object. 
     */
    Command(int senderId, String sender) {
        this.senderId = senderId;
        this.sender = sender;
    }

    /**
     * Gets the user ID of the client who issued the {@code Command}.
     *
     * @return The user ID of the client who issued this command
     */
    public int getSenderId() {
        return senderId;
    }

    /**
     * Gets the nickname of the client who issued the {@code Command}.
     *
     * @return The nickname of the client who issued this command
     */
    public String getSender() {
        return sender;
    }

    /**
     * Processes the command and updates the server model accordingly.
     *
     * @param model An instance of the {@link ServerModelApi} class which
     *              represents the current state of the server.
     * @return A {@link Broadcast} object, informing clients about changes
     *      resulting from the command.
     */
    public abstract Broadcast updateServerModel(ServerModel model);

    /**
     * Returns {@code true} if two {@code Command}s are equal; that is, if
     * they produce the same string representation. 
     * 
     * Note that all subclasses of {@code Command} must override their 
     * {@code toString} method appropriately for this definition to make sense.
     * (We have done this for you below).
     *
     * @param o the object to compare with {@code this} for equality
     * @return true iff both objects are non-null and equal to each other
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Command)) {
            return false;
        }
        return this.toString().equals(o.toString());
    }
}


//==============================================================================
// Command subclasses
//==============================================================================

/**
 * Represents a {@link Command} issued by a client to change his or her nickname.
 */
class NicknameCommand extends Command {
    private final String newNickname;

    public NicknameCommand(int senderId, String sender, String newNickname) {
        super(senderId, sender);
        this.newNickname = newNickname;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        int senderID = getSenderId(); 
        String gotSender = getSender(); 

        Collection<String> usersToBroadcastTo = model.getOthersInChannel(gotSender); 
        usersToBroadcastTo.add(gotSender); 
        if (model.getRegisteredUsers().contains(newNickname)) {
            return Broadcast.error(this, ServerResponse.NAME_ALREADY_IN_USE);
        }

        //change nickname in registered users map and all channels user was part of 
        Collection<Channel> channelsUsersPartOf = model.getChannelGivenNickname(gotSender); 
        if (ServerModel.isValidName(newNickname)) {
            model.changeNickname(senderID, newNickname); 
            return Broadcast.okay(this, usersToBroadcastTo);
        }
        //error if the name is invalid
        return Broadcast.error(this, ServerResponse.INVALID_NAME);

    }

    public String getNewNickname() {
        return newNickname; 
    }

    @Override
    public String toString() {
        return String.format(":%s NICK %s", getSender(), newNickname);
    }
}

/**
 * Represents a {@link Command} issued by a client to create a new channel.
 */
class CreateCommand extends Command {
    private final String channel;
    private final boolean inviteOnly;

    public CreateCommand(int senderId, String sender, String channel, boolean inviteOnly) {
        super(senderId, sender);
        this.channel = channel;
        this.inviteOnly = inviteOnly;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        // if the name is valid, create a channel
        String sender = getSender(); 
        Collection<String> newCollOfUsers = new TreeSet<String>(); 
        newCollOfUsers.add(sender); 
        if (ServerModel.isValidName(channel)) {
            model.createChannel(channel, sender);
            return Broadcast.okay(this, newCollOfUsers); 
        }
        //if channel name is already in use, throw an error
        if (model.getChannels().contains(channel)) {
            return Broadcast.error(this, ServerResponse.NAME_ALREADY_IN_USE); 
        } 
        //else return an error message if the name is invalid i.e can't create channel
        return Broadcast.error(this, ServerResponse.INVALID_NAME);
    }

    public String getChannel() {
        return channel;
    }

    public boolean isInviteOnly() {
        return inviteOnly;
    }

    @Override
    public String toString() {
        int flag = inviteOnly ? 1 : 0;
        return String.format(":%s CREATE %s %d", getSender(), channel, flag);
    }
}

/**
 * Represents a {@link Command} issued by a client to join an existing
 * channel.  All users in the channel (including the new one) should be
 * notified about when a "join" occurs.
 */
class JoinCommand extends Command {
    private final String channel;

    public JoinCommand(int senderId, String sender, String channel) {
        super(senderId, sender);
        this.channel = channel;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        //if a channel exists, but is private the client can't join it 
        if (model.getChannels().contains(channel)) {
            Channel privChannel = model.getSpecifiedChannel(channel); 
            if (privChannel.privateState()) {
                return Broadcast.error(this, ServerResponse.JOIN_PRIVATE_CHANNEL); 
            }
            String sender = getSender(); 
            String owner = model.getSpecifiedChannel(channel).getOwner(); 
            Set<String> usersInChannel = model.getSpecifiedChannel(channel).getUsers(); 

            //add user that requested to join channel 
            model.joinChannel(model.getSpecifiedChannel(channel), sender); 
            //notify existing users in channel of the join 
            return Broadcast.names(this, usersInChannel, owner); 
        }
        //if channel is non-existent, throw an error message
        return Broadcast.error(this, ServerResponse.NO_SUCH_CHANNEL);
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return String.format(":%s JOIN %s", getSender(), channel);
    }
}

/**
 * Represents a {@link Command} issued by a client to send a message to all
 * other clients in the channel.
 */
class MessageCommand extends Command {
    private final String channel;
    private final String message;

    public MessageCommand(int senderId, String sender, 
            String channel, String message) {
        super(senderId, sender);
        this.channel = channel;
        this.message = message;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        // if channel is non-existent, can't send a message to it 
        if (!model.getChannels().contains(channel)) {
            return Broadcast.error(this, ServerResponse.NO_SUCH_CHANNEL);
        }

        String sender = getSender(); 
        Channel chanl = model.getSpecifiedChannel(channel); 
        Set<String> usersInChannel = chanl.getUsers(); 

        //send the message if the sender is a member of that channel
        if (chanl.getUsers().contains(sender)) {
            return Broadcast.okay(this, usersInChannel); 
        }

        //error message if the user is not a member of that channel
        return Broadcast.error(this, ServerResponse.USER_NOT_IN_CHANNEL); 
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return String.format(":%s MESG %s :%s", getSender(), channel, message);
    }
}

/**
 * Represents a {@link Command} issued by a client to leave a channel.
 */
class LeaveCommand extends Command {
    private final String channel;

    public LeaveCommand(int senderId, String sender, String channel) {
        super(senderId, sender);
        this.channel = channel;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        //if the channel is non-existent throw an error
        if (!(model.getChannels().contains(channel))) {
            return Broadcast.error(this, ServerResponse.NO_SUCH_CHANNEL); 
        }

        Channel chanl = model.getSpecifiedChannel(channel); 
        String sender = getSender(); 
        Set<String> usersInChannel = chanl.getUsers(); 

        //make a copy of the users in the channel so removing a user doesn't 
        //affect remaining users in channel 
        if (usersInChannel.contains(sender)) {
            model.leaveChannel(channel, sender); 
            Set<String> usersInCopiedChannel = new TreeSet<String>(); 
            usersInCopiedChannel.addAll(usersInChannel);
            usersInCopiedChannel.add(sender); 
            System.out.println(sender);
            System.out.println(usersInCopiedChannel); 

            return Broadcast.okay(this, usersInCopiedChannel); 
        }
        //error if trying to leave channel user is not a member of
        return Broadcast.error(this, ServerResponse.USER_NOT_IN_CHANNEL);
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return String.format(":%s LEAVE %s", getSender(), channel);
    }
}

/**
 * Represents a {@link Command} issued by a client to add another client to an
 * invite-only channel owned by the sender.
 */
class InviteCommand extends Command {
    private final String channel;
    private final String userToInvite;

    public InviteCommand(int senderId, String sender, String channel, String userToInvite) {
        super(senderId, sender);
        this.channel = channel;
        this.userToInvite = userToInvite;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        //throw error if invitee doesn't exist 
        if (model.getUserId(userToInvite) == -1) {
            return Broadcast.error(this, ServerResponse.NO_SUCH_USER); 
        //if the channel exists, check for supposed errors, if none invite to the priv.channel
        } else if (model.getChannels().contains(channel)) {
            //throw an error if sender isn't the owner
            String sender = getSender(); 
            boolean checkOwner = model.getSpecifiedChannel(channel).getOwner().equals(sender); 
            if (!checkOwner) {
                return Broadcast.error(this, ServerResponse.USER_NOT_OWNER); 
            //throw an error if trying to invite to a public channel
            } else if (model.getSpecifiedChannel(channel).privateState()) {
                return Broadcast.error(this, ServerResponse.INVITE_TO_PUBLIC_CHANNEL);
            }

            //allow invitee to join channel if no errors are encountered
            Set<String> usersInChannel = model.getSpecifiedChannel(channel).getUsers(); 
            String owner = model.getSpecifiedChannel(channel).getOwner(); 
            model.joinChannel(model.getSpecifiedChannel(channel), userToInvite); 
            //notify users in channel of the invitee's join 
            return Broadcast.names(this, usersInChannel, owner);
        } else {
            return Broadcast.error(this, ServerResponse.NO_SUCH_CHANNEL);
        }
    }

    public String getChannel() {
        return channel;
    }

    public String getUserToInvite() {
        return userToInvite;
    }

    @Override
    public String toString() {
        return String.format(":%s INVITE %s %s", getSender(), channel, userToInvite);
    }
}

/**
 * Represents a {@link Command} issued by a client to remove another client
 * from a channel owned by the sender. Everyone in the initial channel
 * (including the user being kicked) should be informed that the user was
 * kicked.
 */
class KickCommand extends Command {
    private final String channel;
    private final String userToKick;

    public KickCommand(int senderId, String sender, String channel, String userToKick) {
        super(senderId, sender);
        this.channel = channel;
        this.userToKick = userToKick;
    }

    @Override
    public Broadcast updateServerModel(ServerModel model) {
        //throw an error if the user to kick out doesn't exist completely
        boolean hasUserToKick = model.getRegisteredUsers().contains(userToKick);
        if (!hasUserToKick) {
            return Broadcast.error(this, ServerResponse.NO_SUCH_USER);
        }
        
        //if the channel exists, check for supposed errors, if none kick from the priv.channel
        boolean channelToKickFrom = model.getChannels().contains(channel);
        if (channelToKickFrom) {
            //throw an error is sender/kicker isn't the owner
            String sender = getSender(); 
            boolean checkOwner = model.getSpecifiedChannel(channel).getOwner().equals(sender); 
            if (!checkOwner) {
                return Broadcast.error(this, ServerResponse.USER_NOT_OWNER); 
            }
            
            //throw error if user to kick out doesn't exist in channel
            Channel chanl = model.getSpecifiedChannel(channel); 
            boolean hasUser = chanl.getUsers().contains(userToKick);
            Set<String> usersInChannel = new TreeSet<String>(); 
            usersInChannel.addAll(chanl.getUsers()); 
            if (!hasUser) {
                return Broadcast.error(this, ServerResponse.USER_NOT_IN_CHANNEL);
            }
            
            //actually kick out user if no errors are encountered
            model.leaveChannel(channel, userToKick);
            boolean kickeeIsOwner = chanl.getOwner().equals(userToKick);
            if (kickeeIsOwner) {
                model.leaveChannel(channel, chanl.getOwner());
            }
            return Broadcast.okay(this, usersInChannel);
        }
        return Broadcast.error(this, ServerResponse.NO_SUCH_CHANNEL);
    }

    @Override
    public String toString() {
        return String.format(":%s KICK %s %s", getSender(), channel, userToKick);
    }
}

