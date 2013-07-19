package chk.jsphelper.util;

import chk.jsphelper.Constant;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class EncryptUtil
{
	private static String algorithm = "";
	private static SecretKey secretKey = null;

	static
	{
		final int keySize = 16;
		final byte[] keyBytes = new byte[keySize];

		final String keyStr = Constant.getValue("Server.URL", "http://localhost/");
		final byte[] temp = EncryptUtil.getBase64Encode(keyStr.getBytes()).getBytes();
		final int len = temp.length;

		for (int i = 0; i < keySize; i++)
		{
			keyBytes[i] = temp[i % len];
		}

		EncryptUtil.algorithm = "AES";
		EncryptUtil.secretKey = new SecretKeySpec(keyBytes, EncryptUtil.algorithm);
	}

	/**
	 * 문자열을 복호화 하는 메소드이다.
	 * 
	 * @param sValue
	 *            - 복호화 할 문자열
	 * @return - 복호화 된 문자열
	 */
	public static String decrypt (final String sValue)
	{
		try
		{
			final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, EncryptUtil.secretKey);
			return new String(cipher.doFinal(EncryptUtil.getBase64Decode(sValue)), "UTF-8");
		}
		catch (final NoSuchAlgorithmException nsae)
		{
			Constant.getLogger().error(nsae.getLocalizedMessage(), nsae);
			return null;
		}
		catch (final NoSuchPaddingException nspe)
		{
			Constant.getLogger().error(nspe.getLocalizedMessage(), nspe);
			return null;
		}
		catch (final InvalidKeyException ike)
		{
			Constant.getLogger().error(ike.getLocalizedMessage(), ike);
			return null;
		}
		catch (final UnsupportedEncodingException uee)
		{
			Constant.getLogger().error(uee.getLocalizedMessage(), uee);
			return null;
		}
		catch (final BadPaddingException bpe)
		{
			Constant.getLogger().error(bpe.getLocalizedMessage(), bpe);
			return null;
		}
		catch (final IllegalBlockSizeException ibse)
		{
			Constant.getLogger().error(ibse.getLocalizedMessage(), ibse);
			return null;
		}
	}

	/**
	 * 문자열을 암호화 하는 메소드이다.
	 * 
	 * @param sValue
	 *            - 암호화 할 문자열
	 * @return - 함호화 된 문자열
	 */
	public static String encrypt (final String sValue)
	{
		try
		{
			final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, EncryptUtil.secretKey);
			return EncryptUtil.getBase64Encode(cipher.doFinal(sValue.getBytes("UTF-8")));
		}
		catch (final NoSuchAlgorithmException nsae)
		{
			Constant.getLogger().error(nsae.getLocalizedMessage(), nsae);
			return null;
		}
		catch (final NoSuchPaddingException nspe)
		{
			Constant.getLogger().error(nspe.getLocalizedMessage(), nspe);
			return null;
		}
		catch (final InvalidKeyException ike)
		{
			Constant.getLogger().error(ike.getLocalizedMessage(), ike);
			return null;
		}
		catch (final UnsupportedEncodingException uee)
		{
			Constant.getLogger().error(uee.getLocalizedMessage(), uee);
			return null;
		}
		catch (final BadPaddingException bpe)
		{
			Constant.getLogger().error(bpe.getLocalizedMessage(), bpe);
			return null;
		}
		catch (final IllegalBlockSizeException ibse)
		{
			Constant.getLogger().error(ibse.getLocalizedMessage(), ibse);
			return null;
		}
	}

	/**
	 * Base64로 디코딩을 하는 메소드이다.
	 * 
	 * @param base64
	 *            디코딩할 문자열
	 * @return - 디코딩된 바이트배열
	 */
	public static byte[] getBase64Decode (final String base64)
	{
		int pad = 0;
		for (int i = base64.length() - 1; base64.charAt(i) == '='; i--)
		{
			pad++;
		}
		final int length = ((base64.length() * 6) / 8) - pad;
		final byte[] raw = new byte[length];
		int rawIndex = 0;
		for (int i = 0; i < base64.length(); i += 4)
		{
			final int block = (EncryptUtil.getValue(base64.charAt(i)) << 18) + (EncryptUtil.getValue(base64.charAt(i + 1)) << 12) + (EncryptUtil.getValue(base64.charAt(i + 2)) << 6) + (EncryptUtil.getValue(base64.charAt(i + 3)));
			for (int j = 0; (j < 3) && ((rawIndex + j) < raw.length); j++)
			{
				raw[rawIndex + j] = (byte) ((block >> (8 * (2 - j))) & 0xff);
			}
			rawIndex += 3;
		}
		return raw;
	}

	/**
	 * Base64로 인코딩하는 메소드이다.
	 * 
	 * @param raw
	 *            - 인코딩할 바이트배열
	 * @return - 결과 문자열
	 */
	public static String getBase64Encode (final byte[] raw)
	{
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < raw.length; i += 3)
		{
			sb.append(EncryptUtil.encodeBlock(raw, i));
		}
		return sb.toString();
	}

	/**
	 * md5 을 두번 해서 반환하는 메소드이다.
	 * 
	 * @param s
	 * @return
	 */
	public static String getDoubleMD5 (final String s)
	{
		return EncryptUtil.getMD5(EncryptUtil.getMD5(s));
	}

	/**
	 * 문자열의 md5 값을 반환하는 메소드이다.
	 * 
	 * @param value
	 *            - md5값을 추출할 메소드
	 * @return - md5값
	 */
	public static String getMD5 (final String value)
	{
		if (StringUtil.isNullOrEmpty(value))
		{
			return value;
		}
		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			return ByteUtil.toHexString(md.digest(value.getBytes()));
		}
		catch (final NoSuchAlgorithmException nsae)
		{
			Constant.getLogger().error(nsae.getLocalizedMessage(), nsae);
			return null;
		}
	}

	/**
	 * 복호화가 안되는 암호화기법으로 암호화하는 메소드 (SHA-1 알고리즘 사용)
	 * 
	 * @param value
	 * @return
	 */
	public static String oneSideEcrypt (final String value)
	{
		if (StringUtil.isNullOrEmpty(value))
		{
			return value;
		}
		final char[] pwd = value.toCharArray();
		MessageDigest md;
		try
		{
			md = MessageDigest.getInstance("SHA-1");
			md.update(new String(pwd).getBytes("UTF-8"));
			final byte[] digested = md.digest();
			return new String(EncryptUtil.getBase64Encode(digested));
		}
		catch (final NoSuchAlgorithmException nsae)
		{
			Constant.getLogger().error(nsae.getLocalizedMessage(), nsae);
			return null;
		}
		catch (final UnsupportedEncodingException uee)
		{
			Constant.getLogger().error(uee.getLocalizedMessage(), uee);
			return null;
		}
	}

	private static char[] encodeBlock (final byte[] raw, final int offset)
	{
		int block = 0;
		final int slack = raw.length - offset - 1;
		final int end = (slack >= 2) ? 2 : slack;
		for (int i = 0; i <= end; i++)
		{
			final byte b = raw[offset + i];
			final int neuter = (b < 0) ? b + 256 : b;
			block += neuter << (8 * (2 - i));
		}
		final char[] base64 = new char[4];
		for (int i = 0; i < 4; i++)
		{
			final int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
			base64[i] = EncryptUtil.getChar(sixbit);
		}
		if (slack < 1)
		{
			base64[2] = '=';
		}
		if (slack < 2)
		{
			base64[3] = '=';
		}
		return base64;
	}

	private static char getChar (final int sixBit)
	{
		if ((0 <= sixBit) && (sixBit <= 25))
		{
			return (char) ('A' + sixBit);
		}
		if ((26 <= sixBit) && (sixBit <= 51))
		{
			return (char) ('a' + (sixBit - 26));
		}
		if ((52 <= sixBit) && (sixBit <= 61))
		{
			return (char) ('0' + (sixBit - 52));
		}
		if (sixBit == 62)
		{
			return '+';
		}
		if (sixBit == 63)
		{
			return '/';
		}
		return '?';
	}

	private static int getValue (final char c)
	{
		if (('A' <= c) && (c <= 'Z'))
		{
			return c - 'A';
		}
		if (('a' <= c) && (c <= 'z'))
		{
			return (c - 'a') + 26;
		}
		if (('0' <= c) && (c <= '9'))
		{
			return (c - '0') + 52;
		}
		if (c == '+')
		{
			return 62;
		}
		if (c == '/')
		{
			return 63;
		}
		if (c == '=')
		{
			return 0;
		}
		return -1;
	}

	private EncryptUtil ()
	{
	}
}