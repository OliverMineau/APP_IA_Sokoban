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

import static java.lang.Math.*;

class IASokoban extends IA {
	Random r;
	final static int MARRON = 0xBB7755;
	final static int ROUGE = 0xFF0000;
	final static int VERT = 0x00FF00;

	boolean retour = true;


	public IASokoban() {
		r = new Random();
	}


	@Override
	public Sequence<Coup> joue() {

		//Debut IA
		//Niveau n = super.niveau;
		Niveau n = jeu.niveau();

		Sequence<Coup> resultat = Configuration.nouvelleSequence();


		if (retour) {

			//Creation du graphe
			GrapheV2 gTotal = new GrapheV2(n.c,n.l);
			Coords perso = new Coords(n.pousseurC, n.pousseurL);
			creationGraphe(gTotal, n, perso, 0);

			//Si aucune caisse ou but dans le niveau
			if(gTotal.caisse == null || gTotal.but == null){

				if(gTotal.caisse == null)
					System.out.println("Pas de caisse dans ce niveau !");
				if(gTotal.but == null)
					System.out.println("Pas de but dans ce niveau !");

				System.out.println("Niveau impossible : passer au niveau suivant\n");
				niveauSuivant();
				return null;
			}

			//Algo aStar de CAISSE -> BUT
			NoeudV2 butDuPerso = caisseVersBut(gTotal.caisse,gTotal.but,gTotal, n);

			//Si la caisse ne peut pas aller vers le but
			if(butDuPerso == null){
				System.out.println("Le pousseur ne peut pas se placer " +
						" pour que la caisse (" + gTotal.caisse.x + "," + gTotal.caisse.y + ")" +
						" arrive au but (" + gTotal.but.x + "," + gTotal.but.y + ")");
				System.out.println("Niveau impossible : passer au niveau suivant\n");
				niveauSuivant();
				return null;

			}else{
				System.out.println("Le pousseur doit se placer à ("+ butDuPerso.x + "," + butDuPerso.y + ")" +
						" pour que la caisse (" + gTotal.caisse.x + "," + gTotal.caisse.y + ")" +
						" arrive au but (" + gTotal.but.x + "," + gTotal.but.y + ")");
			}



			//Liste des etapes/coups que le pousseur doit suivre
			ArrayList<NoeudV2> etapes;

			//Si la position du perso est egale à la position de BUTDUPOUSSEUR
			if(butDuPerso.x == gTotal.perso.x && butDuPerso.y == gTotal.perso.y){
				System.out.println("Le pousseur est a la bonne position");

				//Calculer comment pousser la caisse
				return aCoteDeCaisse(n, resultat);
			}

			////System.out.println("Test chemin entre : " + gTotal.perso.x + "," + gTotal.perso.y + " et " + butDuPerso.x + "," +  butDuPerso.y);
			////System.out.println("Caisse : " + gTotal.caisse.x + "," + gTotal.caisse.y + " et a caisse " + n.aCaisse(gTotal.caisse.y,gTotal.caisse.x));


			//Algo aStar du POUSSEUR -> BUTDUPOUSSEUR
			etapes = aStar(gTotal.perso,butDuPerso,gTotal, n, 0);

			//Si le pousseur ne peut pas atteindre BUTDUPOUSSEUR
			if(etapes == null){

				////System.out.println("Perso ne peut pas se placer pour pousser la caisse");

				//Si la caisse n'a pas de voisins
				ArrayList<NoeudV2> voisins = gTotal.caisse.voisins();
				if(voisins==null || voisins.size() == 0){
					System.out.println("La caisse est bloquée !");
					System.out.println("Niveau impossible : passer au niveau suivant\n");
					niveauSuivant();
					return resultat;
				}

				//Pour chaque voisin de la caisse
				for(NoeudV2 voisin: voisins){

					//Regarder si la position opposée est valide (pas un mur et pas un couloir)
					if(voisin.gauche == gTotal.caisse){
						if(gTotal.caisse.droit != null && gTotal.caisse.droit.estCouloir){
							continue;
						}
					}
					if(voisin.droit == gTotal.caisse){
						if(gTotal.caisse.gauche != null && gTotal.caisse.gauche.estCouloir){
							continue;
						}
					}
					if(voisin.haut == gTotal.caisse){
						if(gTotal.caisse.bas != null && gTotal.caisse.bas.estCouloir){
							continue;
						}
					}
					if(voisin.bas == gTotal.caisse){
						if(gTotal.caisse.haut != null && gTotal.caisse.haut.estCouloir){
							continue;
						}
					}

					////System.out.println("Test chemin entre : " + gTotal.perso.x + "," + gTotal.perso.y + " et " + voisin.x + "," +  voisin.y);

					//Algo POUSSEUR -> VOISINCAISSE
					etapes = aStar(gTotal.perso,voisin,gTotal, n, 0);

					//Chemin est trouvé
					if(etapes != null && etapes.size()>0){
						////System.out.println("Chemin existant pousseur caisse (pas le bon)");
						break;
					}
				}
			}

			//Si aucun chemin trouvé dans les voisins
			if(etapes == null || etapes.size() == 0){
				System.out.println("Le pousseur ne peut pas pousser la caisse jusqu'au but");
				System.out.println("Niveau impossible : passer au niveau suivant\n");
				niveauSuivant();
				return null;
			}

			int pousseurL = n.lignePousseur();
			int pousseurC = n.colonnePousseur();

			//On crée les coups correspondant aux etapes
			for (int i = 0; i < etapes.size() ; i++) {

				Coup coup = new Coup();

				coup.deplacementPousseur(pousseurL, pousseurC, etapes.get(i).y, etapes.get(i).x);

				//Si deja un marque, change la couleur
				if(n.marque(etapes.get(i).y, etapes.get(i).x)==ROUGE)
					n.fixerMarque(ROUGE,etapes.get(i).y, etapes.get(i).x);
				else
					n.fixerMarque(VERT,etapes.get(i).y, etapes.get(i).x);

				resultat.insereQueue(coup);

				pousseurL = etapes.get(i).y;
				pousseurC = etapes.get(i).x;

			}

		}else{
			//Le perso est arrivé a coté de la caisse, pousser
			aCoteDeCaisse(n, resultat);
		}

		retour = !retour;

		return resultat;

	}

	public void niveauSuivant(){
		niveau.nbCaissesSurBut = niveau.nbButs;
		jeu.prochainNiveau();
	}

	public Sequence<Coup> aCoteDeCaisse(Niveau n,Sequence<Coup> resultat){

		//Si a caisse alors on pousse
		int x=0,y=0;
		if(n.aCaisse(n.pousseurL+1,n.pousseurC)){
			x=0;
			y=1;
		}
		else if(n.aCaisse(n.pousseurL-1,n.pousseurC)){
			x=0;
			y=-1;
		}
		else if(n.aCaisse(n.pousseurL,n.pousseurC+1)){
			x=1;
			y=0;
		}
		else if(n.aCaisse(n.pousseurL,n.pousseurC-1)){
			x=-1;
			y=0;
		}

		//Si a cote d'une caisse
		if(x!=0 || y!=0){

			//Si on ne peut plus pousser
			if(n.pousseurL+2*y >= n.l || n.pousseurC+2*x >= n.c)
				return null;

			//Creation du coup
			Coup coup = new Coup();
			coup.deplacementCaisse(n.pousseurL+y,n.pousseurC+x, n.pousseurL+2*y,n.pousseurC+2*x);

			resultat.insereQueue(coup);
		}

		return resultat;
	}

	//Indique si
	private int ajoutAvisiter(ArrayList<NoeudV2> aVisiter, int x, int y) {

		for (int i = 0; i < aVisiter.size(); i++) {
			if (aVisiter.get(i).x == x && aVisiter.get(i).y == y) {
				return i;
			}
		}
		return -1;
	}

	private void ajoutNoeud(ArrayList<NoeudV2> aVisiter, int x, int y, NoeudV2 ndCourant, GrapheV2 g, int direction){
		int nInd = ajoutAvisiter(aVisiter, x, y);

		NoeudV2 tmp = null;
		//Si pas noeud dans aVisiter
		if (nInd == -1) {
			tmp = new NoeudV2(x, y); //Creation du noeud
			aVisiter.add(tmp); // ajout dans les noeud a visiter
			g.ajouteNoeud(tmp); //ajoute dans le graphe
		}

		switch (direction){
			case 0:
				if(nInd == -1)
					ndCourant.gauche = tmp; // ajoute qu'il est le voisin gauche
				else
					ndCourant.gauche = aVisiter.get(nInd);
				break;

			case 1:
				if(nInd == -1)
					ndCourant.droit = tmp; // ajoute qu'il est le voisin droit
				else
					ndCourant.droit = aVisiter.get(nInd);
				break;

			case 2:
				if(nInd == -1)
					ndCourant.haut = tmp; // ajoute qu'il est le voisin haut
				else
					ndCourant.haut = aVisiter.get(nInd);
				break;

			case 3:
				if(nInd == -1)
					ndCourant.bas = tmp; // ajoute qu'il est le voisin bas
				else
					ndCourant.bas = aVisiter.get(nInd);
				break;
		}
	}

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

				//Ajouter le noeud courant dans le graphe
				g.ajouteNoeud(ndCourant);

				//Voisins
				if (x - 1 >= 0 && !n.aMur(y, x - 1)) {
					ajoutNoeud(aVisiter, x-1, y, ndCourant, g, 0);
				}
				if (x + 1 < n.colonnes() && !n.aMur(y, x + 1)) {
					ajoutNoeud(aVisiter, x+1, y, ndCourant, g, 1);
				}
				if (y - 1 >= 0 && !n.aMur(y - 1, x)) {
					ajoutNoeud(aVisiter, x, y-1, ndCourant, g, 2);
				}
				if (y + 1 < n.lignes() && !n.aMur(y + 1, x)) {
					ajoutNoeud(aVisiter, x, y+1, ndCourant, g, 3);
				}

				//Si le noeud courant est la fin d'un couloir (un seul voisin) est qu'il n'y a pas de but
				if(ndCourant.voisins().size() == 1 && !n.aBut(ndCourant.y,ndCourant.x)){

					NoeudV2 tmp = ndCourant.voisins().get(0);
					NoeudV2 ancien = ndCourant;

					ndCourant.estCouloir = true;

					//Tant que le noeud n'est pas un but et qu'il a moins de 3 voisins
					while(tmp.voisins().size() < 3 && n.aBut(tmp.y,tmp.x)){
						tmp.estCouloir = true;
						for(NoeudV2 voisin:tmp.voisins()){
							if(voisin != ancien){
								ancien = tmp;
								tmp = voisin;
								break;
							}
						}
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

				NoeudV2 tmp = arrivee;

				ArrayList<NoeudV2> etapes = new ArrayList<>();

				etapes.add(0,tmp);
				if(type==1 && n.marque(tmp.y,tmp.x)!=0)
					n.fixerMarque(ROUGE,tmp.y,tmp.x);
				else

					//Ajouter les etapes
					while(true){
						if(tmp == depart || tmp == null){
							etapes.remove(0);
							return etapes;
						}

						etapes.add(0,tmp.parent);
						if(type==1)
							n.fixerMarque(ROUGE,tmp.parent.y,tmp.parent.x);

						tmp = tmp.parent;
					}
			}

			ouvertList.remove(courant);
			fermeList.add(courant);

			/**
			 * Ajouter le voisin du noeud courant si :
			 *
			 * 	Si on cherche le chemin pour le pousseur -> caisse (type 0):
			 *  	- Le noeud est non null et
			 *  	- Le noeud ne contient pas une caisse
			 *
			 *	Si on cherche le chemin caisse -> but (type 1):
			 *  	- Le noeud est non null et
			 *  	- Le noeud ne contient pas une caisse et
			 *  	- Le noeud en face n'est pas null et
			 *  	- ( ( Le noeud n'est pas un couloir et
			 *  	- Le noeud n'est pas un coin ) ou
			 *  	- Le noeud contient un but )
			 */
			ArrayList<NoeudV2> voisins = new ArrayList<>();
			if(courant.gauche != null && !n.aCaisse(courant.gauche.y,courant.gauche.x) && (type == 0 || (((!courant.gauche.estCouloir && !courant.gauche.estCoin()) || n.aBut(courant.gauche.y,courant.gauche.x)) && courant.droit != null)))
				voisins.add(courant.gauche);
			if(courant.droit != null && !n.aCaisse(courant.droit.y,courant.droit.x) && (type == 0 || (((!courant.droit.estCouloir && !courant.droit.estCoin()) || n.aBut(courant.droit.y,courant.droit.x)) && courant.gauche != null)))
				voisins.add(courant.droit);
			if(courant.haut != null && !n.aCaisse(courant.haut.y,courant.haut.x) && (type == 0 || (((!courant.haut.estCouloir && !courant.haut.estCoin()) || n.aBut(courant.haut.y,courant.haut.x)) && courant.bas != null)))
				voisins.add(courant.haut);
			if(courant.bas != null && !n.aCaisse(courant.bas.y,courant.bas.x) && (type == 0 || (((!courant.bas.estCouloir && !courant.bas.estCoin()) || n.aBut(courant.bas.y,courant.bas.x)) && courant.haut != null)))
				voisins.add(courant.bas);

			//Pour chaque voisin
			for (NoeudV2 voisin:voisins) {

				//Calcul du F
				double f = voisin.g + voisin.heuristique(arrivee);

				//Si le voisin est dans aucune liste
				if(!ouvertList.contains(voisin) && !fermeList.contains(voisin)){
					voisin.parent = courant;
					voisin.g = courant.g + 1;
					ouvertList.add(voisin);

				}
				//Si le voisin est moins cher
				else if (f < voisin.g + voisin.heuristique(arrivee)){
					voisin.parent = courant;
					voisin.g = courant.g + 1;

					//On change de liste
					if(fermeList.contains(voisin)){
						fermeList.remove(voisin);
						ouvertList.add(voisin);
					}
				}
			}
		}
		return null;

	}

	public NoeudV2 coutMinimal(ArrayList<NoeudV2> noeuds){
		//Cout minimal dans la liste noeuds

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
			//Pas d'étapes, chemin impossible
			return null;
		}

		int dirx = Integer.compare(depart.x, etapes.get(0).x);
		int diry = Integer.compare(depart.y, etapes.get(0).y);
		////System.out.println("Caisse : Premiere etape : " + etapes.get(etapes.size()-1).x + "," + etapes.get(etapes.size()-1).y);

		//Renvoyer le noeud ou le pousseur doit aller pour pousser la caisse dans la bonne direction
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

		////System.out.println("BUT : Deplacement de la caisse impossible");
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

