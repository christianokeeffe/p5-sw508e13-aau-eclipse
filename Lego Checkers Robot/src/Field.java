
public class Field {
	public boolean allowedField;
	public boolean visited = false;
	public int x;
	public int y;
	
	private Piece pieceOnField;
	public Field(){};
	public Field(int inputx, int inputy){
		x = inputx;
		y = inputy;
	}
	
	public void setPieceOnField(Piece inputPiece)
	{
		pieceOnField = inputPiece;
		if(pieceOnField != null)
		{
			pieceOnField.x = x;
			pieceOnField.y = y;
		}
	}
	public Piece getPieceOnField(){
		return pieceOnField;
	}
	
	public void emptyThisField()
	{
		pieceOnField = null;
	}
	
}
