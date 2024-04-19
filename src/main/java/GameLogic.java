import java.util.ArrayList;

public class GameLogic {
    Ships ships_player1, ships_player2;

    // new Element("jack", "membra", 2, 3, 3, 1, "playerMove");
    public GameLogic() {

    }

    /*
       Helper Functions
     */

    /*   Element State:
         0 - Nothing there
         1 - Ship but not hit
         2 - Ship hit but not sunk
         3 - Ship hit and sunk -> for gravestones, return when have time
    */

    // ArrayList<Element> array_player1, array_player2;
    // Based on the Sum of each grid determine if the players win or not
    //  15 + 12 + 9 + 9 + 6 = 51 (all sunk)
    public String didSomeoneWin(){
        if(ships_player1.getSum() == 51) {
            return "Player one wins";
        }
        else if(ships_player2.getSum() == 51) {
            return "Player two wins";
        }
        else {
            return "Keep Playing";
        }
    }

    // This
    public void playerMove(Element elem) {
        int x = elem.getX();
        int y = elem.getY();
    }

}
