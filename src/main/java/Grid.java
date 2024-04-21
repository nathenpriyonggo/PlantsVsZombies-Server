import jdk.javadoc.internal.tool.ElementsTable;

import java.sql.Array;
import java.util.ArrayList;

public class Grid {
    private ArrayList<ArrayList<Element>> grid;


    // Default Constructor
    public Grid() {
        this.grid = new ArrayList<>();
        initializeGrid();
    }

    // Message to grid
    public Grid(ArrayList<Element> message) {
        this.grid = new ArrayList();
        for (int i = 0; i < 7; i++) {
            ArrayList<Element> row = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                int idx = i * 7 + j;
                Element element = message.get(idx);
                row.add(new Element(element.getPlayer(), element.getOpponent(), element.getX(),
                        element.getY(), element.getShipSize(), element.getElementState(),
                        element.getUrl(), element.getFlag()));
            }
            grid.add(row);
        }
    }

    // only for ai
    private void initializeGrid() {
        for (int i = 0; i < 7; i++) {
            ArrayList<Element> row = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
//                row.add(new Element("player", "opp", i, j, 0, 0, ""));
            }
            grid.add(row);
        }
    }

    public void setElement(int x, int y, Element element) {
        grid.get(x).set(y, element);
    }

    public Element getElement(int x, int y) {
        return grid.get(x).get(y);
    }

    //Server will determine state value and then change element state
    //Server will then return element to client
    public void playerAction(Element element, int State)
    {
        element.setElementState(State);
    }

    //this will send sunken message to client
    public void sendSunkShip()
    {

    }

    public void printGrid() {
        ArrayList<Element>[] z = new ArrayList[0];
        for (ArrayList<Element> row : z) {
            for (Element element : row) {
                System.out.print(element.getElementState() + " ");
            }
            System.out.println();
        }
    }

    public int getSum() {
        int sum = 0;
        for (ArrayList<Element> row: grid) {
            for (Element element : row) {
                sum += element.getElementState();
            }
        }
        return sum;
    }

}


