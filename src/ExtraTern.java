import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*המחלקה מייצגת JFrame פשוט עם תווית הודעה וכפתור כדי לבקש מהמשתמש לשחק שוב*/
public class ExtraTern extends JFrame implements ActionListener{
    /*משתני מחלקה:
     - `טקסט`: JLabel כדי להציג את ההודעה "אתה יכול לשחק שוב!".
     - `המשך`: JButton שכותרתו "המשך".
     - `סגור`: משתנה בוליאני לאותת מתי יש לסגור את החלון.
     */
    private JLabel text;
    private JButton cont;
    private boolean close; //set to true when u need to close
    /*בנאי:
   - מאתחל את JFrame עם JPanel כחלונית התוכן שלו.
   - מגדיר את הפריסה של חלונית התוכן ל-GridLayout עם 2 שורות ועמודה אחת.
   - יוצר את ה-JLabel עם ההודעה "אתה יכול לשחק שוב!".
   - יוצר את ה-JButton שכותרתו "המשך" ומוסיף לו ActionListener.
   - מוסיף את JLabel ו-JButton לחלונית התוכן.
   - מגדיר את חלונית התוכן של ה-JFrame, אורז אותו כך שיתאים לגודל המועדף ומגדיר אותו גלוי
*/
    public ExtraTern() {
        close = false;
        JPanel contentPane = new JPanel();
        this.setLayout(new GridLayout(2,1));
        text = new JLabel("You got Extra Tern!");
        cont = new JButton("continue");
        cont.addActionListener(this);
        cont.setActionCommand("continue");
        contentPane.add(text);
        contentPane.add(cont);
        this.add(contentPane);
        this.setContentPane(contentPane);
        this.pack();
        this.setVisible(true);
    }
    //מחזירה את הערך של המשתנה `close`
    public boolean getClose(){
        return close;
    }
    /*יישום ActionListener:
   - עוקף את שיטת `actionPerformed` לטיפול בלחיצות כפתורים.
   - כאשר לוחצים על כפתור "המשך", הוא מגדיר את המשתנה 'סגור' ל-'true' ומסלק את ה-JFrame
*/
    public void actionPerformed(ActionEvent arg0) {
        if(arg0.getActionCommand() == "continue"){
            close = true; //have a boolean close
            this.dispose();
        }
    }
}

