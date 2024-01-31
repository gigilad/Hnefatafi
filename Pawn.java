import java.util.ArrayList;
import java.util.Comparator;

public class Pawn extends ConcretePiece{
 private int kills=0;
    public Pawn(ConcretePlayer owner , Position position,String id){
        super("♟",owner,position,id);
    }
    public void addKill(){this.kills+=1;}
    public int getKills(){return this.kills;}

}
class KillsCopmp implements Comparator<Pawn> {
    @Override
    public int compare(Pawn o1, Pawn o2) {
        return Integer.compare(o2.getKills(),o1.getKills());
    }
}
