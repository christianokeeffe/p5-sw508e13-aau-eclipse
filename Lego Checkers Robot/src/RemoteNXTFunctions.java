import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
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
    private TouchSensor xAxisTouchSensor;
    private TouchSensor zAxisTouchSensor;
    private TouchSensor yAxisTouchSensor;
    private ColorSensor boardColorSensor;
    Board checkersBoard;
    NXTMotor electromagnet;
    Field trashField = new Field();
	
	public RemoteNXTFunctions() throws InterruptedException, IOException{
		connect();
		bottomNXT.A.setSpeed(400);
		bottomNXT.B.setSpeed(400);
	    Motor.A.setSpeed(100);
	    Motor.B.setSpeed(1000);
	    bottomNXT.A.setAcceleration(1000);
	    bottomNXT.B.setAcceleration(1000);
	    Motor.A.setAcceleration(3000);
	    Motor.B.setAcceleration(3000);
	    xAxisTouchSensor = new TouchSensor(bottomNXT.S1);
	    zAxisTouchSensor = new TouchSensor(SensorPort.S2);
	    yAxisTouchSensor = new TouchSensor(bottomNXT.S2);
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
	
	public void takePiece(Field fromField, List<Field> fieldsToStopOnTheWay) throws IOException, InterruptedException
	{
		Field presentField = fromField;
		
		List<Field> takenPieces = new ArrayList<Field>();
		
		for(int i = 0; i < fieldsToStopOnTheWay.size(); i++)
		{
			Field jumpedField = movePieceOverField(presentField,fieldsToStopOnTheWay.get(i));
			if(jumpedField != null){
				takenPieces.add(jumpedField);
			}
			presentField = fieldsToStopOnTheWay.get(i);
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
		moveYAxisMotors(y,goToMagnet);

		bottomNXT.A.waitComplete();
		bottomNXT.B.waitComplete();
		Motor.B.waitComplete();
	}
	
	private void moveYAxisMotors(int y, boolean GoToMagnet)
	{
		int displacement = 0;
		if(GoToMagnet == true){
			displacement =  (int) (yFactor*displacementFactor);
		}
		adjustAngleAxisY(y*yFactor-presentY+displacement);
		
		presentY = y*yFactor+displacement;
	}
	
	public void movePiece(Field FromField, Field ToField) throws IOException, InterruptedException
	{
		moveSensorTo(FromField.x,FromField.y,true);
		Motor.A.rotate(zFactor);
		electromagnet.setPower(100);
		Motor.A.rotate(-(zFactor/2));
		moveSensorTo(ToField.x,ToField.y,true); 
		Motor.A.rotate(zFactor/2);
		electromagnet.setPower(0);
		Delay.msDelay(500);
		resetMotorZ();
		checkersBoard.movePiece(FromField, ToField);
	}
	
	private void adjustAngleAxisX(int angle) throws IOException{
		Motor.B.rotate(angle*xFactor-presentX, true);
		presentX = angle*xFactor;
	}
	
	private void adjustAngleAxisY(int angle){
		bottomNXT.A.rotate(angle, true);
		bottomNXT.B.rotate(angle,true);
	}
	
	private void resetMotorZ(){
		Motor.A.backward();
		while(!zAxisTouchSensor.isPressed()){
			if(zAxisTouchSensor.isPressed()){
				Motor.A.stop();
			}
		}
	}
	private void resetMotors(){
		Motor.B.setSpeed(200);
		Motor.A.backward();
		Motor.B.forward();
		bottomNXT.A.forward();
		bottomNXT.B.forward();

		while(!xAxisTouchSensor.isPressed() || !zAxisTouchSensor.isPressed()|| !yAxisTouchSensor.isPressed())
		{
			if(xAxisTouchSensor.isPressed()){
				Motor.B.stop();
			}
			if(zAxisTouchSensor.isPressed()){
				Motor.A.stop();
			}
			if(yAxisTouchSensor.isPressed()){
				bottomNXT.A.stop();
				bottomNXT.B.stop();
			}
		}
		Motor.B.stop();
		Motor.A.stop();
		bottomNXT.A.stop();
		bottomNXT.B.stop();
		Motor.B.setSpeed(1000);
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
