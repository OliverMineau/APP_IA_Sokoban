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
import Structures.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

class IASokoban extends IA {
	Random r;
	final static int MARRON = 0xBB7755;
	final static int ROUGE = 0xFF0000;
	final static int VERT = 0x00FF00;

	boolean retour = true;

	Noeud caisse;
	Noeud but;

	public IASokoban() {
		r = new Random();
	}

	//Fonction de départ de l'IA de l'APP ALGO6


	@Override
	public Sequence<Coup> joue() {

		//Debut IA
		//Niveau n = super.niveau;
		Niveau n = jeu.niveau();

		Sequence<Coup> resultat = Configuration.nouvelleSequence();

		/*GrapheV2 gTotal = new GrapheV2(n.c,n.l);
		Coords perso = new Coords(n.pousseurC, n.pousseurL);
		creationGraphe(gTotal, n, perso, 0);
		showGraph(gTotal);

		//ArrayList<NoeudV2> et = aStar(gTotal.tableau[gTotal.caisse.y][gTotal.caisse.x],gTotal.tableau[gTotal.but.y][gTotal.but.x],gTotal, n, 1);
		ArrayList<NoeudV2> et = aStar(gTotal.tableau[perso.y][perso.x],gTotal.tableau[gTotal.caisse.y][gTotal.caisse.x],gTotal, n, 0);
		System.out.println("Size : " + et.size());

		for (NoeudV2 e:et) {
			System.out.println("Etape : " + e.x + "," + e.y);
			n.fixerMarque(VERT,e.y,e.x);
		}*/


		if (retour) {
			//Creation g de toutes les positions possibles du perso
			GrapheV2 gTotal = new GrapheV2(n.c,n.l);
			Coords perso = new Coords(n.pousseurC, n.pousseurL);
			creationGraphe(gTotal, n, perso, 0);
			//showGraph(gTotal);

			/*Noeud pousseur = null;
			for (int i = 0; i < gTotal.noeuds.size(); i++) {
				NoeudV2 noeud = gTotal.noeuds.get(i);
				if (noeud.x == n.colonnePousseur() && noeud.y == n.lignePousseur()) {
					pousseur = noeud;
				} else if (noeud.x == gTotal.caisse.x && noeud.y == gTotal.caisse.y) {
					caisse = noeud;
				} else if (n.aBut(noeud.y,noeud.x)) {
					but = noeud;
				}
			}*/

			if(gTotal.caisse == null){
				System.out.println("PAS de Caisses");
				return null;
			}

			//Algo CAISSE -> BUT
			NoeudV2 butDuPerso = caisseVersBut(gTotal.caisse,gTotal.but,gTotal, n);
			System.out.println("Caisse : " + gTotal.caisse.x + "," + gTotal.caisse.y);
			System.out.println("But : " + gTotal.but.x + "," + gTotal.but.y);

			//Trouver un moyen de differencier entre un niveau impossible et
			// le fait de bouger la caisse pour devoiler une possibilité
			if(butDuPerso == null){
				System.out.println("pas de deplacement direct possible");
			}else{
				System.out.println("Deplacement caisse -> but possible et\nperso doit se placer : " + butDuPerso.x + "," + butDuPerso.y);
			}


			//Algo POUSSEUR -> CAISSE
			ArrayList<NoeudV2> etapes = null;

			if(butDuPerso != null){

				if(butDuPerso.x == gTotal.perso.x && butDuPerso.y == gTotal.perso.y){
					System.out.println("Meme endroit donc peut etre a cote de caisse");
					return aCoteDeCaisse(n, resultat);
				}

				System.out.println("Test chemin entre : " + gTotal.perso.x + "," + gTotal.perso.y + " et " + butDuPerso.x + "," +  butDuPerso.y);
				System.out.println("Caisse : " + gTotal.caisse.x + "," + gTotal.caisse.y + " et a caisse " + n.aCaisse(gTotal.caisse.y,gTotal.caisse.x));
				etapes = aStar(gTotal.perso,butDuPerso,gTotal, n, 0);
			}

			if(etapes == null){

				System.out.println("Perso ne peut pas se placer pour pousser la caisse");

				ArrayList<NoeudV2> voisins = gTotal.caisse.voisins();
				if(voisins==null || voisins.size() == 0){
					System.out.println("Passer au niveau suivant, pas de possibilite");
					System.exit(0);
				}
				for(NoeudV2 voisin: voisins){

					System.out.println("Test chemin entre : " + gTotal.perso.x + "," + gTotal.perso.y + " et " + voisin.x + "," +  voisin.y);
					etapes = aStar(gTotal.perso,voisin,gTotal, n, 0);

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

			aCoteDeCaisse(n, resultat);

			System.out.println("Fin");
		}


		retour = !retour;

		return resultat;

	}


	public Sequence<Coup> aCoteDeCaisse(Niveau n,Sequence<Coup> resultat){
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

		return resultat;
	}

	private int ajoutAvisiter(ArrayList<NoeudV2> aVisiter, int x, int y) {
		for (int i = 0; i < aVisiter.size(); i++) {
			if (aVisiter.get(i).x == x && aVisiter.get(i).y == y) {
				return i;
			}
		}
		return -1;
	}

    /*public void creationGraphe(GrapheV2 g, Niveau n, Coords debut, int type) {

		int x = debut.x;
		int y = debut.y;

		//Ajoute le noeud present
		if(!n.aMur(y,x)){
			NoeudV2 nouv = new NoeudV2(x+1,y);
			g.ajouteNoeud(nouv);
		}

		//Ajoute son voisin droit
		if(x+1 < n.c && !n.aMur(y,x+1)){
			NoeudV2 nouv = new NoeudV2(x+1,y);
			nouv
			g.ajouteNoeud(nouv);

		}
	}*/


	public void creationGraphe(GrapheV2 g, Niveau n, Coords debut, int type) {

		ArrayList<NoeudV2> aVisiter = g.noeuds;
		aVisiter.add(new NoeudV2(debut.x, debut.y));

		int ind = 0;
		while (ind < aVisiter.size()) {

			int x = aVisiter.get(ind).x;
			int y = aVisiter.get(ind).y;

			//Si la case n'est pas un mur
			if (!n.aMur(y, x) && !(type == 1 && n.aCaisse(y, x))) {

				NoeudV2 ndCourant= aVisiter.get(ind);

				if (n.aCaisse(y, x)) {
					g.caisse = ndCourant;
				}

				if (n.aPousseur(y, x)) {
					g.perso = ndCourant;
				}

				if (n.aBut(y, x)) {
					g.but = ndCourant;
				}


				//n.fixerMarque(MARRON,y,x);


				g.ajouteNoeud(ndCourant);


				//Voisin gauche
				if (x - 1 >= 0 && !n.aMur(y, x - 1)) {
					int nInd = ajoutAvisiter(aVisiter, x - 1, y);
					//Si pas noeud dans aVisiter
					if (nInd == -1) {
						NoeudV2 tmp = new NoeudV2(x - 1, y); //Creation du noeud
						aVisiter.add(tmp); // ajout dans les noeud a visiter
						ndCourant.gauche = tmp; // ajoute qu'il est le voisin gauche
						g.ajouteNoeud(tmp); //ajoute dans le graphe
					} else {
						ndCourant.gauche = aVisiter.get(nInd);
					}
				}
				if (x + 1 < n.colonnes() && !n.aMur(y, x + 1)) {
					int nInd = ajoutAvisiter(aVisiter, x + 1, y);
					//Si pas noeud dans aVisiter
					if (nInd == -1) {
						NoeudV2 tmp = new NoeudV2(x + 1, y); //Creation du noeud
						aVisiter.add(tmp); // ajout dans les noeud a visiter
						ndCourant.droit = tmp; // ajoute qu'il est le voisin gauche
						g.ajouteNoeud(tmp); //ajoute dans le graphe
					} else {
						ndCourant.droit = aVisiter.get(nInd);
					}
				}
				if (y + 1 < n.lignes() && !n.aMur(y + 1, x)) {
					int nInd = ajoutAvisiter(aVisiter, x, y + 1);
					//Si pas noeud dans aVisiter
					if (nInd == -1) {
						NoeudV2 tmp = new NoeudV2(x, y + 1); //Creation du noeud
						aVisiter.add(tmp); // ajout dans les noeud a visiter
						ndCourant.bas = tmp; // ajoute qu'il est le voisin gauche
						g.ajouteNoeud(tmp); //ajoute dans le graphe
					} else {
						ndCourant.bas = aVisiter.get(nInd);
					}
				}
				if (y - 1 >= 0 && !n.aMur(y - 1, x)) {
					int nInd = ajoutAvisiter(aVisiter, x, y - 1);
					//Si pas noeud dans aVisiter
					if (nInd == -1) {
						NoeudV2 tmp = new NoeudV2(x, y - 1); //Creation du noeud
						aVisiter.add(tmp); // ajout dans les noeud a visiter
						ndCourant.haut = tmp; // ajoute qu'il est le voisin gauche
						g.ajouteNoeud(tmp); //ajoute dans le graphe
					} else {
						ndCourant.haut = aVisiter.get(nInd);
					}
				}
			}

			ind++;
		}
	}

	public ArrayList<NoeudV2> aStar(NoeudV2 depart, NoeudV2 arrivee, GrapheV2 g, Niveau n, int type) {

		ArrayList<NoeudV2> ouvertList = new ArrayList<>();
		ArrayList<NoeudV2> fermeList = new ArrayList<>();

		ouvertList.add(depart);

		while(!ouvertList.isEmpty()){

			NoeudV2 courant = coutMinimal(ouvertList);

			if(courant == arrivee){
				//Arrivée
				//faire qq chose
				System.out.println("FIN A*");

				NoeudV2 tmp = arrivee;

				ArrayList<NoeudV2> etapes = new ArrayList<>();

				System.out.println("Etape" + depart.x + "," + depart.y);
				System.out.println("Etape" + arrivee.x + "," + arrivee.y);

				etapes.add(0,tmp);
				while(true){

					if(tmp == depart || tmp == null){
						etapes.remove(0);
						return etapes;
					}

					etapes.add(0,tmp.parent);
					//System.out.println("Etape ajoutee" + tmp.parent.x + "," + tmp.parent.y);
					tmp = tmp.parent;


				}
			}

			ouvertList.remove(courant);
			fermeList.add(courant);

			//Pour chaque voisin de courant
			ArrayList<NoeudV2> voisins = new ArrayList<>();
			if(courant.gauche != null && !n.aCaisse(courant.gauche.y,courant.gauche.x) && (type == 0 || (!courant.gauche.estCoin() && courant.droit != null)))
				voisins.add(courant.gauche);
			if(courant.droit != null && !n.aCaisse(courant.droit.y,courant.droit.x) && (type == 0 || (!courant.droit.estCoin() && courant.gauche != null)))
				voisins.add(courant.droit);
			if(courant.haut != null && !n.aCaisse(courant.haut.y,courant.haut.x) && (type == 0 || (!courant.haut.estCoin() && courant.bas != null)))
				voisins.add(courant.haut);
			if(courant.bas != null && !n.aCaisse(courant.bas.y,courant.bas.x) && (type == 0 || (!courant.bas.estCoin() && courant.haut != null)))
				voisins.add(courant.bas);

			for (NoeudV2 voisin:voisins) {
				double f = voisin.g + voisin.heuristique(arrivee);

				if(!ouvertList.contains(voisin) && !fermeList.contains(voisin)){
					voisin.parent = courant;
					voisin.g = courant.g + 1;
					ouvertList.add(voisin);

				} else if (f < voisin.g + voisin.heuristique(arrivee)){
					voisin.parent = courant;
					voisin.g = courant.g + 1;
					if(fermeList.contains(voisin)){
						fermeList.remove(voisin);
						ouvertList.add(voisin);
					}
				}
			}
		}
		return null;

	}

	//Renvoie
	public NoeudV2 coutMinimal(ArrayList<NoeudV2> noeuds){
		NoeudV2 noeudMin = noeuds.get(0);

		for (int i = 0; i < noeuds.size(); i++){

			if(noeuds.get(i).f < noeudMin.f){
				noeudMin = noeuds.get(i);
			}
		}
		return noeudMin;
	}


	public NoeudV2 caisseVersBut(NoeudV2 depart, NoeudV2 arrivee, GrapheV2 g, Niveau n){
		ArrayList<NoeudV2> etapes = aStar(depart,arrivee,g, n, 1);

		//Si pas possible
		if(etapes == null || etapes.size() == 0){
			System.out.println("Pas d'etapes caisse but");
			return null;
		}

		int dirx = Integer.compare(depart.x, etapes.get(0).x);
		int diry = Integer.compare(depart.y, etapes.get(0).y);
		System.out.println("Caisse : Premiere etape : " + etapes.get(etapes.size()-1).x + "," + etapes.get(etapes.size()-1).y);

		//Renvoyer le noeud dans la bonne direction pour pousser la caisse
		if(depart.gauche != null && depart.gauche.x == (dirx+depart.x) && depart.gauche.y == (diry+depart.y)){
			return depart.gauche;
		}
		if(depart.droit != null && depart.droit.x == (dirx+depart.x) && depart.droit.y == (diry+depart.y)){
			return depart.droit;
		}
		if(depart.haut != null && depart.haut.x == (dirx+depart.x) && depart.haut.y == (diry+depart.y)){
			return depart.haut;
		}
		if(depart.bas != null && depart.bas.x == (dirx+depart.x) && depart.bas.y == (diry+depart.y)){
			return depart.bas;
		}

		System.out.println("BUT : Deplacement de la caisse impossible");
		return null;
	}


	public void showGraph(GrapheV2 g) {
		for (int i = 0; i < g.noeuds.size(); i++) {
			System.out.format("Coords : x%d,y%d\n", g.noeuds.get(i).x, g.noeuds.get(i).y);

			if(g.noeuds.get(i).gauche !=null)
				System.out.println("	Gauche : " + g.noeuds.get(i).gauche.x + "," + g.noeuds.get(i).gauche.y);
			if(g.noeuds.get(i).droit !=null)
				System.out.println("	Droit : " + g.noeuds.get(i).droit.x + "," + g.noeuds.get(i).droit.y);
			if(g.noeuds.get(i).haut !=null)
				System.out.println("	Haut : " + g.noeuds.get(i).haut.x + "," + g.noeuds.get(i).haut.y);
			if(g.noeuds.get(i).bas !=null)
				System.out.println("	Bas : " + g.noeuds.get(i).bas.x + "," + g.noeuds.get(i).bas.y);

		}
		System.out.println("Taille g : " + g.noeuds.size());

	}


}

