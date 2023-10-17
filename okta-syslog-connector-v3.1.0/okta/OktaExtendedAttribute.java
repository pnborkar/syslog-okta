/*
 * Author:  Jeff Nester
 * 
 * Date:    18-Dec-2013
 * 
 * Version: 1.6
 * 
 * Purpose: Okta Extended attributes class
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
public class OktaExtendedAttribute {
    private String a_name = "";           
    private String a_value = "";   
    
    public OktaExtendedAttribute() {
        a_name = "";
        a_value = "";
    }
    public OktaExtendedAttribute( String name, String value ) {
        a_name = name;
        a_value = value;
    }
    public void setName ( String name ) {
        a_name = name;
    }
    public void setValue ( String value ) {
        a_value = value;
    }
    public String getName( )
    {
        return a_name;      
    }
    public String getValue()
    {
        return a_value;
    }
}
