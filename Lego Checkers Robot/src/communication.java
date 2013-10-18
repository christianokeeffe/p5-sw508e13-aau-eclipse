import lejos.nxt.LCD;
import lejos.nxt.Sound;
public class communication {

	public void playYourTurn(){
		Sound.beepSequenceUp();
		/*File soundFile = new File("yourturn.wav");
		Sound.playSample(soundFile, 100); // 100 ... volume*/
	}
	
	public void illeagalMove()
	{
		Sound.buzz();
		LCD.clear();
		LCD.drawString("Illegal move, try again",0, 0);
		LCD.refresh();
	}
	
	public void myKingNotPlaced()
	{
		Sound.buzz();
		LCD.clear();
		LCD.drawString("Place my king", 0, 0);
		LCD.refresh();
	}
}
