package ressource;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Nurse {
	public int id;
	public int activiyRate;
	@SuppressWarnings("rawtypes")
	public Vector preferences;
	public Map <String,Integer>skills;
	

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Nurse(int id,int rate,Map skills,Vector pref)
	{
		this.id=id;
		this.activiyRate=rate;
		this.skills=skills;
		this.preferences=pref;

		
		
	}
	@SuppressWarnings("rawtypes")
	public Nurse(int id)
	{
		this.id=id;
		this.activiyRate=100;
		this.preferences=new Vector();
		this.skills =new  HashMap<String, Integer>();
		
	}
	
}
