
/*
 * 4 niveaux de qualifications des employés (novice, intermédiaire, préformation, formé)
ombre fixe d’employés avec composition donnée (uniquement variation pendant les vacances d’été i.e. deux modes)
C1 : Récouvrement des tranches par compétences
C2 : Somme des compténces par tranche

C3 : Nombre totale des compténces par tranche,un employé peut combler le besoin avec ses autres compténce

C4 : Un employé ne peut pas traivailler pendant ses vacances
C5 : Série de nuit : Pour les 100%=série de 3 à 5 nuits / 4 sem.
C6 : Série de nuit : Pour les 80% =série de 2 à 4 nuits / 4 sem.
C7 : 2 congés minimum après une série de nuits
C8&C9 : 1 week-end de congé sur 4 semaines de planification (selon règlement interne des HUG), et 2 dans la mesure du possible.
+C11 : 8 congés hebdomadaires (CH) sont planifiés sur une période de 4 semaines
+C10 : Tout personnel soignant doit faire des nuits.
+C12 : 6 jours de travail cons. maximum pour les employés à 100%

+C13 : 1 week-end de congé sur 4 semaines de planification(selon règlement interne des HUG),et 2 dans la mesure du possible. 
 */
package solve;

import java.util.ArrayList;

import ressource.Nurse;
import com.google.ortools.constraintsolver.DecisionBuilder;
import com.google.ortools.constraintsolver.IntExpr;
import com.google.ortools.constraintsolver.IntVar;
import com.google.ortools.constraintsolver.Solver;

public class NurseSolverConstrainte implements SolverInterface {
	public ArrayList<Nurse> listInf;
	static {
		//System.loadLibrary("jniortools");
		System.load("/home/bilginh/Bureau/projet_sem/workspace/HUG/libjniortools.so");
	}
	public NurseSolverConstrainte(ArrayList<Nurse> listInf){
		this.listInf=listInf;
	}
	public  void solve(int n, int num) {

		Solver solver = new Solver("NurseSolver");
		//les données des contraintes
		int jtl;// 6 jours de travail consecutive  (C)
		int nbrCongeWeekend;
		int nbrTrancheParJour=3;
		int nbrJours=28;
		int nbr_nuit_min=1;

		
		/*** DATA  ***/
		// tranche 1 = matin
		// tranche 2 = après midi
		// tranche 3 = nuit

		int nbr_inf = this.listInf.size();
		// nombre tranches 
		int num_tranches = nbrJours*nbrTrancheParJour;

		// nombre infirmiers par jour
		int[] nb_inf_jour = new int[num_tranches];
		for (int i = 0; i < num_tranches; i++)
			nb_inf_jour[i] =(int) (Math.random() * (nbr_inf - 3)) + 1;

		IntVar[][] matrice = new IntVar[nbr_inf][num_tranches];
		IntVar[] matrice_flat = new IntVar[num_tranches * nbr_inf];

		/*****  On applique les préférences des infirmières à la matrice de départ ****/
		for (int i = 0; i < nbr_inf; i++)
			for (int j = 0; j < num_tranches; j++) {
				matrice[i][j] = solver.makeIntVar(0, 1, "m[" + i + "," + j
						+ "]");
				matrice_flat[i * num_tranches + j] = matrice[i][j];
			}
		
			/*prend en charge les preferences
			 * 	for (int i = 0; i < nbr_inf; i++)
					for (int j = 0; j < num_tranches; j++) {
						if (Integer.valueOf(listInf.get(i).preferences.elementAt(j).toString()) == -1) { 
							// -1 = choix indifférent
							matrice[i][j] = solver.makeIntVar(0, 1, "m[" + i + "," + j
									+ "]");
							// sinon on attribut la préférence de l'infirmiere
						} else {
							matrice[i][j] = solver.makeIntVar(
									Integer.valueOf(listInf.get(i).preferences.elementAt(j).toString()),
									Integer.valueOf(listInf.get(i).preferences.elementAt(j).toString()), "m[" + i
											+ "," + j + "]");
						}
						matrice_flat[i * num_tranches + j] = matrice[i][j];
					}*/
		
		
	

	/**** CONTRAINTES ****/

		/***   nombre d'inf par jour minimum   ***/
		for (int i = 0; i < num_tranches; i++) {
			IntVar[] col = new IntVar[nbr_inf];
			for (int j = 0; j < nbr_inf; j++) {
				col[j] = matrice[j][i];
			}
			//System.out.println("contrainte add " + i + " ");
			solver.addConstraint(solver.makeSumEquality(col, nb_inf_jour[i]));
			//System.out.println("contrainte add " + i + " ");
		}
		
		/***    Une seule tranche par inf par jour            ***/
		
		System.out.println("2");
		
		for (int i = 0; i < num_tranches; i += nbrTrancheParJour) {
			IntVar[] row = new IntVar[nbrTrancheParJour];
			for (int j = 0; j < nbr_inf; j++) {
				
				for (int k = 0; k < nbrTrancheParJour; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);

					row[k] = matrice[j][i + k];
				}
				//solver.addConstraint(solver.makeSumLessOrEqual(row, 1)); /***********************ERROR ************/
			}
		}
		

		/***** Jours Travail Limite (+1) travail cons. 6 jours de max c12 pour taux 100% ****/
		
		System.out.println("C12");
		jtl = nbrJours;
		for (int i = 0; i < num_tranches - jtl; i++) {
			IntVar[] row = new IntVar[nbrJours];
			for (int j = 0; j < nbr_inf; j++) {
				if(this.listInf.get(j).taux_active == 100){
				for (int k = 0; k < jtl; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					row[k] = matrice[j][i + k];
				}
				solver.addConstraint(solver.makeSumLessOrEqual(row, (jtl - 1)));
				}
			}
		}
		
		/****  C11 8 jours de congé  ******/
		System.out.println("C11");
		 
		for (int i = 0; i < num_tranches - jtl; i++) {
			IntVar[] row = new IntVar[jtl];
			for (int j = 0; j < nbr_inf; j++) {
				for (int k = 0; k < jtl; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					row[k] = matrice[j][i + k];
				}
				solver.addConstraint(solver.makeSumLessOrEqual(row,
						num_tranches - (nbrTrancheParJour * 8)));
			}
		}
		

		/***** tout personnel doit faire des nuits (1 nuit)  *********/
			
			for (int j = 0; j < nbr_inf; j++) {
				IntVar[] rowNuit = new IntVar[nbrJours];
				
				for (int k = 1; k <= nbrJours; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					rowNuit[k-1] = matrice[j][k*nbrTrancheParJour-1];
				}
				solver.addConstraint(solver.makeSumGreaterOrEqual(rowNuit, nbr_nuit_min));
	
			}
		
		
		/** tout personnel doit faire au moins un weekend de congé c13  ***/
			System.out.println("C13");
		nbrCongeWeekend =3;
		int nbrTrancheSemaine=7;
		int nbrSemaines=4;
		for (int i = 1; i <= nbrSemaines; i++) {
			IntVar[] row1 = new IntVar[nbrSemaines];
			//IntVar[] row3 = new IntVar[nbrSemaines];
			for (int j = 0; j < nbr_inf; j++) {
				IntVar[] wk = new IntVar[2];
				for (int k = 0; k < nbrSemaines; k++) {					
					wk[0] = matrice[j][(k+1)*nbrTrancheSemaine*nbrTrancheParJour -1];
					wk[1] = matrice[j][(k+1)*nbrTrancheSemaine*nbrTrancheParJour -2];
					row1[k] =solver.makeProd(wk[0],wk[1]).var();
		
				}
			}	
				
				solver.addConstraint(solver.makeSumLessOrEqual(row1, nbrCongeWeekend));				
			
		}
				
		/*** Solve ****/
		DecisionBuilder db = solver.makePhase(matrice_flat,
							 solver.CHOOSE_FIRST_UNBOUND, solver.ASSIGN_RANDOM_VALUE);

		solver.newSearch(db);

		int c = 0;
		System.out.println("Solver");
		while (solver.nextSolution()) {
			System.out.println("\n Horaire :");
			for (int i = 0; i < nbr_inf; i++) {
				for (int j = 0; j < num_tranches; j++) {
					System.out.print(matrice[i][j].value() + " ");
				}
				System.out.println();
			}
			System.out.println();
			for (int i = 0; i < num_tranches; i++)
				System.out.print(nb_inf_jour[i] + " ");

			c++;
			if (num > 0 && c >= num) {
				break;
			}
		}

		solver.endSearch();

		// Statistics
		System.out.println();
		System.out.println("Solutions: " + solver.solutions());
		System.out.println("Failures: " + solver.failures());
		System.out.println("Branches: " + solver.branches());
		System.out.println("Wall time: " + solver.wallTime() + "ms");
	}


}
