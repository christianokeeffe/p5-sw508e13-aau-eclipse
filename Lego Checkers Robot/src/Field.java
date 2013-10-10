
public class Field {
	public char pieceColor = ' ';
	public boolean allowedField;
	public boolean moveable = false;
	public boolean isKing = false;
	public boolean visited = false;
	public int x;
	public int y;
	
	public Field(){};
	public Field(int inputx, int inputy){
		x = inputx;
		y = inputy;
	}
	
	public void emptyThisField()
	{
		this.isKing = false;
		this.moveable = false;
		this.pieceColor = ' ';
	}
	
	public void adoptPropterties(Field oldField)
	{
		this.moveable = oldField.moveable;
		this.isKing = oldField.isKing;
		this.pieceColor = oldField.pieceColor;
	}
	
	public void upgradeKing()
	{
		this.isKing = true;
	}
}
