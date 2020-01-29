import java.util.ArrayList;

public class KnowledgeBase {
	int sentNum;
	ArrayList<sentence> sentlist;
	
	KnowledgeBase(){
		sentlist = new ArrayList<>();
	}
	
	public void appendToKB(sentence s)
	{
		sentlist.add(s); // when appending to the kb 
		this.sentNum++;
	}
	
public static KnowledgeBase copyKB(KnowledgeBase knowledgeBase)
	{
	KnowledgeBase copy = new KnowledgeBase();
		for(int i=0;i<knowledgeBase.sentlist.size();i++)
		{
			copy.sentlist.add(sentence.copySentence(knowledgeBase.sentlist.get(i)));
		}
		
		return copy;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder("");
		for(int i =0;i<sentlist.size();i++)
		{
			sb.append(sentlist.get(i).toString()+"\n");
		}
		return sb.toString();
	}
}
