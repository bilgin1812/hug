/*
 * Solver Linear by Google or tools
 *  4 levels of qualifications
 *  13 constaints
 *  3 shifts per Day
 *  Scheduling for 1 month = 28 days and 84 shifts
 *  if you are working with linux  you have to change absolute path of the library jniortools
*/
package solve;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ressource.ConstraintPerShift;
import ressource.Contstraint;
import ressource.Nurse;

import com.google.ortools.linearsolver.*;

public class NurseSolverLinear implements SolverInterface {
	public ArrayList<Nurse> listNurses;
	public Contstraint constraints;
	public ArrayList<ConstraintPerShift> listConstraintsPerShift;
	
	public static final boolean DEBUGc2=false;
	static {
		// Windows
		 System.loadLibrary("jniortools");
		// Linux Expecting an absolute path of the library: libjniortools.so
		 /****** change the the path after downloading this project *********/
		System.load("/home/bilginh/Bureau/projet_sem/workspace/HUG/libjniortools.so");

	}

	public NurseSolverLinear(ArrayList<Nurse> listNurses, Contstraint constraints,
			ArrayList<ConstraintPerShift> listConstraintsPerShift) {
		this.listNurses = listNurses;
		this.constraints = constraints;
		this.listConstraintsPerShift = listConstraintsPerShift;

	}
	/**
	 * add constraints for solver generic
	 */
	public void  addConstraint()
	{
		//TO DO
	}
	/**
	 * When {@paramref solverType} is null, throws IllegalArgumentException
	 *
	 * @param solver algorithm
	 * return MPSolver 
	 */
	private static MPSolver createSolver(String solverType) {
		try {
			return new MPSolver("NurseSolver",
					MPSolver.OptimizationProblemType.valueOf(solverType));
		} catch (java.lang.IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * exports model in a file as string
	 *
	 * @param model
	 */
	public void exportModelinFile(String model) {

		File f = new File("modelSolver");

		try {
			FileWriter fw = new FileWriter(f);

			fw.write(model);
			fw.write("\r\n");

			fw.close();
		} catch (IOException exception) {
			System.out.println("Error  : " + exception.getMessage());
		}
	}

	/**
	 * When {@paramref solverType} is null, throws IllegalArgumentException
	 *
	 * @param solver algorithm
	 * @param exportModel	 true if you want to export model in a file
	 * @param DEBUG	 mode DEBUG	
	 *  
	 */
	@Override
	public void solve(String solverType,Boolean DEBUG, Boolean exportModel) {

		MPSolver solver = createSolver(solverType);
		// infinity value used for constaints 
		double infinity = MPSolver.infinity();

		// Time limit in milliseconds (0 = no limit).
		solver.setTimeLimit(100000);

		
		/** constraints data  **/
		// 6 days of consecutivWork
		int maxConsecutiveWork= this.constraints.maxConsecutiveWork; 
	    //  8 days holiday per month
		int minWeeklyHoliday = this.constraints.minWeeklyHoliday;
		//6 series of night for 100%
		int nbrNightsSeries100 = this.constraints.nightSeriesRate100 ;  
		//nigth series =5
		int nbrNightsSeries80 =this.constraints.nightSeriesRate80;
		// scheduling  for one month 
		int nbrDaysPerMonth = 28; 
		/* 3 shifts per Day
		 *Shift 1 = morning
		 *Shift 2 = afternoon
		 *Shift 3 = night
		 */
		// every week has 21 shifts
		int shiftsOneWeek = 21; // 
		int nbrshiftsPerDay = 3;
		int nbrNurses = this.listNurses.size();
		int totalShiftsPerMonth = nbrDaysPerMonth * nbrshiftsPerDay;
		int[] nbrInfPerDay = new int[totalShiftsPerMonth];
		
		int nbrNurseperDay= (int)(nbrNurses*0.12);//6 ;// (int) (Math.random() * (5)+1);
			System.out.println(" Scheduling "+this.listNurses.size()+" nurses:");
		for (int i = 0; i < totalShiftsPerMonth; i++) {
			nbrInfPerDay[i] = nbrNurseperDay;
			if (i == 0)
				System.out.print("   ");

			if (i % 3 == 0)
				System.out.print(" ");
			if (i % 21 == 0 && i != 0)
				System.out.print("   ");
			System.out.print(nbrInfPerDay[i] + " ");

		}
		System.out.println();

		MPVariable[][] matrice = new MPVariable[nbrNurses][totalShiftsPerMonth];

		MPObjective obj = solver.objective();
		// application fo preferences on the matrix for starting
		for (int i = 0; i < nbrNurses; i++)
			for (int j = 0; j < totalShiftsPerMonth; j++) {

				// 1 = works, 0 = doesn't work
				// creation the matrix init
				matrice[i][j] = solver.makeIntVar(0, 1, "m[" + i + "," + j
						+ "]");

				
				// 5 = i want to work,... 1 = i doesn't wan tot work, 0 =not pref.
				if (Integer.valueOf(listNurses.get(i).preferences.elementAt(j)
						.toString()) != -1) {
					obj.setCoefficient(matrice[i][j], Integer.valueOf(listNurses
							.get(i).preferences.elementAt(j).toString()));
				} else {
					// -1 = don't work (holiday)
					// C4 employee can't work at holidays 
					obj.setCoefficient(matrice[i][j], -infinity);
					// with -infinty we are sure that employee doesn't work
				}
				
			}
		// maximizing preferences of nurses
		obj.setMaximization();
		
/**********************CONSTRAINTS *********************************************/

		//C1  minimum number of nurse per shift
		for (int i = 0; i < totalShiftsPerMonth; i++) {
			MPConstraint c1 = solver.makeConstraint(nbrInfPerDay[i], infinity,"c1"+i); 																																				
			for (int j = 0; j < nbrNurses; j++) {
				c1.setCoefficient(matrice[j][i], 1);
			}
		}

		// C2 :Every nurse can have one shift per day  
		for (int j = 0; j < nbrNurses; j++) {
			for (int i = 0; i < totalShiftsPerMonth; i += nbrshiftsPerDay) {
				MPConstraint c2 = solver.makeConstraint(0, 1);
				
				for (int k = 0; k < nbrshiftsPerDay; k++) {
					c2.setCoefficient(matrice[j][i + k], 1);
					if(DEBUGc2)
						if(j==0)System.out.println("t----"+(i+k));
					
				}
			}
		}	
		
		// C3 Nbr skills per Shift
		String[] comp={"formateur", "novice", "debutant", "chef"};
		for (int i = 0; i < totalShiftsPerMonth; i++) {
			for (int k = 0; k < 4; k++) {

			MPConstraint c3 = solver.makeConstraint(
			listConstraintsPerShift.get(k).numberMinSkills.get(comp[k]),infinity,"c3"+i+k);
				// contrainte skills
				for (int j = 0; j < nbrNurses; j++) {
					if (listNurses.get(j).skills.get(comp[k]) != 0)
						c3.setCoefficient(matrice[j][i], 1);
				}
			}
		}
		//C4 applied in the matrix init, nurse ca'nt work in holidays
		
		//C5
		for (int l = 0; l < nbrNurses; l++)
			// C5 series of night 3-5 for nurse 100%
			if (listNurses.get(l).activiyRate == 100) { 
				for (int j = 0; j < totalShiftsPerMonth
						- (nbrshiftsPerDay * nbrNightsSeries100); j++) {
					MPConstraint c5 = solver.makeConstraint(0, nbrNightsSeries100-1);
					for (int k = 0; k < nbrNightsSeries100; k++) {
						c5.setCoefficient(matrice[l][nbrshiftsPerDay * k + j+ 2], 1);
					}
				}

			} else if (listNurses.get(l).activiyRate == 80){ // C6 night series  2 to 4 nights / 4 weeks. // 80% int
				
				for (int j = 0; j < totalShiftsPerMonth
						- (nbrshiftsPerDay * nbrNightsSeries80); j++) {
					MPConstraint c6 = solver.makeConstraint(0, nbrNightsSeries80-1);
					for (int k = 0; k < nbrNightsSeries80; k++) {
						c6.setCoefficient(matrice[l][nbrshiftsPerDay * k + j+ 2], 1);
					}
				}
			}

		// C7 2 holidays after of one series of night
		
		for (int i = 0; i < nbrNurses; i++) {
			
			if (listNurses.get(i).activiyRate == 100) { 
			
			for (int j = 0; j < totalShiftsPerMonth- (nbrshiftsPerDay * (nbrNightsSeries100+1)); j++) {
				MPConstraint c7 = solver.makeConstraint(-1, nbrNightsSeries100);
				for (int k = 0; k < nbrNightsSeries100+1; k++) {
					if (k== nbrNightsSeries100-1)
						c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j+ 2], -1);
					else
						c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j	+ 2], 1);
					}
				}
		  }
			else if (listNurses.get(i).activiyRate == 80)
			{
				for (int j = 0; j < totalShiftsPerMonth- (nbrshiftsPerDay * (nbrNightsSeries80+1)); j++) {
					MPConstraint c7 = solver.makeConstraint(-1, nbrNightsSeries80);
					for (int k = 0; k < nbrNightsSeries80+1; k++) {
						if (k== nbrNightsSeries80-1)
							c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j+ 2], -1);
						else
							c7.setCoefficient(matrice[i][nbrshiftsPerDay * k + j	+ 2], 1);
						}
					}
			}
		}

		// C8 every nurse have to make at leat one night
		for (int i = 0; i < nbrNurses; i++) {
			MPConstraint c8 = solver.makeConstraint(0, 28,"c8"+i);
			for (int j = 0; j < totalShiftsPerMonth / nbrshiftsPerDay; j = j
					+ nbrshiftsPerDay) {

				c8.setCoefficient(matrice[i][j + 2], 1);
			}
		}

		// C9 8 holidays
		for (int i = 0; i < nbrNurses; i++) {
			MPConstraint c9 = solver.makeConstraint(0, totalShiftsPerMonth-(minWeeklyHoliday * nbrshiftsPerDay),"c9"+i);//60
			for (int j = 0; j < totalShiftsPerMonth; j++) {
				 c9.setCoefficient(matrice[i][j], 1);
			}
		}

		// C10 a nurse can work maximum 6 days consecutif
		maxConsecutiveWork = this.constraints.maxConsecutiveWork;// 6;
		for (int i = 0; i < (totalShiftsPerMonth - maxConsecutiveWork) / nbrshiftsPerDay; i++) {
			for (int j = 0; j < nbrNurses; j++) {
				MPConstraint c10 = solver.makeConstraint(0, maxConsecutiveWork);
				for (int k = 0; k < maxConsecutiveWork; k++) {

					// System.out.println("i:"+i+"j:"+j+"k:"+k);
					c10.setCoefficient(matrice[j][i + k * nbrshiftsPerDay], 1);
				}

			}
		}
			
		//C13 number of minumum weekend holidays per Nurse
		MPVariable[] P = solver.makeIntVarArray(4 * nbrNurses, 0, 1); //
		// everyone hase at leat one weekend holiday
		for (int i = 0; i < nbrNurses; i++) {					
			MPConstraint c13 = solver.makeConstraint(1,3);
			// totalShiftsPerMonth / shiftsOneWeek = 4 weeks
			for (int j = 0; j < totalShiftsPerMonth / shiftsOneWeek; j++) { 									
				P[i*4+j].setInteger(true);
				MPConstraint c11 = solver.makeConstraint(-infinity,0 ,"c11"+i+j);
				MPConstraint c12 = solver.makeConstraint(0, infinity,"c12"+i+j);
				// BUG : c11.setCoefficient(P[i * 4 + j], -2); doesn't work with 2
				c11.setCoefficient(P[i * 4 + j], -1);
				c11.setCoefficient(P[i * 4 + j], -1);
				c12.setCoefficient(P[i * 4 + j], -1);

				// Weekend shifts
				for (int q = shiftsOneWeek - (nbrshiftsPerDay * 2); q < shiftsOneWeek; q++) { 
					c11.setCoefficient(matrice[i][j * shiftsOneWeek + q], 1);
					c12.setCoefficient(matrice[i][j * shiftsOneWeek + q], 1);
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
		}
		
	    // Verify that the solution satisfies all constraints (when using solvers
	    // others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
		else  if (!solver.verifySolution(/*tolerance=*/1e-7, /*logErrors=*/true)) {
	      System.err.println("The solution returned by the solver violated the"
	                         + " problem constraints by at least 1e-7");
	      return;
	    }

		
		 else {

			if (resultStatus != MPSolver.ResultStatus.OPTIMAL)
				System.err
						.println("!!!!!!!!!The problem does not have a solution!");

			for (int i = 0; i < nbrNurses; i++) {
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
			
			System.out.println("Number of nurses      = " + this.listNurses.size());
			System.out.println("Nurses per Day        = " + nbrNurseperDay);
			System.out.println("Number of variables   = " + solver.numVariables());
			System.out.println("Number of constraints = "+ solver.numConstraints());
			System.out.println("Number of iterations  = " + solver.iterations());
			System.out.println("Problem solved in " + solver.wallTime()
					+ " milliseconds");


			// The objective value of the solution.
			System.out.println("Optimal objective value = "
					+ solver.objective().value());
		/* we can export the model such as variables end constraints in a file */
		if(exportModel)
			this.exportModelinFile(solver.exportModelAsLpFormat(true));
		if(DEBUG){
			/************** test c2 ********/
			boolean testC2 = true;
			for (int i = 0; i < totalShiftsPerMonth; i += nbrshiftsPerDay) {
				for (int j = 0; j < nbrNurses; j++) {

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
			for (int j = 0; j < nbrNurses; j++) {
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
			for (int i = 0; i < nbrNurses; i++) {
				boolean b = false;
				// number of weeks = totalShifts/ shiftsOneWeek
				int countHildayWeekEnd = 0;
				for (int j = 0; j < totalShiftsPerMonth / shiftsOneWeek; j++) {
					double a = 0;
					for (int k = shiftsOneWeek - (nbrshiftsPerDay * 2); k < shiftsOneWeek; k++) {
						// test satuday and sunday
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
			
			if(DEBUG){
				System.out.println("C2: " + testC2 + " C9: " + testC9 + " C13: "+ testC13);	
			/*	
			 * We can consult a variable or constraint
			 * System.out.println(P[144].solutionValue()+"_"+P[145].solutionValue()+"_"+P[146].solutionValue()+"_"+P[147].solutionValue()+"_");
			 *System.out.println("c13 "+solver.lookupConstraintOrNull("c1336").getCoefficient(matrice[36][37]));
		  */
			}
		} // fin if DEBUG
			System.out.println(" Result of Solver :  " + resultStatus);
		}
		/**********************************/

	}

}