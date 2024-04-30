import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class BasicStrategy extends Strategy {
    /*שדות:
   - 'חתיכה': מייצגת את החלק שנבחר עבור המהלך הבא.
   - `int xCoord`: מאחסן את קואורדינטת ה-x עבור מיקום החלק שנבחר.
   - `int yCoord`: מאחסן את קואורדינטת ה-y עבור מיקום החלק שנבחר.
   - `int orientation`: מאחסן את הכיוון של היצירה שנבחרה.
   - `int pieceIndex`: מאחסן את האינדקס של החלק שנבחר ביד השחקן.
   - `int[][] tempGrid`: מייצג רשת זמנית המשמשת להערכת מהלכים.
    */
    private Piece piece;
    private int xCoord;
    private int yCoord;
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

        ArrayList<Integer> lowestColor = new ArrayList<Integer>();
        ArrayList<Integer> oldColors = new ArrayList<Integer>();
        ColorScore[] scoreArray = score.toArray(new ColorScore[0]);

        //מוצא אם יש צבע נוסף נמוך ביותר
        for (ColorScore i : getGame().getCurrentPlayer().getColorScores()) {
            if (i.getScore() == lowestScore) {
                lowestColor.add(i.getColor());
            }
        }

        PlayerHand hand = getGame().getCurrentPlayer().getHand();
        //בודק אם היד צריכה החלפה ואם כן מחליפה
        if (getGame().getCurrentPlayer().checkHand() && getGame().getCurrentPlayer().getHand().getSize() == 6) {
            getGame().getCurrentPlayer().tradeHand();
        }
        boolean isColor1 = false, isColor2 = false;
        boolean isMove = false;
        do {
            isMove = false;
            //עובר על כל שורה כל עמודה כל צבע וכל חלק
            for (int x = 0; x < 30; x++) {
                for (int y = 0; y < 15; y++) {
                    for (int o = 0; o < 6; o++) {
                        for (int piece = 0; piece < getGame().getCurrentPlayer().getHand().getSize(); piece++) {
                            int color1 = getGame().getCurrentPlayer().getHand().getPiece(piece).getPrimaryHexagon().getColor(); //צבע ראשון
                            int color2 = getGame().getCurrentPlayer().getHand().getPiece(piece).getSecondaryHexagon().getColor(); //צבע שני
                            isColor1 = false;
                            isColor2 = false;
                            //לכל הצבעים הכי נמוכים אם אחד מהצבעים נמצא בחתיכה תשנה דגל
                            for (int i = 0; i < lowestColor.size(); i++) {
                                if (color1 == lowestColor.get(i)) {
                                    isColor1 = true;
                                }
                                if (color2 == lowestColor.get(i)) {
                                    isColor2 = true;
                                }
                            }
                            //אם אחד מהדגלים השתנה ויש מהלך חוקי בקורדינטות האלה ובכיוון הזה עם הצבע הזה אז תעדכן את כל המשתנים עם המידע הנוכחי
                            if ((isColor1 || isColor2) && getGame().checkLegalMove(o, x, y, color1, color2)) {
                                isMove = true;
                                makeTempGrid(o, x, y, color1, color2);
                                highestScore = getGame().score(x, y, tempGrid);
                                highestX = x;
                                highestY = y;
                                highestOrientation = o;
                                highestPieceIndex = piece;


                            }
                        }
                    }
                }
            }
            System.out.println(isMove);
            //אם לא נמצא מהלך מתאים
            if (!isMove) {
                ArrayList<Integer> newLowestColor = new ArrayList<Integer>();
                //מוסיף את כל הצבעים שהם הכי נמוכים
               for (Integer i : lowestColor){
                   if (!oldColors.contains(i)){
                       oldColors.add(i);
                   }
               }
                boolean found = false;
                int a = 0;
                /*מחפש את הצבע הבא הנמוך ביותר שלא נמצא בצבעים שכבר היו הנמוכים ביותר*/
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
                lowestScore = scoreArray[a].getScore();

                /*מוצא את כל הצבעים שהם באותו ניקוד כמו ההכי נמוך החדש*/
                for (ColorScore i : getGame().getCurrentPlayer().getColorScores()) {
                    if (i.getScore() == lowestScore) {
                        ColorScore cs = new ColorScore(i.getColor(),i.getScore());
                        newLowestColor.add(cs.getColor());
                    }
                }
                lowestColor = newLowestColor;

            }
            System.out.println(isMove);
        } while (!isMove);//כל עוד לא נמצא לנו מהלך
        for (int x = 0; x < 30; x++) {
            for (int y = 0; y < 15; y++) {
                for (int o = 0; o < 6; o++) {
                    for (int piece = 0; piece < getGame().getCurrentPlayer().getHand().getSize(); piece++) {
                        int color1 = getGame().getCurrentPlayer().getHand().getPiece(piece).getPrimaryHexagon().getColor();
                        int color2 = getGame().getCurrentPlayer().getHand().getPiece(piece).getSecondaryHexagon().getColor();
                        isColor1 = false;
                        isColor2 = false;
                        for (int i = 0; i < lowestColor.size(); i++) {//מוצא חתיכות שנמצאות במערך חתיכות קטנות ביותר
                            if (color1 == lowestColor.get(i)) {
                                isColor1 = true;
                            }
                            if (color2 == lowestColor.get(i)) {
                                isColor2 = true;
                            }
                        }
                        if ((isColor1 || isColor2) && getGame().checkLegalMove(o, x, y, color1, color2)) { // אם אחד מהם שנמצא ויש איתו מהלך מתאים
                            makeTempGrid(o, x, y, color1, color2);
                            if (isColor1 && isColor2) {//אם שניהם מתאימים
                                //בודק את שני הכיוונים של החלק והאם הניקוד שיצא יותר גבוה מהנוכחי
                                if (getGame().score(x, y, tempGrid) > highestScore || getGame().score(getGame().getSecondX(o, x, y),
                                        getGame().getSecondY(o, x, y), tempGrid) > highestScore) {
                                    highestScore = getGame().score(x, y, tempGrid);
                                    highestX = x;
                                    highestY = y;
                                    highestOrientation = o;
                                    highestPieceIndex = piece;
                                }
                            } else if (isColor1) {
                                if (getGame().score(x, y, tempGrid) > highestScore) {
                                    highestScore = getGame().score(x, y, tempGrid);
                                    highestX = x;
                                    highestY = y;
                                    highestOrientation = o;
                                    highestPieceIndex = piece;
                                }
                            } else if (isColor2) {
                                if (getGame().score(getGame().getSecondX(o, x, y), getGame().getSecondY(o, x, y),tempGrid) > highestScore) {
                                    highestScore = getGame().score(x, y, tempGrid);
                                    highestX = x;
                                    highestY = y;
                                    highestOrientation = o;
                                    highestPieceIndex = piece;
                                }
                            }


                        }
                    }
                }
            }
        }
        System.out.println("High Score" + highestScore);
        pieceIndex = highestPieceIndex;
        piece = hand.getPiece(pieceIndex);
        xCoord = highestX;
        yCoord = highestY;
        orientation = highestOrientation;
        makeTempGrid(highestOrientation,highestX, highestY,hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());

        for(int y = 0; y < 15; y++){//מדפיס את הלוח עם המהלך הטוב ביותר
            System.out.println("");
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
    /*`getPieceIndex()`, `getPiece()`, `getXCoordinate()`, `getYCoordinate()`, `getOrientation()`: שיטות גטר לאחזר את האינדקס, החתיכה, הקואורדינטות והכיוון שנבחרו.*/
    public int getPieceIndex() {
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
