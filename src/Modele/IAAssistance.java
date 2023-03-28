package Modele;
/*
 * Sokoban - Encore une nouvelle version (à but pédagogique) du célèbre jeu
 * Copyright (C) 2018 Guillaume Huard
 *
 * Ce programme est libre, vous pouvez le redistribuer et/ou le
 * modifier selon les termes de la Licence Publique Générale GNU publiée par la
 * Free Software Foundation (version 2 ou bien toute autre version ultérieure
 * choisie par vous).
 *
 * Ce programme est distribué car potentiellement utile, mais SANS
 * AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
 * commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
 * Licence Publique Générale GNU pour plus de détails.
 *
 * Vous devez avoir reçu une copie de la Licence Publique Générale
 * GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
 * États-Unis.
 *
 * Contact:
 *          Guillaume.Huard@imag.fr
 *          Laboratoire LIG
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */

import Global.Configuration;
import Structures.Coords;
import Structures.Graphe;
import Structures.Noeud;
import Structures.Sequence;

import java.util.*;

class IAAssistance extends IA {
	Random r;
	final static int MARRON = 0xBB7755;
	final static int ROUGE = 0xFF0000;
	final static int VERT = 0x00FF00;


	public IAAssistance() {
		r = new Random();
	}

	//Fonction de départ de l'IA de l'APP ALGO6
	/*
	* Enlever les cases des coins et les couloirs impossibles
	* Mettre dans une structure de graphe
	* Faire le plus court chemin entre le joueur et une caisse
	* Faire le plus court chemin entre la caisse et un but
	* */
	@Override
	public Sequence<Coup> joue() {
		Sequence<Coup> resultat = Configuration.nouvelleSequence();
		int pousseurL = niveau.lignePousseur();
		int pousseurC = niveau.colonnePousseur();

		// Ici, a titre d'exemple, on peut construire une séquence de coups
		// qui sera jouée par l'AnimationJeuAutomatique
		int nb = r.nextInt(5)+1;
		Configuration.info("Entrée dans la méthode de jeu de l'IA");
		Configuration.info("Construction d'une séquence de " + nb + " coups");
		for (int i = 0; i < nb; i++) {
			// Mouvement du pousseur
			Coup coup = new Coup();
			boolean libre = false;
			while (!libre) {
				int nouveauL = r.nextInt(niveau.lignes());
				int nouveauC = r.nextInt(niveau.colonnes());
				if (niveau.estOccupable(nouveauL, nouveauC)) {
					Configuration.info("Téléportation en (" + nouveauL + ", " + nouveauC + ") !");
					coup.deplacementPousseur(pousseurL, pousseurC, nouveauL, nouveauC);
					//coup.ajouteMarque(pousseurL, pousseurC, 0xBB7755);

					resultat.insereQueue(coup);
					pousseurL = nouveauL;
					pousseurC = nouveauC;
					libre = true;
				}
			}
		}
		Configuration.info("Sortie de la méthode de jeu de l'IA");


		//Debut IA
		//Niveau n = super.niveau;
		Niveau n = jeu.niveau();

		//Creation graphe de toutes les positions possibles du perso
		Graphe grapheTotal = new Graphe();
		Coords perso = new Coords(n.pousseurC,n.pousseurL);
		creationGraphe(grapheTotal, n, perso,0);
		showGraph(grapheTotal);

		//Creation graphe des positions possibles du perso sans pousser de caisse
		Graphe graphePerso = new Graphe();
		perso = new Coords(n.pousseurC,n.pousseurL);
		creationGraphe(graphePerso, n, perso,1);
		showGraph(graphePerso);
		return resultat;

	}


	private int ajoutAvisiter(ArrayList<Noeud> aVisiter, int x,int y){
		for(int i = 0; i < aVisiter.size(); i++){
			if(aVisiter.get(i).x == x && aVisiter.get(i).y == y){
				return i;
			}
		}
		 return -1;
	}
	public Noeud creationGraphe(Graphe g, Niveau n, Coords debut, int type){
		Noeud caisse = null;
		ArrayList<Noeud> aVisiter =  g.noeuds;
		aVisiter.add(new Noeud(debut.x,debut.y));

		int ind = 0;
		while (ind < aVisiter.size()){

			int x = aVisiter.get(ind).x;
			int y = aVisiter.get(ind).y;

			//Si la case n'est pas un mur
			if(!n.aMur(y,x) && !(type == 1 && n.aCaisse(y,x))){

				if(type == 1)
					n.fixerMarque(VERT,y,x);
				else
					n.fixerMarque(MARRON,y,x);

				//Liste des adj (indexs)
				ArrayList<Noeud> adj = aVisiter.get(ind).voisins;
				if(x-1 >= 0 && !n.aMur(y,x-1)){
					int nInd = ajoutAvisiter(aVisiter,x-1,y);
					if(nInd == -1){
						Noeud tmp = new Noeud(x-1,y);
						aVisiter.add(tmp);
						adj.add(tmp);
					}else{
						adj.add(aVisiter.get(nInd));
					}
				}
				if(x+1 < n.colonnes() && !n.aMur(y,x+1)){
					int nInd = ajoutAvisiter(aVisiter,x+1,y);
					if(nInd == -1){
						Noeud tmp = new Noeud(x+1,y);
						aVisiter.add(tmp);
						adj.add(tmp);
					}else{
						adj.add(aVisiter.get(nInd));
					}
				}
				if(y+1 < n.lignes() && !n.aMur(y+1,x)){
					int nInd = ajoutAvisiter(aVisiter,x,y+1);
					if(nInd == -1){
						Noeud tmp = new Noeud(x,y+1);
						aVisiter.add(tmp);
						adj.add(tmp);
					}else{
						adj.add(aVisiter.get(nInd));
					}
				}
				if(y-1 >= 0 && !n.aMur(y-1,x)){
					int nInd = ajoutAvisiter(aVisiter,x,y-1);
					if(nInd == -1){
						Noeud tmp = new Noeud(x,y-1);
						aVisiter.add(tmp);
						adj.add(tmp);
					}else{
						adj.add(aVisiter.get(nInd));
					}
				}
			}

			if(n.aCaisse(y,x)){
				caisse = aVisiter.get(ind);
			}

			ind++;
		}

		return caisse;
	}


	public void showGraph(Graphe graphe){
		for(int i =0; i<graphe.noeuds.size(); i++){
			System.out.format("Coords : x%d,y%d\n", graphe.noeuds.get(i).x, graphe.noeuds.get(i).y);

			for(int j =0; j<graphe.noeuds.get(i).voisins.size(); j++){
				System.out.format("\tvoisin : x%d,y%d\n", graphe.noeuds.get(i).voisins.get(j).x, graphe.noeuds.get(i).voisins.get(j).y);
			}
		}
		System.out.println("Taille graphe : " + graphe.noeuds.size());

	}
}
