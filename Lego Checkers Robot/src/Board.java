import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.robotics.Color;
import lejos.util.Delay;

public class Board {

	Field[][] myBoard = new Field[8][8];

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
		myBoard[3][4].setPieceOnField(myBoard[7][0].getPieceOnField());
	}
	
	//Only a method for testing - delete before release
	public void testRedPieces() throws InterruptedException, IOException{
		for(Field[] f : myBoard)
		{
			for(Field field : f)
			{
				if(field.getPieceOnField().color == 'r' || field.getPieceOnField().color == 'b')
				{
					this.isEmptyField(field.x, field.y);
				}
			}
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

	public boolean analyzeBoard() throws InterruptedException, IOException
	{	
		updateMoveables();
		
		OUTERMOST: for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				if(!field.isEmpty()){
					if(field.getPieceOnField().isMoveable && checkAllegiance(field, true))
					{
						if(this.isEmptyField(field.x, field.y))
						{
							if(this.checkMove(field))
							{
								break OUTERMOST;
							}
						}
					}
				}
			}
		}
		
		updateMoveables();
		return true;
	}

	private void movePiece(Field fromField, int toField_x, int toField_y) throws InterruptedException, IOException
	{
		if(checkBounds(toField_x,toField_y))
		{	
			myBoard[toField_x][toField_y].setPieceOnField(fromField.getPieceOnField());
			fromField.emptyThisField();
		}
		else
		{
			myBoard[fromField.x][fromField.y].emptyThisField();
		}

	}

	public void movePiece(Field FromField, Field ToField) throws InterruptedException, IOException
	{
		movePiece(FromField, ToField.x, ToField.y);
	}
	
	private boolean checkJumps(Field field, boolean isKing) throws InterruptedException, IOException
	{
		boolean foundPiece = false;

		if(checkBounds(field.x-1, field.y-1))
		{
			if(checkAllegiance(myBoard[field.x-1][field.y-1], false))
			{
				if(!this.fieldOccupied(field.x-2, field.y-2) && !myBoard[field.x-2][field.y-2].visited)
				{
					if(this.isEmptyField(field.x-2, field.y-2))
					{
						myBoard[field.x-2][field.y-2].setPieceOnField(field.getPieceOnField());

						//Empty jumped field and old field
						myBoard[field.x][field.y].emptyThisField();
						myBoard[field.x-1][field.y-1].emptyThisField();

						return true;
					}
					else
					{
						myBoard[field.x-2][field.y-2].visited = true;
						foundPiece  = this.checkJumps(myBoard[field.x-2][field.y-2], isKing);							
					}
				}
			}
		}


		if(!foundPiece)
		{
			if(checkBounds(field.x+1, field.y-1))
			{
				if(checkAllegiance(myBoard[field.x+1][field.y-1], false))
				{
					if(!this.fieldOccupied(field.x+2, field.y-2) && !myBoard[field.x+2][field.y-2].visited)
					{
						if(!this.isEmptyField(field.x+2, field.y-2))
						{
							myBoard[field.x+2][field.y-2].setPieceOnField(field.getPieceOnField());

							//Empty jumped field and old field
							myBoard[field.x][field.y].emptyThisField();
							myBoard[field.x+1][field.y-1].emptyThisField();

							return true;
						}
						else
						{
							myBoard[field.x+2][field.y-2].visited = true;
							foundPiece = this.checkJumps(myBoard[field.x+2][field.y-2], isKing);
						}
					}
				}
			}
		}

		if(!foundPiece && isKing)
		{
			if(checkBounds(field.x+1, field.y+1))
			{
				if(checkAllegiance(myBoard[field.x+1][field.y+1], false))
				{
					if(!this.fieldOccupied(field.x+2, field.y+2) && !myBoard[field.x+2][field.y+2].visited)
					{
						if( !this.isEmptyField(field.x+2, field.y+2))
						{
							myBoard[field.x+2][field.y+2].setPieceOnField(field.getPieceOnField());

							//Empty jumped field and old field
							myBoard[field.x][field.y].emptyThisField();
							myBoard[field.x+1][field.y+1].emptyThisField();

							return true;
						}
						else
						{
							myBoard[field.x+2][field.y+2].visited = true;
							foundPiece = this.checkJumps(myBoard[field.x+2][field.y+2], isKing);
						}
					}
				}
			}
		}

		if(!foundPiece && isKing)
		{
			if(checkBounds(field.x-1, field.y+1))
			{
				if(checkAllegiance(myBoard[field.x-1][field.y+1], false))
				{
					if(!this.fieldOccupied(field.x-2, field.y+2) && !myBoard[field.x-2][field.y+2].visited)
					{
						if(!this.isEmptyField(field.x-2, field.y+2))
						{
							myBoard[field.x-2][field.y+2].setPieceOnField(field.getPieceOnField());

							//Empty jumped field and old field
							myBoard[field.x][field.y].emptyThisField();
							myBoard[field.x-1][field.y+1].emptyThisField();

							return true;
						}
						else
						{
							myBoard[field.x-2][field.y+2].visited = true;
							foundPiece = this.checkJumps(myBoard[field.x-2][field.y+2], isKing);
						}
					}
				}
			}
		}

		return foundPiece;
	}

	private boolean checkMove(Field field) throws InterruptedException, IOException
	{
		boolean pieceFound = checkJumps(field, field.getPieceOnField().isCrowned);
		this.resetVisited();

		if(!pieceFound)
		{
			if(checkBounds(field.x,field.y))
			{
				if(!fieldOccupied(field.x-1,field.y-1) && !this.isEmptyField(field.x-1, field.y-1))
				{
					movePiece(field, field.x-1, field.y-1);
					return true;
				}
				else if(!fieldOccupied(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!fieldOccupied(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
					else if(!fieldOccupied(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
			}
			else if(field.x==0 && field.y==7)
			{
				if(!fieldOccupied(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}
			}
			else if(field.x == 0 && field.y!=0 && field.y!= 7)
			{
				if(field.getPieceOnField().isCrowned)
				{
					if(!fieldOccupied(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
					if(!fieldOccupied(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
				else {
					myBoard[field.x][field.y].getPieceOnField().isCrowned = true;
					return true;
				}
			}
			else if(field.x == 0 && field.y!=0 && field.y!= 7)
			{
				if(!fieldOccupied(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!fieldOccupied(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
				}
			}
			else if(field.x == 7 && field.y!=0 && field.y!= 7)
			{
				if(!fieldOccupied(field.x-1,field.y-1) && !this.isEmptyField(field.x-1, field.y-1))
				{
					movePiece(field, field.x-1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!fieldOccupied(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
			}

			else if(field.x==7 && field.y==0)
			{
				if (field.getPieceOnField().isCrowned)
				{
					if(!fieldOccupied(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
				else
				{
					myBoard[field.x][field.y].getPieceOnField().isCrowned = true;
					return true;
				}
			}

			this.findMissingPiece();
			return true;
		}
		
		return pieceFound;
	}

	private boolean isEmptyField(int x, int y) throws InterruptedException, IOException
	{	
		if(x > 7 || x < 0 || y > 7 || y < 0)
		{
			return false;
		}

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

	private void updateMoveables()
	{
		boolean moveable, canJump;
		for(Field[] f : myBoard)
		{
			for(Field field : f)
			{
				if(field.allowedField)
				{
					moveable = false;
					canJump = false;
					//Check moveables for robot
					if(field.getPieceOnField() != null){
						if(checkAllegiance(field,false))
						{
							//Check simple move for peasant
							if((!this.fieldOccupied(field.x-1, field.y+1)) || !this.fieldOccupied(field.x+1, field.y+1))
							{
								moveable = true;
							}
	
							//Check whether a peasant jump is possible
							if(checkBounds(field.x-1,field.y+1) && checkAllegiance(myBoard[field.x-1][field.y+1], true) && !this.fieldOccupied(field.x-2, field.y+2))
							{
								moveable = true;
								canJump = true;
							}
							else if(checkBounds(field.x+1,field.y+1) && checkAllegiance(myBoard[field.x+1][field.y+1], true) && !this.fieldOccupied(field.x+2, field.y+2))
							{
								moveable = true;
								canJump = true;
							}
	
							//Check for king
							if(field.getPieceOnField().isCrowned)
							{
								//Check simple move for king
								if(!this.fieldOccupied(field.x - 1, field.y - 1) ||  !this.fieldOccupied(field.x + 1, field.y - 1))
								{
									moveable = true;
								}
	
								//Check whether a king jump is possible
								if(checkBounds(field.x-1,field.y-1) && checkAllegiance(myBoard[field.x-1][field.y-1], true) && !this.fieldOccupied(field.x-2, field.y-2))
								{
									moveable = true;
									canJump = true;
								}
								else if(checkAllegiance(myBoard[field.x+1][field.y-1], true) && !this.fieldOccupied(field.x+2, field.y-2))
								{
									moveable = true;
									canJump = true;
								}
							}
						}
					
						
						//Check moveable for human
						else if(checkAllegiance(field, true))
						{
							//Check simple move for peasant
							
							if(!this.fieldOccupied(field.x-1, field.y-1) || !this.fieldOccupied(field.x+1, field.y-1))
							{
								moveable = true;
							}
	
							//Check whether a peasant jump is possible
							if(checkBounds(field.x-1,field.y-1) && checkAllegiance(myBoard[field.x-1][field.y-1], false) && !this.fieldOccupied(field.x-2, field.y-2))
							{
								moveable = true;
								canJump = true;
							}
							else if(checkBounds(field.x+1,field.y-1) && checkAllegiance(myBoard[field.x+1][field.y-1], false) && !this.fieldOccupied(field.x+2, field.y-2))
							{
								moveable = true;
								canJump = true;
							}
	
							//Check for king
							if(field.getPieceOnField().isCrowned)
							{
								//Check simple move for king
								if(!this.fieldOccupied(field.x - 1, field.y + 1) || !this.fieldOccupied(field.x + 1, field.y + 1))
								{
									moveable = true;
								}
	
								//Check whether a king jump is possible
								if(checkBounds(field.x-1,field.y+1) && checkAllegiance(myBoard[field.x-1][field.y+1], false) && (checkBounds(field.x-2,field.y+2) && !this.fieldOccupied(field.x-2, field.y+2)))
								{
									moveable = true;
									canJump = true;
								}
								else if(checkBounds(field.x+1,field.y+1) && checkAllegiance(myBoard[field.x+1][field.y+1], false) && (checkBounds(field.x+2,field.y+2) && !this.fieldOccupied(field.x+2, field.y+2)))
								{
									moveable = true;
									canJump = true;
								}
							}
						}
					myBoard[field.x][field.y].getPieceOnField().isMoveable = moveable;
					myBoard[field.x][field.y].getPieceOnField().canJump = canJump;
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

		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();
		
		if(red > 180 && red < 255 && green < 120 && green > 30 && blue < 160 && blue > 30)
		{
			return 'r';
		}
		else if(red > 180 && red < 255 && green > 180 && green < 255 && blue > 180 && blue < 255)
		{
			return 'w';
		}
		else if(red > 150 && red < 200 && green > 170 && green < 255 && blue < 200 && blue > 150)
		{
			return 'g';
		}
		else if(red < 140 && red > 90 && green < 170 && green > 120 && blue < 255 && blue > 160)
		{
			return 'b';
		}
		else
		{
			return ' ';
		}
	}


	//panicMode
	public void findMissingPiece() throws InterruptedException, IOException
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
