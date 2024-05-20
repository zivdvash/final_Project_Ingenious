import java.awt.Dimension;

import javax.swing.*;
/*קוד זה מתזמן את זרימת המשחק, מטפל בקלט של המשתמש ומנהל את התצוגה של מסכים שונים (כגון StartPanel, GameBoard, GameOver ו-StrategyResults).
 */

public class GameWindow {
    private JFrame frame;
    private final StartPanel pan;

    /*  - הוא מאתחל את ה-JFrame ומגדיר את הכותרת שלו.
    - זה יוצר מופע של StartPanel, שמוצג בתחילה.
    - זה ממתין למשתמש להתחיל את המשחק או להתחיל מצב ניתוח על ידי לחיצה על כפתורים ב-StartPanel.
    - אם המשתמש מתחיל את המשחק, הוא מאתחל אובייקט משחק חדש עם הפרמטרים שצוינו (שמות שחקנים, סוגים ואסטרטגיות).
    - זה מתחיל שרשור חדש ל-GameBoard.
    - הוא מחליף את חלונית התוכן של ה-JFrame כדי להציג את ה-GameBoard.
    - הוא ממתין לסיום המשחק, ואז מציג את מסך GameOver עם השחקנים הממוינים והתוצאות שלהם.
    - הוא ממתין שהמשתמש יבטל את מסך GameOver לפני שיפטר מה-JFrame.
    */
    GameWindow(){
        frame = new JFrame("Ingenious");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pan = new StartPanel(true);

        frame.setContentPane(pan);
        frame.pack();
        frame.setVisible(true);
        while(!pan.isGameStart() && !pan.isAnalysisStart() && !pan.isCancelled()){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // Re-interrupt the thread to set the interrupt flag
                Thread.currentThread().interrupt();
            }
        }
        if(pan.isGameStart()){
            play(0);
        }else if(pan.isAnalysisStart()){
            play(1);
        }else if(pan.isCancelled()){
            frame.dispose();
        }


    }

    public void play(int a) {
        if (a == 0) {
            Game game = new Game(pan.getNames(), pan.getPlayerTypes(), pan.getStrategies());
            GameBoard gameBoard = game.getGameBoard();
            new Thread(gameBoard).start();/*מנגנון זה מאפשר לבצע ברקע פעולות שעשויות לקחת זמן מה (כמו עדכון לוח משחק), ולהשאיר את ממשק המשתמש מגיב. בהקשר של משחק, זה יכול לשמש לעדכון רציף של מצב המשחק, עיבוד גרפיקה או טיפול בקלט משתמש מבלי להקפיא את ממשק המשתמש.*/
            frame.setContentPane(gameBoard);
            frame.pack();
            frame.setVisible(true);
            try {
                game.play();
            } catch (InterruptedException e) {
                // Re-interrupt the thread to set the interrupt flag
                Thread.currentThread().interrupt();
            }
            frame.dispose();
            frame = new GameOver(game.sortPlayers(), game.getSortedScores());
            frame.setPreferredSize(new Dimension(1500, 800));
            frame.pack();
            frame.setVisible(true);
            while (!((GameOver) frame).cancel()) {
                try {
                    Thread.sleep(0);//נותן שניה למשחק לרוץ ואז בודק האם הוא נגמר שוב
                } catch (InterruptedException e) {
                    // Re-interrupt the thread to set the interrupt flag
                    Thread.currentThread().interrupt();
                }
            }
            frame.dispose();
        } else if (a == 1) {
            pan.openAnalysisMode();
            frame.pack();
            frame.setVisible(true);
            while (!pan.isContinueClicked()) {
                try {
                    Thread.sleep(10);//נותן 10 מילישניות לבוט ללחוץ(משחק של בוטים) ואז בודק האם נלחץ כפתור המשך
                } catch (InterruptedException e) {
                    // Re-interrupt the thread to set the interrupt flag
                    Thread.currentThread().interrupt();
                }
            }
            if (pan.fastOrSlow() == 0) {

                frame.pack();
                frame.setVisible(true);
            }
            int[] wins = new int[pan.numPlayers()];
            initializeWins(wins);
            botGameOutcome(wins);
            outcomeWins(wins);
            BotGameResults strats = new BotGameResults(wins[0], wins[1]);
            frame.setContentPane(strats);
            frame.pack();
            frame.setVisible(true);
            while (!strats.end()) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    // Re-interrupt the thread to set the interrupt flag
                    Thread.currentThread().interrupt();
                }
            }
            frame.dispose();

        }

    }
    //מדפיס שחקנים בסוף המשחק
    public void outcomeWins(int[] wins) {
        for(int player = 0; player < pan.numPlayers(); player ++){
            System.out.println(wins[player]);
        }
    }
    //מאתחל את סדר הניצחון
    public void initializeWins(int[] wins) {
        for(int player = 0; player < pan.numPlayers(); player ++){
            wins[player] = 0;
        }
    }

    //מטפל בתוצאות של משחק בוטים
    public void botGameOutcome(int[] wins) {
        Game game;
        GameBoard gameBoard;
        for(int i = 0; i < pan.getGames(); i ++ ){
            game = new Game(pan.getNames(), pan.getPlayerTypes(), pan.getStrategies());
            gameBoard = game.getGameBoard();
            new Thread(gameBoard).start();
            if(pan.fastOrSlow() == 1){
                frame.setContentPane(gameBoard);
                frame.pack();
                frame.setVisible(true);
            }else{
                game.setSleepTimer(0);
            }
            try {
                game.play();
            } catch (InterruptedException e) {
                // Re-interrupt the thread to set the interrupt flag
                Thread.currentThread().interrupt();
            }

            for(int player = 0; player < game.numPlayers(); player ++){
                if(game.sortPlayers()[0] == game.getPlayers()[player]){
                    wins[player] += 1;
                }

            }

        }
    }


}
