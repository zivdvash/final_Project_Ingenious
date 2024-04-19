import java.util.ArrayList;
/*מחלקה זו משמשת מסגרת להגדרת סוגים שונים של שחקנים במשחק, עם התנהגויות ספציפיות המיושמות על ידי תת מחלקות*/
public abstract class Player {
    //אובייקט מסוג `PlayerHand`, המייצג את ידו של השחקן במשחק
    PlayerHand hand;
    //מחרוזת המייצגת את שם השחקן
    String name;
    //מערך של מספרים שלמים המייצגים את הציונים של השחקן עבור צבעים שונים
    int[] score;
    //ערך בוליאני המציין אם זה התור הנוכחי של השחקן.
    boolean isCurrentTurn;
    //ערך בוליאני המציין אם השחקן סיים את תורו
    boolean isTurnComplete;
    //מספר שלם המייצג את הכיוון של החלק
    int orientation;
    //אובייקט מסוג `Piece`, המייצג את החלק המוחזק כעת על ידי השחקן
    Piece currentPiece;
    //מספרים שלמים המייצגים את הקואורדינטות של הכלי הנוכחי על לוח המשחק
    int pieceX;
    int pieceY;
    //מספר שלם המייצג את הציון הנמוך ביותר מבין הציונים של השחקן
    int lowestScore;
    //מאתחל את שמו, היד ומערך הניקוד של השחקן. זה גם מגדיר את המשתנה 'isTurnComplete' ל-false ומאתחל את המשתנה 'orientation' ל-0
    public Player(String name1, PlayerHand hand1){
        hand = hand1;
        name = name1;
        score = new int[6];
        isTurnComplete = false;
        orientation = 0;
        for (int a = 0; a < 6; a ++){
            score[a] = 0;
        }
    }
    //שיטה זו חייבת להיות מיושמת על ידי תת מחלקות כדי להגדיר כיצד השחקן מבצע מהלך במשחק
    public abstract void move();
    public String getName(){
        return name;
    }
    // ודא שאם לשחקן יש קבוצה של נקודות שוות עבור הניקוד הנמוך ביותר, צור מערך שני של כל הצבעים הנמוכים ביותר, ועברו בדוק אם יש כל אחד מהצבעים האלה ביד
    //בעצם בודק האם יש אפשרות להחלפה
    public boolean checkHand() {
        int lowestScore = score[0];
        // red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5
        int lowestColor = 0; //lowest scoring color
        //checks for lowest scoring color
        ArrayList<Integer> lowestColors = new ArrayList<Integer>();
        for (int count = 0; count < 6; count++) {
            if (score[count] < lowestScore) {
                lowestScore = score[count];
                lowestColor = count;
            }
        }
        for (int count = 0; count < 6; count++) {
            if(score[count] == lowestScore)
                lowestColors.add(count);
        }
        //lowestColor (lowest scoring color) is finally determined
        //System.out.println(lowestColor);
        for (int count = 0; count < hand.getSize(); count++) {
            for(int a = 0; a < lowestColors.size(); a++){
                //check to see if the lowest scoring color (lowestColor) is NOT in the hand/rack
                //if lowestColor is in the hand, checkHand returns false;
                if (hand.getPiece(count).getPrimaryHexagon().getColor() - 1 == lowestColors.get(a) /*lowestColor*/)
                    return false;
                if (hand.getPiece(count).getSecondaryHexagon().getColor() - 1== lowestColors.get(a) /*lowestColor*/)
                    return false;
            }

        }
        return true;
    }
    //מחליף את כל הכלים ביד השחקן עם כלים מתיק המשחק
    public void tradeHand(){
        for(int a = 0; a < 6; a ++){
            hand.getBag().addPiece(hand.removePiece(0));
        }
        hand.getBag().shuffle();
        for(int a = 0; a < 6; a ++){
            hand.addNewPiece(hand.getBag().drawPiece(0));
        }
    }
    public void setLowestScore(int s) {
        lowestScore = s;
    }
    public int getLowestScore() {
        return lowestScore;
    }
    public PlayerHand getHand(){
        return hand;
    }
    //מעדכן את הציונים של השחקן על סמך מערך הקלט
    public void updateScore(int[] score){
        for(int a = 0; a < this.score.length; a ++){
            this.score[a] += score[a];
            if(this.score[a] > 18){
                this.score[a] = 18;
            }
        }
    }
    public int[] getScores(){return score;}
    public boolean getCurrentTurn()
    {
        return isCurrentTurn;
    }
    public void setCurrentTurn(boolean bool){
        isCurrentTurn = bool;
    }
    public boolean getTurnComplete()
    {
        return isTurnComplete;
    }
    public void setTurnComplete(boolean bool){
        isTurnComplete = bool;
    }

    public int getOrientation(){
        return orientation;
    }
    public void setOrientation(int orientation){
        this.orientation = orientation;
    }
    public int getPieceX(){
        return pieceX;
    }
    public int getPieceY(){
        return pieceY;
    }
    public Piece getCurrentPiece(){
        return currentPiece;
    }
    public void removeCurrentPiece(){
        currentPiece = null;
    }
    public void addNewPiece(){
        getHand().addNewPiece(getHand().getBag().drawPiece(0));
    }//מכניס חלק חדש מהשקית של כל החלקים
    public void resetDefault(){
        pieceX = -1;
        pieceY = -1;

    }

}

