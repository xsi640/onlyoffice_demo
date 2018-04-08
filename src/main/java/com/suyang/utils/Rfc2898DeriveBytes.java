package com.suyang.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * This implementation follows RFC 2898 recommendations. See
 * http://www.ietf.org/rfc/Rfc2898.txt
 */
public class Rfc2898DeriveBytes {
	private static final int BLOCK_SIZE = 20;
	private static Random random = new Random();
	private Mac hmacsha1;
	private byte[] salt;
	private int iterations;
	private byte[] buffer = new byte[BLOCK_SIZE];
	private int startIndex = 0;
	private int endIndex = 0;
	private int block = 1;

	/**
	 * Creates new instance.
	 * 
	 * @param password
	 *            The password used to derive the key.
	 * @param salt
	 *            The key salt used to derive the key.
	 * @param iterations
	 *            The number of iterations for the operation.
	 * @throws NoSuchAlgorithmException
	 *             HmacSHA1 algorithm cannot be found.
	 * @throws InvalidKeyException
	 *             Salt must be 8 bytes or more. -or- Password cannot be null.
	 */
	public Rfc2898DeriveBytes(byte[] password, byte[] salt, int iterations) throws NoSuchAlgorithmException,
			InvalidKeyException {
		this.salt = salt;
		this.iterations = iterations;
		this.hmacsha1 = Mac.getInstance("HmacSHA1");
		this.hmacsha1.init(new SecretKeySpec(password, "HmacSHA1"));
	}

	/**
	 * Creates new instance.
	 * 
	 * @param password
	 *            The password used to derive the key.
	 * @param salt
	 *            The key salt used to derive the key.
	 * @param iterations
	 *            The number of iterations for the operation.
	 * @throws NoSuchAlgorithmException
	 *             HmacSHA1 algorithm cannot be found.
	 * @throws InvalidKeyException
	 *             Salt must be 8 bytes or more. -or- Password cannot be null.
	 * @throws UnsupportedEncodingException
	 */
	public Rfc2898DeriveBytes(String password, int saltSize, int iterations) throws NoSuchAlgorithmException,
			InvalidKeyException, UnsupportedEncodingException {
		this.salt = randomSalt(saltSize);
		this.iterations = iterations;
		this.hmacsha1 = Mac.getInstance("HmacSHA1");
		this.hmacsha1.init(new SecretKeySpec(password.getBytes("UTF-8"), "HmacSHA1"));
		this.buffer = new byte[BLOCK_SIZE];
		this.block = 1;
		this.startIndex = this.endIndex = 1;
	}

	/**
	 * Creates new instance.
	 * 
	 * @param password
	 *            The password used to derive the key.
	 * @param salt
	 *            The key salt used to derive the key.
	 * @param iterations
	 *            The number of iterations for the operation.
	 * @throws NoSuchAlgorithmException
	 *             HmacSHA1 algorithm cannot be found.
	 * @throws InvalidKeyException
	 *             Salt must be 8 bytes or more. -or- Password cannot be null.
	 * @throws UnsupportedEncodingException
	 */
	public Rfc2898DeriveBytes(String password, int saltSize) throws NoSuchAlgorithmException, InvalidKeyException,
			UnsupportedEncodingException {
		this(password, saltSize, 1000);
	}

	/**
	 * Creates new instance.
	 * 
	 * @param password
	 *            The password used to derive the key.
	 * @param salt
	 *            The key salt used to derive the key.
	 * @param iterations
	 *            The number of iterations for the operation.
	 * @throws NoSuchAlgorithmException
	 *             HmacSHA1 algorithm cannot be found.
	 * @throws InvalidKeyException
	 *             Salt must be 8 bytes or more. -or- Password cannot be null.
	 * @throws UnsupportedEncodingException
	 *             UTF-8 encoding is not supported.
	 */
	public Rfc2898DeriveBytes(String password, byte[] salt, int iterations) throws InvalidKeyException,
			NoSuchAlgorithmException, UnsupportedEncodingException {
		this(password.getBytes("UTF8"), salt, iterations);
	}

	public byte[] getSalt() {
		return this.salt;
	}

	public String getSaltAsString() {
		return Base64.encodeBase64String(this.salt);
	}

	/**
	 * Returns a pseudo-random key from a data, salt and iteration count.
	 * 
	 * @param cb
	 *            Number of bytes to return.
	 * @return Byte array.
	 */
	public byte[] getBytes(int cb) {
		byte[] result = new byte[cb];
		int offset = 0;
		int size = this.endIndex - this.startIndex;
		if (size > 0) { // if there is some data in buffer
			if (cb >= size) { // if there is enough data in buffer
				System.arraycopy(this.buffer, this.startIndex, result, 0, size);
				this.startIndex = this.endIndex = 0;
				offset += size;
			} else {
				System.arraycopy(this.buffer, this.startIndex, result, 0, cb);
				startIndex += cb;
				return result;
			}
		}

		while (offset < cb) {
			byte[] block = this.func();
			int remainder = cb - offset;
			if (remainder > BLOCK_SIZE) {
				System.arraycopy(block, 0, result, offset, BLOCK_SIZE);
				offset += BLOCK_SIZE;
			} else {
				System.arraycopy(block, 0, result, offset, remainder);
				offset += remainder;
				System.arraycopy(block, remainder, this.buffer, startIndex, BLOCK_SIZE - remainder);
				endIndex += (BLOCK_SIZE - remainder);
				return result;
			}
		}
		return result;
	}

	public static byte[] randomSalt(int size) {
		byte[] salt = new byte[size];
		random.nextBytes(salt);
		return salt;
	}

	/**
	 * Generate random Salt
	 * 
	 * @param size
	 * @return
	 */
	public static String generateSalt(int size) {
		byte[] salt = randomSalt(size);
		return Base64.encodeBase64String(salt);
	}

	private byte[] func() {
		this.hmacsha1.update(this.salt, 0, this.salt.length);
		byte[] tempHash = this.hmacsha1.doFinal(getBytesFromInt(this.block));

		this.hmacsha1.reset();
		byte[] finalHash = tempHash;
		for (int i = 2; i <= this.iterations; i++) {
			tempHash = this.hmacsha1.doFinal(tempHash);
			for (int j = 0; j < 20; j++) {
				finalHash[j] = (byte) (finalHash[j] ^ tempHash[j]);
			}
		}
		if (this.block == 2147483647) {
			this.block = -2147483648;
		} else {
			this.block += 1;
		}
		return finalHash;
	}

	private static byte[] getBytesFromInt(int i) {
		return new byte[] { (byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i };
	}
}
