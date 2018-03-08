import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Types;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DomParser_mains
 */
@WebServlet("/DomParser")
public class DomParser extends HttpServlet {
	private static final long serialVersionUID = 1L;   
	
	List pairs;
	Document dom;
	int i;
		
	public DomParser(){
		pairs = new ArrayList();
	}
	
	
	private void parseFile(){
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();		
		try {			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			InputStream in = getServletContext().getResourceAsStream("/WEB-INF/index_test.xml");					
			dom = db.parse(in);			

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	
	private void parseWDPair(){
		//get the root element
		Element docEle = dom.getDocumentElement();				
		
		//get a nodelist of <word> elements
		NodeList nl = docEle.getElementsByTagName("w");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {				

				Element el = (Element)nl.item(i);
				NodeList nl2 = el.getElementsByTagName("d");
				for(int j=0; j<nl2.getLength(); j++) {
					Element el2 = (Element)nl2.item(j);
					//get the wdpair object
					WDPair wd = getWDPair(el, el2);
					//add wdpair to list
					pairs.add(wd);
				}
				
				/*
				//get the wdpair object
				WDPair wd = getWDPair(el);
				
				//add wdpair to list
				pairs.add(wd);
				*/
			}
		}
	}
		
	
	//Create wdpair object
	private WDPair getWDPair(Element el, Element el2) {		
		String term = getTextValue(el,"n");
		String docu = getDocuValue(el2,"dn");
		double score = getScoreValue(el2,"tf-idf");
		WDPair wd = new WDPair(term, docu, score);
		
		return wd;
	}
	
	
	//look for the tag and get text value	 
	private String getTextValue(Element ele, String tagName) {
		String textVal = "";
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			if (el.getChildNodes().getLength() == 0
			    && el.getFirstChild() == null) {
			    return textVal;
			} else {			
			textVal = el.getFirstChild().getNodeValue();
			}
		}
		return textVal;
	}
	
	//look for the docu tag and get text value
	private String getDocuValue(Element ele, String tagname) {
		String textVal = "";
		/*
		NodeList nl = ele.getElementsByTagName("d");
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = getTextValue(el, tagname);
		}	
		*/
		textVal = getTextValue(ele, tagname);
		return textVal;		
	}
	
	//look for the score tag and get text value
	private double getScoreValue(Element ele, String tagname) {
		String textVal = "";
		double score = 0;
		/*
		NodeList nl = ele.getElementsByTagName("d");
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = getTextValue(el, tagname);
			score = Double.parseDouble(textVal);
		}	
		*/	
		textVal = getTextValue(ele, tagname);
		score = Double.parseDouble(textVal);
		return score;		
	}
	
	
	public void run() {
		parseFile();
		parseWDPair();
				
		try {
			populateIntoDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void populateIntoDB() throws SQLException {
		String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/cs221?serverTimezone=UTC";
        
        
        try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
        Connection connection = null;
		try {
			connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		} catch (SQLException e) {
			System.out.println("Connection Invalid");
		}
		
		//Batch insert pairs
		PreparedStatement psInsertPair = null;
		String sqlInsertPair = null;
		
		sqlInsertPair = "insert into indexNum (term, docu, score) values(?,?,?)";
		
		try {
			connection.setAutoCommit(false);
			psInsertPair = connection.prepareStatement(sqlInsertPair);
			
			for (int i=0; i<pairs.size(); i++) {
	        	   String term = ((WDPair) pairs.get(i)).term;
	        	   String docu = ((WDPair) pairs.get(i)).docu;
	        	   double score = ((WDPair) pairs.get(i)).score;
	        	   
	        	   psInsertPair.setString(1, term);
	        	   psInsertPair.setString(2, docu);
	        	   psInsertPair.setDouble(3, score);
	        	   //System.out.println(term + " " + docu + " " + score);
	        	   
	        	   psInsertPair.addBatch();
		    }
			
			psInsertPair.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		connection.setAutoCommit(true);		
		Statement show = connection.createStatement();
		
		ResultSet count = (ResultSet)show.executeQuery("select count(*) from indexNum;");
	    count.next();
	    i = count.getInt(1);
	    
		try {
			if(psInsertPair!=null) psInsertPair.close();
			if (connection!=null) connection.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.println("<html><head><title>Result</title></head>");
        out.println("<body><h1 align=\"center\">Result</h1>");
        
		long startTime = System.currentTimeMillis();
		
		run();
        
        out.println("<li>Num of pairs inserted: "+ pairs.size()+"</li>");  
        
        out.println("<h2>WDPairData</h2><ul>");
        out.println("<li>Num of word document pairs established: "+ i +"</li></ul>");
		
		long endTime = System.currentTimeMillis();		
		out.println("<t><b>Program running time: " + (endTime - startTime) + "ms</b></t>");
	}

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
