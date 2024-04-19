/*מכילה את ההתנהגות הספציפית לשחקנים אנושיים במשחק, כולל בחירת חלקים, סיבוב ומיקום על לוח המשחק*/
public class HumanPlayer extends Player{
    //מייצג את כיוון השעון עבור חלקים מסתובבים
    static int CLOCKWISE = 1;
    //מייצג את הכיוון נגד כיוון השעון עבור חלקים מסתובבים
    static int COUNTERCLOCKWISE = -1;


    //מאתחל שחקן אנושי עם השם והיד הנתונים, ומגדיר את הכיוון הראשוני ל-0
    HumanPlayer(String name1, PlayerHand hand1){
        super(name1, hand1);
        setOrientation(0);
    }

    // מציין מקום לכל פעולה ששחקן עשוי לבצע במהלך התור שלו
    public void move() {
//		try {
//			Thread.sleep(0);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
    // מאפשרת לשחקן לסובב את היצירה הנוכחית בכיוון השעון או נגד כיוון השעון על סמך הכיוון שצוין. זה מעדכן את הכיוון בהתאם
    public void rotate(int direction){
        if(direction == CLOCKWISE){
            if(getOrientation() < 5){
                setOrientation(getOrientation() + 1);
            }else{
                setOrientation(0);
            }
        }else if(direction == COUNTERCLOCKWISE){
            if(getOrientation() > 0){
                setOrientation(getOrientation() - 1);
            }else{
                setOrientation(5);
            }
        }
    }
    //בוחרת כלי מידו של השחקן באינדקס שצוין ומגדירה אותו בתור הכלי הנוכחי למיקום על לוח המשחק
    public void selectPiece(int index){
        currentPiece = getHand().removePiece(index);

    }
    //מחזירה את הכלי הנוכחי לידיו של השחקן, ולמעשה מבטלת אותו להצבה על לוח המשחק
    public void deselect(){
        getHand().addNewPiece(currentPiece);
        currentPiece = null;
    }
    //קובעות את קואורדינטות ה-x וה-y של היצירה הנוכחית, בהתאמה
    public void setPieceX(int x){
        pieceX = x;
    }
    public void setPieceY(int y){
        pieceY = y;
    }
    //מגדירה בו-זמנית את קואורדינטות ה-x וה-y של היצירה הנוכחית
    public void setPieceCoordinate(int x, int y){
        pieceX = x;
        pieceY = y;
    }

}
