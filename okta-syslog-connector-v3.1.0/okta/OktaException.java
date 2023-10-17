/*
 * Author:  Jeff Nester
 * 
 * Date:    18-Dec-2013
 * 
 * Version: 1.6
 * 
 * Purpose: Okta Exception class
 * 
/*
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

/**
 *
 * @author jnester
 */
public class OktaException extends Throwable{
    String errorCode;
    String errorSummary;
    public OktaException ( String errCode, String summary)  {
        errorCode = errCode;
        errorSummary = summary;
    }
    public String getErrorCode (){
        return errorCode;
    }
    public String getErrorDescription (){
        return errorSummary;      
    }
}
