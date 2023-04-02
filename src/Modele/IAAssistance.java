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

	boolean retour = true;

	Noeud caisse;
	Noeud but;

	public IAAssistance() {
		r = new Random();
	}

	//Fonction de départ de l'IA de l'APP ALGO6


	@Override
	public Sequence<Coup> joue() {

		//Debut IA
		//Niveau n = super.niveau;
		Niveau n = jeu.niveau();

		Sequence<Coup> resultat = Configuration.nouvelleSequence();


		if (retour) {
			//Creation g de toutes les positions possibles du perso
			Graphe gTotal = new Graphe();
			Coords perso = new Coords(n.pousseurC, n.pousseurL);
			creationGraphe(gTotal, n, perso, 0);
			//showGraph(gTotal);

			Noeud pousseur = null;
			for (int i = 0; i < gTotal.noeuds.size(); i++) {
				Noeud noeud = gTotal.noeuds.get(i);
				if (noeud.x == n.colonnePousseur() && noeud.y == n.lignePousseur()) {
					pousseur = noeud;
				} else if (noeud.x == gTotal.caisse.x && noeud.y == gTotal.caisse.y) {
					caisse = noeud;
				} else if (n.aBut(noeud.y,noeud.x)) {
					but = noeud;
				}
			}

			if(caisse == null){
				System.out.println("PAS de Caisses");
				return null;
			}

			//Algo CAISSE -> BUT
			Noeud butDuPerso = caisseVersBut(caisse,but,gTotal, n);
			//System.out.println("Caisse : " + caisse.x + "," + caisse.y);
			//System.out.println("But : " + but.x + "," + but.y);

			if(butDuPerso == null){
				System.out.println("pas de deplacement direct possible");
			}else{
				System.out.println("Deplacement caisse -> but possible et\nperso doit se placer : " + butDuPerso.x + "," + butDuPerso.y);
			}


			//Algo POUSSEUR -> CAISSE
			ArrayList<Noeud> etapes = null;

			if(butDuPerso != null){
				System.out.println("Test chemin entre : " + pousseur.x + "," + pousseur.y + " et " + butDuPerso.x + "," +  butDuPerso.y);
				System.out.println("Caisse : " + caisse.x + "," + caisse.y + " et a caisse " + n.aCaisse(caisse.y,caisse.x));
				etapes = dijkstra(pousseur, butDuPerso, gTotal, n);
			}

			if(etapes == null){

				System.out.println("Perso ne peut pas se placer pour pousser la caisse");

				for(int i=0; i < caisse.voisins.size(); i++){

					System.out.println("Test chemin entre : " + pousseur.x + "," + pousseur.y + " et " + caisse.voisins.get(i).x + "," +  caisse.voisins.get(i).y);
					etapes = dijkstra(pousseur, caisse.voisins.get(i), gTotal, n);

					if(etapes != null && etapes.size()>0){
						System.out.println("Chemin existant pousseur caisse (pas le bon)");
						break;
					}else{
						//System.out.println("Pas de chemin entre : " + pousseur.x + "," + pousseur.y + " et " + caisse.voisins.get(i).x + "," +  caisse.voisins.get(i).y);
					}
				}
			}

			if(etapes == null || etapes.size() == 0){
				System.out.println("Aucun chemin existant");
				return null;
			}

			//dijkstra(Noeud depart, Noeud arrivee, Graphe g, Niveau n)
			int pousseurL = n.lignePousseur();
			int pousseurC = n.colonnePousseur();

			// Ici, a titre d'exemple, on peut construire une séquence de coups
			// qui sera jouée par l'AnimationJeuAutomatique
			Configuration.info("Entrée dans la méthode de jeu de l'IA");

			for (int i = 0; i < etapes.size() ; i++) {
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
				//coup.ajouteMarque(pousseurL, pousseurC,ROUGE);
				n.fixerMarque(VERT,etapes.get(i).y, etapes.get(i).x);

				resultat.insereQueue(coup);

				pousseurL = etapes.get(i).y;
				pousseurC = etapes.get(i).x;

			}
			Configuration.info("Sortie de la méthode de jeu de l'IA");

		}else{

			//Si caisse on pousse
			int x=0,y=0;
			if(n.aCaisse(n.pousseurL+1,n.pousseurC)){
				x=0;
				y=1;
			}
			if(n.aCaisse(n.pousseurL-1,n.pousseurC)){
				x=0;
				y=-1;
			}
			if(n.aCaisse(n.pousseurL,n.pousseurC+1)){
				x=1;
				y=0;
			}
			if(n.aCaisse(n.pousseurL,n.pousseurC-1)){
				x=-1;
				y=0;
			}

			if(x!=0 || y!=0){

				//Regarder si on peut aller derriere la caisse
                /*for (int i = 0; i < gPerso.noeuds.size(); i++) {
					Noeud noeud = gPerso.noeuds.get(i);

					for (int j = 0; < caisse.voisins.size(); j++){

					}
				}*/


				//Si on ne peut plus pousser
				if(n.pousseurL+2*y >= n.l || n.pousseurC+2*x >= n.c)
					return null;

				Coup coup = new Coup();
				coup.deplacementCaisse(n.pousseurL+y,n.pousseurC+x, n.pousseurL+2*y,n.pousseurC+2*x);
				//caisse.x = n.pousseurC+x;
				//caisse.y = n.pousseurL+y;
				resultat.insereQueue(coup);
			}

			System.out.println("Fin");
		}


		retour = !retour;

		return resultat;

	}


	private int ajoutAvisiter(ArrayList<Noeud> aVisiter, int x, int y) {
		for (int i = 0; i < aVisiter.size(); i++) {
			if (aVisiter.get(i).x == x && aVisiter.get(i).y == y) {
				return i;
			}
		}
		return -1;
	}

	public Noeud creationGraphe(Graphe g, Niveau n, Coords debut, int type) {
		Noeud caisse = null;
		ArrayList<Noeud> aVisiter = g.noeuds;
		aVisiter.add(new Noeud(debut.x, debut.y));

		int ind = 0;
		while (ind < aVisiter.size()) {

			int x = aVisiter.get(ind).x;
			int y = aVisiter.get(ind).y;

			//Si la case n'est pas un mur
			if (!n.aMur(y, x) && !(type == 1 && n.aCaisse(y, x))) {

				if (n.aCaisse(y, x)) {
					g.caisse = new Coords(x, y);
				}

				if (n.aPousseur(y, x)) {
					g.perso = new Coords(x, y);
				}

                /*if(type == 1)
					n.fixerMarque(VERT,y,x);
				else
					n.fixerMarque(MARRON,y,x);*/

				//Liste des adj (indexs)
				ArrayList<Noeud> adj = aVisiter.get(ind).voisins;
				if (x - 1 >= 0 && !n.aMur(y, x - 1)) {
					int nInd = ajoutAvisiter(aVisiter, x - 1, y);
					if (nInd == -1) {
						Noeud tmp = new Noeud(x - 1, y);
						aVisiter.add(tmp);
						adj.add(tmp);
					} else {
						adj.add(aVisiter.get(nInd));
					}
				}
				if (x + 1 < n.colonnes() && !n.aMur(y, x + 1)) {
					int nInd = ajoutAvisiter(aVisiter, x + 1, y);
					if (nInd == -1) {
						Noeud tmp = new Noeud(x + 1, y);
						aVisiter.add(tmp);
						adj.add(tmp);
					} else {
						adj.add(aVisiter.get(nInd));
					}
				}
				if (y + 1 < n.lignes() && !n.aMur(y + 1, x)) {
					int nInd = ajoutAvisiter(aVisiter, x, y + 1);
					if (nInd == -1) {
						Noeud tmp = new Noeud(x, y + 1);
						aVisiter.add(tmp);
						adj.add(tmp);
					} else {
						adj.add(aVisiter.get(nInd));
					}
				}
				if (y - 1 >= 0 && !n.aMur(y - 1, x)) {
					int nInd = ajoutAvisiter(aVisiter, x, y - 1);
					if (nInd == -1) {
						Noeud tmp = new Noeud(x, y - 1);
						aVisiter.add(tmp);
						adj.add(tmp);
					} else {
						adj.add(aVisiter.get(nInd));
					}
				}
			}

			if (n.aCaisse(y, x)) {
				caisse = aVisiter.get(ind);
			}

			ind++;
		}

		return caisse;
	}


	public ArrayList<Noeud> dijkstra(Noeud depart, Noeud arrivee, Graphe g, Niveau n) {

		//Initialisation des distances
		double distance[] = new double[g.noeuds.size()];
		Arrays.fill(distance, Double.POSITIVE_INFINITY);
		distance[g.noeuds.indexOf(depart)] = 0;

		ArrayList<Noeud> P = new ArrayList<>();

		int pred[] = new int[g.noeuds.size()];
		Arrays.fill(pred, -1);

		boolean premiereIteration = true;
		//Tant qu'il existe un sommet hors de P
		while (P.size() < g.noeuds.size()) {

			//Trouver le minimum
			double min = Double.POSITIVE_INFINITY;
			int sommet = -1;
			for (int i = 0; i < distance.length; i++) {
				if (!P.contains(g.noeuds.get(i)) && min >= distance[i] && distance[i] != -2) {
					min = distance[i];
					sommet = i;
				}
			}


			//Ajout du sommet
			P.add(g.noeuds.get(sommet));

			Noeud a = g.noeuds.get(sommet);
			for (int j = 0; j < a.voisins.size(); j++) {
				if (!P.contains(a.voisins.get(j)) && !n.aCaisse(a.voisins.get(j).y,a.voisins.get(j).x)) {
					if (distance[g.noeuds.indexOf(a.voisins.get(j))] > distance[sommet] + 1) {

						if(niveau.aCaisse(a.y,a.x) && !premiereIteration){
							distance[g.noeuds.indexOf(a.voisins.get(j))] = -2;
							pred[g.noeuds.indexOf(a.voisins.get(j))] = -2;
						}else{
							distance[g.noeuds.indexOf(a.voisins.get(j))] = distance[sommet] + 1;
							pred[g.noeuds.indexOf(a.voisins.get(j))] = sommet;
						}
					}
				}
			}
			premiereIteration=false;

		}

		ArrayList<Noeud> etapes = new ArrayList<>();
		int i = g.noeuds.indexOf(arrivee);
		int dep = g.noeuds.indexOf(depart);

		while (i != dep) {

			Noeud elm = g.noeuds.get(i);
			int x = elm.x;
			int y = elm.y;
			//n.fixerMarque(ROUGE, y, x);
			System.out.println("Etapes : " + x + " " + y);

			etapes.add(0,elm);

            /*if(n.aCaisse(elm.y,elm.x) ){
				etapes.clear();
				break;
			}*/

			i = pred[i];

			//Pas de chemin existant
			if (i == -1 || i == -2){
				return null;
			}
		}
		int x = g.noeuds.get(i).x;
		int y = g.noeuds.get(i).y;
		//n.fixerMarque(ROUGE, y, x);

		return etapes;
	}

	Noeud caisseVersBut(Noeud depart, Noeud arrivee, Graphe g, Niveau n){
		ArrayList<Noeud> etapes = dijkstra(depart, arrivee, g, n);
		//Si pas possible
		if(etapes == null || etapes.size() == 0){
			System.out.println("Pas d'etapes caisse but");
			return null;
		}

		int dirx = Integer.compare(depart.x, etapes.get(etapes.size()-1).x);
		int diry = Integer.compare(depart.y, etapes.get(etapes.size()-1).y);

		for (int i = 0; i < depart.voisins.size(); i++){
			Noeud voisin = depart.voisins.get(i);

			System.out.println("Voisin de caisse : " + voisin.x + "," + voisin.y);
			System.out.println("Compare caisse : " + (dirx+depart.x) + "," + (diry+depart.y));
			System.out.println("Compare dir : " + dirx+ "," + diry);
			System.out.println("Caisse : " + depart.x + "," + depart.y);

			if(voisin.x == (dirx+depart.x) && voisin.y == (diry+depart.y)){
				return voisin;
			}
		}

		System.out.println("BUT : Deplacement de la caisse impossible");
		return null;
	}

	public void showGraph(Graphe g) {
		for (int i = 0; i < g.noeuds.size(); i++) {
			System.out.format("Coords : x%d,y%d\n", g.noeuds.get(i).x, g.noeuds.get(i).y);

			for (int j = 0; j < g.noeuds.get(i).voisins.size(); j++) {
				System.out.format("\tvoisin : x%d,y%d\n", g.noeuds.get(i).voisins.get(j).x, g.noeuds.get(i).voisins.get(j).y);
			}
		}
		System.out.println("Taille g : " + g.noeuds.size());

	}

/*
// DEBUT A* ALGORITHM
	public int compareParHeuristique(Noeud n1, Noeud n2){
        if(n1.heuristique < n2.heuristique) {
            return -1;
        } else if (n1.heuristique == n2.heuristique) {
            return 0;
        } else {
            return 1;
        }
    }
	public void reconstituerChemin(Noeud u){
		ArrayList<Noeud> chemin = new ArrayList<>();
		Noeud courant = u;
		while(courant != null){
			chemin.add(courant);
			courant = courant.parent;
		}
		Collections.reverse(chemin);
	}

		public double distance(Noeud a, Noeud b){
			int x = Math.abs(a.x - b.x);
			int y = Math.abs(a.y - b.y);
			return Math.sqrt(x*x + y*y);
		}

	public ArrayList<Noeud> heuristique(Graphe g, Noeud depart, Noeud objectif) {
		// contient au fur et à mesure les noeuds issue de openList avec le coût total le plus faible
		Queue<Noeud> closedList = new LinkedList<>();
		// contient tous les noeuds qui ont besoin d'être étudiés
		PriorityQueue<Noeud> openList = new PriorityQueue<>(compareParHeuristique(depart, objectif));
		openList.add(depart);

		while (!openList.isEmpty()) {
			Noeud u = openList.poll(); // defile le 1er element
			if(u.x == objectif.x && u.y == objectif.y){
				reconstituerChemin(u);
				return;
			}

			for (int v = 0; v < u.voisins.size(); v++) {
				if(g.noeuds.contains(u.voisins.get(v))) {
					if (!closedList.contains(u.voisins.get(v)) && !openList.contains(u.voisins.get(v))){
						u.voisins.get(v).cout = u.cout + 1;
						u.voisins.get(v).heuristique = u.voisins.get(v).cout + distance(u.voisins.get(v), objectif);
						u.voisins.get(v).parent = u; // Noeud courant u est parent du/des noeud(s) voisin(s)
						openList.add(u.voisins.get(v));
					}
				}
			}
			closedList.add(u);
		}
		throw new RuntimeException("Impossible d'accéder à l'objectif");
	}

*/
}

