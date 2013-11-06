import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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

	public boolean decideMovement() throws IOException, NoKingLeft{
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
				NXT.checkersBoard.informer.nothingPossible();
			}
		}
		
		return VHUMAN;
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

	private void CalculateJump(Field f) throws IOException, NoKingLeft
	{
		List<Field> jumpPath = new ArrayList<Field>();
		jumpPath = Jump(f, jumpPath,f.getPieceOnField());
		if(!jumpPath.isEmpty()){
			Stack<Field> jumpSequence = new Stack<Field>();

			for(int i = jumpPath.size(); i >= 0; i--){
				jumpSequence.push(jumpPath.get(i));
			}

			NXT.doMove(new Move(jumpSequence));
		}
	}

	private boolean checkList(List<Field> lf, int x, int y){
		boolean contain = false;

		for(Field f : lf){
			if(f.x == x && f.y == y){
				contain = true;
			}
		}

		return contain;
	}

	private boolean checkColor(int x, int y)
	{
		if(x <= 7 && x >= 0 && y <= 7 && y >= 0){
			if(VHUMAN){
				return NXT.checkersBoard.checkAllegiance(NXT.checkersBoard.myBoard[x][y],true);
			}
			else{
				return NXT.checkersBoard.checkAllegiance(NXT.checkersBoard.myBoard[x][y],false);
			}
		}
		else{
			return false;
		}
	}

	private List<Field> Jump(Field f, List<Field> lf, Piece orginalPiece)
	{
		if(checkColor(f.x-1,f.y+GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x-2, f.y+(GLOBAL_y*2)) && !checkList(lf,f.x-2,f.y+(GLOBAL_y*2))){
			lf.add(NXT.checkersBoard.myBoard[f.x-2][f.y+(GLOBAL_y*2)]);
			return Jump(NXT.checkersBoard.myBoard[f.x-2][f.y+(GLOBAL_y*2)],lf,orginalPiece);
		}
		else if(checkColor(f.x+1,f.y+GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x+2, f.y+(GLOBAL_y*2)) && !checkList(lf,f.x+2,f.y+(GLOBAL_y*2))){
			lf.add(NXT.checkersBoard.myBoard[f.x+2][f.y+(GLOBAL_y*2)]);
			return Jump(NXT.checkersBoard.myBoard[f.x+2][f.y+(GLOBAL_y*2)],lf,orginalPiece);
		}
		if(orginalPiece.isCrowned){
			if(checkColor(f.x-1,f.y-GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x-2, f.y-(GLOBAL_y*2)) && !checkList(lf,f.x-2,f.y-(GLOBAL_y*2))){
				lf.add(NXT.checkersBoard.myBoard[f.x-2][f.y-(GLOBAL_y*2)]);
				return Jump(NXT.checkersBoard.myBoard[f.x-2][f.y-(GLOBAL_y*2)],lf,orginalPiece);
			}
			else if(checkColor(f.x+1,f.y-GLOBAL_y) && !NXT.checkersBoard.fieldOccupied(f.x+2, f.y-(GLOBAL_y*2)) && !checkList(lf,f.x+2,f.y-(GLOBAL_y*2))){
				lf.add(NXT.checkersBoard.myBoard[f.x+2][f.y-(GLOBAL_y*2)]);
				return Jump(NXT.checkersBoard.myBoard[f.x+2][f.y-(GLOBAL_y*2)],lf,orginalPiece);
			}
		}
		return lf;
	}
}
