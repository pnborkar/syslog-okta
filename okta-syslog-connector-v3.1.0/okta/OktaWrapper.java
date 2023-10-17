/*
 * Author:  Pramod Borkar
 * 
 * Date:    19-May-2014
 * 
 * Version: 1.6
 * 
 * Purpose: This classes wraps the Okta API calls so that they are available to JAVA.
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
 *
 */
package okta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URLEncoder;



import java.util.HashMap;
import java.util.Map;

// Http Proxy
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.HttpHost;
import org.apache.http.HeaderElement;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import org.apache.http.conn.params.ConnRoutePNames;

//Syslog
import org.graylog2.syslog4j.Syslog;
import org.graylog2.syslog4j.SyslogIF;
import org.graylog2.syslog4j.impl.net.tcp.ssl.SSLTCPNetSyslogConfig;
import org.graylog2.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.graylog2.syslog4j.impl.net.tcp.TCPNetSyslogConfigIF;
import org.graylog2.syslog4j.impl.AbstractSyslogConfigIF;
import org.graylog2.syslog4j.impl.AbstractSyslogConfig;
import org.graylog2.syslog4j.SyslogConfigIF;
import org.graylog2.syslog4j.SyslogMessageIF;
import org.graylog2.syslog4j.SyslogMessageProcessorIF;
import org.graylog2.syslog4j.impl.message.processor.SyslogMessageProcessor;
import org.graylog2.syslog4j.SyslogConstants;
import java.lang.InterruptedException;

// new imports
import org.graylog2.syslog4j.impl.net.tcp.TCPNetSyslogWriter;


//Date format

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

//
import java.net.URLEncoder;

public class OktaWrapper {

    ReadProperties myProps;     // properties from properties file
    HttpResponse response;      // response from http request
    String line;                // temporary variable to hold returned data
    JSONArray jArray;           // JSONArray used through out the code
    JSONObject thisObj;         // JSONObject used through out the code
    String thisLine;            // temporary variable to hold returned data
    HttpGet get;                // get request
    HttpDelete delRequest;      // del request
    HttpPost post;              // post request
    HttpPut put;                // put request
    HttpClient client;          // client used throughout
    private String token;       // token read from properties file
    private String org;         // org read from properties file
    String thisUserID;          // user ID of current object
    String thisKey;             // name of the current key we are retrieving
    
    //
    String PROXY;               // proxy to connect to
    Integer PROXY_PORT ;        // proxy port
    SyslogIF syslog ;
    SyslogConfigIF syslogconfig;
    SyslogMessageIF sysmessage ;
    SyslogMessageProcessor syslogMsgProc ;
    SyslogConstants Sysconstants;
    
    String cef_header;
    Integer syslog_port;
    String sysloginstance = "tcp";
    ReadState rState  ;
    
    String state_file, state_file2;
    String startTime;
    String endTime;
    
    String debug ;
    String limit = "1000";
    String logfile ;
    SimpleDateFormat sdf ;
    Calendar cal;
    String timestamp;
    TCPNetSyslogWriter writer;
    Integer flush_interval = 1000;
    boolean ufilter1 = false;
    boolean ufilter2 = false;
    boolean ufilter3 = false;
    String num_Pagination = "5";
    String filter_str = "";
    
    public OktaWrapper(String thisToken, String thisOrg, String thisProxy, String thisProxyPort, String sysloghost, String filter1, String filter2, String filter3, String filterstr, String cefheader, String syslogport, String sysloginst, String statefile, String statefile2, String hostName, String thisdebug, String thislimit, String flush_time, String numPagination) throws JSONException {
       
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal = Calendar.getInstance();
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        cal.add(Calendar.DATE, 0);
        //cal.add(Calendar.MINUTE , - minutes_from_current);
        logfile = sdf.format(cal.getTime());
        logfile = "logs/okta-syslog-"+logfile+".log";
        
        
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = sdf.format(cal.getTime());
        
        token = thisToken;
        debug = thisdebug;
        
        if ( thislimit.length() > 0 ) {
            limit = thislimit;
        }
        num_Pagination = numPagination;
        
        // org = myProps.getOrg();
        state_file = statefile;
        state_file2 = statefile2;
        rState = new ReadState(state_file, statefile2);
        System.out.println("rstate file :: "+rState);
        org = thisOrg;
        PROXY = thisProxy;
        if (thisProxyPort != "") {
            PROXY_PORT = Integer.parseInt(thisProxyPort);
        }
        if (sysloginst.length() > 0) {
            sysloginstance = sysloginst;
            
        }
        
        if ( flush_time != "") {
            flush_interval = Integer.parseInt(flush_time);
        }
      
        System.out.println("numPagination  :: "+numPagination);

        System.out.println("flush_interval  :: "+flush_interval);
        // Set a Specific Host, then Log to It
        if (sysloghost.length() > 0 ) {
            if (sysloginstance.equalsIgnoreCase("udp")) {
                syslog = Syslog.getInstance(sysloginstance);
            } else {
                // Get the TCPNetWriter here, and get TCPNetSyslogConfig
                
                TCPNetSyslogConfigIF config = new TCPNetSyslogConfig(hostName);
                config.setSetBufferSize(true);
                //config.setSoLingerSeconds(2);
                //System.out.println ("seconds:: " + config.getSoLingerSeconds() );
                //config.setFreshConnectionInterval (2);
                Syslog.destroyInstance("tcp");
                syslog = Syslog.createInstance("tcp",config);
            }
            
            syslog.getConfig().setHost(sysloghost);
            syslog.getConfig().setLocalName(hostName);
            
            if (syslogport.equals("") || syslogport == null ) {
                syslog_port = 514;
            }else {
                syslog_port = Integer.parseInt(syslogport);
                
            }

            syslog.getConfig().setPort(syslog_port);            
            //rState.println(logfile, "\n:: SYSLOG PORT : " + syslog_port, timestamp );
                    }
        
        //syslogMsgProc = SyslogMessageProcessor.getDefault();
        //syslogMsgProc.createSyslogHeader(Sysconstants.FACILITY_AUTH, Sysconstants.LEVEL_INFO, hostName, false, false);
        //syslog.setStructuredMessageProcessor(syslogMsgProc);
        
        cef_header = cefheader;
        
        if (debug.equals("true")) rState.println(logfile, "----      SYSLOG INSTANCE>> " + sysloginstance + "  -----" , timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      Start Time >>   " + rState.getStartTime() + "  -----", timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      End Time >>     " + rState.getEndTime() + "  -----", timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      Host Name >>    " + hostName + "  -----",timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      SYSLOG Facility (FACILITY_AUTH): " + Sysconstants.FACILITY_AUTH + " -----", timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      SYSLOG Level (LEVEL_INFO): " + Sysconstants.LEVEL_INFO + " -----", timestamp);

        //rState.writeToStatefile (state_file, "startokok", "okok");
        startTime = rState.getStartTime();
        endTime  = rState.getEndTime();
        if ( filter1.equalsIgnoreCase("on") || filter1.equalsIgnoreCase("true")) {
            ufilter1 = true;
        }
        if ( filter2.equalsIgnoreCase("on") || filter2.equalsIgnoreCase("true")) {
            ufilter2 = true;
        }
        if ( filter3.equalsIgnoreCase("on") || filter3.equalsIgnoreCase("true")) {
            ufilter3 = true;
        }
        
        filter_str = filterstr;
        
    }

    public String getEvents() throws OktaException, JSONException, IOException, InvalidField, InvalidExtensionKey {
        /* 
         * Method: getEvents
         *  
         * Purpose: Returns an array of all users
         *   
         */
        
        //ArrayList events = new ArrayList();
        
        String ex_filter = "";
        String cur_filter = ""; // Current filter
        String next_filter = ""; // next pagination
        client = new DefaultHttpClient();

        thisUserID = "";                  // Current found User ID
        
        //Get the current time
      
        String startTime_tolog = "" ;
        String starttime_tostatelog = "";
        if ( endTime.length() > 0 ) {
            starttime_tostatelog = endTime;
            startTime_tolog = "published gt \""+ endTime +"\"";
        }else {
            starttime_tostatelog = startTime;
            startTime_tolog = "published gt \""+ startTime +"\"";
        }
        rState.println(logfile, "----      Events filter: ------ " +startTime_tolog , timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      Debug is:   " + debug + "  -----", timestamp);
        if (debug.equals("true")) rState.println(logfile, "----      Limit logs to:   " + limit + "  -----", timestamp);
        
        ex_filter = URLEncoder.encode(startTime_tolog);
        
        
        
        String filter1 = "action.objectType+eq+%22app.generic.config.app_activated%22+or+action.objectType+eq+%22app.generic.config.app_deactivated%22+or+action.objectType+eq+%22app.generic.import.provisioning_data%22+or+action.objectType+eq+%22app.generic.import.import_user%22+or+action.objectType+eq+%22app.generic.config.app_updated%22+or+action.objectType+eq+%22app.generic.import.new_user%22+or+action.objectType+eq+%22app.generic.import.user_update%22+or+action.objectType+eq+%22app.generic.config.app_username_update%22+or+action.objectType+eq+%22app.generic.config.app_password_update%22+or+action.objectType+eq+%22app.generic.import.user_delete%22+or+action.objectType+eq+%22app.generic.import.started%22+or+action.objectType+eq+%22app.generic.import.complete%22+or+action.objectType+eq+%22app.generic.import.user_match.complete%22+or+action.objectType+eq+%22app.generic.import.details.add_custom_object%22+or+action.objectType+eq+%22app.generic.import.details.update_custom_object%22+or+action.objectType+eq+%22app.generic.import.details.delete_custom_object%22+or+action.objectType+eq+%22app.generic.import.details.add_user%22+or+action.objectType+eq+%22app.generic.import.details.update_user%22+or+action.objectType+eq+%22app.generic.import.details.delete_user%22+or+action.objectType+eq+%22app.generic.import.details.add_group%22+or+action.objectType+eq+%22app.generic.import.details.update_group%22+or+action.objectType+eq+%22app.generic.import.details.delete_group%22+or+action.objectType+eq+%22app.generic.import.summary.custom_object%22+or+action.objectType+eq+%22app.generic.import.summary.user%22+or+action.objectType+eq+%22app.generic.import.summary.group%22+or+action.objectType+eq+%22app.generic.import.summary.group_membership%22+or+action.objectType+eq+%22app.auth.sso%22+or+action.objectType+eq+%22app.auth.delegated.outbound%22+or+action.objectType+eq+%22app.user_management.push_password_update%22+or+action.objectType+eq+%22app.user_management.push_profile_success%22+or+action.objectType+eq+%22app.user_management.push_profile_failure%22+or+action.objectType+eq+%22app.user_management.push_new_user%22+or+action.objectType+eq+%22app.user_management.push_pending_user%22+or+action.objectType+eq+%22app.user_management.provision_user%22+or+action.objectType+eq+%22app.user_management.provision_user_failed%22+or+action.objectType+eq+%22app.user_management.importing_profile%22+or+action.objectType+eq+%22app.user_management.update_from_master_failed%22+or+action.objectType+eq+%22app.user_management.verified_user_with_thirdparty%22+or+action.objectType+eq+%22app.user_management.updating_api_credentials_for_password_change%22+or+action.objectType+eq+%22app.user_management.activate_user%22+or+action.objectType+eq+%22app.user_management.deactivate_user%22+or+action.objectType+eq+%22app.user_management.reactivate_user%22+or+action.objectType+eq+%22app.user_management.provision_user.user_inactive%22+or+action.objectType+eq+%22app.user_management.deprovision_task_complete%22";
        
        String filter2 = "action.objectType+eq+%22core.user.sms.message_sent.factor%22+or+action.objectType+eq+%22core.user.sms.message_sent.verify%22+or+action.objectType+eq+%22core.user.sms.message_sent.forgotpw%22+or+action.objectType+eq+%22core.user_auth.radius.login.succeeded%22+or+action.objectType+eq+%22core.user_auth.radius.login.failed%22+or+action.objectType+eq+%22core.user.config.password_update.success%22+or+action.objectType+eq+%22core.user.config.password_update.failure%22+or+action.objectType+eq+%22core.user.config.user_activated%22+or+action.objectType+eq+%22core.user.config.user_deactivated%22+or+action.objectType+eq+%22core.user.config.user_status.password_reset%22+or+action.objectType+eq+%22core.user.config.user_creation.success%22+or+action.objectType+eq+%22core.user.config.user_creation.failure%22+or+action.objectType+eq+%22core.user.impersonation.session.initiated%22+or+action.objectType+eq+%22core.user.impersonation.session.ended%22+or+action.objectType+eq+%22core.user.impersonation.grant.enabled%22+or+action.objectType+eq+%22core.user.impersonation.grant.extended%22+or+action.objectType+eq+%22core.user.impersonation.grant.revoked%22+or+action.objectType+eq+%22core.user.admin_privilege.granted%22+or+action.objectType+eq+%22core.user.admin_privilege.revoked%22+or+action.objectType+eq+%22app.generic.reversibility.credentials.recover%22+or+action.objectType+eq+%22app.generic.reversibility.personal.app.recovery%22+or+action.objectType+eq+%22app.generic.reversibility.individual.app.recovery%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.deactivated.source.group.renamed%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.deactivated.source.group.renamed.failed%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.app.group.renamed%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.app.group.renamed.failed%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.and.groups.deleted.rule.deleted%22+or+action.objectType+eq+%22app.admin.sso.no_response%22+or+action.objectType+eq+%22app.admin.sso.bad_response%22+or+action.objectType+eq+%22app.admin.sso.orgapp.notfound%22+or+action.objectType+eq+%22app.user_management.app_group_group_member_import.insert_success%22+or+action.objectType+eq+%22app.user_management.user_group_import.upsert_success%22+or+action.objectType+eq+%22app.inbound_del_auth.failure.invalid_login_credentials%22+or+action.objectType+eq+%22app.inbound_del_auth.login_success%22+or+action.objectType+eq+%22core.user_auth.login_failed%22+or+action.objectType+eq+%22core.user_auth.login_success%22+or+action.objectType+eq+%22core.user_auth.logout_success%22+or+action.objectType+eq+%22core.user_auth.account_locked%22";
       
       
        String filter3="action.objectType+eq+%22app.generic.provision.assign_user_to_app%22+or+action.objectType+eq+%22app.generic.provision.deactivate_user_from_app%22+or+action.objectType+eq+%22app.rich_client.instance_not_found%22+or+action.objectType+eq+%22app.rich_client.account_not_found%22+or+action.objectType+eq+%22app.rich_client.multiple_accounts_found%22+or+action.objectType+eq+%22app.rich_client.login_failure%22+or+action.objectType+eq+%22app.rich_client.login_success%22+or+action.objectType+eq+%22app.app_instance.change%22+or+action.objectType+eq+%22app.app_instance.logo_update%22+or+action.objectType+eq+%22app.app_instance.logo_reset%22+or+action.objectType+eq+%22app.app_instance.outbound_delauth_enabled%22+or+action.objectType+eq+%22app.app_instance.outbound_delauth_disabled%22+or+action.objectType+eq+%22app.app_instance.config-error%22+or+action.objectType+eq+%22app.inbound_del_auth.failure.not_supported%22+or+action.objectType+eq+%22app.inbound_del_auth.failure.instance_not_found%22+or+action.objectType+eq+%22app.inbound_del_auth.failure.invalid_request.could_not_parse_credentials%22+or+action.objectType+eq+%22app.inbound_del_auth.failure.account_not_found%22+or+action.objectType+eq+%22core.user_auth.session_expired%22+or+action.objectType+eq+%22core.user_auth.mfa_bypass_attempted%22+or+action.objectType+eq+%22app.user_management.deactivate_user.api_account%22+or+action.objectType+eq+%22app.user_management.app_group_group_member_import.delete_success%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.error.duplicate%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.warning.duplicate.name%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.warning.duplicate.name.tobecreated%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.warning.upsertGroup.duplicate.name%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.error.validation%22+or+action.objectType+eq+%22app.user_management.grouppush.mapping.created.from.rule.errors%22+or+action.objectType+eq+%22app.user_management.user_group_import.delete_success%22+or+action.objectType+eq+%22app.user_management.app_group_member_import.insert_success%22+or+action.objectType+eq+%22app.user_management.app_group_member_import.delete_success%22+or+action.objectType+eq+%22app.user_management.app_group_group_member_import.insert_success%22+or+action.objectType+eq+%22app.user_management.app_group_group_member_import.delete_success%22";
 
        
        if (ufilter1) {
            if ( filter_str.equals("")) {
                ex_filter = ex_filter + "+and+" + filter1;
            }else{
                ex_filter = ex_filter + "+and+" + filter_str;
            }
        } else if(ufilter2) {
            ex_filter = ex_filter + "+and+" + filter2;
        } else if(ufilter3) {
             ex_filter = ex_filter + "+and+" + filter3;
        }else{
            ex_filter = ex_filter;
        }
        System.out.println ( "** Exfilter " + ex_filter);
        
        cur_filter = "https://" + org + "/api/v1/events?filter="+ex_filter+ "&limit="+limit;
        next_filter = getHttpResponseforEvents (cur_filter, timestamp, starttime_tostatelog);
        
        int y = 1;
        while (next_filter != "") {
            System.out.println ( "\n\n>>> ***  # Filter " + y);
            next_filter = getHttpResponseforEvents (next_filter, timestamp, starttime_tostatelog);
            if ( !num_Pagination.equals("all")) {
                if ( Integer.parseInt(num_Pagination) == y) {
                    System.out.println ( "\n\n>>> ***  Reached Max# filters " + y + " **** \n\n");
                    break;
                }
            }
            y = y+1;
                
        }
               
        return "ok";
    }

    public String getHttpResponseforEvents(String filter, String timestamp, String starttime_tostatelog) throws OktaException, JSONException, IOException, InvalidField, InvalidExtensionKey {
        CEF cef;
        String nextFilter = "";
        System.out.println ( "\n *** Current Filter ***  " + filter);

        try {
            
            // Build the get object for getting events
            get = new HttpGet(filter);
            
            // Add necessary header variables as defined in the API
            get.addHeader("Authorization", "SSWS " + token);
            get.addHeader("Accept", "application/json");
            get.addHeader("Content-type", "application/json");
            if ( PROXY.length() > 0) {
                rState.println(logfile, "\n----      Setting Proxy: "+ PROXY + ":" + PROXY_PORT + "  ---- \n ", timestamp);
                HttpHost proxy = new HttpHost(PROXY, PROXY_PORT, "http");
                client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
            
            //Credentials credentials = new UsernamePasswordCredentials("", "");
            //AuthScope authScope = new AuthScope(PROXY, PROXY_PORT);
            
            // Make the request to Okta
            response = client.execute(get);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            
            
            Header[] headers = response.getHeaders("Link");
            
            int z = 0;
            while ( z < headers.length ) {
                String tmp =headers[z].getValue();
                if ( (tmp.contains("rel=\"next")) ) {
                    nextFilter = tmp.substring(1,tmp.indexOf(">;"));
                    break;
                }
                z = z+1;
                
            }
            
            // while there is more input on the returned stream read it
            //boolean goNext = false;
            
            
            while ((line = rd.readLine()) != null) {
                String okta_msg = "";
                
                // Check for error
                if (line.contains("errorCode")) {
                    // error must have occurred
                    // have to reformat line to extract error
                    line = "[" + line + "]";
                }
                jArray = new JSONArray(line);
                //rState.println(logfile, line + "...................\n\n", timestamp);
                
                // loop through all of the rows in the array
                int k = 0;
                rState.println(logfile, "----   #OKTA Events: >>   " + jArray.length() + "  -----", timestamp);
                
                while (k < jArray.length()) {
                    //goNext = false;
                    Map<String, String> extensionMap = new HashMap<String, String>();
                    
                    // Convert this row into a JSON Object
                    thisObj = jArray.getJSONObject(k++);
                    
                    // check for error
                    if (!"0".equals(check4Error(thisObj).getErrorCode())) {
                        throw check4Error(thisObj);
                    }
                    
                    // the returned JSON string has a profile in it . Get that profile
                    JSONObject thisProfile = thisObj.getJSONObject("action");
                    //rState.println(logfile,  thisObj.get("eventId") + "   ---------\n",timestamp);
                    Iterator keys = thisProfile.keys();
                    //
                    // loop through all of the fields
                    while (keys.hasNext()) {
                        thisKey = (String) keys.next();
                        
                        if ( thisKey.equals ("categories")) {
                            //rState.println(logfile, "::: " + thisKey + "," + thisProfile.getString(thisKey) + "   ---------\n", timestamp);
                            String[] splitArray = thisProfile.getString(thisKey).split("\"");
                            if  (splitArray.length > 1 ) {
                                okta_msg = splitArray[1];
                                extensionMap.put("cat", okta_msg );
                                
                                //rState.println(logfile, "::: " + splitArray[1] + "   ---------\n");
                            }
                        }
                        if ( thisKey.equals ("requestUri")) { extensionMap.put("request", (String) thisProfile.getString(thisKey)); }
                        if ( thisKey.equals ("message")) {
                            extensionMap.put("msg", (String) thisProfile.getString(thisKey));
                        }
                    }
                    
                    
                    JSONArray jArray1 = thisObj.getJSONArray("actors");
                    
                    int k1 = 0;
                    while (k1 < jArray1.length()) {
                        JSONObject thisObjActor = jArray1.getJSONObject(k1++);
                        //System.out.println ("Actor: " + thisObjActor.get("displayName") +"\n");
                        //System.out.println ("Actor: " + thisObjActor.get("objectType") +"\n");
                        //System.out.println ("Actor: " + thisObjActor.get("id") +"\n");
                        if (thisObjActor.get("objectType").equals("User") ) {
                            extensionMap.put("suser", (String) thisObjActor.get("displayName") );
                            extensionMap.put("suid", (String) thisObjActor.get("login") );
                        }
                        if (thisObjActor.get("objectType").equals("Client") ) {
                            extensionMap.put("src", (String) thisObjActor.get("ipAddress") );
                            extensionMap.put("cs2", (String) thisObjActor.get("displayName") );
                            extensionMap.put("cs2Label", "srcClient" );
                            extensionMap.put("cs3", (String) thisObjActor.get("id") );
                            extensionMap.put("cs3Label", "srcClientId");// Need to watch for this
                        }
                    }
                    
                    JSONArray jArray2 = thisObj.getJSONArray("targets");
                    
                    int k2 = 0;
                    while (k2 < jArray2.length()) {
                        JSONObject thisObjTarget = jArray2.getJSONObject(k2++);
                        //System.out.println ("Target: " + thisObjTarget.get("displayName") +"\n");
                        //System.out.println ("Target: " + thisObjTarget.get("objectType") +"\n");
                        //System.out.println ("Target: " + thisObjTarget.get("id") +"\n");
                        if (thisObjTarget.get("objectType").equals("User") ) {
                            extensionMap.put("duid", (String) thisObjTarget.get("login") );
                            extensionMap.put("duser", (String) thisObjTarget.get("displayName") );
                            
                        }
                        if (thisObjTarget.get("objectType").equals("AppInstance") ) {
                            //extensionMap.put("suid", (String) thisObjTarget.get("id") );
                            extensionMap.put("destinationServiceName", (String) thisObjTarget.get("displayName") );
                        }
                        
                        
                    }
                    //System.out.println ("\n-- -- -- \n");
                    
                    // * Get the CEF format for the event
                    
                    
                    String  eventstr = ""+thisObj.get("eventId");
                    String publishrawtime = ""+thisObj.get("published");
                    //System.out.println ( "\n:: "+publishtime);
                    String publishtime = publishrawtime.substring(0, 19).replace("T", " ");
                    //System.out.println ( "\n:: "+publishtime);
                    
                    
                    //Changing the format of the publish time
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d = sdf.parse(publishtime);
                    cal.setTime(d);
                    sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
                    String eventStarttime = sdf.format(cal.getTime());
                    
                    //rState.println(logfile, "\n------  " + publishtime + "/:/" + eventStarttime);
                    //extensionMap.put("cs1Label", "eventID");
                    extensionMap.put("externalId", eventstr );
                    
                    extensionMap.put("start", eventStarttime );
                    //extensionMap.put("externalID", (String)thisObj.get("sessionId") );
                    
                    Extension extension = new Extension( extensionMap );
                    cef = new CEF(0, "Okta", "Okta Identity Provider", "OKTA.AD.3.0.6.1",  eventstr,  okta_msg , 1,extension );
                    
                    //System.out.println ("\n-- "+cef.toString() + " ----\n");
                    rState.println(logfile, "----  #Event >>   " + k + " : " + eventstr +  "  -----", timestamp);
                    if (debug.equals("true")) rState.println(logfile, "--- "+publishtime + " " + cef_header + " " + cef.toString() + "\n\n" , timestamp) ;
                        //Send to syslog listener
                        //sysmessage.createMessage(publishtime + " " + cef_header + " " + cef.toString());
                        
                        //Sleep for sometime, before sending syslog.info
                        // 250 milliseconds
                        System.out.println ("\n---- Before flush: " + k);
                        syslog.info(cef_header + " " + cef.toString());
                        Thread.sleep(100);
                        Thread.sleep(flush_interval);
                        //if (sysloginstance.equalsIgnoreCase("tcp")) {
                        
                        syslog.flush();
                        
                        if ( k == jArray.length() ) {
                            System.out.println ("\n---- Making last syslog entry before shutdown ----- ");
                            syslog.info(cef_header + " " + cef.toString());
                            syslog.flush();
                        }
                    //}
                    //Thread.sleep(125);
                    //System.out.println ("after flush : " + k);
                    if ( k == jArray.length() ) {
                        
                        
                        rState.println(logfile, "\n----      LAST ENTRY EVENT TIME:> --- "+publishrawtime +"\n", timestamp);
                        rState.println(logfile, "\n----      WRITING TO LOG STATE FILES : --- "+state_file + " ::2nd:: " + state_file2 + "\n", timestamp);
                        rState.writeToStatefile (state_file,state_file2, starttime_tostatelog, publishrawtime, logfile, timestamp);
                        
                    }
                    
                } // while2 loop
                
                // loop through all of the rows in the array
                rState.println(logfile, "\n=================================================<EOM>\n", timestamp);
            }
            
        } catch (JSONException ex) {
            System.out.println ("\n(ERROR)----- JSON ------ " );
            ex.printStackTrace();
            rState.println(logfile, "JSONEx: "+ ex.toString(), timestamp);
            throw ex;
            
        } catch (IOException ex) {
            System.out.println ("\n(ERROR)----- IO ------ " );
            rState.println(logfile, "IOException: "+ ex.toString(), timestamp);
            ex.printStackTrace();
            throw ex;
            
        }catch (InvalidField invalidkey) {
            System.out.println ("\n(ERROR)----- InvlidField ------ " );
            invalidkey.printStackTrace();
            
            throw invalidkey;
            
        }catch (InvalidExtensionKey invalidkey) {
            System.out.println ("\n(ERROR)----- InvlidFieldExtn ------ " );
            invalidkey.printStackTrace();
            throw invalidkey;
            
        }catch (ParseException parseex) {
            System.out.println ("\n(ERROR)----- ParseException ------ " );
            rState.println(logfile, "ParseEx: "+ parseex.toString(), timestamp);
            parseex.printStackTrace();
            
        }
        catch (InterruptedException ex) {
            System.out.println ("\n(ERROR)----- InterruptedException ------ " );
            ex.printStackTrace();
            
        } finally {
            System.out.println ("\n----- Syslog Shutdown ------ *");
            
            syslog.shutdown();
        }
        return nextFilter;
    }
    
    
 
    public OktaException check4Error(JSONObject thisObj) {
        /* 
         * Method: check4Error
         *  
         * Purpose: This method checks to see if an error has occurred. It returns an
         *          OktaException (This is my exception)
         * 
         * Parameters:
         * 
         *   thisObj  - The current JSONObject that might contain an error code.
         *   
         */
        int error;
        String errorCode = "0";
        String errorMsg;
        try {
            // see if the errorCode key exists. If it does we have had an error.
            // return the error. If we have not had an error the key will not be 
            // found and the catch block will be exectued below
            errorCode = thisObj.getString("errorCode");
            JSONArray errorArray = thisObj.getJSONArray("errorCauses");

            int k = 0;
            JSONObject errorObject;

            StringBuffer errorMsgBuf = new StringBuffer("");
            while (k < errorArray.length()) {
                errorObject = errorArray.getJSONObject(k++);
                errorMsgBuf.append(errorObject.getString("errorSummary"));
            }
            errorMsg = errorMsgBuf.toString();
            if (errorMsg.trim().length() == 0) {
                errorMsg = thisObj.getString("errorSummary");
            }

            error = 0;
            return new OktaException(errorCode, errorMsg);
        } catch (JSONException ex) {
            // This mean that no error occurred!!

            return new OktaException("0", "OK");
        }
    }
}
