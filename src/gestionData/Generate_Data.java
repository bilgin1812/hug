package gestionData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import ressource.ConstraintPerShift;
import ressource.Nurse;

public class Generate_Data {

	
	/* Create  liste infirmiers   aléatoire*/	
	@SuppressWarnings("unchecked")
	static public ArrayList<ConstraintPerShift>  CeateTrancheList(int nbrInf, int nbrTranches){
		int nbrCompetences=4;
		//public int type_tranche;  // 1: matin 2: 15h 3 :nuit
	//	public int nombre_min_inf;
	//	public Map<String,Integer> nomb_min_comp ;
		ArrayList<ConstraintPerShift> listTranches = new ArrayList<ConstraintPerShift>();
		Random rn = new Random(10);
			
		int i;
		for( i=0 ; i<nbrTranches ; i++){
			ConstraintPerShift tranche= new ConstraintPerShift(i%3); // i%3 donne 0,1,2 donc type de tranche
			tranche.nombre_min_inf= 2;//rn.nextInt()*10;
			if(tranche.nombre_min_inf <=1)
				tranche.nombre_min_inf++;
	
			String[] cle= {"formateur", "novice", "debutant", "chef"};
			for(int k=0 ;k<nbrCompetences ; k++){
				if(rn.nextBoolean())
					tranche.nomb_min_comp.put(cle[k], 0);
				else
					tranche.nomb_min_comp.put(cle[k], 0);
			}
			listTranches.add(tranche);
		}
			
		return listTranches;
		}
	
	/* Create  liste de tranches    aléatoire*/	
	@SuppressWarnings("unchecked")
	static public ArrayList<Nurse>  CeateInfList (int nbrInf, int nbrTranches){
		int nbrCompetences=4;

		ArrayList<Nurse> listInf = new ArrayList<Nurse>();
		
		int i;
		for( i=0 ; i<nbrInf ; i++){
			Nurse inf= new Nurse(i);
			if(i%4 ==0)
				inf.taux_active =80;
			inf.taux_active =100;
			/*********
			Contraintes :
				-1 = ne pas travailler (vacances)
				0 = c'est égal
				1 = j'ai pas trop envie de travailler
				5 = j'ai très envie de travailler
			*****************/
			
			Random rn=  new Random();
			int p= rn.nextInt(nbrTranches);
			//System.out.println("P:"+p);
			for(int k=0 ;k<nbrTranches ; k++){
				if(k ==p)
					inf.preferences.add(0);
		
				else inf.preferences.add(0);
			}
			String[] cle= {"formateur", "novice", "debutant", "chef"};
			for(int k=0 ;k<nbrCompetences ; k++){
				inf.competences.put(cle[k], 1);
			}
			
			listInf.add(inf);
		}
			System.out.println(+listInf.size()+" infirmiers crés");
		return listInf;
		}
	 
}
