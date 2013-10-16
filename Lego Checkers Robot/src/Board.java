import java.io.IOException;
import java.util.*;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.Color;

public class Board {

	Field[][] myBoard = new Field[8][8];
	Field[] KingPlace = new Field[8];

	char myPeasentColor, myKingColor, opponentPeasentColor, opponentKingColor;
	RemoteNXTFunctions remoteFunctions;

	public Board(RemoteNXTFunctions remoteFunc) throws InterruptedException, IOException
	{
		remoteFunctions = remoteFunc;
		findMyColors();
		findOpponentColors();

		int x,y;

		for(x = 0; x<8; x++)
		{
			for(y=0; y<8; y++)
			{
				Field temp = new Field();
				temp.x = x;
				temp.y = y;

				if((x+y)%2 == 1)
				{
					temp.allowedField = true;
					Piece PieceOnBoard = null;
					if(y < 3)
					{
						PieceOnBoard = new Piece();
						PieceOnBoard.color = myPeasentColor;

						if(y == 2)
						{
							PieceOnBoard.isMoveable = true;
						}
					}

					if(y > 4)
					{
						PieceOnBoard = new Piece();
						PieceOnBoard.color = opponentPeasentColor;
						if(y==5)
						{
							PieceOnBoard.isMoveable = true;
						}
					}
					temp.setPieceOnField(PieceOnBoard);
				}
				else
				{
					temp.allowedField = false;
				}
				myBoard[x][y] = temp;
			}	
		}

		for (int i = 0; i < 8; i++){
			Field temp = new Field();
			temp.x = i;
			temp.y = -2;
			Piece tempPiece = new Piece();
			tempPiece.color = opponentKingColor;
			tempPiece.isCrowned = true;
			temp.setPieceOnField(tempPiece);
			KingPlace[i] = temp;
		}
	}

	private boolean IsGreater(Field InputField, Field FieldToCompare, int compX, int compY){
		if(InputField.getPieceOnField().canJump && !FieldToCompare.getPieceOnField().canJump)
		{
			return true;
		}else if(FieldToCompare.getPieceOnField().canJump && !InputField.getPieceOnField().canJump){
			return false;
		}
		else{
			if(Math.abs(InputField.x -compX)+Math.abs(InputField.y - compY) >Math.abs(FieldToCompare.x -compX)+Math.abs(FieldToCompare.y - compY)){
				return false;
			}else if(Math.abs(InputField.x -compX)+Math.abs(InputField.y - compY) <Math.abs(FieldToCompare.x -compX)+Math.abs(FieldToCompare.y - compY)){
				return true;
			}else{
				if(InputField.y < FieldToCompare.y){
					return true;
				}else{
					return false;
				}
			}
		}
	}

	private void SortListOfFields(List<Field> ListOfFields){
		int x = 0;
		int y = 0;
		for(int i = 0; i < ListOfFields.size(); i++){
			for(int n = i+1; n < ListOfFields.size(); n++){
				if(!IsGreater(ListOfFields.get(i), ListOfFields.get(n),x,y)){
					Field temp1 = ListOfFields.get(i);
					Field temp2 = ListOfFields.get(n);
					ListOfFields.remove(n);
					ListOfFields.remove(i);
					ListOfFields.add(i, temp2);
					ListOfFields.add(n,temp1);
				}
			}
			x = ListOfFields.get(i).x;
			y = ListOfFields.get(i).y;
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
	public boolean analyzeBoard() throws Exception
	{	
		updateMoveables();

		List<Field> MoveableList = new ArrayList<Field>();

		for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				if(!field.isEmpty()){
					if(field.getPieceOnField().isMoveable && checkAllegiance(field, true))
					{
						MoveableList.add(field);
					}
				}
			}
		}

		SortListOfFields(MoveableList);

		OUTERMOST: for(Field field : MoveableList){
			if(this.isFieldEmptyOnBoard(field.x, field.y))
			{
				if(this.trackMovement(field))
				{
					break OUTERMOST;
				}
			}

		}
		updateMoveables();
		return true;
	}

	private void UpgradeToKing(Field field) throws Exception{
		if(checkAllegiance(field, false)){
			field.getPieceOnField().color = myKingColor;
			field.getPieceOnField().isCrowned = true;
		}
		else{
			boolean FoundOne = false;
			int i = 0;
			while(!FoundOne){
				if(i>7){
					throw new Exception();
				}
				if(KingPlace[i].getPieceOnField() != null){
					remoteFunctions.movePiece(field, remoteFunctions.trashField);
					remoteFunctions.movePiece(KingPlace[i], field);
					FoundOne = true;
				}
				i++;

			}
		}
	}

	private void checkForUpgradeKing(Field field) throws Exception{
		int checkrow;
		if(checkAllegiance(field, true)){
			checkrow = 0;
		}else{
			checkrow = 7;
		}

		if(field.y == checkrow){
			UpgradeToKing(field);
		}
	}

	private void movePiece(Field fromField, int toField_x, int toField_y) throws Exception
	{
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

	}

	public void movePiece(Field FromField, Field ToField) throws Exception
	{
		movePiece(FromField, ToField.x, ToField.y);
	}

	private boolean checkSingleJump(Field field, int difX, int difY, Field originalField) throws Exception{
		if(checkBounds(field.x + difX, field.y + difY))
		{
			if(checkAllegiance(myBoard[field.x + difX][field.y + difY], false))
			{
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
						myBoard[field.x+2*difX][field.y+2*difY].visited = true;
						boolean returnval = checkJumps(myBoard[field.x+2*difX][field.y+2*difY], originalField);
						if(returnval){
							myBoard[field.x][field.y].emptyThisField();
							myBoard[field.x+difX][field.y+difY].emptyThisField();
						}
						return returnval;
					}
				}
			}
		}
		return false;
	}

	private boolean checkJumps(Field field, Field originalField) throws Exception
	{
		boolean foundPiece = false;

		foundPiece = checkSingleJump(field, -1, -1, originalField);

		if(!foundPiece)
		{
			foundPiece = checkSingleJump(field, 1, -1, originalField);
		}

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

	private boolean checkMove(Field field, int directY) throws Exception{
		if(checkBounds(field.x,field.y))
		{
			if(checkMoveDirection(field,1,directY))
			{
				movePiece(field, field.x+1, field.y+directY);
				return true;
			}
			else if(checkMoveDirection(field,-1,directY))
			{
				movePiece(field, field.x-1, field.y+directY);
				return true;
			}
			else if(field.getPieceOnField().isCrowned)
			{
				if(checkMoveDirection(field,1,-directY))
				{
					movePiece(field, field.x+1, field.y-directY);
					return true;
				}
				else if(checkMoveDirection(field,-1,directY))
				{
					movePiece(field, field.x-1, field.y-directY);
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkMoveDirection(Field field, int directX, int directY) throws InterruptedException, IOException
	{
		if(!fieldOccupied(field.x+directX,field.y+directY) && !this.isFieldEmptyOnBoard(field.x+directX, field.y+directY))
			return true;
		else
			return false;
	}

	private boolean trackMovement(Field field) throws Exception
	{
		boolean pieceFound = checkJumps(field, field);
		this.resetVisited();

		if(!pieceFound)
		{
			if(checkMove(field,-1))
				pieceFound = true;
			else
			{
				this.findMissingPiece();
				return true;	
			}
		}

		return pieceFound;
	}
	private String boolToString(boolean input){
		String returnString;
		if(input == true)
			returnString = "true";
		else
			returnString = "false";

		return returnString;
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

	private boolean fieldOccupied(int x, int y)
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

	private void checkPiece(Field field, int dify, boolean checkForOpponent)
	{
		field.getPieceOnField().canJump = checkJump(field,dify, checkForOpponent);
		field.getPieceOnField().isMoveable = checkMoveable(field, dify);	
	}

	private boolean checkMoveable(Field field, int dif)
	{
		if((!this.fieldOccupied(field.x-1, field.y+dif)) || !this.fieldOccupied(field.x+1, field.y+dif))
		{
			return true;
		}
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

	private boolean checkJump(Field field, int dif, boolean checkForOpponent)
	{		
		if(checkJumpDirection(field, 1, dif, checkForOpponent) ||checkJumpDirection(field, -1, dif, checkForOpponent))
		{
			return true;
		}		
		else if(field.getPieceOnField().isCrowned){ 
			if(checkJumpDirection(field,1, -dif, checkForOpponent || checkJumpDirection(field, -1, -dif, checkForOpponent)))
			{
				return true;
			}
		}
		return false;
	}

	private boolean checkJumpDirection(Field field, int difx, int dify, boolean checkForOpponent)
	{
		if(checkBounds(field.x-difx,field.y+dify))
		{ 
			if(checkAllegiance(myBoard[field.x-difx][field.y+dify], checkForOpponent) && !this.fieldOccupied(field.x-2*difx, field.y+2*dify))
			{
				return true;
			}
		}
		else if(field.getPieceOnField().isCrowned)
		{ 
			if(checkBounds(field.x-difx,field.y-dify))
			{
				if(checkAllegiance(myBoard[field.x-difx][field.y-dify], checkForOpponent) && !this.fieldOccupied(field.x-2*difx, field.y-2*dify))
				{
					return true;
				}
			}
		}
		return false;
	}

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

	private void findMyColors() throws InterruptedException, IOException
	{
		myPeasentColor = getColor(0,1);
		if(myPeasentColor == 'r')
		{
			myKingColor = 'b';
		}
		else
		{
			myKingColor = 'g';
		}
	}

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


	//panicMode
	private void findMissingPiece() throws InterruptedException, IOException
	{
		int i,j;
		boolean changer = true;

		for(i=0;i<8;i++)
		{	
			if(changer)
			{
				for(j=0;j<8;j++)
				{
					if((i+j)%2 == 1)
					{
						myBoard[i][j].setPieceOnField(GetPiece(i, j));
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
						myBoard[i][j].setPieceOnField(GetPiece(i, j));
					}
				}
				changer = true;
			}
		}

		this.updateMoveables();
	}

	private Piece GetPiece(int x, int y) throws IOException{
		char color = getColor(x, y);
		if(color == ' ')
		{
			return null;
		}else{
			Piece temp = new Piece();
			temp.color = color;
			temp.isCrowned = (color == 'g' || color == 'b');
			return temp;
		}
	}

	private boolean checkAllegiance(Field input, boolean checkForOpponent){
		if((input.isPieceOfColor(myPeasentColor)||input.isPieceOfColor(myKingColor)) && !checkForOpponent){

			return true;
		}
		if((input.isPieceOfColor(opponentPeasentColor)||input.isPieceOfColor(opponentKingColor)) && checkForOpponent){

			return true;
		}
		return false;
	}
	private boolean checkBounds(int x, int y){
		if(x >= 0 && x <= 7 && y >= 0 && y <= 7)
		{
			return true;
		}
		else
			return false;
	}
}
