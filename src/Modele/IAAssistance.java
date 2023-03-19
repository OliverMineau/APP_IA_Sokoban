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
	final static int MARRON = 0xBB7755;
	final static int ROUGE = 0xFF0000;


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

		//Creation graphe des positions possibles du perso
		ArrayList<Graphe> racinePerso = new ArrayList<>();
		creationGraphe(racinePerso, n);
		showGraph(racinePerso);

		//Creation graphe des positions possibles des caisses
		ArrayList<Graphe> racineCaisse = new ArrayList<>();
		creationGrapheCaisse(racineCaisse, n);
		showGraph(racineCaisse);
		System.out.println("Caisses");



		return resultat;

	}

	public void recVerifCouloir(ArrayList<Graphe> racine, ArrayList<Graphe> supp, Graphe elm){

		if(elm.adj.size() >1){
			return;
		} else{

			for (int i=0; i < racine.size(); i++){

			}

		}
	}

	public void supprElm(ArrayList<Graphe> racine, Graphe elm){

		//Chaque Noeud
		for (int i =0; i< racine.size(); i++){

			Graphe rElm = racine.get(i);
			ArrayList<Graphe> rCoords = rElm.adj;

			// Chaque Noeud adjacent
			for (int j =0; j< rElm.adj.size(); j++){

				// Si elem est adjacent
				if(elm.coords.x == rCoords.get(j).coords.x && elm.coords.y == rCoords.get(j).coords.y){
					System.out.format("Suppr(%d %d) %d,%d\n",rElm.coords.x,rElm.coords.y, elm.coords.x,elm.coords.y);
					rCoords.remove(j);
					System.out.format("nsize %d\n",rCoords.size());
					break;
				}
			}
		}
		System.out.format("Suppr def %d,%d\n",elm.coords.x,elm.coords.y);
		racine.remove(elm);
	}

	public void creationGrapheCaisse(ArrayList<Graphe> racine, Niveau n){

		//Memes positions que le perso mais sans les coins et couloirs
		creationGraphe(racine, n);

		//Enlever les couloirs
		int tot1 = 0;
		while(tot1 < racine.size()){
			Graphe elm = racine.get(tot1);
			int x = elm.coords.x;
			int y = elm.coords.y;

			if (elm.adj.size() <= 1 && !n.aBut(y,x)){

				supprElm(racine,elm);

				racine.remove(elm);
				n.fixerMarque(MARRON,elm.coords.y,elm.coords.x);
				tot1 = 0;
			}
			tot1++;
		}

		//Enlever les coins
		int tot = 0;
		while(tot < racine.size()){
			Graphe elm = racine.get(tot);
			int x = elm.coords.x;
			int y = elm.coords.y;

			if(elm.coords.x == 6 && elm.coords.y == 9){
				System.out.format("Len :%d\n",elm.adj.size());
			}

			if(elm.adj.size()==2) {
				if(elm.adj.get(0).coords.x != elm.adj.get(1).coords.x && elm.adj.get(0).coords.y != elm.adj.get(1).coords.y && !n.aBut(y,x)){

					for(int i=0; i < racine.size(); i++){
						if(racine.get(i).equals(elm.adj.get(0)) || racine.get(i).equals(elm.adj.get(1))){
							racine.get(i).adj.remove(elm);
							System.out.println("Supp adj");
						}
					}

					racine.remove(elm);
					n.fixerMarque(ROUGE,elm.coords.y,elm.coords.x);
					tot=0;
				}
			}
			tot++;
		}

	}

	private void ajoutAvisiter(ArrayList<Graphe> aVisiter, ArrayList<Graphe> visite, Graphe gElm){
		for(int i = 0; i < aVisiter.size(); i++){
			if(aVisiter.get(i).coords.x == gElm.coords.x && aVisiter.get(i).coords.y == gElm.coords.y){
				return;
			}
		}
		for(int i = 0; i < visite.size(); i++){
			if(visite.get(i).coords.x == gElm.coords.x && visite.get(i).coords.y == gElm.coords.y){
				return;
			}
		}
		aVisiter.add(gElm);
	}
	public void creationGraphe(ArrayList<Graphe> racine, Niveau n){
		ArrayList<Graphe> aVisiter = new ArrayList<>();
		ArrayList<Graphe> visite = new ArrayList<>();

		aVisiter.add(new Graphe(new Coords(n.pousseurC,n.pousseurL)));

		while (!aVisiter.isEmpty()){

			int x = aVisiter.get(0).coords.x;
			int y = aVisiter.get(0).coords.y;

			if(!n.aMur(y,x)){

				ArrayList<Graphe> adj = new ArrayList<>();
				if(x-1 >= 0 && !n.aMur(y,x-1)){
					Graphe c = new Graphe(new Coords(x-1,y));
					adj.add(c);
					ajoutAvisiter(aVisiter,visite,c);
				}
				if(x+1 < n.colonnes() && !n.aMur(y,x+1)){
					Graphe c = new Graphe(new Coords(x+1,y));
					adj.add(c);
					ajoutAvisiter(aVisiter,visite,c);
				}
				if(y+1 < n.lignes() && !n.aMur(y+1,x)){
					Graphe c = new Graphe(new Coords(x,y+1));
					adj.add(c);
					ajoutAvisiter(aVisiter,visite,c);
				}
				if(y-1 >= 0 && !n.aMur(y-1,x)){
					Graphe c = new Graphe(new Coords(x,y-1));
					adj.add(c);
					ajoutAvisiter(aVisiter,visite,c);
				}

				Graphe nouvG = new Graphe(new Coords(x,y));
				nouvG.adj=adj;
				racine.add(nouvG);
			}

			visite.add(aVisiter.remove(0));
		}


	}


	public void showGraph(ArrayList<Graphe> racine){
		for(int i =0; i<racine.size(); i++){
			System.out.format("Coords : x%d,y%d\n", racine.get(i).coords.x,racine.get(i).coords.y );
		}
		System.out.println("Taille graphe : " + racine.size());

	}
}
