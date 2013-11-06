import lejos.nxt.LCD;
import lejos.nxt.Sound;
public class communication {

	public void playYourTurn(){
		Sound.twoBeeps();
		/*File soundFile = new File("yourturn.wav");
		Sound.playSample(soundFile, 100); // 100 ... volume*/
	}
	
	public void illeagalMove()
	{
		Sound.buzz();
		LCD.clear();
		LCD.drawString("Illegal move",0, 0);
		LCD.refresh();
	}
	
	public void humanWon()
	{
		Sound.beepSequenceUp();;
		LCD.clear();
		LCD.drawString("You won!",0, 0);
		LCD.refresh();
	}
	
	public void robotWon()
	{
		Sound.beepSequence();;
		LCD.clear();
		LCD.drawString("You lost!",0, 0);
		LCD.refresh();
	}
	
	public void draw()
	{
		Sound.beep();
		LCD.clear();
		LCD.drawString("You got draw!",0, 0);
		LCD.refresh();
	}
	
	public void myKingNotPlaced()
	{
		Sound.buzz();
		LCD.clear();
		LCD.drawString("Place my king", 0, 0);
		LCD.refresh();
	}
	
	public void nothingPossible(){
		Sound.buzz();
		LCD.clear();
		LCD.drawString("No possible movement", 0, 0);
		LCD.refresh();
	}
}
