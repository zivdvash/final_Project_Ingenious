import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/*מחלקת 'משחק' היא לב ליבה של אפליקציית המשחק, שבה מנוהלים הכללים, ההתקדמות ומצבי המשחק. הוא מתזמר כיצד שחקנים מקיימים אינטראקציה עם המשחק, מעבד מהלכים, שומר על מצב לוח המשחק ובסופו של דבר קובע את תוצאת המשחק. באמצעות השיטות שלו, הוא מטמיע את ההיגיון של משחק אסטרטגיה מבוסס תורות, ומספק מסגרת למשחק הכוללת ניהול תור, פעולות שחקן, שמירת תוצאות ומעברי מצב משחק.*/
public class Game {

    private Player[] players;
    private Player currentPlayer;
    private int[][] grid;
    private int[][] tempGrid;
    private int[][] emptyGrid;
    private GameBoard gameBoard;
    private PiecesBag PiecesBag;
    private boolean isGameOver;
    private Strategy[] gameStrategies;
    private String[] playerNames;
    private int sleepTimer;
    private Player[] p;
    private int[] sortedScores;
    /*הבנאי של המחלקה `Game`. זה מאתחל משחק חדש עם שמות השחקנים, סוגי השחקנים (אנושי או מחשב) ואסטרטגיות (פשוט או אקראי).
     */
    Game(String[] names, int[] playerTypes, int[] strategies){
        setSleepTimer(500);
        PiecesBag = new PiecesBag();
        players = new Player[names.length];
        isGameOver = false;
        playerNames = names;
        initializeStrategies();
        grid = new int[30][15];
        emptyGrid = new int[30][15];

        for(int a = 0; a < names.length; a ++){
            if(playerTypes[a] == 0){//שם ערכים של שחקן אנושי
                players[a] = new HumanPlayer(names[a],new PlayerHand(PiecesBag));
            }else{//שם ערכים של שחקן ממוחשב
                players[a] = new ComputerPlayer(names[a], getStrategy(strategies[a] - 1), new PlayerHand(PiecesBag));
            }
        }
        //new ComputerPlayer(names[a], getStrategy(strategies[a] - 1), new PlayerHand(PiecesBag))
        gameBoard = new GameBoard(this);
        for (int x = 0; x < 30; x ++){
            for (int y = 0; y < 15; y ++){
                grid[x][y] = gameBoard.getHexColor()[x][y];//מאתחל כל משושה בלוח
                emptyGrid[x][y] = 0;//מאתחל כל משושה בלוח
            }
        }

    }
    //שיטה זו אחראית על ניהול המשחק. זה חוזר על פני השחקנים, ומאפשר להם לבצע מהלכים עד לסיום המשחק.
    public void play() throws InterruptedException{
        Comparator<ColorScore> comparator = Comparator.comparingInt(ColorScore::getScore);
        PriorityQueue<ColorScore> startScores = new PriorityQueue<>(comparator);
        boolean isSecondPlay = false;
        ExtraTern extraTern = null;
        while(!isGameOver){//ניהול תורים של שחקנים
            for(int a = 0; a < players.length && !isGameOver; a ++){//כל עוד לא הגמר המשחק וזה תור של שחקן
                if(isSecondPlay){//האם יש תור נוסף?
                    if(currentPlayer.getClass() == HumanPlayer.class){//לדעת אם לפתוח חלון של תור נוסף
                        extraTern = new ExtraTern();
                    }
                    if(a == 0){//אם לשחקן האחרון היה תור נוסף
                        a = players.length - 1;
                    }else{//תחזור לשחקן הקודם
                        a -= 1;
                    }
                }
                currentPlayer = players[a];
                currentPlayer.setTurnComplete(false);
                isSecondPlay = false;
                startScores.clear();
                for(ColorScore i : currentPlayer.getColorScores()){
                    ColorScore Cs = new ColorScore(i.getColor(),i.getScore());
                    startScores.add(Cs);
                }
                if(currentPlayer.checkHand() && currentPlayer.getClass() == HumanPlayer.class){
                    HandTrade handTrade = new HandTrade();//פותח אינטראקציה של החלפת יד לדעתי שימוש לא טוב במסך תצוגה ולוגיקה
                    while(!handTrade.getIsClosed()){
                        Thread.sleep(0);//לאפשר אינטרקצית משתמש בזמן שהלוח השני פתוח
                    }
                    if(handTrade.getIsTrade()){//אם המשתמש בחר להחליף את היד שלו
                        currentPlayer.tradeHand();
                    }
                }
                if(currentPlayer.getClass() == ComputerPlayer.class){//שחקן ממוחשב?
                    Thread.sleep(sleepTimer);//משהה את הביצוע למשך פרק זמן שצוין על ידי 'sleepTimer'
                }
                do{
                    if(currentPlayer.getClass() == ComputerPlayer.class){//שחקן ממוחשב?
                        if(currentPlayer.getCurrentPiece() != null){//לבדוק שיש חלק ביד
                            currentPlayer.getHand().addNewPiece(currentPlayer.getCurrentPiece());//מכניס חלק חדש
                            currentPlayer.removeCurrentPiece();//מוחק את הנוכחי
                        }
                    }
                    System.out.println(currentPlayer);
                    currentPlayer.move();//תעשה מהלך
                }while(!checkLegalMove());//כל עוד המהלך לא חוקי
                gameBoard.computerGrid(emptyGrid);//מאפס את הייצוג החזותי של לוח המשחק למצב ריק או התחלתי
                if(currentPlayer.getClass() == ComputerPlayer.class){
                    gameBoard.computerGrid(twoHexGrid(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));/*מעדכן את לוח המשחק כדי להציג את המהלך של שחקן המחשב.
                                                                                                                                             השיטה `twoHexGrid(...)` מייצרת ייצוג רשת של המהלך של השחקן הנוכחי בהתבסס על כיוון החתיכה והקואורדינטות שבחרו,
                                                                                                                                              אשר לאחר מכן `computerGrid(...)` משתמש בהן כדי לעדכן את הייצוג החזותי על לוח המשחק.*/
                    Thread.sleep(sleepTimer);
                }
                currentPlayer.updateScore(getTurnScore(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));//עדכון נקודות
                updateGrid(twoHexGrid(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));//עדכן לצמיתות את הייצוג הפנימי של המשחק של הלוח עם המהלך של השחקן הנוכחי.

                currentPlayer.removeCurrentPiece();
                for (ColorScore x : currentPlayer.getColorScores()) {
                    for (ColorScore i : startScores) {
                        if (i.getColor() == x.getColor()) {
                            if (x.getScore() == 18 && i.getScore() < 18) {//בודק האם מגיע עוד תור
                                isSecondPlay = true;
                            }
                        }
                    }
                }

                if(!isSecondPlay){
                    do{
                        currentPlayer.addNewPiece();//אם הוא לא שיחק שוב תוסיף ותוריד חלק
                    }while(currentPlayer.getHand().getSize() < 6);
                }
                if(extraTern != null){//אם צריך אז לסגור מסך
                    extraTern.dispose();
                }
                currentPlayer.setOrientation(0);//להחזיר כיוון למקורי
                if(!isMoveRemaining() || isWinner()){//בדיקה אם נגמר המשחק
                    isGameOver = true;
                }
            }

            if(!isMoveRemaining() || isWinner()){//בדיקה אם נגמר המשחק אחרי התור
                isGameOver = true;
            }
        }
    }
    //שיטה זו מגדירה את טיימר השינה המשמש למהלכים של שחקנים ממוחשבים
    public void setSleepTimer(int a){
        sleepTimer = a;
    }
    //שיטה זו מחזירה מערך של שמות שחקנים
    public String[] playerNames(){
        return playerNames;
    }

    //שיטה זו מחזירה את סדר השחקנים על סמך התוצאות שלהם
    public int[] scoreOrder() {
        int[] scoreOrder = new int[players.length];
        PriorityQueue<Player> playerQueue = new PriorityQueue<>(Comparator.comparing(p -> p.getColorScores().peek().getScore()));

        // Enqueue players into the priority queue
        for (int i = 0; i < players.length; i++) {
            playerQueue.offer(players[i]);
        }

        // Dequeue players from the priority queue and record their order
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = playerQueue.poll();
            scoreOrder[i] = Arrays.asList(players).indexOf(currentPlayer);
        }

        return scoreOrder;
    }


    //שיטה זו מחזירה את הציונים של השחקנים.
     public int[] scores(){
        int[] lowScore = new int[players.length];
        int[] scores = new int[players.length];
        for(int a = 0; a < players.length; a ++){
            for(ColorScore i : players[a].getColorScores()){
                lowScore[a] = players[a].getColorScores().peek().getScore();
            }

        }
        for(int a = 0; a < players.length; a ++){
            scores[scoreOrder()[a]] = lowScore[a];
        }
        return scores;

    }

    // שיטה זו מחזירה את סדר שמות השחקנים על סמך הניקוד שלהם
    public String[] nameOrder(){
        String[] nameOrder = new String[playerNames.length];
        for(int a = 0; a < players.length; a ++){
            nameOrder[scoreOrder()[a]] = playerNames[a];
        }
        return nameOrder;
    }
    // שיטה זו ממיינת את השחקנים על סמך התוצאות שלהם כל שחקן משובץ על פי הניקוד שלו
    public Player[] sortPlayers() {
        //אם יש רק 2 שחקנים
        /*- זה מאתחל 'ניקוד' של מערך דו-ממדי כדי להחזיק נקודות ממוינות של שני השחקנים לשם השוואה.
    - הוא ממיין את הניקוד של שני השחקנים ומאחסן את הניקוד הנמוך ביותר שלהם בשיטת 'setLowestScore'.
    - הוא משווה את ההניקוד הנמוך ביותר כדי לקבוע את סדר השחקנים. אם הניקוד הנמוך ביותר שלהם שווה, הוא מסתכל על הניקוד השני הנמוך ביותר כשובר שוויון כדי להכריע בדירוג.
    - לבסוף, הוא מקצה את השחקנים הממוינים ואת הניקוד הנמוכים ביותר שלהם למערכי 'p' ו-'sortedScores' בהתאמה.*/
        if (players.length==2) {
            p = new Player[2];
            sortedScores = new int[2];
            int[][] score = new int[2][6];
            int[] a = new int[6];
            int[] b = new int[6];
            int C = 0;
            for(ColorScore i: players[0].getColorScores()) {
                a[C] = i.getScore();
                C++;
            }
            C = 0;
            for(ColorScore i: players[1].getColorScores()) {
                b[C] = i.getScore();
                C++;
            }
            for (int i=0; i<2; i++) {
                for (int j=0; j<6; j++) {
                    if (i==0) {
                        score[i][j]=a[j];
                    }
                    if (i==1){
                        score[i][j]=b[j];
                    }
                }
            }
            if (score[0][0]>score[1][0]) {
                p[0]=players[0];
                p[1]=players[1];
            }
            else if (score[0][0]<score[1][0]){
                p[0]=players[1];
                p[1]=players[0];
            }
            else {
                if (score[0][1]>score[1][1]) {
                    p[0]=players[0];
                    p[1]=players[1];
                }
                else {
                    p[0]=players[1];
                    p[1]=players[0];
                }
            }
            sortedScores[0]=p[0].getColorScores().peek().getScore();
            sortedScores[1]=p[1].getColorScores().peek().getScore();
        }

        return p;
    }
    //שיטה זו מחזירה את הציונים הממוינים של השחקנים
    public int[] getSortedScores() {
        return sortedScores;
    }
    //שיטה זו מעדכנת את לוח המשחק עם לוח חדש
    public void updateGrid(int[][] newGrid){
        for(int x = 0; x < grid[0].length; x ++){
            for(int y = 0; y < grid.length; y ++){
                if(newGrid[y][x] != 0){
                    grid[y][x] = newGrid[y][x];
                }
            }
        }
        gameBoard.updateGrid(grid);
    }
    // שיטה זו מחזירה את לוח המשחק
    public GameBoard getGameBoard(){
        return gameBoard;
    }
    // שיטה זו מגדירה את התור הנוכחי עבור השחקן
    public void setCurrentTurn(boolean bool){
        currentPlayer.setCurrentTurn(bool);
    }
    //שיטה זו קובעת אם התור של השחקן הושלם
    public void setTurnComplete(boolean bool){
        currentPlayer.setTurnComplete(bool);
    }
    //שיטה זו מחזירה את האסטרטגיה על סמך המדד 1/2
    public Strategy getStrategy(int a){
        return gameStrategies[a];
    }
    //שיטה זו מחזירה את השחקן הנוכחי
    public Player getCurrentPlayer(){
        return currentPlayer;
    }
    //שיטה זו מאתחלת את אסטרטגיות המשחק
    public void initializeStrategies(){
        gameStrategies = new Strategy[2];
        gameStrategies[1] = new BasicStrategy(this); // BasicStrategy = 2
        gameStrategies[0] = new RandomStrategy(this);// RandomStrategy = 1
    }
    // שיטה זו מסובבת את החלק של השחקן הנוכחי
    public void rotate(int direction){
        ((HumanPlayer) currentPlayer).rotate(direction);
    }
    //שיטה זו מבטלת את הבחירה של היצירה של השחקן הנוכחי
    public void deselect(){
        ((HumanPlayer) currentPlayer).deselect();
    }
    //שיטה זו בוחרת חתיכה עבור השחקן הנוכח
    public void select(int piece){
        ((HumanPlayer) currentPlayer).selectPiece(piece);
    }
    //שיטה זו מגדירה חלק על הלוח עבור השחקן הנוכחי
    public void setPiece(int x, int y){//maybe have a method check legal move and then set turn complete
        ((HumanPlayer) currentPlayer).setPieceX(x);
        ((HumanPlayer) currentPlayer).setPieceY(y);
        currentPlayer.setTurnComplete(true);
    }
    // שיטה זו מחשבת את הניקוד לתורו של השחקן הנוכחי
    public int[] getTurnScore(int o, int x, int y){
        int[] newScore = {0,0,0,0,0,0};
        makeTempGrid(o,x,y);
        newScore[currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor() - 1] += score(x,y);
        newScore[currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor() - 1] += score(getSecondX(o,x,y),getSecondY(o,x,y));
        return newScore;
    }
    //שיטה זו יוצרת לוח זמני לחישובים
    public void makeTempGrid(int o, int x, int y){
        tempGrid = new int[30][15];
        for (int X = 0; X < 30; X ++){
            for (int Y = 0; Y < 15; Y ++){
                if(twoHexGrid(o,x,y)[X][Y] == 0){
                    tempGrid[X][Y] = grid[X][Y];
                }else{
                    tempGrid[X][Y] = twoHexGrid(o,x,y)[X][Y];
                }
            }
        }
    }
    // שיטה זו מחשבת את הניקוד עבור הצבת חלק על הלוח
    public int score(int xInit, int yInit) {
        //makeTempGrid(currentPlayer.getOrientation(),xInit,yInit);
        int score = 0;
        int x=xInit;
        int y=yInit;
        int color = tempGrid[x][y];
        while ((x-2)>=0 && grid[x-2][y]==color) {
            x-=2;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+2)<30 && grid[x+2][y]==color) {
            x+=2;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x-1)>=0 && (y-1)>=0 && grid[x-1][y-1]==color) {
            x-=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y-1)>=0 && grid[x+1][y-1]==color) {
            x+=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x-1)>=0 && (y+1)<15 && grid[x-1][y+1]==color) {
            x-=1;
            y+=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y+1)<15 && grid[x+1][y+1]==color) {
            x+=1;
            y+=1;
            score+=1;
        }
        return score;
    }
    // שיטה זו מחשבת את הניקוד עבור הצבת יצירה בלוח רזרבי
    public int score(int xInit, int yInit, int[][] spareGrid) {
        int score = 0;
        int x=xInit;
        int y=yInit;
        int color = spareGrid[x][y];
        while ((x-2)>=0 && grid[x-2][y]==color) {
            x-=2;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+2)<30 && grid[x+2][y]==color) {
            x+=2;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x-1)>=0 && (y-1)>=0 && grid[x-1][y-1]==color) {
            x-=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y-1)>=0 && grid[x+1][y-1]==color) {
            x+=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x-1)>=0 && (y+1)<15 && grid[x-1][y+1]==color) {
            x-=1;
            y+=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y+1)<15 && grid[x+1][y+1]==color) {
            x+=1;
            y+=1;
            score+=1;
        }
        //System.out.println("Score: " +score);
        return score;
    }
    //שיטה זו בודקת אם המהלך חוקי עבור השחקן הנוכחי
    public boolean checkLegalMove() {
        if(currentPlayer.getCurrentPiece() != null){
            int CoordX = currentPlayer.getPieceX();
            int CoordY = currentPlayer.getPieceY();

            if (currentPlayer.getCurrentPiece().getPrimaryHexagon() != null && currentPlayer.getCurrentPiece().getSecondaryHexagon() != null) {
                Hex primaryHex = currentPlayer.getCurrentPiece().getPrimaryHexagon();   // Potential null source
                Hex secondaryHex = currentPlayer.getCurrentPiece().getSecondaryHexagon(); // Potential null source
                int color1 = primaryHex.getColor();
                int color2 = secondaryHex.getColor();
                if(CoordX > -1 && CoordY > -1) {
                    if (currentPlayer.getOrientation() == 0) {
                        if (CoordX > 0 && CoordY > 0) {
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY - 1)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY - 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 1) {
                        if (CoordX < 29 && CoordY > 0) {
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY - 1)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY - 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 2) {
                        if (CoordX < 28) {
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 2)][(CoordY)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 2, CoordY))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 3) {
                        if (CoordX < 29 && CoordY < 14)
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX + 1)][(CoordY + 1)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY + 1))
                                    return true;
                            }

                    } else if (currentPlayer.getOrientation() == 4) {
                        if (CoordX > 0 && CoordY < 14) {
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY + 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 5) {
                        if (CoordX > 1) {
                            if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 2, CoordY))
                                    return true;
                            }
                        }
                    }
                }
           }
        }
        return false;
    }

    // שיטה זו בודקת אם מהלך חוקי עם הכיוון והקואורדינטות הנתונות
    public boolean checkLegalMove(int o, int x, int y) {
        if(currentPlayer.getCurrentPiece() != null){
            int CoordX = x;
            int CoordY = y;
            int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
            int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
            if(CoordX > -1 && CoordY > -1){
                if (currentPlayer.getOrientation()==0) {
                    if (CoordX > 0 && CoordY > 0) {
                        if (grid[CoordX][CoordY]==-1 && grid[(CoordX-1)][(CoordY-1)]==-1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-1, CoordY-1))
                                return true;
                        }
                    }
                }
                else if (currentPlayer.getOrientation()==1) {
                    if (CoordX < 29 && CoordY > 0) {
                        if (grid[CoordX][CoordY]==-1 && grid[(CoordX+1)][(CoordY-1)]==-1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+1, CoordY-1))
                                return true;
                        }
                    }
                }
                else if (currentPlayer.getOrientation()==2) {
                    if (CoordX < 28) {
                        if (grid[CoordX][CoordY]==-1 && grid[(CoordX+2)][(CoordY)]==-1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+2, CoordY))
                                return true;
                        }
                    }
                }
                else if (currentPlayer.getOrientation()==3) {
                    if (CoordX < 29 && CoordY < 14)
                        if (grid[CoordX][CoordY]==-1 && grid[(CoordX+1)][(CoordY+1)]==-1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+1, CoordY+1))
                                return true;
                        }

                }
                else if (currentPlayer.getOrientation()==4) {
                    if (CoordX > 0 && CoordY < 14) {
                        if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-1, CoordY+1))
                                return true;
                        }
                    }
                }
                else if (currentPlayer.getOrientation()==5) {
                    if (CoordX > 1) {
                        if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
                            if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-2, CoordY))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    //שיטה זו בודקת אם המהלך חוקי עם הכיוון, הקואורדינטות והצבעים הנתונים
    public boolean checkLegalMove(int o, int x, int y, int color1, int color2) {
        int CoordX = x;
        int CoordY = y;
        if(CoordX > -1 && CoordY > -1){
            if (o==0) {
                if (CoordX > 0 && CoordY > 0) {
                    if (grid[CoordX][CoordY]==-1 && grid[(CoordX-1)][(CoordY-1)]==-1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-1, CoordY-1))
                            return true;
                    }
                }
            }
            else if (o==1) {
                if (CoordX < 29 && CoordY > 0) {
                    if (grid[CoordX][CoordY]==-1 && grid[(CoordX+1)][(CoordY-1)]==-1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+1, CoordY-1))
                            return true;
                    }
                }
            }
            else if (o==2) {
                if (CoordX < 28) {
                    if (grid[CoordX][CoordY]==-1 && grid[(CoordX+2)][(CoordY)]==-1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+2, CoordY))
                            return true;
                    }
                }
            }
            else if (o==3) {
                if (CoordX < 29 && CoordY < 14)
                    if (grid[CoordX][CoordY]==-1 && grid[(CoordX+1)][(CoordY+1)]==-1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX+1, CoordY+1))
                            return true;
                    }

            }
            else if (o==4) {
                if (CoordX > 0 && CoordY < 14) {
                    if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 1)][(CoordY + 1)] == -1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-1, CoordY+1))
                            return true;
                    }
                }
            }
            else if (o==5) {
                if (CoordX > 1) {
                    if (grid[CoordX][CoordY] == -1 && grid[(CoordX - 2)][(CoordY)] == -1) {
                        if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX-2, CoordY))
                            return true;
                    }
                }
            }
        }

        return false;
    }
    // שיטה זו בודקת אם הצבת יצירה בקואורדינטות הנתונות תהיה חוקית בהתבסס על משושים שכנים
    private boolean checkAround(int color, int x, int y) {
        boolean legal = false;
        for (int i=0; i<6; i++) {
            if (((i==0||i==1) && y<1) || ((i==3||i==4) && y>13) || ((i==0||i==4) && x<1) || ((i==1||i==3) && x>28)
                    || (i==2 && x>27) || (i==5 && x<3)) {
            }
            else {
                if (grid[getSecondX(i, x, y)][getSecondY(i, x, y)]==color) {
                    legal = true;
                }
            }
        }
        return legal;
    }
    //שיטה זו מחזירה מערך של שחקנים
    public Player[] getPlayers(){
        return players;
    }
    // שיטה זו מחזירה את מספר השחקנים
    public int numPlayers(){
        return players.length;
    }
    //שיטה זו מחזירה את קואורדינטת ה-x של המשושה השני על סמך כיוון
    public int getSecondX(int o, int x, int y) {
        if (o==0) {
            return x-1;
        }
        else if (o==1) {
            return x+1;
        }
        else if (o==2) {
            return x+2;
        }
        else if (o==3) {
            return x+1;
        }
        else if (o==4) {
            return x-1;
        }
        else if (o==5) {
            return x-2;
        }
        return -1;
    }
    //שיטה זו מחזירה את קואורדינטת ה-y של המשושה השני על סמך כיוון
    public int getSecondY(int o, int x, int y) {
        if (o==0) {
            return y-1;
        }
        else if (o==1) {
            return y-1;
        }
        else if (o==2) {
            return y;
        }
        else if (o==3) {
            return y+1;
        }
        else if (o==4) {
            return y+1;
        }
        else if (o==5) {
            return y;
        }
        return -1;
    }
    /*הפונקציה `twoHexGrid` מייצרת לוח חדש (מערך) המייצגת מהלך פוטנציאלי על לוח המשחק. להלן פירוט של מה שהוא עושה:

    1. **מאתחל לוח חדש:** הוא יוצר לוח חדש בגודל 30x15 , מאתחל את כל התאים שלו ל-0, מה שאומר חללים ריקים.

    2. **מגדיר את המשושה הראשי של היצירה:** הוא מגדיר את תא הלוח בקואורדינטות `(x, y)` לצבע המשושה הראשי של החתיכה של השחקן הנוכחי. הצבע מתקבל מ-`currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor()`.

    3. **מגדיר את המשושה המשני של החתיכה בהתבסס על כיוון:** בהתאם לכיוון (`o`) של היצירה, הוא מגדיר תא לוח נוסף סמוך למשושה הראשי לצבע המשושה המשני. ישנם שש כיוונים אפשריים (0 עד 5), כל אחד מתאים למיקום יחסי שונה של המשושה המשני לזה הראשי:
         - `0`: המשושה המשני נמצא מצפון-מערב למשושה הראשוני.
         - `1`: המשושה המשני נמצא מצפון-מזרח למשושה הראשוני.
         - `2`: המשושה המשני נמצא ישירות ממזרח למשושה הראשוני.
         - `3`: המשושה המשני נמצא מדרום מזרח למשושה הראשוני.
         - `4`: המשושה המשני נמצא מדרום-מערב למשושה הראשוני.
         - `5`: המשושה המשני נמצא ישירות ממערב למשושה הראשוני.

    הפונקציה מחשבת את המיקום הנכון עבור המשושה המשני בהתבסס על הכיוון ומעדכנת את תא הלוח במיקום זה לצבע המשושה המשני.

    4. **מחזיר את הלוח החדש:** לבסוף, הוא מחזיר את הלוח החדש הזה, שמכיל כעת שני ערכים שאינם אפס המתאימים לצבעים של שני המשושים של היצירה, כאשר כל שאר התאים נשארים ב-0 (ריק).

    הלוח שנוצר מייצג רק את המהלך הנוכחי שנחשב או נעשה, לא את כל מצב המשחק. זוהי "תמונת מצב" של המקום בו השחקן רוצה למקם את היצירה שלו על הלוח, המשמשת להצגה של המהלך, בדיקת חוקיותו או חישוב השפעתו, מבלי לשנות את רשת הלוח הראשי של המשחק. גישה זו מאפשרת בדיקת מהלכים היפותטיים וחישוב ניקוד מבלי להשפיע על מצב המשחק בפועל עד לאישור המהלך.*/
    public int[][] twoHexGrid(int o, int x, int y) {
        int[][] grid = new int[30][15];
        for (int i = 0; i<30; i++) {
            for (int j=0; j<15; j++) {
                grid[i][j]=0;
            }
        }
        int CoordX = x;
        int CoordY = y;
        grid[x][y] = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
        if (o==0) {
            grid[(CoordX-1)][(CoordY-1)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        else if (o==1) {
            grid[(CoordX+1)][(CoordY-1)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        else if (o==2) {
            grid[(CoordX+2)][(CoordY)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        else if (o==3) {
            grid[(CoordX+1)][(CoordY+1)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        else if (o==4) {
            grid[(CoordX-1)][(CoordY+1)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        else if (o==5) {
            grid[(CoordX-2)][(CoordY)]=currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        }
        return grid;
    }
    public int[][] twoHexGrid(int o, int x, int y, int color1, int color2) {
        int[][] grid = new int[30][15];
        for (int i = 0; i<30; i++) {
            for (int j=0; j<15; j++) {
                grid[i][j]=0;
            }
        }
        //    	int CoordX = currentPlayer.getPieceX();
        //    	int CoordY = currentPlayer.getPieceY();
        int CoordX = x;
        int CoordY = y;
        grid[x][y] = color1;
        if (o==0) {
            grid[(CoordX-1)][(CoordY-1)]=color2;
        }
        else if (o==1) {
            grid[(CoordX+1)][(CoordY-1)]=color2;
        }
        else if (o==2) {
            grid[(CoordX+2)][(CoordY)]=color2;
        }
        else if (o==3) {
            grid[(CoordX+1)][(CoordY+1)]=color2;
        }
        else if (o==4) {
            grid[(CoordX-1)][(CoordY+1)]=color2;
        }
        else if (o==5) {
            grid[(CoordX-2)][(CoordY)]=color2;
        }
        return grid;
    }
    //בודק ניצחון
    public boolean isWinner(){
        try{
            boolean isWinner = true;
            for (ColorScore i : currentPlayer.getColorScores()){
                if(i.getScore() < 18){
                    isWinner = false;
                }
            }
            return isWinner;
        }catch(Exception e){
            return false;
        }
    }
    //בודק האם נשארו מהלכים אפשריים על הלוח
    public boolean isMoveRemaining(){
        boolean isMove = false;//need boolean in loop
        try{
            for(int x = 0; x < 30 && !isMove; x ++){
                for(int y = 0; y < 15 && !isMove; y ++){
                    for(int o = 0; o < 6 && !isMove; o ++){
                        for(int piece = 0; piece < currentPlayer.getHand().getSize(); piece ++){
                            if(checkLegalMove(o,x,y,currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor(),currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor())){
                                isMove = true;
                            }
                        }
                    }
                }
            }
            return isMove;
        }catch(Exception e){
            return isMove;
        }

    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }


}


