import java.util.Comparator;

public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x =x;
        this.y=y;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public String toString(){
        return "("+this.x+", "+this.y+")";
    }

}
class Xcomp implements Comparator<Position>{
    @Override
    public int compare(Position pos1 ,Position pos2){
        return Integer.compare(pos1.getX(), pos2.getX());
    };

}
class Ycomp implements Comparator<Position>{
    @Override
    public int compare(Position pos1 ,Position pos2){
        return Integer.compare(pos1.getY(), pos2.getY());
    };

}
