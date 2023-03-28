package Structures;

import java.util.ArrayList;

public class Noeud {
    public int x;
    public int y;

    public ArrayList<Noeud> voisins;

    public Noeud(int x, int y){
        this.x=x;
        this.y=y;
        voisins= new ArrayList<>();
    }

}
