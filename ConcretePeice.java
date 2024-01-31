import java.util.ArrayList;
import java.util.Comparator;

abstract class ConcretePiece implements Piece{
    private String type;
    private ConcretePlayer owner;
    private Position position;
    private String id;
    private int distance=0;

    private ArrayList<Position> positions=new ArrayList<Position>();

    //constructor
    public ConcretePiece(String type , ConcretePlayer owner ,Position position,String id){
        this.type=type;
        this.owner =owner;
        this.position=position;
        this.id=id;
        addPos(position);
    }

    //getters
    public ArrayList<Position> getPositions(){
        return this.positions;
    }
    public int getDistance(){return  this.distance;}
    //get the current position
    public Position getPosition(){
        return this.position;
    }
    public String getId(){return this.id;}
    @Override
    public Player getOwner() {
        return this.owner;
    }
    @Override
    public String getType() {
        return this.type;
    }


//methods
    public void addPos(Position position){
        if(positions.size()==0){
            this.positions.add(position);
        }else{
            Position lastPos = positions.getLast();
            checkDistance(lastPos,position);
            this.positions.add(position);
        }
    }
    public void checkDistance(Position lastPos, Position newPos){
        if(lastPos.getX() == newPos.getX()){
            this.distance += Math.abs(lastPos.getY()-newPos.getY());
        }else{
            this.distance += Math.abs(lastPos.getX()-newPos.getX());
        }

    }
    public int extractIdInteger(){
        String onlyNum = this.id.replaceAll("[^0-9]","");
        return Integer.parseInt(onlyNum);
    }
}

//Comparators
class DistanceCompare implements Comparator<ConcretePiece>{
    @Override
    public int compare(ConcretePiece peice1 ,ConcretePiece peice2){
        return Integer.compare(peice2.getDistance(),peice1.getDistance());
        };

}

class KillsCompare implements  Comparator<Pawn>{
    @Override
    public int compare(Pawn o1, Pawn o2) {
        return Integer.compare(o2.getKills(),o1.getKills());
    }

}
class IdComp implements Comparator<ConcretePiece>{
    @Override
    public int compare(ConcretePiece o1, ConcretePiece o2) {
        return Integer.compare(o1.extractIdInteger(), o2.extractIdInteger());
    }


}
class StepsAmountComp implements Comparator<ConcretePiece>{
    @Override
    public int compare(ConcretePiece o1, ConcretePiece o2) {
        return Integer.compare(o1.getPositions().size(),o2.getPositions().size());
    }

}
