import java.io.IOException;

import lejos.nxt.LCD;
import lejos.robotics.Color;

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
					Piece PieceOnBoard = new Piece();
					if(y < 3)
					{
						PieceOnBoard.color = myPeasentColor;

						if(y == 2)
						{
							PieceOnBoard.isMoveable = true;
						}
					}

					if(y > 4)
					{
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
		OUTERMOST: for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				if(!field.isEmpty()){
					if(field.getPieceOnField().isMoveable && field.getPieceOnField().color != myPeasentColor && field.getPieceOnField().color != myKingColor)
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
		return true;
	}

	private void movePiece(Field fromField, int toField_x, int toField_y) throws InterruptedException, IOException
	{
		if((toField_x >= 0 && toField_x <= 7) && (toField_y >= 0 && toField_y <= 7))
		{	
			myBoard[toField_x][toField_y].setPieceOnField(fromField.getPieceOnField());
			fromField.emptyThisField();

			this.updateMoveables();
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

		if(!foundPiece)
		{
			if(field.x-1 > 0 && field.x-1 < 7 && field.y-1 > 0 && field.y-1 < 7)
			{
				if(myBoard[field.x-1][field.y-1].getPieceOnField().color == myPeasentColor || myBoard[field.x-1][field.y-1].getPieceOnField().color == myKingColor)
				{
					if(!this.containsPiece(field.x-2, field.y-2) && !myBoard[field.x-2][field.y-2].visited)
					{
						if(!this.isEmptyField(field.x-2, field.y-2))
						{
							myBoard[field.x-2][field.y-2].setPieceOnField(field.getPieceOnField());;

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
		}

		if(!foundPiece)
		{
			if(field.x+1 > 0 && field.x+1 < 7 && field.y-1 > 0 && field.y-1 < 7)
			{
				if(myBoard[field.x+1][field.y-1].getPieceOnField().color == myPeasentColor || myBoard[field.x+1][field.y-1].getPieceOnField().color == myKingColor)
				{
					if(!this.containsPiece(field.x+2, field.y-2) && !myBoard[field.x+2][field.y-2].visited)
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
			if(field.x+1 > 0 && field.x+1 < 7 && field.y+1 > 0 && field.y+1 < 7)
			{
				if(myBoard[field.x+1][field.y+1].getPieceOnField().color == myPeasentColor || myBoard[field.x+1][field.y+1].getPieceOnField().color == myKingColor)
				{
					if(!this.containsPiece(field.x+2, field.y+2) && !myBoard[field.x+2][field.y+2].visited)
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
			if(field.x-1 > 0 && field.x-1 < 7 && field.y+1 > 0 && field.y+1 < 7)
			{
				if(myBoard[field.x-1][field.y+1].getPieceOnField().color == myPeasentColor || myBoard[field.x-1][field.y+1].getPieceOnField().color == myKingColor)
				{
					if(!this.containsPiece(field.x-2, field.y+2) && !myBoard[field.x-2][field.y+2].visited)
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
		boolean pieceFound = this.checkJumps(field, field.getPieceOnField().isCrowned);
		this.resetVisited();

		if(!pieceFound)
		{
			if((field.x > 0 && field.x < 7) && (field.y > 0 && field.y <= 7))
			{
				if(!containsPiece(field.x-1,field.y-1) && !this.isEmptyField(field.x-1, field.y-1))
				{
					movePiece(field, field.x-1, field.y-1);
					return true;
				}
				else if(!containsPiece(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!containsPiece(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
					else if(!containsPiece(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
			}
			else if(field.x==0 && field.y==7)
			{
				if(!containsPiece(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}
			}
			else if(field.x == 0 && field.y!=0 && field.y!= 7)
			{
				if(field.getPieceOnField().isCrowned)
				{
					if(!containsPiece(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
					if(!containsPiece(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
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
				if(!containsPiece(field.x+1,field.y-1) && !this.isEmptyField(field.x+1, field.y-1))
				{
					movePiece(field, field.x+1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!containsPiece(field.x+1,field.y+1) && !this.isEmptyField(field.x+1, field.y+1))
					{
						movePiece(field, field.x+1, field.y+1);
						return true;
					}
				}
			}
			else if(field.x == 7 && field.y!=0 && field.y!= 7)
			{
				if(!containsPiece(field.x-1,field.y-1) && !this.isEmptyField(field.x-1, field.y-1))
				{
					movePiece(field, field.x-1, field.y-1);
					return true;
				}

				if(field.getPieceOnField().isCrowned)
				{
					if(!containsPiece(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
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
					if(!containsPiece(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
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

	private boolean containsPiece(int x, int y)
	{
		if(x <= 7 && y <= 7 && x >= 0 && y >= 0)
		{
			return myBoard[x][y].isEmpty();
		}
		else
		{
			return false;
		}
	}

	private void updateMoveables()
	{
		boolean moveable;
		for(Field[] f : myBoard)
		{
			for(Field field : f)
			{
				if(field.allowedField)
				{
					moveable = false;
					//Check moveables for robot
					if(field.getPieceOnField() != null){
						if(checkAllegiance(field,false))
						{
							//Check simple move for peasant
							if(((field.x - 1 >= 0 && field.x - 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && !this.containsPiece(field.x-1, field.y+1)) || ((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && !this.containsPiece(field.x+1, field.y+1)))
							{
								moveable = true;
							}
	
							//Check whether a peasant jump is possible
							if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && checkAllegiance(myBoard[field.x-1][field.y+1], true) && !this.containsPiece(field.x-2, field.y+2))
							{
								moveable = true;
							}
							else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && checkAllegiance(myBoard[field.x+1][field.y+1], true) && !this.containsPiece(field.x+2, field.y+2))
							{
								moveable = true;
							}
	
							//Check for king
							if(field.getPieceOnField().isCrowned)
							{
								//Check simple move for king
								if((field.x - 1 >= 0 && field.x - 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x - 1, field.y - 1) || ((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x + 1, field.y - 1)))
								{
									moveable = true;
								}
	
								//Check whether a king jump is possible
								if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && checkAllegiance(myBoard[field.x-1][field.y-1], true) && !this.containsPiece(field.x-2, field.y-2))
								{
									moveable = true;
								}
								else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && checkAllegiance(myBoard[field.x+1][field.y-1], true) && !this.containsPiece(field.x+2, field.y-2))
								{
									moveable = true;
								}
							}
						}
					
						
						//Check moveable for human
						else if(checkAllegiance(field, true))
						{
							//Check simple move for peasant
							if(((field.x - 1 >= 0 && field.x - 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x-1, field.y-1)) || ((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x+1, field.y-1)))
							{
								moveable = true;
							}
	
							//Check whether a peasant jump is possible
							if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && checkAllegiance(myBoard[field.x-1][field.y-1], false) && !this.containsPiece(field.x-2, field.y-2))
							{
								moveable = true;
							}
							else if((field.x + 1 >= 0 && field.x +1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && checkAllegiance(myBoard[field.x+1][field.y-1], false) && !this.containsPiece(field.x+2, field.y-2))
							{
								moveable = true;
							}
	
							//Check for king
							if(field.getPieceOnField().isCrowned)
							{
								//Check simple move for king
								if(!this.containsPiece(field.x - 1, field.y + 1) || !this.containsPiece(field.x + 1, field.y + 1))
								{
									moveable = true;
								}
	
								//Check whether a king jump is possible
								if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && checkAllegiance(myBoard[field.x-1][field.y+1], false) && !this.containsPiece(field.x-2, field.y+2))
								{
									moveable = true;
								}
								else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && checkAllegiance(myBoard[field.x+1][field.y+1], false) && !this.containsPiece(field.x+2, field.y+2))
								{
									moveable = true;
								}
							}
						}
					myBoard[field.x][field.y].getPieceOnField().isMoveable = moveable;
					}
					
				}
			}
		}
	}

	private void findMyColors() throws InterruptedException, IOException
	{
		myPeasentColor = 'r';//getColor(0,1);
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
		
		LCD.clear();
		LCD.drawInt(red, 0, 0);
		LCD.drawInt(green, 0, 1);
		LCD.drawInt(blue, 0, 2);
		LCD.refresh();
		
		if(red > 5 && green < 5 && blue < 5)
		{
			return 'r';
		}
		else if(red > 0 && green > 0 && blue > 0)
		{
			return 'w';
		}
		else if(green > 0 && red < 5 && blue < 5)
		{
			return 'g';
		}
		else if(blue > 0 && red < 5 && green < 5)
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
						myBoard[i][j].getPieceOnField().color = getColor(i, j);
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
						myBoard[i][j].getPieceOnField().color = getColor(i, j);
					}
				}
				changer = true;
			}
		}
		
		this.updateMoveables();
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
}
