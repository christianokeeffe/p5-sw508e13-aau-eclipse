import java.io.IOException;

import lejos.nxt.Button;


public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Mere og endnu mere
		MoveFunctions Test = new MoveFunctions();
		while(true){
			Test.MoveSensorTo(8, 8);
			Button.waitForAnyPress();
			Test.MoveSensorTo(-8, -8);
			Button.waitForAnyPress();
		}
		
		
	}

}
