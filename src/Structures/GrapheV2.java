package Structures;

import java.util.ArrayList;
import java.util.Arrays;

public class GrapheV2 {

    public NoeudV2 caisse;
    public NoeudV2 perso;
    public NoeudV2 but;

    public NoeudV2[][] tableau;
    public ArrayList<NoeudV2> noeuds;

    public GrapheV2(int tailleX, int tailleY){
        this.noeuds= new ArrayList<>();
        this.tableau = new NoeudV2[tailleY][tailleX];
        for (NoeudV2[] col: tableau)
            Arrays.fill(col, null);
    }

    public void ajouteNoeud(NoeudV2 nd){

        if(tableau[nd.y][nd.x] == null){
            tableau[nd.y][nd.x] = nd;
        }
        if(!noeuds.contains(nd)){
            noeuds.add(nd);
        }
    }

}
