package chk.jsphelper.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chk.jsphelper.Constant;

public final class HtmlUtil
{
	/**
	 * 문자열에서 위험스러운 태그를 &lt;block&gt; 태그로 변환하는 메소드이다.<br>
	 * 위험 태그명 리스트는 프로퍼티의 Security.blockingTag 항목에서 "," 구분자로 지정할 수 있다.
	 * 
	 * @param sValue
	 *            - 변환할 문자열
	 * @return - 변환된 문자열
	 */
	public static String blockingTag (final String sValue)
	{
		final String[] tag = Constant.getValue("Security.blockingTag", "script,iframe").split(",");
		String rtnValue = sValue;
		for (final String element : tag)
		{
			rtnValue = rtnValue.replaceAll("(?i)<" + element, "<block ");
			rtnValue = rtnValue.replaceAll("(?i)</" + element, "</block ");
		}
		return rtnValue;
	}

	/**
	 * 게시판 리스트의 타이틀이 길 때 글을 잘라내는 메소드이다
	 * 
	 * @param sValue
	 *            - 타이틀 문자열
	 * @param iLength
	 *            - 잘라낼 길이
	 * @return - 길이보다 길면 잘려진 타이틀 잘려지면 자동으로 title 속성이 붙음
	 */
	public static String cutTitle (final String sValue, final int iLength)
	{
		return HtmlUtil.cutTitle(sValue, iLength, true);
	}

	/**
	 * 게시판 리스트의 타이틀이 길 때 글을 잘라내는 메소드이다
	 * 
	 * @param sValue
	 *            - 타이틀 문자열
	 * @param iLength
	 *            - 잘라낼 길이
	 * @param bTitle
	 *            - title 속성을 달지 말지 여부
	 * @return - 길이보다 길면 잘려진 타이틀
	 */
	public static String cutTitle (final String sValue, final int iLength, final boolean bTitle)
	{
		if (sValue == null)
		{
			return "";
		}
		final int length = StringUtil.strLength(sValue);
		String val = sValue;
		if (iLength < length)
		{
			val = StringUtil.cutString(sValue, iLength) + "..";
		}
		if (bTitle)
		{
			return "<span title='" + HtmlUtil.encHTML(sValue) + "'>" + val + "</span>";
		}
		else
		{
			return val;
		}
	}

	/**
	 * 문자열에서 태그와 특수문자를 제대로 표현하기 위해 변한하는 메소드이다.
	 * 
	 * @param sValue
	 *            - 변환할 문자열
	 * @return - 변환된 문자열
	 */
	public static String encHTML (final String sValue)
	{
		String sRtn = null;
		if ((sValue == null) || "".equals(sValue))
		{
			return "";
		}
		else
		{
			sRtn = sValue.replace("\"", "&#34;");
			sRtn = sRtn.replace("&", "&#38;");
			sRtn = sRtn.replace("'", "&#39;");
			sRtn = sRtn.replace("<", "&#60;");
			sRtn = sRtn.replace(">", "&#62;");
			sRtn = sRtn.replace("\n", "<br/>");
			return sRtn;
		}
	}

	/**
	 * 줄바꿈을 &lt;br/&gt; 태그로 변환하는 메소드이다.
	 * 
	 * @param src
	 *            - 변환할 문자열
	 * @return - br 태그로 변환된 문자열
	 */
	public static String line2Br (final String src)
	{
		if (StringUtil.isNullOrEmpty(src))
		{
			return src;
		}
		return src.replace("\n", "<br/>");
	}

	/**
	 * 문자열에서 태그를 제거한 순수 문자열만을 추출하는 메소드이다.
	 * 
	 * @param str
	 *            - 태그가 포함된 문자열
	 * @return - 태그를 제거한 순수문자열
	 */
	public static String removeTag (String str)
	{
		// javascript tags and everything in between
		final Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
		final Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL);
		// HTML/XML tags
		final Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		// final Pattern nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>");
		// entity references
		final Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
		// repeated whitespace
		final Pattern WHITESPACE = Pattern.compile("\\s\\s+");

		Matcher m;

		m = SCRIPTS.matcher(str);
		str = m.replaceAll("");
		m = STYLE.matcher(str);
		str = m.replaceAll("");
		m = TAGS.matcher(str);
		str = m.replaceAll("");
		m = ENTITY_REFS.matcher(str);
		str = m.replaceAll("");
		m = WHITESPACE.matcher(str);
		str = m.replaceAll(" ");

		return str;
	}

	private HtmlUtil ()
	{
	}
}