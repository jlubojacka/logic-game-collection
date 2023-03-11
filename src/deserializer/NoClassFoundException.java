
package deserializer;

class NoClassFoundException extends Exception {

    public NoClassFoundException() {
        super("Please register class of resulting object.");
    }
    
}
