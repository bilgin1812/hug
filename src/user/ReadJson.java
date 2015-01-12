package user;

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

public ReadJson()
{}

public ArrayList<Infirmier> readJson(String file) throws org.json.simple.parser.ParseException
{
	ArrayList<Infirmier> list= new ArrayList<Infirmier>();
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
             
             JSONArray preferences1 = (JSONArray) InfirmierObj.get("preferences1");
             Iterator<JSONObject> iterator1 = ((ArrayList) preferences1).iterator();
             while (iterator1.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator1.next());
             }
             
             JSONArray preferences2 = (JSONArray) InfirmierObj.get("preferences2");
             Iterator<JSONObject> iterator2 = ((ArrayList) preferences2).iterator();
             while (iterator2.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator2.next());
             }
             JSONArray preferences3 = (JSONArray) InfirmierObj.get("preferences3");
             Iterator<JSONObject> iterator3 = ((ArrayList) preferences3).iterator();
             while (iterator3.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator3.next());
             }
             JSONArray preferences4 = (JSONArray) InfirmierObj.get("preferences4");
             Iterator<JSONObject> iterator4 = ((ArrayList) preferences4).iterator();
             while (iterator4.hasNext()) {
            	// System.out.println(iterator1.next());
            	 pref.add(iterator4.next());
             }
             JSONObject competences= (JSONObject) InfirmierObj.get("competences");
             for (Object rkey : competences.keySet()) {
                 Object val = competences.get(rkey);
                 comp.put(rkey.toString(), Integer.valueOf(val.toString()));

             }

            
             
             Infirmier inf=new Infirmier(Integer.valueOf(id),Integer.valueOf(taux),comp,pref);
             list.add(inf);
       
         }

     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
	return list;
	
}


}

