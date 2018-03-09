import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class Search
 */
@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/cs221?serverTimezone=UTC";

        //response.setContentType("application/json"); // Response mime type
        response.setContentType("text/html"); 


        PrintWriter out = response.getWriter();
        
        String query = request.getParameter("query").toLowerCase();


        
		

            
        try {
            //Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement stmt = connection.createStatement();
            
            String[] queries = tokenizer(query);
            String mysql_query = searchClause(queries);
            
           
            //get the coresponding results
            ResultSet rs = stmt.executeQuery(mysql_query);
            
            if(!rs.next()) {
            	out.println("<HTML>" + "<HEAD><TITLE>" + "Search Engine" + "</TITLE></HEAD><BODY><H1>No Result Found</H1></BODY></HTML>");
            	return;
            }
            rs.previous();
            
            
            //calculate tf.idf.
            //if there is only one word in the query, just use the pre-computed tf.idf as the results for document(the scores are sorted by mysql)
            
            out.println("<HTML>" + "<HEAD><TITLE>" + "Search Engine" + "</TITLE></HEAD><BODY><H1>Top 10 documents that statisfy the query</H1><ol>");
            if(tokenizer(query).length==1) {
            	while(rs.next()) {
            		out.println("<li>document: "+rs.getString(1)+"<br/>tf.idf score:"+rs.getDouble(2)+"</li>");
                 }
        		out.println("</ol></BODY></HTML>");
            	return;
            }
            
            
            
           /* 	
           HashMap<String,Double> ds = new HashMap<String,Double>();
            	
           while(rs.next()) {
            	String docu = rs.getString(1);
            	double score = rs.getDouble(2);
            	ds.put(docu, score);
            	for(int i=2;i<=queries.length;i++) {
            		score = score + rs.getDouble(2*i);
            	}
            	ds.put(docu,score);
            }
           */
            
            ArrayList<DSPair> dsList = new ArrayList<DSPair>(); 
            while(rs.next()) {
            	String docu = rs.getString(1);
            	double score = 0;
            	for(int i=1;i<=queries.length;i++) {
            		score = score + rs.getDouble(2*i);
            	}
            	dsList.add(new DSPair(docu,score));
            }
            // sort by scores using comparable interface;
            Collections.sort(dsList); 
            
            int limit = 0;
            
            for(int i=0;i<dsList.size() && limit<=10;i++) {
            	String docu=dsList.get(i).getDocu();
            	double score=dsList.get(i).getScore();            	
        		out.println("<li>document: "+docu+"<br/>tf.idf score:"+score+"</li>");    		
        		limit++;
            }  
            
            out.println("</ol></BODY></HTML>");
      
            /*
            JsonArray jsonArray = new JsonArray();
            
            while(rs.next()) {
            	jsonArray.add(rs.getString(1));
            }
            
            System.out.println(jsonArray);
            
            out.write(jsonArray.toString());
            */
             

		} catch (Exception e) {
            out.println("<HTML>" + "<HEAD><TITLE>" + "Search Engine: Error" + "</TITLE></HEAD>\n<BODY>"
                    + "<P>SQL error in doGet: " + e.getMessage() + "</P></BODY></HTML>");
            return;
		}
		
        out.close();	
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
			search.append("select docu,score from indexNum where term=\'"+terms[0]+"\' ORDER BY score DESC limit 0,10");
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
