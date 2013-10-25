import java.util.Queue;


public class Move {
	public Field moveFrom;
	public Queue<Field> moveTo;
	public boolean isJump;
	
	Move(Field movefrom, Field moveto, boolean isjump)
	{
		Queue<Field> moveToList = new Queue<Field>();
		
		moveToList.push(moveto);
		
		this.moveFrom = movefrom;
		this.moveTo = moveToList;
		this.isJump = isjump;
	}
}

