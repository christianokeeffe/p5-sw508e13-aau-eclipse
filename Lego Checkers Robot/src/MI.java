import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.util.Delay;
import customExceptions.NoKingLeft;


public class MI 
{
	RemoteNXTFunctions nXTF;

	private Stack<Move> simulatedMoves = new Stack<Move>();
	MI(RemoteNXTFunctions NXT)
	{
		nXTF = NXT;
	}

	/* -----------------------------------------------------------------------------------  *
	/* MI brain starts */

	/* how much the AI/MI looks forward */
	private int numberofmovelook		= 2;
	/*points*/
	private int MovePoint				= 4;
	private int JumpPoint				= 8;

	/* bonus point for doing specific moves */
	private int MiddleMoveBonus 		= 3;

	private int MoveLastRowPenalty 		= 1;

	/* how glad the MI/AI are for the result of the game */
	private int gameIsWon = 10;
	private int gameIsLost = -gameIsWon;
	private int gameIsDraw = 5;


	public Move lookForBestMove() throws NoKingLeft, IOException /* does not start to see if the game is ended*/, InterruptedException
	{
		List<Move> Moves = possibleMovesForRobot();
		
		Move bestMove = new Move();
		double price = -1000, tempPrice;
		int antal = 0;
		for(Move move : Moves)
		{
			revertAllMoves();
			simulateMove(move);
			
			tempPrice =  -Negamax(numberofmovelook, -1, -10000, -price);
			revertMove();
			antal ++;
			LCD.clear();
			LCD.drawString("P:  "+ price, 0, 0);
			LCD.drawString("TP: "+ tempPrice, 0, 1);
			LCD.drawString("gang: "+ antal, 0, 2);
			LCD.refresh();
			Button.ENTER.waitForAnyPress();
			if(tempPrice > price)
			{
				price = tempPrice;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	
	public double Negamax(int depth, int turn, double alpha, double beta) throws NoKingLeft, IOException, InterruptedException 
	{
	    if (depth == 0)
	    {
	        return evaluation(turn /*, nXTF.checkersBoard.myBoard*/);
	    }
	    List<Move> moves;
	    if(turn == 1)
	    {
	    	moves = possibleMovesForRobot();
	    }
	    else
	    {
	    	moves = possibleMovesForHuman();
	    }
	    
	    for (Move move : moves) 
	    {
	        simulateMove(move);
	        double newScore = -Negamax(depth - 1, turn*-1, -beta, -alpha);
        	revertMove();
	        if (newScore >= beta) // alpha-beta cutoff
	        {
	        	return newScore;
	        }
	        else if(newScore > alpha)
	        {
	        	alpha = newScore;
	        }
	    }
	    return alpha;
	}
	
	private double evaluation(int turn)
	{
		int OPpieces = 0;
		int OWpieces = 0;
		for(Field[] F: nXTF.checkersBoard.myBoard)
		{
			for(Field Q: F)
			{
				if(!Q.isEmpty())
				{
					if(nXTF.checkersBoard.checkAllegiance(Q, false))
					{
						OWpieces ++;
					}
					else
					{
						OPpieces++;
					}
				}
			}
		}
		return OWpieces-OPpieces;
	}


	/* MI brain stops */
	/* -----------------------------------------------------------------------------------  */
	private List<Move> possibleMovesForHuman() throws InterruptedException, IOException, NoKingLeft
	{
		return possibleMoves(-1);
	}

	private List<Move> possibleMovesForRobot() throws InterruptedException, IOException, NoKingLeft
	{
		return possibleMoves(1);
	}

	public void simulateMove(Move move) throws NoKingLeft, IOException
	{
		if(move.moves.size() >= 2)
		{
			int stop = move.moves.size()-1;

			Stack<Field> tempStack = new Stack<Field>();
			for(int i = 0; i < stop; i++)
			{
				Field from = move.moves.pop();
				Field to = move.moves.peek();
				if(Math.abs(from.x - to.x) == 2)
				{
					move.takenPieces.push(nXTF.checkersBoard.myBoard[(from.x+to.x)/2][(from.y+to.y)/2].getPieceOnField());
					nXTF.checkersBoard.myBoard[(from.x+to.x)/2][(from.y+to.y)/2].setPieceOnField(null);
				}
				nXTF.checkersBoard.movePieceInRepresentation(from, to);
				tempStack.push(from);
			}
			for(int i = 0; i < stop; i++)
				move.moves.push(tempStack.pop());
			
			simulatedMoves.push(move);
		}
	}

	private void revertAllMoves() throws NoKingLeft, IOException
	{
		int stop = simulatedMoves.size();
		for(int i = 0; i < stop; i++)
		{
			revertMove();
		}
	}

	public void revertMove() throws NoKingLeft, IOException
	{
		if(simulatedMoves.size() != 0)
		{
			Move temp = simulatedMoves.pop();
			int stop = temp.moves.size()-1;
			Stack<Field> tempMoves = new Stack<Field>();
			Field tempMove = null;
			
			for(int j=0; j < stop; j++)
			{	
				tempMove = temp.moves.pop();
				nXTF.checkersBoard.movePieceInRepresentation(temp.moves.peek(), tempMove);
				tempMoves.push(tempMove);
			}
			
			stop = tempMoves.size();
			for(int j=0; j < stop; j++)
			{
				temp.moves.push(tempMoves.pop());
			}
			
			stop = temp.takenPieces.size();
			
			for(int i=0; i < stop;i++)
			{
				Piece tempPiece = temp.takenPieces.pop();
				nXTF.checkersBoard.myBoard[tempPiece.x][tempPiece.y].setPieceOnField(tempPiece);
			}
		}
	}

	private List<Move> possibleMoves(int moveForSide) throws InterruptedException, IOException, NoKingLeft //-1 = human, 1 = robot
	{
		List<Move> movements = new ArrayList<Move>();
		nXTF.checkersBoard.updateMoveables();
		for(Field[] f : nXTF.checkersBoard.myBoard)
		{
			for(Field field : f)
			{
				if(!field.isEmpty())
				{
					if(field.getPieceOnField().isMoveable && nXTF.checkersBoard.checkAllegiance(field, moveForSide == -1))
					{
						//Jumps
						List<Stack<Field>> listOfMoves = nXTF.checkersBoard.analyzeFunctions.jumpSequence(field, moveForSide == 1, field.getPieceOnField().isCrowned);

						for(Stack<Field> stackOfFields : listOfMoves)
						{
							if(stackOfFields.size() >= 2) 
							{
								movements.add(new Move(stackOfFields));
							}
						}

						if(!field.getPieceOnField().canJump)
						{
							List<Field> possibleMoves = nXTF.checkersBoard.checkMoveable(field, moveForSide);
							//Simple moves
							if(!possibleMoves.isEmpty())
							{
								for(Field posField : possibleMoves)
								{
									Move movement = new Move(field, posField);
									movements.add(movement);
								}
							}
						}

					}
				}
			}
		}
		
		return movements;
	}
}
