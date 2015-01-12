package user;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ressource.Infirmier;
import solve.Tranche;

public class Afficher {
	
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";

	public static int nb_inf=4;
	
	public static void AfficheInfirmiers(ArrayList<Infirmier> list)
	{
		System.out.println(list.size()+" infirmiers");
		for(Infirmier i:list)
			System.out.println("inf :"+i.id+" taux: "+i.taux_active+" compt:"+i.competences.toString()+" pref. :"+i.preferences.toString());

	}
	 public static void Affiche_Tranches(	ArrayList<Tranche> planning ,ArrayList<Infirmier> listInf) {
		for(Infirmier i:listInf)
		 System.out.println(i.getjson());
		 
			String[][] str = new String[20][planning.size()];
			for (int i = 0; i < 20; i++)
				for (int j = 0; j < planning.size(); j++)
					str[i][j] = "";

			for (int i = 0; i < planning.size(); i++) {
				//entetes
				if (i == 0) 
				{
					for (int j = 0; j < planning.size(); j++)
						str[0][j] = "tranche " + (j + 1);
				}
				//affiche infirmiers par tranche
				for (int j = 0; j < planning.get(i).infirmiers.size()  ; j++)
					str[j + 1][i] = String.valueOf("inf"+planning.get(i).infirmiers.get(j).id);

			}
			// affichage du nombre de personnes dans la tranche
			for (int i = 0; i < planning.size(); i++) {
				str[nb_inf][i] ="Manque inf:" +Integer.toString(planning.get(i).nombre_min_inf-planning.get(i).infirmiers.size()  );
						
			}

			String s = "";
			for (int i = 0; i < nb_inf + 1; i++) {
				s = "| ";
				for (int j = 0; j < planning.size(); j++) {
					String tmp;
					tmp = str[i][j];
					int l;
					if (j < 9)
						l = 9 - tmp.length();
					else
						l = 10 - tmp.length();
					for (int k = 0; k < l; k++)
						tmp += " ";
					
					String ts = tmp.trim();
					//if (i == nb_inf && Integer.parseInt( ts ) > 0) tmp = ANSI_RED + tmp + ANSI_RESET;
					//if (i == nb_inf && Integer.parseInt( ts ) <= 0) tmp = ANSI_GREEN + tmp + ANSI_RESET;
					s += tmp + " | ";
				}
				System.out.println(s);
				if (i == 0 || i == nb_inf - 1)
					System.out
							.println("|---------------------------------------------------------------------------------------------"
									+ "----------------------------------------------------------------------------------------------"
									+ "----------------------------------------------------------------------------------------------"
									+ "-------------------------------------------------------------------------|");
			}
			System.out.println("Contrainte travail cons. 6 jours");
			for(Infirmier i:listInf)
				System.out.println(i.id+"->"+i.travail_cons_6jours);
		}

}
