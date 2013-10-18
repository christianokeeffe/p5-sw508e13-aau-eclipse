import java.io.IOException;
import java.util.*;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.Color;

public class Board {

	Field[][] myBoard = new Field[8][8];
	Field[] kingPlace = new Field[8];
	int analyzeBoardRepeatNumber = 0;

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

	//Method to used in sorting the list of places to move the robot
	private boolean isGreater(Field inputField, Field fieldToCompare, int compX, int compY)
	{
		if(inputField.getPieceOnField().canJump && !fieldToCompare.getPieceOnField().canJump)
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

	//Analyzes the current board setup
	public boolean analyzeBoard() throws Exception
	{	
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

		OUTERMOST: for(Field field : moveableList){
			if(this.isFieldEmptyOnBoard(field.x, field.y))
			{
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
			}else{
				findMissingPiece();
			}
		}

		//Find the pieces that are currently moveable
		updateMoveables();
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

	private void checkForUpgradeKing(Field field) throws Exception
	{
		if(peasentIsOnEndRow(field))
		{
			if(checkAllegiance(field, false))
			{
				field.getPieceOnField().color = myKingColor;
				field.getPieceOnField().isCrowned = true;
			}
			else
			{
				boolean foundOne = false;
				int i = 0;
				while(!foundOne)
				{
					if(i>7)
					{
						throw new Exception();
					}
					if(!kingPlace[i].isEmpty())
					{
						//Move old piece to trash
						remoteFunctions.movePiece(field, remoteFunctions.trashField);
						//Insert king at location
						remoteFunctions.movePiece(kingPlace[i], field);
						foundOne = true;
					}
					i++;
				}
			}
		}
	}

	//Moves a piece in the board representation
	private void movePiece(Field fromField, int toField_x, int toField_y) throws Exception
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

	public void movePiece(Field FromField, Field ToField) throws Exception
	{
		movePiece(FromField, ToField.x, ToField.y);
	}

	//Checks weather a given jump is possible
	private boolean checkSingleJump(Field field, int difX, int difY, Field originalField) throws Exception
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
	private boolean checkJumps(Field field, Field originalField) throws Exception
	{
		boolean foundPiece = false;

		foundPiece = checkSingleJump(field, -1, -1, originalField);

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

	//Determine simple move
	private boolean checkMove(Field field, int directY) throws Exception
	{
		//Verify that the given field is inbound
		if(checkBounds(field.x,field.y))
		{
			//Check the first direction
			if(checkMoveDirection(field,1,directY))
			{
				movePiece(field, field.x+1, field.y+directY);
				return true;
			}
			//Second direction
			else if(checkMoveDirection(field,-1,directY))
			{
				movePiece(field, field.x-1, field.y+directY);
				return true;
			}
			//If king, also check backwards
			else if(field.getPieceOnField().isCrowned)
			{
				if(checkMoveDirection(field,1,-directY))
				{
					movePiece(field, field.x+1, field.y-directY);
					return true;
				}
				else if(checkMoveDirection(field,-1,-directY))
				{
					movePiece(field, field.x-1, field.y-directY);
					return true;
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
	private boolean trackMovement(Field field) throws Exception
	{
		boolean pieceFound = checkJumps(field, field);
		this.resetVisited();

		if(!pieceFound)
		{
			if(checkMove(field,-1))
			{
				pieceFound = true;
			}
		}

		return pieceFound;
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
			field.getPieceOnField().isMoveable = checkMoveable(field, dify);	
		}
	}
	//latex start navn

	//Checks if a given field is moveable
	private boolean checkMoveable(Field field, int dif)
	{
		//Check forward
		if((!this.fieldOccupied(field.x-1, field.y+dif)) || !this.fieldOccupied(field.x+1, field.y+dif))
		{
			return true;
		}
		//if king, check backwards also
		else if(field.getPieceOnField().isCrowned)
		{
			if(!this.fieldOccupied(field.x - 1, field.y - dif) ||  !this.fieldOccupied(field.x + 1, field.y - dif))
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

	//Check if a given field is can jump
	private boolean checkJump(Field field, int dif, boolean checkForOpponent)
	{		
		//Check forwards
		if(checkJumpDirection(field, 1, dif, checkForOpponent) ||checkJumpDirection(field, -1, dif, checkForOpponent))
		{
			return true;
		}		
		//if king, check backwards
		else if(field.getPieceOnField().isCrowned)
		{ 
			if(checkJumpDirection(field,1, -dif, checkForOpponent || checkJumpDirection(field, -1, -dif, checkForOpponent)))
			{
				return true;
			}
		}
		return false;
	}

	//Checks jumps
	private boolean checkJumpDirection(Field field, int difx, int dify, boolean checkForOpponent)
	{
		//Forward
		if(checkBounds(field.x+difx,field.y+dify))
		{ 
			if(checkAllegiance(myBoard[field.x+difx][field.y+dify], checkForOpponent) && !this.fieldOccupied(field.x+2*difx, field.y+2*dify))
			{
				return true;
			}
		}
		//if king, also check backwards
		else if(field.getPieceOnField().isCrowned)
		{ 
			if(checkBounds(field.x+difx,field.y-dify))
			{
				if(checkAllegiance(myBoard[field.x+difx][field.y-dify], checkForOpponent) && !this.fieldOccupied(field.x+2*difx, field.y-2*dify))
				{
					return true;
				}
			}
		}
		return false;
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
	private char getColor(int x, int y) throws IOException
	{
		Color colorResult = remoteFunctions.getColorOnField(x, y);

		LCD.clear();
		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();
		LCD.drawInt(red, 0, 1);
		LCD.drawInt(green, 0, 2);
		LCD.drawInt(blue, 0, 3);
		if(red > 130  && green < 120 && green > 0 && blue < 160 && blue > 0)
		{
			LCD.drawChar('r', 0, 0);LCD.refresh();
			return 'r';
		}
		else if(red > 180 && green > 160 && blue > 160)
		{
			LCD.drawChar('w', 0, 0);LCD.refresh();
			return 'w';
		}
		else if(red > 150 && red < 180 && green > 170  && blue < 200 && blue > 150)
		{
			LCD.drawChar('g', 0, 0);LCD.refresh();
			return 'g';
		}
		else if(red < 140 && red > 90 && green < 170 && green > 120 && blue < 255 && blue > 160)
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
