package gestionData;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.ortools.linearsolver.MPVariable;

import ressource.*;

public class ShowList {

	/*
	 * @param list of nurses
	 */
	public static void showNursesList(ArrayList<Nurse> list) {
		System.out.println(list.size() + " Nurses");
		for (Nurse i : list)
			System.out.println("inf :" + i.id + " activiy Rate: "
					+ i.activiyRate + " compt:" + i.skills.toString()
					+ " pref. :" + i.preferences.toString());

	}

	public static void WriteCSV(MPVariable[][] m, int nbrNurses,
			int totalShiftsPerMonth, ArrayList<Nurse> li) {
		String csvFile = "Planning.csv";
		String line = "";
		String separator = ";";
		try {
			FileWriter fw = new FileWriter(csvFile);
			// entête
			line = ";;;;Lu;Ma;Me;Je;Ve;Sa;Di;Lu;Ma;Me;Je;Ve;Sa;Di;Lu;Ma;Me;Je;Ve;Sa;Di;Lu;Ma;Me;Je;Ve;Sa;Di";
			fw.write(line + "\n");
			line = "Nom;Taux;CH;RE;";
			for (int i = 1; i <= totalShiftsPerMonth / 3; i++) {
				line += i + ";";
			}
			fw.write(line + "\n");
			// données
			for (int i = 0; i < nbrNurses; i++) {
				line = "";
				line += li.get(i).id + ";"; // Name
				line += li.get(i).activiyRate + ";"; // % activity
				line += "0;0;";
				for (int j = 0; j < totalShiftsPerMonth; j += 3) {
					if (m[i][j].solutionValue() == 1)
						line += "Morning";
					else if (m[i][j + 1].solutionValue() == 1)
						line += "Afternoon";
					else if (m[i][j + 2].solutionValue() == 1)
						line += "Night";
					else
						line += "Free";
					line += separator;
				}
				// Adding line return
				fw.write(line + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
