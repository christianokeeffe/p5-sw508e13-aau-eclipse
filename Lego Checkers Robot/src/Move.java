import java.util.ArrayList;
import java.util.List;

public class Move {
    public List<Field> moves;
    //public boolean isJump;
    public List<Piece> takenPieces = new ArrayList<Piece>();
    public boolean wasKingBefore = false;

    Move(Field movefrom, Field moveto, boolean wasKing) {
        List<Field> moveToList = new ArrayList<Field>();
        moveToList.add(movefrom);
        moveToList.add(moveto);                                             //HERE CHANGE
        this.moves = moveToList;
        this.wasKingBefore = wasKing;
    }

    Move(List<Field> moveToList, boolean wasKing) {
        this.moves = moveToList;
        this.wasKingBefore = wasKing;
    }

    Move() {
        List<Field> moveToList = new ArrayList<Field>();
        this.moves = moveToList;
    }

    public final boolean isJump() {
        if (moves.size() >= 2) {
            //Field from = moves.pop();
            //Field to = moves.peek();                      ///HERE CHANGE
            //moves.push(from);
            
            Field to = moves.get(moves.size()-1);
            Field from  = moves.get(moves.size()-2);
            
            if (Math.abs(from.x - to.x) == 2 && Math.abs(from.y - to.y) == 2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    public final void addStep(Field step) {
        this.moves.add(step);
    }
}

