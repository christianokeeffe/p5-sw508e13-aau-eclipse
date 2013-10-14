
public class Field {
	public boolean allowedField;
	public boolean visited = false;
	public int x;
	public int y;
	
	private Piece pieceOnField = null;
	public Field(){};
	public Field(int inputx, int inputy){
		x = inputx;
		y = inputy;
	}
	
	public void setPieceOnField(Piece inputPiece)
	{
		pieceOnField = inputPiece;
		if(!isEmpty())
		{
			pieceOnField.x = x;
			pieceOnField.y = y;
		}
	}
	
	public boolean isEmpty(){
		if(pieceOnField == null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isPieceOfColor(char input){
		if(isEmpty()){
			return false;
		}
		else{
			if(pieceOnField.color == input){
				return true;
			}else{
				return false;
			}
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
