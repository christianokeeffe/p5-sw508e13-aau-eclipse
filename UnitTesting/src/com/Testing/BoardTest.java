package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.rmi.RemoteException;
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
	public void testNewBoard() throws InterruptedException, IOException {
	    RemoteNXTFunctions testRemote= new RemoteNXTFunctions();
	    testRemote.analyzeTestVariable = 4;
	    Board testboard = new Board(testRemote);
	    
	    assertEquals(testboard.myBoard[0][1].getPieceOnField().color, 'w');
	    assertEquals(testboard.myBoard[0][5].getPieceOnField().color, 'r');
	    
	    
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
	 public void testResetVisited() {
	    checkersBoard.resetVisited();
	        for (Field[] f : checkersBoard.myBoard) {
	            for (Field field : f) {
	                assertTrue(field.visited == false);
	            }
	        }
	    }

    @Test
	public void testsortListOfFields() {
	    
	    List<Field> listToSort = new ArrayList<Field>();
	    Field tempFirst = new Field();
	    Field tempSecond = new Field();
	    Piece tempKing = new Piece(checkersBoard);
	    Piece tempPeasant = new Piece(checkersBoard);
	    emptyBoard();
	    tempFirst.x = 2;
	    tempFirst.y = 2;
	    tempSecond.x = 2;
	    tempSecond.y = 5;
	    
	    tempPeasant.color = 'w';
	    tempPeasant.isMoveable = true;
	    tempKing.color = 'b';
	    tempKing.isCrowned = true;
	    tempKing.canJump = true;
	    tempKing.isMoveable = true;
	    
	    tempFirst.setPieceOnField(tempPeasant);
	    tempSecond.setPieceOnField(tempKing);
	        
	    listToSort.add(tempFirst);
	    listToSort.add(tempSecond);
	    
	    checkersBoard.sortListOfFields(listToSort);
	    
	    assertEquals(listToSort.get(0).getPieceOnField().isCrowned, true);
	    assertEquals(listToSort.get(1).getPieceOnField().isCrowned, false);
	}
	
  
    @Test
    public void testIsGreater() {
        emptyBoard();
        Field tempInput = new Field();
        Field tempComp = new Field();
        Piece tempFirst = new Piece(checkersBoard);
        Piece tempSecond = new Piece(checkersBoard);
        tempFirst.color = 'w';
        tempFirst.canJump = true;
        tempFirst.isMoveable = true;
        tempFirst.isCrowned = false;
        
        tempSecond.color = 'w';
        tempSecond.isMoveable = true;
        tempSecond.canJump = false;
        tempSecond.isCrowned = false;
        
        tempInput.x = 3;
        tempInput.y = 2;
        tempInput.setPieceOnField(tempFirst);
        tempComp.x = 2;
        tempComp.y = 5;
        tempComp.setPieceOnField(tempSecond);
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 3, 2));
        tempInput.x = 4;
        tempInput.y = 1;
        tempInput.setPieceOnField(tempFirst);
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempFirst.color = 'r';
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempFirst.color = 'b';
        tempFirst.isCrowned = true;
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempFirst.canJump = false;
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempFirst.isCrowned = false;
        tempFirst.color = 'w';
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempComp.y = 1;
        tempComp.setPieceOnField(tempSecond);
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 4, 1));
        tempInput.y = 3;
        tempInput.setPieceOnField(tempFirst);
        assertFalse(checkersBoard.isGreater(tempInput, tempComp, 4, 3));
        tempSecond.isCrowned = true;    
        assertTrue(checkersBoard.isGreater(tempInput, tempComp, 5, 2));
        assertFalse(checkersBoard.isGreater(tempInput, tempComp, 3, 2));
    }
    
    @Test
    public void testverifyCorrectMove() throws InterruptedException, IOException {
        Field tempF = new Field();
        Piece tempP = new Piece(checkersBoard);
        
        tempF.x = 1;
        tempF.y = 4;
        tempP.isMoveable = true;
        tempP.color = 'w';
        tempF.setPieceOnField(tempP);
        
        assertTrue(checkersBoard.verifyCorrectMove(tempF));
        tempF.x = 2;
        tempF.y = 5;
        assertFalse(checkersBoard.verifyCorrectMove(tempF));
        tempP.isMoveable = false;
        assertTrue(checkersBoard.verifyCorrectMove(tempF));
        tempF.getPieceOnField().color = 'r';
        assertTrue(checkersBoard.verifyCorrectMove(tempF));  
    }
    
    @Test
    public void testpeasentIsOnEndRow() {
        Field tempF = new Field();
        Piece tempP = new Piece(checkersBoard);
        
        tempP.color = 'w';
        tempP.isCrowned = false;
        tempF.x = 5;
        tempF.y = 0;
        tempF.setPieceOnField(tempP);
        
        assertTrue(checkersBoard.peasentIsOnEndRow(tempF));
        tempP.isCrowned = true;
        assertFalse(checkersBoard.peasentIsOnEndRow(tempF));
        tempF.y = 7;
        assertFalse(checkersBoard.peasentIsOnEndRow(tempF));
        tempP.isCrowned = false;
        tempP.color = 'r';
        assertTrue(checkersBoard.peasentIsOnEndRow(tempF));
        tempP.isCrowned = true;
        assertFalse(checkersBoard.peasentIsOnEndRow(tempF));
        tempF.y = 0;
        assertFalse(checkersBoard.peasentIsOnEndRow(tempF));
        tempF.emptyThisField();
        assertFalse(checkersBoard.peasentIsOnEndRow(tempF));
        
    }
    
    @Test
    public void testIsFieldEmptyOnBoard() throws InterruptedException, IOException {
        Field tempF = new Field();

        tempF.x = 2;
        tempF.y = 5;
        
        assertTrue(checkersBoard.isFieldEmptyOnBoard(tempF.x, tempF.y));
        tempF.x = 0;
        tempF.y = 1;
        assertFalse(checkersBoard.isFieldEmptyOnBoard(tempF.x, tempF.y));
        tempF.x = -1;
        assertFalse(checkersBoard.isFieldEmptyOnBoard(tempF.x, tempF.y));
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
	

    @Test
    public void testUpdateMoveables() {
        emptyBoard();
        checkersBoard.myBoard[2][1].setPieceOnField(producePiece(2,1,'b',true));
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(3,2,'w',false));
        
        checkersBoard.updateMoveables();
        assertTrue(checkersBoard.myBoard[2][1].getPieceOnField().canJump);
    }
	
	@Test
	public void testFindMissingPiece() throws InterruptedException, IOException {
	    checkersBoard.findMissingPiece();
	    assertEquals(checkersBoard.myBoard[0][1].getPieceOnField().color, 'r');
	    assertEquals(checkersBoard.myBoard[1][4].getPieceOnField().color, 'w');
	}

}
