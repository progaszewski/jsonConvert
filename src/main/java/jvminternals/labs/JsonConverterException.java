package jvminternals.labs;

@SuppressWarnings("serial")
public class JsonConverterException extends Exception {
	public JsonConverterException(Throwable cause) {
		super(cause);
	}

	public JsonConverterException(String msg) {
		super(msg);
	}
}
