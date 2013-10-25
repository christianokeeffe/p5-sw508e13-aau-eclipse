import java.io.IOException;


public class Move {
	public Field moveFrom;
	public Field moveTo;
	public boolean isJump;
	
	Move(Field movefrom, Field moveto, boolean isjump)
	{
		this.moveFrom = movefrom;
		this.moveTo = moveto;
		this.isJump = isjump;
	}
}

