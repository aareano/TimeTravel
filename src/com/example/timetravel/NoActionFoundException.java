package com.example.timetravel;

public class NoActionFoundException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;					// Not sure what this bit is.
																		// It just wanted it, so I made it happy.

	public NoActionFoundException (String s) {
      super(s);
    }
}
