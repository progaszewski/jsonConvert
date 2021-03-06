package jvminternals.labs;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class JsonConverter implements JsonConverterInterface {

	final String TAB = "   ";

	@Override
	public <T> String toJson(T obj) throws JsonConverterException {
		int deep = 0;

		return generateJson(obj, deep);

	}

	@Override
	public <T> T fromJson(String json, Class<T> cls)
			throws JsonConverterException {
		try {
			return (T) cls.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new JsonConverterException(e);
		}
	}

	private <T> String generateJson(T obj, int deep) throws JsonConverterException{
		String json = "", objType = "";




		if (obj == null) {
			throw new JsonConverterException("null object converion");
		}
/*
		for (Field f : obj.getClass().getFields()){
			try {
				System.out.print(f.getName() + ": ");
				Object o = f.get(obj);
				if(o != null){
					System.out.println(o.getClass().getName());
				}else{
					System.out.println(f.getType());
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

*/
		json += getTabs(deep++) + "{\n";
		json += getTabs(deep++) + obj.getClass().getSimpleName() + ": {\n";

		ArrayList<Field> privateFields = new ArrayList<Field>();
		ArrayList<Field> publicFields = new ArrayList<Field>();

		//Podział na pola prywatne i publiczne
		for(Field f :obj.getClass().getDeclaredFields()){
			
				try {
					f.get(obj);
					publicFields.add(f);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					f.setAccessible(true);
					privateFields.add(f);
				}
			
		}


		//tworzenie jsona dla pol prywatnych
		if(privateFields.size() != 0) {
			json += getTabs(deep++) + "private: {\n";

			json = generate(privateFields, json, deep, objType, obj);

			json += getTabs(--deep) + "}\n";
		}


		//tworzenie jsona dla pol publicznych
		if(publicFields.size() != 0) {
			json += getTabs(deep++) + "public: {\n";

			json = generate(publicFields, json, deep, objType, obj);

			json += getTabs(--deep) + "}\n";

		}

		json += getTabs(--deep) + "}\n";
		json += getTabs(--deep) + "}\n";

		return json;
	}

	//prasowanie pol do formatu jsona
	private <T> String generate(ArrayList<Field> fields, String json, int deep, String objType, T obj) throws JsonConverterException{
		for (Field f : fields){
			try {
				json += getTabs(deep) + "\"" + f.getName() + "\": ";
				//json += f.get(obj);
				objType = getValOfField(f, obj);
				if(!objType.equals("$$$object$$$")){
					json += objType;
					json += ",\n";
				}else{
					json += "\n" + generateJson(f.get(obj), deep + 1);
					json += getTabs(deep) + ",\n";
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

		}
		if(json.charAt(json.length() - 2) == ','){
			json = json.substring(0, json.length() - 2) + "\n";
		}

		return json;
	}
	//Rysowanie tabulacji
	private String getTabs(int c){
		String tabs = "";
		for(int i = 0; i < c; i++){
			tabs += TAB;
		}
		return tabs;
	}
	//podstawowe typy pol
	private <T> String getValOfField(Field f, T obj) throws IllegalArgumentException, IllegalAccessException{
		Object fType = f.getType();



		switch(fType.toString()){
		case "int": return f.getInt(obj) + "";
		case "boolean" : return f.getBoolean(obj) + "";
		case "double" : return f.getDouble(obj) + "d";
		case "float" : return f.getFloat(obj) + "f";
		case "short": return "(short)" + f.getShort(obj);
		case "byte": return "(byte)" + f.getByte(obj);
		case "long": return f.getLong(obj) + "l";

		//default: return f.get(obj) + " (type: " + fType.toString() + " )";
		default : return getObjectVal(f, obj);
		}

		//return fType + "";
	}
	//Podstawowe klasy
	private <T> String getObjectVal(Field f, T obj) throws IllegalArgumentException, IllegalAccessException{
		Object object = f.get(obj);

		if(object == null){
			Object type = f.getType();
			return "null (" + type.toString().split(" ")[1] + ")";
		}
		
		if(object.getClass().getName().charAt(0) == '[')
			try {
				return getArray(object, object.getClass().getName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		switch(object.getClass().getName()){
		case "java.lang.String": return "\"" + object.toString() + "\"";
		case "java.lang.Integer": return object.toString() + " (Integer)";
		case "java.lang.Double": return object.toString() + "D";
		case "java.lang.Float": return object.toString() + "F";
		case "java.lang.Boolean": return object.toString().toUpperCase();
		case "java.lang.Long": return object.toString() + "L";
		case "java.lang.Byte": return "(Byte)" + object.toString();
		case "java.lang.Short": return "(Short)" + object.toString();
		//case "[I": return getArray(f, obj, "[I");
		//case "[D": return getArray(f, obj, "[D");
		//case "[F": return getArray(f, obj, "[F");
		//case "[Z": return getArray(f, obj, "[Z");

		default: 
			if(f.getType().toString().split(" ")[1].matches("java.util.*List")){
				return object.toString() + " (" + f.getType().toString().split("\\.")[2] + ")";
			}
		}

		return "$$$object$$$";
	}

	//Parsowanie tablic podstawowych
	private String getArray(Object object, String className) throws ClassNotFoundException {
		String prefix = "", sufix = "";

		//System.out.println("\n\n\n!!!!! " + object.getClass().getName());
		switch(className){
		case "[D": sufix = "d"; break;
		case "[F": sufix = "f"; break;
		case "[S": prefix = "(short)"; break;
		case "[B": prefix = "(byte)"; break;
		case "[J": sufix = "l"; break;
		case "[Ljava.lang.Double;": sufix = "D"; break;
		case "[Ljava.lang.Float;": sufix = "F"; break;
		case "[Ljava.lang.String;": prefix = sufix = "\""; break;
		case "[Ljava.lang.Long;": sufix = "L"; break;
		case "[Ljava.lang.Byte;": prefix = "(Byte)"; break;
		case "[Ljava.lang.Short;": prefix = "(Short)"; break;
		}

		String arrayString = "[";
		if(Array.getLength(object) != 0){
			for(int i = 0; i < Array.getLength(object) - 1; i++){
				arrayString += prefix + Array.get(object, i) + sufix + ", ";
			}
			arrayString += prefix + Array.get(object, Array.getLength(object) - 1) + sufix; 
		}

		arrayString += "]";
		return arrayString;



		//return "";
	}

}
