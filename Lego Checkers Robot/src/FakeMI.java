import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import customExceptions.NoKingLeft;

//This is a test class to emulate a MI, not suppose to be part of the release
public class FakeMI{
	Random numberGen;
	int GLOBAL_y;
	boolean VHUMAN;
	List<Field> jumpList;
	List<Field> moveList;
	List<Field> jumpPath;
	RemoteNXTFunctions NXT;

	FakeMI(RemoteNXTFunctions inputNXT, boolean versusHuman){
		numberGen = new Random();
		jumpList = new ArrayList<Field>();
		moveList = new ArrayList<Field>();

		NXT = inputNXT;

		if(versusHuman){
			GLOBAL_y = 1;
			VHUMAN = true;
		}
		else{
			GLOBAL_y = -1;
			VHUMAN = false;
		}

		updateList();
	}

	public void updateList(){
		moveList.clear();
		jumpList.clear();
		for(Field[] arrayField : NXT.checkersBoard.myBoard)
		{
			for(Field field : arrayField)
			{
				if(field.getPieceOnField() != null){
					if(VHUMAN){
						if(NXT.checkersBoard.checkAllegiance(field, false))
						{
							Piece temp = field.getPieceOnField();
							if(temp.isMoveable)
							{
								moveList.add(field);
							}
							if(temp.canJump)
							{
								jumpList.add(field);
							}
						}
					}
					else{
						if(NXT.checkersBoard.checkAllegiance(field, true))
						{
							Piece temp = field.getPieceOnField();
							if(temp.isMoveable)
							{
								moveList.add(field);
							}
							if(temp.canJump)
							{
								jumpList.add(field);
							}
						}
					}
				}
			}
		}
	}

	public void decideMovement() throws IOException, NoKingLeft, InterruptedException{
		updateList();
		if(!jumpList.isEmpty())
		{
			int jtemp = numberGen.nextInt(jumpList.size());
			CalculateJump(jumpList.get(jtemp));
			NXT.checkersBoard.updateMoveables();
		}
		else
		{
			if(!moveList.isEmpty())
			{
				int mtemp = numberGen.nextInt(moveList.size());
				Move(moveList.get(mtemp));
				NXT.checkersBoard.updateMoveables();
			}
			else
			{
				NXT.checkersBoard.informer.nothingPossible();
			}
		}
	}

	private void callMove(Field from, Field to){
		try {
			NXT.doMove(new Move(from,to));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoKingLeft e) {
			e.printStackTrace();
		}
	}

	private void Move(Field f){
		if(!f.getPieceOnField().isCrowned){
			if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + GLOBAL_y)){
				int random = numberGen.nextInt(2);
				if(random == 0){
					callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y+GLOBAL_y]);
				}
				else if(random == 1){
					callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y+GLOBAL_y]);
				}
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y+GLOBAL_y]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y+GLOBAL_y]);
			}
		}
		else if(f.getPieceOnField().isCrowned){
			if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x + 1, f.y - GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x - 1, f.y - GLOBAL_y)){
				int random = numberGen.nextInt(4);
				if(random == 0){
					callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y+GLOBAL_y]);
				}
				else if(random == 1){
					callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y+GLOBAL_y]);
				}
				else if(random == 3){
					callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y-GLOBAL_y]);
				}
				else if(random == 4){
					callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y-GLOBAL_y]);
				}
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y + GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y+GLOBAL_y]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y + GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y+GLOBAL_y]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x + 1, f.y - GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x+1][f.y-GLOBAL_y]);
			}
			else if(!NXT.checkersBoard.fieldOccupied(f.x - 1, f.y - GLOBAL_y)){
				callMove(f, NXT.checkersBoard.myBoard[f.x-1][f.y-GLOBAL_y]);
			}
		}
	}

	private void CalculateJump(Field f) throws IOException, NoKingLeft, InterruptedException
	{
		List<Stack<Field>> jumpPath = new ArrayList<Stack<Field>>();
		if(f.getPieceOnField()!= null){
			jumpPath = NXT.checkersBoard.jumpSequence(f, VHUMAN, f.getPieceOnField().isCrowned);
			NXT.checkersBoard.resetVisited();
			if(jumpPath.size() == 1){
				NXT.doMove(new Move(jumpPath.get(0)));
			}
			else if(jumpPath.size() > 1){
				NXT.doMove(new Move(jumpPath.get(numberGen.nextInt(jumpPath.size() - 1))));
			}
			else{
				LCD.clear();
				LCD.drawString("stack er tom", 0, 0);
				LCD.refresh();
				Button.ENTER.waitForPress();
			}
		}
	}
}
