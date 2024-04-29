/*בסך הכל, מחלקה זו מייצגת יצירה המורכבת משני משושים, עם היכולת לאתחל אותם עם אובייקטים 'משושים' קיימים או עם ערכים שלמים המייצגים צבעים כדי ליצור אובייקטים 'משושים' חדשים.*/
public class Piece{
    //משתנים אלה מייצגים את שני המשושים המרכיבים את החלק
    private Hex hex1, hex2;

    // צבעים שונים: אדום, ירוק, כחול, כתום, צהוב וסגול. קבועים אלה מקבלים ערכים 0 עד 5 בהתאמה
    int  red = 0, green = 1, blue = 2, orange = 3, yellow = 4, purple = 5;

    // לוקח שני אובייקטים `Hex` כפרמטרים ומאתחל את המשתנים `hex1` ו-`hex2` עם האובייקטים הללו
    Piece(Hex hexOne, Hex hexTwo) {
        hex1 = hexOne;
        hex2 = hexTwo;
    }

    //לוקח שני מספרים שלמים המייצגים צבעים כפרמטרים ומאתחל את `hex1` ו-`hex2` עם אובייקטי `Hex` חדשים בעלי הצבעים שצוינו
    Piece(int color1,int color2){
        hex1 = new Hex(color1);
        hex2 = new Hex(color2);
    }

    //מחזיר את האובייקט `hex1`
    public Hex getPrimaryHexagon() {
        return hex1;
    }

    //מחזיר את האובייקט `hex2`
    public Hex getSecondaryHexagon() {
        return hex2;
    }
}
