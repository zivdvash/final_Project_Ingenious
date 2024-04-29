// מקפלת את ההתנהגות של שחקן מחשב במשחק. הוא משתמש באסטרטגיה כדי לחשב את המהלכים שלו ומספק שיטות לגשת למידע על החלק הנוכחי של השחקן, המיקום והכיוון
public class ComputerPlayer extends Player {
    //מכריז על משתני המופע `compName` ו-`compStrategy`
   private String compName;
   private Strategy compStrategy;
    // הבנאי 'ComputerPlayer' מאתחל שחקן מחשב עם שם נתון, אסטרטגיה ויד.
    //     - הוא קורא לבנאי של מחלקת העל 'שחקן' באמצעות מילת המפתח 'סופר' כדי לאתחל את שמו והיד של השחקן.
    //     - הוא מקצה את האסטרטגיה והשם המועברים כפרמטרים למשתני המופע `compStrategy` ו`compName`, בהתאמה.
    public ComputerPlayer(String name, Strategy s, PlayerHand h) {
        super(name, h);
        compName = name;
        compStrategy = s;
    }
    /*שיטת `move()` אחראית על ביצוע המהלך של נגן המחשב.
     - הוא קורא לשיטת `calculateMove()` של האסטרטגיה של שחקן המחשב (`compStrategy`) כדי לקבוע את המהלך הבא.
     - לאחר חישוב המהלך, הוא מעדכן את החלק הנוכחית, קואורדינטות החתיכה והכיוון בהתבסס על חישובי האסטרטגיה.
     - הוא מסיר את הכלי מידו של השחקן לאחר ביצוע המהלך.
*/
    public void move(){
        compStrategy.calculateMove(getHand(), getColorScores());
        setCurrentPiece( compStrategy.getPiece());
        getHand().removePiece(compStrategy.getPieceIndex());
        setPieceX(compStrategy.getXCoordinate()) ;
        setPieceY(compStrategy.getYCoordinate()) ;
        setOrientation(compStrategy.getOrientation());
    }
    /*getCurrentPiece()`: מחזירה את היצירה הנוכחית שנגן המחשב מחזיק.
     - `getPieceX()`: מחזירה את קואורדינטת ה-x של מיקום הכלי על לוח המשחק.
     - `getY()`: מחזירה את קואורדינטת ה-y של מיקום הכלי על לוח המשחק.
     - `getOrientation()`: מחזירה את הכיוון של היצירה.
*/

}


