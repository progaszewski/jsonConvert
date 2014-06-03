package jvminternals.labs.run;

import java.util.ArrayList;
import java.util.List;

import jvminternals.labs.JsonConverter;
import jvminternals.labs.JsonConverterException;

public class Book {
	
	public int id;
	public float f;
	public double d;
	private long l = 4L;
	public short s = (short)0;
	public byte by = (byte)0;
	public Byte cBy;
	public Short cS = 0;
	public Double cD = 4D;
	public Float cF;
	public Integer cI;
	public boolean b;
	public Boolean cB;
	public String s1 = "String 1";
	public String s2;
	public Test t = new Test();
	
	public int[] tab = new int[] {3, 3};
	public double[] tabD = new double[] {0.0D, 1.0D};
	public float[] tabF = new float[] {0.0F, 1.0F};
	public Integer[] tabInteger = new Integer[] {1, 3};
	public Double[] tabDouble = new Double[] {1D, 3D};
	public Test[] tabT = new Test[] { new Test() };
	public String[] tabString = new String[] {"String 1", "Coś"};
	public Book book;
	
	public ArrayList<String> list = new ArrayList<String>() {
		{
			add("String11");
			add("String11d");
		}
	};
	
	public boolean[] tabB = new boolean[] {true, false};
	public short[] ss;
	public byte[] bb;
	public long[] ll;
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}

	public static void main(String[] args) throws JsonConverterException {
		Book book = new Book();
		JsonConverter tJ = new JsonConverter();
		
		System.out.println(tJ.toJson(book));
	}

}
