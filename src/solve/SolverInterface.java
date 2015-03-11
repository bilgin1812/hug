
package solve;
/*
 * this classe is for making a solver generic
 */

public interface SolverInterface {
	
	
public void addConstraint();
void solve(String solverType, Boolean DEBUG, Boolean exportModel);
	

}
