package main;

import gestionData.*;
import ressource.*;
import solve.*;
import java.util.ArrayList;
import org.json.simple.parser.ParseException;

public class Main {

	public SolverInterface itsSolverInterface;
	public ReadJson itsReadJson;
	public Generate_Data itsGenerate_Data;
	public static int durePlanning = 28;

	public static void main(String[] args) throws ParseException {
		int nbrTotalShifts = 28 * 3;
		int nbrNurses = 1000;

		String fileNurses = "Nurses.json";
		String fileConstraints = "Contsraints.json";
		String fileShifts = "Constraints_per_Shift.json";

		/************** Creation of random data end contraints ***********************************/

		Contstraint contraintes = new Contstraint();
		contraintes.minWeeklyHoliday = 8;    // c9
		contraintes.numberMinNight = 1;      // c8 
		contraintes.maxConsecutiveWork = 6;  // c10
		contraintes.nbMinWeekendHoliday = 1; // c13 
		contraintes.nightSeriesRate80 = 6;   // c6 rate80
		contraintes.nightSeriesRate100 = 7;  // c5 rate 100
		contraintes.nbrShiftPerDay = 1;      //c2

		ArrayList<Nurse> randomNursesList = Generate_Data.CeateInfList(
				nbrNurses, nbrTotalShifts);
		ArrayList<ConstraintPerShift> randomConstaintsList = Generate_Data
				.CreateShitfsList(nbrNurses, nbrTotalShifts);

		/************** Create  files json ***********************************************************/

		CreateJson.getJSON_infirmier(randomNursesList, fileNurses);
		CreateJson.getJSON_Contraintes(contraintes, fileConstraints);
		CreateJson
				.getJSONConstraintsPerShift(randomConstaintsList, fileShifts);

		/************** Read files json ****************************************************************/

		ReadJson.readJsonNurses(fileNurses); 
																		
		Contstraint contraintes1 = ReadJson
				.readJsonContraintes(fileConstraints);
		ArrayList<ConstraintPerShift> list_contrainte_tranche = ReadJson
				.readJsonTranches(fileShifts);

		/**************************************** SOLVER *********************************************************/



		NurseSolverLinear mySolver = new NurseSolverLinear(randomNursesList,
				contraintes1, list_contrainte_tranche);

		//ShowList.showNursesList(randomNursesList);

		mySolver.solve("GLOP_LINEAR_PROGRAMMING",false,false); // <-- recommended (done by google)
		// mySolver.solve(1, 2, "GLPK_LINEAR_PROGRAMMING");
		// mySolver.solve(1, 2, "CLP_LINEAR_PROGRAMMING");

	}

}
