import java.util.ArrayList;
import java.util.List;
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
    private List<MoveData> moveDataList = new ArrayList<>();

    //בנאי:
    //   - `SimpleStrategy(Game g)`: מאתחל את האסטרטגיה עם מופע המשחק המשויך.
    BasicStrategy(Game g) {
        super(g);
    }

    /*`calculateMove(Hand h, int[] score)`: מחשב את המהלך הבא על סמך היד של השחקן והניקוד הנוכחי
    שיטת `calculateMove` חוזרת על מיקומים וכיוון אפשריים של חלקים על הלוח, ומעריכה את הציון הפוטנציאלי של כל מהלך. הוא בוחר את המהלך עם הניקוד הגבוה ביותר וקובע את היצירה המתאימה, הקואורדינטות והכיוון בהתאם
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
            //בדיקה האם לבצע הכי נמוך יש מהלך
            isMove = FindAllMoves(lowestColors);
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
        MoveData bestMove = findBestMove(moveDataList);
        System.out.println("High Score" + bestMove.getScore());
        InsertHighestMove(bestMove.getPiece(),bestMove.getOrientation(),bestMove.getCell().getX(),bestMove.getCell().getY());
        //לא נראלי שצריך סבור שזה רק לדיבג
        // makeTempGrid(bestMove.getOrientation(),bestMove., highestY,hand.getPiece(pieceIndex).getPrimaryHexagon().getColor(),hand.getPiece(pieceIndex).getSecondaryHexagon().getColor());
       // printBestMoveGrid();

    }

    private void InsertHighestMove(Piece Hpiece ,int highestX, int highestY, int highestOrientation) {
        piece = Hpiece;
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
    //מדפיס את הלוח עם המהלך הטוב ביותר
    /*    private void printBestMoveGrid() {
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

    /*        List<MoveData> moveDataList = new ArrayList<>();

        for (Cell cell : getGame().getDynamicBoard()) {
            for (Cell neigbor : cell.getNeighbors()) {
                if (neigbor.getColor() == -1) {
                    MoveData moveData = findBestMove(neigbor);
                    moveDataList.add(moveData);
                }
            }
        }

        MoveData botMov =  getBestMove(moveDataList);

        makeBotMov(botMov);*/
    /* מוודא שלצבע הכי נמוך יש מהלך אפשרי בהתאם ללוח וליד השחקן*/
    /*    public boolean ConfirmLowestColors(int[] returnValues, ArrayList<Integer>lowestColors) {
        List<MoveData> moveDataList = new ArrayList<>();
        MoveData temp;
        boolean isMove = false;
        ArrayList<Piece> LowestColorPiece = findLowestColorPiece(lowestColors);
        for (Cell cell : getGame().getDynamicBoard()) {
            for (Cell neigbor : cell.getNeighbors()) {
                if (neigbor.getColor() == -1) {
                    for (int o = 0; o < directions; o++) {
                        for (Piece piece : LowestColorPiece) {
                            //אם אחד מהדגלים השתנה ויש מהלך חוקי בקורדינטות האלה ובכיוון הזה עם הצבע הזה אז תעדכן את כל המשתנים עם המידע הנוכחי
                            if (getGame().checkLegalMove(o, neigbor.getX(), neigbor.getY(), piece.getPrimaryHexagon().getColor(),piece.getSecondaryHexagon().getColor())) {
                                temp= new MoveData(getGame().CalculateScore(neigbor.getX(), neigbor.getY(),getGame().getDynamicBoard()),piece,cell,o);
                                moveDataList.add(temp);

                            }
                        }
                    }
                }
            }
        }
        return isMove;
    }*/

    public boolean FindAllMoves(ArrayList<Integer>lowestColors) {
        MoveData temp;
        boolean isMove = false;
        boolean isColor1, isColor2;
        for (Cell cell : getGame().getDynamicBoard()) {
            for (Cell neigbor : cell.getNeighbors()) {
                if (neigbor.getColor() == -1) {
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
                            if ((isColor1 || isColor2) && getGame().checkLegalMove(o, neigbor.getX(), neigbor.getY(), color1, color2)) { // אם אחד מהם שנמצא ויש איתו מהלך מתאים
                                if (isColor1 && isColor2) {//אם שניהם מתאימים
                                    //בודק את שני הכיוונים של החלק והאם הניקוד שיצא יותר גבוה מהנוכחי
                                    temp = new MoveData(getGame().CalculateScore(neigbor.getX(), neigbor.getY(), getGame().getDynamicBoard()), getGame().getCurrentPlayer().getHand().getPiece(piece), cell, o);
                                    moveDataList.add(temp);
                                    temp = new MoveData(getGame().CalculateScore(getGame().getSecondX(o, neigbor.getX(), neigbor.getY()),
                                            getGame().getSecondY(o, neigbor.getX(), neigbor.getY()), getGame().getDynamicBoard()), getGame().getCurrentPlayer().getHand().getPiece(piece), cell, o);
                                    moveDataList.add(temp);

                                } else if (isColor1) {

                                    temp = new MoveData(getGame().CalculateScore(neigbor.getX(), neigbor.getY(), getGame().getDynamicBoard()), getGame().getCurrentPlayer().getHand().getPiece(piece), cell, o);
                                    moveDataList.add(temp);

                                } else {
                                    temp = new MoveData(getGame().CalculateScore(getGame().getSecondX(o, neigbor.getX(), neigbor.getY()),
                                            getGame().getSecondY(o, neigbor.getX(), neigbor.getY()), getGame().getDynamicBoard()), getGame().getCurrentPlayer().getHand().getPiece(piece), cell, o);
                                    moveDataList.add(temp);
                                }
                            }
                        }
                    }
                }
            }
        }
        return isMove;
    }
    public MoveData findBestMove( List<MoveData> moveDataList){
        MoveData bestMove = moveDataList.get(0);
        for (MoveData moveData : moveDataList){
            if(moveData.getScore() > bestMove.getScore())
                bestMove = moveData;

        }
        return  bestMove;
    }


    //מוצא חתיכות עם הצבע הכי נמוך
    /*    public ArrayList<Piece> findLowestColorPiece(ArrayList<Integer>lowestColors){
        ArrayList<Piece> truePieces = new ArrayList<>();
        for (int piece = 0; piece < getGame().getCurrentPlayer().getHand().getSize(); piece++) {
            int color1 = getGame().getCurrentPlayer().getHand().getPiece(piece).getPrimaryHexagon().getColor(); //צבע ראשון
            int color2 = getGame().getCurrentPlayer().getHand().getPiece(piece).getSecondaryHexagon().getColor(); //צבע שני
            //לכל הצבעים הכי נמוכים אם אחד מהצבעים נמצא בחתיכה תשנה דגל
            for (Integer lowestColor : lowestColors) {
                if (color1 == lowestColor) {
                    truePieces.add(getGame().getCurrentPlayer().getHand().getPiece(piece));
                }
                if (color2 == lowestColor) {
                    truePieces.add(getGame().getCurrentPlayer().getHand().getPiece(piece));
                }
            }
        }
       return truePieces;
    }
*/

/*

    private MoveData tryMove(Cell neighbor) {
        getGame().getDynamicBoard()


    }
*/

    /*אחרי שווידאנו שיש מהלך בצבע הכי נמוך שמצאנו אנו רוצים למצוא את המהלך הכי טוב בהתאם לכיוון ולמיקום במגרש*/
    /*    public void FindBestMove(int[] returnValues, ArrayList<Integer> lowestColors) {
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
    }*/

//מכניס משתנים כדי לשמור מהלך הכי טוב חדש


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
