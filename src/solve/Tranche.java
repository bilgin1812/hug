package solve;

import ressource.*;

import java.util.*;

public class Tranche {
	public boolean  b;
	public ArrayList<Infirmier> infirmiers;
	public int cont;
	int type_tranche;
	public int nombre_min_inf;
	Map<String,Integer> nomb_min_comp;
	
	public Tranche()
	{
		Random rd = new Random();
		infirmiers= new ArrayList<Infirmier>();


		nombre_min_inf= rd.nextInt(5)+3;
		Map<String,Integer> nomb_min_comp= new HashMap<String, Integer>();
		
	}

}
