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
		
		myBoard.get(3).get(3).isKing = true;
		myBoard.get(3).get(3).moveable = true;
		myBoard.get(3).get(3).pieceColor = 'w';
		
		for (List<Field> f : myBoard) {
			this.checkMovement(f);
		}
		
		return true;
	}
	
	public boolean checkMovement(List<Field> fields) throws InterruptedException, IOException
	{
		
		int i=0, j=0, k=0;
		
		for(k=0; k<fields.size(); k++)
		{
		
			LCD.drawString("isEmptyField called", 0, 0);
			LCD.refresh();
			
			Field field = new Field();
		    field = fields.get(k);
			 
		    if(field.moveable)
		    {
		    	
		    	if(this.isEmptyField(i, j))
		    	{
					if(field.isKing)
					{
						if((i > 0 && i < 7) && (j > 0 && j < 7))
						{
							if(!this.isEmptyField(i-1, j-1))
							{
								//King moved here
							}
							else if(!this.isEmptyField(i+1, j-1))
							{
								//King moved here
							}
							else if(!this.isEmptyField(i+1, j+1))
							{
								//King moved here
							}
							else if(!this.isEmptyField(i-1, j+1))
							{
								//King moved here
							}
						}
						else if(i==0 && j==0)
						{
							if(!this.isEmptyField(i+1, j+1))
							{
								//King moved here
							}
						}
						else if(i==7 && j==7)
						{
							if(!this.isEmptyField(i-1, j-1))
							{
								//King moved here
							}
						}
						else if(i == 0 && j!=0 && j!= 7)
						{
							if(!this.isEmptyField(i+1, j+1))
							{
								//King moved here
							}
							if(!this.isEmptyField(i+1, j-1))
							{
								//King moved here
							}
						}
						else if(j == 0 && i!=0 && i!= 7)
						{
							if(!this.isEmptyField(i+1, j+1))
							{
								//King moved here
							}
							if(!this.isEmptyField(i-1, j+1))
							{
								//King moved here
							}
						}
						else if(j == 7 && i!=0 && i!= 7)
						{
							if(!this.isEmptyField(i+1, j-1))
							{
								//King moved here
							}
							if(!this.isEmptyField(i-1, j-1))
							{
								//King moved here
							}
						}
						else if(i == 7 && j!=0 && j!= 7)
						{
							if(!this.isEmptyField(i-1, j-1))
							{
								//King moved here
							}
							if(!this.isEmptyField(i-1, j+1))
							{
								//King moved here
							}
						}
					}
		    	}
		    }
		    j++;
		}
		i++;
		
		return true;
	}
	
	public boolean isEmptyField(int x, int y) throws InterruptedException, IOException
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

	
	public char findMyColor() throws InterruptedException, IOException
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
}
