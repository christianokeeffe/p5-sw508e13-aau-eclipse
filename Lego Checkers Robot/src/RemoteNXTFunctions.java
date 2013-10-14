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
	private static final int yFactor = -345;
	private static final int xFactor = -300;
	private static final int zFactor = 230;
	private static final double displacementFactor = 3.2;
    private int presentY = (int)(-yFactor*2.70);
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
		motorZ.setSpeed(100);
	    motorX.setSpeed(1000);
	    motorZ.setAcceleration(3000);
	    motorX.setAcceleration(3000);
	    
		bottomNXT.A.setSpeed(400);/* Left yAxis motor */
		bottomNXT.B.setSpeed(400); /* Right yAxis motor */
		bottomNXT.A.setAcceleration(1000);
	    bottomNXT.B.setAcceleration(1000);
	       
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
	
	public void movePiece(Field FromField, Field ToField) throws IOException, InterruptedException
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
	
	public void takePiece(Field fromField, List<Field> midwayFields) throws IOException, InterruptedException
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
	
	private Field movePieceOverField(Field fromField, Field toField) throws IOException, InterruptedException{
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
		adjustAngleAxisX(x);
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
	
	private void adjustAngleAxisX(int angle) throws IOException{
		motorX.rotate(angle*xFactor-presentX, true);
		presentX = angle*xFactor;
	}
	
	private void adjustAngleAxisY(int angle){
		bottomNXT.A.rotate(angle, true);
		bottomNXT.B.rotate(angle,true);
	}
	
	private void resetMotorZ(){
		motorZ.backward();
		while(!touchSensorZ.isPressed()){
			if(touchSensorZ.isPressed()){
				motorZ.stop();
			}
		}
	}
	
	private void resetMotors(){
		motorX.setSpeed(200);
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
		motorX.setSpeed(1000);
	}
	
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
}
