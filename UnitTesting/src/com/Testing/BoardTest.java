package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


import com.CustomClasses.FunktionForTesting;
//Remember that myBoard in board should be public and all properties in Piece should also be public
import com.OriginalFiles.*;

import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;

public class BoardTest extends FunktionForTesting {
	
	public BoardTest () throws InterruptedException, IOException
	{
	    super();
	}
	
	@Test
	//test for the Board class' constructor
	public void testBoard(){
		for(int x = 0; x < 8; x++){
			for(int y = 0; y < 8; y++){
				Field temp = checkersBoard.myBoard[x][y];
				if((x+y)%2 == 1)
				{
					assertTrue(temp.x == x);
					assertTrue(temp.y == y);
					assertTrue(temp.allowedField);
					
					//test if the robots piece is piece and have the right values
					if(y < 3)
					{
						assertNotNull(temp.getPieceOnField());
						assertTrue(temp.getPieceOnField().getX() == x);
						assertTrue(temp.getPieceOnField().getY() == y);
						assertEquals('r',temp.getPieceOnField().color);

						if(y == 2)
						{
							//test if the robots first row is movable and not kings
							assertTrue(temp.getPieceOnField().isMoveable);
							assertFalse(temp.getPieceOnField().isCrowned);
						}
					}

					//Same procedure for the human players pieces
					if(y > 4)
					{
						assertNotNull(temp.getPieceOnField());
						assertTrue(temp.getPieceOnField().getX() == x);
						assertTrue(temp.getPieceOnField().getY() == y);
						assertEquals('w',temp.getPieceOnField().color);
						
						if(y==5)
						{
							assertTrue(temp.getPieceOnField().isMoveable);
							assertFalse(temp.getPieceOnField().isCrowned);
						}
					}
					
					if(y >= 3 && y <= 4){
						assertNull(temp.getPieceOnField());
					}
				}
				else
				{
					//test if the all the white fields are not allowed to move on
					assertFalse(temp.allowedField);
				}
			}
		}
	}
	
	

	@Test
	public void testMovePiece() throws NoKingLeft, IOException, custom.Exceptions.NoKingLeft {
		Piece temp = checkersBoard.myBoard[2][5].getPieceOnField();
		
		//test if the representation is updated probably
		checkersBoard.movePieceInRepresentation(checkersBoard.myBoard[2][5], checkersBoard.myBoard[3][2], false);

		assertEquals(temp, checkersBoard.myBoard[3][2].getPieceOnField());
		assertNull(checkersBoard.myBoard[2][5].getPieceOnField());
		
		checkersBoard.movePieceInRepresentation(checkersBoard.myBoard[1][2], produceField(8,8), false);
		
		assertNull(checkersBoard.myBoard[1][2].getPieceOnField());
	}

	@Test
	public void testFieldOccupied() {
		//test to see whether this function return is occupied if the field has a piece or if it is outside the board
		assertTrue(checkersBoard.fieldOccupied(1, 0));
		assertFalse(checkersBoard.fieldOccupied(2, 3));
		assertTrue(checkersBoard.fieldOccupied(9, 9));
	}
	
	//function to produce a list of fields which are used for testing check moveable function
	private List<Field> produceList(Field field, int direction){
		List<Field> result = new ArrayList<Field>();
		
		if(field.y +direction <= 7 && field.y +direction >= 0){
			if(field.x == 0){
				result.add(checkersBoard.myBoard[field.x+1][field.y+direction]);
			}
			else if(field.x == 7){
				result.add(checkersBoard.myBoard[field.x-1][field.y+direction]);
			}
			else{
				result.add(checkersBoard.myBoard[field.x-1][field.y+direction]);
				result.add(checkersBoard.myBoard[field.x+1][field.y+direction]);
			}
		}
		
		return result;
	}	
	
	@Test
	public void testCheckMoveable() throws InterruptedException, IOException {
		//test whether this function results lists corresponding the to the valid moves
		List<Field> tempList = produceList(checkersBoard.myBoard[2][5],-1);
		
		assertEquals(checkersBoard.checkMoveable(checkersBoard.myBoard[2][5], -1),tempList);
		
		checkersBoard.myBoard[2][5].getPieceOnField().isCrowned = true;
		
		assertEquals(checkersBoard.checkMoveable(checkersBoard.myBoard[2][5], -1),tempList);
		
		tempList = produceList(checkersBoard.myBoard[0][5],-1);
		
		assertEquals(checkersBoard.checkMoveable(checkersBoard.myBoard[0][5], -1),tempList);
		
		tempList = produceList(checkersBoard.myBoard[7][2],1);
		
		assertEquals(checkersBoard.checkMoveable(checkersBoard.myBoard[7][2], 1),tempList);
		
		checkersBoard.myBoard[7][2].getPieceOnField().isCrowned = true;
		
		assertEquals(checkersBoard.checkMoveable(checkersBoard.myBoard[7][2], 1),tempList);
		
		resetBoard();
	}
	
	@Test
	public void testCheckJumpDirection() throws InterruptedException, IOException {
		//test if this function returns the fields allowed for a piece to jump too
	    checkersBoard.myBoard[3][4].setPieceOnField(producePiece(0,0,'r',false));
		
		assertEquals(checkersBoard.checkJumpDirection(checkersBoard.myBoard[4][5], -1, -1, false, false), checkersBoard.myBoard[2][3]);
		assertNull(checkersBoard.checkJumpDirection(checkersBoard.myBoard[4][5], 1, -1, false, false));
		
		checkersBoard.myBoard[3][4].getPieceOnField().isCrowned = true;
		checkersBoard.myBoard[4][3].setPieceOnField(producePiece(4, 3,'w',false));
		checkersBoard.myBoard[5][2].emptyThisField();
		
		assertEquals(checkersBoard.checkJumpDirection(checkersBoard.myBoard[3][4], 1, -1, true, true), checkersBoard.myBoard[5][2]);
		assertNull(checkersBoard.checkJumpDirection(checkersBoard.myBoard[3][4], -1, -1, true, true));
		
		resetBoard();
	}

	@Test
	public void testCheckAllegiance() {
		//Test for humans pieces
		assertTrue(checkersBoard.checkAllegiance(checkersBoard.myBoard[4][5], true));
		assertFalse(checkersBoard.checkAllegiance(checkersBoard.myBoard[4][5], false));
		
		//Test for robots pieces
		assertTrue(checkersBoard.checkAllegiance(checkersBoard.myBoard[2][1], false));
		assertFalse(checkersBoard.checkAllegiance(checkersBoard.myBoard[2][1], true));
		
		Field checkField = produceField(9,9);
		
		//Test for out for bounds
		assertFalse(checkersBoard.checkAllegiance(checkField,true));
		assertFalse(checkersBoard.checkAllegiance(checkField,false));
	}

}
