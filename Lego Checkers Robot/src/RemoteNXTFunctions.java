import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import custom.Exceptions.NoKingLeft;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteNXT;
import lejos.robotics.Color;
import lejos.util.Delay;


public class RemoteNXTFunctions {
    RemoteNXT bottomNXT = null;
    //Each of the factor variables determines
    //how far the motor associated with that axis will move.
    private static final int Y_FACTOR = -268;
    private static final int X_FACTOR = -228;
    private static final int Z_FACTOR = 674;
    //The displacement is multiplied with the
    //Y_FACTOR to place the magnet on the right field.
    private static final double DISPLACEMENT_FACTOR_Y = 4.7;
    private int presentY = 0;
    private int presentX = 0;
    private int presentZ = 0;
    private TouchSensor touchSensorX;
    private TouchSensor touchSensorZ;
    private TouchSensor touchSensorY1;
    private TouchSensor touchSensorY2;
    private ColorSensor boardColorSensor;
    public Board checkersBoard;
    private NXTMotor electromagnet;
    private NXTRegulatedMotor motorZ;
    private NXTRegulatedMotor motorX;
    private TouchSensor bigRedButton;

    public RemoteNXTFunctions() throws InterruptedException, IOException {
        connect();
        motorZ = new NXTRegulatedMotor(MotorPort.A);
        motorX = new NXTRegulatedMotor(MotorPort.B);
        motorZ.setSpeed(150);
        motorX.setSpeed(900);

        bigRedButton = new TouchSensor(bottomNXT.S3);
        touchSensorX = new TouchSensor(bottomNXT.S1);
        touchSensorZ = new TouchSensor(SensorPort.S2);
        touchSensorY1 = new TouchSensor(bottomNXT.S2);
        touchSensorY2 = new TouchSensor(bottomNXT.S4);
        boardColorSensor = new ColorSensor(SensorPort.S1);
        electromagnet = new NXTMotor(MotorPort.C);
        resetMotors();
        initColorSensor();
        checkersBoard = new Board(this);
    }
    //function for creating a piece
    protected Piece producePiece(char color, boolean upgrade){
        Piece temp = new Piece(checkersBoard);
        temp.color = color;
        if (upgrade) {
            temp.isMoveable = true;
            temp.isCrowned = true;
            temp.canJump = true;
        }

        return temp;
    }

    public final void emptyBoard() {
        for (Field[] aF : checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }

    }
    //The Color sensor is calibrated
    public final void initColorSensor() throws IOException {
        getColorOnField(0, 3);
        Delay.msDelay(250);
        boardColorSensor.calibrateLow();
        Delay.msDelay(250);
        getColorOnField(0, 2);
        Delay.msDelay(250);
        boardColorSensor.calibrateHigh();
        Delay.msDelay(250);
    }

    public final void waitForRedButton() {
        boolean checkButton = true;
        while (checkButton) {
            if (bigRedButton.isPressed()) {
                checkButton = false;
            }
        }
    }

    public final Color getColorOnField(int x, int y) throws IOException {
        moveSensorTo(x, y, false);
        return boardColorSensor.getColor();
    }

    private void moveZTo(double pos) {
        motorZ.rotate((int) (pos * Z_FACTOR - presentZ), false);
        presentZ = (int) (pos * Z_FACTOR);
    }

    public final void resetAfterMove() throws IOException {
        moveSensorTo(1, -2, false);
    }

    //latex start movePieceRemote
    private void movePiece(Field fromField, Field toField)
            throws IOException, NoKingLeft {
        moveSensorTo(fromField.x, fromField.y, true);
        Delay.msDelay(500);
        moveZTo(1);
        electromagnet.setPower(100);
        Delay.msDelay(100);
        moveZTo(0);
        Delay.msDelay(100);
        moveSensorTo(toField.x, toField.y, true);
        Delay.msDelay(500);
        moveZTo(1);
        electromagnet.setPower(0);
        moveZTo(0);

        checkersBoard.movePieceInRepresentation(fromField, toField, false);
    }
    //latex end

    public final void trashPieceOnField(Field field)
            throws IOException, NoKingLeft {
            if (field.getPieceOnField().isCrowned) {
                int j = checkersBoard.kingPlace.length - 1;
                OUTERMOST: while (j >= 0) {
                    if (checkersBoard.kingPlace[j].isEmpty()) {
                        break OUTERMOST;
                    }
                    j--;
                }
                movePiece(field, checkersBoard.kingPlace[j]);
            } else {
                int l = checkersBoard.trashPlace.length - 1;
                int h = checkersBoard.trashPlace[0].length - 1;
                OUTER: while (h >= 0) {
                    while (l >= 0) {
                        if (checkersBoard.trashPlace[l][h].isEmpty()) {
                            break OUTER;
                        }
                        l--;
                    }
                    h--;
                    l = checkersBoard.trashPlace.length - 1;
                }
                movePiece(field, checkersBoard.trashPlace[l][h]);
            }
    }
    //latex start NXTDoMove
    public final void doMove(Move move) throws IOException, NoKingLeft {
        List<Field> takenPieces = new ArrayList<Field>();

        int stop = move.moves.size() - 1;
        for (int i = 0; i < stop; i++) {
            Field jumpedField = movePieceOverField(move.moves.get(i),
                                                   move.moves.get(i + 1));
            if (jumpedField != null) {
                takenPieces.add(jumpedField);
            }
        }

        for (int i = 0; i < takenPieces.size(); i++) {
            trashPieceOnField(takenPieces.get(i));
        }
    }
    //latex end

    private Field movePieceOverField(Field fromField, Field toField)
            throws IOException, NoKingLeft {
        movePiece(fromField, toField);

        if (checkersBoard.checkBounds(fromField.x, fromField.y)
                && Math.abs(fromField.x - toField.x) == 2) {
            return checkersBoard.myBoard[(fromField.x + toField.x) / 2]
                                        [(fromField.y + toField.y) / 2];
        } else {
            return null;
        }
    }
    //latex start MoveSensor
    private void moveSensorTo(int x, int y, boolean goToMagnet)
            throws IOException {
        adjustAngleAxisX(x);
        moveMotorsAxisY(y, goToMagnet);

        bottomNXT.A.waitComplete();
        bottomNXT.B.waitComplete();
        motorX.waitComplete();
    }
    //latex end

    private void moveMotorsAxisY(int y, boolean goToMagnet) {
        int displacement = 0;
        if (goToMagnet) {
            displacement =  (int) (Y_FACTOR * DISPLACEMENT_FACTOR_Y);
        }
        adjustAngleAxisY(y * Y_FACTOR - presentY + displacement);

        presentY = y * Y_FACTOR + displacement;
    }

    private void adjustAngleAxisX(int angle)
            throws IOException {
        motorX.rotate(angle * X_FACTOR - presentX, true);
        presentX = angle * X_FACTOR;
    }

    //latex start slaveNXT
    private void adjustAngleAxisY(int angle) {
        bottomNXT.A.rotate(angle, true);
        bottomNXT.B.rotate(angle, true);
    }
    //latex end

    private void startMotorsReset() {
        bottomNXT.A.setAcceleration(6000);
        bottomNXT.B.setAcceleration(6000);
        motorZ.setSpeed(150);
        motorX.setSpeed(200);
        bottomNXT.A.setSpeed(200);
        bottomNXT.B.setSpeed(200);

        motorX.backward();
        bottomNXT.A.backward();
        bottomNXT.B.backward();
        motorZ.forward();
        Delay.msDelay(1000);

        motorZ.backward();
        motorX.forward();
        bottomNXT.A.forward();
        bottomNXT.B.forward();
    }

    private void stopMotorsReset() {
        motorX.stop();
        motorZ.stop();
        bottomNXT.A.stop();
        bottomNXT.B.stop();
        motorX.setSpeed(900);
        motorZ.setSpeed(700);
        bottomNXT.A.setSpeed(900);
        bottomNXT.B.setSpeed(900);


        bottomNXT.B.smoothAcceleration(true);
        bottomNXT.A.smoothAcceleration(true);
        bottomNXT.A.setAcceleration(300);
        bottomNXT.B.setAcceleration(300);
    }
    //Resets the motors to their starting positions
    public final void resetMotors() {
        startMotorsReset();
        while (!touchSensorX.isPressed() || !touchSensorZ.isPressed()
               || !touchSensorY1.isPressed() || !touchSensorY2.isPressed()) {
            if (touchSensorX.isPressed()) {
                motorX.stop();
            }
            if (touchSensorZ.isPressed()) {
                motorZ.stop();
            }
            if (touchSensorY1.isPressed()) {
                bottomNXT.A.stop();
            }
            if (touchSensorY2.isPressed()) {
                bottomNXT.B.stop();
            }
        }
        presentY = 1070;
        presentX = 120;
        stopMotorsReset();
    }
    //latex start connect
    private void connect() throws InterruptedException {
        // Now connect
        try {
            LCD.clear();
            LCD.drawString("Connecting...", 0, 0);
            bottomNXT = new RemoteNXT("CheckBottom", Bluetooth.getConnector());
            LCD.clear();
            LCD.drawString("Connected", 0, 1);
            Thread.sleep(2000);
        } catch (IOException ioe) {
            LCD.clear();
            LCD.drawString("Conn Failed", 0, 0);
            Thread.sleep(2000);
            System.exit(1);
        }
    }
    //latex end
}
