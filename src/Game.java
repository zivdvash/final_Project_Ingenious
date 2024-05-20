import java.util.*;

//מחלקת 'משחק' היא לב ליבה של אפליקציית המשחק, שבה מנוהלים הכללים, ההתקדמות ומצבי המשחק. הוא מתזמר כיצד שחקנים מקיימים אינטראקציה עם המשחק, מעבד מהלכים, שומר על מצב לוח המשחק ובסופו של דבר קובע את תוצאת המשחק. באמצעות השיטות שלו, הוא מטמיע את ההיגיון של משחק אסטרטגיה מבוסס תורות, ומספק מסגרת למשחק הכוללת ניהול תור, פעולות שחקן, שמירת תוצאות ומעברי מצב משחק./
public class Game {
    private static final int ROWS = 30;
    private static final int COLS = 15;
    private static final int MAX_SCORE = 18;
    private static final int MAX_HAND_PIECE = 6;
    private static final int BOARD_START_INDEX = 0;
    private static final int WHITE_CELL =-1;
    private static final int NULL_CELL = 0;
    private final Player[] players;
    private Player currentPlayer;
    private Map<Integer, Integer> colorCells;
    private Map<Integer, Integer> whiteCells;
    private Map<Integer, Integer> cloneBoard;
    private int[][] emptyGrid;
    private GameBoard gameBoard;
    private final PiecesBag PiecesBag;
    private boolean isGameOver;
    private Strategy[] gameStrategies;
    private int sleepTimer;
    private Player[] playersOrder;
    private int[] sortedScores;

    /*הבנאי של המחלקה Game. זה מאתחל משחק חדש עם שמות השחקנים, סוגי השחקנים (אנושי או מחשב) ואסטרטגיות (פשוט או אקראי).
     */
    Game(String[] names, int[] playerTypes, int[] strategies) {
        setSleepTimer(100);
        PiecesBag = new PiecesBag();
        players = new Player[names.length];
        isGameOver = false;
        initializeStrategies();
        emptyGrid = new int[ROWS][COLS];
        setPlayerValues(names, playerTypes, strategies);
        gameBoard = new GameBoard(this);
        initializeBoard();
        emptyBoard();

    }

    public void initializeBoard() {
        cloneBoard = new HashMap<Integer, Integer>();
        colorCells = new HashMap<Integer, Integer>();
        whiteCells = new HashMap<Integer, Integer>();
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                if (gameBoard.getHexColor()[x][y] == -1)
                    whiteCells.put(x * ROWS + y, gameBoard.getHexColor()[x][y]);
                else if (gameBoard.getHexColor()[x][y] != -1 && gameBoard.getHexColor()[x][y] != 0) {
                    colorCells.put(x * ROWS + y, gameBoard.getHexColor()[x][y]);

                }
            }
        }
    }

    private void emptyBoard() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                emptyGrid[x][y] = 0;//מאתחל כל משושה בלוח
            }
        }
    }

    private void setPlayerValues(String[] names, int[] playerTypes, int[] strategies) {
        for (int playrIndex = 0; playrIndex < names.length; playrIndex++) {
            if (playerTypes[playrIndex] == 0) {//שם ערכים של שחקן אנושי
                players[playrIndex] = new HumanPlayer(names[playrIndex], new PlayerHand(PiecesBag));
            } else {//שם ערכים של שחקן ממוחשב
                players[playrIndex] = new ComputerPlayer(names[playrIndex], getStrategy(strategies[playrIndex] - 1), new PlayerHand(PiecesBag));
            }
        }
    }

    //שיטה זו אחראית על ניהול המשחק. זה חוזר על פני השחקנים, ומאפשר להם לבצע מהלכים עד לסיום המשחק.
    public void play() throws InterruptedException {
        Comparator<ColorScore> comparator = Comparator.comparingInt(ColorScore::getScore);
        PriorityQueue<ColorScore> startScores = new PriorityQueue<>(comparator);
        boolean isSecondPlay = false;
        ExtraTern extraTern = null;
        while (!isGameOver) {//ניהול תורים של שחקנים
            for (int playerNum = 0; playerNum < players.length && !isGameOver; playerNum++) {//כל עוד לא הגמר המשחק וזה תור של שחקן
                if (isSecondPlay) {//האם יש תור נוסף?
                    if (currentPlayer.getClass() == HumanPlayer.class) {//לדעת אם לפתוח חלון של תור נוסף
                        extraTern = new ExtraTern();
                    }
                    if (playerNum == 0) {//אם לשחקן האחרון היה תור נוסף
                        playerNum = players.length - 1;
                    } else {//תחזור לשחקן הקודם
                        playerNum -= 1;
                    }
                }
                currentPlayer = players[playerNum];
                currentPlayer.setTurnComplete(false);
                isSecondPlay = false;
                startScores.clear();
                saveStartScores(startScores);
                handTradeHandling();
                if (currentPlayer.getClass() == ComputerPlayer.class) {//שחקן ממוחשב?
                    Thread.sleep(sleepTimer);//משהה את הביצוע למשך פרק זמן שצוין על ידי 'sleepTimer'
                }
                makeSingleMove();
                saveGridForComputerPlayer();
                currentPlayer.updateScore(getTurnScore(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));//עדכון נקודות
                updateBoard(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY());//עדכן לצמיתות את הייצוג הפנימי של המשחק של הלוח עם המהלך של השחקן הנוכחי.
                currentPlayer.removeCurrentPiece();
                isSecondPlay = extraTurnCheck(startScores, isSecondPlay);
                endOfTurnHandling(isSecondPlay, extraTern);
                if (!isMoveRemaining() || isWinner()) {//בדיקה אם נגמר המשחק
                    isGameOver = true;
                }
            }
            if (!isMoveRemaining() || isWinner()) {//בדיקה אם נגמר המשחק אחרי התור
                isGameOver = true;
            }
        }
    }

    //שומר את המסך החדש עבור שחקן ממוחשב לצורך חישוב מהלך
    public void saveGridForComputerPlayer() throws InterruptedException {
        gameBoard.computerGrid(emptyGrid);//מאפס את הייצוג החזותי של לוח המשחק של שחקן המחשב למצב ריק או התחלתי
        if (currentPlayer.getClass() == ComputerPlayer.class) {
            gameBoard.computerGrid(makeTempGridForGameBoard(currentPlayer.getOrientation(), currentPlayer.getPieceX(), currentPlayer.getPieceY()));/*מעדכן את לוח המשחק כדי להציג את המהלך של שחקן המחשב,  השיטה twoHexGrid(...) מייצרת ייצוג לוח של המהלך של השחקן הנוכחי בהתבסס על כיוון החתיכה והקואורדינטות שבחרו, אשר לאחר מכן computerGrid(...) משתמש בהן כדי לעדכן את הייצוג החזותי על לוח המשחק.*/
            Thread.sleep(sleepTimer);
        }
    }

    //שומר ניקוד התחלתי בתור קדימויות חדש
    public void saveStartScores(PriorityQueue<ColorScore> startScores) {
        for (ColorScore cs : currentPlayer.getColorScores()) {
            ColorScore Cs = new ColorScore(cs.getColor(), cs.getScore());
            startScores.add(Cs);
        }
    }

    //בדיקה האם יש תור נוסף
    public boolean extraTurnCheck(PriorityQueue<ColorScore> startScores, boolean isSecondPlay) {
        for (ColorScore currentCs : currentPlayer.getColorScores()) {
            for (ColorScore cs : startScores) {
                if (cs.getColor() == currentCs.getColor()) {
                    if (currentCs.getScore() == MAX_SCORE && cs.getScore() < MAX_SCORE) {//בודק האם מגיע עוד תור
                        isSecondPlay = true;
                    }
                }
            }
        }
        return isSecondPlay;
    }

    //מתעסק עם סיום התור
    public void endOfTurnHandling(boolean isSecondPlay, ExtraTern extraTern) {
        if (!isSecondPlay) {
            do {
                currentPlayer.addNewPiece();//אם הוא לא שיחק שוב תוסיף ותוריד חלק
            } while (currentPlayer.getHand().getSize() < MAX_HAND_PIECE);
        }
        if (extraTern != null) {//אם צריך אז לסגור מסך
            extraTern.dispose();
        }
        currentPlayer.setOrientation(0);//להחזיר כיוון למקורי
        if (!isMoveRemaining() || isWinner()) {//בדיקה אם נגמר המשחק
            isGameOver = true;
        }
    }

    //ביצוע מהלך של השחקן הנוכחי
    public void makeSingleMove() {
        do {
            if (currentPlayer.getClass() == ComputerPlayer.class) {//שחקן ממוחשב?
                if (currentPlayer.getCurrentPiece() != null) {//לבדוק שיש חלק ביד
                    currentPlayer.getHand().addNewPiece(currentPlayer.getCurrentPiece());//מכניס חלק חדש
                    currentPlayer.removeCurrentPiece();//מוחק את הנוכחי
                }
            }
            currentPlayer.move();//תעשה מהלך
        } while (!checkLegalMoveForMouseMove());//כל עוד המהלך לא חוקי
    }

    //אחראי על כל עניין החלפה של היד
    public void handTradeHandling() throws InterruptedException {
        if (currentPlayer.checkHand() && currentPlayer.getClass() == HumanPlayer.class) {
            HandTrade handTrade = new HandTrade();//פותח אינטראקציה של החלפת יד
            while (!handTrade.getIsClosed()) {
                Thread.sleep(0);//לאפשר אינטרקצית משתמש בזמן שהלוח השני פתוח
            }
            if (handTrade.getIsTrade()) {//אם המשתמש בחר להחליף את היד שלו
                currentPlayer.tradeHand();
            }
        }
    }

    //שיטה זו מגדירה את טיימר השינה המשמש למהלכים של שחקנים ממוחשבים
    public void setSleepTimer(int sleepTime) {
        sleepTimer = sleepTime;
    }


    // שיטה זו ממיינת את השחקנים על סמך התוצאות שלהם כל שחקן משובץ על פי הניקוד שלו
    public Player[] sortPlayers() {
        //אם יש רק 2 שחקנים
        /*- זה מאתחל 'ניקוד' של מערך דו-ממדי כדי להחזיק נקודות ממוינות של שני השחקנים לשם השוואה.
    - הוא ממיין את הניקוד של שני השחקנים ומאחסן את הניקוד הנמוך ביותר שלהם בשיטת 'setLowestScore'.
    - הוא משווה את ההניקוד הנמוך ביותר כדי לקבוע את סדר השחקנים. אם הניקוד הנמוך ביותר שלהם שווה, הוא מסתכל על הניקוד השני הנמוך ביותר כשובר שוויון כדי להכריע בדירוג.
    - לבסוף, הוא מקצה את השחקנים הממוינים ואת הניקוד הנמוכים ביותר שלהם למערכי 'p' ו-'sortedScores' בהתאמה.*/

        playersOrder = new Player[players.length];
        sortedScores = new int[players.length];
        int[][] score = new int[playersOrder.length][MAX_HAND_PIECE];
        int[] p1 = new int[MAX_HAND_PIECE];
        int[] p2 = new int[MAX_HAND_PIECE];
        setPlayersScores(p1, p2);
        setScoresMatrix(score, p1, p2);
        leadingPlayerCheck(score);
        sortedScores[0] = playersOrder[0].getColorScores().peek().getScore();
        sortedScores[1] = playersOrder[1].getColorScores().peek().getScore();


        return playersOrder;
    }

    //בודק את מצב ניקוד השחקנים לצורך תצוגת מנצח
    private void leadingPlayerCheck(int[][] score) {
        boolean flage = false;
        int color = 0;

        while (!flage&&color<6) {
            if (score[0][color] > score[1][color]) {
                playersOrder[0] = players[0];
                playersOrder[1] = players[1];
                flage = true;
            } if(score[0][color] < score[1][color]) {
                playersOrder[0] = players[1];
                playersOrder[1] = players[0];
                flage = true;
            }
            color++;
        }
        // ממיין לפי שמות
        if (color == MAX_HAND_PIECE-1) {
            if (players[0].getName().compareTo(players[1].getName()) > 0) {
                playersOrder[0] = players[0];
                playersOrder[1] = players[1];
            } else {
                playersOrder[0] = players[1];
                playersOrder[1] = players[0];
            }
        }
    }

    //מעביר את שני מערכ הניקוד למטריצה לצורך נוחות בהשוואה
    private static void setScoresMatrix(int[][] score, int[] p1, int[] p2) {
        for (int color = 0; color < MAX_HAND_PIECE; color++) {
            score[0][color] = p1[color];
            score[1][color] = p2[color];

        }
    }

    //מכניס את הניקוד של כל שחקן למערך לצורך בדיקה של הניקוד
    private void setPlayersScores(int[] p1, int[] p2) {
        int color = 0;
        for (ColorScore cs : players[0].getColorScores()) {
            p1[color] = cs.getScore();
            color++;
        }
        color = 0;
        for (ColorScore cs : players[1].getColorScores()) {
            p2[color] = cs.getScore();
            color++;
        }
    }

    //שיטה זו מחזירה את הציונים הממוינים של השחקנים
    public int[] getSortedScores() {
        return sortedScores;
    }

    //שיטה זו מעדכנת את לוח המשחק
    public void updateBoard(int Orientation, int x,int y) {
        colorCells.put(x * ROWS+ y, getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor());
        whiteCells.remove(x * ROWS+ y);
        colorCells.put(getSecondX(Orientation,x,y) * ROWS+ getSecondY(Orientation,x,y), getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor());
        whiteCells.remove(getSecondX(Orientation,x,y) * ROWS+ getSecondY(Orientation,x,y));
        gameBoard.updateGrid(colorCells);
    }

    // שיטה זו מחזירה את לוח המשחק
    public GameBoard getGameBoard() {
        return gameBoard;
    }

    //שיטה זו מחזירה את האסטרטגיה על סמך המדד 1/2
    public Strategy getStrategy(int strategyNum) {
        return gameStrategies[strategyNum];
    }

    //שיטה זו מחזירה את השחקן הנוכחי
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    //שיטה זו מאתחלת את אסטרטגיות המשחק
    public void initializeStrategies() {
        gameStrategies = new Strategy[2];
        gameStrategies[1] = new BasicStrategy(this); // BasicStrategy = 2
        gameStrategies[0] = new RandomStrategy(this);// RandomStrategy = 1
    }

    // שיטה זו מסובבת את החלק של השחקן הנוכחי
    public void rotate(int direction) {
        ((HumanPlayer) currentPlayer).rotate(direction);
    }

    //שיטה זו מבטלת את הבחירה של היצירה של השחקן הנוכחי
    public void deselect() {
        ((HumanPlayer) currentPlayer).deselect();
    }

    //שיטה זו בוחרת חתיכה עבור השחקן הנוכח
    public void select(int piece) {
        ((HumanPlayer) currentPlayer).selectPiece(piece);
    }

    //שיטה זו מגדירה חלק על הלוח עבור השחקן הנוכחי
    public void setPiece(int x, int y) {//maybe have a method check legal move and then set turn complete
        currentPlayer.setPieceX(x);
        currentPlayer.setPieceY(y);
        currentPlayer.setTurnComplete(true);
    }
//שיטה זו מחשבת ומחזירה את הניקוד של התור
    public int[] getTurnScore(int orientation, int x, int y) {
        int[] newScore = {0, 0, 0, 0, 0, 0};
        makeCloneBoardWithXY(orientation, x, y,currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor(),currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor());//מעדכן את הלוח הזמני כדי לבצע חישוב של נקודות
        newScore[currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor() - 1] += calculateScoreForScoreBoard(x, y, cloneBoard.get(x*ROWS+y));//חישוב נקודות על משושה ראשי
        newScore[currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor() - 1] += calculateScoreForScoreBoard(getSecondX(orientation, x, y), getSecondY(orientation, x, y), cloneBoard.get(getSecondX(orientation, x, y)*ROWS+getSecondY(orientation, x, y)));//חישוב נקודות על משושה משני
        return newScore;
    }

    //שיטה זו יוצרת לוח זמני לחישובים
    public Map<Integer,Integer> makeCloneBoardWithXY(int Orientation, int x, int y,int color1,int color2) {
        cloneBoard.clear();
        cloneBoard.putAll(colorCells);
        return makeCloneBoard(Orientation, x, y,color1,color2);
    }


    // שיטה זו מחשבת את הניקוד עבור הצבת חלק על הלוח
    public int calculateScoreForScoreBoard(int xInit, int yInit, Map<Integer, Integer> tempGrid) {
        if (tempGrid.get(xInit * ROWS + yInit) != null){
            int color = tempGrid.get(xInit * ROWS  + yInit);
            return calculateScoreForScoreBoard(xInit, yInit, color);
        }
        return 0;
    }
    //מחשב ניקוד כולל לכל הכיוונים
    private int calculateScoreForScoreBoard(int xInit, int yInit, int color) {
        int score = 0;
        // calculate left side
        score = calculateLeftSide(color, xInit, yInit, score);
        score = calculateRightSide(color, xInit, yInit, score);
        score = calculateBottomLeft(color, xInit, yInit, score);
        score = calculateTopLeft(color, xInit, yInit, score);
        score = calculateBottomRight(color, xInit, yInit, score);
        score = calculateTopRight(color, xInit, yInit, score);
        return score;
    }
    //מחשב ניקוד למעלה
    private int calculateTopRight(int color, int x, int y, int score) {
        while ((x + 1) < ROWS && (y + 1) < COLS && colorCells.containsKey((x + 1) * ROWS + y + 1) && colorCells.get((x + 1) * ROWS + y + 1) == color) {
            x += 1;
            y += 1;
            score += 1;
        }
        return score;
    }
    //מחשב ניקוד לימין למטה
    private int calculateBottomRight(int color, int x, int y, int score) {
        while ((x - 1) >= BOARD_START_INDEX && (y + 1) < COLS && colorCells.containsKey((x - 1) * ROWS + y + 1) && colorCells.get((x - 1) * ROWS + y + 1) == color) {
            x -= 1;
            y += 1;
            score += 1;
        }
        return score;
    }
    //מחשב ניקוד לשמאל למעלה
    private int calculateTopLeft(int color, int x, int y, int score) {
        while ((x + 1) < ROWS && (y - 1) >= BOARD_START_INDEX && colorCells.containsKey((x + 1) * ROWS + y - 1) && colorCells.get((x + 1) * ROWS  + y - 1) == color) {
            x += 1;
            y -= 1;
            score += 1;
        }
        return score;
    }
    //מחשב ניקוד לשמאל למטה
    private int calculateBottomLeft(int color, int x, int y, int score) {
        while ((x - 1) >= BOARD_START_INDEX && (y - 1) >= BOARD_START_INDEX && colorCells.containsKey((x - 1) * ROWS  + y - 1) && colorCells.get((x - 1) * ROWS + y - 1) == color) {
            x -= 1;
            y -= 1;
            score += 1;
        }
        return score;
    }

    //מחשב ניקוד לימין
    private int calculateRightSide(int color, int x, int y, int score) {
        while ((x + 2) < ROWS && colorCells.containsKey((x + 2) * ROWS + y) && colorCells.get((x + 2) * ROWS+ y) == color) {
            x += 2;
            score += 1;
        }
        return score;
    }
    // מחשב ניקוד לשמאל
    private int calculateLeftSide(int color, int x, int y, int score) {
        while ((x - 2) >= BOARD_START_INDEX && colorCells.containsKey((x - 2) * ROWS + y) && colorCells.get((x - 2) * ROWS + y) == color) {
            x -= 2;
            score += 1;
        }
        return score;
    }

    //מחשב ניקוד עבור תצוגה על המסך לכן הוא משתשמש במטריצה (לתצוגה של הניקוד משמאל למעלה)
    public int calculateScoreForScoreBoard(int xInit, int yInit, int[][] tempGrid) {
        int color = tempGrid[xInit][yInit];
        return calculateScoreForScoreBoard(xInit, yInit, color);
    }

    //שיטה זו בודקת אם המהלך חוקי עבור השחקן הנוכחי
    public boolean checkLegalMoveForMouseMove() {
        if (currentPlayer.getCurrentPiece() != null) {
            int CordX = currentPlayer.getPieceX();
            int CordY = currentPlayer.getPieceY();

            if (currentPlayer.getCurrentPiece().getPrimaryHexagon() != null && currentPlayer.getCurrentPiece().getSecondaryHexagon() != null) {
                Hex primaryHex = currentPlayer.getCurrentPiece().getPrimaryHexagon();   // Potential null source
                Hex secondaryHex = currentPlayer.getCurrentPiece().getSecondaryHexagon(); // Potential null source
                int color1 = primaryHex.getColor();
                int color2 = secondaryHex.getColor();
                if (CordX > -1 && CordY > -1) {
                    if (currentPlayer.getOrientation() == 0) {
                        return checkBottomLeft(CordX, CordY, color1, color2);
                    } else if (currentPlayer.getOrientation() == 1) {
                        return checkTopLeft(CordX, CordY, color1, color2);
                    } else if (currentPlayer.getOrientation() == 2) {
                        return checkRightSide(CordX, CordY, color1, color2);
                    } else if (currentPlayer.getOrientation() == 3) {
                        return checkTopRight(CordX, CordY, color1, color2);

                    } else if (currentPlayer.getOrientation() == 4) {
                        if (checkBottomRight(CordX, CordY))
                            return checkAround(color1, CordX, CordY) || checkAround(color2, CordX - 1, CordY + 1);
                    } else if (currentPlayer.getOrientation() == 5) {
                        if (CordX > 1) {
                            if (whiteCells.get(CordX * ROWS + CordY) != null && whiteCells.get((CordX - 2) * ROWS + CordY) != null) {
                                return checkAround(color1, CordX, CordY) || checkAround(color2, CordX - 2, CordY);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
//בודק מהלך בימין למעלה
    private boolean checkTopRight(int cordX, int cordY, int color1, int color2) {
        if (cordX < 29 && cordY < 14)
            if (whiteCells.get(cordX * ROWS + cordY) != null && whiteCells.get((cordX + 1) * ROWS + (cordY + 1)) != null) {
                return checkAround(color1, cordX, cordY) || checkAround(color2, cordX + 1, cordY + 1);
            }
        return false;
    }
    //בודק מהלך בימין
    private boolean checkRightSide(int cordX, int cordY, int color1, int color2) {
        if (cordX < 28) {
            if (whiteCells.get(cordX * ROWS + cordY) != null && whiteCells.get((cordX + 2) * ROWS + cordY) != null) {
                return checkAround(color1, cordX, cordY) || checkAround(color2, cordX + 2, cordY);
            }
        }
        return false;
    }
    //בודק מהלך בשמאל למעלה
    private boolean checkTopLeft(int cordX, int cordY, int color1, int color2) {
        if (cordX < 29 && cordY > 0) {
            if (whiteCells.get(cordX * ROWS + cordY) != null && whiteCells.get((cordX + 1) * ROWS + (cordY - 1)) != null) {
                return checkAround(color1, cordX, cordY) || checkAround(color2, cordX + 1, cordY - 1);
            }
        }
        return false;
    }
    //בודק מהלך בשמאל למטה
    private boolean checkBottomLeft(int cordX, int cordY, int color1, int color2) {
        if (cordX > 0 && cordY > 0) {
            if (whiteCells.get(cordX * ROWS + cordY) != null && whiteCells.get((cordX - 1) * ROWS + (cordY - 1)) != null) {
                return checkAround(color1, cordX, cordY) || checkAround(color2, cordX - 1, cordY - 1);

            }
        }
        return false;
    }

    public Map<Integer, Integer> getWhiteCells() {
        return whiteCells;
    }

    // שיטה זו בודקת אם מהלך חוקי עם הכיוון והקואורדינטות הנתונות
    public boolean checkLegalMoveForeScoreBoard(int x, int y) {
        int xy = x*ROWS+y;
        if (currentPlayer.getCurrentPiece() != null) {
            int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
            int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
            return checkLegalMovePermanent(currentPlayer.getOrientation(), xy, color1, color2);
        }
        return false;
    }

    //שיטה זו בודקת אם המהלך חוקי עם הכיוון, הקואורדינטות והצבעים הנתונים
    public boolean checkLegalMovePermanent(int orientation, int xy, int color1, int color2) {
        int y = xy % ROWS  ;
        int x = (xy - y)/ROWS;
        if (x > -1 && y > -1) {
            if (orientation == 0) {
                return checkBottomLeft(x, y, color1, color2);
            } else if (orientation == 1) {
                return checkTopLeft(x, y, color1, color2);
            } else if (orientation == 2) {
                return checkRightSide(x, y, color1, color2);
            } else if (orientation == 3) {
                return checkTopRight(x, y, color1, color2);
            } else if (orientation == 4) {
                if (checkBottomRight(x, y)) return checkAround(color1, x, y) || checkAround(color2, x - 1, y + 1);
            } else if (orientation == 5) {
                if (x > 1) {
                    if (whiteCells.get(x * ROWS + y) != null && whiteCells.get((x - 2) * ROWS + y) != null) {
                        return checkAround(color1, x, y) || checkAround(color2, x - 2, y);
                    }
                }
            }
        }
        return false;
    }
    //בודק מהלך בימין למטה
    private boolean checkBottomRight(int x, int y) {
        if (x > 0 && y < 14) {
            return whiteCells.get(x * ROWS + y) != null && whiteCells.get((x - 1) * ROWS + (y + 1)) != null;
        }
        return false;
    }
//בודק את כל החלקים מסביב בשביל לדעת אם יש לנו צבע תואם באחת מהמשבצות ליד
    private boolean checkAround(int color, int x, int y) {
        boolean legal = false;
        for (int orientation=0; orientation<MAX_HAND_PIECE; orientation++) {
            if (!(((orientation==0||orientation==1) && y<1) || ((orientation==3||orientation==4) && y>13) || ((orientation==0||orientation==4) && x<1) || ((orientation==1||orientation==3) && x>28)
                    || (orientation==2 && x>27) || (orientation==5 && x<3))) {
                if (colorCells.containsKey(getSecondX(orientation, x, y)*ROWS+getSecondY(orientation, x, y)) && colorCells.get(getSecondX(orientation, x, y)*ROWS+getSecondY(orientation, x, y))==color){
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
    public int getSecondX(int orientation, int x, int y) {
        int[] calculateOrientations = {-1, 1, 2, 1, -1, -2};
        return calculateOrientations[orientation]+x;
    }
    //שיטה זו מחזירה את קואורדינטת ה-y של המשושה השני על סמך כיוון
    public int getSecondY(int orientation, int x, int y) {
        int[] calculateOrientations = {-1, -1, 0, 1, 1, 0};
        return calculateOrientations[orientation]+y;
    }
    /*הפונקציה twoHexGrid מייצרת לוח חדש (מערך) המייצגת מהלך פוטנציאלי על לוח המשחק. להלן פירוט של מה שהוא עושה:

    1. *מאתחל לוח חדש:* הוא יוצר לוח חדש בגודל 30x15 , מאתחל את כל התאים שלו ל-0, מה שאומר חללים ריקים.

    2. *מגדיר את המשושה הראשי של היצירה:* הוא מגדיר את תא הלוח בקואורדינטות (x, y) לצבע המשושה הראשי של החתיכה של השחקן הנוכחי. הצבע מתקבל מ-currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor().

    3. *מגדיר את המשושה המשני של החתיכה בהתבסס על כיוון:* בהתאם לכיוון (o) של היצירה, הוא מגדיר תא לוח נוסף סמוך למשושה הראשי לצבע המשושה המשני. ישנם שש כיוונים אפשריים (0 עד 5), כל אחד מתאים למיקום יחסי שונה של המשושה המשני לזה הראשי:
         - 0: המשושה המשני נמצא מצפון-מערב למשושה הראשוני.
         - 1: המשושה המשני נמצא מצפון-מזרח למשושה הראשוני.
         - 2: המשושה המשני נמצא ישירות ממזרח למשושה הראשוני.
         - 3: המשושה המשני נמצא מדרום מזרח למשושה הראשוני.
         - 4: המשושה המשני נמצא מדרום-מערב למשושה הראשוני.
         - 5: המשושה המשני נמצא ישירות ממערב למשושה הראשוני.

    הפונקציה מחשבת את המיקום הנכון עבור המשושה המשני בהתבסס על הכיוון ומעדכנת את תא הלוח במיקום זה לצבע המשושה המשני.

    4. *מחזיר את הלוח החדש:* לבסוף, הוא מחזיר את הלוח החדש הזה, שמכיל כעת שני ערכים שאינם אפס המתאימים לצבעים של שני המשושים של היצירה, כאשר כל שאר התאים נשארים ב-0 (ריק).

    הלוח שנוצר מייצג רק את המהלך הנוכחי שנחשב או נעשה, לא את כל מצב המשחק. זוהי "תמונת מצב" של המקום בו השחקן רוצה למקם את היצירה שלו על הלוח, המשמשת להצגה של המהלך, בדיקת חוקיותו או חישוב השפעתו, מבלי לשנות את רשת הלוח הראשי של המשחק. גישה זו מאפשרת בדיקת מהלכים היפותטיים וחישוב ניקוד מבלי להשפיע על מצב המשחק בפועל עד לאישור המהלך.*/

    public int[][] makeTempGridForGameBoard(int orientation, int x, int y) {
        int color1 = currentPlayer.getCurrentPiece().getPrimaryHexagon().getColor();
        int color2 = currentPlayer.getCurrentPiece().getSecondaryHexagon().getColor();
        int[][] grid = new int[ROWS][COLS];
        for (int i = 0; i<ROWS; i++) {
            for (int j=0; j<COLS; j++) {
                grid[i][j]=0;
            }
        }
        grid[x][y] = color1;
        if (orientation==0) {
            grid[(x -1)][(y -1)]=color2;
        }
        else if (orientation==1) {
            grid[(x +1)][(y -1)]=color2;
        }
        else if (orientation==2) {
            grid[(x +2)][(y)]=color2;
        }
        else if (orientation==3) {
            grid[(x +1)][(y +1)]=color2;
        }
        else if (orientation==4) {
            grid[(x -1)][(y +1)]=color2;
        }
        else if (orientation==5) {
            grid[(x -2)][(y)]=color2;
        }
        return grid;
    }
//מייצר שיכפול של הלוח
    public Map<Integer,Integer> makeCloneBoard(int Orientation, int x, int y, int color1, int color2) {
        cloneBoard.put(x*ROWS+y,color1);
        cloneBoard.put(getSecondX(Orientation,x,y)*ROWS+getSecondY(Orientation,x,y),color2);
        return cloneBoard;
    }


    //בודק ניצחון
    public boolean isWinner(){
        try{
            boolean isWinner = true;
            for (ColorScore i : currentPlayer.getColorScores()){
                if(i.getScore() < MAX_SCORE){
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
        Set<Integer> keys = whiteCells.keySet();
        try {
            for (int xy : keys){
                for (int Orientation = 0; Orientation < MAX_HAND_PIECE && !isMove; Orientation++) {
                    for (int piece = 0; piece < currentPlayer.getHand().getSize(); piece++) {
                        if (checkLegalMovePermanent(Orientation, xy, currentPlayer.getHand().getPiece(piece).getPrimaryHexagon().getColor(), currentPlayer.getHand().getPiece(piece).getSecondaryHexagon().getColor())) {
                            isMove = true;
                        }
                    }
                }
            }


            return isMove;
        }catch(Exception e){
            return isMove;
        }

    }

    public Map<Integer,Integer> getColorCells() {
        return colorCells;
    }
}
