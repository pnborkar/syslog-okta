/*
 * Author:  Pramod Borkar
 *
 * Date:    19-May-2014
 *
 * Version: 1.1
 *
 * Purpose: This is a class to represent an event to convert into CEF
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

/**
 * Exception thrown when an invalid field string is used
 * <p>
 * Nothing specific is done here besides calling super() for the various constructor calls
 *
 * @author Adam Lesperance
 *
 */
public final class InvalidField extends Exception {

    /**
     * Default serial ID
     */
    private static final long serialVersionUID = 1L;


    //~--- constructors -------------------------------------------------------

    /**
     * Don't do anything if no args are provided
     */
    public InvalidField() {}


    /**
     * @param message
     *            the message to be thrown
     */
    public InvalidField( final String message ) {
        super( message );
    }


    /**
     * @param cause
     *            the exception that caused the issue
     */
    public InvalidField( final Throwable cause ) {
        super( cause );
    }


    /**
     * @param message
     *            the message to be thrown
     * @param cause
     *            the exception that caused the issue
     */
    public InvalidField( final String message, final Throwable cause ) {
        super( message, cause );
    }
}
