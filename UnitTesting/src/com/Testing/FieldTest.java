package com.Testing;

import static org.junit.Assert.*;

import org.junit.Test;

import com.CustomClasses.FunktionForTesting;
import com.OriginalFiles.Field;
import com.OriginalFiles.Piece;

public class FieldTest extends FunktionForTesting{
    Field testField;
    @Test
    public void testField() {
        testField = new Field();
        testConstructor(testField, 0, 0);
    }

    @Test
    public void testFieldIntInt() {
        testField = new Field(1, 2);
        testConstructor(testField, 1, 2);
    }

    private void testConstructor(Field field, int x, int y){
        assertEquals(x, field.x);
        assertEquals(y, field.y);
        field.x = 2;
        field.y = 1;
        assertEquals(2, field.x);
        assertEquals(1, field.y);

        assertFalse(field.allowedField);
        field.allowedField = true;
        assertTrue(field.allowedField);

        assertNull(field.getPieceOnField());
        field.setPieceOnField(new Piece());
        assertNotNull(field.getPieceOnField());

        assertFalse(field.visited);
        field.visited = true;
        assertTrue(field.visited);
    }

    @Test
    public void testSetPieceOnField() {
        testField = new Field();
        assertNull(testField.getPieceOnField());
        testField.setPieceOnField(new Piece());
        testFieldsPieceEqPiece(testField, false);

        testField.setPieceOnField(producePiece(1, 2, 'r', true));

        testFieldsPieceEqPiece(testField, true);

        testField.x = 1;
        testField.y = 2;
        testField.setPieceOnField(producePiece(1, 2, 'r', true));
        testFieldsPieceEqPiece(testField, true);
    }

    private void testFieldsPieceEqPiece(Field PieceField, boolean testForTrue){
        Piece tempPiece = PieceField.getPieceOnField();

        assertEquals(PieceField.x, tempPiece.getX());
        assertEquals(PieceField.y, tempPiece.getY());

        if (testForTrue) {
            assertTrue(tempPiece.isCrowned);
            assertTrue(tempPiece.isMoveable);
            assertTrue(tempPiece.canJump);
        } else {
            assertFalse(tempPiece.isCrowned);
            assertFalse(tempPiece.isMoveable);
            assertFalse(tempPiece.canJump);
        }
    }

    @Test
    public void testGetPieceOnField() {
        testField = new Field();
        assertNull(testField.getPieceOnField());
        testField.setPieceOnField(producePiece(1, 2, 'r', true));
        testFieldsPieceEqPiece(testField, true);
    }

    @Test
    public void testIsEmpty() {
        testField = new Field();
        assertTrue(testField.isEmpty());

        testField.setPieceOnField(producePiece(0, 0, 'r', false));
        assertFalse(testField.isEmpty());

        testField.emptyThisField();
        assertTrue(testField.isEmpty());
    }

    @Test
    public void testIsPieceOfColor() {
        testField = new Field();
        testField.setPieceOnField(producePiece(0, 0, 'r', false));
        assertTrue(testField.isPieceOfColor('r'));
        assertFalse(testField.isPieceOfColor('w'));

        testField.getPieceOnField().color = 'g';
        assertFalse(testField.isPieceOfColor('b'));
        assertTrue(testField.isPieceOfColor('g'));
    }

    @Test
    public void testEmptyThisField() {
        testField = new Field();
        assertNull(testField.getPieceOnField());

        testField.setPieceOnField(producePiece(0, 0, 'r', false));
        assertNotNull(testField.getPieceOnField());

        testField.emptyThisField();
        assertNull(testField.getPieceOnField());
    }

}
