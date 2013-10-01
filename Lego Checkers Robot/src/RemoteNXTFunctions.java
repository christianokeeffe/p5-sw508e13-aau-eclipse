import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.Delay;


public class RemoteNXTFunctions {
	RemoteNXT bottomNXT = null;
	private static final int yFactor = -345;
	private static final int xFactor = -300;
	private static final int zFactor = 230;
	private static final double displacementFactor = 3.2;
    private int presentY = (int)(-yFactor*2.75);
    private int presentX = 0;
    private TouchSensor touchSensorX;
    private TouchSensor touchSensorZ;
    private TouchSensor touchSensorY;
    private ColorSensor boardColorSensor;
    Board checkersBoard;
    NXTMotor electromagnet;
    NXTRegulatedMotor motorX = Motor.A;
    NXTRegulatedMotor motorZ = Motor.B;
    RemoteMotor motorYLeft = bottomNXT.A;
    RemoteMotor motorYRight = bottomNXT.B;
    Field trashField = new Field();
	
	public RemoteNXTFunctions() throws InterruptedException, IOException{
		connect();
		/*Motor.A.setSpeed(100);  xAxis motor 
	    Motor.B.setSpeed(1000);  zAxis motor 
		bottomNXT.A.setSpeed(400);  yAxis motors 
		bottomNXT.B.setSpeed(400);  yAxis motors 
		bottomNXT.A.setAcceleration(1000);
	    bottomNXT.B.setAcceleration(1000); 
	    Motor.A.setAcceleration(3000);
	    Motor.B.setAcceleration(3000); */
		
		motorYLeft.setSpeed(400);
		motorYRight.setSpeed(400);
		motorYLeft.setAcceleration(3000);
		motorYRight.setAcceleration(3000);
		
		motorX.setSpeed(100);
		motorZ.setSpeed(1000);
	    motorX.setAcceleration(1000);
	    motorZ.setAcceleration(1000);
		
	    touchSensorX = new TouchSensor(bottomNXT.S1);
	    touchSensorZ = new TouchSensor(SensorPort.S2);
	    touchSensorY = new TouchSensor(bottomNXT.S2);
	    boardColorSensor = new ColorSensor(SensorPort.S1);
	    electromagnet = new NXTMotor(MotorPort.C);
	    resetMotors();
	    checkersBoard = new Board(this);
	    trashField.y = -4;
		trashField.x = 3;
	}
	
	public ColorSensor.Color getColorOnField (int x, int y) throws IOException{
		moveSensorTo(x, y, false);
		
		return boardColorSensor.getColor();
	}
	
	public void movePiece(Field FromField, Field ToField) throws IOException, InterruptedException
	{
		moveSensorTo(FromField.x,FromField.y,true);
		motorX.rotate(zFactor);
		electromagnet.setPower(100);
		motorX.rotate(-(zFactor/2));
		moveSensorTo(ToField.x,ToField.y,true); 
		motorX.rotate(zFactor/2);
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

		motorYLeft.waitComplete();
		motorYRight.waitComplete();
		motorZ.waitComplete();
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
		motorZ.rotate(angle*xFactor-presentX, true);
		presentX = angle*xFactor;
	}
	
	private void adjustAngleAxisY(int angle){
		motorYLeft.rotate(angle, true);
		motorYRight.rotate(angle,true);
	}
	
	private void resetMotorZ(){
		motorX.backward();
		while(!touchSensorZ.isPressed()){
			if(touchSensorZ.isPressed()){
				motorX.stop();
			}
		}
	}
	
	private void resetMotors(){
		motorZ.setSpeed(200);
		motorX.backward();
		motorZ.forward();
		motorYLeft.forward();
		motorYRight.forward();

		while(!touchSensorX.isPressed() || !touchSensorZ.isPressed()|| !touchSensorY.isPressed())
		{
			if(touchSensorX.isPressed()){
				motorZ.stop();
			}
			if(touchSensorZ.isPressed()){
				motorX.stop();
			}
			if(touchSensorY.isPressed()){
				motorYLeft.stop();
				motorYRight.stop();
			}
		}
		motorZ.stop();
		motorX.stop();
		motorYLeft.stop();
		motorYRight.stop();
		motorZ.setSpeed(1000);
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
