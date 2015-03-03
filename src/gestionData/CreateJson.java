package gestionData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ressource.ConstraintPerShift;
import ressource.Contstraint;
import ressource.Nurse;

public class CreateJson {

/* create json object tranches avec contraintes  */
	
	@SuppressWarnings("unchecked")
	static public void  getJSONContraintes_par_Tranche(ArrayList<ConstraintPerShift> listTranches,String filename){
		
		JSONObject obj_final=new JSONObject();
		JSONObject obj=null;
		JSONArray list = new JSONArray();
		
		for(ConstraintPerShift tranche:listTranches){
			obj = new JSONObject();
			obj.put("type_tranche", tranche.type_tranche);
			obj.put("nombre_min_inf", tranche.nombre_min_inf);
			
			 JSONObject cmpt = new JSONObject();
			for(Entry<String, Integer> entry : tranche.nomb_min_comp.entrySet()) 
				    cmpt.put(entry.getKey(),entry.getValue());

			 obj.put("nomb_min_comp", cmpt);
			 list.add(obj);
		}
		obj_final.put("Contraintes_par_tranche", list);
		try {
	 
			FileWriter file = new FileWriter(filename);
			file.write(obj_final.toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		}
	
/* create json object contrainte  */
	
	@SuppressWarnings("unchecked")
	static public void  getJSON_Contraintes(Contstraint cont,String filename){
		
		JSONObject obj_final=new JSONObject();
		JSONObject obj=null;
		
			obj = new JSONObject();
			obj.put("conge_hebdomaire", cont.conge_hebdomaire);
			obj.put("toutes_inf_nuit", cont.toutes_inf_nuit);
			obj.put("travail_cons_6jours", cont.maxConsecutiveWork);
			obj.put("weekend_conge_min", cont.nbMinWeekendHoliday);
			obj.put("seri_nuits_taux80", cont.seri_nuits_taux80);
			obj.put("seri_nuits_taux100", cont.seri_nuits_taux100);

		obj_final.put("contraintes",obj);
		
		try {
	 
			FileWriter file = new FileWriter(filename);
			file.write(obj_final.toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		}
		

	

	
/* create fichier json infirmiers */

static public void getJSON_infirmier(ArrayList<Nurse> listInf,String filename)
{
	JSONObject obj_final=new JSONObject();
	JSONObject obj=null;
	JSONArray listJsonInfirmier = new JSONArray();
	
	for(Nurse inf : listInf){
		obj = new JSONObject();
		obj.put("id", inf.id);
		obj.put("taux", inf.taux_active);
	 
		JSONArray list = new JSONArray();
		for(int i=0 ; i<inf.preferences.size() ; i++)
			list.add(inf.preferences.get(i));
		obj.put("preferences", list);
		
		 JSONObject cmpt = new JSONObject();
		for(Entry<String, Integer> entry : inf.competences.entrySet()) 
			    cmpt.put(entry.getKey(),entry.getValue());

		 obj.put("competences", cmpt);
		 listJsonInfirmier.add(obj);
	}
	obj_final.put("List_infirmiers", listJsonInfirmier);
	try {
 
		FileWriter file = new FileWriter(filename);
		file.write(obj_final.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
}


}
