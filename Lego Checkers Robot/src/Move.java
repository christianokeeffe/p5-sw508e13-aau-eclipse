import java.util.Queue;


public class Move {
	public Field moveFrom;
	public Queue<Field> moveTo;
	public boolean isJump;
	
	Move(Field movefrom, Field moveto, boolean isjump)
	{
		Queue<Field> moveToList = new Queue<Field>();
		moveToList.addElement(moveto);
		
		this.moveFrom = movefrom;
		this.moveTo = moveToList;
		this.isJump = isjump;
	}
	
	Move(Field movefrom, boolean isjump)
	{
		Queue<Field> moveToList = new Queue<Field>();
		
		this.moveFrom = movefrom;
		this.moveTo = moveToList;
		this.isJump = isjump;
	}
	
	public void addStep(Field step)
	{
		this.moveTo.addElement(step);
	}
}

