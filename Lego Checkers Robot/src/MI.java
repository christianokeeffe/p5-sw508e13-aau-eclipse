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
	public double inf = 100000;

	private Stack<Move> simulatedMoves = new Stack<Move>();
	MI(RemoteNXTFunctions NXT)
	{
		nXTF = NXT;
	}

	/* -----------------------------------------------------------------------------------  *
	/* MI brain starts */

	///Test method
	public void scanPieces(int side) throws IOException
	{
		for(Field[] F: nXTF.checkersBoard.myBoard)
		{
			for(Field Q: F)
			{
				if(!Q.isEmpty())
				{
					if(side == 1)
					{
						if(nXTF.checkersBoard.checkAllegiance(Q, false))
						{
							nXTF.getColorOnField(Q.x, Q.y);
						}
					}
					else
					{
						if(nXTF.checkersBoard.checkAllegiance(Q, true))
						{
							nXTF.getColorOnField(Q.x, Q.y);
						}
					}
				}
			}
		}
	}
	
	
	public Move lookForBestMove() throws NoKingLeft, IOException /* does not start to see if the game is ended*/, InterruptedException
	{
		List<Move> Moves = possibleMovesForRobot();
		
		Move bestMove = new Move();
		double price = -inf, tempPrice;
		int antal = 0;
		for(Move move : Moves)
		{
			revertAllMoves();
			simulateMove(move);
			
			tempPrice =  -Negamax(numberofmovelook, -1, -inf, -price);
			revertMove();
			antal ++;
			/*LCD.clear();
			LCD.drawString("P:  "+ price, 0, 0);
			LCD.drawString("TP: "+ tempPrice, 0, 1);
			LCD.drawString("From X: " + move.moves.peek().x, 0, 3);
			LCD.drawString("From Y: " + move.moves.peek().y, 0, 4);
			LCD.drawString("gang: "+ antal, 0, 2);
			LCD.refresh();
			Button.ENTER.waitForAnyPress();*/
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
	        return turn*evaluation(turn);
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
	    
	    double bestValue = -inf;
	    
	    OUTERMOST:for (Move move : moves) 
	    {
	        simulateMove(move);
	        double newScore = -Negamax(depth - 1, -turn, -beta, -alpha);
        	revertMove();
        	
        	bestValue = max(bestValue, newScore);
        	alpha = max(alpha, newScore);
        	if(alpha >= beta)
        	{
        		break OUTERMOST;
        	}
	    }
	    return bestValue;
	}
	/* how much the AI/MI looks forward */
	private int numberofmovelook		= 2;

	/* how glad the MI/AI are for the result of the game */
	private double gameIsWon = inf;
	private int gameIsDraw = 20;
	
	private final int valueOfPiece = 10;
	private final int middleBonus = 3;
	private final int backlineBonus = 5;
	private final int pieceDifferenceFactor = 4;
	private final int kingBonus = 8;
	
	private double evaluation(int turn)
	{
		int OPpieces = 0;
		int OWpieces = 0;
		double valueOfBoard = 0;
		nXTF.checkersBoard.updateMoveables();
		for(Field[] F: nXTF.checkersBoard.myBoard)
		{
			for(Field Q: F)
			{
				if(!Q.isEmpty())
				{
					if(nXTF.checkersBoard.checkAllegiance(Q, false))
					{
						OWpieces ++;
						
						valueOfBoard += priceForField(Q);
					}
					else
					{
						OPpieces++;
						valueOfBoard -= priceForField(Q);
					}
				}
			}
		}
		boolean isHuman = (turn ==-1);

		switch(nXTF.checkersBoard.analyzeFunctions.gameHasEnded(isHuman))
		{
		case 0:
			break;
		case 1:
			valueOfBoard -= gameIsWon;
			break;
		case 2:
			valueOfBoard += gameIsWon;
			break;
		case 3:
			if(OWpieces-OPpieces > 0)
				valueOfBoard -= gameIsDraw;
			else if(OWpieces-OPpieces < 0)
				valueOfBoard += gameIsDraw;
			break;
		}

		valueOfBoard +=  pieceDifferenceFactor*((OWpieces/OPpieces)-1);
		
		return valueOfBoard;
	}
	
	private double priceForField(Field field)
	{
		int returnValue = 0;
		
		returnValue += valueOfPiece +middleBonus - min(Math.abs(3-field.x), Math.abs(4-field.x));
		if(!field.getPieceOnField().isCrowned && ((nXTF.checkersBoard.checkAllegiance(field, true) && field.y == 7) || (nXTF.checkersBoard.checkAllegiance(field, false) && field.y == 0)))
			returnValue += backlineBonus;
		if(field.getPieceOnField().isCrowned)
			returnValue+= kingBonus;
		return returnValue;
	}
	
	private int min(int x, int y)
	{
		if(x < y)
			return x;
		return y;
	}
	
	private double max(double x, double y)
	{
		if(x < y)
			return y;
		return x;
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
				nXTF.checkersBoard.movePieceInRepresentation(from, to, true);
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
				nXTF.checkersBoard.movePieceInRepresentation(temp.moves.peek(), tempMove, true);
				if(tempMove.getPieceOnField().isCrowned && !temp.wasKingBefore)
				{
					if(nXTF.checkersBoard.checkAllegiance(tempMove, true))
					{
						tempMove.getPieceOnField().color = nXTF.checkersBoard.opponentPeasentColor;
					}
					else if(nXTF.checkersBoard.checkAllegiance(tempMove, false))
					{
						tempMove.getPieceOnField().color = nXTF.checkersBoard.myPeasentColor;
					}
					if(!tempMove.isEmpty())
						tempMove.getPieceOnField().isCrowned = false;
				}
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
								movements.add(new Move(stackOfFields, field.getPieceOnField().isCrowned));
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
									Move movement = new Move(field, posField, field.getPieceOnField().isCrowned);
									movements.add(movement);
								}
							}
						}
					}
				}
			}
		}
		


		nXTF.checkersBoard.sortListOfMoves(movements);;
		boolean mustJump = false;
		if(movements.size() != 0)
		{
			mustJump = movements.get(0).isJump();
			
			for(int i = 0 ; movements.size() > i ; i++ )
			{
				if(mustJump && !movements.get(i).isJump())
				{
					movements.remove(i);
					i--;
				}
			}
		
		}	
		return movements;
	}
}
