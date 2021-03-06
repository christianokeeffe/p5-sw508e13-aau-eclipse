import java.io.IOException;

import custom.Exceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class Communication {
    RemoteNXTFunctions nxt;

    Communication(RemoteNXTFunctions inputNxt) {
        nxt = inputNxt;
    }

    public final void playYourTurn() {
        Sound.twoBeeps();
        LCD.clear();
        LCD.drawString("Your turn", 0, 0);
        LCD.refresh();
    }

    public final void cleanUp() throws IOException, NoKingLeft {
        LCD.drawString("Press enter", 0, 2);
        LCD.drawString("To clean up", 0, 3);
        Button.ENTER.waitForPress();
        LCD.clear();
        LCD.drawString("Please wait", 0, 0);
        LCD.refresh();
        nxt.checkersBoard.analyzeFunctions.cleanUp();
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

            dif = (dif + 5) % 5;
        }
        LCD.clear();
        LCD.drawString(difficulty(dif), 0, 0);
        LCD.drawString("is chosen", 0, 1);
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

    public final void humanWon() throws IOException, NoKingLeft {
        Sound.beepSequenceUp();
        LCD.clear();
        LCD.drawString("You won!", 0, 0);
        LCD.refresh();
        cleanUp();
    }

    public final void robotWon() throws IOException, NoKingLeft {
        Sound.beepSequence();
        LCD.clear();
        LCD.drawString("You lost!", 0, 0);
        LCD.refresh();
        cleanUp();
    }

    public final void draw() throws IOException, NoKingLeft {
        Sound.beep();
        LCD.clear();
        LCD.drawString("You got draw!", 0, 0);
        LCD.refresh();
        cleanUp();
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
