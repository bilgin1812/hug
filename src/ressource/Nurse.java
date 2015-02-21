package ressource;

import java.awt.Point;
import java.util.HashMap;
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

public class Nurse {
	public int id;
	public int taux_active;
	public Vector preferences;
	public Map <String,Integer>competences;
	

	
	public Nurse(int id,int taux,Map comp,Vector pref)
	{
		this.id=id;
		this.taux_active=taux;
		this.competences=comp;
		this.preferences=pref;

		
		
	}
	public Nurse(int id)
	{
		this.id=id;
		this.taux_active=100;
		this.preferences=new Vector();
		this.competences =new  HashMap<String, Integer>();
		
	}
	
}
