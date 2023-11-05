package devs.mrp.springturkey.exceptions;

import java.io.IOException;

public class DoesNotExistException extends IOException {

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
