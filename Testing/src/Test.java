import lejos.nxt.*;
import java.lang.*;

public class Test {
	public static void main(String[] args) throws Exception {
		NXTMotor C = new NXTMotor(MotorPort.C);
		TouchSensor T = new TouchSensor(SensorPort.S4);
		
		C.forward();
		
		
		while(! T.isPressed()){
			Thread.sleep(200);
		}
		C.backward();
	}

}
