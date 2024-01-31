import org.junit.platform.engine.support.descriptor.FileSystemSource;

import java.awt.*;
import java.util.*;


public class GameLogic implements  PlayableLogic{
    private  ConcretePlayer firstPlayer;
    private  ConcretePlayer secondPlayer;
    private  ConcretePlayer winner;
    private  ConcretePlayer loser;

    private boolean isFirstPlayerTurn;
    private ConcretePiece[][] board;
    private int boardSize=11;
    private  boolean isGameFinished=false;
    private Position kingPos;
    private ArrayList<Pawn> pawns = new ArrayList<Pawn>();
    private ArrayList<ConcretePiece> peices = new ArrayList<ConcretePiece>();
    private int[][] amountPieceAtPosition = new int[11][11];
    KillsCompare killsCompare = new KillsCompare();
    //distance comparator
    DistanceCompare distanceCompare =new DistanceCompare();
    //compare the counts of time diffrenet peices were on the pos
    Comparator<Position> pieceCountComp= new Comparator<Position>() {
        @Override
        public int compare(Position o1, Position o2) {
            if(amountPieceAtPosition[o1.getX()][o1.getY()] < amountPieceAtPosition[o2.getX()][o2.getY()]) return 1;
            if(amountPieceAtPosition[o1.getX()][o1.getY()] > amountPieceAtPosition[o2.getX()][o2.getY()]) return -1;

            return 0;
        }

    };
    //compare between 2 players by the winner
    Comparator<ConcretePiece> winnComp=(o1,o2)->{
        if (o1.getOwner()==winner && o2.getOwner()!=winner) {
            return -1;
        } else if (o1.getOwner()!=winner && o2.getOwner()==winner) {
            return 1;
        }
        return 0;

    };
    public GameLogic(){
        initGame(true);
    }
    private void initGame(boolean bol){
        if (bol){
            this.firstPlayer = new ConcretePlayer(false);
            this.secondPlayer = new ConcretePlayer(true);
        }
        this.isGameFinished=false;
        this.isFirstPlayerTurn=true;
        this.board = new ConcretePiece[11][11];
        initBoard();
    }
    private void initBoard(){
        //initial first player pawns
        int [][] pawnPoisitionFirstPlayer={
                {3,0},{4,0},{5,0},{6,0},{7,0},{5,1},{0,3},{10,3},{0,4},{10,4},{0,5},{1,5},
                {9,5},{10,5},{0,6},{10,6},{0,7},{10,7},{5,9},{3,10},{4,10},{5,10},{6,10},{7,10}
        };
        int attackId =1;

        for( int[] pos:pawnPoisitionFirstPlayer){
            int row= pos[0];
            int col = pos[1];
            //place a pawn in all position needed
            Pawn pawn = new Pawn(firstPlayer,new Position(row,col),"A"+attackId);
            board[row][col]=pawn;
            pawns.add(pawn);
            peices.add(pawn);
            amountPieceAtPosition[row][col]++;
            attackId++;
        }
        int defenseId=1;
        int [][] pawnPoisitionSecondPlayer={
                {5,3},{4,4},{5,4},{6,4},{3,5},{4,5},{5,5},{6,5},{7,5},{4,6},{5,6},{6,6},{5,7}
        };

        for( int[] pos:pawnPoisitionSecondPlayer) {
            int row = pos[0];
            int col = pos[1];
            if (defenseId==7){
                //create king
                King king = new King(secondPlayer, new Position(5,5),"K7");
                this.kingPos = new Position(5,5);
                board[5][5]=king;
                amountPieceAtPosition[row][col]++;
                peices.add(king);

                defenseId++;
            }else{
                //place a pawn in all position needed
                Pawn pawn = new Pawn(secondPlayer, new Position(row, col),"D"+defenseId);
                board[row][col] = pawn;
                pawns.add(pawn);
                peices.add(pawn);
                amountPieceAtPosition[row][col]++;

                defenseId++;
            }


        }

    };
    @Override
    public boolean move(Position a, Position b) {
        ConcretePiece piece = getPieceAtPosition(a);
        //Checking that the move is legally and the path is clear;
        if (piece == null || !isValidMove(piece, a, b) || !isPathClear(a, b)) {
            return false;
        }
        //If the king arrive to the corner Game over;
        if (piece.getType()=="♔" &&isCorner(b)){
            this.isGameFinished=true;
                this.secondPlayer.incWin();
                this.winner=secondPlayer;
                this.loser= firstPlayer;
                this.printAll();
                this.reset();
               return true;
        }
        //Update board
        board[a.getX()][a.getY()]= null;
        board[b.getX()][b.getY()]=piece;
        if(isPieceFirstTime(piece,b)){
            amountPieceAtPosition[b.getX()][b.getY()]++;
        };
        piece.addPos(b);

        //If the king is surrounded - Game over
        if(isKingSurrounded(kingPos.getX(),kingPos.getY())){
            this.isGameFinished=true;
            this.secondPlayer.incWin();
            this.winner=firstPlayer;
            this.loser=secondPlayer;
            printAll();
            this.reset();
            return true;
        }
        if(!(piece.getType()=="♔")) {
            checkAround(b, piece);
        }else{
            this.kingPos = new Position(b.getX(),b.getY());
        }

        this.isFirstPlayerTurn = !this.isFirstPlayerTurn;

        return true;

    }
    public void printAll(){
        this.printHistorySteps();
        this.printStarts();
        this.printKills();
        this.printStarts();
        this.printDistance();
        this.printStarts();
        this.printPieceCount();
        this.printStarts();

    }
    public void printHistorySteps(){
        StepsAmountComp stepComp = new StepsAmountComp();
        Comparator<ConcretePiece> historyComp = winnComp.thenComparing(stepComp);
        peices.sort(historyComp);
        for(ConcretePiece piece: this.peices){
            if(piece.getPositions().size()>1) {
                String result = "" + piece.getId() + ": [";
                for (int i = 0; i < piece.getPositions().size(); i++) {
                    Position pos = piece.getPositions().get(i);
                    result += pos.toString();
                    if (i < piece.getPositions().size() - 1) {
                        result += ", ";
                    }
                }
                result += "]";
                System.out.println(result);
            }
        }
    }

    public void printDistance(){
        IdComp idComp = new IdComp();
        DistanceCompare distnaceComp =new DistanceCompare();
        Comparator<ConcretePiece> disComp =distnaceComp.thenComparing(idComp).thenComparing(winnComp);
        peices.sort(disComp);
        for(ConcretePiece piece: this.peices){
            if(piece.getDistance()!=0) {
                String result =""+ piece.getId()+": " + piece.getDistance() +" squares";
                System.out.println(result);
            }
        }
    }

    public void printKills(){
        IdComp idComp = new IdComp();
        Comparator<Pawn> nmk =new KillsCopmp().thenComparing(idComp).thenComparing(winnComp);
        pawns.sort(nmk);
        for (Pawn pawn : pawns) {
            if(pawn.getKills()>0){System.out.println( pawn.getId() + ": " + pawn.getKills() + " kills");
            }

        }
    }
    public void printPieceCount(){
        Xcomp xComp = new Xcomp();
        Ycomp yComp = new Ycomp();
        Comparator<Position> posComp = pieceCountComp.thenComparing(xComp).thenComparing(yComp);
        ArrayList<Position> positionArray= new ArrayList<Position>();
        for (int i=0;i<boardSize;i++){
            for(int j=0; j<boardSize;j++){
                positionArray.add(new Position(i,j));
            }
        }
       Collections.sort(positionArray,posComp);
        for (Position pos:positionArray){
            if(amountPieceAtPosition[pos.getX()][pos.getY()]>1){
                System.out.println(pos.toString() +""+amountPieceAtPosition[pos.getX()][pos.getY()]+" pieces");
            }
        }



    }
    public boolean isKingSurrounded(int x, int y){
        //check the four adjacent position around the king
       boolean up =(y==0)||((board[x][y-1]!= null)&&(haveDifferentOwners(board[x][y],board[x][y-1])));
       boolean right=(x==boardSize-1)||((board[x+1][y]!= null)&&(haveDifferentOwners(board[x][y],board[x+1][y])));;
       boolean left =(x==0)||((board[x-1][y]!= null)&&(haveDifferentOwners(board[x][y],board[x-1][y])));
       boolean down =(y==boardSize-1)||((board[x][y+1]!= null)&&(haveDifferentOwners(board[x][y],board[x][y+1])));

        return up&&right&&down&&left;
    }
    //check if this the first time this piece was in this position
    public boolean isPieceFirstTime(ConcretePiece piece, Position pos){
        for(Position position: piece.getPositions()){
            if(position.getX()==pos.getX()&&position.getY()==pos.getY())return false;
        }
        return true;
    }
    //check that the move played by the right player turn made legally
    private boolean isValidMove(ConcretePiece piece, Position a, Position b){
        boolean isPlayerOneTurn= piece.getOwner().isPlayerOne();
        if(isPlayerOneTurn == this.isFirstPlayerTurn) return false;
        if(a.getX()==b.getX() && a.getY() == b.getY())return false;
        if(a.getX() != b.getX() && a.getY() != b.getY()) return false; // not horizontal or vertical
        if(isCorner(b)&& !(piece.getType()=="♔")) return false;
        return true;
    }
    private boolean isCorner(Position position){
        int x = position.getX();
        int y = position.getY();
        return(x==0 || x==boardSize-1)&&(y==0|| y==boardSize-1);
    }
    //checking that the path from position a to b is clear;
    private boolean isPathClear(Position a, Position b) {
        if (a.getX() == b.getX()) {
            int minY = Math.min(a.getY(), b.getY());
            int maxY = Math.max(a.getY(), b.getY());

            for (int y = minY + 1; y < maxY; y++) {
                Position pos = new Position(a.getX(), y);
                ConcretePiece pieceType = getPieceAtPosition(pos);
                if (pieceType != null) return false;
            }
        }
        return true;
    }

    private void checkAround(Position b, ConcretePiece piece){
        //get the x and y coordinates of the recentely moved piece and check adjacent positions in four directions:
        int x = b.getX();
        int y =b.getY();
        if(PosibalToEat(x+1,y,1,0,piece)) {
            eatPiece(x + 1, y);
        }
        if(PosibalToEat(x-1,y,-1,0,piece)) {
            eatPiece(x - 1, y);

        }
        if(PosibalToEat(x,y+1,0,1,piece)) {
            eatPiece(x , y+1);

        }
        if(PosibalToEat(x,y-1,0,-1,piece)) {
            eatPiece(x , y-1);

        }



    }
     public boolean PosibalToEat(int x , int y, int derX,int derY,ConcretePiece piece){
         Position targetPosition= new Position(x,y);
         if (!isDiffrentPlayer(x,y,piece)) {
             return false;
         }
         if (((getPieceAtPosition(targetPosition).getType()=="♔"))){return false;}

         Position nextPosition = new Position(x+derX,y+derY);
         if(!inBoard(nextPosition)) {
             if(piece.getType()=="♟"){
                 Pawn pawn = this.getPawnById(piece.getId());
                 pawn.addKill();

             }
             return true;
         }
         if (!haveDifferentOwners(getPieceAtPosition(nextPosition),piece)&&board[nextPosition.getX()][nextPosition.getY()]!=null) {
             if(!(getPieceAtPosition(nextPosition).getType()=="♔")){
                 if(piece.getType()=="♟"){
                     Pawn pawn = this.getPawnById(piece.getId());
                     pawn.addKill();                 }
                 return true;}
         }
         return false;
        };
    public Pawn getPawnById(String id){
        for (int i =0; i<pawns.size();i++){
            if(pawns.get(i).getId()==id) return pawns.get(i);
        }
        return null;
    }
     public boolean inBoard(Position position ){
            if(position.getX()<0||position.getX()>boardSize-1||position.getY()<0||position.getY()>boardSize-1){
                return false;}

            return true;
        }

     private boolean isDiffrentPlayer(int x, int y, ConcretePiece piece){
            Position position = new Position(x,y);
            if(!inBoard(position)){return false;}
            if(board[x][y]!=null){
                if(haveDifferentOwners(piece,getPieceAtPosition(position))){
                    return true;
                }
            }
            return false;
    }

      public boolean haveDifferentOwners(ConcretePiece piece1, ConcretePiece piece2) {
            if (piece1 == null || piece2 == null) {
                return false; // Assuming null means no piece is present
            }
            return !piece1.getOwner().equals(piece2.getOwner());
        }

      public void eatPiece(int x ,int y){
            board[x][y]=null;
        }
      public void printStarts(){
         System.out.println("***************************************************************************");
        }


    @Override
    public ConcretePiece getPieceAtPosition(Position position) {
        ConcretePiece conPiece = board[position.getX()][position.getY()];
        return conPiece;
    }

    @Override
    public Player getFirstPlayer() {
        return this.firstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        return this.secondPlayer;
    }

    @Override
    public boolean isGameFinished() {
        return this.isGameFinished;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return !this.isFirstPlayerTurn;
    }

    @Override
    public void reset() {

            initGame(false);
    }

    @Override
    public void undoLastMove() {

    }

    @Override
    public int getBoardSize() {
        return this.boardSize;
    }
}
