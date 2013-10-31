package com.OriginalFiles;

import java.io.IOException;
import java.util.*;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;

public class Board {

	public Field[][] myBoard = new Field[8][8];
	Field[] kingPlace = new Field[8];
	private int analyzeBoardRepeatNumber = 0;
	private int totalAnalyzeRuns = 0;
	private static final int analyzeRunsBeforeReset = 10;

	private Field fieldToCheck;

	char myPeasentColor, myKingColor, opponentPeasentColor, opponentKingColor;

	public Board()
	{
		try {
			findMyColors();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findOpponentColors();

		int x,y;

		//Create the 8 times 8 board
		for(x = 0; x<8; x++)
		{
			for(y=0; y<8; y++)
			{
				Field temp = new Field();
				temp.x = x;
				temp.y = y;

				//Every second field is an allowed field
				//latex start ConstructorLoop
				if((x+y)%2 == 1)
				{
					temp.allowedField = true;
					Piece pieceOnBoard = null;
					if(y < 3)
					{
						pieceOnBoard = new Piece();
						pieceOnBoard.color = myPeasentColor;

						//Every piece on the front line of each player is moveable from the start
						if(y == 2)
						{
							pieceOnBoard.isMoveable = true;
						}
					}
					//latex end

					if(y > 4)
					{
						pieceOnBoard = new Piece();
						pieceOnBoard.color = opponentPeasentColor;
						if(y==5)
						{
							pieceOnBoard.isMoveable = true;
						}
					}
					temp.setPieceOnField(pieceOnBoard);
				}
				else
				{
					temp.allowedField = false;
				}
				myBoard[x][y] = temp;
			}	
		}

		//Set the location of the human players king pieces
		//latex start ConstructorKing
		for (int i = 0; i < 8; i++)
		{
			Field temp = new Field();
			temp.x = i;
			temp.y = -2;
			Piece tempPiece = new Piece();
			tempPiece.color = opponentKingColor;
			tempPiece.isCrowned = true;
			temp.setPieceOnField(tempPiece);
			kingPlace[i] = temp;
		}
		//latex end
	}
	
	/*returns 0 if game is not ended, 1 if human won, 2 if robot win and 3 if there is a draw*/
	public int gameIsEnded(boolean IsHumanTurn)
	{
		updateMoveables();

		List<Piece> humanPieceList = new ArrayList<Piece>();
		List<Piece> robotPieceList = new ArrayList<Piece>();
		boolean robotHasMovable = false;
		boolean humanHasMovable = false;

		for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				if(!field.isEmpty()){
					if(checkAllegiance(field, true))
					{
						humanPieceList.add(field.getPieceOnField());
						if(field.getPieceOnField().isMoveable){
							humanHasMovable = true;
						}
					}
					if(checkAllegiance(field, false))
					{
						robotPieceList.add(field.getPieceOnField());
						if(field.getPieceOnField().isMoveable){
							robotHasMovable = true;
						}
					}
				}
			}
		}
		
		if(robotPieceList.size() == 0 || (!IsHumanTurn && !robotHasMovable))
		{
			return 1;
		}
		if(humanPieceList.size() == 0 || (IsHumanTurn && !humanHasMovable))
		{
			return 2;
		}
		if(robotPieceList.size() == 1 && humanPieceList.size() == 1)
		{
			if(robotPieceList.get(0).isCrowned && humanPieceList.get(0).isCrowned)
			{
				if(IsHumanTurn)
				{
					if(isOnDoubleCorners(humanPieceList.get(0)) && isNearDoubleCorners(robotPieceList.get(0)) && !hasTheMove(true))
						return 3;
				}
				else
				{
					if(isOnDoubleCorners(robotPieceList.get(0)) && isNearDoubleCorners(humanPieceList.get(0)) && !hasTheMove(false))
						return 3;
				}
			}
		}
		return 0;
	}
	
	private boolean hasTheMove(boolean humansTurn)
	{
		int rowToCheck = 0;
		
		if(!humansTurn)
		{
			rowToCheck = 1;
		}
		
		int pieceCount = 0;
		
		for(int i = rowToCheck; i < 8 ; i += 2)
		{
			for(int j = 0; j < 8; j++)
			{
				if(!myBoard[i][j].isEmpty())
					pieceCount++;
			}
		}
		
		if(pieceCount % 2 == 1)
			return true;
		return false;
	}
	
	private boolean isOnDoubleCorners(Piece piece)
	{
		if((piece.x == 0 && piece.y == 1)||(piece.x == 1 && piece.y == 0)||(piece.x == 7 && piece.y == 6)||(piece.x == 6 && piece.y == 7))
			return true;
		return false;
	}
	
	private boolean isNearDoubleCorners(Piece piece)
	{
		if((piece.x == 1 && piece.y == 2)||(piece.x == 2 && piece.y == 1)||(piece.x == 6 && piece.y == 5)||(piece.x == 5 && piece.y == 6)||(piece.x == 2 && piece.y == 3)||(piece.x == 3 && piece.y == 2)||(piece.x == 4 && piece.y == 5)||(piece.x == 5 && piece.y == 4))
			return true;
		return false;
	}

	//Method to used in sorting the list of places to move the robot
	private boolean isGreater(Field inputField, Field fieldToCompare, int compX, int compY)
	{
		//Check jumps to be crowned first
		if(inputField.getPieceOnField().canJump && !inputField.getPieceOnField().isCrowned && inputField.getPieceOnField().color == opponentPeasentColor && inputField.getPieceOnField().y == 2)
		{
			return true;
		}
		else if(inputField.getPieceOnField().canJump && !fieldToCompare.getPieceOnField().canJump)
		{
			return true;
		}
		else if(fieldToCompare.getPieceOnField().canJump && !inputField.getPieceOnField().canJump)
		{
			return false;
		}
		else
		{
			if(inputField.y == 1 && fieldToCompare.y != 1 && inputField.getPieceOnField().isCrowned != true)
			{
				return true;
			}else if (fieldToCompare.y == 1 && inputField.y != 1 && fieldToCompare.getPieceOnField().isCrowned != true)
			{
				return false;
			}
			else
			{
				if(weight(inputField,compX,compY) > weight(fieldToCompare,compX,compY))
				{
					return false;
				}
				else if(weight(inputField, compX, compY) < weight(fieldToCompare, compX, compY))
				{
					return true;
				}
				else
				{
					if(inputField.y < fieldToCompare.y)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
	}

	private int weight(Field inputField, int x, int y)
	{
		return Math.abs(inputField.x - x) + Math.abs(inputField.y - y);
	}

	//Sorts the list of fields to search to minimize robot movement
	private void sortListOfFields(List<Field> listOfFields)
	{
		int x = 0;
		int y = 0;
		for(int i = 0; i < listOfFields.size(); i++)
		{
			for(int n = i+1; n < listOfFields.size(); n++)
			{
				if(!isGreater(listOfFields.get(i), listOfFields.get(n),x,y))
				{
					Field temp1 = listOfFields.get(i);
					Field temp2 = listOfFields.get(n);
					listOfFields.remove(n);
					listOfFields.remove(i);
					listOfFields.add(i, temp2);
					listOfFields.add(n,temp1);
				}
			}
			x = listOfFields.get(i).x;
			y = listOfFields.get(i).y;
		}
	}

	private void resetVisited()
	{
		for (Field[] f : myBoard)
		{
			for (Field field : f)
			{
				field.visited = false;
			}
		}
	}
	
	public boolean checkForGameHasEnded(boolean isHumansTurn)
	{
		switch (gameIsEnded(isHumansTurn)) {
		case 0:
			return false;
		case 1:
			return true;
		case 2:
			return true;
		case 3:
			return true;
		default:
			return false;
		}
	}

	//Analyzes the current board setup
	public boolean analyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove
	{	
		totalAnalyzeRuns += 1;
		
		if(totalAnalyzeRuns > analyzeRunsBeforeReset)
		{
			totalAnalyzeRuns = 0;
		}
		
		//Find the pieces that are currently moveable
		updateMoveables();

		List<Field> moveableList = new ArrayList<Field>();

		for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				if(!field.isEmpty()){
					if(field.getPieceOnField().isMoveable && checkAllegiance(field, true))
					{
						moveableList.add(field);
					}
				}
			}
		}

		sortListOfFields(moveableList);
		boolean foundOne = false;
		boolean mustJump = false;
		if(moveableList.size() != 0)
			mustJump = moveableList.get(0).getPieceOnField().canJump;

		OUTERMOST: for(Field field : moveableList){
			if(this.isFieldEmptyOnBoard(field.x, field.y))
			{
				if(mustJump&&!field.getPieceOnField().canJump)
				{
					throw new customExceptions.IllegalMove();
				}

				if(this.trackMovement(field))
				{

					foundOne = true;
					//Break the loop
					break OUTERMOST;
				}

			}
		}

		if(foundOne == false){
			if(analyzeBoardRepeatNumber < 3)
			{
				analyzeBoardRepeatNumber ++;
				analyzeBoard();
				analyzeBoardRepeatNumber = 0;
			}
			else if(analyzeBoardRepeatNumber < 6)
			{
				analyzeBoardRepeatNumber ++;
				analyzeBoard();
				analyzeBoardRepeatNumber = 0;
			}
			else
			{
				findMissingPiece();
			}
		}

		//Find the pieces that are currently moveable
		updateMoveables();
		checkRobotPieceReplaced();
		
		return true;
	}
	
	//Function to check if user have replaced the robots peasent piece with a king piece
	private void checkRobotPieceReplaced() throws IOException
	{
		if(fieldToCheck != null)
		{
			boolean checkCondition = true;
			while(checkCondition)
			{
				if(getColor(fieldToCheck.x, fieldToCheck.y) == myKingColor)
				{
					checkCondition = false;
					fieldToCheck = null;
				}
			}
		}
	}

	private boolean verifyOpPieceIsOnField(Field field) throws InterruptedException, IOException
	{
		if(checkAllegiance(field, true))
		{
			if(field.getPieceOnField().isMoveable)
			{
				if(isFieldEmptyOnBoard(field.x, field.y))
				{
					return false;
				}else
				{
					return true;
				}
			}
		}
		return true;
	}

	private boolean peasentIsOnEndRow(Field field)
	{
		if(!field.isEmpty()){
			int checkRow;
			if(checkAllegiance(field, true))
			{
				checkRow = 0;
			}
			else
			{
				checkRow = 7;
			}

			if(field.y == checkRow && !field.getPieceOnField().isCrowned)
			{
				return true;
			}	
		}
		return false;
	}

	private void checkForUpgradeKing(Field field) throws NoKingLeft, IOException
	{
		if(peasentIsOnEndRow(field))
		{
			if(checkAllegiance(field, false))
			{
				field.getPieceOnField().color = myKingColor;
				field.getPieceOnField().isCrowned = true;
				fieldToCheck = field;
			}
			else
			{
				boolean foundOne = false;
				int i = 0;
				while(!foundOne)
				{
					if(i>7)
					{
						throw new customExceptions.NoKingLeft();
					}
					if(!kingPlace[i].isEmpty())
					{
						//Move old piece to trash
						foundOne = true;
					}
					i++;
				}
			}
		}
	}

	//Moves a piece in the board representation
	private void movePiece(Field fromField, int toField_x, int toField_y) throws NoKingLeft, IOException
	{
		//latex start movePiece
		if(checkBounds(toField_x,toField_y))
		{	
			myBoard[toField_x][toField_y].setPieceOnField(fromField.getPieceOnField());
			fromField.emptyThisField();
			checkForUpgradeKing(myBoard[toField_x][toField_y]);
		}
		else
		{
			myBoard[fromField.x][fromField.y].emptyThisField();
		}
		//latex end
	}

	public void movePiece(Field FromField, Field ToField) throws NoKingLeft, IOException
	{
		movePiece(FromField, ToField.x, ToField.y);
	}

	//Checks weather a given jump is possible
	private boolean checkSingleJump(Field field, int difX, int difY, Field originalField) throws InterruptedException, IOException, NoKingLeft 
	{
		//First check that the position is inbound
		if(checkBounds(field.x + difX, field.y + difY))
		{
			//Check the correct color
			if(checkAllegiance(myBoard[field.x + difX][field.y + difY], false))
			{
				//If there is no piece on the given field, and the field has not been visisted yet
				if(!this.fieldOccupied(field.x+2*difX, field.y+2*difY) && !myBoard[field.x+2*difX][field.y+2*difY].visited)
				{
					if(!isFieldEmptyOnBoard(field.x+2*difX, field.y+2*difY))
					{
						movePiece(originalField, field.x+2*difX, field.y+2*difY);
						//Empty jumped field and old field
						myBoard[field.x][field.y].emptyThisField();
						myBoard[field.x+difX][field.y+difY].emptyThisField();

						return true;
					}
					else
					{
						//Else check further jumps
						myBoard[field.x+2*difX][field.y+2*difY].visited = true;
						boolean returnVal = checkJumps(myBoard[field.x+2*difX][field.y+2*difY], originalField);
						if(returnVal)
						{
							myBoard[field.x][field.y].emptyThisField();
							myBoard[field.x+difX][field.y+difY].emptyThisField();
						}
						return returnVal;
					}
				}
			}
		}
		return false;
	}

	//Checks if piece has jumped
	private boolean checkJumps(Field field, Field originalField) throws InterruptedException, IOException, NoKingLeft
	{
		//latex start checkJumps
		boolean foundPiece = false;

		foundPiece = checkSingleJump(field, -1, -1, originalField);
		//latex end

		if(!foundPiece)
		{
			foundPiece = checkSingleJump(field, 1, -1, originalField);
		}

		//If the piece was a king, check also backwards directions
		if(!foundPiece && originalField.getPieceOnField().isCrowned)
		{
			foundPiece = checkSingleJump(field, 1, 1, originalField);
		}

		if(!foundPiece && originalField.getPieceOnField().isCrowned)
		{
			foundPiece = checkSingleJump(field, -1, 1, originalField);
		}

		return foundPiece;
	}

	private boolean checkIfOthersHasMove(Field field, Field FromField) throws InterruptedException, IOException
	{

		List<Field> checkArray = new ArrayList<Field>();
		if(checkBounds(field.x+1,field.y+1))
			checkArray.add(myBoard[field.x+1][field.y+1]);

		if(checkBounds(field.x+1,field.y-1))
			checkArray.add(myBoard[field.x+1][field.y-1]);

		if(checkBounds(field.x-1,field.y-1))
			checkArray.add(myBoard[field.x-1][field.y-1]);

		if(checkBounds(field.x-1,field.y+1))
			checkArray.add(myBoard[field.x-1][field.y+1]);

		boolean returnValue = true;
		for(int i = 0; i < checkArray.size(); i++)
		{
			if(!(checkArray.get(i).x == FromField.x && checkArray.get(i).y == FromField.y))
			{
				returnValue = verifyOpPieceIsOnField(checkArray.get(i));
			}
			if(!returnValue)
			{
				return returnValue;
			}
		}
		return returnValue;
	}

	//Determine simple move
	private boolean checkMove(Field field, int directY) throws InterruptedException, IOException, NoKingLeft
	{
		//Verify that the given field is inbound
		if(checkBounds(field.x,field.y))
		{
			//Check the first direction
			if(checkMoveDirection(field,1,directY))
			{
				if(checkIfOthersHasMove(myBoard[field.x+1][field.y+directY],field))
				{
					movePiece(field, field.x+1, field.y+directY);
					return true;
				}
				else
				{
					return false;
				}
			}
			//Second direction
			else if(checkMoveDirection(field,-1,directY))
			{
				if(checkIfOthersHasMove(myBoard[field.x-1][field.y+directY], field))
				{
					movePiece(field, field.x-1, field.y+directY);
					return true;
				}
				else
				{
					return false;
				}
			}
			//If king, also check backwards
			else if(field.getPieceOnField().isCrowned)
			{
				if(checkMoveDirection(field,1,-directY))
				{
					if(checkIfOthersHasMove(myBoard[field.x+1][field.y-directY], field))
					{
						movePiece(field, field.x+1, field.y-directY);
						return true;
					}
					else
					{
						return false;
					}
				}
				else if(checkMoveDirection(field,-1,-directY))
				{
					if(checkIfOthersHasMove(myBoard[field.x-1][field.y-directY], field))
					{
						movePiece(field, field.x-1, field.y-directY);
						return true;
					}
					else
					{
						return false;
					}
				}
			}
		}
		return false;
	}

	//Check if field has been occupied
	private boolean checkMoveDirection(Field field, int directX, int directY) throws InterruptedException, IOException
	{
		if(!fieldOccupied(field.x+directX,field.y+directY) && !this.isFieldEmptyOnBoard(field.x+directX, field.y+directY))
			return true;
		else
			return false;
	}

	//Try to find the piece which has been moved
	private boolean trackMovement(Field field) throws IllegalMove, InterruptedException, IOException, NoKingLeft
	{
		boolean jumpable = field.getPieceOnField().canJump;
		boolean jumpFound = checkJumps(field, field);
		this.resetVisited();
		boolean moveFound = false;

		if(!jumpFound && jumpable)
		{
			throw new customExceptions.IllegalMove();
		}
		
		if(!jumpFound)
		{
			if(checkMove(field,-1))
			{
				moveFound = true;
			}
		}
		
		return moveFound;
	}

	private boolean isFieldEmptyOnBoard(int x, int y) throws InterruptedException, IOException
	{	
		if(checkBounds(x,y)){
			char color = getColor(x, y);

			if(color == ' ')
			{
				return true;
			}
			else
			{
				return false;
			}

		}
		else
			return false;
	}

	public boolean fieldOccupied(int x, int y)
	{
		if(checkBounds(x,y))
		{
			return !myBoard[x][y].isEmpty();
		}
		else
		{
			return true;
		}
	}

	//Check if a piece can move and is jumpable
	//latex start checkPiece
	private void checkPiece(Field field, int dify, boolean checkForOpponent)
	{
		field.getPieceOnField().canJump = checkJump(field, dify, checkForOpponent);

		if(field.getPieceOnField().canJump)
		{
			field.getPieceOnField().isMoveable = true;
		}
		else
		{
			field.getPieceOnField().isMoveable = checkMoveableBoolean(field, dify);	
		}
	}
	//latex end
	
	private boolean checkMoveableBoolean(Field field, int dif)
	{
		if(checkMoveable(field, dif).isEmpty())
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	//Checks if a given field is moveable
	public List<Field> checkMoveable(Field field, int dif)
	{
		List<Field> possibleMoves = new ArrayList<Field>();
		
		//Check forward
		if((!this.fieldOccupied(field.x-1, field.y+dif)) && !this.fieldOccupied(field.x+1, field.y+dif))
		{
			possibleMoves.add(myBoard[field.x-1][field.y+dif]);
			possibleMoves.add(myBoard[field.x+1][field.y+dif]);
			return possibleMoves;
		}
		else if(!this.fieldOccupied(field.x-1, field.y+dif))
		{
			possibleMoves.add(myBoard[field.x-1][field.y+dif]);
			return possibleMoves;
		}
		else if(!this.fieldOccupied(field.x+1, field.y+dif))
		{
			possibleMoves.add(myBoard[field.x+1][field.y+dif]);
			return possibleMoves;
		}
		//if king, check backwards also
		else if(field.getPieceOnField().isCrowned)
		{
			if(!this.fieldOccupied(field.x - 1, field.y - dif) &&  !this.fieldOccupied(field.x + 1, field.y - dif))
			{
				possibleMoves.add(myBoard[field.x-1][field.y-dif]);
				possibleMoves.add(myBoard[field.x+1][field.y-dif]);
				return possibleMoves;
			}
			else if(!this.fieldOccupied(field.x - 1, field.y - dif))
			{
				possibleMoves.add(myBoard[field.x-1][field.y-dif]);
				return possibleMoves;
			}
			else if(!this.fieldOccupied(field.x + 1, field.y - dif))
			{
				possibleMoves.add(myBoard[field.x+1][field.y-dif]);
				return possibleMoves;
			}
			else
			{
				return possibleMoves;
			}
		}
		else
			return possibleMoves;
	}

	//Check if a given field is can jump
	private boolean checkJump(Field field, int dif, boolean checkForOpponent)
	{		
		//Check forwards
		if(checkJumpDirectionBoolean(field, 1, dif, checkForOpponent) ||checkJumpDirectionBoolean(field, -1, dif, checkForOpponent))
		{
			return true;
		}		
		//if king, check backwards
		else if(field.getPieceOnField().isCrowned)
		{ 
			if(checkJumpDirectionBoolean(field,1, -dif, checkForOpponent || checkJumpDirectionBoolean(field, -1, -dif, checkForOpponent)))
			{
				return true;
			}
		}
		return false;
	}

	//Checks jumps
	public Field checkJumpDirection(Field field, int difx, int dify, boolean checkForOpponent)
	{
		//Forward
		if(checkBounds(field.x+difx,field.y+dify))
		{ 
			if(checkAllegiance(myBoard[field.x+difx][field.y+dify], checkForOpponent) && !this.fieldOccupied(field.x+2*difx, field.y+2*dify))
			{
				return myBoard[field.x+2*difx][field.y+2*dify];
			}
		}
		//if king, also check backwards
		else if(field.getPieceOnField().isCrowned)
		{ 
			if(checkBounds(field.x+difx,field.y-dify))
			{
				if(checkAllegiance(myBoard[field.x+difx][field.y-dify], checkForOpponent) && !this.fieldOccupied(field.x+2*difx, field.y-2*dify))
				{
					return myBoard[field.x+2*difx][field.y-2*dify];
				}
			}
		}
		return null;
	}
	
	private boolean checkJumpDirectionBoolean(Field field, int difx, int dify, boolean checkForOpponent)
	{
		if(checkJumpDirection(field, difx, dify, checkForOpponent) == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	//Updates the moveable property on each piece
	private void updateMoveables()
	{
		for(Field[] f : myBoard)
		{
			for(Field field : f)
			{
				if(field.allowedField)
				{
					//Check moveables for robot
					if(field.getPieceOnField() != null){
						if(checkAllegiance(field,false))
						{
							checkPiece(field, 1, true);
						}			
						//Check moveable for human
						else if(checkAllegiance(field, true))
						{
							checkPiece(field, -1, false);
						}
					}				
				}
			}
		}
	}
	
	//Remember to never update this specific method
	private void findMyColors() throws InterruptedException, IOException
	{
		myPeasentColor = 'r';
		if(myPeasentColor == 'r')
		{
			myKingColor = 'b';
		}
		else if(myPeasentColor == 'w')
		{
			myKingColor = 'g';
		}
	}

	//Sets the colors of the human players pieces
	private void findOpponentColors()
	{
		if(myPeasentColor == 'r')
		{
			opponentPeasentColor = 'w';
			opponentKingColor = 'g';
		}
		else
		{
			opponentPeasentColor = 'r';
			opponentKingColor = 'b';
		}
	}

	//Remember to never update this specific method
	public char getColor(int x, int y)
	{
		if(x == 2 && y == 5){
			return ' ';
		}
		if(x == 1 && y == 4){
			return 'w';
		}
		if(x == 4 && y == 3){
			return ' ';
		}
		
		if(myBoard[x][y].getPieceOnField() != null){
			return myBoard[x][y].getPieceOnField().color;
		}
		else{
			return ' ';
		}
	}


	//if a piece is missing, scan whole board
		private void findMissingPiece() throws InterruptedException, IOException
		{
			int i,j;
			boolean changer = true;

			for(i=0;i<8;i++)
			{	
				//Change direction of the robot, to minimize robot movement
				if(changer)
				{
					for(j=0;j<8;j++)
					{
						if((i+j)%2 == 1)
						{
							myBoard[i][j].setPieceOnField(getPiece(i, j));
						}
					}
					changer = false;
				}
				else
				{
					for(j=7;j>=0;j--)
					{
						if((i+j)%2 == 1)
						{
							myBoard[i][j].setPieceOnField(getPiece(i, j));
						}
					}
					changer = true;
				}
			}

			this.updateMoveables();
		}

		//Get piece on given position
		private Piece getPiece(int x, int y) throws IOException{
			char color = getColor(x, y);
			if(color == ' ')
			{
				return null;
			}
			else
			{
				Piece temp = new Piece();
				temp.color = color;
				temp.isCrowned = (color == 'g' || color == 'b');
				return temp;
			}
		}

		//Check if a given piece is the robots or the opponents
		public boolean checkAllegiance(Field input, boolean checkForOpponent)
		{
			if(checkBounds(input.x, input.y))
			{
				if((input.isPieceOfColor(myPeasentColor) || input.isPieceOfColor(myKingColor)) && !checkForOpponent)
				{
					return true;
				}
				if((input.isPieceOfColor(opponentPeasentColor) || input.isPieceOfColor(opponentKingColor)) && checkForOpponent)
				{
					return true;
				}
			}
			return false;
		}

		//Return true if the given position is inbounds
		private boolean checkBounds(int x, int y)
		{
			if(x >= 0 && x <= 7 && y >= 0 && y <= 7)
				return true;
			else
				return false;
		}
	}
