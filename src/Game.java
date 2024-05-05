import java.util.*;

/*מחלקת 'משחק' היא לב ליבה של אפליקציית המשחק, שבה מנוהלים הכללים, ההתקדמות ומצבי המשחק. הוא מתזמר כיצד שחקנים מקיימים אינטראקציה עם המשחק, מעבד מהלכים, שומר על מצב לוח המשחק ובסופו של דבר קובע את תוצאת המשחק. באמצעות השיטות שלו, הוא מטמיע את ההיגיון של משחק אסטרטגיה מבוסס תורות, ומספק מסגרת למשחק הכוללת ניהול תור, פעולות שחקן, שמירת תוצאות ומעברי מצב משחק.*/
public class Game {
    public static final int ROWS = 30;
    public static final int COLS = 15;
    private static final int WHITE_CELL_COLOR = -1;
    private static final int MAX_SCORE = 18;
    private static final int MAX_HAND_PIECES = 18;
    private final Player[] players;
    private Player currentPlayer;
    private final Cell[][] staticBoard = new Cell[COLS][COLS];
    private final HashSet<Cell> dynamicBoard;

    private final int[][] emptyGrid;
    private final GameBoard gameBoard;
    private final PiecesBag PiecesBag;
    private boolean isGameOver;
    private Strategy[] gameStrategies;
    private int sleepTimer;
    private Player[] p;
    private int[] sortedScores;
    public final static int MAX_ROW_LENGTH = 11;
    private final int[][] directionsUpperPart = new int[][] { { 0, 1 }, { 0, -1 }, { -1, 0 }, { -1, -1 }, { 1, 1 },
            { 1, 0 } };
    private final int[][] directionsLowerPart = new int[][] { { 0, 1 }, { 0, -1 }, { -1, 1 }, { -1, 0 }, { 1, 0 },
            { 1, -1 } };
    private final int[][] directionsMiddleRow = new int[][] { { 0, 1 }, { 0, -1 }, { -1, 0 }, { -1, -1 }, { 1, 0 },
            { 1, -1 } };
    /*הבנאי של המחלקה `Game`. זה מאתחל משחק חדש עם שמות השחקנים, סוגי השחקנים (אנושי או מחשב) ואסטרטגיות (פשוט או אקראי).
     */
    Game(String[] names, int[] playerTypes, int[] strategies){
        setSleepTimer(100);
        PiecesBag = new PiecesBag();
        players = new Player[names.length];
        isGameOver = false;
        initializeStrategies();

        dynamicBoard = new HashSet<>();
        emptyGrid = new int[ROWS][COLS];

        SetPlayerValues(names, playerTypes, strategies);
        //new ComputerPlayer(names[a], getStrategy(strategies[a] - 1), new PlayerHand(PiecesBag))
        gameBoard = new GameBoard(this);
        initializeDynamicBoard();
        initializeStaticBoard();
        printGrid();

    }

    private void initializeStaticBoard() {
        // MAX_ROW_LENGTH may cause bugs
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                // Check if the position is valid on the hexagonal board
                if (isValidPosition(x, y)) {
                    int color = gameBoard.getHexColor()[x][y];
                    Cell cell = new Cell(x, y, color, null);
                    staticBoard[x][y] = cell;
                }
            }
        }

        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                // Check if the position is valid on the hexagonal board
                if (isValidPosition(x, y)) {
                    Cell cell = staticBoard[x][y];
                    HashSet<Cell> neighbors = findNeighbors(cell);
                    cell.setNeighbors(neighbors);
                }
            }
        }
    }
    private void printGrid() {
        for (int y = 0; y < staticBoard.length; y++) {
            System.out.println();
            for (int x = 0; x < staticBoard[y].length; x++) {
                if(staticBoard[x][y] != null) {
                    System.out.print(staticBoard[x][y].getColor());
                }
                else {
                    System.out.print(" ");
                }
            }
        }
    }
    private void initializeDynamicBoard() {
        // Iterate through all possible positions on the board
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                // Check if the position is valid on the hexagonal board
                if (isValidPosition(x, y)) {
                    int color = gameBoard.getHexColor()[x][y];

                    if (color != WHITE_CELL_COLOR) {
                        Cell coloredCell = staticBoard[x][y];
                        dynamicBoard.add(coloredCell);
                    }
                }
            }
        }
    }
    // Finds and returns a list of neighboring cells for a given cell
    private HashSet<Cell> findNeighbors(Cell cell) {

        HashSet<Cell> neighbors = new HashSet<>();
        int x = cell.getX();
        int y = cell.getY();

        // determines the suitable directions array, according to the row


        if (x >= 0 && x <= 5) {
            for (int[] direction : directionsMiddleRow) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (0 <= newX && newX < ROWS && 0 <= newY && newY < COLS)
                {
                    Cell neighbor = staticBoard[newX][newY];
                    if (neighbor != null) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        else if (x == 6) {
            for (int[] direction : directionsMiddleRow) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (0 <= newX && newX < ROWS && 0 <= newY && newY < COLS)
                {
                    Cell neighbor = staticBoard[newX][newY];
                    if (neighbor != null) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        else if (x >= 6 && x <= 11) {
            for (int[] direction : directionsMiddleRow) {
                int newX = x + direction[0];
                int newY = y + direction[1];
                if (0 <= newX && newX < ROWS && 0 <= newY && newY < COLS)
                {
                    Cell neighbor = staticBoard[newX][newY];
                    if (neighbor != null) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }



        return neighbors;
    }

    // Returns the Cell object at a given set of coordinates

    private boolean isValidPosition(int x, int y) {
        switch (x) {
            case 0:
            case 11:
                return y >= 0 && y <= 5; // 6 cells on the first and last rows
            case 1:
            case 10:
                return y >= 0 && y <= 6; // 7 cells on the second and second-to-last rows
            case 2:
            case 9:
                return y >= 0 && y <= 7; // 8 cells on the third and third-to-last rows
            case 3:
            case 8:
                return y >= 0 && y <= 8; // 9 cells on the fourth and fourth-to-last rows
            case 4:
            case 7:
                return y >= 0 && y <= 9; // 10 cells on the middle row
            case 6:
                return y >= 0 && y <= 10; //11 cells on the middle row
            default:
                return false;
        }
    }

    private void SetPlayerValues(String[] names, int[] playerTypes, int[] strategies) {
        for(int a = 0; a < names.length; a ++){
            if(playerTypes[a] == 0){//שם ערכים של שחקן אנושי
                players[a] = new HumanPlayer(names[a],new PlayerHand(PiecesBag));
            }else{//שם ערכים של שחקן ממוחשב
                players[a] = new ComputerPlayer(names[a], getStrategy(strategies[a] - 1), new PlayerHand(PiecesBag));
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
                SaveStartScores(startScores);
                HandTradeHandling();
                if(currentPlayer.getClass() == ComputerPlayer.class){//שחקן ממוחשב?
                    Thread.sleep(sleepTimer);//משהה את הביצוע למשך פרק זמן שצוין על ידי 'sleepTimer'
                }
                MakeSingleMove();
                SaveGridForComputerPlayer();
                currentPlayer.updateScore(getTurnScore(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));//עדכון נקודות
                //שיטה זו מעדכנת את לוח המשחק עם לוח חדש
                HashSet<Cell> newCells = createNewCells(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY());//עדכן לצמיתות את הייצוג הפנימי של המשחק של הלוח עם המהלך של השחקן הנוכחי.
                dynamicBoard.addAll(newCells);

                currentPlayer.removeCurrentPiece();
                isSecondPlay = ExtraTurnCheck(startScores, isSecondPlay);
                EndOfTurnHandling(isSecondPlay, extraTern);
                if(!isMoveRemaining() || isWinner()){//בדיקה אם נגמר המשחק
                    isGameOver = true;
                }
            }
            if(!isMoveRemaining() || isWinner()){//בדיקה אם נגמר המשחק אחרי התור
                isGameOver = true;
            }
        }
    }
    //שומר את המסך החדש עבור שחקן ממוחשב לצורך חישוב מהלך
    public void SaveGridForComputerPlayer()throws InterruptedException{
        gameBoard.updateComputerGrid(emptyGrid);//מאפס את הייצוג החזותי של לוח המשחק של שחקן המחשב למצב ריק או התחלתי
        if(currentPlayer.getClass() == ComputerPlayer.class){
            gameBoard.updateComputerGrid(toHexGrid(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));/*מעדכן את לוח המשחק כדי להציג את המהלך של שחקן המחשב,  השיטה `twoHexGrid(...)` מייצרת ייצוג רשת של המהלך של השחקן הנוכחי בהתבסס על כיוון החתיכה והקואורדינטות שבחרו, אשר לאחר מכן `computerGrid(...)` משתמש בהן כדי לעדכן את הייצוג החזותי על לוח המשחק.*/
            Thread.sleep(sleepTimer);
        }
    }
    //שומר ניקוד התחלתי בתור קדימויות חדש
    public void SaveStartScores(PriorityQueue<ColorScore> startScores) {
        for(ColorScore i : currentPlayer.getColorScores()){
            ColorScore Cs = new ColorScore(i.getColor(),i.getScore());
            startScores.add(Cs);
        }
    }
    //בדיקה האם יש תור נוסף
    public boolean ExtraTurnCheck(PriorityQueue<ColorScore> startScores, boolean isSecondPlay) {
        for (ColorScore x : currentPlayer.getColorScores()) {
            for (ColorScore i : startScores) {
                if (i.getColor() == x.getColor()) {
                    if (x.getScore() == MAX_SCORE && i.getScore() < MAX_SCORE) {//בודק האם מגיע עוד תור
                        isSecondPlay = true;
                    }
                }
            }
        }
        return isSecondPlay;
    }
    //מתעסק עם סיום התור
    public void EndOfTurnHandling(boolean isSecondPlay, ExtraTern extraTern) {
        if(!isSecondPlay){
            do{
                currentPlayer.addNewPiece();//אם הוא לא שיחק שוב תוסיף ותוריד חלק
            }while(currentPlayer.getHand().getSize() < MAX_HAND_PIECES);
        }
        if(extraTern != null){//אם צריך אז לסגור מסך
            extraTern.dispose();
        }
        currentPlayer.setOrientation(0);//להחזיר כיוון למקורי
        if(!isMoveRemaining() || isWinner()){//בדיקה אם נגמר המשחק
            isGameOver = true;
        }
    }
    //ביצוע מהלך של השחקן הנוכחי
    public void MakeSingleMove() {
        do{
            if(currentPlayer.getClass() == ComputerPlayer.class){//שחקן ממוחשב?
                if(currentPlayer.getCurrentPiece() != null){//לבדוק שיש חלק ביד
                    currentPlayer.getHand().addNewPiece(currentPlayer.getCurrentPiece());//מכניס חלק חדש
                    currentPlayer.removeCurrentPiece();//מוחק את הנוכחי
                }
            }
            currentPlayer.move();//תעשה מהלך
        }while(!checkLegalMove());//כל עוד המהלך לא חוקי
    }
    //אחראי על כל עניין החלפה של היד
    public void HandTradeHandling() throws InterruptedException {
        if(currentPlayer.checkHand() && currentPlayer.getClass() == HumanPlayer.class){
            HandTrade handTrade = new HandTrade();//פותח אינטראקציה של החלפת יד
            while(!handTrade.getIsClosed()){
                Thread.sleep(0);//לאפשר אינטרקצית משתמש בזמן שהלוח השני פתוח
            }
            if(handTrade.getIsTrade()){//אם המשתמש בחר להחליף את היד שלו
                currentPlayer.tradeHand();
            }
        }
    }
    //שיטה זו מגדירה את טיימר השינה המשמש למהלכים של שחקנים ממוחשבים
    public void setSleepTimer(int a){
        sleepTimer = a;
    }
    //שיטה זו מחזירה את סדר השחקנים על סמך התוצאות שלהם
    public int[] scoreOrder() {
        int[] scoreOrder = new int[players.length];
        PriorityQueue<Player> playerQueue = new PriorityQueue<>(Comparator.comparing(p -> p.getColorScores().peek().getScore()));

        // Enqueue players into the priority queue
        for (Player player : players) {
            playerQueue.offer(player);
        }

        // Dequeue players from the priority queue and record their order
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = playerQueue.poll();
            scoreOrder[i] = Arrays.asList(players).indexOf(currentPlayer);
        }

        return scoreOrder;
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
            SetPlayersScores(a, b);
            SetScoresMatrix(score, a, b);
            LeadingPlayerCheck(score);
            sortedScores[0]=p[0].getColorScores().peek().getScore();
            sortedScores[1]=p[1].getColorScores().peek().getScore();
        }

        return p;
    }
    //בודק את מצב ניקוד השחקנים לצורך תצוגת מנצח
    private void LeadingPlayerCheck(int[][] score) {
        boolean flage = false;
        int j = 0;

        while (!flage){
            if (score[0][j]> score[1][j]) {
                p[0]=players[0];
                p[1]=players[1];
                flage = true;
            }
            else if (score[0][j]< score[1][j]){
                p[0]=players[1];
                p[1]=players[0];
                flage = true;
            }
            else if (j==5){
                if (players[0].getName().compareTo(players[1].getName()) > 0) {
                    p[0] = players[0];
                    p[1] = players[1];
                }
                else {
                    p[0] = players[1];
                    p[1] = players[0];
                }
                flage = true;
            }
            j++;
        }
    }
    //מעביר את שני מערכי הניקוד למטריצה לצורך נוחות בהשוואה
    private static void SetScoresMatrix(int[][] score, int[] a, int[] b) {
        for (int i=0; i<2; i++) {
            for (int j=0; j<MAX_HAND_PIECES; j++) {
                if (i==0) {
                    score[i][j]= a[j];
                }
                if (i==1){
                    score[i][j]= b[j];
                }
            }
        }
    }

    //מכניס את הניקוד של כל שחקן למערך לצורך בדיקה של הניקוד
    private void SetPlayersScores(int[] a, int[] b) {
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
    }

    //שיטה זו מחזירה את הציונים הממוינים של השחקנים
    public int[] getSortedScores() {
        return sortedScores;
    }
    // שיטה זו מחזירה את לוח המשחק
    public GameBoard getGameBoard(){
        return gameBoard;
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
        currentPlayer.setPieceX(x);
        currentPlayer.setPieceY(y);
        currentPlayer.setTurnComplete(true);
    }
    // שיטה זו מחשבת את הניקוד לתורו של השחקן הנוכחי
    public int[] getTurnScore(int o, int x, int y){
        int[] newScore = {0,0,0,0,0,0};
        //makeTempGrid(o,x,y);
        newScore[currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor() - 1] += CalculateScore(x,y,dynamicBoard);
        newScore[currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor() - 1] += CalculateScore(getSecondX(o,x,y),getSecondY(o,x,y),dynamicBoard);
        return newScore;
    }
    //שיטה זו יוצרת לוח זמני לחישובים
    //לא נראלי שצריך כי עכשיו יש CELLS
    /*    public void makeTempGrid(int o, int x, int y){
        HashSet<Cell> cells =

    }
    */

    // שיטה זו מחשבת את הניקוד עבור הצבת חלק על הלוח
    public int CalculateScore(int xInit, int yInit, HashSet<Cell> tempGrid) {
        int score = 0;
        int x=xInit;
        int y=yInit;
        int color = -1;
        for (Cell cell : tempGrid) {
            if (x == cell.getX() && y == cell.getY())
                color = cell.getColor();
        }

        while ((x-2)>=0 && staticBoard[x-2][y].getColor() == color) {
            x-=2;
            score+=1;
        }
        x=xInit;
        while ((x+2)<30 && staticBoard[x+2][y].getColor()==color) {
            x+=2;
            score+=1;
        }
        x=xInit;
        while ((x-1)>=0 && (y-1)>=0 && staticBoard[x-1][y-1].getColor()==color) {
            x-=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y-1)>=0 && staticBoard[x+1][y-1].getColor()==color) {
            x+=1;
            y-=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x-1)>=0 && (y+1)<15 && staticBoard[x-1][y+1].getColor()==color) {
            x-=1;
            y+=1;
            score+=1;
        }
        x=xInit;
        y=yInit;
        while ((x+1)<30 && (y+1)<15 && staticBoard[x+1][y+1].getColor()==color) {
            x+=1;
            y+=1;
            score+=1;
        }
        return score;
    }
    //שיטה זו בודקת אם המהלך חוקי עבור השחקן הנוכחי
    /*אני סבור שלא צריך לרשום שם 29 אלב לעשות בכלל לוח של 15 על 15 אבל בסדר*/
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
                            if (staticBoard[CoordX][CoordY].getColor() == -1 && staticBoard[(CoordX - 1)][(CoordY - 1)].getColor() == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY - 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 1) {
                        if (CoordX < 29 && CoordY > 0) {
                            if (staticBoard[CoordX][CoordY].getColor()  == -1 && staticBoard[(CoordX + 1)][(CoordY - 1)].getColor() == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY - 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 2) {
                        if (CoordX < 28) {
                            if (staticBoard[CoordX][CoordY].getColor() == -1 && staticBoard[(CoordX + 2)][(CoordY)].getColor() == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 2, CoordY))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 3) {
                        if (CoordX < 29 && CoordY < 14)
                            if (staticBoard[CoordX][CoordY].getColor()  == -1 && staticBoard[(CoordX + 1)][(CoordY + 1)].getColor() == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX + 1, CoordY + 1))
                                    return true;
                            }

                    } else if (currentPlayer.getOrientation() == 4) {
                        if (CoordX > 0 && CoordY < 14) {
                            if (staticBoard[CoordX][CoordY].getColor()  == -1 && staticBoard[(CoordX - 1)][(CoordY + 1)].getColor() == -1) {
                                if (checkAround(color1, CoordX, CoordY) || checkAround(color2, CoordX - 1, CoordY + 1))
                                    return true;
                            }
                        }
                    } else if (currentPlayer.getOrientation() == 5) {
                        if (CoordX > 1) {
                            if (staticBoard[CoordX][CoordY].getColor()  == -1 && staticBoard[(CoordX - 2)][(CoordY)].getColor() == -1) {
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
    public boolean checkLegalMove(int x, int y) {
        if(currentPlayer.getCurrentPiece() != null){
            int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
            int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
            return checkLegalMove(currentPlayer.getOrientation(),x, y, color1, color2);
        }
        return false;
    }
    //שיטה זו בודקת אם המהלך חוקי עם הכיוון, הקואורדינטות והצבעים הנתונים
    public boolean checkLegalMove(int o, int x, int y, int color1, int color2) {
        if(x > -1 && y > -1){
            if (o==0) {
                if (x > 0 && y > 0) {
                    if (staticBoard[x][y].getColor()==-1 && staticBoard[(x -1)][(y -1)].getColor()==-1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x -1, y -1))
                            return true;
                    }
                }
            }
            else if (o==1) {
                if (x < 29 && y > 0) {
                    if (staticBoard[x][y].getColor()==-1 && staticBoard[(x +1)][(y -1)].getColor()==-1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x +1, y -1))
                            return true;
                    }
                }
            }
            else if (o==2) {
                if (x < 28) {
                    if (staticBoard[x][y].getColor()==-1 && staticBoard[(x +2)][(y)].getColor()==-1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x +2, y))
                            return true;
                    }
                }
            }
            else if (o==3) {
                if (x < 29 && y < 14)
                    if (staticBoard[x][y].getColor()==-1 && staticBoard[(x +1)][(y +1)].getColor()==-1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x +1, y +1))
                            return true;
                    }

            }
            else if (o==4) {
                if (x > 0 && y < 14) {
                    if (staticBoard[x][y].getColor() == -1 && staticBoard[(x - 1)][(y + 1)].getColor() == -1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x -1, y +1))
                            return true;
                    }
                }
            }
            else if (o==5) {
                if (x > 1) {
                    if (staticBoard[x][y].getColor() == -1 && staticBoard[(x - 2)][(y)].getColor() == -1) {
                        if (checkAround(color1, x, y) || checkAround(color2, x -2, y))
                            return true;
                    }
                }
            }
        }

        return false;
    }
    // שיטה זו בודקת אם הצבת יצירה בקואורדינטות הנתונות תהיה חוקית בהתבסס על משושים שכנים
    private boolean checkAround(int color, int x, int y) {
        HashSet<Cell> friends = staticBoard[x][y].getNeighbors();

        for (Cell friend : friends) {
            if (friend.getColor() == color) {
                return true;
            }
        }
        return false;
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
    public HashSet<Cell> createNewCells(int o, int x, int y) {
        return createNewCells(o, x, y,currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor(),currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor());
    }
    public HashSet<Cell> createNewCells(int o, int x, int y, int color1, int color2) {

        Cell firstCell = staticBoard[x][y];
        firstCell.setColor(color1);

        Cell secondCell = null;


        if (o==0) {
            secondCell = staticBoard[x -1][y -1];
        }
        else if (o==1) {
            secondCell = staticBoard[x +1][y -1];
        }
        else if (o==2) {
            secondCell = staticBoard[x +2][y];
        }
        else if (o==3) {
            secondCell = staticBoard[x + 1][y + 1];
        }
        else if (o==4) {
            secondCell = staticBoard[x - 1][y + 1];
        }
        else if (o==5) {
            secondCell = staticBoard[x - 2][y];
        }

        firstCell.setColor(color2);

        HashSet<Cell> cells = new HashSet<>();

        cells.add(firstCell);
        cells.add(secondCell);
        return new HashSet<>(cells);
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

    public int[][] toHexGrid(int o, int x, int y) {
        return toHexGrid(o, x, y,currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor(),currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor());
    }
    public int[][] toHexGrid(int o, int x, int y, int color1, int color2) {
        int[][] grid = new int[30][15];
        for (int i = 0; i<30; i++) {
            for (int j=0; j<15; j++) {
                grid[i][j]=0;
            }
        }
        grid[x][y] = color1;
        switch (o) {
            case 0: {
                grid[(x - 1)][(y - 1)] = color2;
            }
            case 1: {
                grid[(x + 1)][(y - 1)] = color2;
            } case 2: {
                grid[(x + 2)][(y)] = color2;
            } case 3: {
                grid[(x + 1)][(y + 1)] = color2;
            } case  4:{
                grid[(x - 1)][(y + 1)] = color2;
            } case  5: {
                grid[(x - 2)][(y)] = color2;
            }
        }
        return grid;
    }

    public Cell[][] getStaticBoard() {
        return staticBoard;
    }

    public HashSet<Cell> getDynamicBoard() {
        return dynamicBoard;
    }
}
