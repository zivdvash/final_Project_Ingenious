import java.util.ArrayList;
import java.util.Collections;
/*בסך הכל, מחלקה 'GrabBag' מכילה את ההיגיון לניהול החלקים הזמינים לשחקנים לצייר במהלך המשחק. הוא מטפל ביצירה, ערבוב, ציור והוספה של חלקים לתיק האחיזה.*/
public class PiecesBag
{
    //`ArrayList` בשם `Pieces` כדי לאחסן את החלקים בתיק
    private final ArrayList<Piece> Pieces=new ArrayList<>();

    //מאתחל את תיק האחיזה על ידי יצירת חלקים וערבוב שלהם
    PiecesBag()
    {
        createPieces();
        Collections.shuffle(Pieces);
    }

    //מערבבת את החלקים בתיק האחיזה
    public void shuffle(){
        Collections.shuffle(Pieces);
    }

    //מאכלסת את תיק האחיזה בחתיכות. הוא חוזר על שילובי הצבעים האפשריים עבור שני המשושים של כל חלק ומוסיף אותם לתיק בהתאם. זה מבטיח שכל שילוב יתווסף מספר מסוים של פעמים בהתאם לכללי המשחק
    public void createPieces()
    {
        //creates the pieces and adds to pieces arraylist
        for (int counter=5;counter>=0;counter--)
        {
            for (int counter2=counter;counter2>=0;counter2--)
            {
                if (counter!=counter2)
                {

                    for (int counter3=0;counter3<6;counter3++)
                    {
                        Piece piece=new Piece (counter + 1,counter2 + 1);
                        Pieces.add(piece);
                    }
                }
                else
                {
                    for (int counter3=0;counter3<5;counter3++)
                    {
                        Piece piece = new Piece(counter + 1,counter2 + 1);
                        Pieces.add(piece);
                    }
                }

            }
        }

    }
    // מסירה ומחזירה חלק מתיק האחיזה באינדקס שצוין `i`
    public Piece drawPiece(int i)
    {
        return (Pieces.remove(i));
    }
    //מוסיפה חתיכה לתיק האחיזה
    public void addPiece(Piece p)
    {
        Pieces.add(p);
    }
}
