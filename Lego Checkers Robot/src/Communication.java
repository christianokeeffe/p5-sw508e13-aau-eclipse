import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.remote.RemoteMotor;


public class Communication {
    RemoteNXTFunctions nxt;

    Communication(RemoteNXTFunctions inputNxt) {
        nxt = inputNxt;
        nxt.bottomNXT.C.setSpeed(900);
    }

    public final void playYourTurn() {
        Sound.twoBeeps();
        nxt.bottomNXT.C.forward();
        LCD.clear();
        LCD.drawString("Your turn", 0, 0);
        LCD.refresh();
        /*File soundFile = new File("yourturn.wav");
        Sound.playSample(soundFile, 100); // 100 ... volume*/
    }

    public final int getDifficulty() {
        Sound.playNote(Sound.PIANO, 500, 500);
        int dif = 2;
        boolean dontStop = true;
        while (dontStop) {
            LCD.clear();
            LCD.drawString("Set difficulty", 0, 0);
            LCD.drawString("Press enter", 0, 3);
            LCD.drawString("to select", 0, 4);
            LCD.drawString(difficulty(dif), 0, 1);
            LCD.refresh();
            int button = Button.waitForAnyPress();
            switch (button) {
            case 1:
                dontStop = false;
                break;
            case 2:
                dif--;
                break;
            case 4:
                dif++;
                break;
            default:
                break;
            }

            dif = dif % 5;
        }
        LCD.clear();
        LCD.drawString(difficulty(dif), 0, 0);
        LCD.drawString("is choosen", 0, 1);
        LCD.refresh();
        return dif;
    }

    private String difficulty(int dif) {
        switch (dif) {
        case 0:
            return "Very Easy";
        case 1:
            return "Easy";
        case 2:
            return "Medium";
        case 3:
            return "Hard";
        case 4:
            return "Very hard (slow)";
        default:
            break;
        }
        return "ERROR";
    }

    public final void robotTurn() {
        nxt.bottomNXT.C.stop(true);
        nxt.bottomNXT.C.setPower(0);
        LCD.clear();
        LCD.drawString("Please wait", 0, 0);
        LCD.refresh();
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
