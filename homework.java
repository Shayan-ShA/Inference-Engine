import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class homework{

	public void Input(inferenceClass inference) {

		FileReader file = null;
		BufferedReader br = null;
		
		try {
			file = new FileReader("input.txt");
			br = new BufferedReader(file);
			short i = 0, j = 0;
			inference.no_of_queries = (short) Integer.parseInt(br.readLine());
			String queries[] = new String[inference.no_of_queries];
			
			for(i=0; i<inference.no_of_queries; i++)
			{
				queries[i] = br.readLine();
			}
			inference.FormQueries(queries);
			inference.knowledgeBase.sentNum =  (short) Integer.parseInt(br.readLine());
			String sentences[] = new String[inference.knowledgeBase.sentNum];
			
			
			
			for(i=0; i<inference.knowledgeBase.sentNum; i++)
			{
//				System.out.println(i  );
				sentences[i] = br.readLine();
				sentences[i] = CNFConverter(sentences[i]);
			}
			
//			for(i=0; i<inference.knowledgeBase.sentNum; i++){
//				System.out.println(sentences[i]);
//				
//			}

			this.setKnowledgeBase(sentences, inference);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (file != null)
					file.close();
				if (br != null)
					br.close();
			} catch (IOException ex) {

			}
		}
	}

	public String CNFConverter(String str) {
		int idx = str.indexOf("=>");
		if(idx != -1) {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			char[] arr = str.substring(0,idx).toCharArray();
			while(i<arr.length) {
				while(i<arr.length && arr[i] == ' ') {
					sb.append(arr[i]);
					i++;
				}
				if(i<arr.length && (arr[i] == '&' || arr[i] == '^')) {
					sb.append('|');
				}else if(i<arr.length && arr[i] == '~' && arr[i+1]<'a') {
					i++;
					while(i<arr.length && arr[i] != ')') {
						sb.append(arr[i]);
						i++;
					}
					sb.append(')');
    			}else if(i<arr.length && arr[i]<'a') {
					sb.append("~");
					while(i<arr.length && arr[i] != ')') {
						sb.append(arr[i]);
						i++;
					}
					sb.append(')');
				}
				i++;
			}
			sb.append('|');
			sb.append(str.substring(idx+2));
			return sb.toString();
		}
		return str;
	}
	
	
	public void Output(boolean output[])
	{
			StringBuilder b = new StringBuilder();
			for(int i=0;i<output.length;i++)
			{
				String a  = String.valueOf(output[i]);
				if(i==output.length-1)
					b.append(a.toUpperCase());
				else
					b.append(a.toUpperCase()+"\n");
			}
			FileWriter f;
			BufferedWriter w = null;
			try {
				f = new FileWriter("output.txt");
				w = new BufferedWriter(f);
				w.write(b.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					w.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

	}
	
	public void setKnowledgeBase(String sentences[], inferenceClass inf)
	{
		for(int i=0;i<sentences.length;i++)
		{
			sentence s = new sentence();
			s.formPredicate(sentences[i], i);
			inf.putIntoHashMap(inf.predicateIntMapOriginal, s, i);
			inf.knowledgeBase.sentlist.add(s);
		}	
	}
	
	public boolean[] Resol(inferenceClass inference)
	{
		boolean res[] = new boolean[inference.no_of_queries];
		for(int i=0; i< inference.no_of_queries;i++)
		{
			sentence s =new sentence();

			s.predicatelist.add(inference.negateQuery(inference.queryList.get(i)));
			HashMap<String, String> substitutionMap = new HashMap<>();
			ArrayList<Integer> co = new ArrayList<>();
			
//			System.out.println("Checkup " + s);
			
			inference.kcopy = KnowledgeBase.copyKB(inference.knowledgeBase);
			inference.kcopy.appendToKB(s);
			inference.predMap = inference.copyHashTable(inference.predicateIntMapOriginal);
			inference.putIntoHashMap(inference.predMap, s, inference.knowledgeBase.sentlist.size());
			co = inference.setCountUsed(co);
			
			res[i] = inference.Resolution(s, co); //, substitutionMap);			
		}
		return res;
	}
	public static void main(String[] args) {
		homework hk = new homework();
		inferenceClass inference = new inferenceClass();
		hk.Input(inference);
		sentence sent = new sentence();
		sent.predicatelist.add(inference.queryList.get(0));
		boolean ans[] = hk.Resol(inference);		
		hk.Output(ans);
	}
}

