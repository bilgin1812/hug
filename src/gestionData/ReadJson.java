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
/*
 * Reads Json files
 */
public class ReadJson {
	/*
	 * @param Json filename 
	 */

static public Contstraint readJsonContraintes(String file) throws org.json.simple.parser.ParseException
	{
	
	 Contstraint cont = new Contstraint();
	 JSONParser parser = new JSONParser();

    try {     
        Object obj = parser.parse(new FileReader(file));

        JSONObject jsonObject =  (JSONObject) obj;

        JSONObject cont_obj = (JSONObject)jsonObject.get("constraints");
    
        cont.minWeeklyHoliday    = Integer.valueOf(  cont_obj.get("minWeeklyHoliday").toString());
        cont.nightSeriesRate100  = Integer.valueOf(  cont_obj.get("nightSeriesRate100").toString());
        cont.nightSeriesRate80   = Integer.valueOf(  cont_obj.get("nightSeriesRate80").toString());
        cont.numberMinNight     = Integer.valueOf(  cont_obj.get("numberMinNight").toString());
        cont.maxConsecutiveWork = Integer.valueOf(  cont_obj.get("maxConsecutiveWork").toString());
        cont.nbMinWeekendHoliday   = Integer.valueOf(  cont_obj.get("nbMinWeekendHoliday").toString());

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
	return cont;
		
	}


/*
 * Read list of nurses
 * @param Json filename 
 */
static public ArrayList<Nurse> readJsonNurses(String file) throws org.json.simple.parser.ParseException
{
	ArrayList<Nurse> list= new ArrayList<Nurse>();
	 JSONParser parser = new JSONParser();

     try {     
         Object obj = parser.parse(new FileReader(file));

         JSONObject jsonObject =  (JSONObject) obj;

         Object infirmiers = jsonObject.get("List_Nurses");
         Iterator<JSONObject> iterator = ((ArrayList) infirmiers).iterator();
         while (iterator.hasNext()) {
        	 Map<String,Integer> comp = new HashMap<String,Integer>();
        	 Vector pref = new Vector();;
        	 
        	 JSONObject InfirmierObj =  iterator.next();
             String id = InfirmierObj.get("id").toString();
             String taux =  InfirmierObj.get("activiyRate").toString();
             
             JSONArray preferences1 = (JSONArray) InfirmierObj.get("preferences");
             Iterator<JSONObject> iterator1 = ((ArrayList) preferences1).iterator();
             while (iterator1.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator1.next());
             }
             

             JSONObject competences= (JSONObject) InfirmierObj.get("skills");
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
/*
 * read the list of Constraints per Shift
 * @param Json filename
 */

static public ArrayList<ConstraintPerShift> readJsonTranches(String file) throws org.json.simple.parser.ParseException
{
	ArrayList<ConstraintPerShift> list= new ArrayList<ConstraintPerShift>();
	 JSONParser parser = new JSONParser();


	 
     try {     
         Object obj = parser.parse(new FileReader(file));

         JSONObject jsonObject =  (JSONObject) obj;

         Object liste_tranche = jsonObject.get("ConstraintsPerShift");
         Iterator<JSONObject> iterator = ((ArrayList) liste_tranche).iterator();
         
         while (iterator.hasNext()) {
        	 Map<String,Integer> comp = new HashMap<String,Integer>();
       	 
        	 JSONObject tranche_obj =  iterator.next();
             
             JSONObject nbr_min_comp= (JSONObject) tranche_obj.get("numberMinSkills");
             for (Object rkey : nbr_min_comp.keySet()) {
                 Object val = nbr_min_comp.get(rkey);
                 comp.put(rkey.toString(), Integer.valueOf(val.toString()));

             } 
             ConstraintPerShift tr=new ConstraintPerShift();
             tr.numberMinSkillsSum =Integer.valueOf(tranche_obj.get("numberMinSkillsSum").toString());
             tr.numberMinSkills=comp;
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

