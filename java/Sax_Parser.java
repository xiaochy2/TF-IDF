import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


public class Sax_Parser extends DefaultHandler {
	List pairs;
	private String tempVal;
	private WDPair tempWDP;
	String name;
	
	public Sax_Parser() {
		pairs = new ArrayList();
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();	
		Sax_Parser sp = new Sax_Parser();
		sp.run();
		long endTime = System.currentTimeMillis();
		System.out.println("Program running time: " + (endTime - startTime) + "ms");	
	}
	
    private void parseFile() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();			
			//parse the file and also register this class for call backs
			sp.parse("index1.xml", this);			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	
    
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// reset
		//tempVal = "";
		//if (qName.equalsIgnoreCase("n")) {
		//}

	}
    
    public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);
	}
    
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	if (qName.equalsIgnoreCase("n")) {
    		name = tempVal;
    	} else if (qName.equalsIgnoreCase("dn")) {
    		tempWDP = new WDPair();
    		tempWDP.setTerm(name);
    		tempWDP.setDocu(tempVal);
    	} 
    	  else if (qName.equalsIgnoreCase("tf-idf")) {
    		tempWDP.setScore(Double.parseDouble(tempVal));
    		pairs.add(tempWDP);
    	}
    }
    
    public void run() {
		parseFile();
				
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
		//Statement show = connection.createStatement();
		    
		try {
			if(psInsertPair!=null) psInsertPair.close();
			if (connection!=null) connection.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	
	}
    
    		
    		
    		
}
    
    
	
	
	
	
	
	
	
	
	
	
	
	
	
}
