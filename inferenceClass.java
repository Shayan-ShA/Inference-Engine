import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class inferenceClass {
	    KnowledgeBase knowledgeBase, kcopy;
		int no_of_queries;
		int iterations = 0;
		ArrayList<predicate> queryList;
		Hashtable<String, ArrayList<Integer>> predMap;
		Hashtable<String, ArrayList<Integer>> predicateIntMapOriginal;

	inferenceClass() {

		knowledgeBase = new KnowledgeBase();
		kcopy = new KnowledgeBase();
		predMap = new Hashtable<>();
		predicateIntMapOriginal = new Hashtable<>();
		queryList = new ArrayList<>();
	}
	
	public boolean Const(String a) {
		if (a.charAt(0) <= 'Z')
			return true;
	
		return false;
	}
	
	public boolean Var(String a) {
		if (a.charAt(0) >= 'a' && a.charAt(0) <= 'z')
			return true;
	
		return false;
	}
	
	public void FormQueries(String queries[]){
		for (int i = 0; i < queries.length; i++) {
			query q = new query();
			q.argumNum = 1;
			q.getName(queries[i]);
			q.getArguments(queries[i], 0);
			queryList.add(q);
		}
	}
	
	public ArrayList<Integer> setCountUsed(ArrayList<Integer> countUsed)
	{
		countUsed = new ArrayList<>();
		for(int i = 0; i<kcopy.sentlist.size(); i++)
		{
			countUsed.add(i, 0);
		}
		
		return countUsed;
	}
	
	public predicate negateQuery(predicate q1) {
		predicate q = predicate.predicateCopy(q1);
		if (q.name.charAt(0) == '~')
			q.name = q.name.substring(1);
		else
			q.name = "~" + q.name;
		return q;
	}
	
	public ArrayList<Integer> returnCountCopy(ArrayList<Integer> countUsed)
	{
		ArrayList<Integer> countCopy = new ArrayList<>();
		for(int i=0; i< countUsed.size(); i++)
		{
			countCopy.add(countUsed.get(i));
		}
		
		return countCopy;
	}

	
	public HashMap<String, String> Unify(sentence prevResolvedSent1, sentence newSent1, HashMap<String, String> theta) {
		
		sentence prevResolvedSent = sentence.copySentence(prevResolvedSent1);
		sentence newSent = sentence.copySentence(newSent1);

		ArrayList<Boolean> flag = new ArrayList<>();
		for (int i = 0; i < prevResolvedSent.predicatelist.size(); i++) {

			for (int j = 0; j < newSent.predicatelist.size(); j++) {
				
				if(checkNegation(prevResolvedSent.predicatelist.get(i).name, newSent.predicatelist.get(j).name)){
					if(updateSubstitutionMap1(prevResolvedSent.predicatelist.get(i), newSent.predicatelist.get(j), theta)==false || 
							newSent.predicatelist.get(j).arguments.size() != prevResolvedSent.predicatelist.get(i).arguments.size())//CHECK IF A SUBSTITUTION IS POSSIBLE
						{
						flag.add(false);
						}
					else
						flag.add(true);
					}
			}
		}
		if (!flag.contains(true))
			return null;
		
		return theta;
	}
	 
	public sentence merge(sentence prevResolvedSent, sentence newSent, HashMap<String, String> SubstitutionMap) {

		sentence Resolved = new sentence();
		sentence prevResolvedSentCopy = sentence.copySentence(prevResolvedSent);
		sentence newSentCopy = sentence.copySentence(newSent);
		Substitute(prevResolvedSentCopy, SubstitutionMap);

		Substitute(newSentCopy, SubstitutionMap);

		if(prevResolvedSent.equals(newSent))
			return new sentence();
		int flag=0;
		for(int i=0; i< prevResolvedSentCopy.predicatelist.size();i++)
		{
			predicate negatedTemp = negateQuery(prevResolvedSentCopy.predicatelist.get(i));
			if(newSentCopy.contains(negatedTemp))
				continue;
				Resolved.predicatelist.add(prevResolvedSentCopy.predicatelist.get(i));
		}
		for(int i=0; i< newSentCopy.predicatelist.size();i++)
		{
			predicate negatedTemp = negateQuery(newSentCopy.predicatelist.get(i));
			if(prevResolvedSentCopy.contains(negatedTemp))
				continue;
				Resolved.predicatelist.add(newSentCopy.predicatelist.get(i));
		}
//		System.out.println(Resolved);
		
		return Resolved;
	
	}

	public void putIntoHashMap(Hashtable<String, ArrayList<Integer>> predicateIntMapOriginal, sentence s, int row) {
		for (int i = 0; i < s.predicatelist.size(); i++) {
			if (predicateIntMapOriginal.containsKey(s.predicatelist.get(i).name))
				predicateIntMapOriginal.get(s.predicatelist.get(i).name).add(row);
			else {
				ArrayList<Integer> rowList = new ArrayList<>();
				rowList.add(row);
				predicateIntMapOriginal.put(s.predicatelist.get(i).name, rowList);
			}
			
		}
	}	
	
	
	public boolean checkNegation(String x, String y)
	{
		if(x.charAt(0)=='~'&& !(y.charAt(0)== '~'))
		{
			if(x.substring(1).equals(y))
				return true;
			return false;
		}
		if(y.charAt(0)=='~'&&!(x.charAt(0)=='~'))
		{
			if(y.substring(1).equals(x))
				return true;
			return false;
		}
		
		return false;
	}
	
	public boolean updateSubstitutionMap(predicate a, predicate b, HashMap<String, String> substitutionMap) {

		for(int i=0;i<a.arguments.size();i++)
		{
			
			String arg1= a.arguments.get(i);
			String arg2 = b.arguments.get(i);
			if (Const(arg1) && Const(arg2))
			{
				
				if (arg1.equals(arg2))
				{
					continue;
				} else {
					return false;
				}
			}
		
			if (Var(arg1) && Const(arg2)){
				
				if (substitutionMap.containsKey(arg1)) {
					if (substitutionMap.get(arg1).equals(arg2))	continue;

					else return false;
				}
				substitutionMap.put(arg1, arg2);
				continue;
			}
	
			if (Var(arg2) && Const(arg1))
			{
				if (substitutionMap.containsKey(arg2)) {
					if (substitutionMap.get(arg2).equals(arg1))	continue;
					else return false;
				}
				substitutionMap.put(arg2, arg1);
				continue;
			}
			
			if (Var(arg1) && Var(arg2))
			{
				if (substitutionMap.containsKey(arg1) && substitutionMap.containsKey(arg2))
																									 
				{
					if (substitutionMap.get(arg1).equals(substitutionMap.get(arg2))) continue;
					else return false;
				}
	
				if (substitutionMap.containsKey(arg1) && !substitutionMap.containsKey(arg2)){
					substitutionMap.put(arg2, substitutionMap.get(arg1));
					continue;
				}

				if (substitutionMap.containsKey(arg2) && !substitutionMap.containsKey(arg1)){
					substitutionMap.put(arg1, substitutionMap.get(arg2));
					continue;
				}
				
				if (!substitutionMap.containsKey(arg1) && !substitutionMap.containsKey(arg2))
				{
					substitutionMap.put(arg1, arg1); 
					substitutionMap.put(arg2, arg1);
					continue;																					
				}
			}
	
		}
					
		return true;			
	}
	
	public boolean updateSubstitutionMap1(predicate a, predicate b, HashMap<String, String> substitutionMap) {
		for(int i=0;i<a.arguments.size();i++){

			String arg1= a.arguments.get(i);
			String arg2 = b.arguments.get(i);

			if (Const(arg1) && Const(arg2)){
				
				if (arg1.equals(arg2)){
					continue;
				} else {
					return false;
				}
			}
			
			if (Var(arg1) && Var(arg2)){
				if (substitutionMap.containsKey(arg1) && substitutionMap.containsKey(arg2)) // both variables have a substitution
				{
					if(Const(substitutionMap.get(arg1))&& Const(substitutionMap.get(arg2)))
						{
							if(substitutionMap.get(arg1).equals(substitutionMap.get(arg2)))
								continue;
							else
								return false;
						}
						if(Var(substitutionMap.get(arg1))&& Const(substitutionMap.get(arg2)))
						{
								substitutionMap.put(arg1, substitutionMap.get(arg2));
								continue;
						}
						if(Var(substitutionMap.get(arg2))&& Const(substitutionMap.get(arg1)))
						{
							substitutionMap.put(arg2, substitutionMap.get(arg1));
							continue;
						}
						else {
							substitutionMap.put(arg2, substitutionMap.get(arg1));
							continue;
						}
				}
	
				if (substitutionMap.containsKey(arg1) && !substitutionMap.containsKey(arg2)){
					substitutionMap.put(arg2, substitutionMap.get(arg1));
					continue;																		
				}
	
				if (substitutionMap.containsKey(arg2) && !substitutionMap.containsKey(arg1))
																										
				{
					substitutionMap.put(arg1, substitutionMap.get(arg2));
					continue;																		
				}
				
				if (!substitutionMap.containsKey(arg1) && !substitutionMap.containsKey(arg2)){
					substitutionMap.put(arg1, arg1); 
					substitutionMap.put(arg2, arg1);
					continue;																					
				}
			}
	
			
			if (Var(arg1) && Const(arg2)){
				if (substitutionMap.containsKey(arg1)) {
						if (Const(substitutionMap.get(arg1)))
						{
							if(substitutionMap.get(arg1).equals(arg2))
								continue;
							else
								return false;
						}
						else {
							String first = substitutionMap.get(arg1);
							substitutionMap.put(arg1, arg2);
							substitutionMap.put(first, arg2); 
							continue;
						}
						
					}
				else {
					substitutionMap.put(arg1, arg2);
					continue;
					}
			}
	
			if (Var(arg2) && Const(arg1))
			{
				if (substitutionMap.containsKey(arg2)) {

						if (Const(substitutionMap.get(arg2)))
						{
							if(substitutionMap.get(arg2).equals(arg1))
								continue;
							else
								return false;
						}
						else {

							String first = substitutionMap.get(arg2);							
								substitutionMap.put(arg2, arg1);
								substitutionMap.put(first, arg1); 
								continue;
							}
					}
				else {
					substitutionMap.put(arg2, arg1);
					continue;
					}
			}
		}
					
		return true;			
	}
	public void Substitute(sentence currSent, HashMap<String, String> substitution){
	
		for(int i=0;i<currSent.predicatelist.size(); i++){
			for(int j=0; j<currSent.predicatelist.get(i).arguments.size();)
			{
				if(Const(currSent.predicatelist.get(i).arguments.get(j))){
						j++;
						continue;
				}
				if(substitution.containsKey(currSent.predicatelist.get(i).arguments.get(j)))
				{
					String constant = new String(substitution.get(currSent.predicatelist.get(i).arguments.get(j)));
					if(Var(constant)&&!currSent.predicatelist.get(i).arguments.get(j).equals(constant))
						currSent.predicatelist.get(i).arguments.set(j, constant);
					else {
					currSent.predicatelist.get(i).arguments.set(j, constant);
					j++;}
				}
				else 
					j++;
					
			}
		}
	}
	
	public static HashMap<String,String> copyHashMap(HashMap<String, String> h)
	{
		HashMap<String, String> copy = new HashMap<>();
		
		for (String key: h.keySet()) {
			copy.put(key, h.get(key));
		}
		
		return copy;
	}
	
	public static Hashtable<String, ArrayList<Integer>> copyHashTable(Hashtable<String, ArrayList<Integer>> original)
	{
	Hashtable <String, ArrayList<Integer>> copy =new Hashtable<>();
	 Set<String> keys = original.keySet();
     for(String key: keys){
    	 copy.put(key, original.get(key));
     }
	return copy;
		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Queries are \n");
		for (int i = 0; i < queryList.size(); i++)
			sb.append(queryList.get(i) + "\n");
	
		return sb.toString() + "\nSentences in KB \n" + knowledgeBase.toString();
	}
	
	public boolean Resolution(sentence resolvedSent, ArrayList<Integer> count) {

		  if(resolvedSent.predicatelist.size() == 0) return true; 

		  boolean  result = false;
		  for (int i = 0; i < resolvedSent.predicatelist.size(); i++){ 
			  predicate currTemp = predicate.predicateCopy(resolvedSent.predicatelist.get(i));
			  currTemp = negateQuery(currTemp);
			 
			  if(predMap.get(currTemp.name) == null|| predMap.get(currTemp.name).size()==0)
				  return false; 
			  
			  for (int j = 0; j <predMap.get(currTemp.name).size(); j++)
			  {
				  int index =predMap.get(currTemp.name).get(j); 
				  int value= count.get(index)+1;
				 count.set(index, value);
				  if( count.get(index)>5)//100)
					  {
					  continue;
					  }
				  HashMap<String, String> beta = new HashMap<>();
				  HashMap<String, String> theta = Unify(sentence.copySentence(resolvedSent), sentence.copySentence(kcopy.sentlist.get(index)),beta);// copyHashMap(substitutionMap)); //send copy 
				  if(theta==null)
					  result= false;
				  else //if(!theta.isEmpty()) 
				  { 
					  sentence newResolvedSent = merge(sentence.copySentence(resolvedSent), kcopy.sentlist.get(index), theta); // copyHashMap(theta)); 
					  
					  List<predicate> list = newResolvedSent.predicatelist;
					  /////////////////////////////***************
					  
//					  for(predicate pre: list) {
//						  System.out.println(pre.name);
//						  for(String cons:pre.arguments) System.out.println(cons);
//					  }
					  ////////////////////////////***************
					  
					  if(newResolvedSent.predicatelist.size() == 0) {
						  return true;
					  }

//					  System.out.println("-----");
//					  for(String st:theta.keySet()) {
//						  System.out.println(st + " " + theta.get(st));
//					  }
//					  System.out.println("-----");
					  
					  result = Resolution(newResolvedSent, returnCountCopy(count));//, copyHashMap(theta));
					  
					  if(result==true)
						  return true;
				  }
			  } 
		  }
		  return result; 
		}
}

