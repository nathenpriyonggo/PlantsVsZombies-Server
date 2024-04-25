/*   Element State:
    0 - Nothing there
    1 - Ship but not hit
    2 - Ship hit but not sunk
*/

/*
        peaShip = new Ship(2);
        sunShip = new Ship(3);
        wallShip = new Ship(3);
        snowShip = new Ship(4);
        chompShip = new Ship(5);
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

public class AI {

    Ships AIships;
    Ships opponentShips = new Ships();
    private final int[] shipSizes = {5, 4, 3, 3, 2};
    private final int rowSize = 7;
    private final int colSize = 7;
    private Set<Element> allMovesMade;
    private ArrayList<Element> movesToMake;


    public AI() {
        this.AIships = new Ships();
        this.allMovesMade = new HashSet<>();
        this.movesToMake = new ArrayList<>();

    }

    // gets the ships class for the AI
    public Ships getAIships() {
        Random rand = new Random();

        for (int size : shipSizes) {

            boolean placed = false;
            boolean orient = rand.nextBoolean();
            while (!placed) {
                int x = rand.nextInt(rowSize);
                int y = rand.nextInt(colSize);
                System.out.println(x + " " + y + " " + size + " " + orient);
                if (placeShip(x, y, size, orient)) {
                    placed = true;
                }
            }
        }

        return AIships;
    }

    // NOTE TO Self - Can you set the urls to the correct images?
    // To help place the ships
    private boolean placeShip(int x, int y, int size, boolean Vertical) {

        if (!Vertical && ((x + size) > colSize)) {
            return false; // Ship exceeds grid boundaries
        }
        if (Vertical && (y + size) > rowSize) {
            return false; // Ship exceeds grid boundaries
        }

        for (int i = 0; i < size; i++) {
            if(Vertical) {
                if (AIships.isShipAtPosition(x, y + i)) {
                    return false;
                }
            }
            else {
                if (AIships.isShipAtPosition(x + i , y)) {
                    return false;
                }
            }
        }


        // If size two
        if (size == 2) {
            AIships.addNodeToPea(new Element(x, y, size, 1, null, null));
            // If the orient is vertical (true) place it below
            if (!Vertical) {
                AIships.addNodeToPea(new Element(x + 1, y, size, 1, null, null));
            } else {
                AIships.addNodeToPea(new Element(x, y + 1, size, 1, null, null));
            }
        } else if (size == 3 && !(AIships.isDuplicate())) {
            AIships.setDuplicate();
            AIships.addNodeToSun(new Element(x, y, size, 1, null, null));
            if (!Vertical) {
                AIships.addNodeToSun(new Element(x + 1, y, size, 1, null, null));
                AIships.addNodeToSun(new Element(x + 2, y, size, 1, null, null));
            } else {
                AIships.addNodeToSun(new Element(x, y + 1, size, 1, null, null));
                AIships.addNodeToSun(new Element(x, y + 2, size, 1, null, null));
            }
        } else if (size == 3 && (AIships.isDuplicate())) {
            AIships.addNodeToWall(new Element(x, y, size, 1, null, null));
            if (!Vertical) {
                AIships.addNodeToWall(new Element(x + 1, y, size, 1, null, null));
                AIships.addNodeToWall(new Element(x + 2, y, size, 1, null, null));
            } else {
                AIships.addNodeToWall(new Element(x, y + 1, size, 1, null, null));
                AIships.addNodeToWall(new Element(x, y + 2, size, 1, null, null));
            }
        } else if (size == 4) {
            AIships.addNodeToSnow(new Element(x, y, size, 1, null, null));
            if (!Vertical) {
                AIships.addNodeToSnow(new Element(x + 1, y, size, 1, null, null));
                AIships.addNodeToSnow(new Element(x + 2, y, size, 1, null, null));
                AIships.addNodeToSnow(new Element(x + 3, y, size, 1, null, null));
            } else {
                AIships.addNodeToSnow(new Element(x, y + 1, size, 1, null, null));
                AIships.addNodeToSnow(new Element(x, y + 2, size, 1, null, null));
                AIships.addNodeToSnow(new Element(x, y + 3, size, 1, null, null));
            }
        } else if (size == 5) {
            AIships.addNodeToChomp(new Element(x, y, size, 1, null, null));
            if (!Vertical) {
                AIships.addNodeToChomp(new Element(x + 1, y, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x + 2, y, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x + 3, y, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x + 4, y, size, 1, null, null));
            } else {
                AIships.addNodeToChomp(new Element(x, y + 1, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x, y + 2, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x, y + 3, size, 1, null, null));
                AIships.addNodeToChomp(new Element(x, y + 4, size, 1, null, null));
            }
        }
        return true;
    }

    public ArrayList<Element> generateMoves(Ships gameState) {
        this.opponentShips = new Ships(gameState);
        System.out.println("Generating Moves");
        int gridWidth = 7;
        int centerX = 3;
        int centerY = 3;

        // Randomly interleave strategic and random moves
        for (int d = 0; d < gridWidth; d++) {
            for (int i = -d; i <= d; i++) {
                for (int j = -d; j <= d; j++) {
                    if (Math.abs(i) == d || Math.abs(j) == d) {
                        int x = centerX + i;
                        int y = centerY + j;
                        if (isValidPosition(x, y)) {
                            addSmartMove(x, y);
                        }
                    }
                }
            }
        }


        return movesToMake;
    }

    private void addSmartMove(int x, int y) {
        Element move = new Element(x, y, 0, 0, "", "");
        if (isValidMove(x, y) && !allMovesMade.contains(move)) {
            movesToMake.add(move);
            allMovesMade.add(move);
        }
    }


    private boolean isValidMove(int x, int y) {
        return isValidPosition(x, y) && opponentShips.isPositionHit(x, y);  // Ensure the position has NOT been hit
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 7 && y >= 0 && y < 7;  // Ensure the coordinates are within the grid bounds
    }
}





