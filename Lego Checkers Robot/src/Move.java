import java.util.Stack;

public class Move {
	public Stack<Field> moves;
	//public boolean isJump;
	public Stack<Piece> takenPieces = new Stack<Piece>();
	
	Move(Field movefrom, Field moveto)
	{
		Stack<Field> moveToList = new Stack<Field>();
		
		moveToList.push(moveto);
		moveToList.push(movefrom);
		this.moves = moveToList;
	}
	
	Move(Stack<Field> moveToList)
	{
		this.moves = moveToList;
	}
	
	public boolean isJump()
	{
		if(moves.size() >= 2)
		{
			Field from = moves.pop();
			Field to = moves.peek();
			moves.push(from);
			
			if(Math.abs(from.x - to.x) == 2 && Math.abs(from.y - to.y) == 2)
				return true;
			return false;
		}
		else
		{
			return false;
		}
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

