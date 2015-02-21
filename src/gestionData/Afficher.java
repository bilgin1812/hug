package gestionData;

import java.util.ArrayList;
import ressource.*;

public class Afficher {
	

	
	public static void AfficheInfirmiers(ArrayList<Nurse> list)
	{
		System.out.println(list.size()+" infirmiers");
		for(Nurse i:list)
			System.out.println("inf :"+i.id+" taux: "+i.taux_active+" compt:"+i.competences.toString()+" pref. :"+i.preferences.toString());

	}
	 public static void Affiche_Tranches(	ArrayList<ConstraintPerShift> planning ,ArrayList<Nurse> listInf) {
		//for(Infirmier i:listInf);
		
		
		}

}
