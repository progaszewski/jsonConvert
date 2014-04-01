package jvminternals.labs;

public interface JsonConverterInterface {
	public <T> String toJson(T obj) throws JsonConverterException;
	public <T> T fromJson(String json, Class<T> cls) throws JsonConverterException;
}
