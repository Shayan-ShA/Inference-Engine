import java.util.ArrayList;
import java.util.Arrays;

public class predicate {

	String name;
	int argumNum;
	ArrayList<String> constants;
	ArrayList<String> arguments;

	predicate(String name, int no_arguments, String val){
		this.name = name;
		this.argumNum = no_arguments;
		constants = new ArrayList<>();
		arguments = new ArrayList<>();
	}
	
	predicate(){
		constants = new ArrayList<>();
		arguments = new ArrayList<>();
	}
	public void getName(String fullPredicate){
			this.name = fullPredicate.substring(0, fullPredicate.indexOf('(')).trim();
	}
	
	public void getArguments(String fullPredicate, int row){
		String argumentSubstring = fullPredicate.substring( fullPredicate.indexOf('(')+1,  fullPredicate.indexOf(')')).trim(); //2
		String argumentArray[] = argumentSubstring.split(",");
		
		for(String x: argumentArray)
		{
			if(x.trim().charAt(0)<'a')
				constants.add(x.trim());
			else {
				x = x+row;
			}
			arguments.add(x.trim());
		}	
	}
	
	public static predicate predicateCopy(predicate p)
	{
		predicate copy = new predicate();
		copy.name = p.name;
		copy.argumentsCopy(p);
		
		return copy;
	}
	
	public void argumentsCopy(predicate p)
	{
		int i=0;
		for(String x: p.arguments)
			this.arguments.add(i++, x);
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("");
		for(int i=0; i<arguments.size(); i++)
		{
			sb.append(arguments.get(i));
			if(i!=(arguments.size()-1))
				sb.append(", ");
		}
		return  name+""+ arguments;//"\n"+name+"\t Arguments = "+ arguments+ " \t no_of_arguments = "+no_of_arguments+"\t constants"+" = "+constants;
	}	
}

