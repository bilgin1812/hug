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
	static public void  getJSONConstraintsPerShift(ArrayList<ConstraintPerShift> listShifts,String filename){
		
		JSONObject obj_final=new JSONObject();
		JSONObject obj=null;
		JSONArray list = new JSONArray();
		
		for(ConstraintPerShift tranche:listShifts){
			obj = new JSONObject();
			obj.put("numberMinSkillsSum", tranche.numberMinSkillsSum);
			
			 JSONObject cmpt = new JSONObject();
			for(Entry<String, Integer> entry : tranche.numberMinSkills.entrySet()) 
				    cmpt.put(entry.getKey(),entry.getValue());

			 obj.put("numberMinSkills", cmpt);
			 list.add(obj);
		}
		obj_final.put("ConstraintsPerShift", list);
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
			obj.put("minWeeklyHoliday", cont.minWeeklyHoliday);
			obj.put("numberMinNight", cont.numberMinNight);
			obj.put("maxConsecutiveWork", cont.maxConsecutiveWork);
			obj.put("nbMinWeekendHoliday", cont.nbMinWeekendHoliday);
			obj.put("nightSeriesRate80", cont.nightSeriesRate80);
			obj.put("nightSeriesRate100", cont.nightSeriesRate100);

		obj_final.put("constraints",obj);
		
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
		obj.put("activiyRate", inf.activiyRate);
	 
		JSONArray list = new JSONArray();
		for(int i=0 ; i<inf.preferences.size() ; i++)
			list.add(inf.preferences.get(i));
		obj.put("preferences", list);
		
		 JSONObject cmpt = new JSONObject();
		for(Entry<String, Integer> entry : inf.skills.entrySet()) 
			    cmpt.put(entry.getKey(),entry.getValue());

		 obj.put("skills", cmpt);
		 listJsonInfirmier.add(obj);
	}
	obj_final.put("List_Nurses", listJsonInfirmier);
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
