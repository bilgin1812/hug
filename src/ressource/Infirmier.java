package ressource;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.json.JsonSerializer;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Infirmier {
	public int id;
	public Vector tranche;
	public Vector preferences;
	public Map <String,Integer>competences;
	public int taux_active;
	public int conge_hebdomaire =0; // c11 8 congé hebdomaire sur 4 semaines
	public int toutes_inf_nuit=0; // c10 tous les inf au moins une nuit
	public int travail_cons_6jours =0 ; // c12 pas de travail cons plus que 6 jours 
	public int weekend_conge_min=0; // c13 au moins 1 weekend congé
	public int seri_nuits_taux80=0; // c6 pour taux 80
	public int seri_nuits_taux100=0; // c5 pour taux 100
	
	public Infirmier(int id,int taux,Map comp,Vector pref)
	{
		this.id=id;
		this.taux_active=taux;
		this.competences=comp;
		this.preferences=pref;

		
		
	}
	public Infirmier(int id)
	{
		this.id=id;
		this.taux_active=100;
		
	}
	public String getjson()
	
	{
		
		JSONObject obj = new JSONObject();
		obj.put("id", this.id);
		obj.put("taux", this.taux_active);
		
		JSONArray list = new JSONArray();
		list.add("1");
		list.add("2");
		for(Entry<String, ?> entry : this.competences.entrySet()) {
		   // String cle = entry.getKey();
		    //Object valeur =  entry.getValue();
	
		}

	 
		obj.put("competence", list);

	
	 
		return (obj.toJSONString());
	 		
		}
	
}
