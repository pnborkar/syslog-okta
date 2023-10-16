Code: 		Okta Syslog Connector code

Author: 	Pramod Borkar

Date:   	19-May-2014

Version:        V1.0

Purpose:	This code demonstrates using the Okta Events API using JAVA and send the log to a syslog listener.



Comments:	The oktaAPI.java file contains the main class and has several method calls commented out. OktaWrapper has Events call which calls Okta API and parses the information


		If you uncomment the sections and recompile the code you will be able to see how those
		sections work as well.

		For this code to work in your org you will need to create a token and update the 
                
	       
		properties.json file has following keys which are important:

	Example would be : 

  	"token": "00imHMYbobR_TAj4y5G2zr_5GSdQANwC0v6-Ch2SJU", /* API token */
    	"org": "oktademo.okta.com", /* the org this will be used to build the URL */
    	"syslog": "fmvdclsrcl100.example.com", /* Syslog listener */
   	"start_time": "2013-11-22", /* Start time of Okta events  */
   	"proxy": "",/* Proxy host */
   	"proxy_port": "912",
    	"min_from_current_to_log": "10", /* Number of minutes from current to log */
    	"cef_header": "local0.info vmsokta01.example.com OKTA:"


	Also you will need to find an ID for one of your users to see the components work.



To Compile the Code:	
-----------------------

  cd > to current folder 

  rm -rf okta/*.class
  javac -cp okta-jars/*:. okta/*


To Execute the code:
-----------------------

  java -cp okta-jars/*:. okta/oktaAPI

  
******* 
NOTE: This code is provided as an example.
 
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE
