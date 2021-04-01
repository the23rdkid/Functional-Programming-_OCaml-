//import java.util.*; 
//import java.util.Collection; 
//import java.util.Collections; 
import java.util.Set; 
import java.util.TreeSet; 

public class Channel implements Comparable {

    //fields 
    private Set<String> namesOfUsersInChannel; 
    private String owner; 
    private String name; 
    private boolean chIsPrivate; 
    
    public Channel(String owner, Boolean chIsPrivate) {
        namesOfUsersInChannel = new TreeSet<>(); 
        this.owner = owner; 
        namesOfUsersInChannel.add(owner); 
        this.chIsPrivate = chIsPrivate; 
    }
    
    public Channel(String owner) {
        this.owner = owner; 
        this.chIsPrivate = false; 
        namesOfUsersInChannel = new TreeSet<>(); 
        namesOfUsersInChannel.add(owner);
    }
    
    //getters
    public Set<String> getUsers() {
        return namesOfUsersInChannel; 
    }
    
    public String getOwner() {
        return this.owner; 
    }
    
    //setter 
    public void setOwner(String newName) {
        this.owner = newName; //set owner's name to a new one
    }
    
    
    public boolean privateState() {
        return this.chIsPrivate; 
    }
    
    public void addUser(String nickname) {
        namesOfUsersInChannel.add(nickname);
    }
    
    public void removeUser(String user) {
        namesOfUsersInChannel.remove(user);
    }
   
    public void changePrivateState(Boolean changeChPrivateState) {
        this.chIsPrivate = !chIsPrivate; 
    }
    
    //inherited abstract method that MUST be implemented
    @Override
    public int compareTo(Object o) {
        return Integer.compare(1, 0); 
    }
    @Override 
    public boolean equals(Object o) {
        if (o instanceof Channel) {
            return (this.name.equals(((Channel) o).name));
        }
        return false; 
    }
    
    @Override
    public String toString() {
        return name; 
    }
}
