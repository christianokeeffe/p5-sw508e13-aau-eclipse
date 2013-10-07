import java.io.IOException;
import lejos.nxt.ColorSensor;


public class Board {

	Field[][] myBoard = new Field[8][8];

	char myColor, opponentColor;
	RemoteNXTFunctions remoteFunctions;
	
	public Board(RemoteNXTFunctions remoteFunc) throws InterruptedException, IOException
	{
		remoteFunctions = remoteFunc;
		myColor = findMyColor();
		opponentColor = findOpponentColor();

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

					if(y < 3)
					{
						temp.pieceColor = myColor;

						if(y == 2)
						{
							temp.moveable = true;
						}
					}

					if(y > 4)
					{
						temp.pieceColor = opponentColor;
						if(y==5)
						{
							temp.moveable = true;
						}
					}
				}
				else
				{
					temp.allowedField = false;
				}
				myBoard[x][y] = temp;
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
				if(field.moveable && field.pieceColor != myColor)
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
		return true;
	}

	private void movePiece(Field fromField, int toField_x, int toField_y) throws InterruptedException, IOException
	{
		if((toField_x >= 0 && toField_x <= 7) && (toField_y >= 0 && toField_y <= 7))
		{
			myBoard[toField_x][toField_y].adoptPropterties(fromField);
			myBoard[fromField.x][fromField.y].emptyThisField();

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

	private void upgradeKing(Field field)
	{
		myBoard[field.x][field.y].isKing = true;
	}

	private boolean checkJumps(Field field, boolean isKing) throws InterruptedException, IOException
	{
		boolean foundPiece = false;

		if(!foundPiece)
		{
			if(field.x-1 > 0 && field.x-1 < 7 && field.y-1 > 0 && field.y-1 < 7)
			{
				if(myBoard[field.x-1][field.y-1].pieceColor == myColor)
				{
					if(!this.containsPiece(field.x-2, field.y-2) && !myBoard[field.x-2][field.y-2].visited)
					{
						if(!this.isEmptyField(field.x-2, field.y-2))
						{
							myBoard[field.x-2][field.y-2].adoptPropterties(field);

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
				if(myBoard[field.x+1][field.y-1].pieceColor == myColor)
				{
					if(!this.containsPiece(field.x+2, field.y-2) && !myBoard[field.x+2][field.y-2].visited)
					{
						if(!this.isEmptyField(field.x+2, field.y-2))
						{
							myBoard[field.x+2][field.y-2].adoptPropterties(field);

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
				if(myBoard[field.x+1][field.y+1].pieceColor == myColor)
				{
					if(!this.containsPiece(field.x+2, field.y+2) && !myBoard[field.x+2][field.y+2].visited)
					{
						if( !this.isEmptyField(field.x+2, field.y+2))
						{
							myBoard[field.x+2][field.y+2].adoptPropterties(field);

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
				if(myBoard[field.x-1][field.y+1].pieceColor == myColor)
				{
					if(!this.containsPiece(field.x-2, field.y+2) && !myBoard[field.x-2][field.y+2].visited)
					{
						if(!this.isEmptyField(field.x-2, field.y+2))
						{
							myBoard[field.x-2][field.y+2].adoptPropterties(field);

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
		boolean pieceFound = this.checkJumps(field, field.isKing);
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

				if(field.isKing)
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
				if(field.isKing)
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
					upgradeKing(field);
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

				if(field.isKing)
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

				if(field.isKing)
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
				if (field.isKing)
				{
					if(!containsPiece(field.x-1,field.y+1) && !this.isEmptyField(field.x-1, field.y+1))
					{
						movePiece(field, field.x-1, field.y+1);
						return true;
					}
				}
				else
				{
					upgradeKing(field);
					return true;
				}
			}

			Field missingPiece = this.findMissingPiece(field.isKing);

			if(missingPiece.x != 0 && missingPiece.y != 0)
			{
				this.movePiece(field, missingPiece.x, missingPiece.y);
				return true;
			}
		}
		
		return pieceFound;
	}

	private boolean isEmptyField(int x, int y) throws InterruptedException, IOException
	{	
		if(x > 7 || x < 0 || y > 7 || y < 0)
		{
			return false;
		}

		ColorSensor.Color colorResult = remoteFunctions.getColorOnField(x, y);

		if(colorResult.getRed() < 50 && colorResult.getGreen() < 50 && colorResult.getBlue() < 50)
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
			if(myBoard[x][y].pieceColor == ' ')
			{
				return false;
			}
			else
			{
				return true;
			}
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

					if(field.pieceColor == myColor)
					{
						//Check simple move for peasant
						if(((field.x - 1 >= 0 && field.x - 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && !this.containsPiece(field.x-1, field.y+1)) || ((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && !this.containsPiece(field.x+1, field.y+1)))
						{
							moveable = true;
						}

						//Check whether a peasant jump is possible
						if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && myBoard[field.x-1][field.y+1].pieceColor == opponentColor && !this.containsPiece(field.x-2, field.y+2))
						{
							moveable = true;
						}
						else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && myBoard[field.x+1][field.y+1].pieceColor == opponentColor && !this.containsPiece(field.x+2, field.y+2))
						{
							moveable = true;
						}

						//Check for king
						if(field.isKing)
						{
							//Check simple move for king
							if(!this.containsPiece(field.x - 1, field.y - 1) || !this.containsPiece(field.x + 1, field.y - 1))
							{
								moveable = true;
							}

							//Check whether a king jump is possible
							if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && myBoard[field.x-1][field.y-1].pieceColor == opponentColor && !this.containsPiece(field.x-2, field.y-2))
							{
								moveable = true;
							}
							else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && myBoard[field.x+1][field.y-1].pieceColor == opponentColor && !this.containsPiece(field.x+2, field.y-2))
							{
								moveable = true;
							}
						}
					}

					//Check moveable for human
					else if(field.pieceColor == opponentColor)
					{
						//Check simple move for peasant
						if(((field.x - 1 >= 0 && field.x - 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x-1, field.y-1)) || ((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && !this.containsPiece(field.x+1, field.y-1)))
						{
							moveable = true;
						}

						//Check whether a peasant jump is possible
						if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && myBoard[field.x-1][field.y-1].pieceColor == myColor && !this.containsPiece(field.x-2, field.y-2))
						{
							moveable = true;
						}
						else if((field.x + 1 >= 0 && field.x +1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && myBoard[field.x+1][field.y-1].pieceColor == myColor && !this.containsPiece(field.x+2, field.y-2))
						{
							moveable = true;
						}

						//Check for king
						if(field.isKing)
						{
							//Check simple move for king
							if(!this.containsPiece(field.x - 1, field.y + 1) || !this.containsPiece(field.x + 1, field.y + 1))
							{
								moveable = true;
							}

							//Check whether a king jump is possible
							if((field.x - 1 >= 0 && field.x -1 <= 7 && field.y+1 >= 0 && field.y+1 <= 7) && myBoard[field.x-1][field.y+1].pieceColor == myColor && !this.containsPiece(field.x-2, field.y+2))
							{
								moveable = true;
							}
							else if((field.x + 1 >= 0 && field.x + 1 <= 7 && field.y-1 >= 0 && field.y-1 <= 7) && myBoard[field.x+1][field.y+1].pieceColor == myColor && !this.containsPiece(field.x+2, field.y+2))
							{
								moveable = true;
							}
						}
					}

					myBoard[field.x][field.y].moveable = moveable;
				}
			}
		}
	}

	private char findMyColor() throws InterruptedException, IOException
	{
		ColorSensor.Color colorResult = remoteFunctions.getColorOnField(0, 1);

		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();

		if(red > 150 && green <= 100 && blue <= 100)
		{
			return 'r';
		}
		else
		{
			return 'w';
		}
	}

	private char findOpponentColor()
	{
		if(myColor == 'r')
		{
			return 'w';
		}
		else if(myColor == 'w')
		{
			return 'r';
		}
		else 
		{
			return ' ';
		}
	}


	//panicMode
	private Field findMissingPiece(boolean wasKing) throws InterruptedException, IOException
	{
		int i,j;

		for(i=0;i<8;i++)
		{	
			for(j=0;j<8;j++)
			{
				if((i+j)%2 == 0)
				{
					if(myBoard[i][j].pieceColor != 'r' && myBoard[i][j].pieceColor != 'w')
					{
						if(!isEmptyField(i, j))
						{
							myBoard[i][j].pieceColor = opponentColor;
							myBoard[i][j].isKing = wasKing;
							this.findDeadPieces();
							return myBoard[i][j];
						}
					}
				}
			}
		}

		return myBoard[0][0];
	}


	private void findDeadPieces() throws InterruptedException, IOException
	{
		int i,j;
		for(i=0;i<8;i++)
		{	
			for(j=0;j<8;j++)
			{
				if((i+j)%2 == 1)
				{
					if(myBoard[i][j].pieceColor == 'r' || myBoard[i][j].pieceColor == 'w')
					{
						if(isEmptyField(i, j))
						{
							myBoard[i][j].emptyThisField();
						}
					}
				}
			}
		}
	}
}
