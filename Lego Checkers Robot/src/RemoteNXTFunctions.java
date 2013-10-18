import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private static final int yFactor = -345;
	private static final int xFactor = -300;
	private static final int zFactor = 1300;
	//The displacement is multiplied with the yFactor to place the magnet on the right field.
	private static final double displacementFactor = 3.2;
    private int presentY = (int)(-yFactor*2.60);
    private int presentX = 0;
    private TouchSensor touchSensorX;
    private TouchSensor touchSensorZ;
    private TouchSensor touchSensorY;
    public ColorSensor boardColorSensor;
    Board checkersBoard;
    NXTMotor electromagnet;
    private NXTRegulatedMotor motorZ;
    private NXTRegulatedMotor motorX;
    Field trashField = new Field();
	
	public RemoteNXTFunctions() throws InterruptedException, IOException{
		connect();
		motorZ = new NXTRegulatedMotor(MotorPort.A);
		motorX = new NXTRegulatedMotor(MotorPort.B);
		motorZ.setSpeed(300);
	    motorX.setSpeed(900);
	    motorZ.setAcceleration(3000);
	    motorX.setAcceleration(3000);
	       
	    touchSensorX = new TouchSensor(bottomNXT.S1);
	    touchSensorZ = new TouchSensor(SensorPort.S2);
	    touchSensorY = new TouchSensor(bottomNXT.S2);
	    boardColorSensor = new ColorSensor(SensorPort.S1);
	    electromagnet = new NXTMotor(MotorPort.C);
	    resetMotors();
	    initColorSensor();
	    checkersBoard = new Board(this);
	    trashField.y = -4;
		trashField.x = 3;
	}
	
	//The Color sensor is calibrated
	private void initColorSensor() throws IOException
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
	
	public Color getColorOnField (int x, int y) throws IOException{
		moveSensorTo(x, y, false);
		return boardColorSensor.getColor();
	}
	
	public void movePiece(Field FromField, Field ToField) throws Exception
	{
		moveSensorTo(FromField.x,FromField.y,true);
		motorZ.rotate(zFactor);
		electromagnet.setPower(100);
		motorZ.rotate(-(zFactor/2));
		moveSensorTo(ToField.x,ToField.y,true); 
		motorZ.rotate(zFactor/2);
		electromagnet.setPower(0);
		Delay.msDelay(500);
		resetMotorZ();
		checkersBoard.movePiece(FromField, ToField);
	}
	
	//Makes a piece jump one or move pieces and then remove those pieces from the board
	public void takePiece(Field fromField, List<Field> midwayFields) throws Exception
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
	
	private Field movePieceOverField(Field fromField, Field toField) throws Exception{
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
	
	private void moveSensorTo(int x, int y, boolean goToMagnet) throws IOException
	{
		adjustAngleAxisX(x, goToMagnet);
		moveMotorsAxisY(y,goToMagnet);

		bottomNXT.A.waitComplete();
		bottomNXT.B.waitComplete();
		motorX.waitComplete();
	}
	
	private void moveMotorsAxisY(int y, boolean GoToMagnet)
	{
		int displacement = 0;
		if(GoToMagnet == true){
			displacement =  (int) (yFactor*displacementFactor);
		}
		adjustAngleAxisY(y*yFactor-presentY+displacement);
		
		presentY = y*yFactor+displacement;
	}
	
	private void adjustAngleAxisX(int angle, boolean goToMagnet) throws IOException{
		motorX.rotate(angle*xFactor-presentX, true);
		presentX = angle*xFactor;
	}
	
	private void adjustAngleAxisY(int angle){
		bottomNXT.A.rotate(angle, true);
		bottomNXT.B.rotate(angle,true);
	}
	//latex start slaveNXT
	private void resetMotorZ(){
		motorZ.backward();
		while(!touchSensorZ.isPressed()){
				
			
		}motorZ.stop();
	}
	//latex end
	
	//Resets the motors to their starting positions
	private void resetMotors(){
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

		while(!touchSensorX.isPressed() || !touchSensorZ.isPressed()|| !touchSensorY.isPressed())
		{
			if(touchSensorX.isPressed()){
				motorX.stop();
			}
			if(touchSensorZ.isPressed()){
				motorZ.stop();
			}
			if(touchSensorY.isPressed()){
				bottomNXT.A.stop();
				bottomNXT.B.stop();
			}
		}
		motorX.stop();
		motorZ.stop();
		bottomNXT.A.stop();
		bottomNXT.B.stop();
		motorX.setSpeed(900);

		bottomNXT.A.setSpeed(900);
		bottomNXT.B.setSpeed(900);
		bottomNXT.A.setAcceleration(1);
		bottomNXT.B.setAcceleration(1);
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
