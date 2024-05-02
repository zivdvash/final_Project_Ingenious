/*בסך הכל, מחלקה זו מייצגת יצירה המורכבת משני משושים, עם היכולת לאתחל אותם עם אובייקטים 'משושים' קיימים או עם ערכים שלמים המייצגים צבעים כדי ליצור אובייקטים 'משושים' חדשים.*/
public class Piece{
    //משתנים אלה מייצגים את שני המשושים המרכיבים את החלק
    private final Hex hex1;
    private final Hex hex2;

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
