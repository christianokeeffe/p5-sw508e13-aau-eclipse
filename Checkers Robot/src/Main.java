import java.io.IOException;

import lejos.nxt.Button;
import lejos.util.*;


public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// Mere og endnu mere
		MoveFunctions Test = new MoveFunctions();
		while(true){
			Test.MoveSensorTo(2, 2);
			Delay.msDelay(1000);
			Test.MoveSensorTo(5, 5);
			Delay.msDelay(1000);
			Test.MoveSensorTo(8, 8);
			Delay.msDelay(1000);
			Test.MoveSensorTo(7, 3);
			Delay.msDelay(1000);
			Test.MoveSensorTo(0, 0);
			if(Button.waitForAnyPress(1000) != 0){
				return;
			}
		}
		
		
	}

}
