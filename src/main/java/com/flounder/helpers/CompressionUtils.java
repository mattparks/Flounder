package com.flounder.helpers;

import java.io.*;
import java.util.zip.*;

/**
 * A helper for compressing and decompressing strings with gzip.
 */
public class CompressionUtils {
	/**
	 * Compresses a string into an array of bytes.
	 *
	 * @param string The string to compress.
	 *
	 * @return The compressed list of bytes.
	 *
	 * @throws Exception If failed to compress.
	 */
	public static byte[] compress(String string) throws Exception {
		if (string == null || string.length() == 0) {
			return null;
		}

		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(string.getBytes("UTF-8"));
		gzip.close();
		String outStr = obj.toString("UTF-8");
		return obj.toByteArray();
	}

	/**
	 * Decompresses a array of bytes back into a string.
	 *
	 * @param bytes The compressed list of bytes.
	 *
	 * @return The string uncompress.
	 *
	 * @throws Exception If failed to uncompress.
	 */
	public static String decompress(byte[] bytes) throws Exception {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
		StringBuilder result = new StringBuilder();
		String line;

		while ((line = bf.readLine()) != null) {
			result.append(line);
		}

		return result.toString();
	}
}
