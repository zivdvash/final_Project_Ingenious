import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
// מחלקה זו מטפלת בעיקר ב-GUI ובאינטראקציה של המשתמש למסחר ביד, עם תלות חיצונית מינימלית.
public class HandTrade extends JFrame implements ActionListener{

    private final JFrame frame;
    public boolean isTrade;
    private boolean isClosed;
    /*`HandTrade()` Constructor**:
    - מאתחל את רכיבי ה-GUI של מחלקת `HandTrade`, כולל המסגרת, הלוחות, התווית והלחצנים. הוא מגדיר את הפריסה, מוסיף רכיבים לפאנלים ומצמיד מאזיני פעולה ללחצנים.
    - מאתחל את הדגלים 'isTrade' ו-'isClosed' ל'false'.
    - מציג את המסגרת על ידי הגדרת חלונית התוכן שלה והפיכתה לגלויה.
*/
    HandTrade(){
        frame = new JFrame("Ingenious");
        JPanel pan = new JPanel();
        JPanel pan1 = new JPanel();
        JPanel pan2 = new JPanel();
        isTrade = false;
        isClosed = false;
        JLabel label = new JLabel("Would you like to trade your hand?");
        JButton trade = new JButton("Trade");
        trade.setActionCommand("Trade");
        trade.addActionListener(this);
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("Cancel");
        cancel.addActionListener(this);
        pan.setLayout(new GridLayout(1,1));
        pan2.setLayout(new GridLayout(2,0));
        pan1.add(label);
        pan2.add(trade);
        pan2.add(cancel);
        pan.add(pan1);
        pan.add(pan2);
        frame.setContentPane(pan);
        frame.pack();
        frame.setVisible(true);

    }
    //`actionPerformed` מטפלת באינטראקציה של המשתמש עם כפתורים (`מסחר` ו`ביטול`) על ידי הטמעת ממשק `ActionListener`. הוא מגדיר את הדגלים 'isTrade' ו-'isClosed' בהתאם ומסלק את המסגרת כאשר לוחצים על כל אחד מהלחצנים.

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Trade")){
            isTrade = true;
            isClosed = true;
            frame.dispose();
        }else if(e.getActionCommand().equals("Cancel")){
            isClosed = true;
            frame.dispose();
        }

    }
    //getIsTrade() - מחזירה את הערך של דגל `isTrade`, המציין אם המשתמש בחר לסחור ביד שלו (`נכון`) או לא (`שקר`).
    public boolean getIsTrade(){
        return isTrade;
    }
    //`getIsClosed()`: - מחזירה את הערך של הדגל `isClosed`, המציין אם המסגרת נסגרה (`אמת`) או לא (`false`)
    public boolean getIsClosed(){
        return isClosed;
    }

}
