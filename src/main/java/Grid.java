import java.util.ArrayList;

public class Grid {
    private ArrayList<ArrayList<Element>> grid;

    public Grid() {
        this.grid = new ArrayList<>();
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < 7; i++) {
            ArrayList<Element> row = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                row.add(new Element("player", "opp", i, j, 0, 0, ""));
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

    public void printGrid() {
        ArrayList<Element>[] z = new ArrayList[0];
        for (ArrayList<Element> row : z) {
            for (Element element : row) {
                System.out.print(element.getElementState() + " ");
            }
            System.out.println();
        }
    }

}


