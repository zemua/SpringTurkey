package devs.mrp.springturkey.Exceptions;

public class TurkeySurpriseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TurkeySurpriseException(String msg) {
		super(msg);
	}

	public TurkeySurpriseException(String msg, Throwable e) {
		super(msg, e);
	}

}
