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

import javax.swing.*;
import java.util.*;

class IAAssistance extends IA {
	Random r;
	final static int MARRON = 0xBB7755;
	final static int ROUGE = 0xFF0000;
	final static int VERT = 0x00FF00;

	boolean retour=true;

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


		//Debut IA
		//Niveau n = super.niveau;
		Niveau n = jeu.niveau();

		Sequence<Coup> resultat = Configuration.nouvelleSequence();

		if(retour){
			//Creation graphe de toutes les positions possibles du perso
			Graphe grapheTotal = new Graphe();
			Coords perso = new Coords(n.pousseurC,n.pousseurL);
			creationGraphe(grapheTotal, n, perso,0);
			showGraph(grapheTotal);

			//Creation graphe des positions possibles du perso sans pousser de caisse
			/*Graphe graphePerso = new Graphe();
			perso = new Coords(n.pousseurC,n.pousseurL);
			creationGraphe(graphePerso, n, perso,1);
			showGraph(graphePerso);*/

			Noeud pousseur = null;
			Noeud caisse = null;
			for (int i =0; i < grapheTotal.noeuds.size(); i++){
				Noeud noeud = grapheTotal.noeuds.get(i);
				if(noeud.x == niveau.colonnePousseur() && noeud.y == niveau.lignePousseur()){
					pousseur = noeud;
				} else if (noeud.x == grapheTotal.caisse.x && noeud.y == grapheTotal.caisse.y) {
					caisse = noeud.voisins.get(1);
				}
			}

			ArrayList<Noeud> etapes = dijkstra(pousseur, caisse, grapheTotal, n);


			int pousseurL = niveau.lignePousseur();
			int pousseurC = niveau.colonnePousseur();

			// Ici, a titre d'exemple, on peut construire une séquence de coups
			// qui sera jouée par l'AnimationJeuAutomatique
			Configuration.info("Entrée dans la méthode de jeu de l'IA");

			for (int i = 0; i < etapes.size()-1; i++) {
				// Mouvement du pousseur
				Coup coup = new Coup();

				/*int y =  etapes.get(i+1).y;
				int x = etapes.get(i+1).x;
				int dirx = Integer.compare(x, etapes.get(i).x);
				int diry = Integer.compare(y, etapes.get(i).y);

				if(n.aCaisse(y,x)){
					System.out.println(dirx + " " + diry);
					coup.deplacementCaisse(y, x, y + diry, x + dirx );
					resultat.insereQueue(coup);
				}*/

				coup.deplacementPousseur(pousseurL, pousseurC, etapes.get(i).y, etapes.get(i).x);
				pousseurL = etapes.get(i).y;
				pousseurC = etapes.get(i).x;
				resultat.insereQueue(coup);

			}
			Configuration.info("Sortie de la méthode de jeu de l'IA");

		}else{
			System.out.println("Fin");
		}


		retour=!retour;

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

				if(n.aCaisse(y,x)){
					g.caisse = new Coords(x,y);
				}

				if(n.aPousseur(y,x)){
					g.perso = new Coords(x,y);
				}

				/*if(type == 1)
					n.fixerMarque(VERT,y,x);
				else
					n.fixerMarque(MARRON,y,x);*/

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



	public ArrayList<Noeud> dijkstra(Noeud depart, Noeud arrivee, Graphe graphe, Niveau n){

		//Initialisation des distances
		double distance[] = new double[graphe.noeuds.size()];
		Arrays.fill(distance, Double.POSITIVE_INFINITY);
		distance[graphe.noeuds.indexOf(depart)] = 0;

		ArrayList<Noeud> P = new ArrayList<>();

		int pred[] = new int[graphe.noeuds.size()];
		Arrays.fill(pred, -1);

		//Tant qu'il existe un sommet hors de P
		while (P.size() < graphe.noeuds.size()){

			//Trouver le minimum
			double min = Double.POSITIVE_INFINITY;
			int sommet = -1;
			for (int i = 0; i < distance.length; i++){
				if(!P.contains(graphe.noeuds.get(i)) && min > distance[i]){
					min = distance[i];
					sommet=i;
				}
			}

			//Ajout du sommet
			P.add(graphe.noeuds.get(sommet));

			Noeud a = graphe.noeuds.get(sommet);
			for(int j = 0; j < a.voisins.size(); j++){
				if(!P.contains(a.voisins.get(j))){
					if(distance[graphe.noeuds.indexOf(a.voisins.get(j))] > distance[sommet]+1){
						distance[graphe.noeuds.indexOf(a.voisins.get(j))]=distance[sommet]+1;
						pred[graphe.noeuds.indexOf(a.voisins.get(j))]=sommet;
					}
				}
			}
		}

		ArrayList<Noeud> etapes = new ArrayList<>();
		int i = graphe.noeuds.indexOf(arrivee);
		int dep = graphe.noeuds.indexOf(depart);

		while (i!=dep){

			Noeud elm = graphe.noeuds.get(i);
			int x = elm.x;
			int y = elm.y;
			n.fixerMarque(ROUGE,y,x);
			System.out.println("Etapes : " + x + " " + y);
			etapes.add(0,elm);
			i = pred[i];
		}
		int x = graphe.noeuds.get(i).x;
		int y = graphe.noeuds.get(i).y;
		n.fixerMarque(ROUGE,y,x);

		return etapes;
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
