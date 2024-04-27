import java.util.PriorityQueue;

/*'אסטרטגיה' משמש כתבנית ליישום אסטרטגיות שונות ששחקנים יכולים להשתמש בהם במהלך המשחק.*/
public abstract class Strategy {
    //`משחק `: מייצג את מופע המשחק המשויך לאסטרטגיה.
    Game game;
    //`Strategy(Game g)`: מאתחל את האסטרטגיה עם מופע המשחק המשויך
    Strategy(Game g){
        game = g;
    }
    //calculateMove(Hand h, int[] score)`: שיטה זו אחראית לחישוב המהלך הבא על סמך ידו של השחקן והניקוד הנוכחי.
    abstract public void calculateMove(PlayerHand h, PriorityQueue<ColorScore> score);
    //`getPiece()`: מחזירה את החלק שנבחר על ידי האסטרטגיה למהלך הבא
    abstract public Piece getPiece();
    //`getPieceIndex()`: מחזירה את האינדקס של החלק שנבחר ביד השחקן
    abstract public int getPieceIndex();
    //`getXCoordinate()`: מחזירה את קואורדינטת ה-x שבה תוצב החלק הנבחר על לוח המשחק
    abstract public int getXCoordinate();
    //`getYCoordinate()`: מחזירה את קואורדינטת ה-y שבה תוצב החלק הנבחר על לוח המשחק.
    abstract public int getYCoordinate();
    //`getOrientation()`: מחזירה את הכיוון של היצירה שנבחרה.
    abstract public int getOrientation();
}