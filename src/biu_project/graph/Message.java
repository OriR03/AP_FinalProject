package biu_project.graph;

import java.util.Date;

public class Message {

	public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date date;

	/**
	 * Message constructor, we get data either by bytes, string or double and convert it to double
	 * @param data bytes
	 * @param asText String
	 * @param asDouble double
	 */
	public Message(byte[] data, String asText, double asDouble) {
		super();
		this.data = data;
		this.asText = asText;
		this.asDouble = asDouble;
		this.date = new Date();
	}

	public Message(String s) {
		this(s.getBytes(), s, checkd1(s));
	}
	public Message(double v) {
        this(Double.toString(v).getBytes(), Double.toString(v), v);
	}
	public Message (byte[] d) {
		this(d,d.toString(), checkd2(d));
	}

	private static double checkd1(String s) {//check if string can be cast to double
		double d;
		try {
			d = Double.parseDouble(s);
		}
		catch(NumberFormatException e){
			d= Double.NaN;
		}
		return d;
	}

	private static double checkd2(byte[] s) {//check if byte data can be cast to double
		double d;
		try {
			d = Double.parseDouble(s.toString());
		}
		catch(NumberFormatException e){
			d= Double.NaN;
		}
		return d;
	}
}



