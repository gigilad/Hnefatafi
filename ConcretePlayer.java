import java.util.ArrayList;

public class ConcretePlayer implements Player{
    private boolean isPlayerone;
    private int wins=0;

    public ConcretePlayer(boolean bool){
        this.isPlayerone=bool;
    }
    @Override
    public boolean isPlayerOne() {
        return isPlayerone;
    }
    @Override
    public int getWins() {
        return this.wins;
    }
    //increase win by 1;
    public boolean incWin(){
        this.wins =+1;
        return true;
    }

}
