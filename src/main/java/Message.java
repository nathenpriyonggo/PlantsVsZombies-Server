import java.io.Serializable;
import java.util.Objects;


public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    private String data, username, receiver, flag;
    private Element element;



    public Message(String playerName, String OpponentName , Element element, String flag) {
        this.username = origUsername;
        this.data = data;
        this.receiver = destUsername;
        this.flag = flag;
    }


    public String getData() {return data;}
    public String getUsername() {return username;}
    public String getReceiver() {return receiver;}
    public boolean isCheckUniqueName() {return Objects.equals(flag, "isCheckUniqueName");}
    public boolean isInfoName() {return (Objects.equals(flag, "isInfoName"));}
    public boolean isPublicText() {return (Objects.equals(flag, "isPublicText"));}
    public boolean isPrivateText() {return (Objects.equals(flag, "isPrivateText"));};
    public boolean isUpdateFriends() {return (Objects.equals(flag, "isUpdateFriends"));}
    public Element getElement(){return element;}

    public class Element {
        int x_coord, y_coord; // The position of the element
        int shipSize; // To determine the element's size
        int elementState; // To determine the state of each element

        // Now we can create the 2d array of elements
    }
}