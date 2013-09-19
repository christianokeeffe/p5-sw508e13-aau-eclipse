import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		checkTopFunc.GetColorOnField(7, 7);
	}

}
