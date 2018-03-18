import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SearchOptimize {
	
	static final String loginUser = "mytestuser";
    static final String loginPasswd = "mypassword";
    static final String loginUrl = "jdbc:mysql://localhost:3306/cs221?serverTimezone=UTC";
	
	
	public static void main(String[] args) throws IOException {
		
		String[] query  = {"mondego","machine learning","software engineering","security","student affairs","graduate courses","Crista Lopes",
							"REST","computer games","information retrieval"};
		
		 try {
	            Class.forName("com.mysql.jdbc.Driver").newInstance();
	            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
	            Statement stmt = connection.createStatement();
	            
	            
	            System.out.print("number of queries:"+query.length);
	            /*
				File file = new File("F:/SearchResult/result.txt");
				PrintStream ps = new PrintStream(new FileOutputStream(file));
	            */
				for(int i=0;i<query.length;i++) {
					
					File file = new File("F:/SearchResult/"+query[i]);
					PrintStream ps = new PrintStream(new FileOutputStream(file));
									
					
					String[] terms = tokenizer(query[i]);
					String mysql_query = searchClause(terms);
					
					ResultSet rs = stmt.executeQuery(mysql_query);
		            
		            //System.out.print(rs.toString());	
	            	ArrayList<DSPair> dsList = new ArrayList<DSPair>(); 
	            	while(rs.next()) {
	                	String docu = rs.getString(1);
	                	String urltest = JsonConverter.Url(docu);
	                	double score = 0;
	                	for(int j=1;j<=terms.length;j++) {
	                		score = score + rs.getDouble(2*j);
	                	}
	                	//optimization 
	                	for(int c=0;c<terms.length;c++) {
	                		if(urltest.contains(terms[c])) {
	                			score = score*1.2;
	                		}
	                	}
	                	
	                	
	                	dsList.add(new DSPair(docu,score));
	                }
	            	Collections.sort(dsList); 
	            	
	            	int limit = 0;
	            	//ps.append(query[i]+":\n");
	                for(int k=0;k<dsList.size() && limit<=4;k++) {
	                	
	                	String docu=dsList.get(k).getDocu();
	                	String url = JsonConverter.Url(docu);
	                	ps.append(docu+":");
	                	ps.append(url);
	                	if(k!=dsList.size()-1) {
	                		ps.append("\n");
	                	}
	            		limit++;
	                }  
		                //ps.append("\n");		           
		   
		            ps.close();
				}			
				System.out.println("\n Search completed");
			 
		 }catch (Exception e) {
	            System.out.println(e.getMessage());	    
	            return;
		 }
	}
	
	
	
	public static String[] tokenizer(String ori){
		StringBuffer ori_b = new StringBuffer(ori);
		for(int i=0;i<ori_b.length();i++) {
			if(ori_b.charAt(i)=='+'||ori_b.charAt(i)=='-'||ori_b.charAt(i)=='~'||ori_b.charAt(i)=='?'
					||ori_b.charAt(i)=='&'||ori_b.charAt(i)==':'||ori_b.charAt(i)=='.'||ori_b.charAt(i)=='@'
					||ori_b.charAt(i)=='*'||ori_b.charAt(i)=='_'||ori_b.charAt(i)=='/') {
				
				ori_b.setCharAt(i, ' ');
			}
		}
		ori = ori_b.toString();
		ori=ori.trim();
		String [] s = new String[5];
		s = ori.split("\\s+");
		return s;		
	}
	public static String searchClause(String[] terms){
		StringBuffer search  = new StringBuffer();
		if(terms.length==1) {
			search.append("select docu,score from indexNum where term=\'"+terms[0]+"\'");
			return search.toString();
			
		}
		search.append("select * from ");
		for(int i=0;i<terms.length;i++) {
			search.append("(select docu,score from indexNum where term=\'"+terms[i]+"\') as term"+i);
			if(i!=terms.length-1) {
				search.append(",");
			}
		}
		search.append(" where ");
		for(int j=1;j<terms.length;j++) {
			search.append("term0.docu=term"+j+".docu");
			if(j!=terms.length-1) {
				search.append(" and ");
			}
		}
		return search.toString();		   
	}

}

