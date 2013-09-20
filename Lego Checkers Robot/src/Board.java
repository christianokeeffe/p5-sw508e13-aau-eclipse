import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;


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
					if((i+j)%2 == 0)
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
							if(myColor != 'r')
							{
								temp.pieceColor = 'w';
							}
							else
							{
								temp.pieceColor = myColor;
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
		myBoard.get(3).get(3).isKing = true;
		myBoard.get(3).get(3).moveable = true;
		myBoard.get(3).get(3).pieceColor = 'w';
		
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
			 
		    if(field.moveable)
		    {
		    	
		    	if(this.isEmptyField(i, j))
		    	{
					if(field.isKing)
					{
						checkKingMove(field,i,j);
					}
					else
					{
						checkPeasantMove(field,i,j);
					}
		    	}
		    }
		}
		
		return true;
	}
	
	private void movePiece(Field field, int x, int y, int a, int b)
	{
		myBoard.get(x).get(y).isKing = field.isKing;
		myBoard.get(x).get(y).pieceColor = field.pieceColor;
		
		myBoard.get(a).get(b).isKing = false;
		myBoard.get(a).get(b).pieceColor = ' ';
	}
	
	private void checkPeasantMove(Field field, int i, int j) throws InterruptedException, IOException
	{
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
			else if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
		else if(i==7 && j==7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
		}
		else if(i == 0 && j!= 7)
		{
			//upgrade white peasant to king
		}
		else if(j == 0 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
		else if(j == 7 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
		}
		else if(i == 7 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
	}
	
	private void checkKingMove(Field field, int i, int j) throws InterruptedException, IOException
	{
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
			else if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
			}
			else if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
			}
			else if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
		else if(i==0 && j==0)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
			}
		}
		else if(i==7 && j==7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
		}
		else if(i == 0 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
			}
			if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
			}
		}
		else if(j == 0 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i+1, j+1))
			{
				movePiece(field, i+1, j+1, i, j);
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
		else if(j == 7 && i!=0 && i!= 7)
		{
			if(!this.isEmptyField(i+1, j-1))
			{
				movePiece(field, i+1, j-1, i, j);
			}
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
		}
		else if(i == 7 && j!=0 && j!= 7)
		{
			if(!this.isEmptyField(i-1, j-1))
			{
				movePiece(field, i-1, j-1, i, j);
			}
			if(!this.isEmptyField(i-1, j+1))
			{
				movePiece(field, i-1, j+1, i, j);
			}
		}
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

	
	private char findMyColor() throws InterruptedException, IOException
	{
		ColorSensor.Color colorResult = remoteFunctions.GetColorOnField(0, 0);
		
		int red = colorResult.getRed();
		int green = colorResult.getGreen();
		int blue = colorResult.getBlue();
		
		if(red > 150 && green <= 50 && blue <= 50)
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
							findDeadPieces();
						}
					}
				}
			}
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
