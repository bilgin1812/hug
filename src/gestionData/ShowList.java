package gestionData;

import java.util.ArrayList;
import ressource.*;

public class ShowList {
	
/*
 * @param list of nurses
 */
	public static void showNursesList(ArrayList<Nurse> list)
	{
		System.out.println(list.size()+" Nurses");
		for(Nurse i:list)
			System.out.println("inf :"+i.id+" activiy Rate: "+i.activiyRate+" compt:"+i.skills.toString()+" pref. :"+i.preferences.toString());

	}
	

}
