package okta;

/*
 * Author:  Pramod Borkar
 * 
 * Date:    18-May-2014
 * 
 * Version: 1.6
 * 
 * Purpose: To demonstrate Okta API using Java for Syslog listener
 * 
 * Input:   properites.json - you must specify 
 *                      token ;
 *                      org,
 *                      syslog listener 
 *                      proxy host 
 *                      proxy port
 *
 * 
 *          * Token - this is currently created using the GUI
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


import java.io.IOException;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.net.URLEncoder;


public class oktaAPI {

    public static void main(String[] args) throws JSONException {
        
        try {
            // OktaWrapper is a class that has all of the Okta API calls that are 
            // available.
            ReadProperties rProp = new ReadProperties();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            
            cal.add(Calendar.DATE, 0);
            String timest1 = sdf.format(cal.getTime());

            String logfile = "logs/okta-syslog-"+timest1+".log";
            
            cal.add(Calendar.DATE, -1);
            String timest2 = sdf.format(cal.getTime());
            
            
            cal.add(Calendar.DATE, -1);
            String timest3 = sdf.format(cal.getTime());
           
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            rProp.println(logfile, "\n----------- TIMESTAMP: "+ sdf.format(cal.getTime()) + "---------------\n");
                          
            rProp.println(logfile,"\n----  Demonstration of Okta Events API ----");
            rProp.println(logfile, "\n* THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND");
            rProp.println(logfile, "* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF");
            rProp.println(logfile, "* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.");
            rProp.println(logfile, "* IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR");
            rProp.println(logfile, "* OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,");
            rProp.println(logfile, "* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR");
            rProp.println(logfile, "* OTHER DEALINGS IN THE SOFTWARE\n\n");
            
            OktaWrapper oWrap = new OktaWrapper(rProp.getToken(), rProp.getOrg(), rProp.getProxy(), rProp.getProxyPort(), rProp.getSyslog(), rProp.getFilter1(), rProp.getFilter2(), rProp.getFilter3(), rProp.getFilterStr(), rProp.getCEFheader(), rProp.getSyslogPort(), rProp.getSystlogInst(), rProp.getStateFile1(),rProp.getStateFile2(), rProp.getHostName(), rProp.getDebug(), rProp.getLimit(), rProp.getFlushtime(), rProp.numPagination());
            
            
            //rProp.rollLogs(timest1, timest2, timest3, logfile);
            rProp.println(logfile, "----      Listing events: ----");
            //sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            //cal = Calendar.getInstance();
            //Date d = sdf.parse("2014-05-08 12:51:11.000");
            //cal.setTime(d);
            //cal.add(Calendar.DATE, 1);
            //cal.add(Calendar.MINUTE , - Integer.parseInt(rProp.getLogTime()));
            
            
            
            oWrap.getEvents();
            //Iterator usersIterator = users.listIterator();
            //while (usersIterator.hasNext()) {
            // thisPerson = (OktaPerson) usersIterator.next();
            //oHelp.printUser( thisPerson );
            //}
            
        
        } catch (OktaException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (InvalidField ex) {
           ex.printStackTrace();
        }catch (InvalidExtensionKey ex) {
            ex.printStackTrace();
        
        }
    }
    
    
    
}
