package flounder.textures;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.zip.*;

/**
 * A png decoder that is useful for getting exact readings from PNG files.
 */
public class TextureDecoder {
	private static final byte[] SIGNATURE = {(byte) 137, 80, 78, 71, 13, 10, 26, 10};
	private static final int IHDR = 0x49484452;
	private static final int PLTE = 0x504C5445;
	private static final int tRNS = 0x74524E53;
	private static final int IDAT = 0x49444154;
	private static final int IEND = 0x49454E44;
	private static final byte COLOUR_GREYSCALE = 0;
	private static final byte COLOUR_TRUECOLOUR = 2;
	private static final byte COLOUR_INDEXED = 3;
	private static final byte COLOUR_GREYALPHA = 4;
	private static final byte COLOUR_TRUEALPHA = 6;
	private final InputStream input;
	private final CRC32 crc;
	private final byte[] buffer;
	private int chunkLength;
	private int chunkType;
	private int chunkRemaining;
	private int width;
	private int height;
	private int bitdepth;
	private int colourType;
	private int bytesPerPixel;
	private byte[] palette;
	private byte[] paletteA;
	private byte[] transPixel;

	public TextureDecoder(final InputStream input) throws IOException {
		this.input = input;
		this.crc = new CRC32();
		this.buffer = new byte[4096];

		readFully(buffer, 0, SIGNATURE.length);

		if (!checkSignature(buffer)) {
			throw new IOException("Not a valid PNG file");
		}

		openChunk(IHDR);
		readIHDR();
		closeChunk();

		searchIDAT:
		for (; ; ) {
			openChunk();

			switch (chunkType) {
				case IDAT:
					break searchIDAT;
				case PLTE:
					readPLTE();
					break;
				case tRNS:
					readtRNS();
					break;
			}

			closeChunk();
		}

		if (colourType == COLOUR_INDEXED && palette == null) {
			throw new IOException("Missing PLTE chunk");
		}
	}

	private static boolean checkSignature(final byte[] buffer) {
		for (int i = 0; i < SIGNATURE.length; i++) {
			if (buffer[i] != SIGNATURE[i]) {
				return false;
			}
		}

		return true;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Checks if the image has transparency information either from an alpha channel or from a tRNS chunk.
	 *
	 * @return True if the image has transparency.
	 *
	 * @see #hasAlphaChannel()
	 * @see #overwriteTRNS(byte, byte, byte)
	 */
	public boolean hasAlpha() {
		return hasAlphaChannel() || paletteA != null || transPixel != null;
	}

	/**
	 * Checks if the image has a real alpha channel. This method does not check for the presence of a tRNS chunk.
	 *
	 * @return True if the image has an alpha channel
	 *
	 * @see #hasAlpha()
	 */
	public boolean hasAlphaChannel() {
		return colourType == COLOUR_TRUEALPHA || colourType == COLOUR_GREYALPHA;
	}

	public boolean isRGB() {
		return colourType == COLOUR_TRUEALPHA || colourType == COLOUR_TRUECOLOUR || colourType == COLOUR_INDEXED;
	}

	/**
	 * Overwrites the tRNS chunk entry to make a selected colour transparent. This can only be invoked when the image has no alpha channel. Calling this method causes {@link #hasAlpha()} to return true.
	 *
	 * @param r The red component of the colour to make transparent.
	 * @param g The green component of the colour to make transparent.
	 * @param b The blue component of the colour to make transparent.
	 *
	 * @throws UnsupportedOperationException If the tRNS chunk data can't be set.
	 * @see #hasAlphaChannel()
	 */
	public void overwriteTRNS(final byte r, final byte g, final byte b) {
		if (hasAlphaChannel()) {
			throw new UnsupportedOperationException("Image has an alpha channel!");
		}

		byte[] pal = palette;

		if (pal == null) {
			transPixel = new byte[]{0, r, 0, g, 0, b};
		} else {
			paletteA = new byte[pal.length / 3];

			for (int i = 0, j = 0; i < pal.length; i += 3, j++) {
				if (pal[i] != r || pal[i + 1] != g || pal[i + 2] != b) {
					paletteA[j] = (byte) 0xFF;
				}
			}
		}
	}

	/**
	 * Computes the implemented format conversion for the desired format.
	 *
	 * @param format The desired format.
	 *
	 * @return Format which best matches the desired format.
	 *
	 * @throws UnsupportedOperationException If this PNG file can't be decoded.
	 */
	public Format decideTextureFormat(final Format format) {
		switch (colourType) {
			case COLOUR_TRUECOLOUR:
				switch (format) {
					case ABGR:
					case RGBA:
					case BGRA:
					case RGB:
						return format;
					default:
						return Format.RGB;
				}
			case COLOUR_TRUEALPHA:
				switch (format) {
					case ABGR:
					case RGBA:
					case BGRA:
					case RGB:
						return format;
					default:
						return Format.RGBA;
				}
			case COLOUR_GREYSCALE:
				switch (format) {
					case LUMINANCE:
					case ALPHA:
						return format;
					default:
						return Format.LUMINANCE;
				}
			case COLOUR_GREYALPHA:
				return Format.LUMINANCE_ALPHA;
			case COLOUR_INDEXED:
				switch (format) {
					case ABGR:
					case RGBA:
					case BGRA:
						return format;
					default:
						return Format.RGBA;
				}
			default:
				throw new UnsupportedOperationException("Texture format not yet implemented!");
		}
	}

	/**
	 * Decodes the image into the specified buffer. The last line is placed at the current position. After decode the buffer position is at the end of the first line.
	 *
	 * @param buffer The buffer.
	 * @param stride The stride in bytes from start of a line to start of the next line, must be positive.
	 * @param format The target format into which the image should be decoded.
	 *
	 * @throws IOException Of a read or data error occurred.
	 * @throws IllegalArgumentException Of the start position of a line falls outside the buffer.
	 * @throws UnsupportedOperationException Of the image can't be decoded into the desired format.
	 */
	public void decodeFlipped(final ByteBuffer buffer, final int stride, final Format format) throws IOException {
		if (stride <= 0) {
			throw new IllegalArgumentException("Stride");
		}

		int pos = buffer.position();
		int posDelta = (height - 1) * stride;
		buffer.position(pos + posDelta);
		decode(buffer, -stride, format);
		buffer.position(buffer.position() + posDelta);
	}

	/**
	 * Decodes the image into the specified buffer. The first line is placed at the current position. After decode the buffer position is at the end of the last line.
	 *
	 * @param buffer The buffer.
	 * @param stride The stride in bytes from start of a line to start of the next line, can be negative.
	 * @param format The target format into which the image should be decoded.
	 *
	 * @throws IOException If a read or data error occurred.
	 * @throws IllegalArgumentException If the start position of a line falls outside the buffer.
	 * @throws UnsupportedOperationException If the image can't be decoded into the desired format.
	 */
	public void decode(final ByteBuffer buffer, final int stride, final Format format) throws IOException {
		final int offset = buffer.position();
		final int lineSize = (width * bitdepth + 7) / 8 * bytesPerPixel;
		byte[] curLine = new byte[lineSize + 1];
		byte[] prevLine = new byte[lineSize + 1];
		byte[] palLine = bitdepth < 8 ? new byte[width + 1] : null;
		final Inflater inflater = new Inflater();

		try {
			for (int y = 0; y < height; y++) {
				readChunkUnzip(inflater, curLine, 0, curLine.length);
				unfilter(curLine, prevLine);

				buffer.position(offset + y * stride);

				switch (colourType) {
					case COLOUR_TRUECOLOUR:
						switch (format) {
							case ABGR:
								copyRGBtoABGR(buffer, curLine);
								break;
							case RGBA:
								copyRGBtoRGBA(buffer, curLine);
								break;
							case BGRA:
								copyRGBtoBGRA(buffer, curLine);
								break;
							case RGB:
								copy(buffer, curLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported format for this image");
						}
						break;
					case COLOUR_TRUEALPHA:
						switch (format) {
							case ABGR:
								copyRGBAtoABGR(buffer, curLine);
								break;
							case RGBA:
								copy(buffer, curLine);
								break;
							case BGRA:
								copyRGBAtoBGRA(buffer, curLine);
								break;
							case RGB:
								copyRGBAtoRGB(buffer, curLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported format for this image");
						}
						break;
					case COLOUR_GREYSCALE:
						switch (format) {
							case LUMINANCE:
							case ALPHA:
								copy(buffer, curLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported format for this image");
						}
						break;
					case COLOUR_GREYALPHA:
						switch (format) {
							case LUMINANCE_ALPHA:
								copy(buffer, curLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported format for this image");
						}
						break;
					case COLOUR_INDEXED:
						switch (bitdepth) {
							case 8:
								palLine = curLine;
								break;
							case 4:
								expand4(curLine, palLine);
								break;
							case 2:
								expand2(curLine, palLine);
								break;
							case 1:
								expand1(curLine, palLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported bitdepth for this image");
						}
						switch (format) {
							case ABGR:
								copyPALtoABGR(buffer, palLine);
								break;
							case RGBA:
								copyPALtoRGBA(buffer, palLine);
								break;
							case BGRA:
								copyPALtoBGRA(buffer, palLine);
								break;
							default:
								throw new UnsupportedOperationException("Unsupported format for this image");
						}
						break;
					default:
						throw new UnsupportedOperationException("Not yet implemented");
				}

				byte[] tmp = curLine;
				curLine = prevLine;
				prevLine = tmp;
			}
		} finally {
			inflater.end();
		}
	}

	private void readChunkUnzip(final Inflater inflater, final byte[] buffer, int offset, int length) throws IOException {
		assert buffer != this.buffer;

		try {
			do {
				int read = inflater.inflate(buffer, offset, length);

				if (read <= 0) {
					if (inflater.finished()) {
						throw new EOFException();
					}

					if (inflater.needsInput()) {
						refillInflater(inflater);
					} else {
						throw new IOException("Can't inflate " + length + " bytes");
					}
				} else {
					offset += read;
					length -= read;
				}
			} while (length > 0);
		} catch (DataFormatException ex) {
			throw (IOException) new IOException("Inflate error").initCause(ex);
		}
	}

	private void refillInflater(final Inflater inflater) throws IOException {
		while (chunkRemaining == 0) {
			closeChunk();
			openChunk(IDAT);
		}

		int read = readChunk(buffer, 0, buffer.length);
		inflater.setInput(buffer, 0, read);
	}

	private void closeChunk() throws IOException {
		if (chunkRemaining > 0) {
			// Just skip the rest and the CRC.
			skip(chunkRemaining + 4);
		} else {
			readFully(buffer, 0, 4);
			int expectedCrc = readInt(buffer, 0);
			int computedCrc = (int) crc.getValue();

			if (computedCrc != expectedCrc) {
				throw new IOException("Invalid CRC");
			}
		}

		chunkRemaining = 0;
		chunkLength = 0;
		chunkType = 0;
	}

	private void skip(long amount) throws IOException {
		while (amount > 0) {
			long skipped = input.skip(amount);

			if (skipped < 0) {
				throw new EOFException();
			}

			amount -= skipped;
		}
	}

	private void readFully(final byte[] buffer, int offset, int length) throws IOException {
		do {
			int read = input.read(buffer, offset, length);

			if (read < 0) {
				throw new EOFException();
			}

			offset += read;
			length -= read;
		} while (length > 0);
	}

	private int readInt(final byte[] buffer, final int offset) {
		return buffer[offset] << 24 | (buffer[offset + 1] & 255) << 16 | (buffer[offset + 2] & 255) << 8 | buffer[offset + 3] & 255;
	}

	private void openChunk(final int expected) throws IOException {
		openChunk();

		if (chunkType != expected) {
			throw new IOException("Expected chunk: " + Integer.toHexString(expected));
		}
	}

	private void openChunk() throws IOException {
		readFully(buffer, 0, 8);
		chunkLength = readInt(buffer, 0);
		chunkType = readInt(buffer, 4);
		chunkRemaining = chunkLength;
		crc.reset();
		crc.update(buffer, 4, 4); // Only chunkType.
	}

	private int readChunk(final byte[] buffer, final int offset, int length) throws IOException {
		if (length > chunkRemaining) {
			length = chunkRemaining;
		}

		readFully(buffer, offset, length);
		crc.update(buffer, offset, length);
		chunkRemaining -= length;
		return length;
	}

	private void unfilter(final byte[] curLine, final byte[] prevLine) throws IOException {
		switch (curLine[0]) {
			case 0: // none
				break;
			case 1:
				unfilterSub(curLine);
				break;
			case 2:
				unfilterUp(curLine, prevLine);
				break;
			case 3:
				unfilterAverage(curLine, prevLine);
				break;
			case 4:
				unfilterPaeth(curLine, prevLine);
				break;
			default:
				throw new IOException("Invalide filter type in scanline: " + curLine[0]);
		}
	}

	private void unfilterSub(byte[] curLine) {
		final int bpp = bytesPerPixel;

		for (int i = bpp + 1, n = curLine.length; i < n; ++i) {
			curLine[i] += curLine[i - bpp];
		}
	}

	private void unfilterUp(byte[] curLine, final byte[] prevLine) {
		for (int i = 1, n = curLine.length; i < n; ++i) {
			curLine[i] += prevLine[i];
		}
	}

	private void unfilterAverage(byte[] curLine, final byte[] prevLine) {
		final int bpp = bytesPerPixel;
		int i;

		for (i = 1; i <= bpp; ++i) {
			curLine[i] += (byte) ((prevLine[i] & 0xFF) >>> 1);
		}

		for (int n = curLine.length; i < n; ++i) {
			curLine[i] += (byte) ((prevLine[i] & 0xFF) + (curLine[i - bpp] & 0xFF) >>> 1);
		}
	}

	private void unfilterPaeth(byte[] curLine, final byte[] prevLine) {
		final int bpp = bytesPerPixel;
		int i;

		for (i = 1; i <= bpp; ++i) {
			curLine[i] += prevLine[i];
		}

		for (int n = curLine.length; i < n; ++i) {
			int a = curLine[i - bpp] & 255;
			int b = prevLine[i] & 255;
			int c = prevLine[i - bpp] & 255;
			int p = a + b - c;
			int pa = p - a;

			if (pa < 0) {
				pa = -pa;
			}

			int pb = p - b;

			if (pb < 0) {
				pb = -pb;
			}

			int pc = p - c;

			if (pc < 0) {
				pc = -pc;
			}

			if (pa <= pb && pa <= pc) {
				c = a;
			} else if (pb <= pc) {
				c = b;
			}

			curLine[i] += (byte) c;
		}
	}

	private void copyRGBtoABGR(final ByteBuffer buffer, final byte[] curLine) {
		if (transPixel != null) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];

			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;

				if (r == tr && g == tg && b == tb) {
					a = 0;
				}

				buffer.put(a).put(b).put(g).put(r);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put((byte) 0xFF).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
			}
		}
	}

	private void copyRGBtoRGBA(final ByteBuffer buffer, final byte[] curLine) {
		if (transPixel != null) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];

			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;

				if (r == tr && g == tg && b == tb) {
					a = 0;
				}

				buffer.put(r).put(g).put(b).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]).put((byte) 0xFF);
			}
		}
	}

	private void copyRGBtoBGRA(final ByteBuffer buffer, final byte[] curLine) {
		if (transPixel != null) {
			byte tr = transPixel[1];
			byte tg = transPixel[3];
			byte tb = transPixel[5];

			for (int i = 1, n = curLine.length; i < n; i += 3) {
				byte r = curLine[i];
				byte g = curLine[i + 1];
				byte b = curLine[i + 2];
				byte a = (byte) 0xFF;

				if (r == tr && g == tg && b == tb) {
					a = 0;
				}

				buffer.put(b).put(g).put(r).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 3) {
				buffer.put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]).put((byte) 0xFF);
			}
		}
	}

	private void copy(final ByteBuffer buffer, final byte[] curLine) {
		buffer.put(curLine, 1, curLine.length - 1);
	}

	private void copyRGBAtoABGR(final ByteBuffer buffer, final byte[] curLine) {
		for (int i = 1, n = curLine.length; i < n; i += 4) {
			buffer.put(curLine[i + 3]).put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]);
		}
	}

	private void copyRGBAtoBGRA(final ByteBuffer buffer, final byte[] curLine) {
		for (int i = 1, n = curLine.length; i < n; i += 4) {
			buffer.put(curLine[i + 2]).put(curLine[i + 1]).put(curLine[i]).put(curLine[i + 3]);
		}
	}

	private void copyRGBAtoRGB(final ByteBuffer buffer, final byte[] curLine) {
		for (int i = 1, n = curLine.length; i < n; i += 4) {
			buffer.put(curLine[i]).put(curLine[i + 1]).put(curLine[i + 2]);
		}
	}

	private void expand4(final byte[] source, byte[] destination) {
		for (int i = 1, n = destination.length; i < n; i += 2) {
			int val = source[1 + (i >> 1)] & 255;

			switch (n - i) {
				default:
					destination[i + 1] = (byte) (val & 15);
				case 1:
					destination[i] = (byte) (val >> 4);
			}
		}
	}

	private void expand2(final byte[] source, byte[] destination) {
		for (int i = 1, n = destination.length; i < n; i += 4) {
			int val = source[1 + (i >> 2)] & 255;

			switch (n - i) {
				default:
					destination[i + 3] = (byte) (val & 3);
				case 3:
					destination[i + 2] = (byte) (val >> 2 & 3);
				case 2:
					destination[i + 1] = (byte) (val >> 4 & 3);
				case 1:
					destination[i] = (byte) (val >> 6);
			}
		}
	}

	private void expand1(final byte[] source, byte[] destination) {
		for (int i = 1, n = destination.length; i < n; i += 8) {
			int val = source[1 + (i >> 3)] & 255;

			switch (n - i) {
				default:
					destination[i + 7] = (byte) (val & 1);
				case 7:
					destination[i + 6] = (byte) (val >> 1 & 1);
				case 6:
					destination[i + 5] = (byte) (val >> 2 & 1);
				case 5:
					destination[i + 4] = (byte) (val >> 3 & 1);
				case 4:
					destination[i + 3] = (byte) (val >> 4 & 1);
				case 3:
					destination[i + 2] = (byte) (val >> 5 & 1);
				case 2:
					destination[i + 1] = (byte) (val >> 6 & 1);
				case 1:
					destination[i] = (byte) (val >> 7);
			}
		}
	}

	private void copyPALtoABGR(final ByteBuffer buffer, final byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(a).put(b).put(g).put(r);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(a).put(b).put(g).put(r);
			}
		}
	}

	private void copyPALtoRGBA(final ByteBuffer buffer, final byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(r).put(g).put(b).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(r).put(g).put(b).put(a);
			}
		}
	}

	private void copyPALtoBGRA(final ByteBuffer buffer, final byte[] curLine) {
		if (paletteA != null) {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = paletteA[idx];
				buffer.put(b).put(g).put(r).put(a);
			}
		} else {
			for (int i = 1, n = curLine.length; i < n; i += 1) {
				int idx = curLine[i] & 255;
				byte r = palette[idx * 3];
				byte g = palette[idx * 3 + 1];
				byte b = palette[idx * 3 + 2];
				byte a = (byte) 0xFF;
				buffer.put(b).put(g).put(r).put(a);
			}
		}
	}

	private void readIHDR() throws IOException {
		checkChunkLength(13);
		readChunk(buffer, 0, 13);
		width = readInt(buffer, 0);
		height = readInt(buffer, 4);
		bitdepth = buffer[8] & 255;
		colourType = buffer[9] & 255;

		switch (colourType) {
			case COLOUR_GREYSCALE:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}

				bytesPerPixel = 1;
				break;
			case COLOUR_GREYALPHA:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}

				bytesPerPixel = 2;
				break;
			case COLOUR_TRUECOLOUR:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}

				bytesPerPixel = 3;
				break;
			case COLOUR_TRUEALPHA:
				if (bitdepth != 8) {
					throw new IOException("Unsupported bit depth: " + bitdepth);
				}

				bytesPerPixel = 4;
				break;
			case COLOUR_INDEXED:
				switch (bitdepth) {
					case 8:
					case 4:
					case 2:
					case 1:
						bytesPerPixel = 1;
						break;
					default:
						throw new IOException("Unsupported bit depth: " + bitdepth);
				}

				break;
			default:
				throw new IOException("Unsupported colour format: " + colourType);
		}

		if (buffer[10] != 0) {
			throw new IOException("Unsupported compression method");
		}

		if (buffer[11] != 0) {
			throw new IOException("Unsupported filtering method");
		}

		if (buffer[12] != 0) {
			throw new IOException("Unsupported interlace method");
		}
	}

	private void readPLTE() throws IOException {
		int paletteEntries = chunkLength / 3;

		if (paletteEntries < 1 || paletteEntries > 256 || chunkLength % 3 != 0) {
			throw new IOException("PLTE chunk has wrong length");
		}

		palette = new byte[paletteEntries * 3];
		readChunk(palette, 0, palette.length);
	}

	private void readtRNS() throws IOException {
		switch (colourType) {
			case COLOUR_GREYSCALE:
				checkChunkLength(2);
				transPixel = new byte[2];
				readChunk(transPixel, 0, 2);
				break;
			case COLOUR_TRUECOLOUR:
				checkChunkLength(6);
				transPixel = new byte[6];
				readChunk(transPixel, 0, 6);
				break;
			case COLOUR_INDEXED:
				if (palette == null) {
					throw new IOException("tRNS chunk without PLTE chunk");
				}
				paletteA = new byte[palette.length / 3];
				Arrays.fill(paletteA, (byte) 0xFF);
				readChunk(paletteA, 0, paletteA.length);
				break;
			default:
		}
	}

	private void checkChunkLength(final int expected) throws IOException {
		if (chunkLength != expected) {
			throw new IOException("Chunk has wrong size");
		}
	}

	public enum Format {
		ALPHA(1, true), LUMINANCE(1, false), LUMINANCE_ALPHA(2, true), RGB(3, false), RGBA(4, true), BGRA(4, true), ABGR(4, true);

		private final int m_numComponents;
		private final boolean m_hasAlpha;

		Format(int numComponents, boolean hasAlpha) {
			m_numComponents = numComponents;
			m_hasAlpha = hasAlpha;
		}

		public int getNumComponents() {
			return m_numComponents;
		}

		public boolean isHasAlpha() {
			return m_hasAlpha;
		}
	}
}