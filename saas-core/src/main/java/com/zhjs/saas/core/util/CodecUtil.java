package com.zhjs.saas.core.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;


/**
 * 
 * @author:		Jackie Wang 
 * @since:		2017-11-08
 * @modified:	2017-11-08
 * @version:	
 */
public abstract class CodecUtil
{

	/**
	 * Private constructor to prevent instantiation.
	 */
	private CodecUtil(){}
	
	public static final String Charset = "UTF-8";

	/**
	 * Constant time comparison to prevent against timing attacks.
	 * @param expected
	 * @param actual
	 * @return
	 */
	public static boolean equals(String expected, String actual) {
		byte[] expectedBytes = bytesUtf8(expected);
		byte[] actualBytes = bytesUtf8(actual);
		int expectedLength = expectedBytes == null ? -1 : expectedBytes.length;
		int actualLength = actualBytes == null ? -1 : actualBytes.length;

		int result = expectedLength == actualLength ? 0 : 1;
		for (int i = 0; i < actualLength; i++) {
			byte expectedByte = expectedLength <= 0 ? 0 : expectedBytes[i % expectedLength];
			byte actualByte = actualBytes[i % actualLength];
			result |= expectedByte ^ actualByte;
		}
		return result == 0;
	}

	private static byte[] bytesUtf8(String s) {
		if (s == null) {
			return null;
		}

		return encode(s); // need to check if Utf8.encode() runs in constant time (probably not). This may leak length of string.
	}
	
	
	private static final Charset UTF8 = java.nio.charset.Charset.forName("UTF-8");

	/**
	 * Get the bytes of the String in UTF-8 encoded form.
	 */
	public static byte[] encode(CharSequence string) {
		try {
			ByteBuffer bytes = UTF8.newEncoder().encode(CharBuffer.wrap(string));
			byte[] bytesCopy = new byte[bytes.limit()];
			System.arraycopy(bytes.array(), 0, bytesCopy, 0, bytes.limit());

			return bytesCopy;
		}
		catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Encoding failed", e);
		}
	}

	/**
	 * Decode the bytes in UTF-8 form into a String.
	 */
	public static String decode(byte[] bytes) {
		try {
			return UTF8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
		}
		catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Decoding failed", e);
		}
	}

}
