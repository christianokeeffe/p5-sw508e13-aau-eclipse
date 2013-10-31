import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import customExceptions.NoKingLeft;
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
	//Each of the factor variables determines how far the motor associated with that axis will move. 
	private static final int yFactor = -268;
	private static final int xFactor = -228;
	private static final int zFactor = 674;
	//The displacement is multiplied with the yFactor to place the magnet on the right field.
	private static final double displacementFactorY = 4.7;
	private static final int displacementX = 1;
	private int presentY = 0;
	private int presentX = 0;
	private int presentZ = 0;
	private TouchSensor touchSensorX;
	private TouchSensor touchSensorZ;
	private TouchSensor touchSensorY1;
	private TouchSensor touchSensorY2;
	public ColorSensor boardColorSensor;
	Board checkersBoard;
	NXTMotor electromagnet;
	private NXTRegulatedMotor motorZ;
	private NXTRegulatedMotor motorX;
	Field trashField = new Field(3,-6);

	public RemoteNXTFunctions() throws InterruptedException, IOException{
		connect();
		motorZ = new NXTRegulatedMotor(MotorPort.A);
		motorX = new NXTRegulatedMotor(MotorPort.B);
		motorZ.setSpeed(150);
		motorX.setSpeed(900);

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

	//The Color sensor is calibrated
	public void initColorSensor() throws IOException
	{
		getColorOnField(0, 3);
		Delay.msDelay(250);
		boardColorSensor.calibrateLow();;
		Delay.msDelay(250);
		getColorOnField(0, 2);
		Delay.msDelay(250);
		boardColorSensor.calibrateHigh();;
		Delay.msDelay(250);

	}
	
	public void waitForRedButton()
	{
		TouchSensor bigRedButton = new TouchSensor(bottomNXT.S3);
		boolean checkButton = true;
		while(checkButton)
		{
			if(bigRedButton.isPressed())
			{
				checkButton = false;
			}
		}
	}

	public Color getColorOnField (int x, int y) throws IOException{
		moveSensorTo(x, y, false);
		return boardColorSensor.getColor();
	}
	
	public void moveZTo(double pos)
	{
		motorZ.rotate((int)(pos*zFactor-presentZ), false);
		presentZ = (int)(pos*zFactor);
	}

	//latex start movePiece
	public void movePiece(Field FromField, Field ToField) throws IOException, NoKingLeft
	{
		moveSensorTo(FromField.x,FromField.y,true);
		Delay.msDelay(300);
		moveZTo(1);
		electromagnet.setPower(100);
		Delay.msDelay(100);
		if(ToField == trashField)
		{
			moveZTo(0);
		}
		else
		{
			moveZTo(0.5);
		}
		moveSensorTo(ToField.x,ToField.y,true);
		Delay.msDelay(300);
		if(ToField != trashField)
		{
			moveZTo(1);
		}
		electromagnet.setPower(0);
		moveZTo(0);
		
		checkersBoard.movePiece(FromField, ToField);
	}
	//latex end

	//Makes a piece jump one or more pieces and then remove those pieces from the board
	public void takePiece(Field fromField, List<Field> midwayFields) throws IOException, NoKingLeft
	{
		Field presentField = fromField;

		List<Field> takenPieces = new ArrayList<Field>();

		for(int i = 0; i < midwayFields.size(); i++)
		{
			Field jumpedField = movePieceOverField(presentField,midwayFields.get(i));
			if(jumpedField != null){
				takenPieces.add(jumpedField);
			}
			presentField = midwayFields.get(i);
		}

		for(int i = 0; i < takenPieces.size(); i++){
			movePiece(takenPieces.get(i), trashField);
		}
	}

	private Field movePieceOverField(Field fromField, Field toField) throws IOException, NoKingLeft{
		movePiece(fromField, toField);

		if(Math.abs(fromField.x - toField.x) == 2){
			Field returnField = new Field();
			returnField.x = (fromField.x + toField.x)/2;
			returnField.y = (fromField.y + toField.y)/2;
			return returnField;
		}
		else
		{
			return null;
		}
	}
	//latex start MoveSensor
	private void moveSensorTo(int x, int y, boolean goToMagnet) throws IOException
	{
		adjustAngleAxisX(x, goToMagnet);
		moveMotorsAxisY(y,goToMagnet);

		bottomNXT.A.waitComplete();
		bottomNXT.B.waitComplete();
		motorX.waitComplete();
	}
	//latex end

	private void moveMotorsAxisY(int y, boolean GoToMagnet)
	{
		int displacement = 0;
		if(GoToMagnet == true){
			displacement =  (int) (yFactor*displacementFactorY);
		}
		adjustAngleAxisY(y*yFactor-presentY+displacement);

		presentY = y*yFactor+displacement;
	}

	private void adjustAngleAxisX(int angle, boolean goToMagnet) throws IOException{
		int displacement = 0;
		if(goToMagnet){
			displacement = (xFactor - displacementX);
		}
		motorX.rotate(angle*xFactor-presentX+displacement, true);
		presentX = angle*xFactor+displacement;
	}

	//latex start slaveNXT
	private void adjustAngleAxisY(int angle){
		bottomNXT.A.rotate(angle, true);
		bottomNXT.B.rotate(angle,true);
	}
	//latex end

	private void startMotorsReset(){
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

	private void stopMotorsReset(){
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
	public void resetMotors(){
		startMotorsReset();
		while(!touchSensorX.isPressed() || !touchSensorZ.isPressed()|| !touchSensorY1.isPressed()|| !touchSensorY2.isPressed())
		{
			if(touchSensorX.isPressed()){
				motorX.stop();
			}
			if(touchSensorZ.isPressed()){
				motorZ.stop();
			}
			if(touchSensorY1.isPressed()){
				bottomNXT.A.stop();
			}
			if(touchSensorY2.isPressed()){
				bottomNXT.B.stop();
			}
		}
		presentY = 1070;
		presentX = 120;
		stopMotorsReset();
	}
	//latex start connect
	private void connect() throws InterruptedException{
		// Now connect
		try {
			LCD.clear();
			LCD.drawString("Connecting...",0,0);
			bottomNXT = new RemoteNXT("CheckBottom", Bluetooth.getConnector());
			LCD.clear();
			LCD.drawString("Connected",0,1);
			Thread.sleep(2000);
		} catch (IOException ioe) {
			LCD.clear();
			LCD.drawString("Conn Failed",0,0);
			Thread.sleep(2000);
			System.exit(1);
		}
	}
	//latex end
}
