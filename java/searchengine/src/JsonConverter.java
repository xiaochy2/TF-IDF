import java.io.*;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class JsonConverter {
	public static String Url(String filename) throws IOException {
        File file = new File("/SearchEngine/bookkeeping.json");
        String content = FileUtils.readFileToString(file);
        
        JSONObject obj = new JSONObject(content);
        String absolute = "http://" + obj.getString(filename); 
        return absolute;
        
    }
		
}
