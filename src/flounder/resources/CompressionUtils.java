package flounder.resources;

import java.io.*;
import java.util.zip.*;

public class CompressionUtils {
	public static byte[] compress(String str) throws Exception {
		if (str == null || str.length() == 0) {
			return null;
		}

		ByteArrayOutputStream obj=new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();
		String outStr = obj.toString("UTF-8");
		return obj.toByteArray();
	}

	public static String decompress(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
		String outStr = "";
		String line;

		while ((line=bf.readLine())!=null) {
			outStr += line;
		}

		return outStr;
	}
}
