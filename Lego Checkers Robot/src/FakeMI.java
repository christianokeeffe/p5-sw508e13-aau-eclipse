import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;


//This is a test class to emulate a MI, not suppose to be part of the release
public class FakeMI{
	Random numberGen;
	List<Field> jumpList;
	List<Field> moveList;
	List<Field> jumpPath;
	RemoteNXTFunctions NXT;

	FakeMI()
	{
		numberGen = new Random();
		jumpList = new ArrayList<Field>();
		moveList = new ArrayList<Field>();
		try {
			NXT = new RemoteNXTFunctions();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		updateList();
	}

	public void updateList()
	{
		moveList.clear();
		jumpList.clear();
		for(Field[] arrayField : NXT.checkersBoard.myBoard)
		{
			for(Field field : arrayField)
			{
				if(field.getPieceOnField() != null){
					if(NXT.checkersBoard.checkAllegiance(field, false))
					{
						Piece temp = field.getPieceOnField();
						if(temp.isMoveable)
						{
							moveList.add(field);
						}
						if(temp.canJump)
						{
							Delay.msDelay(500);
							LCD.clear();
							LCD.drawInt(temp.x, 0, 0);
							LCD.drawInt(temp.y, 0, 1);
							LCD.refresh();
							Delay.msDelay(1500);
							jumpList.add(field);
						}
					}
				}
			}
		}
	}

	public void decideMovement() throws Exception
	{
		updateList();
		if(!jumpList.isEmpty())
		{
			int jtemp = numberGen.nextInt(jumpList.size());
			CalculateJump(jumpList.get(jtemp));
		}
		else
		{
			if(!moveList.isEmpty())
			{
				int mtemp = numberGen.nextInt(moveList.size());
				Move(moveList.get(mtemp));
			}
			else
			{
				LCD.clear();
				LCD.drawString("ERROR press enter", 0, 0);
				LCD.refresh();
				Button.ENTER.waitForPress();
			}
		}

	}

	private void Move(Field f) throws Exception
	{
		if(!f.getPieceOnField().isCrowned)
		{
			if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + 1) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + 1))
			{
				int random = numberGen.nextInt(2);
				if(random == 0)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y+1]);
				}
				else if(random == 1)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y+1]);
				}
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y+1]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y+1]);
			}
		}
		else if(f.getPieceOnField().isCrowned)
		{
			if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + 1) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + 1) && !NXT.checkersBoard.fieldOccupied(f.x + 1, f.y - 1) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y - 1))
			{
				int random = numberGen.nextInt(4);
				if(random == 0)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y+1]);
				}
				else if(random == 1)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y+1]);
				}
				else if(random == 3)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y-1]);
				}
				else if(random == 4)
				{
					NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y-1]);
				}
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y+1]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y+1]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y - 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x+1][f.y-1]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y - 1))
			{
				NXT.movePiece(f, NXT.checkersBoard.myBoard[f.x-1][f.y-1]);
			}
		}
	}

	private boolean CalculateJump(Field f) throws Exception
	{
		Delay.msDelay(500);
		LCD.clear();
		LCD.drawString("jumper", 0, 0);
		LCD.refresh();
		Delay.msDelay(1000);

		List<Field> jumpPath = new ArrayList<Field>();
		jumpPath = Jump(f, jumpPath,f.getPieceOnField());
		if(!jumpPath.isEmpty())
		{
			NXT.takePiece(f, jumpPath);
			jumpPath.clear();
			return true;
		}
		else
		{
			Delay.msDelay(500);
			LCD.clear();
			LCD.drawString("not jumping", 0, 0);
			LCD.refresh();
			Delay.msDelay(1000);
			return false;
		}
	}

	private boolean checkList(List<Field> lf, int x, int y)
	{
		boolean contain = false;
		for(Field f : lf)
		{
			if(f.x == x && f.y == y)
			{
				contain = true;
			}
		}
		return contain;
	}
	
	private boolean checkColor(int x, int y, boolean oppenentColor)
	{
		if(x <= 7 && x >= 0 && y <= 7 && y >= 0)
		{
			return NXT.checkersBoard.checkAllegiance(NXT.checkersBoard.myBoard[x][y],oppenentColor);
		}
		else
		{
			return false;
		}
	}

	private List<Field> Jump(Field f, List<Field> lf, Piece orginalPiece)
	{
		Delay.msDelay(500);
		LCD.clear();
		LCD.drawString("tester", 0, 0);
		LCD.refresh();
		Delay.msDelay(1000);
		if(checkColor(f.x-1,f.y+1, true) && !NXT.checkersBoard.fieldOccupied(f.x-2, f.y+2) && !checkList(lf,f.x-2,f.y+2))
		{
			Delay.msDelay(500);
			LCD.clear();
			LCD.drawString("1", 0, 0);
			LCD.refresh();
			Delay.msDelay(1000);
			lf.add(NXT.checkersBoard.myBoard[f.x-2][f.y+2]);
			return Jump(NXT.checkersBoard.myBoard[f.x-2][f.y+2],lf,orginalPiece);
		}
		else if(checkColor(f.x+1,f.y+1, true) && !NXT.checkersBoard.fieldOccupied(f.x+2, f.y+2) && !checkList(lf,f.x+2,f.y+2))
		{
			Delay.msDelay(500);
			LCD.clear();
			LCD.drawString("2", 0, 0);
			LCD.refresh();
			Delay.msDelay(1000);
			lf.add(NXT.checkersBoard.myBoard[f.x+2][f.y+2]);
			return Jump(NXT.checkersBoard.myBoard[f.x+2][f.y+2],lf,orginalPiece);
		}
		if(orginalPiece.isCrowned)
		{
			if(checkColor(f.x-1,f.y-1, true) && !NXT.checkersBoard.fieldOccupied(f.x-2, f.y-2) && !checkList(lf,f.x-2,f.y-2))
			{
				Delay.msDelay(500);
				LCD.clear();
				LCD.drawString("3", 0, 0);
				LCD.refresh();
				Delay.msDelay(1000);
				lf.add(NXT.checkersBoard.myBoard[f.x-2][f.y-2]);
				return Jump(NXT.checkersBoard.myBoard[f.x-2][f.y-2],lf,orginalPiece);
			}
			else if(checkColor(f.x+1,f.y-1, true) && !NXT.checkersBoard.fieldOccupied(f.x+2, f.y-2) && !checkList(lf,f.x+2,f.y-2))
			{
				Delay.msDelay(500);
				LCD.clear();
				LCD.drawString("4", 0, 0);
				LCD.refresh();
				Delay.msDelay(1000);
				lf.add(NXT.checkersBoard.myBoard[f.x+2][f.y-2]);
				return Jump(NXT.checkersBoard.myBoard[f.x+2][f.y-2],lf,orginalPiece);
			}
		}

		Delay.msDelay(500);
		LCD.clear();
		LCD.drawString("tester tester", 0, 0);
		LCD.refresh();
		Delay.msDelay(1000);
		return lf;
	}
}
