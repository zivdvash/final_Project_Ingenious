import java.util.ArrayList;
import java.util.PriorityQueue;

public class BasicStrategy extends Strategy {
    /*שדות:
   - 'חתיכה': מייצגת את החלק שנבחר עבור המהלך הבא.
   - `int xCord`: מאחסן את קואורדינטת ה-x עבור מיקום החלק שנבחר.
   - `int yCord`: מאחסן את קואורדינטת ה-y עבור מיקום החלק שנבחר.
   - `int orientation`: מאחסן את הכיוון של היצירה שנבחרה.
   - `int pieceIndex`: מאחסן את האינדקס של החלק שנבחר ביד השחקן.
   - `int[][] tempGrid`: מייצג רשת זמנית המשמשת להערכת מהלכים.
    */
    private Piece piece;
    private int xCord;
    private int yCord;
    private static final int directions = 6;
    private int orientation;
    private int pieceIndex;
    private int[][] tempGrid;
    //בנאי:
    //   - `SimpleStrategy(Game g)`: מאתחל את האסטרטגיה עם מופע המשחק המשויך.
    BasicStrategy(Game g) {
        super(g);
    }
    //`makeTempGrid(int o, int x, int y, int color1, int color2)`: יוצר לוח זמני על סמך הפרמטרים הנתונים
    private void makeTempGrid(int o, int x, int y, int color1, int color2) {
        tempGrid = new int[30][15];
        for (int X = 0; X < 30; X++) {
            for (int Y = 0; Y < 15; Y++) {
                if (getGame().twoHexGrid(o, x, y, color1, color2)[X][Y] == 0) {
                    tempGrid[X][Y] = getGame().getGrid()[X][Y];
                } else {
                    tempGrid[X][Y] = getGame().twoHexGrid(o, x, y, color1, color2)[X][Y];
                }
            }
        }
    }
    /*`calculateMove(Hand h, int[] score)`: מחשב את המהלך הבא על סמך היד של השחקן והניקוד הנוכחי
    שיטת `calculateMove` חוזרת על מיקומים וכיוון אפשריים של חלקים על הלוח, ומעריכה את הציון הפוטנציאלי של כל מהלך. הוא בוחר את המהלך עם הניקוד הגבוה ביותר וקובע את היצירה המתאימה, הקואורדינטות והכיוון בהתאם
    אסטרטגיה זו שואפת למקסם את הניקוד על ידי התחשבות בצבעים בעלי הניקוד הנמוך ביותר בידו של השחקן וניסיון למקם כלים בעמדות המניבות את הניקוד הגבוה ביותר. הוא גם מתאים את האסטרטגיה שלו אם הוא לא יכול למצוא מהלך חוקי עם הצבעים בעלי הניקוד הנמוך ביותר.
    לבסוף, הוא מדפיס את ייצוג הרשת הזמני למטרות ניפוי באגים.
     */
    public void calculateMove(PlayerHand h, PriorityQueue<ColorScore> score) {
        int highestScore = 0;
        int highestX = 0;
        int highestY = 0;
        int highestOrientation = 0;
        int highestPieceIndex = 0;
        int lowestScore = getGame().getCurrentPlayer().getColorScores().peek().getScore();
        int[] returnValues = new int[5];
        int w = 0;
        fillReturnValues(w, returnValues, highestScore, highestX, highestY, highestOrientation, highestPieceIndex);
        ArrayList<Integer> lowestColors = new ArrayList<>();
        ArrayList<Integer> oldColors = new ArrayList<>();
        ColorScore[] scoreArray = score.toArray(new ColorScore[0]);
        
        FindLowestColorsColors(lowestScore, lowestColors);
        PlayerHand hand = getGame().getCurrentPlayer().getHand();
        //בודק אם היד צריכה החלפה ואם כן מחליפה
        HandTradeCheck();
        boolean isMove;
        do {
            isMove = ConfirmLowestColors(returnValues,false,lowestColors);
            //אם לא נמצא מהלך מתאים
            if (!isMove) {
                ArrayList<Integer> newLowestColors = new ArrayList<>();
                //מוסיף את כל הצבעים שהם הכי נמוכים
                FindPreviousLowestColors(lowestColors, oldColors);
                int a = FindNextLowestColor(oldColors);
                lowestScore = scoreArray[a].getScore();

                /*מוצא את כל הצבעים שהם באותו ניקוד כמו ההכי נמוך החדש*/
                FindNewLowestColors(lowestScore, newLowestColors);
                lowestColors = newLowestColors;

            }
        } while (!isMove);//כל עוד לא נמצא לנו מהלך
        FindBestMove(returnValues,lowestColors);
        InsertHighestMove(highestPieceIndex, hand, highestX, highestY, highestOrientation);
        makeTempGrid(highestOrientation,highestX, highestY,hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());
        printBestMoveGrid();

    }

    private void InsertHighestMove(int highestPieceIndex, PlayerHand hand, int highestX, int highestY, int highestOrientation) {
        pieceIndex = highestPieceIndex;
        piece = hand.getPiece(pieceIndex);
        xCord = highestX;
        yCord = highestY;
        orientation = highestOrientation;
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
                lowestColor.add(i.getColor());
            }
        }
    }
    //ממלא את כל הערכים שצריכים לעבור בין הפונקציה המרכזית לפונקציה חיצונית שקובעים בסופו של דבר את המהלך
    private static void fillReturnValues(int w, int[] returnValues, int highestScore, int highestX, int highestY, int highestOrientation, int highestPieceIndex) {
        while (w < returnValues.length){
            returnValues[w++] = highestScore;
            returnValues[w++] = highestX;
            returnValues[w++] = highestY;
            returnValues[w++] = highestOrientation;
            returnValues[w++] = highestPieceIndex;
        }
    }

    //מדפיס את הלוח עם המהלך הטוב ביותר
    private void printBestMoveGrid() {
        for(int y = 0; y < 15; y++){
            System.out.println();
            for(int x = 0; x < 30;x++){
                if(tempGrid[x][y] == 0){
                    System.out.print(" ");
                }else if(tempGrid[x][y] == -1){
                    System.out.print(0);
                }else{
                    System.out.print(tempGrid[x][y]);
                }
            }
        }
    }
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
    /*מחפש את הצבע הבא הנמוך ביותר שלא נמצא בצבעים שכבר היו הנמוכים ביותר*/
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
        if(a == 6)
            a-=1;
        return a;
    }

    /*`getPieceIndex()`, `getPiece()`, `getXCoordinate()`, `getYCoordinate()`, `getOrientation()`: שיטות גטר לאחזר את האינדקס, החתיכה, הקואורדינטות והכיוון שנבחרו.*/
    public int getPieceIndex() {
        return pieceIndex;
    }
    /* מוודא שלצבע הכי נמוך יש מהלך אפשרי בהתאם ללוח וליד השחקן*/
    public boolean ConfirmLowestColors(int[] returnValues, boolean isMove, ArrayList<Integer>lowestColors) {
        boolean isColor1, isColor2;
        //עובר על כל שורה כל עמודה כל צבע וכל חלק
        for (int x = 0; x < 30; x++) {
            for (int y = 0; y < 15; y++) {
                for (int o = 0; o < directions; o++) {
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
                            makeTempGrid(o, x, y, color1, color2);
                            InsertMove(returnValues, x, y, o, piece);
                        }
                    }
                }
            }
        }
        return isMove;
    }
    /*אחרי שווידאנו שיש מהלך בצבע הכי נמוך שמצאנו אנו ריצים למצוא את המהלך הכי טוב בהתאם לכיוון ולמיקום במגרש*/
    public void FindBestMove(int[] returnValues, ArrayList<Integer>lowestColors) {
        boolean isColor1, isColor2;
        for (int x = 0; x < 30; x++) {
            for (int y = 0; y < 15; y++) {
                for (int o = 0; o < directions; o++) {
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
                            makeTempGrid(o, x, y, color1, color2);
                            if (isColor1 && isColor2) {//אם שניהם מתאימים
                                //בודק את שני הכיוונים של החלק והאם הניקוד שיצא יותר גבוה מהנוכחי
                                if (getGame().CalculateScore(x, y, tempGrid) > returnValues[0] || getGame().CalculateScore(getGame().getSecondX(o, x, y),
                                        getGame().getSecondY(o, x, y), tempGrid) > returnValues[0]) {
                                    InsertMove(returnValues, x, y, o, piece);
                                }
                            } else if (isColor1) {
                                if (getGame().CalculateScore(x, y, tempGrid) > returnValues[0]) {
                                    InsertMove(returnValues, x, y, o, piece);
                                }
                            } else {
                                if (getGame().CalculateScore(getGame().getSecondX(o, x, y), getGame().getSecondY(o, x, y),tempGrid) > returnValues[0]) {
                                    InsertMove(returnValues, x, y, o, piece);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//מכניס משתנים כדי לשמור מהלך הכי טוב חדש
    private void InsertMove(int[] returnValues, int x, int y, int o, int piece) {
        returnValues[0] = getGame().CalculateScore(x, y, tempGrid);
        returnValues[1] = x;
        returnValues[2] = y;
        returnValues[3] = o;
        returnValues[4] = piece;
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
