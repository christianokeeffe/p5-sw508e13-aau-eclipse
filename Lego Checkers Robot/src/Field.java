
public class Field {
	//allowedField determines if is an allowed field to play on (a black field)
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
	
	//Get and Set to access to private variable pieceOnField. Updates the coordinates of an piece when assigned to a field. 
	public void setPieceOnField(Piece inputPiece)
	{
		pieceOnField = inputPiece;
		if(!isEmpty())
		{
			pieceOnField.x = x;
			pieceOnField.y = y;
		}
	}

	public Piece getPieceOnField(){
		return pieceOnField;
	}
	
	//A field is empty, if no piece is assigned to it.
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
	
	public void emptyThisField()
	{
		pieceOnField = null;
	}
	
}
