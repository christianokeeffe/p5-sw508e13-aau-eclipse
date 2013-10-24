import java.io.IOException;
import java.util.Hashtable;
import java.util.List;


public class MI 
{
	RemoteNXTFunctions NXT;
	
	MI()
	{
		try {
			NXT = new RemoteNXTFunctions();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  
	public Hashtable<Field, Field> possibleMovesForHuman()
	{
		Hashtable<Field, Field> movements = new Hashtable<Field,Field>();
		
		for(Field[] f : NXT.checkersBoard.myBoard)
		{
			for(Field field : f)
			{
				if(field.getPieceOnField().isMoveable)
				{
					List<Field> possibleMoves = NXT.checkersBoard.checkMoveable(field, -1);
					//Simple moves
					if(!possibleMoves.isEmpty())
					{
						for(Field posField : possibleMoves)
						{
							movements.put(field, posField);
						}
					}
					
					//Jumps
					if(NXT.checkersBoard.checkJumpDirection(field, 1, -1, false) != null)
					{
						movements.put(field, NXT.checkersBoard.checkJumpDirection(field, 1, -1, false));
					}
					if(NXT.checkersBoard.checkJumpDirection(field, -1, -1, false) != null)
					{
						movements.put(field, NXT.checkersBoard.checkJumpDirection(field, -1, -1, false));
					}
				}
			}
		}
		
		return movements;
	}
}
