import java.io.*;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

public class JsonConverter {
	public static String Url(String filename) throws IOException {
        File file = new File("bookkeeping.json");
        String content = FileUtils.readFileToString(file);
        
        JSONObject obj = new JSONObject(content);
        return obj.getString(filename);       
    }
}
