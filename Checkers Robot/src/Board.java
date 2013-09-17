import java.util.ArrayList;
import java.util.List;


public class Board {
	List<List<Square>> Rows;
	
	public Board(){
		int i,j;
		Rows = new ArrayList<List<Square>>();
		for(i=0;i<8;i++){
			List<Square> Columns = new ArrayList<Square>();
			for(j=0;j<8;j++)
			{
				Square temp = new Square();
				if(i+j%2 == 0)
				{
					temp.SquareColor = "Black";
					
					if(i < 3)
					{
						temp.Piece = "Black";
					}
					else if(i > 4)
					{
						temp.Piece = "White";
					}
				}
				else
				{
					temp.SquareColor = "White";
				}
				temp.PositionX = i;
				temp.PositionY = j;
				Columns.add(temp);
			}
			Rows.add(Columns);
		}
	}
}
