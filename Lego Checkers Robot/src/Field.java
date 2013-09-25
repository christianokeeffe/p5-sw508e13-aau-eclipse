
public class Field {
	public char pieceColor;
	public boolean allowedField;
	public boolean moveable = false;
	public boolean isKing = false;
	public int x;
	public int y;
	
	public Field(){};
	public Field(int inputx, int inputy){
		x = inputx;
		y = inputy;
	}
}
