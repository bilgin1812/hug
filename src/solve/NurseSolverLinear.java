/*
 * 4 niveaux de qualifications des employÃ©s (novice, intermÃ©diaire, prÃ©formation, formÃ©)
 ombre fixe dâemployÃ©s avec composition donnÃ©e (uniquement variation pendant les vacances dâÃ©tÃ© i.e. deux modes)
 C1 : RÃ©couvrement des tranches par compÃ©tences
 C2 : Somme des comptÃ©nces par tranche

 C3 : Nombre totale des comptÃ©nces par tranche,un employÃ© peut combler le besoin avec ses autres comptÃ©nce

 C4 : Un employÃ© ne peut pas traivailler pendant ses vacances
 C5 : SÃ©rie de nuit : Pour les 100%=sÃ©rie de 3 Ã  5 nuits / 4 sem.
 C6 : SÃ©rie de nuit : Pour les 80% =sÃ©rie de 2 Ã  4 nuits / 4 sem.
 C7 : 2 congÃ©s minimum aprÃšs une sÃ©rie de nuits
 C8 : 8 congÃ©s hebdomadaires (CH) sont planifiÃ©s sur une pÃ©riode de 4 semaines
 C9 : Tout personnel soignant doit faire des nuits.
 +C10 : 6 jours de travail cons. maximum pour les employÃ©s Ã  100%
 +C11 : 1 week-end de congÃ© sur 4 semaines de planification(selon rÃšglement interne des HUG),et 2 dans la mesure du possible. 
 */
package solve;

import java.util.ArrayList;

import ressource.ConstraintPerShift;
import ressource.Contstraint;
import ressource.Nurse;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class NurseSolverLinear implements SolverInterface {
	public ArrayList<Nurse> listInf;
	public Contstraint constraints;
	public ArrayList<ConstraintPerShift>  listConstraintsPerShift;
	static {
		// Windows
		//System.loadLibrary("jniortools");

		// Linux
		 System.load("/home/bilginh/Bureau/projet_sem/workspace/HUG/libjniortools.so");

	}

	public NurseSolverLinear(ArrayList<Nurse> listInf, Contstraint contraintes,ArrayList<ConstraintPerShift>  listConstraintsPerShift) {
		this.listInf = listInf;
		this.constraints=contraintes;
		this.listConstraintsPerShift=listConstraintsPerShift;
		
	}


	private static MPSolver createSolver(String solverType) {
		try {
			return new MPSolver("NurseSolver",
					MPSolver.OptimizationProblemType.valueOf(solverType));
		} catch (java.lang.IllegalArgumentException e) {
			return null;
		}
	}

	public void solve(int n, int num,String solverType) {

		MPSolver solver = createSolver(solverType);
		// GLOP_LINEAR_PROGRAMMING <-- recommandé (celui fait par google)
		// CLP_LINEAR_PROGRAMMING
		// GLPK_LINEAR_PROGRAMMING

		// Time limit in milliseconds (0 = no limit).
		solver.setTimeLimit(10000);

		// les donnÃ©es des contraintes
		int jtl;// 6 jours de travail consecutive (C)

		// DATA

		// tranche 1 = matin
		// tranche 2 = après midi
		// tranche 3 = nuit

		int nbr_inf = this.listInf.size();
		int jc = 8; // jours de congé par mois
		int nbrJours = 28;
		int nbrTrancheParJour = 3;
		int num_days = nbrJours * nbrTrancheParJour;

		// nb inf par jours
		int[] nb_inf_jour = new int[num_days];
		for (int i = 0; i < num_days; i++) {
			nb_inf_jour[i] = (int) (Math.random() * (nbr_inf - 13)) + 3;
			System.out.print(nb_inf_jour[i] + " ");
		}
		System.out.println();

		MPVariable[][] matrice = new MPVariable[nbr_inf][num_days];

		MPObjective obj = solver.objective();
		// On applique les préférences des infirmières à la matrice de départ
		for (int i = 0; i < nbr_inf; i++)
			for (int j = 0; j < num_days; j++) {

				// 1 = travaille, 0 = travaille pas
				// on créer une matrice de qui sera composé de 1 ou 0
				matrice[i][j] = solver.makeIntVar(0, 1, "m[" + i + "," + j
						+ "]");

				// pref va du plus grand au plus petit
				// 5 = je veux vraiment travailler, 1 = je veux pas trop, 0 =
				// c'est égal
				if (Integer.valueOf(listInf.get(i).preferences.elementAt(j).toString()) != -1) 
					{
					obj.setCoefficient(matrice[i][j],
							Integer.valueOf( listInf.get(i).preferences.elementAt(j).toString()));
				} else {
					// -1 = je NE PEUT PAS travailler (vacances)
					// C4 Un employe de peut pas travailler durant ses vacances
					obj.setCoefficient(matrice[i][j], -Double.MAX_VALUE);
					// on met une valeure très basse pour être sûr qu'il ne
					// travaillera pas
				}
			}
		obj.setMaximization();

		// --------------------------------------------------------------------
		// --------------------------------------------------------------------
		// --------------------------------------------------------------------
		// Contraintes

		// nombre d'inf par tranches minimum
		for (int i = 0; i < num_days; i++) {
			MPConstraint c1 = solver.makeConstraint(nb_inf_jour[i],
					nb_inf_jour[i]);
			for (int j = 0; j < nbr_inf; j++) {
				c1.setCoefficient(matrice[j][i], 1);
			}
		}

		// Une seule tranche par inf par jour
		for (int i = 0; i < num_days; i += nbrTrancheParJour) {
			for (int j = 0; j < nbr_inf; j++) {
				MPConstraint c2 = solver.makeConstraint(0, 1);
				for (int k = 0; k < nbrTrancheParJour; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					c2.setCoefficient(matrice[j][i + k], 1);
				}
			}
		}

		for (int l = 0; l < nbr_inf; l++)
			if (listInf.get(l).taux_active == 100) {
				// C5 Série de nuit : 3à5 nuits / 4sem
				// 100%
				int nb_nuit_max = 6;
				for (int j = 0; j < num_days
						- (nbrTrancheParJour * nb_nuit_max); j++) {
					MPConstraint c5 = solver.makeConstraint(0, 5);
					for (int k = 0; k < nb_nuit_max; k++) {
						c5.setCoefficient(matrice[l][nbrTrancheParJour * k + j
								+ 2], 1);
					}
				}

			} else {
				// C6 Série nuits de 2 à 4 nuits / 4 sem.
				// 80%
				int nb_nuit_max = 5;
				for (int j = 0; j < num_days
						- (nbrTrancheParJour * nb_nuit_max); j++) {
					MPConstraint c5 = solver.makeConstraint(0, 4);
					for (int k = 0; k < nb_nuit_max; k++) {
						c5.setCoefficient(matrice[l][nbrTrancheParJour * k + j
								+ 2], 1);
					}
				}
			}

		// C7 2 congé minimum après une série de nuits
		int repos = 3;
		int nb_nuits = 5;
		for (int i = 0; i < nbr_inf; i++) {
			for (int j = 0; j < num_days - (nbrTrancheParJour * nb_nuits); j++) {
				MPConstraint c7 = solver.makeConstraint(0, repos);
				for (int k = 0; k < nb_nuits; k++) {
					if (k % nbrTrancheParJour == 0)
						c7.setCoefficient(matrice[i][nbrTrancheParJour * k + j
								+ 2], -1);
					else
						c7.setCoefficient(matrice[i][nbrTrancheParJour * k + j
								+ 2], 1);
				}
			}
		}

		// C8 Chaque infirmière doit faire une nuit au minimum
		for (int i = 0; i < nbr_inf; i++) {
			MPConstraint c10 = solver.makeConstraint(0, 28);
			for (int j = 0; j < num_days / nbrTrancheParJour; j++) {
				c10.setCoefficient(matrice[i][nbrTrancheParJour * j + 2], 1);
			}
		}

		// C9 8 jours de congé
		for (int i = 0; i < nbr_inf; i++) {
			MPConstraint c11 = solver.makeConstraint(0, jc * nbrTrancheParJour);
			for (int j = 0; j < num_days; j++) {
				c11.setCoefficient(matrice[i][j], 1);
			}
		}

		// C10 Jours Travail Limite travail cons. 6 jours de max pour taux
		// 100%
		jtl = 6;
		for (int i = 0; i < (num_days - jtl) / nbrTrancheParJour; i++) {
			for (int j = 0; j < nbr_inf; j++) {
				MPConstraint c12 = solver.makeConstraint(0, jtl);
				for (int k = 0; k < jtl; k++) {
					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					c12.setCoefficient(matrice[j][i + k * nbrTrancheParJour], 1);
				}

			}
		}

		// à revoir
		int num_semaine = 21; // 7 jours 3 tranches
		// C11tout personnel doit faire au moins un weekend de congÃ©
		for (int i = 0; i < nbr_inf; i++) {
			MPConstraint c13 = solver.makeConstraint(0, 6);
			for (int j = 0; j < num_days / num_semaine; j++) {
				for (int k = num_semaine - (nbrTrancheParJour * 2); k < num_semaine; k++) {
					// on parcours les 2 jours du week-end (samedi, dimanche)
					c13.setCoefficient(matrice[i][j * num_semaine + k], 1);
				}
			}
		}

		// --------------------------------------------------------------------
		// --------------------------------------------------------------------
		// --------------------------------------------------------------------
		// Solve

		final MPSolver.ResultStatus resultStatus = solver.solve();

		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
			System.err
					.println("The problem does not have an optimal solution!");
			return;
		} else {

			for (int i = 0; i < nbr_inf; i++) {
				for (int j = 0; j < num_days; j++) {
					if (j % 21 == 0)
						System.out.print(" | ");
					if (j % 3 == 0)
						System.out.print(" ");
					System.out.print(matrice[i][j].solutionValue() + " ");
				}
				System.out.println();
			}

			System.out
					.println("Number of variables = " + solver.numVariables());
			System.out.println("Number of constraints = "
					+ solver.numConstraints());

			System.out.println("Problem solved in " + solver.wallTime()
					+ " milliseconds");

			// The objective value of the solution.
			System.out.println("Optimal objective value = "
					+ solver.objective().value());

		}
	}

}