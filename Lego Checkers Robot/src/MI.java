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
		Hashtable<Field, Field> movements = possibleMoves(-1);
		return movements;
	}
	
	public Hashtable<Field, Field> possibleMovesForRobot()
	{
		Hashtable<Field, Field> movements = possibleMoves(1);
		return movements;
	}
	
	public Hashtable<Field, Field> possibleMoves(int moveForSide) //-1 = human, 1 = robot
	{
		Hashtable<Field, Field> movements = new Hashtable<Field,Field>();
		
		for(Field[] f : NXT.checkersBoard.myBoard)
		{
			for(Field field : f)
			{
				if(field.getPieceOnField().isMoveable)
				{
					List<Field> possibleMoves = NXT.checkersBoard.checkMoveable(field, moveForSide);
					//Simple moves
					if(!possibleMoves.isEmpty())
					{
						for(Field posField : possibleMoves)
						{
							movements.put(field, posField);
						}
					}
					
					//Jumps
					Field jumpDirectionForward = NXT.checkersBoard.checkJumpDirection(field, 1, moveForSide, false);
					if(jumpDirectionForward != null)
					{
						movements.put(field, jumpDirectionForward);
					}
					
					Field jumpDirectionBackwards = NXT.checkersBoard.checkJumpDirection(field, -1, moveForSide, false);
					if(jumpDirectionBackwards != null)
					{
						movements.put(field, jumpDirectionBackwards);
					}
				}
			}
		}
		
		return movements;
	}
}
