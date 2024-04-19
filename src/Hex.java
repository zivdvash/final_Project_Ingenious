// red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5, white = 6, grey = 7, dark grey = 8
/*בסך הכל, מחלקה 'משושה' זו מקפלת את המאפיינים וההתנהגות של חלק משושה, במיוחד הצבע שלו, מה שמאפשר מניפולציה קלה ושליפה של מידע צבע.*/
public class Hex {
    private int color;
    //מאתחל את צבע המשושה עם מספר הצבע הנתון
    Hex(int colorNum) {
        color = colorNum;
    }
    //מחזירה את צבע המשושה
    public int getColor() {
        return color;
    }
    //מגדיר את צבע המשושה למספר הצבע שצוין
    public void setColor(int colorNum) {
        color = colorNum;
    }
}
