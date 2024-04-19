
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*בסך הכל, מחלקה `StrategyResults` מספקת ממשק פשוט להצגת תוצאות ניתוח אסטרטגיה ומאפשרת למשתמש לסיים ריצה באמצעות הכפתור "Finish".*/

public class BotGameResults extends JPanel{
    private boolean finish;
    /*בנאי
 - מגדיר את הפריסה של הפאנל עם פריסת רשת.
 - יוצר תוויות להצגת הכותרת ("Strategy Analysis") ומספר הזכיות עבור כל אסטרטגיה.
 - יוצר כפתור שכותרתו "הקודם" כדי לאפשר למשתמש לחזור למסך הקודם.
 - מגדיר מאזין פעולה עבור כפתור "הקודם".
 */
    public BotGameResults(int stratOneWins, int stratTwoWins) {
        Finish finishButton = new Finish();
        this.setLayout(new GridLayout(4,1));
        this.setBackground(Color.BLACK);
        JLabel title = new JLabel("Strategy Analysis");
        title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        title.setForeground(Color.GREEN);
        JLabel strat1 = new JLabel("Strategy 1: " + stratOneWins + " games won");
        strat1.setForeground(Color.RED);
        JLabel strat2 = new JLabel("Strategy 2: " + stratTwoWins + " games won");
        strat2.setForeground(Color.BLUE);
        JButton cont = new JButton("Finish");
        cont.setActionCommand("Finish");
        cont.addActionListener(finishButton);
        this.add(title);
        this.add(strat1);
        this.add(strat2);
        this.add(cont);
    }
    //`end()`: מחזירה true כאשר לוחצים על כפתור "הקודם" והמשתמש רוצה לצאת מהתוכנית.
    public boolean end(){
        return finish;
    }
    /*מחלקה פנימית
     - `חזרה`: מיישמת ActionListener לטיפול באירוע הלחיצה על כפתור "הקודם". כאשר לוחצים עליו, הוא מגדיר את הדגל הבולאני 'חזרה' ל-true, מה שמציין שהמשתמש רוצה לסיים את הצפייה.
     */
    private class Finish implements ActionListener{
        public void actionPerformed(ActionEvent arg0) {
            if(arg0.getActionCommand().equals("Finish")){
                finish = true;
                System.exit(0);
            }
        }
    }
}

