package Structures;

import java.util.ArrayList;
import java.util.List;

public class Graphe {

    public Coords coords;
    public Boolean estCoin;
    public Boolean estCoteCoin;
    public ArrayList<Graphe> adj;

    public Graphe(Coords coords){
        this.coords=coords;
    }


}
