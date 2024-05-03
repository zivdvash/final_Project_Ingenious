import java.lang.Math;
import java.util.PriorityQueue;

public class RandomStrategy extends Strategy {
    private Piece piece;
    private int xCord;
    private int yCord;
    private int orientation;
    private int pieceIndex;
    //מאתחל את האובייקט `RandomStrategy` על ידי קריאה לבנאי מחלקת העל (`אסטרטגיה`) עם אובייקט `Game` המסופק.
    RandomStrategy(Game g) {
        super(g);
    }
    /*שיטה זו יוצרת קואורדינטות אקראיות `(xCord, yCord)` בתוך גבולות לוח המשחק וכיוון אקראי לכלי. הוא בודק אם המהלך שנוצר חוקי באמצעות שיטת `checkLegalMove()`. אם המהלך חוקי, הוא מגדיר את 'legalMove' ל'true' ובוחר באופן אקראי חתיכה מידו של השחקן. תהליך זה נמשך עד למציאת מהלך חוקי.
     */
    public void calculateMove(PlayerHand h, PriorityQueue<ColorScore> colorScores) {
        checkHandAndTrade();
        boolean legalMove = false;
        do {
            xCord = (int)(Math.random() * 30);
            yCord = (int)(Math.random() * 15);
            orientation = (int)(Math.random() * 6);
            if (checkLegalMove(xCord, yCord, orientation)) {
                legalMove = true;
                pieceIndex = (int)(Math.random() * h.getSize());
                piece = h.getPiece(pieceIndex);
            }
        } while (!legalMove);

    }
    //בודק אם יש צורך להחלפת יד
    private void checkHandAndTrade() {
        if (getGame().getCurrentPlayer().checkHand() && getGame().getCurrentPlayer().getHand().getSize() == 6) {
            getGame().getCurrentPlayer().tradeHand();
        }
    }
    /* שיטה זו בודקת אם המהלך שנוצר `(CordX, CordY)` עם הכיוון הנתון הוא חוקי. זה בודק כל כיוון אפשרי כדי להבטיח שהצבת יצירה בקואורדינטות שצוינו לא תפר שום כללי המשחק. אם המהלך חוקי, הוא מחזיר 'נכון'; אחרת, הוא מחזיר 'false'.
    כל תנאי אם מותאם לכיוון ספציפי של חלק ובודק אם הצבת חלק בכיוון זה בקואורדינטות הנתונות (CordX, CordY) היא חוקית (כלומר, שני המשושים שבהם החתיכה ימוקם ריקים, מסומנים ב-1 ב-game.grid).
    להלן הסבר על מה שכל תנאי בודק, בהתחשב באופי המשושה של הרשת:
    כיוון 0: זה מרמז שהיצירה מכוונת כך שהמשושה השני שלה נמצא מצפון-מערב לראשון. התנאי בודק אם המיקום המיועד של היצירה והתא מצפון-מערב לו הם גם ריקים וגם בגבולות הרשת.
    כיוון 1: לכיוון זה יש את המשושה השני מצפון מזרח לראשון. הוא מאמת את הריקנות של תא המטרה והתא מצפון-מזרח לו, ומבטיח שהם נמצאים בגבולות העליונים והימניים של הרשת.
    כיוון 2: כאן, היצירה מיושרת אופקית עם המשושה השני ישירות מימין. התנאי מבטיח שגם תא היעד וגם שני המדרגות מימין ריקים ובתוך הגבול הימני של הרשת.
    כיוון 3: בכיוון זה, היצירה ממוקמת עם המשושה השני מדרום מזרח לראשון. הוא בודק את הזמינות של תא היעד ושל תא היעד הדרום-מזרחי, ונשאר בתוך הקצוות התחתונים והימניים של הרשת.
    כיוון 4: זה כולל את היצירה עם המשושה השני שלה מדרום-מערב לראשון. התנאי מאשר שגם המיקום המיועד וגם התא הדרום-מערבי ריקים ובגבולות הרשת השמאלית והתחתון.
    כיוון 5: היצירה מיושרת אופקית אך נמשכת שמאלה, כשהמשושה השני שני שלבים משמאל לראשון. זה בודק את הריקנות והגבולות בצד שמאל של הרשת.
    קואורדינטות הרשת CordX ו-CordY מייצגות את המיקומים על ייצוג דו-ממדי של רשת משושה, כאשר גודל הרשת נחשב למשושים של 30x15. הבדיקות עבור CordX ו-CordY מבטיחות שהיצירה לא יוצאת מגבולות הרשת כאשר היא מונחת.
    CordX > 0 ו-CordX < 29 משמשים לבדיקת גבולות אופקיים, בהתחשב בכך שהיצירה יכולה להרחיב משושה אחד לכל צד (או שני משושים ימינה בכיוון 2).
    CordY > 0 ו-CordY < 14 בודקים גבולות אנכיים, ומבטיחים שהיצירה אינה חורגת מעבר לחלק העליון או התחתון של הרשת.
    הבדיקות game.grid[CordX][CordY] == -1 מוודאות שתא משושה ריק לפני שממקמים שם חתיכה. מערכת זו מאפשרת אסטרטגיית מיקום רב-תכליתית המתאימה לרשת המשושה של המשחק, ומאפשרת שש כיוונים אפשריים למיקום חלקים ומבטיחה שכל מהלך עומד בכללי המשחק למיקום חוקי.
    */
public boolean checkLegalMove(int CordX, int CordY, int orientation) {
        // Modified to use the orientation parameter
        if (orientation==0 && CordX > 0 && CordY > 0 && getGame().getGrid()[CordX][CordY]==-1 && getGame().getGrid()[CordX-1][CordY-1]==-1) {
            return true;
        } else if (orientation==1 && CordX < 29 && CordY > 0 && getGame().getGrid()[CordX][CordY]==-1 && getGame().getGrid()[CordX+1][CordY-1]==-1) {
            return true;
        } else if (orientation==2 && CordX < 28 && getGame().getGrid()[CordX][CordY]==-1 && getGame().getGrid()[CordX+2][CordY]==-1) {
            return true;
        } else if (orientation==3 && CordX < 29 && CordY < 14 && getGame().getGrid()[CordX][CordY]==-1 && getGame().getGrid()[CordX+1][CordY+1]==-1) {
            return true;
        } else if (orientation==4 && CordX > 0 && CordY < 14 && getGame().getGrid()[CordX][CordY] == -1 && getGame().getGrid()[CordX-1][CordY+1] == -1) {
            return true;
        } else if (orientation==5 && CordX > 1 && getGame().getGrid()[CordX][CordY] == -1 && getGame().getGrid()[CordX-2][CordY] == -1) {
            return true;
        }
        return false;
    }

    public int getPieceIndex(){
        return pieceIndex;
    }
    public Piece getPiece() {
        return piece;
    }
    public int getXCordinate() {
        return xCord;
    }
    public int getYCordinate() {
        return yCord;
    }
    public int getOrientation() {
        return orientation;
    }
}