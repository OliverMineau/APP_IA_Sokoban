package Structures;

import java.util.ArrayList;

public class Noeud {
    public int x;
    public int y;
    public Noeud parent;
    public double heuristique;
    public int cout;

    public ArrayList<Noeud> voisins;

    public Noeud(int x, int y){
        this.x=x;
        this.y=y;
        voisins= new ArrayList<>();
        this.parent = null;
        this.heuristique = 0;
        this.cout = 0;
    }

}
