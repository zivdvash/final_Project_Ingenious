import java.util.*;

// מחלקה אבסטרקטית המייצגת שחקן במשחק
public abstract class Player{
    // יד השחקן
    private final PlayerHand hand;
    // שם השחקן
    private final String name;

    // צבעים מסודרים לפי הציון
    private PriorityQueue<ColorScore> colorScores;
    // האם זה תור השחקן הנוכחי
    private boolean isCurrentTurn;
    // האם השחקן השלים את תורו
    private boolean isTurnComplete;
    // כיוון החלק הנוכחי של השחקן
    private int orientation;
    // החלק הנוכחי שהשחקן מחזיק
    private Piece currentPiece;
    // קואורדינטות החלק של השחקן על לוח המשחק
    private int pieceX;
    private int pieceY;

    // בנאי
    public Player(String name1, PlayerHand hand1) {
        hand = hand1;
        name = name1;
        // הגדרת תורים מסודרים על פי הציון

        Comparator<ColorScore> comparator = Comparator.comparingInt(ColorScore::getScore);
        colorScores = new PriorityQueue<>(comparator);
        for(int i = 0; i<6; i++){
            ColorScore C = new ColorScore(i,0);
            colorScores.offer(C);
        }
        // איתחול משתנים נוספים
        isTurnComplete = false;
        orientation = 0;
    }

    // מתודה אבסטרקטית לתנועת השחקן
    public abstract void move();

    // קבלת שם השחקן
    public String getName() {
        return name;
    }

    // ודא שאם לשחקן יש קבוצה של נקודות שוות עבור הניקוד הנמוך ביותר, צור מערך שני של כל הצבעים הנמוכים ביותר, ועברו בדוק אם יש כל אחד מהצבעים האלה ביד
    //בעצם בודק האם יש אפשרות להחלפה
    public boolean checkHand() {
        int lowestScore = colorScores.peek().getScore();
        //checks for lowest scoring color
        ArrayList<Integer> lowestColors = new ArrayList<>();

        for (ColorScore i: colorScores){
            if(i.getScore() == lowestScore)
                lowestColors.add(i.getColor());
        }
        //lowestColor (lowest scoring color) is finally determined
        //System.out.println(lowestColor);
        for (int count = 0; count < hand.getSize(); count++) {
            for (Integer lowestColor : lowestColors) {
                //check to see if the lowest scoring color (lowestColor) is NOT in the hand/rack
                //if lowestColor is in the hand, checkHand returns false;
                if (hand.getPiece(count).getPrimaryHexagon().getColor() - 1 == lowestColor /*lowestColor*/)
                    return false;
                if (hand.getPiece(count).getSecondaryHexagon().getColor() - 1 == lowestColor /*lowestColor*/)
                    return false;
            }

        }
        return true;
    }
    // החלפת היד של השחקן
    public void tradeHand() {
        for (int a = 0; a < 6; a++) {
            hand.getBag().addPiece(hand.removePiece(0));
        }
        hand.getBag().shuffle();
        for (int a = 0; a < 6; a++) {
            hand.addNewPiece(hand.getBag().drawPiece(0));
        }
    }

    //מעדכן את הציונים של השחקן על סמך מערך הקלט
    public void updateScore(int[] score) {
        List<ColorScore> updatedScores = new ArrayList<>();

        for (ColorScore cs : colorScores) {
            for (int a = 0; a < colorScores.size(); a++) {
                if (cs.getColor() == a) {
                    int newScore = cs.getScore() + score[a];
                    if (newScore > 18) {
                        newScore = 18;
                    }
                    cs.setScore(newScore);
                    updatedScores.add(cs);
                }
            }
        }

        colorScores.clear();
        colorScores.addAll(updatedScores);
    }

    // קבלת התורים המסודרים על פי הציון
    public PriorityQueue<ColorScore> getColorScores() {
        return colorScores;
    }
    // הגדרת תור השחקן
    public void setCurrentTurn(boolean bool) {
        isCurrentTurn = bool;
    }
    // הגדרת השלמת התור של השחקן
    public void setTurnComplete(boolean bool) {
        isTurnComplete = bool;
    }

    // קבלת כיוון החלק של השחקן
    public int getOrientation() {
        return orientation;
    }

    // הגדרת כיוון החלק של השחקן
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    // קבלת קואורדינטות החלק של השחקן לפי ציר X
    public int getPieceX() {
        return pieceX;
    }

    // קבלת קואורדינטות החלק של השחקן לפי ציר Y
    public int getPieceY() {
        return pieceY;
    }

    // קבלת החלק הנוכחי של השחקן
    public Piece getCurrentPiece() {
        return currentPiece;
    }

    // הסרת החלק הנוכחי של השחקן
    public void removeCurrentPiece() {
        currentPiece = null;
    }

    // הוספת חלק חדש ליד השחקן
    public void addNewPiece() {
        hand.addNewPiece(hand.getBag().drawPiece(0));
    }

    public PlayerHand getHand(){
        return hand;
    }
    // איפוס הגדרות ברירת המחדל של השחקן
    public void resetDefault() {
        pieceX = -1;
        pieceY = -1;
    }
    public void setCurrentPiece(Piece currentPiece) {
        this.currentPiece = currentPiece;
    }

    public void setPieceX(int pieceX) {
        this.pieceX = pieceX;
    }

    public void setPieceY(int pieceY) {
        this.pieceY = pieceY;
    }



}
