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
 *          Org   - the organization like acme.okta.com would be acme
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
import java.net.URLEncoder;


/**
 *
 * @author pborkar
 */
public class ReadState {

    JSONObject props = null;
// init method to read property file
    public ReadState(String filename, String secondfile) throws JSONException {
        File file = new File(filename);
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
                }else {
                    
                    System.out.println("\n" + filename+ " is null. Reading from "+ secondfile);
                    read2file(secondfile);
                    return ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        props = new JSONObject(contents.toString());
    }
    
    public JSONObject read2file(String filename) throws JSONException {
        File file = new File(filename);
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
        return props = new JSONObject(contents.toString());
    }

    
    public String getStartTime() throws JSONException {
        return props.getString("start");
    }

    public String getEndTime() throws JSONException {
        return props.getString("end");
    }
    
    public String writeToStatefile(String filename, String secondfile, String starttime, String endtime, String logfile, String timestamp) throws JSONException {
        File file = new File(filename);
        File file2 = new File(secondfile);
        BufferedWriter writer = null;
        BufferedWriter writer2 = null;
        
        try {
            println (logfile, "Writing to 1st file : "+ filename , timestamp);
            writer = new BufferedWriter(new FileWriter(file));
            String text = null;
            writer.write("\n{\n");
            writer.write("\n"+"  \"start\": \""+starttime+"\",");
            writer.write("\n"+"  \"end\": \""+endtime+"\"\n");
            writer.write("\n}\n");
            
            System.out.println("\n Writing to 2nd file : "+ secondfile);
            println (logfile, "Writing to 2nd file : "+ secondfile , timestamp);

            writer2 = new BufferedWriter(new FileWriter(file2));
            text = null;
            writer2.write("\n{\n");
            writer2.write("\n"+"  \"start\": \""+starttime+"\",");
            writer2.write("\n"+"  \"end\": \""+endtime+"\"\n");
            writer2.write("\n}\n");

        } catch (IOException e) {
            e.printStackTrace();
            println (logfile, e.toString(), timestamp);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (writer2 != null) {
                    writer2.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "done";
        
    }
    
    public String println(String filename, String message, String timestamp) throws JSONException {
        File file = new File(filename);
        BufferedWriter writer = null;
        
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            String text = null;
            writer.write("\n ("+ timestamp  + "): " + message);
            
            
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

   
    
  
    
    
}
