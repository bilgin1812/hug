package test;

import java.util.ArrayList;
import java.util.Random;

import org.json.simple.parser.ParseException;

import user.Afficher;
import user.ReadCVS;
import user.ReadJson;
import ressource.*;
import solve.*;

public class Test {
	public static int durePlanning=28;

	public static void main(String[] args) {
		
		
/* lire fichier json pour recuperer les infirmiers */
		ReadJson obj = new ReadJson();
		ArrayList<Infirmier> listInf = null;
		try {
			listInf = obj.readJson("infirmiers.json");
			//listInf = obj.readJson("inf.json");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Tranche> planning =  new ArrayList<Tranche>();
		
		
		Afficher.AfficheInfirmiers(listInf);
		
		int n = 20; // nombre d'infirmi�res
		int num = 2; // combien d'horaires � afficher
		NurseSolver mySolver= new NurseSolver(listInf);
		mySolver.solve(n, num);
		/*
		Random rd= new Random();
		for(int i=0; i<durePlanning ; i++ )
		{
			Tranche t= new Tranche();
			//infirmier par tranche
			for(int k=0 ;k<rd.nextInt(5)+2 ; k++ )
				t.infirmiers.add(listInf.get(rd.nextInt(listInf.size())));
			planning.add(t);

			
		}

		*/

	}

}
