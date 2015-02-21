package gestionData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ressource.Nurse;
import net.sf.sojo.interchange.Serializer;
import net.sf.sojo.interchange.json.JsonSerializer;

 
public class ReadCVS {
 
public ReadCVS()
{
}
  public ArrayList<Nurse> read() {
 
	String csvFile = "infirmiers.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";
	String cvsSplitPref ="\\+";
	ArrayList<Nurse> listInf= new ArrayList<Nurse>();
 
	try {
 
		Map<String, String> maps = new HashMap<String, String>();
		Boolean cpt=true;
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
 
			if(cpt) cpt=false;
			else {
			
			// use comma as separator
			String[] champs = line.split(cvsSplitBy);
			int id=Integer.valueOf(champs[0]);
			int taux=Integer.valueOf(champs[1]);
			
			Map comp = new HashMap<Integer, Integer>();
			for (int i=0; i<4 ;i++)
				comp.put(i, Integer.valueOf(champs[i+2]));
			Vector pref=new Vector<Integer>();
			
			for(int i=6 ; i<10 ; i++)
			{
				
				String[] st=champs[i].split(cvsSplitPref);
			
				for(int j=0;j<7 ; j++)
				{
					//System.out.println(Integer.valueOf(st[j]));
					pref.add(Integer.valueOf(st[j]));
				}
			}
			Nurse inf =new Nurse(id, taux, comp, pref);
			listInf.add(inf);
		
			}
		}
 
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 

	    
	return listInf;
  }
  
 
 
}