import lejos.nxt.LCD;
import lejos.nxt.Sound;


public class Communication {
    RemoteNXTFunctions nxt;
    
    Communication(RemoteNXTFunctions inputNxt) {
        nxt = inputNxt;
    }
    
    public final void playYourTurn() {
        Sound.twoBeeps();
        nxt.bottomNXT.C.setPower(100);
        /*File soundFile = new File("yourturn.wav");
        Sound.playSample(soundFile, 100); // 100 ... volume*/
    }
    
    public final void robotTurn() {
        nxt.bottomNXT.C.setPower(100);
    }

    public final void illeagalMove() {
        Sound.buzz();
        LCD.clear();
        LCD.drawString("Illegal move", 0, 0);
        LCD.refresh();
    }

    public final void humanWon() {
        Sound.beepSequenceUp();
        LCD.clear();
        LCD.drawString("You won!", 0, 0);
        LCD.refresh();
    }

    public final void robotWon() {
        Sound.beepSequence();
        LCD.clear();
        LCD.drawString("You lost!", 0, 0);
        LCD.refresh();
    }

    public final void draw() {
        Sound.beep();
        LCD.clear();
        LCD.drawString("You got draw!", 0, 0);
        LCD.refresh();
    }

    public final void myKingNotPlaced() {
        Sound.buzz();
        LCD.clear();
        LCD.drawString("Place my king", 0, 0);
        LCD.refresh();
    }

    public final void nothingPossible() {
        Sound.buzz();
        LCD.clear();
        LCD.drawString("No possible movement", 0, 0);
        LCD.refresh();
    }
}
