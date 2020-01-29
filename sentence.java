
import java.util.ArrayList;
import java.util.Arrays;

public class sentence {
	ArrayList<predicate> predicatelist;
	ArrayList<String> predicateNames;
	
	sentence(){
		predicatelist = new ArrayList<>();
		predicateNames = new ArrayList<>();
	}
	
	sentence(String sent){		
	}
	
	public static int countOccurenceOf(String sent, char c)
	{
		int count=0;
		for(int i=0;i<sent.length();i++)
			if(sent.charAt(i)==c)
				count++;
		return count;
	}
	
	public void formPredicate(String sent, int row) {
		String parts[] = sent.split("\\|");
		for(int i=0;i<parts.length;i++){
			predicate p = new predicate();
			p.argumNum = countOccurenceOf(parts[i], ',') + 1;
			p.getName(parts[i]);
			p.getArguments(parts[i], row);
			predicatelist.add(p);
			predicateNames.add(p.name);
		}
	}
	
	public static sentence copySentence(sentence s)
	{
		sentence copy = new sentence();
		for(int i=0; i<s.predicatelist.size();i++){
			copy.predicatelist.add(predicate.predicateCopy(s.predicatelist.get(i)));
		}
		return copy;
	}
	
	public boolean contains(predicate p)
	{
		for(int i=0; i < predicatelist.size(); i++){
			if(!predicatelist.get(i).name.equals(p.name))
				continue;
			int j;
			for(j=0;j < p.arguments.size(); j++){
				if(p.arguments.get(j).equals(predicatelist.get(i).arguments.get(j)))
					continue;
				else break;
			}
			if(j==p.arguments.size()) return true;
		}
		return false;
	}
	
	public String toString(){
		StringBuilder asb = new StringBuilder("");
		for(int i=0; i<predicatelist.size(); i++){
			asb.append(predicatelist.get(i).toString());
			if(i!=(predicatelist.size()-1))
				asb.append(" | ");
		}
		return asb.toString();
	}
}

