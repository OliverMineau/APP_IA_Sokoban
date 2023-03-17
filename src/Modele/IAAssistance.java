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
import Structures.Sequence;

import java.util.*;

class IAAssistance extends IA {
	Random r;

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

		Niveau n = super.niveau;
		ArrayList<Graphe> racinePerso = new ArrayList<>();
		creationGraphePerso(racinePerso, n);

		ArrayList<Graphe> racineCaisse = new ArrayList<>();
		creationGrapheCaisse(racineCaisse, n);

		showGraph(racinePerso);
		System.out.println();
		showGraph(racineCaisse);


		return resultat;

	}

	public void creationGraphePerso(ArrayList<Graphe> racine, Niveau n){

		for (int y = 0; y<n.lignes(); y++){
			for (int x = 0; x<n.colonnes(); x++){
				if(!n.aMur(y,x)){

					ArrayList<Coords> adj = new ArrayList<>();
					if(x-1 >= 0){
						adj.add(new Coords(x-1,y));
					}
					if(x+1 < n.colonnes()){
						adj.add(new Coords(x+1,y));
					}
					if(y-1 < n.lignes()){
						adj.add(new Coords(x,y-1));
					}
					if(y-1 >= 0){
						adj.add(new Coords(x,y+1));
					}

					racine.add(new Graphe(new Coords(x,y), adj));
				}
			}
		}
	}

	public void creationGrapheCaisse(ArrayList<Graphe> racine, Niveau n){

		for (int y = 0; y<n.lignes(); y++){
			for (int x = 0; x<n.colonnes(); x++){
				if(!n.aMur(y,x)){

					ArrayList<Coords> adj = new ArrayList<>();
					int c1,c2;
					c1=c2=0;

					// G
					if(x-1 >= 0 && !n.aMur(y,x-1)){
						adj.add(new Coords(x-1,y));
					}else{
						c1+=1;
					}

					// D
					if(x+1 < n.colonnes() && !n.aMur(y,x+1)){
						adj.add(new Coords(x+1,y));
					}else{
						c2+=1;
					}

					// B
					if(y+1 < n.lignes() && !n.aMur(y+1,x)){
						adj.add(new Coords(x,y+1));

					}else{
						c2+=1;
						c1+=1;
					}

					// H
					if(y-1 >= 0 && !n.aMur(y-1,x)){
						adj.add(new Coords(x,y-1));
					}else{
						c1+=1;
						c2+=1;
					}

					if((c1 < 2 && c2 < 2) || n.aBut(y,x)){
						racine.add(new Graphe(new Coords(x,y), adj));
					}
				}
			}
		}
	}


	public void showGraph(ArrayList<Graphe> racine){
		for(int i =0; i<racine.size(); i++){
			System.out.format("Coords : x%d,y%d\n", racine.get(i).coords.x,racine.get(i).coords.y );
		}
		System.out.println("Taille graphe perso : " + racine.size());

	}
}
