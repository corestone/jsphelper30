package chk.jsphelper.util;

/**
 * @author Corestone H. Kang
 *         바이트에 관련된 유틸리티 메소드를 모아둔 클래스이다.
 */
public final class ByteUtil
{
	/**
	 * <p>
	 * 두 배열의 값이 동일한지 비교한다.
	 * </p>
	 * 
	 * <pre>
	 * ArrayUtils.equals(null, null)                        = true
	 * ArrayUtils.equals(["one", "two"], ["one", "two"])    = true
	 * ArrayUtils.equals(["one", "two"], ["three", "four"]) = false
	 * </pre>
	 * 
	 * @param array1
	 * @param array2
	 * @return 동일하면 <code>true</code>, 아니면 <code>false</code>
	 */
	public static boolean equalsBytes (final byte[] array1, final byte[] array2)
	{

		if (array1 == array2)
		{
			return true;
		}

		if ((array1 == null) || (array2 == null))
		{
			return false;
		}

		if (array1.length != array2.length)
		{
			return false;
		}

		for (int i = 0; i < array1.length; i++)
		{
			if (array1[i] != array2[i])
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * <p>
	 * int 형의 값을 바이트 배열(4바이트)로 변환한다.
	 * </p>
	 * 
	 * @param value
	 *            - int 형 데이타
	 * @return - 변환된 byte배열
	 */
	public static byte[] int2Bytes (final int value)
	{
		final byte[] dest = new byte[4];
		for (int i = 0; i < 4; i++)
		{
			dest[i] = (byte) (value >> ((7 - i) * 8));
		}
		return dest;
	}

	/**
	 * <p>
	 * long 형의 값을 바이트 배열(8바이트)로 변환한다.
	 * </p>
	 * 
	 * @param value
	 *            - long 형 데이타
	 * @return - 변환된 byte배열
	 */
	public static byte[] long2Bytes (final long value)
	{
		final byte[] dest = new byte[8];
		for (int i = 0; i < 8; i++)
		{
			dest[i] = (byte) (value >> ((7 - i) * 8));
		}
		return dest;
	}

	/**
	 * <p>
	 * unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
	 * </p>
	 * 
	 * <pre>
	 * ByteUtils.toHexString(null)                   = null
	 * ByteUtils.toHexString([(byte)1, (byte)255])   = "01ff"
	 * </pre>
	 * 
	 * @param bytes
	 *            -
	 *            0~255 사이의 값으로 이루어진 바이트 배열
	 * @return - 해당 바이트배열을 16진수 형태의 문자열로 바꾼 값
	 */
	public static String toHexString (final byte[] bytes)
	{
		if (bytes == null)
		{
			return null;
		}

		final StringBuilder result = new StringBuilder();
		for (final byte b : bytes)
		{
			result.append(Integer.toString((b & 0xF0) >> 4, 16));
			result.append(Integer.toString(b & 0x0F, 16));
		}
		return result.toString();
	}

	private ByteUtil ()
	{
	}
}