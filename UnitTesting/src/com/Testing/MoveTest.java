package com.Testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.OriginalFiles.Field;
import com.OriginalFiles.Move;

public class MoveTest {

    @Test
    public final void testMoveFieldFieldBoolean() {
        Field fromField = new Field(5, 4);
        Field toField = new Field(6, 5);

        Move myMove = new Move(fromField, toField, false);

        assertFalse(myMove.wasKingBefore);
        assertEquals(myMove.moves.get(0), fromField);
        assertEquals(myMove.moves.get(1), toField);
        assertEquals(myMove.takenPieces.size(), 0);
        assertFalse(myMove.isJump());
    }

    @Test
    public final void testMoveListOfFieldBoolean() {
        List<Field> listOfFields = new ArrayList<Field>();

        Field fromField = new Field(1, 0);
        Field toField = new Field(2, 1);

        listOfFields.add(fromField);
        listOfFields.add(toField);

        Move myMove = new Move(listOfFields, false);

        assertFalse(myMove.wasKingBefore);
        assertEquals(myMove.moves, listOfFields);
        assertFalse(myMove.isJump());

       assertEquals(myMove.takenPieces.size(), 0);
    }

    @Test
    public final void testMove() {
        Move myMove = new Move();

        assertEquals(myMove.moves.size(), 0);
        assertFalse(myMove.wasKingBefore);
        assertFalse(myMove.isJump());
    }

    @Test
    public final void testIsJump() {
        Field fromField = new Field(5, 4);
        Field toField = new Field(6, 5);

        Move myMove = new Move(fromField, toField, false);

        assertFalse(myMove.isJump());

        fromField = new Field(5, 4);
        toField = new Field(7, 6);

        myMove = new Move(fromField, toField, false);

        assertTrue(myMove.isJump());
    }

    @Test
    public final void testAddStep() {
        Field fromField = new Field(3, 4);
        Field toField = new Field(4, 5);

        Move myMove = new Move(fromField, toField, false);

        assertEquals(myMove.moves.size(), 2);

        Field newStep = new Field(5, 6);

        myMove.addStep(newStep);

        assertEquals(myMove.moves.size(), 3);
    }

}
