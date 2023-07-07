package devs.mrp.springturkey.Exceptions;

import java.io.IOException;

public class DoesNotBelongToUserException extends IOException {

	private static final long serialVersionUID = 1L;

	public DoesNotBelongToUserException() {
		super();
	}

	public DoesNotBelongToUserException(String msg) {
		super(msg);
	}

	public DoesNotBelongToUserException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DoesNotBelongToUserException(Throwable cause) {
		super(cause);
	}

}
