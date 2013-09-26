import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;


public class Board {
	
	//List<List<Field>> myBoard = new ArrayList<List<Field>>();
	Field[][] myBoard = new Field[8][8];
	
	char myColor;
	RemoteNXTFunctions remoteFunctions;
	
	int globa_y = 0;
	
	public Board(RemoteNXTFunctions remoteFunc) throws InterruptedException, IOException
	{
		remoteFunctions = remoteFunc;
		myColor = findMyColor();
		
		
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
						temp.pieceColor = this.findOpponentColor();
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
	
	
	public boolean analyzeBoard() throws InterruptedException, IOException
	{			
		myBoard[0][5].isKing= true;
		
		for (Field[] f : myBoard) 
		{
			for (Field field : f) 
			{
				
				if(field.moveable && field.pieceColor != myColor)
				{
					LCD.drawString("x: " + field.x + " y: " + field.y, 0, globa_y);
					LCD.refresh();
					globa_y++;
					
					if(this.isEmptyField(field))
					{
						
						if(field.isKing)
						{
							if(this.checkKingMove(field)){
								
							}
						}
						else
						{	
							if(this.checkPeasantMove(field))
							{
								//Break the loop
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void movePiece(Field FromField, int toField_x, int toField_y) throws InterruptedException, IOException
	{
		
		if((toField_x >= 0 && toField_x <= 7) && (toField_y >= 0 && toField_y <= 7))
		{
			myBoard[toField_x][toField_y].isKing = FromField.isKing;
			myBoard[toField_x][toField_y].pieceColor = FromField.pieceColor;
			
			myBoard[FromField.x][FromField.y].isKing = false;
			myBoard[FromField.x][FromField.y].pieceColor = ' ';
			
			
			//this.updatePeasantMoveables(a, b, x, y);
		}
		else
		{
			myBoard[FromField.x][FromField.y].isKing = false;
			myBoard[FromField.x][FromField.y].pieceColor = ' ';
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
	
	
	private boolean checkPeasantMove(Field field) throws InterruptedException, IOException
	{
		if((field.x > 0 && field.x < 7) && (field.y > 0 && field.y <= 7))
		{
			if(myBoard[field.x-1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y-1))
			{
				movePiece(field, field.x-1, field.y-1);
				return true;
			}
			else if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
		}
		else if(field.x==0 && field.y==7)
		{
			if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
		}
		else if(field.x != 0 && field.y == 0)
		{
			upgradeKing(field);
			return true;
		}
		else if(field.x == 0 && field.y!=0 && field.y!= 7)
		{
			if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
		}
		else if(field.x == 7 && field.y!=0 && field.y!= 7)
		{
			if(myBoard[field.x-1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y-1))
			{
				movePiece(field, field.x-1, field.y-1);
				return true;
			}
		}
		return false;
	}
	
	
	private boolean checkKingMove(Field field) throws InterruptedException, IOException
	{
		if((field.x > 0 && field.x < 7) && (field.y > 0 && field.y < 7))
		{
			if(myBoard[field.x-1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y-1))
			{
				movePiece(field, field.x-1, field.y-1);
				return true;
			}
			else if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
			else if(myBoard[field.x+1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y+1))
			{
				movePiece(field, field.x+1, field.y+1);
				return true;
			}
			else if(myBoard[field.x-1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y+1))
			{
				movePiece(field, field.x-1, field.y+1);
				return true;
			}
		}
		else if(field.x==7 && field.y==0)
		{
			if(myBoard[field.x-1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y+1))
			{
				movePiece(field, field.x-1, field.y+1);
				return true;
			}
		}
		else if(field.x==0 && field.y==7)
		{
			if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
		}
		else if(field.x == 0 && field.y!=0 && field.y!= 7)
		{
			if(myBoard[field.x+1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y+1))
			{
				movePiece(field, field.x+1, field.y+1);
				return true;
			}
			if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
		}
		else if(field.y == 0 && field.x!=0 && field.x!= 7)
		{
			if(myBoard[field.x+1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y+1))
			{
				movePiece(field, field.x+1, field.y+1);
				return true;
			}
			if(myBoard[field.x-1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y+1))
			{
				movePiece(field, field.x-1, field.y+1);
				return true;
			}
		}
		else if(field.y == 7 && field.x!=0 && field.x!= 7)
		{
			if(myBoard[field.x+1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x+1, field.y-1))
			{
				movePiece(field, field.x+1, field.y-1);
				return true;
			}
			if(myBoard[field.x-1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y-1))
			{
				movePiece(field, field.x-1, field.y-1);
				return true;
			}
		}
		else if(field.x == 7 && field.y!=0 && field.y!= 7)
		{
			if(myBoard[field.x-1][field.y-1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y-1))
			{
				movePiece(field, field.x-1, field.y-1);
				return true;
			}
			if(myBoard[field.x-1][field.y+1].pieceColor != ' ' && !this.isEmptyField(field.x-1, field.y+1))
			{
				movePiece(field, field.x-1, field.y+1);
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
	
	private boolean isEmptyField(Field inputField) throws InterruptedException, IOException
	{	
		ColorSensor.Color colorResult = remoteFunctions.GetColorOnField(inputField.x, inputField.y);
		
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
	
	/*
	private void updatePeasantMoveables(int i, int j, int x, int y) throws InterruptedException, IOException
	{
		LCD.drawString("Field : x:" + x + " y:" + y, 0 , globa_y);
		LCD.refresh();
		globa_y++;
		
		LCD.drawString("Field : i:" + i + " i:" + j, 0 , globa_y);
		LCD.refresh();
		globa_y++;
		
		if((i>0 && i<7) && (j > 0 && j<7))
		{
			//Check forward
			
			if(this.isEmptyField(j-1, i-1))
			{
				//bla
			}
			
			if(this.isEmptyField(j+1, i-1))
			{
				//bla
			}
		}

		//from x,y to i,j
		if((i > 0 && i < 7) && (j > 0 && j < 7))
		{
			//Checking forward
			if(this.isEmptyField(i-1, j-1)  || this.isEmptyField(i+1, j-1))
			{
				myBoard.get(i).get(j).moveable = true;
			}
			
			LCD.drawString("Field : x:" + y + " y:" + x, 0 , globa_y);
			LCD.refresh();
			globa_y++;
			
			LCD.drawString("Field : i:" + i + " j:" + j, 0 , globa_y);
			LCD.refresh();
			globa_y++;		
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
*/
	
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
	
	/*
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
	*/
	
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
							myBoard[i][j].pieceColor = ' ';
							myBoard[i][j].isKing = false;
						}
					}
				}
			}
		}
	}
	
}
