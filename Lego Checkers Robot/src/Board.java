import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;


public class Board {
	
	List<List<Field>> myBoard = new ArrayList<List<Field>>();
	char myColor;
	RemoteNXTFunctions remoteFunctions;
	
	public Board(RemoteNXTFunctions remoteFunc) throws InterruptedException, IOException
	{
		remoteFunctions = remoteFunc;
		myColor = findMyColor();
		
			int j,i;
			
			for(i=0;i<8;i++){
				
				List<Field> Columns = new ArrayList<Field>();
				for(j=0;j<8;j++)
				{
					Field temp = new Field();
					if((i+j)%2 == 1)
					{
						temp.allowedField = true;
						
						if(i < 3)
						{
							if(i == 2)
							{
								temp.moveable = true;
							}
							temp.pieceColor = myColor;
						}
						else if(i > 4)
						{
							if(i == 5)
							{
								temp.moveable = true;
							}
							if(myColor == 'r')
							{
								temp.pieceColor = 'w';
							}
							else
							{
								temp.pieceColor = 'r';
							}
						}
					}
					else
					{
						temp.allowedField = false;
					}
					Columns.add(temp);
				}
				myBoard.add(Columns);
		}
	}
	
	
	public boolean analyzeBoard() throws InterruptedException, IOException
	{
		// Test case, should be removed after used
		/*myBoard.get(3).get(4).isKing = true;
		myBoard.get(3).get(4).moveable = true;
		myBoard.get(3).get(4).pieceColor = 'w';*/
		
		int i = 0;
		for (List<Field> f : myBoard) {
			this.checkMovement(f,i);
			i++;
		}
		
		return true;
	}
	
	private boolean checkMovement(List<Field> fields, int i) throws InterruptedException, IOException
	{
		int j;
		
		for(j=0; j<fields.size(); j++)
		{
			Field field = new Field();
		    field = fields.get(j);
			 
		    if(field.moveable && field.pieceColor != myColor)
		    {
		    	if(this.isEmptyField(i, j))
		    	{
					if(field.isKing)
					{
						if (checkKingMove(field,i,j))
						{
							j = 8;
						}
					}
					else
					{
						if (checkPeasantMove(field,i,j))
						{
							j = 8;
						}
						
					}
		    	}
		    }
		}
		
		return true;
	}
	
	private void movePiece(Field FromField, int x, int y, int a, int b)
	{
		if((x >= 0 && x <= 7) && (y >= 0 && y <= 7))
		{
			myBoard.get(a).get(b).isKing = FromField.isKing;
			myBoard.get(a).get(b).pieceColor = FromField.pieceColor;
			
			myBoard.get(x).get(y).isKing = false;
			myBoard.get(x).get(y).pieceColor = ' ';
		}
		else
		{
			myBoard.get(a).get(b).isKing = false;
			myBoard.get(a).get(b).pieceColor = ' ';
		}
	}
	
	public void movePiece(Field FromField, Field ToField)
	{
		movePiece(FromField,FromField.x, FromField.y, ToField.x, ToField.y);
	}
	
	private void upgradeKing(int x, int y)
	{
		myBoard.get(x).get(y).isKing = true;
	}
	
	private boolean checkPeasantMove(Field field, int i, int j) throws InterruptedException, IOException
	{
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
			else if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		else if(i==7 && j==7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
		}
		else if(i == 0 && j!= 7)
		{
			upgradeKing(i,j);
			return true;
		}
		else if(j == 0 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		else if(j == 7 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
		}
		else if(i == 7 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		return false;
	}
	
	private boolean checkKingMove(Field field, int i, int j) throws InterruptedException, IOException
	{
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
			else if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
				return true;
			}
			else if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
				return true;
			}
			else if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		else if(i==0 && j==0)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
				return true;
			}
		}
		else if(i==7 && j==7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
		}
		else if(i == 0 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
				return true;
			}
			if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
				return true;
			}
		}
		else if(j == 0 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
				return true;
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		else if(j == 7 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
				return true;
			}
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
		}
		else if(i == 7 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
				return true;
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
				return true;
			}
		}
		return false;
	}
	
	private boolean isEmptyField(int x, int y) throws InterruptedException, IOException
	{
		
		ColorSensor.Color colorResult = remoteFunctions.GetColorOnField(x, y);
		
		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();
		
		if(red < 50 && green < 50 && blue < 50)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void checkMove(int i, int j, int x, int y) throws InterruptedException, IOException
	{
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			if(this.isEmptyField(i-1, j-1))
			{
				myBoard.get(i-1).get(j-1).moveable = true;
			}
			else if(this.isEmptyField(i-1, j+1))
			{
				myBoard.get(i-1).get(j+1).moveable = true;
			}
		}
		
		if((x > 0 && x < 7) && (y > 0 && y < 7))
		{
			if(x-1 != i && y-1 != j)
			{
				if(!this.isEmptyField(x-1, y-1))
				{
					if(this.isEmptyField(x, y - 2))
					{
						myBoard.get(x-1).get(y-1).moveable = true;
					}
					
					if(this.isEmptyField(x + 1, y + 1))
					{
						myBoard.get(x).get(y).moveable = true;
					}
					else
					{
						if(y + 2 <= 7){
							if(this.isEmptyField(x, y + 2))
							{
								myBoard.get(x + 1).get(y + 1).moveable = true;
							}
						}
					}
					
					if(this.isEmptyField(x + 1, y - 1))
					{
						myBoard.get(x).get(y).moveable = true;
					}
					else
					{
						if(y - 2 >= 0){
							if(this.isEmptyField(x, y - 2))
							{
								myBoard.get(x + 1).get(y - 1).moveable = true;
							}
						}
					}
					
					if(!this.isEmptyField(x + 1, y - 1) && !this.isEmptyField(x + 1, y + 1))
					{
						myBoard.get(x).get(y).moveable = false;
					}
				}
			}
			
			if(x-1 != i && y+1 != j)
			{
				if(!this.isEmptyField(x-1, y+1))
				{
					if(this.isEmptyField(x, y + 2))
					{
						myBoard.get(x-1).get(y+1).moveable = true;
					}
				}
			}
		}
	}
	
	private char findMyColor() throws InterruptedException, IOException
	{
		ColorSensor.Color colorResult = remoteFunctions.GetColorOnField(0, 1);
		
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
		else
		{
			return 'r';
		}
	}
	
	//panicMode
	private void findMissingPiece(boolean wasKing) throws InterruptedException, IOException
	{
		int i,j;
		boolean deadFlag = false;
		
		outerloop:
		for(i=0;i<8;i++)
		{	
			for(j=0;j<8;j++)
			{
				if((i+j)%2 == 0)
				{
					if(myBoard.get(i).get(j).pieceColor != 'r' && myBoard.get(i).get(j).pieceColor != 'w')
					{
						if(!isEmptyField(i, j))
						{
							myBoard.get(i).get(j).pieceColor = findOpponentColor();
							myBoard.get(i).get(j).isKing = wasKing;
							deadFlag = true;
							break outerloop;
						}
					}
				}
			}
		}
		if(deadFlag)
		{
			findDeadPieces();
		}
	}
	
	private void findDeadPieces() throws InterruptedException, IOException
	{
		int i,j;
		for(i=0;i<8;i++)
		{	
			for(j=0;j<8;j++)
			{
				if((i+j)%2 == 0)
				{
					if(myBoard.get(i).get(j).pieceColor == 'r' || myBoard.get(i).get(j).pieceColor == 'w')
					{
						if(isEmptyField(i, j))
						{
							myBoard.get(i).get(j).pieceColor = ' ';
							myBoard.get(i).get(j).isKing = false;
						}
					}
				}
			}
		}
	}
}
