import java.lang.Math;
public class RandomStrategy extends Strategy {
    private Piece piece;
    private int xCoord;
    private int yCoord;
    private int orientation;
    private int pieceIndex;
    //מאתחל את האובייקט `RandomStrategy` על ידי קריאה לבנאי מחלקת העל (`אסטרטגיה`) עם אובייקט `Game` המסופק.
    RandomStrategy(Game g) {
        super(g);
    }
    /*שיטה זו יוצרת קואורדינטות אקראיות `(xCoord, yCoord)` בתוך גבולות לוח המשחק וכיוון אקראי לכלי. הוא בודק אם המהלך שנוצר חוקי באמצעות שיטת `checkLegalMove()`. אם המהלך חוקי, הוא מגדיר את 'legalMove' ל'true' ובוחר באופן אקראי חתיכה מידו של השחקן. תהליך זה נמשך עד למציאת מהלך חוקי.
     */
    public void calculateMove(PlayerHand h, int[] score) {
        checkHandAndTrade();
        boolean legalMove = false;
        do {
            xCoord = (int)(Math.random() * 30);
            yCoord = (int)(Math.random() * 15);
            orientation = (int)(Math.random() * 6);
            if (checkLegalMove(xCoord, yCoord, orientation)) {
                legalMove = true;
                pieceIndex = (int)(Math.random() * h.getSize());
                piece = h.getPiece(pieceIndex);
            }
        } while (!legalMove);
    }
    //בודק אם יש צורך להחלפת יד
    private void checkHandAndTrade() {
        if (game.getCurrentPlayer().checkHand() && game.getCurrentPlayer().getHand().getSize() == 6) {
            game.getCurrentPlayer().tradeHand();
        }
    }
    /* שיטה זו בודקת אם המהלך שנוצר `(CoordX, CoordY)` עם הכיוון הנתון הוא חוקי. זה בודק כל כיוון אפשרי כדי להבטיח שהצבת יצירה בקואורדינטות שצוינו לא תפר שום כללי המשחק. אם המהלך חוקי, הוא מחזיר 'נכון'; אחרת, הוא מחזיר 'false'.
    כל תנאי אם מותאם לכיוון ספציפי של חלק ובודק אם הצבת חלק בכיוון זה בקואורדינטות הנתונות (CoordX, CoordY) היא חוקית (כלומר, שני המשושים שבהם החתיכה ימוקם ריקים, מסומנים ב-1 ב-game.grid).
    להלן הסבר על מה שכל תנאי בודק, בהתחשב באופי המשושה של הרשת:
    כיוון 0: זה מרמז שהיצירה מכוונת כך שהמשושה השני שלה נמצא מצפון-מערב לראשון. התנאי בודק אם המיקום המיועד של היצירה והתא מצפון-מערב לו הם גם ריקים וגם בגבולות הרשת.
    כיוון 1: לכיוון זה יש את המשושה השני מצפון מזרח לראשון. הוא מאמת את הריקנות של תא המטרה והתא מצפון-מזרח לו, ומבטיח שהם נמצאים בגבולות העליונים והימניים של הרשת.
    כיוון 2: כאן, היצירה מיושרת אופקית עם המשושה השני ישירות מימין. התנאי מבטיח שגם תא היעד וגם שני המדרגות מימין ריקים ובתוך הגבול הימני של הרשת.
    כיוון 3: בכיוון זה, היצירה ממוקמת עם המשושה השני מדרום מזרח לראשון. הוא בודק את הזמינות של תא היעד ושל תא היעד הדרום-מזרחי, ונשאר בתוך הקצוות התחתונים והימניים של הרשת.
    כיוון 4: זה כולל את היצירה עם המשושה השני שלה מדרום-מערב לראשון. התנאי מאשר שגם המיקום המיועד וגם התא הדרום-מערבי ריקים ובגבולות הרשת השמאלית והתחתון.
    כיוון 5: היצירה מיושרת אופקית אך נמשכת שמאלה, כשהמשושה השני שני שלבים משמאל לראשון. זה בודק את הריקנות והגבולות בצד שמאל של הרשת.
    קואורדינטות הרשת CoordX ו-CoordY מייצגות את המיקומים על ייצוג דו-ממדי של רשת משושה, כאשר גודל הרשת נחשב למשושים של 30x15. הבדיקות עבור CoordX ו-CoordY מבטיחות שהיצירה לא יוצאת מגבולות הרשת כאשר היא מונחת.
    CoordX > 0 ו-CoordX < 29 משמשים לבדיקת גבולות אופקיים, בהתחשב בכך שהיצירה יכולה להרחיב משושה אחד לכל צד (או שני משושים ימינה בכיוון 2).
    CoordY > 0 ו-CoordY < 14 בודקים גבולות אנכיים, ומבטיחים שהיצירה אינה חורגת מעבר לחלק העליון או התחתון של הרשת.
    הבדיקות game.grid[CoordX][CoordY] == -1 מוודאות שתא משושה ריק לפני שממקמים שם חתיכה. מערכת זו מאפשרת אסטרטגיית מיקום רב-תכליתית המתאימה לרשת המשושה של המשחק, ומאפשרת שש כיוונים אפשריים למיקום חלקים ומבטיחה שכל מהלך עומד בכללי המשחק למיקום חוקי.
    */
    public boolean checkLegalMove(int CoordX, int CoordY, int orientation) {
        // Modified to use the orientation parameter
        if (orientation==0 && CoordX > 0 && CoordY > 0 && game.grid[CoordX][CoordY]==-1 && game.grid[CoordX-1][CoordY-1]==-1) {
            return true;
        } else if (orientation==1 && CoordX < 29 && CoordY > 0 && game.grid[CoordX][CoordY]==-1 && game.grid[CoordX+1][CoordY-1]==-1) {
            return true;
        } else if (orientation==2 && CoordX < 28 && game.grid[CoordX][CoordY]==-1 && game.grid[CoordX+2][CoordY]==-1) {
            return true;
        } else if (orientation==3 && CoordX < 29 && CoordY < 14 && game.grid[CoordX][CoordY]==-1 && game.grid[CoordX+1][CoordY+1]==-1) {
            return true;
        } else if (orientation==4 && CoordX > 0 && CoordY < 14 && game.grid[CoordX][CoordY] == -1 && game.grid[CoordX-1][CoordY+1] == -1) {
            return true;
        } else if (orientation==5 && CoordX > 1 && game.grid[CoordX][CoordY] == -1 && game.grid[CoordX-2][CoordY] == -1) {
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
    public int getXCoordinate() {
        return xCoord;
    }
    public int getYCoordinate() {
        return yCoord;
    }
    public int getOrientation() {
        return orientation;
    }
}