import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

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
	private int numberofmovelook			= 1;
	/*points*/
	private int ownMovePoint				= 4;
	private int ownJumpPoint				= 8;

	private int opponentMovePoint			= -ownMovePoint;
	private int opponentJumpPoint			= -ownJumpPoint;

	/* bonus point for doing specific moves */
	private int ownMiddleMoveBonus 			= 3;
	private int opponentMiddleMoveBonus 	= 1; /* tror ikke kan bruges*/

	private int ownMoveLastRowPenalty 		= 1;
	private int opponentMoveLastRowPenalty 	= 2;

	/* how glad the MI/AI are for the result of the game */
	private int gameIsWon = 10;
	private int gameIsLost = -gameIsWon;
	private int gameIsDraw = 5;


	public Move lookForBestMove() throws NoKingLeft, IOException /* does not start to see if the game is ended*/, InterruptedException
	{


		List<Move> Moves = possibleMovesForRobot();
		LCD.clear();
		LCD.drawString("TEST yes", 0, 0);
		LCD.refresh();
		Delay.msDelay(5000);

		Move bestMove = null;

		double price = Double.MIN_VALUE, tempPrice;

		for(Move move : Moves)
		{
			LCD.clear();
			LCD.drawString("TEST f�r opp : " + Moves.indexOf(move), 0, 0);
			LCD.refresh();
			Delay.msDelay(2000);;
			tempPrice =  opponentTurn(move, 1);

			LCD.clear();
			LCD.drawString("TEST efter opp: " + Moves.indexOf(move), 0, 0);
			LCD.refresh();
			Delay.msDelay(2000);;

			if(price < tempPrice)
			{
				price = tempPrice;
				bestMove = move;
			}
		}
		return bestMove;
	}

	private double ownTurn(Move move, int moveLook) throws NoKingLeft, IOException, InterruptedException
	{
		int numberOfMoves = 0;
		double sum = 0;
		double price = findOwnPrice(move);
		simulateMove(move);

		int result = nXTF.checkersBoard.gameIsEnded(true);
		if( result != 0 && numberofmovelook >= moveLook)
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
			List<Move> Moves = possibleMovesForRobot();
			for(Move tempMove : Moves)
			{
				sum = sum + opponentTurn(tempMove, moveLook+1);
				numberOfMoves++;
			}
			sum = sum/numberOfMoves;
			moveLook ++;
		}
		revertMove();
		return price + sum;
	}

	private double opponentTurn(Move move, int moveLook) throws NoKingLeft, IOException, InterruptedException
	{
		
		int numberOfMoves = 0;
		double sum = 0;
		double price = findOpponentPrice(move);
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
			List<Move> Moves = possibleMovesForHuman();
			for(Move tempMove : Moves)
			{
				sum = sum + ownTurn(tempMove, moveLook+1);
				numberOfMoves++;
			}
			sum = sum/numberOfMoves;
			moveLook ++;
		}
		revertMove();
		return price + sum;
	}


	private double findOpponentPrice(Move move)
	{
		double price = 0;

		if(move.isJump == true)
		{
			price = price + opponentJumpPoint * move.moves.size();
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
			price = price + ownJumpPoint * move.moves.size();
		}
		else
		{
			price = price + ownMovePoint;
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

	private void simulateMove(Move move) throws NoKingLeft, IOException
	{
		if(move.isJump)
		{
			int stop = move.moves.size()-1;

			Stack<Field> tempStack = new Stack<Field>();
			for(int i = 0; i < stop; i++)
			{
				Field from = move.moves.pop();
				Field to = move.moves.peek();
				move.takenPieces.push(nXTF.checkersBoard.myBoard[(from.x+to.x)/2][(from.y+to.y)/2].getPieceOnField());
				nXTF.checkersBoard.movePiece(from, to);
				tempStack.push(from);
			}
			for(int i = 0; i < stop; i++)
				move.moves.push(tempStack.pop());
		}
		else if(!move.moves.isEmpty())
		{
			Field temp = move.moves.pop();
			nXTF.checkersBoard.movePiece(temp, move.moves.peek());
			move.moves.push(temp);

		}
		simulatedMoves.push(move);
	}

	private void revertAllMoves() throws NoKingLeft, IOException
	{
		int stop = simulatedMoves.size();
		for(int i = 0; i < stop; i++)
		{
			revertMove();
		}
	}

	private void revertMove() throws NoKingLeft, IOException
	{
		if(simulatedMoves.size() != 0)
		{
			Move temp = simulatedMoves.pop();
			int stop = temp.moves.size();
			for(int i=0; i < stop;i++)
				nXTF.checkersBoard.movePiece(temp.moves.pop(), temp.moves.peek());
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
					if(field.getPieceOnField().isMoveable)
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

		return movements;
	}
}
