import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class MI 
{
	RemoteNXTFunctions NXT;
	private Stack<Move> simulatedMoves = new Stack<Move>();
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
	
	/* -----------------------------------------------------------------------------------  *
	/* MI brain starts */
	
	/* how much the AI/MI looks forward */
	private int numberofmovelook			= 3;
	/*points*/
	private int ownMovePoint				= 2;
	private int ownJumpPoint				= 4;
	
	private int opponentMovePoint			= 1;
	private int opponentJumpPoint			= 2;
	
	/* bonus point for doing specific moves */
	private int ownMiddleMoveBonus 			= 2;
	private int opponentMiddleMoveBonus 	= 1; /* tror ikke kan bruges*/
	
	private int ownMoveLastRowPenalty 		= 1;
	private int opponentMoveLastRowPenalty 	= 2;
	
	/* how glad the MI/AI are for the result of the game */
	private int gameIsWon = 10;
	private int gameIsLost = 1;
	private int gameIsdraw = 2;
	
	
	public Move lookForBestMove() /* does not start to see if the game is ended*/
	{
	    List<Move> Moves = possibleMovesForRobot();
		Move bestMove = null;
		
		double price = 0, tempPrice;
		
		for(Move move : Moves)
		{
			tempPrice =  opponentTurn(move, 1);
			
			if(price < tempPrice)
			{
				price = tempPrice;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	private double ownTurn(Move move, int moveLook)
	{
		int numberOfMoves = 0;
		double sum = 0;
		double price = 0;
		/* do move on representation board */
		
		int result = NXT.checkersBoard.gameIsEnded(true);
		if( result != 0 && numberofmovelook >= moveLook)
		{
			if(result == 1)
				price = gameIsLost;
			if(result == 2)
				price = gameIsWon;
			if(result == 3)
				price = gameIsdraw;
		}
		else if(numberofmovelook >= moveLook)
		{
			price = findOwnPrice(move);
			List<Move> Moves = possibleMovesForRobot();
			for(Move tempMove : Moves)
			{
				sum = sum + opponentTurn(tempMove, moveLook+1);
				numberOfMoves++;
			}
			sum = sum/numberOfMoves;
			moveLook ++;
		}
		/* undo move on representation board */
		return price + sum;
	}
	
	private double opponentTurn(Move move, int moveLook)
	{
		int numberOfMoves = 0;
		double sum = 0;
		double price = 0;
		/* do move on representation board */
		
		int result = NXT.checkersBoard.gameIsEnded(false);
		if( result > 0 && numberofmovelook >= moveLook)
		{
			if(result == 1)
				price = gameIsLost;
			if(result == 2)
				price = gameIsWon;
			if(result == 3)
				price = gameIsdraw;
		}
		
		else if(numberofmovelook >= moveLook)
		{
			price = findOpponentPrice(move);
			List<Move> Moves = possibleMovesForHuman();
			for(Move tempMove : Moves)
			{
				sum = sum + ownTurn(tempMove, moveLook+1);
				numberOfMoves++;
			}
			sum = sum/numberOfMoves;
			moveLook ++;
		}
		/* undo move on representation board */
		return price + sum;
	}
	
	
	private double findOpponentPrice(Move move)
	{
		double price = 0;
		
		if(move.isJump == true)
		{
			price = price + opponentJumpPoint * move.moveTo.size();
		}
		else
		{
			price = price + opponentMovePoint;
		}
		return price;
	}
	private double findOwnPrice(Move move)
	{
		double price = 0;
		
		if(move.isJump == true)
		{
			price = price + ownJumpPoint * move.moveTo.size();
		}
		else
		{
			price = price + ownMovePoint;
		}
		return price;
	}
	
	/* MI brain stops */
	/* -----------------------------------------------------------------------------------  */
	private List<Move> possibleMovesForHuman()
	{
		return possibleMoves(-1);
	}
	
	private List<Move> possibleMovesForRobot()
	{
		return possibleMoves(1);
	}
	
	private void simulateMove(Field fromField, Field toField) throws Exception
	{
		Move temp = new Move(fromField, toField, false);
		if(!fromField.isEmpty())
		{
			NXT.checkersBoard.movePiece(fromField, toField);
			simulatedMoves.push(temp);
		}
	}
	
	private void revertMove() throws Exception
	{
		if(simulatedMoves.size() != 0)
		{
			Move temp = simulatedMoves.pop();
			NXT.checkersBoard.movePiece(temp.moveTo, temp.moveFrom);
		}
	}
	
	private List<Move> possibleMoves(int moveForSide) //-1 = human, 1 = robot
	{
		List<Move> movements = new ArrayList<Move>();
		
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
							Move movement = new Move(field, posField, false);
							movements.add(movement);
						}
					}
					
					//Jumps
					Field jumpDirectionForward = NXT.checkersBoard.checkJumpDirection(field, 1, moveForSide, false);
					if(jumpDirectionForward != null)
					{
						Move movement = new Move(field, jumpDirectionForward, true);
						movements.add(movement);
					}
					
					Field jumpDirectionBackwards = NXT.checkersBoard.checkJumpDirection(field, -1, moveForSide, false);
					if(jumpDirectionBackwards != null)
					{
						Move movement = new Move(field, jumpDirectionBackwards, true);
						movements.add(movement);
					}
				}
			}
		}
		
		return movements;
	}
}
