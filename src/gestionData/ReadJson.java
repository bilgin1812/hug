package gestionData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ressource.*;

public class ReadJson {

static public Contstraint readJsonContraintes(String file) throws org.json.simple.parser.ParseException
	{
	
	 Contstraint cont = new Contstraint();
	 JSONParser parser = new JSONParser();

    try {     
        Object obj = parser.parse(new FileReader(file));

        JSONObject jsonObject =  (JSONObject) obj;

        JSONObject cont_obj = (JSONObject)jsonObject.get("contraintes");
       // System.out.println(cont_obj.toString());

        cont.conge_hebdomaire    = Integer.valueOf(  cont_obj.get("conge_hebdomaire").toString());
        cont.seri_nuits_taux100  = Integer.valueOf(  cont_obj.get("seri_nuits_taux100").toString());
        cont.seri_nuits_taux80   = Integer.valueOf(  cont_obj.get("seri_nuits_taux80").toString());
        cont.toutes_inf_nuit     = Integer.valueOf(  cont_obj.get("toutes_inf_nuit").toString());
        cont.travail_cons_6jours = Integer.valueOf(  cont_obj.get("travail_cons_6jours").toString());
        cont.weekend_conge_min   = Integer.valueOf(  cont_obj.get("weekend_conge_min").toString());

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
	return cont;
		
	}

/***** lire json liste infrimiers *********/
static public ArrayList<Nurse> readJsonInf(String file) throws org.json.simple.parser.ParseException
{
	ArrayList<Nurse> list= new ArrayList<Nurse>();
	 JSONParser parser = new JSONParser();

     try {     
         Object obj = parser.parse(new FileReader(file));

         JSONObject jsonObject =  (JSONObject) obj;

         Object infirmiers = jsonObject.get("List_infirmiers");
         Iterator<JSONObject> iterator = ((ArrayList) infirmiers).iterator();
         while (iterator.hasNext()) {
        	 Map<String,Integer> comp = new HashMap<String,Integer>();
        	 Vector pref = new Vector();;
        	 
        	 JSONObject InfirmierObj =  iterator.next();
             String id = InfirmierObj.get("id").toString();
             String taux =  InfirmierObj.get("taux").toString();
             
             JSONArray preferences1 = (JSONArray) InfirmierObj.get("preferences");
             Iterator<JSONObject> iterator1 = ((ArrayList) preferences1).iterator();
             while (iterator1.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator1.next());
             }
             

             JSONObject competences= (JSONObject) InfirmierObj.get("competences");
             for (Object rkey : competences.keySet()) {
                 Object val = competences.get(rkey);
                 comp.put(rkey.toString(), Integer.valueOf(val.toString()));

             }

            
             
             Nurse inf=new Nurse(Integer.valueOf(id),Integer.valueOf(taux),comp,pref);
             list.add(inf);
       
         }

     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
	return list;
	
}
/***************lire fichier json tranches ********/

static public ArrayList<ConstraintPerShift> readJsonTranches(String file) throws org.json.simple.parser.ParseException
{
	ArrayList<ConstraintPerShift> list= new ArrayList<ConstraintPerShift>();
	 JSONParser parser = new JSONParser();


	 
     try {     
         Object obj = parser.parse(new FileReader(file));

         JSONObject jsonObject =  (JSONObject) obj;

         Object liste_tranche = jsonObject.get("Contraintes_par_tranche");
         Iterator<JSONObject> iterator = ((ArrayList) liste_tranche).iterator();
         
         while (iterator.hasNext()) {
        	 Map<String,Integer> comp = new HashMap<String,Integer>();
       	 
        	 JSONObject tranche_obj =  iterator.next();
             
             JSONObject nbr_min_comp= (JSONObject) tranche_obj.get("nomb_min_comp");
             for (Object rkey : nbr_min_comp.keySet()) {
                 Object val = nbr_min_comp.get(rkey);
                 comp.put(rkey.toString(), Integer.valueOf(val.toString()));

             } 
             ConstraintPerShift tr=new ConstraintPerShift(Integer.valueOf(tranche_obj.get("type_tranche").toString()));
             tr.nombre_min_inf =Integer.valueOf(tranche_obj.get("nombre_min_inf").toString());
             tr.nomb_min_comp=comp;
             list.add(tr);
       
         }

     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
	return list;
	
}
}

