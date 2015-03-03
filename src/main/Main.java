package main;

import gestionData.Afficher;
import gestionData.CreateJson;
import gestionData.Generate_Data;
import gestionData.ReadCVS;
import gestionData.ReadJson;

import java.util.ArrayList;
import java.util.Random;

import org.json.simple.parser.ParseException;

import ressource.*;
import solve.*;
import gestionData.Generate_Data;
import gestionData.CreateJson;
import gestionData.ReadJson;
import solve.SolverInterface;

public class Main {
	public SolverInterface itsSolverInterface;
	public ReadJson itsReadJson;
	private CreateJson itsCreateJson;
	public Generate_Data itsGenerate_Data;
	public static int durePlanning = 28;

	public static void main(String[] args) throws ParseException {
		int nbr_tranches = 28 * 3;
		int nbr_inf =50; // nombre d'infirmieres
		int num = 2; // combien d'horaires a afficher
		String filename_infirmier = "inf.json";
		String filename_contraintes = "Contraintes.json";
		String filename_tranches = "Contraintes_par_tranches";

		/**************  génération de données alétaoires  et contraintes en dure***********************************/

		Contstraint contraintes = new Contstraint();
		contraintes.conge_hebdomaire = 8; // c11 8 congé hebdomaire sur 4 semaines
		contraintes.toutes_inf_nuit = 1; // c10 tous les inf au moins une nuit
		contraintes.maxConsecutiveWork = 6; // c12 pas de travail cons plus que 6 jours 
		contraintes.nbMinWeekendHoliday= 1; // c13 au moins 1 weekend congé
		contraintes.seri_nuits_taux80 = 5; // c6 pour taux 80
		contraintes.seri_nuits_taux100 = 6; // c5 pour taux 100
		contraintes.nbrShiftPerDay = 1; // nombre tranche par jour par infirmier

		ArrayList<Nurse> listInf_Aletoire = Generate_Data.CeateInfList(
				nbr_inf, nbr_tranches);
		ArrayList<ConstraintPerShift> listCont_Aletoire = Generate_Data
				.CreateShitfsList(nbr_inf, nbr_tranches);

		/************** générations des fichiers json ***********************************************************/

		CreateJson.getJSON_infirmier(listInf_Aletoire, filename_infirmier);
		CreateJson.getJSON_Contraintes(contraintes, filename_contraintes);
		CreateJson.getJSONContraintes_par_Tranche(listCont_Aletoire,
				filename_tranches);

		/************** lire les fichiers json ****************************************************************/
		/* lire fichier json pour recuperer les infirmiers */

		ArrayList<Nurse> listInf = ReadJson.readJsonInf("inf.json");  //"infirmiersGeneres.json" pref mixtes
		Contstraint contraintes1 = ReadJson
				.readJsonContraintes(filename_contraintes);
		ArrayList<ConstraintPerShift> list_contrainte_tranche = ReadJson
				.readJsonTranches(filename_tranches);

		/****************************************SOLVER *********************************************************/

		//Afficher.AfficheInfirmiers(listInf_Aletoire);
		//solver par contrainte
		//NurseSolver_par_contrainte mySolver1= new NurseSolver_par_contrainte(listInf_Aletoire);
		//mySolver.solve(n, num);$

		/* SOLVER LINEAR */
	//	NurseSolverLinear mySolver = new NurseSolverLinear(listInf_Aletoire, contraintes1,list_contrainte_tranche);
		
		NurseSolverLinear mySolver = new NurseSolverLinear(listInf_Aletoire, contraintes1,list_contrainte_tranche);
		
		 
		//N solv = new N(listInf_Aletoire);
		//solv.solve( );
		
		mySolver.solve( "GLOP_LINEAR_PROGRAMMING");  //<-- recommended (done by  google)
		//mySolver.solve(1, 2, "GLPK_LINEAR_PROGRAMMING");
		//mySolver.solve(1, 2, "CLP_LINEAR_PROGRAMMING");

	}

}
