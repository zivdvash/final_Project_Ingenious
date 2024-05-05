import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
/*`GameBoard` פועלת כמרכיב החזותי והאינטראקטיבי המרכזי של המשחק. הוא מגשר על המצב הלוגי של המשחק עם המצגת הגרפית שלו, מטפל בתשומות משתמש לפעולות משחק, ומעדכן את האלמנטים החזותיים בתגובה להתקדמות המשחק, מה שמבטיח חווית שחקן מגיבה ומושכת.*/
public class GameBoard extends JPanel implements Runnable,MouseListener,MouseMotionListener{
    /*שדות:
    1. **משושה**: מערך דו-ממדי של אובייקטים 'מצולעים' המייצגים את המשושים של לוח המשחק.
    2. **HandPieces**: מערך דו-ממדי של אובייקטים 'פוליגון' המייצגים את כלי היד של השחקן.
    3. **gameBoardTempGrid**: מערך דו-ממדי המייצג את לוח המשחק הזמני.
    4. **piece**: אובייקט 'מצולע' המייצג כלי משחק.
    5. **hexColor**: מערך דו-ממדי המייצג את צבעי המשושים על לוח המשחק.
    6. **רוחב, אורך**: מידות לוח המשחק.
    7. **X, Y, stoX, stoY**: משתנים עבור קואורדינטות עכבר.
    8. **isHumanPlayer**: בוליאני המציין אם השחקן הנוכחי הוא אנושי.
    9. **rotateClockwise, rotateCounterClockwise, returnPiece, scoreBox**: אובייקטים 'Rectangle2D' עבור אינטראקציה של ממשק משתמש.
    10. **משחק**: התייחסות לאובייקט המשחק.
    11. **כיוון**: מספר שלם המייצג את הכיוון של היצירה הנוכחית.
    12. **colors, colorcord**: מערכים המייצגים צבעים והערכים המספריים המתאימים להם.
    13. **ניקוד1, ניקוד2**: משתנים לאחסון תוצאות השחקנים.
    14. **computerGrid**: מערך דו-ממדי המייצג את הלוח של שחקן המחשב.
    */
    private  static  final int ROWS = 30;
    private  static  final int  COLS = 15;
    private  static  final int  MAX_HAND_PIECE = 6;
    private  static  final int  MAX_SCORE = 18;
    private static final Polygon[][] hexagon = new Polygon[ROWS][COLS];
    private static final Polygon[][] handPieces = new Polygon[MAX_HAND_PIECE][2];
    private int[][] gameBoardTempGrid;
    private Polygon piece;

    private static final int[][]hexColor = new int[ROWS][COLS]; //Contains value representing color of a hex on the grid - for actual game, not initializing board
    private static final int width = 1500;
    private static final int length = 800;
    private int X, Y, stoX, stoY;
    private boolean isHumanPlayer;
    private static final Rectangle2D rotateClockwise = new Rectangle2D.Double(width - 175, length -175, 175, 175);
    private static final Rectangle2D rotateCounterClockwise = new Rectangle2D.Double(width/3 , length -175, 175, 175);
    private static final Rectangle2D returnPiece = new Rectangle2D.Double(width - 175, 0, 175, 175);
    private static final Rectangle2D scoreBox = new Rectangle2D.Double(width/3, 0, 175, 175);
    private final Game game;
    private int orientation;
    private int score1;
    private int score2;
    private int[][] computerGrid;
    //אתחל את לוח המשחק, מגדיר רכיבי ממשק משתמש, מאתחל את הלוח ומאתחל את חלקי היד.
    public GameBoard(Game game){ //JFrame made in tester class
        this.game = game;
        setBackground(Color.WHITE);
        setVisible(true);
        setPreferredSize(new Dimension(width,length));
        addMouseListener(this);
        addMouseMotionListener(this);
        initializeGrid();
        makeBoard();
        makeHand();
        computerGrid = new int[ROWS][COLS];
        for(int y = 0; y < COLS; y++){
            for(int x = 0; x < ROWS;x++){
                computerGrid[x][y] = 0;
            }
        }
    }
   //מצייר רכיבי ממשק משתמש, ניקוד ולוח המשחק
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        try{
            Graphics2D g2d = (Graphics2D)g;
            g.setColor(Color.BLACK);
            g2d.setFont(new Font("Futura", Font.BOLD,14));
            g2d.draw(rotateClockwise);
            g2d.drawString("Rotate Clockwise", width - 175, length - 175);
            g2d.draw(rotateCounterClockwise);
            g2d.drawString("Rotate Counter Clockwise", width/3 , length - 175);
            g2d.draw(returnPiece);
            g2d.drawString("Return Piece", width - 175, 175);
            g2d.draw(scoreBox);
            g2d.drawString(game.getCurrentPlayer().getName(), width - 1450, length - 160);
            g.drawLine(500,0,500,800);//separates 3rd of board from rest. should paint the side components to the left of it.
            g.drawRect(50,650,400,140);
            paintScore(g);
            g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor()));
            g.drawPolygon(makeHex(width/3 + 40, 50)); //getColor for hexagon
            g.fillPolygon(makeHex(width/3 + 40, 50)); //getColor for hexagon
            ShowScore(g, g2d);
        }catch(Exception e){}
        paintBoard(g);
    }

    private void ShowScore(Graphics g, Graphics2D g2d) {
        if(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor() == game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()){
            score1 = score1 + score2;
            g.setColor(Color.BLACK);
            g2d.drawString(Integer.toString(score1), width/3 +80, 54); //getScore for the string
        }else{
            g.setColor(Color.BLACK);
            g2d.drawString(Integer.toString(score1), width/3 +80, 54); //getScore for the string
            g2d.drawString(Integer.toString(score2), width/3 +80, 129);
            g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()));
            g.drawPolygon(makeHex(width/3 + 40, 125));
            g.fillPolygon(makeHex(width/3 + 40, 125));
        }
        score1 = 0;
        score2 = 0;
    }

    //מחזירה את מערך hexColor 2D
    public int[][] getHexColor(){
        return hexColor;
    }
    //מסובב את היצירה הנוכחית
    public void rotate(int direction){
        game.rotate(direction);
    }
    //מאתחל את חלקי היד
    private void makeHand(){
        //score drawing below
        int c=85;
        for(int x = 0; x < MAX_HAND_PIECE; x++){
            handPieces[x][0] = makeScoreHex(c, 693);
            c+=65;
        }
        c = 85;
        for(int x = 0; x < MAX_HAND_PIECE; x++){
            handPieces[x][1] = makeScoreHex(c, 745);
            c+=65;
        }
    }
    // מצייר את הניקוד על לוח המשחק
    private void paintScore(Graphics g){
        horizontalLines(g);
        verticalLines(g);
        //score drawing below
        g.drawRect(50,650,400,140);
        int c=85;
        for (int counter = 0;counter < game.getCurrentPlayer().getHand().getSize(); counter++){
            game.getCurrentPlayer().getHand().getPiece(counter);
            int color=game.getCurrentPlayer().getHand().getPiece(counter).getSecondaryHexagon().getColor();
            g.setColor(pickColor(color));
            g.fillPolygon(makeScoreHex(c,693));
            color=game.getCurrentPlayer().getHand().getPiece(counter).getPrimaryHexagon().getColor();
            g.setColor(pickColor(color));
            g.fillPolygon(makeScoreHex(c,745));
            c+=65;
        }
    }
    //מצייר קווים אנכיים על לוח המשחק.
    private void verticalLines(Graphics g){
        int change=50;
        int yInit = 80;
        int yInit2=93;
        for(int boxes = 0; boxes < game.getPlayers().length; boxes++){
            for (int counter2=0;counter2 <= MAX_SCORE;counter2++){
                g.drawLine(change,yInit,change,yInit + 105);
                g.drawString(""+counter2 ,change +5,yInit2);
                change+=23;
            }
            yInit2+=140;
            yInit += 140;
            change = 50;
        }
    }
    //מצייר קווים אופקיים על לוח המשחק
    private void horizontalLines(Graphics g){
        int change = 80;
        for (int counter=0;counter< game.getPlayers().length;counter++){
            g.drawString(game.getPlayers()[counter].getName(),50,change-15);
            g.drawRect(50,change,435,105);//380 * 10
            g.setColor(Color.RED);
            int constant=50;
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],3)&& c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+15,24,15);
            }

            g.setColor(Color.BLUE);
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],5) && c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+30,24,15);
            }
            g.setColor(Color.GREEN);
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],4) && c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+45,24,15);
            }
            g.setColor(new Color(255,128,0));//orange
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],0) && c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+60,24,15);
            }
            g.setColor(Color.YELLOW);
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],1) && c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+75,24,15);
            }
            g.setColor(Color.MAGENTA);
            for (int c=0;c<=getScoreByColor(game.getPlayers()[counter],2) && c <= MAX_SCORE;c++)
            {
                g.fillRect(constant+(23 * c),change+90,24,15);
            }
            g.setColor(Color.BLACK);
            g.drawLine(50,change+15,483,change+15);//x1, y1, x2, y2
            g.drawLine(50,change+30,483,change+30);
            g.drawLine(50,change+45,483,change+45);
            g.drawLine(50,change+60,483,change+60);
            g.drawLine(50,change+75,483,change+75);
            g.drawLine(50,change+90,483,change+90);
            change+=140;
        }
    }
    // Get the score for a specific color from the player's colorScores PriorityQueue
    public int getScoreByColor(Player player, int color) {
        // Iterate over the colorScores PriorityQueue
        for (ColorScore colorScore : player.getColorScores()) {
            // Check if the color matches the desired color
            if (colorScore.getColor() == color) {
                // Return the score associated with the color
                return colorScore.getScore();
            }
        }
        // If the color is not found, return a default value (e.g., 0)
        return 0; // or any other default value as per your requirement
    }

    // יוצר משושה לתצוגת הניקוד
    private Polygon makeScoreHex(int x, int y){
        Polygon hex = new Polygon();
        double init,value;
        for(int a = 0; a<=MAX_HAND_PIECE; a++){
            init = Math.PI/6;
            value = Math.PI / 3.0 * a;
            hex.addPoint((int)(Math.round(x + Math.sin(value+init) * 30)), (int)(Math.round(y + Math.cos(value+init) * 30)));
        }
        return hex;
    }
    //מאתחל את לוח המשחק
    private void initializeGrid(){
        for(int x = 0; x < ROWS; x++){
            for(int y = 0; y < COLS;y++){
                if((y == 0 || y == 14) && (x< 8 || x >23)){
                    hexagon[x][y] = null;
                }else if((y == 1 || y == 13) && (x< 6 || x >24)){
                    hexagon[x][y] = null;
                }else if((y == 2 || y == 12) && (x< 5 || x >25)){
                    hexagon[x][y] = null;
                }else if((y == 3 || y == 11) && (x< 4 || x >26)){
                    hexagon[x][y] = null;
                }else if((y == 4 || y == 10) && (x< 3 || x >27)){
                    hexagon[x][y] = null;
                }else if((y == 5 || y == 9) && (x< 2 || x >28)){
                    hexagon[x][y] = null;
                }else if((y == 6 || y == 8) && x < 1){
                    hexagon[x][y] = null;
                }else{
                    SetHexes(x, y);
                }
            }
        }
    }
//מכניס ערך התחלתי לכל משושה
    private void SetHexes(int x, int y) {
        if(x % 2 == 0 && y % 2 == 0){
            if(x >= 10 && x < 20)
                hexagon[x][y] = makeHex((int)((width/3 + 110)+ x *87*.6*.5) - 1, (y *45 + 80));
            else if(x >=20)
                hexagon[x][y] = makeHex((int)((width/3 + 110)+ x *87*.6*.5) - 2, (y *45 + 80));
            else
                hexagon[x][y] = makeHex((int)((width/3 + 110)+ x *87*.6*.5), (y *45 + 80));
            hexColor[x][y] = -1;
        }
        else if(!(x % 2 == 0) && !(y % 2 == 0)){
            if(x >= 11 && x < 21)
                hexagon[x][y] = makeHex((int)(((width/3 + 110)+ x *87*.6 *.5)) - 1 , (y *45 + 80));
            else if(x >=21)
                hexagon[x][y] = makeHex((int)(((width/3 + 110)+ x *87*.6 *.5)) - 2, (y *45 + 80));
            else
                hexagon[x][y] = makeHex((int)(((width/3 + 110)+ x *87*.6 *.5)) , (y *45 + 80));
            hexColor[x][y] = -1;
        }
    }

    //יוצר כלי משחק
    private void makePiece(/*Piece myPiece*/int x, int y){
        piece = makeHex(x,y);
    }
    //מכוון את החלק הנוכחי
    private void orientPiece(Graphics g){
        if(game.getCurrentPlayer().getCurrentPiece()!= null && game.getCurrentPlayer().getClass() == HumanPlayer.class){
            makePiece(X,Y);
            g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getPrimaryHexagon().getColor()));
            g.fillPolygon(piece);
            g.drawPolygon(piece);
            if(orientation == 0)
                makePiece((int)(X - 87*.6 *.5),Y - 45);
            if(orientation == 1)
                makePiece((int)(X + 87*.6 *.5),Y - 45);
            if(orientation == 2)
                makePiece(X + 50,Y );
            if(orientation == 3)
                makePiece((int)(X + 87*.6 *.5),Y + 45);
            if(orientation == 4)
                makePiece((int)(X - 87*.6 *.5),Y + 45);
            if(orientation == 5)
                makePiece(X - 50,Y );
            g.setColor(pickColor(game.getCurrentPlayer().getCurrentPiece().getSecondaryHexagon().getColor()));
            g.fillPolygon(piece);
            g.drawPolygon(piece);
        }

    }
    //מחזירה את הצבע על סמך הערך המספרי
    private Color pickColor(int myColor){
        //  orange = 1, yellow = 2, purple = 3, red = 4, green = 5, blue = 6
        if(myColor == 1)
            return new Color(255,128,0);
        else if(myColor == 2)
            return Color.YELLOW;
        else if(myColor == 3)
            return Color.MAGENTA;
        else if(myColor == 4)
            return Color.RED;
        else if(myColor == 5)
            return Color.GREEN;
        else if(myColor == 6)
            return Color.BLUE;
        else
            return null;
    }
    // יוצר לוח משחק זמני
    private void makeGameBoardTempGrid(int x, int y, int o){
        gameBoardTempGrid = new int[ROWS][COLS];
        for (int X = 0; X < ROWS; X ++){
            for (int Y = 0; Y < COLS; Y ++){
                if(game.twoHexGrid(o,x,y)[X][Y] == 0){
                    gameBoardTempGrid[X][Y] = game.getGrid()[X][Y];
                }else{
                    gameBoardTempGrid[X][Y] = game.twoHexGrid(o,x,y)[X][Y];
                }
            }
        }
    }
    //Make a piece follow the mouse method: input - piece
    //צובע את לוח המשחק
    private void paintBoard(Graphics g){
        boolean onSpace = false;
        orientation = game.getCurrentPlayer().getOrientation();
        for(int x = 1; x<ROWS; x++){
            for(int y = 0; y<COLS;y++){
                onSpace = PaintBaseGrid(g, x, y, onSpace);
            }

        }
        EmphasizeBotMove(g);
        EmphasizePiece(g, onSpace);
        g.setColor(Color.BLACK);
        orientPiece(g);

    }
//שם את הצבעים בפינות הלוח
    private boolean PaintBaseGrid(Graphics g, int x, int y, boolean onSpace) {
        if(!(hexagon[x][y] == null)){
            if(x == 1 || x == 29 || x ==28 || y == 0 ||  y == 14 || hexagon[x -2][y] == null || hexagon[x +2][y] == null)
                g.setColor(Color.GRAY);
            else if(y ==1 || y == 13 || x == 3 || x == 27 || x == 26 || hexagon[x -4][y] == null || hexagon[x +4][y] == null)
                g.setColor(Color.LIGHT_GRAY);
            if(hexagon[x][y].contains(X,Y) ){
                onSpace = true;
                stoX = x;
                stoY = y;
                getScoreToShow(x, y);
            }if(computerGrid[x][y]!=0){//הצגה של ניקוד לשחקן מחשב
                onSpace = true;
                getScoreToShow(x, y);
            }
            if(hexColor[x][y] != -1)
                g.setColor(pickColor(hexColor[x][y]));
            g.fillPolygon(hexagon[x][y]); //draws hex

            g.setColor(Color.BLACK);
            g.drawPolygon(hexagon[x][y]); // draws hex outline

            ((Graphics2D) g).setStroke(new BasicStroke(1));
            g.setColor(Color.WHITE);
        }
        return onSpace;
    }
//מחשב ומציג את הצבעים בריבוע משמאל למעלה ללוח
    private void getScoreToShow(int x, int y) {
        try{
            if(game.checkLegalMove(x, y)){
                makeGameBoardTempGrid(x,y, orientation);
                score1 = game.CalculateScore(x,y,gameBoardTempGrid);
                score2 = game.CalculateScore(game.getSecondX(orientation, x, y), game.getSecondY(orientation, x, y) , gameBoardTempGrid);
            }
        }catch(Exception e){
        }
    }
//מדגיש את מהלך הבוט
    private void EmphasizeBotMove(Graphics g) {
        for(int x = 1; x<ROWS; x++){
            for(int y = 0; y<COLS;y++){
                if(!(hexagon[x][y] == null)){
                    if(computerGrid[x][y] != 0){
                        g.setColor(pickColor(computerGrid[x][y]));
                        g.fillPolygon(hexagon[x][y]);
                        g.setColor(Color.RED);
                        ((Graphics2D) g).setStroke(new BasicStroke(5));
                        g.drawPolygon(hexagon[x][y]);
                    }
                }
            }
        }
    }
//מסמן את חלק המשחק אצל שחקן אנושי לפני שהוא מונח
    private void EmphasizePiece(Graphics g, boolean onSpace) {
        g.setColor(Color.CYAN);
        if(onSpace && game.getCurrentPlayer().getCurrentPiece() != null && game.getCurrentPlayer().getClass() == HumanPlayer.class){
            try{
                if(orientation == 0){
                    if(hexColor[stoX-1][stoY-1] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX][stoY]);
                        g.fillPolygon(hexagon[stoX-1][stoY-1]);
                        g.drawPolygon(hexagon[stoX-1][stoY-1]);

                    }
                }else if(orientation == 1){
                    if(hexColor[stoX+1][stoY-1] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX+1][stoY-1]);
                        g.fillPolygon(hexagon[stoX+1][stoY-1]);
                        g.drawPolygon(hexagon[stoX+1][stoY-1]);
                    }
                }else if(orientation == 2){
                    if(hexColor[stoX+2][stoY] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX][stoY]);
                        g.fillPolygon(hexagon[stoX+2][stoY]);
                        g.drawPolygon(hexagon[stoX+2][stoY]);
                    }
                }else if(orientation == 3){
                    if(hexColor[stoX+1][stoY+1] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX][stoY]);
                        g.fillPolygon(hexagon[stoX+1][stoY+1]);
                        g.drawPolygon(hexagon[stoX+1][stoY+1]);

                    }
                }else if(orientation == 4){
                    if(hexColor[stoX-1][stoY+1] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX][stoY]);
                        g.fillPolygon(hexagon[stoX-1][stoY+1]);
                        g.drawPolygon(hexagon[stoX-1][stoY+1]);
                    }
                }else if(orientation == 5){
                    if(hexColor[stoX-2][stoY] == -1 && hexColor[stoX][stoY] == -1){
                        g.fillPolygon(hexagon[stoX][stoY]);
                        g.drawPolygon(hexagon[stoX][stoY]);
                        g.fillPolygon(hexagon[stoX-2][stoY]);
                        g.drawPolygon(hexagon[stoX-2][stoY]);

                    }
                }
            }catch(Exception e){}
        }
    }

    //עדכון הלוח של שחקן המחשב
    public void computerGrid(int[][] newGrid){
        computerGrid = newGrid;
    }
    //עדכון לוח המשחק
    public void updateGrid(int[][] newGrid){
        for(int x = 0; x<ROWS; x++){
            for(int y = 0; y<COLS;y++){
                hexColor[x][y] = newGrid [x][y];
            }
        }
    }
    //מגדיר את הצבעים הראשוניים על לוח המשחק
    private void setGrid(int x, int y, int c){ //sets initial colors
        hexColor[x][y] = c;
    }
    //מאתחל את לוח המשחק
    private void makeBoard (){ // could be made more efficient
        for(int x = 1; x<ROWS; x++){
            for(int y = 0; y<COLS;y++){
                if(!(hexagon[x][y] == null)){
                    if(x == 1 || x == 29 || x==28 || y == 0 ||  y == 14 || hexagon[x-2][y] == null || hexagon[x+2][y] == null)
                        hexColor[x][y] = 0;
                    else if(y==1 || y == 13 || x == 3 || x == 27 || x== 26 || hexagon[x-4][y] == null || hexagon[x+4][y] == null)
                        hexColor[x][y] = 0;
                    else
                        hexColor[x][y] = -1;
                }
            }
        }
        setGrid(10,2,4);
        setGrid(20,2,5);
        setGrid(25,7,6);
        setGrid(20,12,1);
        setGrid(10,12,2);
        setGrid(5,7,3);
    }
    //יוצר משושה
    private Polygon makeHex(int x, int y){ //z is currently radius of hex
        Polygon hex = new Polygon();
        double value;
        for(int a = 0; a<=MAX_SCORE; a++){
            value = Math.PI / 3.0 * a;
            hex.addPoint((int)(Math.round(x + Math.sin(value) * 30)), (int)(Math.round(y + Math.cos(value) * 30)));
        }
        return hex;
    }
    // מעדכן ברציפות את לוח המשחק
    public void run() {//update mouse x and y location, or something.
        while(game.isMoveRemaining() || game.isWinner()){
            repaint();
        }
    }
    //מטפל בלחיצות עכבר
    public void mouseClicked(MouseEvent e) {//Currently for debugging
        X = e.getX();
        Y = e.getY();
        for(int x = 0; x < ROWS; x++){
            for(int y = 0; y < COLS;y++){
                try{
                    GetAndSetPieceOnBoard(e, x, y);

                }catch(Exception exception){
                }
            }
        }

        SquaresOnTheSidesHandling();
    }
//שם את החלק במקום הנכון על הלוח או שם את החלק ביד השחקן בהתאם לפעולות השחקן
    private void GetAndSetPieceOnBoard(MouseEvent e, int x, int y) {
        if(hexagon[x][y]!= null && hexagon[x][y].contains(e.getX(), e.getY())){

            if(game.getCurrentPlayer().getCurrentPiece() != null){
                game.setPiece(x, y);
            }
        }
        if(x < 6 && y < 2){
            if(handPieces[x][y]!= null && handPieces[x][y].contains(e.getX(), e.getY())){
                if(game.getCurrentPlayer().getCurrentPiece() == null){
                    game.select(x);
                }
            }
        }
    }
//מייצג את הפעולות של הריבועי מצידי הלוח
    private void SquaresOnTheSidesHandling() {
        if(game.getCurrentPlayer().getCurrentPiece() != null){
            if(rotateClockwise.contains(X,Y)){
                rotate(1);
                game.getCurrentPlayer().resetDefault();
            }else if(rotateCounterClockwise.contains(X,Y)){
                rotate(-1);
                game.getCurrentPlayer().resetDefault();
            }else if(returnPiece.contains(X,Y)){
                game.deselect();
                game.getCurrentPlayer().setOrientation(0);
                game.getCurrentPlayer().resetDefault();
            }
        }
    }

    //מטפל בתנועת עכבר
    public void mouseMoved(MouseEvent e) {
        X = e.getX();
        Y = e.getY();
        repaint();
    }
    @Override
    //מטפל בכניסת עכבר
    public void mouseEntered(MouseEvent arg0) {}
    @Override
    // מטפל ביציאת העכבר
    public void mouseExited(MouseEvent arg0) {}
    @Override
    //מטפל בלחיצת עכבר
    public void mousePressed(MouseEvent arg0) {}
    @Override
    //מטפל בשחרור עכבר
    public void mouseReleased(MouseEvent arg0) {}
    @Override
    //מטפל בגרירת עכבר
    public void mouseDragged(MouseEvent arg0) {}
}

