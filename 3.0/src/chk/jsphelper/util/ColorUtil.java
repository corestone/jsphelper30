package chk.jsphelper.util;

import java.awt.Color;

public final class ColorUtil
{
	public enum MODE
	{
		DARK, LIGHT
	}

	/**
	 * 컬러 클래스를 가지고 웹칼라 방식의 문자열을 생성한다. 단 #ffcc99ff 방식의 Hex방식만 지원한다.
	 * 
	 * @param color
	 *            - 칼라 오브젝트
	 * @return # + Hex방식의 8자리 칼라코드(알파코드까지)
	 */
	public static String color2Web (final Color color)
	{

		final String rCo = Integer.toHexString(color.getRed());
		final String gCo = Integer.toHexString(color.getGreen());
		final String bCo = Integer.toHexString(color.getBlue());
		final String aCo = Integer.toHexString(color.getAlpha());
		return "#" + (rCo.length() == 1 ? "0" + rCo : rCo) + (gCo.length() == 1 ? "0" + gCo : gCo) + (bCo.length() == 1 ? "0" + bCo : bCo) + (aCo.length() == 1 ? "0" + aCo : aCo);
	}

	/**
	 * 웹 칼라 문자열을 랜덤하게 추출해 내는 메소드이다.
	 * 
	 * @return 랜덤하게 추출된 웹컬러 문자열
	 */
	public static String getRandomColor ()
	{
		final String rCo = Integer.toHexString((int) (Math.random() * 256));
		final String gCo = Integer.toHexString((int) (Math.random() * 256));
		final String bCo = Integer.toHexString((int) (Math.random() * 256));

		return "#" + rCo + gCo + bCo;
	}

	/**
	 * 웹칼라코드를 가지고 조금 더 어두운 색을 찾는 메소드이다.
	 * 
	 * @param color
	 *            - 웹칼라코드
	 * @return 변경된 칼라코드
	 */
	public static String moreColor (final String color, final MODE mode)
	{
		if (StringUtil.isNullOrEmpty(color))
		{
			return color;
		}
		final int[] c = ColorUtil.convertColorCode(color);
		switch (mode)
		{
			case DARK :
				c[0] = c[0] / 2;
				c[1] = c[1] / 2;
				c[2] = c[2] / 2;
				break;
			case LIGHT :
				c[0] = (c[0] + 256) / 2;
				c[1] = (c[1] + 256) / 2;
				c[2] = (c[2] + 256) / 2;
				break;
		}
		String temp = "#" + (c[0] < 16 ? "0" + Integer.toHexString(c[0]) : Integer.toHexString(c[0])) + (c[1] < 16 ? "0" + Integer.toHexString(c[1]) : Integer.toHexString(c[1])) + (c[2] < 16 ? "0" + Integer.toHexString(c[2]) : Integer.toHexString(c[2]));
		if (c[3] != 255)
		{
			temp += (c[3] < 16 ? "0" + Integer.toHexString(c[3]) : Integer.toHexString(c[3]));
		}
		return temp;
	}

	/**
	 * 컬러 클래스를 생성하는데 웹칼라 방식의 문자열을 가지고 생성한다. 단 #a5cc99ff 방식의 Hex방식만 지원한다.
	 * 
	 * @param webcolor
	 *            - # + Hex방식의 칼라코드값
	 * @return 칼라 오브젝트
	 */
	public static Color web2Color (final String webcolor)
	{
		final int[] colorCode = ColorUtil.convertColorCode(webcolor);
		return new Color(colorCode[0], colorCode[1], colorCode[2], colorCode[3]);
	}

	private static int[] convertColorCode (final String source)
	{
		final String temp = source.replace("#", "");
		final int[] colorCode = { 0, 0, 0, 255 };

		switch (temp.length())
		{
			case 3 :
				colorCode[0] = Integer.parseInt(temp.substring(0, 1) + temp.substring(0, 1), 16);
				colorCode[1] = Integer.parseInt(temp.substring(1, 2) + temp.substring(1, 2), 16);
				colorCode[2] = Integer.parseInt(temp.substring(2, 3) + temp.substring(2, 3), 16);
				break;
			case 4 :
				colorCode[0] = Integer.parseInt(temp.substring(0, 1) + temp.substring(0, 1), 16);
				colorCode[1] = Integer.parseInt(temp.substring(1, 2) + temp.substring(1, 2), 16);
				colorCode[2] = Integer.parseInt(temp.substring(2, 3) + temp.substring(2, 3), 16);
				colorCode[3] = Integer.parseInt(temp.substring(3, 4) + temp.substring(3, 4), 16);
				break;
			case 6 :
				colorCode[0] = Integer.parseInt(temp.substring(0, 2), 16);
				colorCode[1] = Integer.parseInt(temp.substring(2, 4), 16);
				colorCode[2] = Integer.parseInt(temp.substring(4, 6), 16);
				break;
			case 8 :
				colorCode[0] = Integer.parseInt(temp.substring(0, 2), 16);
				colorCode[1] = Integer.parseInt(temp.substring(2, 4), 16);
				colorCode[2] = Integer.parseInt(temp.substring(4, 6), 16);
				colorCode[3] = Integer.parseInt(temp.substring(6, 8), 16);
				break;
		}
		return colorCode;
	}
}