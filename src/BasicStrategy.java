import java.util.*;

public class BasicStrategy extends Strategy {
    /*שדות:
   - 'חתיכה': מייצגת את החלק שנבחר עבור המהלך הבא.
   - int xCord: מאחסן את קואורדינטת ה-x עבור מיקום החלק שנבחר.
   - int yCord: מאחסן את קואורדינטת ה-y עבור מיקום החלק שנבחר.
   - int orientation: מאחסן את הכיוון של היצירה שנבחרה.
   - int pieceIndex: מאחסן את האינדקס של החלק שנבחר ביד השחקן.
   - int[][] tempGrid: מייצג רשת זמנית המשמשת להערכת מהלכים.
    */
    private Piece piece;
    private SaveMove bestMove;
    private  static  final int ROWS = 30;
    private  static  final int  COLS = 15;
    private  static  final int  MAX_HAND_PIECE = 6;
    private int xCord;
    private int yCord;
    private static final int directions = 6;
    private int orientation;
    private int pieceIndex;
    private Map<Integer,Integer> cloneBoard;
    //בנאי:
    //   - SimpleStrategy(Game g): מאתחל את האסטרטגיה עם מופע המשחק המשויך.
    BasicStrategy(Game g) {
        super(g);
    }
    //makeTempGrid(int o, int x, int y, int color1, int color2): יוצר לוח זמני על סמך הפרמטרים הנתונים
    private void cloneBoard(int o, int x, int y, int color1, int color2) {
        cloneBoard = new HashMap<>();
        for (int X = 0; X < ROWS; X++) {
            for (int Y = 0; Y < COLS; Y++) {
                if (getGame().MakeTempGrid(o, x, y, color1, color2)[X][Y] == 0 && getGame().getColorCells().get(X*ROWS+Y) != null) {
                    cloneBoard.put(X*ROWS+Y,getGame().getColorCells().get(X*ROWS+Y));
                } else {
                    if (getGame().MakeTempGrid(o, x, y, color1, color2)[X][Y] != 0)
                        cloneBoard.put(X*ROWS+Y,getGame().convertMatrixToMap(getGame().MakeTempGrid(o, x, y, color1, color2)).get(X*ROWS+Y));
                }
            }
        }
    }
    /*calculateMove(Hand h, int[] score): מחשב את המהלך הבא על סמך היד של השחקן והניקוד הנוכחי
    שיטת calculateMove חוזרת על מיקומים וכיוון אפשריים של חלקים על הלוח, ומעריכה את הציון הפוטנציאלי של כל מהלך. הוא בוחר את המהלך עם הניקוד הגבוה ביותר וקובע את היצירה המתאימה, הקואורדינטות והכיוון בהתאם
    אסטרטגיה זו שואפת למקסם את הניקוד על ידי התחשבות בצבעים בעלי הניקוד הנמוך ביותר בידו של השחקן וניסיון למקם כלים בעמדות המניבות את הניקוד הגבוה ביותר. הוא גם מתאים את האסטרטגיה שלו אם הוא לא יכול למצוא מהלך חוקי עם הצבעים בעלי הניקוד הנמוך ביותר.
    לבסוף, הוא מדפיס את ייצוג הרשת הזמני למטרות ניפוי באגים.
     */
    public void calculateMove(PlayerHand h, PriorityQueue<ColorScore> score) {
        int lowestScore = getGame().getCurrentPlayer().getColorScores().peek().getScore();
        ArrayList<Integer> lowestColors = new ArrayList<>();
        ArrayList<Integer> oldColors = new ArrayList<>();
        ColorScore[] scoreArray = score.toArray(new ColorScore[0]);
        FindLowestColorsColors(lowestScore, lowestColors);
        PlayerHand hand = getGame().getCurrentPlayer().getHand();
        //בודק אם היד צריכה החלפה ואם כן מחליפה
        HandTradeCheck();
        boolean isMove;
        do {
            isMove = ConfirmLowestColors(lowestColors);
            //אם לא נמצא מהלך מתאים
            if (!isMove) {
                ArrayList<Integer> newLowestColors = new ArrayList<>();
                //מוסיף את כל הצבעים שהם הכי נמוכים
                FindPreviousLowestColors(lowestColors, oldColors);
                int a = FindNextLowestColor(oldColors);
                lowestScore = scoreArray[a].getScore();
                //מוצא את כל הצבעים שהם באותו ניקוד כמו ההכי נמוך החדש
                FindNewLowestColors(lowestScore, newLowestColors);
                lowestColors = newLowestColors;

            }
        } while (!isMove);//כל עוד לא נמצא לנו מהלך
        bestMove = FindBestMove(bestMove,lowestColors);
        InsertHighestMove(hand);
        // makeTempGrid(bestMove.highestOrientation, bestMove.highestX, bestMove.highestY, hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());
        // printBestMoveGrid();
        System.out.print("move it");
        System.out.print("\n");
    }

    private void InsertHighestMove(PlayerHand hand) {
        pieceIndex = bestMove.highestPieceIndex;
        piece = hand.getPiece(bestMove.highestPieceIndex);
        xCord = bestMove.highestX;
        yCord = bestMove.highestY;
        orientation = bestMove.highestOrientation;
    }

    //בודק האם היד צריכה החלפה
    private void HandTradeCheck() {
        if (getGame().getCurrentPlayer().checkHand() && getGame().getCurrentPlayer().getHand().getSize() == 6) {
            getGame().getCurrentPlayer().tradeHand();
        }
    }

    //מוצא אם יש צבע נוסף נמוך ביותר
    private void FindLowestColorsColors(int lowestScore, ArrayList<Integer> lowestColor) {
        for (ColorScore i : getGame().getCurrentPlayer().getColorScores()) {
            if (i.getScore() == lowestScore) {
                lowestColor.add(i.getColor()+1);
            }
        }
    }

    //מכניס משתנים כדי לשמור מהלך הכי טוב חדש
    private SaveMove InsertMove (int highestScore, int highestX, int highestY, int highestOrientation, int highestPieceIndex) {
        return new SaveMove(highestScore,highestX,highestY,highestOrientation,highestPieceIndex);
    }

    //מדפיס את הלוח עם המהלך הטוב ביותר
    /*    private void printBestMoveGrid() {
        for(int y = 0; y < COLS; y++){
            System.out.println();
            for(int x = 0; x < ROWS ;x++){
                if(tempGrid[x][y] == 0){
                    System.out.print(" ");
                }else if(tempGrid[x][y] == -1){
                    System.out.print(0);
                }else{
                    System.out.print(tempGrid[x][y]);
                }
            }
        }
    }*/

    //שומר את הצבעים הכי נמוכים בשביל לדעת לאילו צבעים לא להיתייחס אם החיפוש נכשל
    private static void FindPreviousLowestColors(ArrayList<Integer> lowestColor, ArrayList<Integer> oldColors) {
        for (Integer i : lowestColor){
            if (!oldColors.contains(i)){
                oldColors.add(i);
            }
        }
    }
    //מחפש את כל הצבעים שהם עם אותו ניקוד כמו הצבע הנמוך ביותר
    private void FindNewLowestColors(int lowestScore, ArrayList<Integer> newLowestColors) {
        for (ColorScore i : getGame().getCurrentPlayer().getColorScores()) {
            if (i.getScore() == lowestScore) {
                ColorScore cs = new ColorScore(i.getColor(),i.getScore());
                newLowestColors.add(cs.getColor());
            }
        }
    }
///מחפש את הצבע הבא הנמוך ביותר שלא נמצא בצבעים שכבר היו הנמוכים ביותר
    private int FindNextLowestColor(ArrayList<Integer> oldColors) {
        boolean found = false;
        int a = 0;
        while (a<6 && !found){
            for (ColorScore i : getGame().getCurrentPlayer().getColorScores()) {
                if (!oldColors.contains(i.getColor()))
                    found = true;

                if(!found)
                    a++;
            }
        }
        if(a == MAX_HAND_PIECE)
            a-=1;
        return a;
    }

    /*getPieceIndex(), getPiece(), getXCoordinate(), getYCoordinate(), getOrientation(): שיטות גטר לאחזר את האינדקס, החתיכה, הקואורדינטות והכיוון שנבחרו.*/
    public int getPieceIndex() {
        return pieceIndex;
    }
    /* מוודא שלצבע הכי נמוך יש מהלך אפשרי בהתאם ללוח וליד השחקן*/
    public boolean ConfirmLowestColors(ArrayList<Integer>lowestColors) {
        boolean isMove = false;
        Set<Integer> keys = getGame().getWhiteCells().keySet();
        //עובר על כל שורה כל עמודה כל צבע וכל חלק
        for (int xy : keys) {
            for (int o = 0; o < directions; o++) {
                if (!isMove) {
                    isMove = isMoveExists(lowestColors, o, xy);
                }

            }
        }
        return isMove;
    }

    private boolean isMoveExists(ArrayList<Integer> lowestColors, int o, int xy) {
        boolean isMove = false;
        boolean isColor1;
        boolean isColor2;
        int y = xy % ROWS  ;
        int x = (xy - y)/ROWS;
        for (int piece = 0; piece < getGame().getCurrentPlayer().getHand().getSize(); piece++) {
            int color1 = getGame().getCurrentPlayer().getHand().getPiece(piece).getPrimaryHexagon().getColor(); //צבע ראשון
            int color2 = getGame().getCurrentPlayer().getHand().getPiece(piece).getSecondaryHexagon().getColor(); //צבע שני
            isColor1 = false;
            isColor2 = false;
            //לכל הצבעים הכי נמוכים אם אחד מהצבעים נמצא בחתיכה תשנה דגל
            for (Integer lowestColor : lowestColors) {
                if (color1 == lowestColor) {
                    isColor1 = true;
                }
                if (color2 == lowestColor) {
                    isColor2 = true;
                }
            }
            //אם אחד מהדגלים השתנה ויש מהלך חוקי בקורדינטות האלה ובכיוון הזה עם הצבע הזה אז תעדכן את כל המשתנים עם המידע הנוכחי
            if ((isColor1 || isColor2) && getGame().checkLegalMove(o, x, y, color1, color2)) {
                isMove = true;
                cloneBoard(o, x, y, color1, color2);
                bestMove = InsertMove(getGame().CalculateScore(x, y, cloneBoard), x, y, o,piece);
            }
        }
        return isMove;
    }

   //אחרי שווידאנו שיש מהלך בצבע הכי נמוך שמצאנו אנו ריצים למצוא את המהלך הכי טוב בהתאם לכיוון ולמיקום במגרש/
    public SaveMove FindBestMove(SaveMove bestMove, ArrayList<Integer>lowestColors) {
        Set<Integer> keys = getGame().getWhiteCells().keySet();
        for (int xy : keys) {
            for (int o = 0; o < directions; o++) {
                bestMove = FindBestMoveInOnePiece(bestMove, lowestColors, o, xy);
            }
        }

        return bestMove;
    }

    private SaveMove FindBestMoveInOnePiece(SaveMove bestMove, ArrayList<Integer> lowestColors, int o, int xy) {
        boolean isColor1;
        boolean isColor2;
        int y = xy % ROWS  ;
        int x = (xy - y)/ROWS;
        for (int piece = 0; piece < getGame().getCurrentPlayer().getHand().getSize(); piece++) {
            int color1 = getGame().getCurrentPlayer().getHand().getPiece(piece).getPrimaryHexagon().getColor();
            int color2 = getGame().getCurrentPlayer().getHand().getPiece(piece).getSecondaryHexagon().getColor();
            isColor1 = false;
            isColor2 = false;
            for (Integer lowestColor : lowestColors) {//מוצא חתיכות שנמצאות במערך חתיכות קטנות ביותר
                if (color1 == lowestColor) {
                    isColor1 = true;
                }
                if (color2 == lowestColor) {
                    isColor2 = true;
                }
            }
            if ((isColor1 || isColor2) && getGame().checkLegalMove(o, x, y, color1, color2)) { // אם אחד מהם שנמצא ויש איתו מהלך מתאים
                cloneBoard(o, x, y, color1, color2);
                if (isColor1 && isColor2) {//אם שניהם מתאימים
                    //בודק את שני הכיוונים של החלק והאם הניקוד שיצא יותר גבוה מהנוכחי
                    if (getGame().CalculateScore(x, y, cloneBoard) > bestMove.highestScore || getGame().CalculateScore(getGame().getSecondX(o, x, y),
                            getGame().getSecondY(o, x, y), cloneBoard) > bestMove.highestScore) {
                        bestMove = InsertMove(getGame().CalculateScore(x, y, cloneBoard), x, y, o,piece);
                    }
                }else if (isColor1) {
                    if (getGame().CalculateScore(x, y, cloneBoard) > bestMove.highestScore) {
                        bestMove = InsertMove(getGame().CalculateScore(x, y, cloneBoard), x, y, o,piece);
                    }
                } else {
                    if (getGame().CalculateScore(getGame().getSecondX(o, x, y), getGame().getSecondY(o, x, y), cloneBoard) > bestMove.highestScore) {
                        bestMove = InsertMove(getGame().CalculateScore(x, y, cloneBoard), x, y, o,piece);
                    }
                }
            }
        }
        return bestMove;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getXCoordinate() {
        return xCord;
    }

    public int getYCoordinate() {
        return yCord;
    }

    public int getOrientation() {
        return orientation;
}
}
