package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
//Remember that myBoard in board should be public and all properties in Piece should also be public
import com.OriginalFiles.*;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;

public class BoardTest {
	Board test = new Board();
	@Test
	//test for the Board class' constructor
	public void testBoard(){
		for(int x = 0; x < 8; x++){
			for(int y = 0; y < 8; y++){
				Field temp = test.myBoard[x][y];
				if((x+y)%2 == 1)
				{
					assertTrue(temp.x == x);
					assertTrue(temp.y == y);
					assertTrue(temp.allowedField);
					
					//test if the robots piece is piece and have the right values
					if(y < 3)
					{
						assertNotNull(temp.getPieceOnField());
						assertTrue(temp.getPieceOnField().x == x);
						assertTrue(temp.getPieceOnField().y == y);
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
						assertTrue(temp.getPieceOnField().x == x);
						assertTrue(temp.getPieceOnField().y == y);
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
	
	private void resetBoard() throws InterruptedException, IOException{
		test = new Board();
	}
	
	//Method used to construct a win scenario for either of the players
	private void constructWinCase(boolean change){
		for(Field[] i : test.myBoard){
			for(Field f : i){
				if(f.getPieceOnField() != null){
					if(change){
						if(f.getPieceOnField().color == 'r'){
							test.myBoard[f.x][f.y].getPieceOnField().color = 'w';
						}
					}
					else{
						if(f.getPieceOnField().color == 'w'){
							test.myBoard[f.x][f.y].getPieceOnField().color = 'r';
						}
					}
				}
			}
		}
	}	
	
	//Used to empty the entire board
	private void emptyBoard(){
		for(Field[] af : test.myBoard){
			for(Field f : af){
				f.emptyThisField();
			}
		}
	}
	
	@Test
	public void testGameIsEnded() throws InterruptedException, IOException {
		//test if the game is not ended with start positioning
		assertEquals(0, test.gameIsEnded(true));
		assertEquals(0, test.gameIsEnded(false));

		constructWinCase(true);
		//test if the game registers that the human have won independent of whos turn it is
		assertEquals(1, test.gameIsEnded(true));
		assertEquals(1, test.gameIsEnded(false));
		
		constructWinCase(false);
		//test if the game registers that the robot have won independent of whos turn it is
		assertEquals(2, test.gameIsEnded(true));
		assertEquals(2, test.gameIsEnded(false));
		
		emptyBoard();
		
		//test if the game ended when it is a draw when it is humans turn
		//test if the game is not ended when it is the robots turn
		test.myBoard[4][5].setPieceOnField(producePiece('b',true));
		test.myBoard[6][7].setPieceOnField(producePiece('g',true));
		
		assertEquals(3, test.gameIsEnded(true));
		assertNotEquals(3, test.gameIsEnded(false));
		
		emptyBoard();
		
		//test if the game ended when it is a draw when it is robots turn
		//test if the game is not ended when it is the human turn
		test.myBoard[1][0].setPieceOnField(producePiece('b',true));
		test.myBoard[1][2].setPieceOnField(producePiece('g',true));
		
		assertEquals(3, test.gameIsEnded(false));
		assertNotEquals(3, test.gameIsEnded(true));
		
		resetBoard();
	}

	@Test
	public void testCheckForGameHasEnded() throws InterruptedException, IOException {
		
		//test if the games ends in the different scenarios
		assertFalse(test.checkForGameHasEnded(true));
		assertFalse(test.checkForGameHasEnded(false));
		
		constructWinCase(true);
		
		assertTrue(test.checkForGameHasEnded(true));
		assertTrue(test.checkForGameHasEnded(false));
		
		constructWinCase(false);
		
		assertTrue(test.checkForGameHasEnded(true));
		assertTrue(test.checkForGameHasEnded(false));
		
		emptyBoard();
		
		//draw case if humans turn
		test.myBoard[4][5].setPieceOnField(producePiece('b',true));
		test.myBoard[6][7].setPieceOnField(producePiece('g',true));
		
		assertTrue(test.checkForGameHasEnded(true));
		assertFalse(test.checkForGameHasEnded(false));
		
		emptyBoard();
		
		//draw case if robots turn
		test.myBoard[1][0].setPieceOnField(producePiece('b',true));
		test.myBoard[1][2].setPieceOnField(producePiece('g',true));
		
		assertTrue(test.checkForGameHasEnded(false));
		assertFalse(test.checkForGameHasEnded(true));
		
		resetBoard();
	}
	
	@Test
	public void testAnalyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove{
		boolean testException = false;
		
		//test if analyzeboard works when moving a piece
		assertTrue(test.analyzeBoard());
		
		//production of an illegal move
		test.myBoard[3][4].setPieceOnField(producePiece('r',false));
		test.myBoard[2][5].setPieceOnField(producePiece('w',true));
		
		//test that analyzeBoard finds and reports the illegal move
		try {
			test.analyzeBoard();
		}
		catch (IllegalMove e) {
			testException = true;
		}
		
		assertTrue(testException);
		
		resetBoard();
	}
	
	//produces a field
	private Field produceField(int x, int y){
		Field temp = new Field();
		temp.x = x;
		temp.y = y;
		temp.allowedField = true;
		
		return temp;
	}

	@Test
	public void testMovePiece() throws NoKingLeft, IOException {
		Piece temp = test.myBoard[2][5].getPieceOnField();
		
		//test if the representation is updated probably
		test.movePiece(test.myBoard[2][5], test.myBoard[3][2]);

		assertEquals(temp, test.myBoard[3][2].getPieceOnField());
		assertNull(test.myBoard[2][5].getPieceOnField());
		
		test.movePiece(test.myBoard[1][2], produceField(8,8));
		
		assertNull(test.myBoard[1][2].getPieceOnField());
	}

	@Test
	public void testFieldOccupied() {
		//test to see whether this function return is occupied if the field has a piece or if it is outside the board
		assertTrue(test.fieldOccupied(1, 0));
		assertFalse(test.fieldOccupied(2, 3));
		assertTrue(test.fieldOccupied(9, 9));
	}
	
	//function to produce a list of fields which are used for testing check moveable function
	private List<Field> produceList(Field field, int direction){
		List<Field> result = new ArrayList<Field>();
		
		if(field.y +direction <= 7 && field.y +direction >= 0){
			if(field.x == 0){
				result.add(test.myBoard[field.x+1][field.y+direction]);
			}
			else if(field.x == 7){
				result.add(test.myBoard[field.x-1][field.y+direction]);
			}
			else{
				result.add(test.myBoard[field.x-1][field.y+direction]);
				result.add(test.myBoard[field.x+1][field.y+direction]);
			}
		}
		
		return result;
	}	
	
	@Test
	public void testCheckMoveable() throws InterruptedException, IOException {
		//test whether this function results lists corresponding the to the valid moves
		List<Field> tempList = produceList(test.myBoard[2][5],-1);
		
		assertEquals(test.checkMoveable(test.myBoard[2][5], -1),tempList);
		
		test.myBoard[2][5].getPieceOnField().isCrowned = true;
		
		assertEquals(test.checkMoveable(test.myBoard[2][5], -1),tempList);
		
		tempList = produceList(test.myBoard[7][2],1);
		
		assertEquals(test.checkMoveable(test.myBoard[7][2], 1),tempList);
		
		test.myBoard[7][2].getPieceOnField().isCrowned = true;
		
		assertEquals(test.checkMoveable(test.myBoard[7][2], 1),tempList);
		
		resetBoard();
	}
	
	//function for creating a piece
	private Piece producePiece(char color, boolean upgrade){
		Piece temp = new Piece();
		temp.color = color;
		
		if(upgrade){
			temp.isMoveable = true;
			temp.isCrowned = true;
		}
		
		return temp;
	}
	
	@Test
	public void testCheckJumpDirection() throws InterruptedException, IOException {
		//test if this function returns the fields allowed for a piece to jump too
		test.myBoard[3][4].setPieceOnField(producePiece('r',false));
		
		assertEquals(test.checkJumpDirection(test.myBoard[4][5], -1, -1, false), test.myBoard[2][3]);
		assertNull(test.checkJumpDirection(test.myBoard[4][5], 1, -1, false));
		
		test.myBoard[3][4].getPieceOnField().isCrowned = true;
		test.myBoard[4][3].setPieceOnField(producePiece('w',false));
		test.myBoard[5][2].emptyThisField();
		
		assertEquals(test.checkJumpDirection(test.myBoard[3][4], 1, -1, true), test.myBoard[5][2]);
		assertNull(test.checkJumpDirection(test.myBoard[3][4], -1, -1, true));
		
		resetBoard();
	}

	@Test
	//Test not that valid for the real GetColor function
	public void testGetColor() {
		assertEquals(test.myBoard[1][0].getPieceOnField().color, test.getColor(1, 0));
		assertEquals(' ',test.getColor(0, 3));
		assertEquals('w',test.getColor(1,4));
	}

	@Test
	public void testCheckAllegiance() {
		//Test for humans pieces
		assertTrue(test.checkAllegiance(test.myBoard[4][5], true));
		assertFalse(test.checkAllegiance(test.myBoard[4][5], false));
		
		//Test for robots pieces
		assertTrue(test.checkAllegiance(test.myBoard[2][1], false));
		assertFalse(test.checkAllegiance(test.myBoard[2][1], true));
		
		Field checkField = produceField(9,9);
		
		//Test for out for bounds
		assertFalse(test.checkAllegiance(checkField,true));
		assertFalse(test.checkAllegiance(checkField,false));
	}

}
