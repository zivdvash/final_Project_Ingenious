import java.util.ArrayList;
/*בסך הכל, מחלקת 'יד' מספקת פונקציונליות לניהול הכלים שבידי שחקן במהלך המשחק, כולל הוספה, הסרה וגישה לכלים, כמו גם קבלת גודל היד וגישה ל-GrabBag המשויך.*/
public class PlayerHand {
    //player refills hand and creates
    private ArrayList<Piece> pieces=new ArrayList<Piece>();
    PiecesBag bag;
    //הבנאי מאתחל אובייקט `יד` חדש על ידי מילויו בחלקים שנלקחו מה-GrabBag המסופק. זה מוסיף שישה חלקים ליד בהתחלה
    PlayerHand(PiecesBag bag)
    {
        this.bag=bag;
        for (int counter=0;counter<6;counter++)
        {
            this.addNewPiece(bag.drawPiece(counter));
        }
    }
    // שיטה זו מסירה חלק מהיד על סמך האינדקס שלו ומחזירה את החלק שהוסר
    public Piece removePiece(int index) {
        return pieces.remove(index);
    }
    //שיטה זו מוסיפה יצירה חדשה ליד
    public void addNewPiece(Piece piece) {
        pieces.add(piece);
    }
    //שיטה זו מחזירה הפניה ליד הנוכחית
    public PlayerHand getHand() {
        return this;
    }
    //שיטה זו מחזירה את היצירה באינדקס שצוין ביד
    public Piece getPiece(int pieceIndex) {
        return pieces.get(pieceIndex);
    }
    //שיטה זו מחזירה את רשימת החלקים שנמצאים כעת ביד
    public ArrayList<Piece> getPieces()
    {
        return pieces;
    }
    //שיטה זו מחזירה את `PiecesBag` המשויך ליד
    public PiecesBag getBag(){
        return bag;
    }
    //שיטה זו מחזירה את מספר החלקים שנמצאים כעת ביד
    public int getSize(){
        return pieces.size();
    }

}



