import java.util.ArrayList;
import java.util.List;

public class Move {
    public List<Field> moves;
    //public boolean isJump;
    public List<Piece> takenPieces = new ArrayList<Piece>();
    public boolean wasKingBefore = false;

    public Move(Field movefrom, Field moveto, boolean wasKing) {
        List<Field> moveToList = new ArrayList<Field>();
        moveToList.add(movefrom);
        moveToList.add(moveto);
        this.moves = moveToList;
        this.wasKingBefore = wasKing;
    }

    public Move(List<Field> moveToList, boolean wasKing) {
        this.moves = moveToList;
        this.wasKingBefore = wasKing;
    }

    public Move() {
        List<Field> moveToList = new ArrayList<Field>();
        this.moves = moveToList;
    }

    public final boolean isJump() {
        if (moves.size() >= 2) {
            Field to = moves.get(1);
            Field from  = moves.get(0);

            return Math.abs(from.x - to.x) == 2 && Math.abs(from.y - to.y) == 2;
        } else {
            return false;
        }
    }

    public final void addStep(Field step) {
        this.moves.add(step);
    }
}

