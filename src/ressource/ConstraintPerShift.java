package ressource;


import java.util.*;

public class ConstraintPerShift {
	
	public int type_tranche;  // 1: matin 2: 15h 3 :nuit
	public int nombre_min_inf;
	public Map<String,Integer> nomb_min_comp ;
	
	public ConstraintPerShift(int type_tranche)
	{
		this.nomb_min_comp = new HashMap<String, Integer>();
		this.type_tranche =  type_tranche;
	}

}
