package gestionData;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import ressource.ConstraintPerShift;
import ressource.Nurse;

public class Generate_Data {

	
	/* Create  liste infirmiers   aléatoire*/	
	@SuppressWarnings("unchecked")
	static public ArrayList<ConstraintPerShift>  CreateShitfsList(int nbrInf, int nbrTranches){
		int nbrCompetences=4;
		//public int type_tranche;  // 1: matin 2: 15h 3 :nuit
	//	public int nombre_min_inf;
	//	public Map<String,Integer> nomb_min_comp ;
		ArrayList<ConstraintPerShift> listTranches = new ArrayList<ConstraintPerShift>();
		Random rn = new Random(10);
			
		int i;
		for( i=0 ; i<nbrTranches ; i++){
			ConstraintPerShift tranche= new ConstraintPerShift(); // i%3 donne 0,1,2 donc type de tranche
			tranche.numberMinSkillsSum= 5;//rn.nextInt()*10;
			if(tranche.numberMinSkillsSum <=1)
				tranche.numberMinSkillsSum++;
	
			String[] cle= {"formateur", "novice", "debutant", "chef"};
			for(int k=0 ;k<nbrCompetences ; k++){
				if(rn.nextBoolean())
					tranche.numberMinSkills.put(cle[k], 0);
				else
					tranche.numberMinSkills.put(cle[k], 0);
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
				inf.activiyRate =80;
			inf.activiyRate =100;
			/*********
			Contraintes :
				-1 = holiday
				0 = without pre.
				1 = i don't want to work
				5 = i really want to work
			*****************/
			
			Random rn=  new Random();
			// we can limit number prefs.
			//int p= 20 ;//rn.nextInt(nbrTranches);
			//System.out.println("P:"+p);
			for(int k=0 ;k<nbrTranches ; k++){
				/*p--;
				if(p>0)
					inf.preferences.add(rn.nextInt(4));*/
				if(k==i+1)
					inf.preferences.add(1);
				else if(k==i+2)
					inf.preferences.add(2);
				else if(k==i+3)
					inf.preferences.add(3);
				else inf.preferences.add(0);
			}
			String[] cle= {"formateur", "novice", "debutant", "chef"};
			for(int k=0 ;k<nbrCompetences ; k++){
				inf.skills.put(cle[k], 1);
			}
			
			listInf.add(inf);
		}
			
		return listInf;
		}
	 
}
