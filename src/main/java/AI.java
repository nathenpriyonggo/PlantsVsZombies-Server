//import java.util.ArrayList;
//import java.util.Random;
//
//public class AI {
//
//    private Ships opponentShips;
//    private ArrayList<Integer> previousHitsX;
//    private ArrayList<Integer> previousHitsY;
//    private boolean previousHit;
//
//    public AI(Ships opponentShips) {
//        this.opponentShips = opponentShips;
//        this.previousHitsX = new ArrayList<>();
//        this.previousHitsY = new ArrayList<>();
//        this.previousHit = false;
//    }
//
//    public void makeMove() {
//        int x, y;
//        if (previousHit) {
//            x = previousHitsX.get(previousHitsX.size() - 1);
//            y = previousHitsY.get(previousHitsY.size() - 1);
//        } else {
//            Random random = new Random();
//            x = random.nextInt(10);
//            y = random.nextInt(10);
//        }
//
//        if (isValidMove(x, y)) {
//            Element hitElement = opponentShips.didItHitZombie(x, y);
//            if (hitElement.getElementState() > 0) {
//                System.out.println("Hit at (" + x + ", " + y + ")");
//                opponentShips.updateHitElement(hitElement);
//                previousHitsX.add(x);
//                previousHitsY.add(y);
//                previousHit = true;
//                if (opponentShips.isAllShipsSunk()) {
//                    System.out.println("All opponent's ships sunk! You win!");
//                }
//            } else {
//                System.out.println("Miss at (" + x + ", " + y + ")");
//                previousHit = false;
//            }
//        } else {
//            makeMove();
//        }
//    }
//
//    private boolean isValidMove(int x, int y) {
//        if (x < 0 || x >= 10 || y < 0 || y >= 10) {
//            return false; // Move is outside the board boundaries
//        }
//
//        Element element = opponentShips.getElementAtPosition(x, y);
//        if (element.getElementState() != 0) {
//            return false;
//        }
//
//        return true;
//    }
//
//
//}