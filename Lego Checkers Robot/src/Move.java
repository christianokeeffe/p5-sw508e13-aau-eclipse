import java.util.Stack;


public class Move {
	public Stack<Field> moves;
	public boolean isJump;
	public Stack<Piece> takenPieces = new Stack<Piece>();
	
	Move(Field movefrom, Field moveto, boolean isjump)
	{
		Stack<Field> moveToList = new Stack<Field>();
		
		moveToList.push(moveto);
		moveToList.push(movefrom);
		this.moves = moveToList;
		this.isJump = isjump;
	}
	
	Move(Stack<Field> moveToList, boolean isjump)
	{
		this.moves = moveToList;
		this.isJump = isjump;
	}
	
	Move()
	{
		Stack<Field> moveToList = new Stack<Field>();
		this.moves = moveToList;
	}
	
	public void addStep(Field step)
	{
		this.moves.push(step);
	}
}

