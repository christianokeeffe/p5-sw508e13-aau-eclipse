import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.LCD;
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
		double price = Double.MIN_VALUE, tempPrice;

		for(Move move : Moves)
		{	
			tempPrice =  movePrice(move, 10, -1);
			
			if(price < tempPrice && move.moves.size() > 0)
			{
				price = tempPrice;
				bestMove = move;
			}
		}
		
		return bestMove;
	}
	
	private double movePrice(Move move, int moveLook, int  robotMove) throws NoKingLeft, IOException //robotMove = +1 for robot, -1 for human
, InterruptedException
	{
		
		int numberOfMoves = 0;
		double temp = 0;
		double bestMoveprice = Double.MIN_VALUE;
		
		double price = findPrice(move, robotMove);

		simulateMove(move);

		int result = nXTF.checkersBoard.gameIsEnded(false);
		if( result > 0 && numberofmovelook >= moveLook)
		{
			if(result == 1)
				price = gameIsLost;
			if(result == 2)
				price = gameIsWon;
			if(result == 3)
				price = gameIsDraw;
		}
		else if(numberofmovelook >= moveLook)
		{
			List<Move> moves;
			
			if(robotMove == 1)
			{
				moves = possibleMovesForHuman();
			}
			else
			{
				moves = possibleMovesForRobot();
			}
			
			
			for(Move tempMove : moves)
			{
				temp =  movePrice(tempMove, moveLook+1, robotMove*-1);
				if(temp > bestMoveprice)
				{
					bestMoveprice = temp;
				}
			}
		}
		
		revertMove();
		/*
		LCD.clear();
		LCD.drawString("TEST", 0, 0);
		LCD.refresh();
		Delay.msDelay(3000); */
		
		return price + bestMoveprice;
	}

	
	private double findPrice(Move move, int robotTurn) //robotTurn = +1 for robot, -1 for human
	{
		double price = 0;

		if(move.isJump == true)
		{
			price = price + (JumpPoint*robotTurn) * move.moves.size();
		}
		else
		{
			price = price + MovePoint*robotTurn;
		}
		return price;
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
		if(!move.moves.isEmpty())
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
				nXTF.checkersBoard.movePiece(from, to);
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
			
			for(int i=0; i < stop;i++)
			{
				Field tempmove = temp.moves.pop();
				nXTF.checkersBoard.movePiece(temp.moves.peek(),tempmove);
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
		for(Field[] f : nXTF.checkersBoard.myBoard)
		{
			for(Field field : f)
			{
				if(!field.isEmpty())
				{
					if(field.getPieceOnField().isMoveable && nXTF.checkersBoard.checkAllegiance(field, moveForSide == -1))
					{
						//Jumps
						List<Stack<Field>> listOfMoves = nXTF.checkersBoard.jumpSequence(field, moveForSide == 1, field.getPieceOnField().isCrowned);

						for(Stack<Field> stackOfFields : listOfMoves)
						{
							movements.add(new Move(stackOfFields, true));
						}

						if(!field.getPieceOnField().canJump)
						{
							List<Field> possibleMoves = nXTF.checkersBoard.checkMoveable(field, moveForSide);
							//Simple moves
							
							if(!possibleMoves.isEmpty())
							{
								for(Field posField : possibleMoves)
								{
				
									Move movement = new Move(field, posField, false);
									movements.add(movement);
								}
							}
						}

					}
				}
			}
		}

		for(Move m : movements)
		{
			LCD.clear();
			int count =0;
			int zerocount = 0;
			int onecount = 0;
			if(m.moves.size() >= 2)
			{
				LCD.drawString("count: " + count, 0, 0);
				count++;
				LCD.drawString("Number of moves: " + m.moves.size(), 0, 1);
				LCD.drawString("From: " + m.moves.peek().x + ", " + m.moves.pop().y, 0, 2);
				LCD.drawString("To: " + m.moves.peek().x + ", " + m.moves.pop().y, 0, 3);
				
				LCD.refresh();
				Button.ENTER.waitForAnyPress();
			}
			/*
			else if(m.moves.size() == 1)
			{
				LCD.clear();
				LCD.drawString("found one: size: " + onecount, 0, 4);
				LCD.refresh();
				Button.ENTER.waitForAnyPress();
				onecount++;
			}
			else if(m.moves.size() == 0)
			{
				LCD.clear();
				LCD.drawString("found zero: size: " + zerocount, 0, 5);
				LCD.refresh();
				Button.ENTER.waitForAnyPress();
				zerocount++;
			}
			*/
		}
		
		return movements;
	}
}
