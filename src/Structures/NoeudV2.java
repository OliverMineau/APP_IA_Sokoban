package Structures;

import java.util.ArrayList;

public class NoeudV2 {

    public int x;
    public int y;

    public double f = 0;
    public double g = 0;

    public NoeudV2 parent = null;

    public NoeudV2 gauche = null;
    public NoeudV2 droit = null;
    public NoeudV2 haut = null;
    public NoeudV2 bas = null;

    public NoeudV2(int x, int y){
        this.x=x;
        this.y=y;
    }

    public ArrayList<NoeudV2> voisins(){
        ArrayList<NoeudV2> nds = new ArrayList<>();
        nds.add(gauche);
        nds.add(droit);
        nds.add(haut);
        nds.add(bas);
        return  nds;
    }

    public boolean estVoisin(NoeudV2 nd){
        if(nd == gauche){
            return true;
        } else if(nd == droit){
            return true;
        }else if(nd == haut){
            return true;
        }else if(nd == bas){
            return true;
        }else{
            return false;
        }
    }

    public boolean estCoin(){
        if((gauche == null && haut == null) ||
            (droit == null && haut == null) ||
                (gauche == null && bas == null) ||
                (droit == null && bas == null)){
            return true;
        }
        return false;
    }

    public double heuristique(NoeudV2 arr){

        return Math.pow(Math.abs(this.x - arr.x),2) + Math.pow(Math.abs(this.y - arr.y),2);
    }

}
