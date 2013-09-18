import lejos.nxt.*;
import java.lang.*;

public class Test {
	public static void main(String[] args) throws Exception {
		NXTMotor A = new NXTMotor(MotorPort.A);
		TouchSensor T = new TouchSensor(SensorPort.S4);
		
		A.setPower(100);
		
		
		while(! T.isPressed()){
			Thread.sleep(200);
		}
		A.setPower(0);;
	}

}
