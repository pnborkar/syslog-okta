/*
 * Author:  Pramod Borkar
 * 
 * Date:    18-May-2014
 * 
 * Version: 1.0
 * 
 * Purpose: This class reads the properties file and makes the values available 
 *          to the main program.
 * 
 * Input:   properites.json - you must specify the token and org in the json 
 *          file before running this code
 * 
 *          Token - this is currently created using the GUI
 *          Org   - the organization like intel.okta.com would be intel
 * 
 * NOTE: This code is provided as an example.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE
 *
 */
package okta;

import java.io.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author pborkar
 */
public class ReadProperties {

    JSONObject props = null;
// init method to read property file
    public ReadProperties() throws JSONException {
        File file = new File("properties.json");
        StringBuilder contents = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            // repeat until all lines is read
            while ((text = reader.readLine()) != null) {
                contents.append(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        props = new JSONObject(contents.toString());
    }

    
    public String getToken() throws JSONException {
        // getToken - returns the token read from the property file
        return props.getString("token");
    }

    public String getOrg() throws JSONException {
        // getOrg - returns the org read from the property file
        return props.getString("org");
    }
    
    public String getProxy() throws JSONException {
        // getOrg - returns the Proxy host from the property file
        return props.getString("proxy");
    }
    
    public String getProxyPort() throws JSONException {
        // getOrg - returns the Proxy port from the property file
        return props.getString("proxy_port");
    }
    public String getSyslog() throws JSONException {
        // getOrg - returns the Syslog listener from the property file
        return props.getString("syslog");
    }
    public String getFilter1() throws JSONException {
        //
        return props.getString("filter1");
    }
    public String getFilter2() throws JSONException {
        //
        return props.getString("filter2");
    }
    public String getFilter3() throws JSONException {
        //
        return props.getString("filter3");
    }
    public String getFilterStr() throws JSONException {
        //
        return props.getString("filter_str");
    }
    public String getCEFheader() throws JSONException {
        // getOrg - returns the #min to log from current* from the property file
        return props.getString("cef_header");
    }
    
    public String getSyslogPort() throws JSONException {
        // getOrg - returns the #min to log from current* from the property file
        return props.getString("syslog_port");
    }
    
    public String getSystlogInst() throws JSONException {
        // getOrg - returns the #min to log from current* from the property file
        return props.getString("syslog_instance");
    }
    
    public String getStateFile1() throws JSONException {
        return props.getString("state_file_1");
    }
    
    public String getStateFile2() throws JSONException {
        return props.getString("state_file_2");
    }
    
    public String getHostName() throws JSONException {
        return props.getString("host_name");
    }
    
    public String getDebug() throws JSONException {
        return props.getString("debug");
    }
    
    public String getLimit() throws JSONException {
        return props.getString("limit_logs");
    }
    
    public String getFlushtime() throws JSONException {
        return props.getString("flush_interval");
    }
    public String numPagination() throws JSONException {
        return props.getString("number_pagination");
    }
    
    
    
    
    public String println(String filename, String message) throws JSONException {
        File file = new File(filename);
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            String text = null;
            writer.write("\n "+ message);
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "done";
        
    }
    
    public String rollLogs(String timestamp1, String timestamp2, String timestamp3, String filename) throws JSONException {

    // Directory path here
    String path = "logs";
    
    String files;
    File folder = new File(path);
    File[] listOfFiles = folder.listFiles();
    
    for (int i = 0; i < listOfFiles.length; i++)
    {
        
        if (listOfFiles[i].isFile())
        {
            files = listOfFiles[i].getName();
            if ( files.contains (timestamp1) || files.contains (timestamp2) || files.contains (timestamp3)) {
                println (filename, "----    * Keeping file * " + files);
            }else {
                
                listOfFiles[i].delete();
                println (filename, "----    *** DELETING file: *** " + files);
            }
            
            
        }
    }
      return "done";
    }
    
    
       
    
    
}
