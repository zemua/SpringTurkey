package devs.mrp.springturkey.Exceptions;

import java.io.IOException;

public class AlreadyExistsException extends IOException {

	private static final long serialVersionUID = 1L;

	public AlreadyExistsException() {
		super();
	}

	public AlreadyExistsException(String msg) {
		super (msg);
	}

	public AlreadyExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AlreadyExistsException(Throwable cause) {
		super(cause);
	}

}
