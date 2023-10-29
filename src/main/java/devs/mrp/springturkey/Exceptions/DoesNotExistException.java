package devs.mrp.springturkey.Exceptions;

import java.io.IOException;

public class DoesNotExistException extends IOException { // TODO add controller advice to handle exceptions

	private static final long serialVersionUID = 1L;

	public DoesNotExistException() {
		super();
	}

	public DoesNotExistException(String msg) {
		super(msg);
	}

	public DoesNotExistException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DoesNotExistException(Throwable cause) {
		super(cause);
	}

}
