import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*מחלקת `StartPanel` מספקת ממשק גמיש ואינטראקטיבי להגדרת פרמטרים של משחק וביצוע ניתוח אסטרטגיה.
 */
public class StartPanel extends JPanel{
    private InputHandler handle;
    private JRadioButton[][] topButtons;
    private ButtonGroup[] buttonGroups;
    private JTextField[] names;
    private JComboBox[] strategies;
    private String[] strategy;
    private JButton play;
    private JButton cancel;
    private boolean stratScreen;
    private boolean isCancelled;
    private boolean isPlay;
    private JRadioButton fastMode;
    private JRadioButton slowMode;
    private int games;
    private JTextField numOfGames;
    private StrategyAnalysisMode stratListener;
    private JLabel error;
    private int errorCounter;

    boolean isContinueClicked = false;
    /*בנאי:
   - מאתחל את פריסת הפאנל ומגדיר את מטפל הקלט.
   - קורא שיטות להגדרת לחצני בחירה, תיבות קלט שמות וחלונות גלילה מטה של אסטרטגיה.
   - מאתחל כפתורים להפעלה וביטול פעולות.
   - מגדיר דגלים למסכי ביטול, משחק ואסטרטגיה.
    */
    StartPanel(boolean first){
        if(first){
            this.setSize(600,500);
            this.setLayout(new GridLayout(12,4)); // Increased rows to accommodate the buttons

            JLabel title = new JLabel("Welcome To Ingenious");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
            this.add(title);
            this.add(new JLabel()); // Add an empty label for spacing

            title = new JLabel("Choose type of players:");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
            this.add(title);
            this.add(new JLabel()); // Add an empty label for spacing

            title = new JLabel("Player 1:");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            this.add(title);
            title = new JLabel("Player 2:");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            this.add(title);

            handle = new InputHandler();
            setRadioButtons();  //handles ALL BUTTONS

            title = new JLabel("Enter names:");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
            this.add(title);
            this.add(new JLabel()); // Add an empty label for spacing
            setNameBoxes();     //handles ALL NAMES

            title = new JLabel("Choose strategy's for bots:");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            this.add(title);
            this.add(new JLabel()); // Add an empty label for spacing
            title = new JLabel("1 - easy; 2 - medium");
            title.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
            this.add(title);
            this.add(new JLabel()); // Add an empty label for spacing
            setStrategyBoxes(); //handles ALL scrolldown strats

            play = new JButton("Play");
            play.setActionCommand("Play");
            play.addActionListener(handle);
            this.add(play);

            cancel = new JButton("Cancel");
            cancel.setActionCommand("Cancel");
            cancel.addActionListener(handle);
            this.add(cancel);

            isCancelled= false;
            isPlay = false;
            stratScreen = false;
        }
    }

    //מגדיר וממקם את לחצני הבחירה עבור סוגי שחקנים (ללא, אנושי, מחשב).
    private void setRadioButtons(){//CALLED FROM CONSTRUCTROR
        topButtons = new JRadioButton[3][4];
        buttonGroups = new ButtonGroup[4];
        for(int i = 0; i < 4; i++){
            buttonGroups[i] = new ButtonGroup();
        }
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 2; col++){
                if(row == 0){
                    topButtons[row][col] = new JRadioButton("None");
                }else if(row == 1){
                    topButtons[row][col] = new JRadioButton("Human");
                }else if(row == 2){
                    topButtons[row][col] = new JRadioButton("Computer");
                }
                buttonGroups[col].add(topButtons[row][col]);
                this.add(topButtons[row][col]);
                topButtons[row][col].addActionListener(handle);
            }
        }
        topButtons[1][0].setSelected(true);
        topButtons[2][1].setSelected(true);

    }
    //מגדיר וממקם את שדות הטקסט עבור שמות שחקנים.
    private void setNameBoxes(){
        names = new JTextField[2];
        for(int i = 0; i < 2; i++){
            names[i] = new JTextField("Player " + (i+1));
            names[i].setPreferredSize(new Dimension(10, 10));
            if(i >= 2){
                names[i].disable();
            }
            this.add(names[i]);
        }
    }
    // מגדיר וממקם את תיבות המשולבות לבחירת אסטרטגיות שחקן.
    private void setStrategyBoxes(){
        strategies = new JComboBox[2];
        strategy = new String[2];
        strategy[0] = "Strategy 1";
        strategy[1] = "Strategy 2";
        for(int i = 0; i < 2; i++){
            strategies[i] = new JComboBox(strategy);
            strategies[i].setSelectedIndex(0);
            if(i != 1){
                strategies[i].setForeground(Color.GRAY);
                strategies[i].disable();
            }
            this.add(strategies[i]);
        }
    }
    //מחזירה true אם לוחצים על כפתור הביטול.
    public boolean isCancelled(){
        return isCancelled;
    }
    //מחזירה מערך המציין את הסוג של כל שחקן (אדם או מחשב).
    //type of the player: will be 0 for humans, 1 for computers
    public int[] getPlayerTypes(){
        int numPlayers = numPlayers();
        int[] ret = new int[numPlayers];
        for(int i = 0; i < numPlayers; i++){
            if(topButtons[1][i].isSelected()){//if human
                ret[i] = 0;
            }else if(topButtons[2][i].isSelected()){
                ret[i] = 1;
            }
        }
        return ret;
    }
    //מחזירה true אם לוחצים על לחצן ההפעלה ומספיק הגדרות מסומנות.
    public boolean isGameStart(){
        return isPlay;
    }
    // מחזירה true אם מסך האסטרטגיה מופעל.
    public boolean isAnalysisStart(){
        return stratScreen;
    }
    //מחזירה מערך המציין את האסטרטגיה שנבחרה עבור כל שחקן.
    //value of [0] strategy 1 default, [1] strategy 2
    public int[] getStrategies(){
        int[] ret = new int[2];
        for(int c = 0; c < 2; c++){
            if(strategies[c].isEnabled()){
                if(strategies[c].getSelectedIndex() == 0){
                    ret[c] = 1;
                }else if(strategies[c].getSelectedIndex() == 1){
                    ret[c] = 2;
                }
            }else{ //if not selected
                ret[c] = 0;
            }
        }
        return ret;
    }
    //מחזירה מערך של שמות שחקנים.
    public String[] getNames(){
        int numPlayers = numPlayers();
        String[] ret = new String[numPlayers];
        for(int i = 0 ; i < numPlayers; i++){
            if(names[i].isEnabled()){
                ret[i] = names[i].getText();
            }else{
                ret[i] = null;
            }
        }
        return ret;
    }
    // מחזירה את מספר השחקנים שנבחרו.
    public int numPlayers(){
        int numPlayers = 0;
        for(int col = 0; col < 2; col++){
            if(!topButtons[0][col].isSelected()){//if NONE is NOT SELECTED
                numPlayers++; //more players
            }
        }
        return numPlayers;
    }
    //מחזירה true אם מסך האסטרטגיה פעיל
    public boolean strategyScreen(){
        return stratScreen;
    }
    /*מיישמת ActionListener לטיפול בלחיצות כפתורים ולעדכן רכיבי ממשק משתמש בהתבסס על קלט המשתמש*/
    private class InputHandler implements ActionListener{

        public void actionPerformed(ActionEvent arg0) {
            String clicked = arg0.getActionCommand();
            int numPlayers = numPlayers();
            if(numPlayers < 2){//אם אין מספיק שחקנים
                play.disable();
                play.setForeground(Color.GRAY);
            }else if(numPlayers >= 2){
                play.enable();
                play.setForeground(Color.BLACK);
            }
            if(clicked.equals("Cancel")){
                isCancelled = true;
            }else if(clicked.equals("Play")){
                if(numPlayers >= 2){
                    if(allComputers()){
                        stratScreen = true;
                    }else{
                        isPlay = true;
                    }
                }
            }
            for(int col = 0;  col < 2; col++){
                if(topButtons[0][col].isSelected()){ //if NONE
                    names[col].disable();			//no NAME
                    strategies[col].disable();		//no STRATEGY
                    names[col].setForeground(Color.GRAY);
                    strategies[col].setForeground(Color.GRAY);
                }else if(topButtons[1][col].isSelected()){//if HUMAN
                    strategies[col].disable();		//no STRATEGY
                    strategies[col].setForeground(Color.GRAY);
                    names[col].enable();					//can TYPE NAME, no STRATEGY
                    names[col].setForeground(Color.BLACK);
                }else{//if COMPUTER
                    names[col].enable();					//can TYPE NAME
                    strategies[col].enable();				//can HAVE STRATEGY
                    names[col].setForeground(Color.BLACK);
                    strategies[col].setForeground(Color.BLACK);
                }
            }
        }
        private boolean allComputers(){
            for(int col = 0; col < 2; col++){
                if(topButtons[1][col].isSelected()){//if there is a human
                    return false;
                }
            }
            return true;
        }
    }
    //מעביר את הלוח למצב ניתוח אסטרטגיה על ידי הסרת רכיבים קיימים והוספת רכיבים לבחירת מספר המשחקים והמצב.
    public void openAnalysisMode(){
        this.removeAll();
        this.setLayout(new GridLayout(5,2));
        stratListener = new StrategyAnalysisMode();

        ButtonGroup fastOrSlow = new ButtonGroup();
        fastMode = new JRadioButton("Fast Mode");
        slowMode= new JRadioButton("Slow Mode");
        fastOrSlow.add(fastMode);
        fastOrSlow.add(slowMode);
        fastMode.setSelected(true);

        JLabel title = new JLabel("Strategy Analysis Mode");
        JLabel prompt = new JLabel("Number of Games: ");
        numOfGames = new JTextField();

        JButton cont = new JButton("Continue");
        cont.setActionCommand("Continue");
        cont.addActionListener(stratListener);
        error = new JLabel("Input error: please input an integer");
        error.setVisible(false);
        this.add(title);
        this.add(new JLabel(""));
        this.add(prompt);
        this.add(numOfGames);
        this.add(fastMode);
        this.add(new JLabel(""));
        this.add(slowMode);
        this.add(error);
        this.setVisible(true);
        this.add(cont);
    }
    // מחזירה 0 עבור מצב מהיר ו-1 עבור מצב איטי.
    public int fastOrSlow(){
        if(fastMode.isSelected()){
            return 0;
        }
        return 1;
    }
    // מחזירה את מספר המשחקים שהוזנו לניתוח האסטרטגיה.
    public int getGames(){
        if(games > 0){
            return games;
        }else{
            return -1;
        }
    }
    //מחזירה true אם לוחצים על כפתור ההמשך במהלך מצב ניתוח האסטרטגיה.
    public boolean isContinueClicked(){
        return isContinueClicked;
    }

    /*מיישמת ActionListener כדי לטפל בלחיצת כפתור המשך במהלך מצב ניתוח האסטרטגיה ולאמת קלט.
     */
    private class StrategyAnalysisMode implements ActionListener{
        public void actionPerformed(ActionEvent arg0) {// כשלוחצים על Continue אז כל הבדיקה הזאת קוראת
            if(arg0.getActionCommand().equals("Continue")){
                //get the number from whatever inputted
                try{//if not a clear int
                    games = Integer.parseInt(numOfGames.getText());
                    if(games <= 0){//אם נמוך מ0
                        errorCounter++;
                        error.setText("Input error: please input an integer greater than 0");
                        if(errorCounter % 2 == 0){
                            error.setForeground(Color.BLUE);
                        }else{
                            error.setVisible(true);
                            error.setForeground(Color.RED);
                        }
                    }
                    isContinueClicked = true;
                }catch(Exception ex){//אם היה כל דבר אחר שהוא לא מספר שלם
                    errorCounter++;
                    error.setText("Input error: please input an integer");
                    if(errorCounter % 2 == 0){
                        error.setForeground(Color.BLUE);
                    }else{
                        error.setVisible(true);
                        error.setForeground(Color.RED);
                    }
                }
            }
        }
    }
}
