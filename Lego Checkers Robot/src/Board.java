import java.io.IOException;
import java.util.*;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.Color;

public class Board {

	Field[][] myBoard = new Field[8][8];
	Field[] kingPlace = new Field[8];
	private int analyzeBoardRepeatNumber = 0;
	private int totalAnalyzeRuns = 0;
	private static final int analyzeRunsBeforeReset = 10;

	private Field fieldToCheck;
	communication informer = new communication();

	char myPeasentColor, myKingColor, opponentPeasentColor, opponentKingColor;
	RemoteNXTFunctions remoteFunctions;

	public Board(RemoteNXTFunctions remoteFunc) throws InterruptedException, IOException
	{
		remoteFunctions = remoteFunc;

		findMyColors();
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

	/*returns 0 if game has not ended, 1 if the human won, 2 if the robot won and 3 if it was a draw*/
	public int gameHasEnded(boolean humanTurn)
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

		if(robotPieceList.size() == 0 || (!humanTurn && !robotHasMovable))
		{
			return 1;
		}
		if(humanPieceList.size() == 0 || (humanTurn && !humanHasMovable))
		{
			return 2;
		}
		if(robotPieceList.size() == 1 && humanPieceList.size() == 1)
		{
			if(robotPieceList.get(0).isCrowned && humanPieceList.get(0).isCrowned)
			{
				if(humanTurn)
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
		switch (gameHasEnded(isHumansTurn)) {
		case 0:
			return false;
		case 1:
			informer.humanWon();
			return true;
		case 2:
			informer.robotWon();
			return true;
		case 3:
			informer.draw();
			return true;
		default:
			return false;
		}
	}

	//Analyzes the current board setup
	public boolean analyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove
	{	
		if(!checkForGameHasEnded(true))
		{
			totalAnalyzeRuns += 1;

			if(totalAnalyzeRuns > analyzeRunsBeforeReset)
			{
				totalAnalyzeRuns = 0;
				remoteFunctions.resetMotors();
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
					remoteFunctions.resetMotors();
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
			if(!checkForGameHasEnded(false)){
				return true;
			}	
		}
		return false;
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
				else
				{
					informer.myKingNotPlaced();
					remoteFunctions.waitForRedButton();
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
						remoteFunctions.trashPieceOnField(field);
						//Insert king at location
						remoteFunctions.doMove(new Move(kingPlace[i], field));
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
	
	public List<Stack<Field>> jumpSequence(Field input, boolean checkForOpponent, boolean isCrowned) throws  InterruptedException, IOException, NoKingLeft 

	{
		List<Stack<Field>> returnList = new ArrayList<Stack<Field>>();
		Field tempField = checkJumpDirection(input, -1, 1, checkForOpponent, isCrowned);
		if(tempField != null)
		{
			returnList.addAll(jumpSequence(tempField, checkForOpponent, isCrowned));
		}
		tempField = checkJumpDirection(input, -1, -1, checkForOpponent, isCrowned);
		if(tempField != null)
		{
			returnList.addAll(jumpSequence(tempField, checkForOpponent, isCrowned));
		}
		tempField = checkJumpDirection(input, 1, -1, checkForOpponent, isCrowned);
		if(tempField != null)
		{
			returnList.addAll(jumpSequence(tempField, checkForOpponent, isCrowned));
		}
		tempField = checkJumpDirection(input, 1, 1, checkForOpponent, isCrowned);
		if(tempField != null)
		{
			returnList.addAll(jumpSequence(tempField, checkForOpponent, isCrowned));
		}

		if(returnList.size() == 0)
		{
			returnList.add(new Stack<Field>());
		}

		for(int i = 0; i < returnList.size();i++)
		{
			returnList.get(i).push(input);
		}

		input.visited = true;
		return returnList;
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
		boolean pieceFound = false;
		if(field.getPieceOnField().canJump)
		{	
			if(findJumpPiece(field))
			{
				pieceFound = true;
			}
			if(!pieceFound)
			{
				throw new customExceptions.IllegalMove();
			}
		}
		else if(checkMove(field,-1))
		{
			pieceFound = true;
		}

		return pieceFound;
	}

	private boolean findJumpPiece(Field field) throws InterruptedException, IOException, NoKingLeft
	{
		List<Stack<Field>> jumpList = new ArrayList<Stack<Field>>();
		jumpList = jumpSequence(field, false, field.getPieceOnField().isCrowned);
		resetVisited();
		for(int i=0; i<jumpList.size();i++)
		{
			Stack<Field> tempList = new Stack<Field>();
			int stop = jumpList.get(i).size();
			for(int j=0; j<stop;j++)
			{
				tempList.push(jumpList.get(i).pop()); 
			}
			Field desField = tempList.peek();
			if(!isFieldEmptyOnBoard(desField.x, desField.y))
			{
				movePiece(field, desField);
				int stopj = tempList.size()-1;
				for(int j=0;j<stopj;j++)
				{
					Field tempfield = tempList.pop();
					Field tempfield2 = tempList.peek();
					Field takenField = myBoard[(tempfield.x + tempfield2.x)/2][(tempfield.y + tempfield2.y)/2];
					movePiece(takenField,remoteFunctions.trashField);
				}
				return true;
			}
		}
		return false;
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
		field.getPieceOnField().canJump = checkJump(field, checkForOpponent, field.getPieceOnField().isCrowned);

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

	//Checks if a given field is movable
	public List<Field> checkMoveable(Field field, int dif)
	{
		List<Field> possibleMoves = new ArrayList<Field>();

		//Check forward
		if(!this.fieldOccupied(field.x-1, field.y+dif))
		{
			possibleMoves.add(myBoard[field.x-1][field.y+dif]);
		}
		if(!this.fieldOccupied(field.x+1, field.y+dif))
		{
			possibleMoves.add(myBoard[field.x+1][field.y+dif]);
		}
		//if king, check backwards also
		if(field.getPieceOnField().isCrowned)
		{
			if(!this.fieldOccupied(field.x - 1, field.y - dif))
			{
				possibleMoves.add(myBoard[field.x-1][field.y-dif]);
			}
			if(!this.fieldOccupied(field.x + 1, field.y - dif))
			{
				possibleMoves.add(myBoard[field.x+1][field.y-dif]);
			}
		}
		return possibleMoves;
	}

	//Check if a given field can jump
	//latex start checkJump
	private boolean checkJump(Field field, boolean checkForOpponent, boolean isCrowned)
	{		
		if(checkJumpDirectionBoolean(field, -1, -1, checkForOpponent, isCrowned)||checkJumpDirectionBoolean(field, 1, -1, checkForOpponent, isCrowned)
				||checkJumpDirectionBoolean(field, 1, 1, checkForOpponent, isCrowned) ||checkJumpDirectionBoolean(field, -1, 1, checkForOpponent, isCrowned))
		{
			return true;
		}		
		return false;
	}
	//latex end

	//Checks jumps
	//latex start jumpDirection
	public Field checkJumpDirection(Field field, int difx, int dify, boolean checkForOpponent, boolean isCrowned)
	{
		if(((checkForOpponent && dify == -1)||(!checkForOpponent && dify == 1)) && !isCrowned)
			return null;

		if(checkBounds(field.x+2*difx,field.y+2*dify))
		{
			if(!myBoard[field.x+2*difx][field.y+2*dify].visited)
			{
				if(checkAllegiance(myBoard[field.x+difx][field.y+dify], checkForOpponent) && !this.fieldOccupied(field.x+2*difx, field.y+2*dify))
				{
					return myBoard[field.x+2*difx][field.y+2*dify];
				}
			}
		}
		return null;
	}
	//latex end

	private boolean checkJumpDirectionBoolean(Field field, int difx, int dify, boolean checkForOpponent, boolean isCrowned)
	{
		if(checkJumpDirection(field, difx, dify, checkForOpponent, isCrowned) == null)
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
		resetVisited();
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
		resetVisited();
	}

	private void printValues(Field field){
		boolean moveable = field.getPieceOnField().isMoveable;
		boolean canJump = field.getPieceOnField().canJump;
		int x = field.getPieceOnField().x;
		int y = field.getPieceOnField().y;

		LCD.drawInt(x, 0, 0);
		LCD.drawInt(y, 2, 0);
		if(moveable){
			LCD.drawString("moveable = true", 0, 1);
		}
		else{
			LCD.drawString("moveable = false", 0, 1);
		}
		if(canJump){
			LCD.drawString("canJump = true", 0, 2);
		}
		else{
			LCD.drawString("canJump = false", 0, 2);
		}
		LCD.refresh();
		Button.ENTER.waitForPress();
	}

	//Sets the colors of the pieces of the robot
	private void findMyColors() throws InterruptedException, IOException
	{
		myPeasentColor = getColor(0,1);
		if(myPeasentColor == 'r')
		{
			myKingColor = 'b';
		}
		else if(myPeasentColor == 'w')
		{
			myKingColor = 'g';
		}else{
			remoteFunctions.resetMotors();
			remoteFunctions.initColorSensor();
			findMyColors();
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

	//Returns the color on the given position
	public char getColor(int x, int y) throws IOException
	{
		Color colorResult = remoteFunctions.getColorOnField(x, y);

		LCD.clear();
		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();
		LCD.drawInt(red, 0, 1);
		LCD.drawInt(green, 0, 2);
		LCD.drawInt(blue, 0, 3);
		if(red > 160  && green < 140 && green > 30 && blue < 140 && blue > 50)
		{
			LCD.drawChar('r', 0, 0);LCD.refresh();
			return 'r';
		}
		else if(red > 205 && green > 190 && blue > 180)
		{
			LCD.drawChar('w', 0, 0);LCD.refresh();
			return 'w';
		}
		else if(red > 140 && red < 205 && green > 170  && blue < 200 && blue > 150)
		{
			LCD.drawChar('g', 0, 0);LCD.refresh();
			return 'g';
		}
		else if(red < 160 && red > 100 && green < 220 && green > 150 && blue > 170)
		{
			LCD.drawChar('b', 0, 0);LCD.refresh();
			return 'b';
		}
		else
		{
			LCD.drawChar('e', 0, 0);LCD.refresh();
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
