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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Random;

import ressource.ConstraintPerShift;
import ressource.Contstraint;
import ressource.Nurse;

import com.google.ortools.linearsolver.*;

public class NurseSolverLinear implements SolverInterface {
	public ArrayList<Nurse> listInf;
	public Contstraint constraints;
	public ArrayList<ConstraintPerShift> listConstraintsPerShift;
	public static final boolean DEBUG=true;
	public static final boolean DEBUGc2=false;
	static {
		// Windows
		 System.loadLibrary("jniortools");

		// Linux chemin absolu
		System.load("/home/bilginh/Bureau/projet_sem/workspace/HUG/libjniortools.so");

	}

	public NurseSolverLinear(ArrayList<Nurse> listInf, Contstraint contraintes,
			ArrayList<ConstraintPerShift> listConstraintsPerShift) {
		this.listInf = listInf;
		this.constraints = contraintes;
		this.listConstraintsPerShift = listConstraintsPerShift;

	}

	private static MPSolver createSolver(String solverType) {
		try {
			return new MPSolver("NurseSolver",
					MPSolver.OptimizationProblemType.valueOf(solverType));
		} catch (java.lang.IllegalArgumentException e) {
			return null;
		}
	}

	public void exportModelinFile(String model) {

		File f = new File("modelSolver");

		try {
			FileWriter fw = new FileWriter(f);

			fw.write(model);
			fw.write("\r\n");

			fw.close();
		} catch (IOException exception) {
			System.out.println("Error de write : " + exception.getMessage());
		}
	}

	public void solve(String solverType) {

		MPSolver solver = createSolver(solverType);
		double infinity = solver.infinity();

		// Time limit in milliseconds (0 = no limit).
		solver.setTimeLimit(100000);

	
		// constraints data  
		int jtl= this.constraints.maxConsecutiveWork; // 6 days of consecutivWork
		int jc = this.constraints.conge_hebdomaire;//  8 days holiday per month
		int nbrMaxNight = this.constraints.seri_nuits_taux100 ; //6 series of night for 100% 
		
		int nbrJours = 28; // scheduling  for one month 
		/* 3 shifts per Day
		 *Shift 1 = morning
		 *Shift 2 = afternoon
		 *Shift 3 = night
		 */
		int nbrshiftsPerDay = 3;
		int nbrInfirmier = this.listInf.size();
		int totalShiftsPerMonth = nbrJours * nbrshiftsPerDay;
		int[] nbrInfPerDay = new int[totalShiftsPerMonth];
		
		if(DEBUG)
			System.out.println(" Scheduling "+this.listInf.size()+" nurses:");
		for (int i = 0; i < totalShiftsPerMonth; i++) {
			nbrInfPerDay[i] = 5;// (int) (Math.random() * (5)+1);
			if (i == 0)
				System.out.print("   ");

			if (i % 3 == 0)
				System.out.print(" ");
			if (i % 21 == 0 && i != 0)
				System.out.print("   ");
			System.out.print(nbrInfPerDay[i] + " ");

		}
		System.out.println();

		MPVariable[][] matrice = new MPVariable[nbrInfirmier][totalShiftsPerMonth];

		MPObjective obj = solver.objective();
		// On applique les préférences des infirmières à la matrice de départ
		for (int i = 0; i < nbrInfirmier; i++)
			for (int j = 0; j < totalShiftsPerMonth; j++) {

				// 1 = travaille, 0 = travaille pas
				// on créer une matrice de qui sera composé de 1 ou 0
				matrice[i][j] = solver.makeIntVar(0, 1, "m[" + i + "," + j
						+ "]");

				// pref va du plus grand au plus petit
				// 5 = je veux vraiment travailler, 1 = je veux pas trop, 0 =
				// c'est égal
				if (Integer.valueOf(listInf.get(i).preferences.elementAt(j)
						.toString()) != -1) {
					// System.out.println("OBJJJJ"+listInf.get(i).preferences.elementAt(j)
					// .toString());
					obj.setCoefficient(matrice[i][j], Integer.valueOf(listInf
							.get(i).preferences.elementAt(j).toString()));
				} else {
					// -1 = don't work (holiday)
					// C4 employee can't work at holidays 
					obj.setCoefficient(matrice[i][j], -infinity);
					// on met une valeure très basse pour être sûr qu'il ne
					// travaillera pas
				}
			}
		obj.setMaximization();

/**********************CONSTRAINTS ********************************************************/

		//  minimum number of nurse per shift
		for (int i = 0; i < totalShiftsPerMonth; i++) {
			MPConstraint c1 = solver.makeConstraint(nbrInfPerDay[i], nbrInfPerDay[i]+20); 
																																						// deleted
			for (int j = 0; j < nbrInfirmier; j++) {
				c1.setCoefficient(matrice[j][i], 1);
			}
		}

		// C2 :Every nurse can have one shift per day  
		for (int j = 0; j < nbrInfirmier; j++) {
			for (int i = 0; i < totalShiftsPerMonth; i += nbrshiftsPerDay) {
				MPConstraint c2 = solver.makeConstraint(0, 1);
				
				for (int k = 0; k < nbrshiftsPerDay; k++) {
					c2.setCoefficient(matrice[j][i + k], 1);
					if(DEBUGc2)
						if(j==0)System.out.println("t----"+(i+k));
					
				}
			}
		}

		for (int l = 0; l < nbrInfirmier; l++)
			// C5 series of night 3-5 for nurse 100%
			if (listInf.get(l).taux_active == 100) { 
				for (int j = 0; j < totalShiftsPerMonth
						- (nbrshiftsPerDay * nbrMaxNight); j++) {
					MPConstraint c5 = solver.makeConstraint(0, 6);
					for (int k = 0; k < nbrMaxNight; k++) {
						c5.setCoefficient(matrice[l][nbrshiftsPerDay * k + j+ 2], 1);
					}
				}

			} else { // C6 Série nuits de 2 à 4 nuits / 4 sem. // 80% int
				nbrMaxNight = 5;
				for (int j = 0; j < totalShiftsPerMonth
						- (nbrshiftsPerDay * nbrMaxNight); j++) {
					MPConstraint c5 = solver.makeConstraint(0, 5);
					for (int k = 0; k < nbrMaxNight; k++) {
						c5.setCoefficient(matrice[l][nbrshiftsPerDay * k + j+ 2], 1);
					}
				}
			}

		// C7 2 congé minimum après une série de nuits
		int repos = 3;
		int nb_nuits = 5;
		for (int i = 0; i < nbrInfirmier; i++) {
			for (int j = 0; j < totalShiftsPerMonth- (nbrshiftsPerDay * nb_nuits); j++) {
				MPConstraint c7 = solver.makeConstraint(-1, 4);
				for (int k = 0; k < nb_nuits; k++) {
					if (k== 3)
						c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j+ 2], -1);
					else
						c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j	+ 2], 1);
				}
			}
		}

		// C8 Chaque infirmière doit faire une nuit au minimum
		for (int i = 0; i < nbrInfirmier; i++) {
			MPConstraint c8 = solver.makeConstraint(0, 28);
			for (int j = 0; j < totalShiftsPerMonth / nbrshiftsPerDay; j = j
					+ nbrshiftsPerDay) {

				c8.setCoefficient(matrice[i][j + 2], 1);
			}
		}

		// C9 8 jours de congé
		for (int i = 0; i < nbrInfirmier; i++) {
			MPConstraint c9 = solver.makeConstraint(0, totalShiftsPerMonth-(jc * nbrshiftsPerDay));//60
			for (int j = 0; j < totalShiftsPerMonth; j++) {
				 c9.setCoefficient(matrice[i][j], 1);
			}
		}

		// C10 Jours Travail Limite travail cons. 6 jours de max pour taux
		// 100%
		jtl = this.constraints.maxConsecutiveWork;// 6;
		for (int i = 0; i < (totalShiftsPerMonth - jtl) / nbrshiftsPerDay; i++) {
			for (int j = 0; j < nbrInfirmier; j++) {
				MPConstraint c10 = solver.makeConstraint(0, jtl);
				for (int k = 0; k < jtl; k++) {

					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					c10.setCoefficient(matrice[j][i + k * nbrshiftsPerDay], 1);
				}

			}
		}

		/************************/

		int shiftsOneWeek = 21; // 7 jours 3 tranches // C11tout

		// TO DO
		MPVariable[] P = solver.makeIntVarArray(4 * nbrInfirmier, 0, 1); //
		// everyone hase at leat one weekend holiday
		MPConstraint c13;
		MPConstraint	c14;
		MPVariable[] K = null;
		for (int i = 0; i < nbrInfirmier; i++) {
			
			if(i==36)
			{
					c14 = solver.makeConstraint(0, 3);
				K = solver.makeIntVarArray(4, 0, 1);

				for (int j = 0; j < totalShiftsPerMonth / shiftsOneWeek; j++) { 									
					
					MPConstraint c15 = solver.makeConstraint(-1, 0);
					MPConstraint c16 = solver.makeConstraint(0, 1);
					c15.setCoefficient(K[j], -2);
					c16.setCoefficient(K[j], -1);

					// les shifts of weekend
					for (int k = shiftsOneWeek - (nbrshiftsPerDay * 2); k < shiftsOneWeek; k++) { 
						c15.setCoefficient(matrice[36][j * shiftsOneWeek + k], 1);
						c16.setCoefficient(matrice[36][j * shiftsOneWeek + k], 1);
						// System.out.println("  wekend :"+(j*shiftsOneWeek+k));
					}
					c14.setCoefficient(K[j], 1);
				}
			
				
				}
			
			
			 c13 = solver.makeConstraint(0, 3,"c13"+i);
			//MPVariable[] P1 = solver.makeIntVarArray(4, 0, 1); //
			// totalShiftsPerMonth / shiftsOneWeek = 4
			for (int j = 0; j < totalShiftsPerMonth / shiftsOneWeek; j++) { 									
				P[i*4+j].setInteger(true);
				MPConstraint c11 = solver.makeConstraint(-1, 0);
				MPConstraint c12 = solver.makeConstraint(0, 1);
				c11.setCoefficient(P[i * 4 + j], -2);
				c12.setCoefficient(P[i * 4 + j], -1);

				// les shifts of weekend
				for (int k = shiftsOneWeek - (nbrshiftsPerDay * 2); k < shiftsOneWeek; k++) { 
					c11.setCoefficient(matrice[i][j * shiftsOneWeek + k], 1);
					c12.setCoefficient(matrice[i][j * shiftsOneWeek + k], 1);
					// System.out.println("  wekend :"+(j*shiftsOneWeek+k));
				}
				c13.setCoefficient(P[i*4+j], 1);
			}
		

		}

		/************  solve  *********************/


		final MPSolver.ResultStatus resultStatus = solver.solve();

		if (resultStatus == MPSolver.ResultStatus.INFEASIBLE) {
			System.err
					.println("!!!!!!!!!!!The problem does not have a solution!");
			return;

		} else {

			if (resultStatus != MPSolver.ResultStatus.OPTIMAL)
				System.err
						.println("!!!!!!!!!The problem does not have a solution!");

			for (int i = 0; i < nbrInfirmier; i++) {
				for (int j = 0; j < totalShiftsPerMonth; j++) {
					if (j == 0) {
						if (i < 10)
							System.out.print(i + " -");
						else
							System.out.print(i + "-");

					}
					if (j % 21 == 0 && j != 0)
						System.out.print(" | ");
					if (j % 3 == 0)
						System.out.print(" ");
					System.out.print((int) matrice[i][j].solutionValue() + " ");
				}
				System.out.println();
			}

			System.out
					.println("Number of variables = " + solver.numVariables());
			System.out.println("Number of constraints = "
					+ solver.numConstraints());

			System.out.println("Number of iterations = " + solver.iterations());
			System.out.println("Problem solved in " + solver.wallTime()
					+ " milliseconds");


			// The objective value of the solution.
			System.out.println("Optimal objective value = "
					+ solver.objective().value());

			/************** test c2 ********/
			boolean testC2 = true;
			for (int i = 0; i < totalShiftsPerMonth; i += nbrshiftsPerDay) {
				for (int j = 0; j < nbrInfirmier; j++) {

					int m = 0;
					for (int k = 0; k < nbrshiftsPerDay; k++) {
						// System.out.println("i:"+i+"j:"+j+"k:"+k);
						m += (matrice[j][i + k].solutionValue());
					}
					if (m > 1) {
						if(DEBUG)
							System.out.println(" Error C2 Inf - " + j+ " tranche :" + i);
						testC2 = false;
					}
					m = 0;

				}
			}

			/************** test c9 ********/
			boolean testC9 = true;
			int totalShiftdonePerInfirmier = 0;
			for (int j = 0; j < nbrInfirmier; j++) {
				for (int i = 0; i < totalShiftsPerMonth; i++) {
					totalShiftdonePerInfirmier += (matrice[j][i]
							.solutionValue());
				}
				if (totalShiftdonePerInfirmier > 60) {
					System.out.println(" Error C9 Inf - " + j);
					testC9 = false;
				}
				totalShiftdonePerInfirmier = 0;

			}

			/***************** /* test c13 */
			boolean testC13 = true;
			for (int i = 0; i < nbrInfirmier; i++) {
				boolean b = false;
				// number of weeks = totalShifts/ shiftsOneWeek
				int countHildayWeekEnd = 0;
				for (int j = 0; j < totalShiftsPerMonth / shiftsOneWeek; j++) {
					double a = 0;
					for (int k = shiftsOneWeek - (nbrshiftsPerDay * 2); k < shiftsOneWeek; k++) {
						// on parcours les 2 jours du week-end (samedi,
						// dimanche)
						a += matrice[i][j * shiftsOneWeek + k].solutionValue();

					}
					if (a == 0.0) {
						b = true;
						countHildayWeekEnd++;
					}
					a = 0.0;

				}
				if (countHildayWeekEnd < 1)
					testC13 = false;
				if(DEBUG){
				System.out.println(i + "-" + b + " nbr congé weekend : "
						+ countHildayWeekEnd);
				System.out.println(" P  :" + P[i * 4].solutionValue() + " "
						+ P[i * 4 + 1].solutionValue() + " "
						+ P[i * 4 + 2].solutionValue() + " "
						+ P[i * 4 + 3].solutionValue());
				}
			}
			this.exportModelinFile(solver.exportModelAsLpFormat(true));
			if(DEBUG){
				System.out.println("C2: " + testC2 + " C9: " + testC9 + " C13: "+ testC13);	
			/*	System.out.println(P[144].solutionValue()+"_"+P[145].solutionValue()+"_"+P[146].solutionValue()+"_"+P[147].solutionValue()+"_");
				System.out.println(K[0].solutionValue()+"_"+K[1].solutionValue()+"_"+K[2].solutionValue()+"_"+K[3].solutionValue()+"_");
				
				System.out.print("**"+matrice[36][36].solutionValue());
				System.out.print("**"+matrice[36][37].solutionValue());
				System.out.print("**"+matrice[36][38].solutionValue());
				System.out.print("**"+matrice[36][39].solutionValue());
				System.out.print("**"+matrice[36][40].solutionValue());
				System.out.print("**"+matrice[36][41].solutionValue());
				
				//System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][37]));
				//System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][38]));
				//System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][39]));
				//System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][40]));
				//System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][41]));
				 * 
				 */
			}
			System.out.println(" Result of Solver :  " + resultStatus);
		}
		/**********************************/

	}

}