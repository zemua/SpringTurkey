package devs.mrp.springturkey.exceptions;

import java.io.IOException;

public class WrongDataException extends IOException {

	private static final long serialVersionUID = 1L;

	public WrongDataException() {
		super();
	}

	public WrongDataException(String msg) {
		super(msg);
	}

	public WrongDataException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public WrongDataException(Throwable cause) {
		super(cause);
	}

}
