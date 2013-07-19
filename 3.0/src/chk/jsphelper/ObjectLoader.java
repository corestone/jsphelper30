package chk.jsphelper;

import chk.jsphelper.object.*;
import chk.jsphelper.object.enums.ObjectType;
import chk.jsphelper.object.sub.*;
import org.apache.commons.digester3.Digester;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Corestone H. Kang
 *         XML로부터 오브젝트를 매핑하여서 생성하는 클래스이다.<br>
 *         최초에 자동으로 실행되며 요청시마다 XML의 변경사항을 반영한다.<br>
 *         단 DataSource 오브젝트는 최초에 한번만 반영된다.
 */
public class ObjectLoader
{
	private static final Digester digester = new Digester();
	private static final Map<ObjectType, Map<String, Long>> OBJECT_MODIFED_TIME = new HashMap<ObjectType, Map<String, Long>>();
	private static boolean parseSuccess = true;
	private static final SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static URL url = null;

	static
	{
		try
		{
			ObjectLoader.url = new URL("/chk/jsphelper/resources/object-1.1.dtd");
			ObjectLoader.digester.register("-//Onuju Software Foundation//DTD Struts Configuration 1.0//EN", ObjectLoader.url.toString());
		}
		catch (final MalformedURLException murle)
		{
		}
		catch (final Exception e)
		{
			Constant.getLogger().error(e.getLocalizedMessage(), e);
		}
		ObjectLoader.digester.setValidating(true);
		ObjectLoader.mappingDataSource();
		ObjectLoader.reloadXml();
	}

	/**
	 * @return - 오브젝트 XML의 로드가 최종적으로 성공했는지를 여부
	 */
	public static boolean isSuccess ()
	{
		return ObjectLoader.parseSuccess;
	}

	/**
	 * XML은 기본적으로 웹서버가 구동될 때 자동으로 메모리에 적재된다.<br>
	 * 단 XML이 수정되면 자동으로 시스템에 반영되지 않으므로 해당 메소드를 통해 다시 메모리에 오브젝트를 적재해야 하는데<br>
	 * 그 역할을 수행하는 것이 바로 이 메소드이다.
	 * 
	 * @return - 매핑 성공 여부
	 */
	public static boolean reloadXml ()
	{
		ObjectLoader.parseSuccess = true;

		ObjectLoader.parseSuccess = ObjectLoader.mappingExcel() ? ObjectLoader.parseSuccess : false;
		ObjectLoader.parseSuccess = ObjectLoader.mappingSql() ? ObjectLoader.parseSuccess : false;
		ObjectLoader.parseSuccess = ObjectLoader.mappingTransaction() ? ObjectLoader.parseSuccess : false;
		ObjectLoader.parseSuccess = ObjectLoader.mappingServlet() ? ObjectLoader.parseSuccess : false;
		ObjectLoader.parseSuccess = ObjectLoader.mappingUpload() ? ObjectLoader.parseSuccess : false;
		ObjectLoader.parseSuccess = ObjectLoader.mappingMail() ? ObjectLoader.parseSuccess : false;

		return ObjectLoader.parseSuccess;
	}

	/**
	 * 해당 오브젝트 파일이 수정이 일어났는지를 판단하는 메소드이다.<br>
	 * 시간을 가지고 판단하기 때문에 이전시간으로 수정되면 XML파일이 반영되지 않는다.
	 * 
	 * @param type
	 *            - 오브젝트 타입
	 * @param file
	 *            - 파일 오브젝트
	 * @return - 수정이 이루어졌는지 여부
	 */
	private static synchronized boolean isObjectChanged (final ObjectType type, final File file)
	{
		final Map<String, Long> hm = ObjectLoader.OBJECT_MODIFED_TIME.get(type);
		final String fileName = file.getName();
		final long fileModifiedTime = file.lastModified();
		if ((hm == null) || (hm.get(fileName) == null))
		{
			Constant.getLogger().info("{} xml 파일 '{}'을 처음으로 로드하여 반영합니다.", new Object[] { type, file.getPath() });
			return true;
		}
		else if (hm.get(fileName).longValue() < fileModifiedTime)
		{
			final Calendar c1 = Calendar.getInstance(Locale.KOREA);
			final Calendar c2 = Calendar.getInstance(Locale.KOREA);

			c1.setTimeInMillis(hm.get(file).longValue());
			c2.setTimeInMillis(fileModifiedTime);

			Constant.getLogger().info("{} xml 파일 '{}'의 최종수정시간({})이 원래 수정시간({})보다 최근인 것으로 xml를 로드하여 반영합니다.", new Object[] { type, file, ObjectLoader.sdfFull.format(c2.getTime()), ObjectLoader.sdfFull.format(c1.getTime()) });
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 데이타소스 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingDataSource ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/datasource", DataSource.class);
		ObjectLoader.digester.addSetProperties("service/datasource");
		ObjectLoader.digester.addObjectCreate("service/datasource/property", DataSourceProperty.class);
		ObjectLoader.digester.addSetProperties("service/datasource/property");
		ObjectLoader.digester.addSetNext("service/datasource/property", "addProperty");
		ObjectLoader.digester.addSetNext("service/datasource", "putObject");

		final String[] files = Constant.getValue("XML.DataSource", "datasource.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType._DATASOURCE);
	}

	/**
	 * 엑셀 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingExcel ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/excel", Excel.class);
		ObjectLoader.digester.addSetProperties("service/excel");
		ObjectLoader.digester.addObjectCreate("service/excel/coltype", ExcelColtype.class);
		ObjectLoader.digester.addSetProperties("service/excel/coltype");
		ObjectLoader.digester.addSetNext("service/excel/coltype", "addColType");
		ObjectLoader.digester.addSetNext("service/excel", "putObject");

		final String[] files = Constant.getValue("XML.Excel", "excel.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.EXCEL);
	}

	/**
	 * 메일 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingMail ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/mail", Mail.class);
		ObjectLoader.digester.addSetProperties("service/mail");
		ObjectLoader.digester.addBeanPropertySetter("service/mail", "body");
		ObjectLoader.digester.addObjectCreate("service/mail/from", MailFrom.class);
		ObjectLoader.digester.addSetProperties("service/mail/from");
		ObjectLoader.digester.addSetNext("service/mail/from", "putFrom");
		ObjectLoader.digester.addObjectCreate("service/mail/to", MailTo.class);
		ObjectLoader.digester.addSetProperties("service/mail/to");
		ObjectLoader.digester.addSetNext("service/mail/to", "putTo");
		ObjectLoader.digester.addObjectCreate("service/mail/content", MailContent.class);
		ObjectLoader.digester.addSetProperties("service/mail/content");
		ObjectLoader.digester.addSetNext("service/mail/content", "putContent");
		ObjectLoader.digester.addSetNext("service/mail", "putObject");

		final String[] files = Constant.getValue("XML.Mail", "mail.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.MAIL);
	}

	/**
	 * 서블릿 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingServlet ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/servlet", Servlet.class);
		ObjectLoader.digester.addSetProperties("service/servlet");
		ObjectLoader.digester.addSetNext("service/servlet", "putObject");

		final String[] files = Constant.getValue("XML.Servlet", "servlet.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.SERVLET);
	}

	/**
	 * 쿼리 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingSql ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/sql", Sql.class);
		ObjectLoader.digester.addSetProperties("service/sql");
		ObjectLoader.digester.addBeanPropertySetter("service/sql", "query");
		ObjectLoader.digester.addObjectCreate("service/sql/bind", SqlBind.class);
		ObjectLoader.digester.addSetProperties("service/sql/bind");
		ObjectLoader.digester.addSetNext("service/sql/bind", "addBind");
		ObjectLoader.digester.addObjectCreate("service/sql/clob", SqlClob.class);
		ObjectLoader.digester.addSetProperties("service/sql/clob");
		ObjectLoader.digester.addSetNext("service/sql/clob", "addClob");
		ObjectLoader.digester.addSetNext("service/sql", "putObject");

		final String[] files = Constant.getValue("XML.Sql", "sql.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.SQL);
	}

	/**
	 * 쿼리트랜젝션 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingTransaction ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/transaction", Transaction.class);
		ObjectLoader.digester.addSetProperties("service/transaction");
		ObjectLoader.digester.addObjectCreate("service/transaction/sqls", TransactionSqls.class);
		ObjectLoader.digester.addSetProperties("service/transaction/sqls");
		ObjectLoader.digester.addObjectCreate("service/transaction/sqls/param", TransactionSqlsParam.class);
		ObjectLoader.digester.addSetProperties("service/transaction/sqls/param");
		ObjectLoader.digester.addSetNext("service/transaction/sqls/param", "addParams");
		ObjectLoader.digester.addObjectCreate("service/transaction/sqls/return", TransactionSqlsReturn.class);
		ObjectLoader.digester.addSetProperties("service/transaction/sqls/return");
		ObjectLoader.digester.addSetNext("service/transaction/sqls/return", "addReturns");
		ObjectLoader.digester.addSetNext("service/transaction/sqls", "addSqls");
		ObjectLoader.digester.addSetNext("service/transaction", "putObject");

		final String[] files = Constant.getValue("XML.Transaction", "transaction.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.TRANSACTION);
	}

	/**
	 * 업로드 매핑 작업을 하는 메소드이다.
	 * 
	 * @return 매핑 성공 여부
	 */
	private static boolean mappingUpload ()
	{
		ObjectLoader.digester.addObjectCreate("service", ObjectFactory.class);
		ObjectLoader.digester.addObjectCreate("service/upload", Upload.class);
		ObjectLoader.digester.addSetProperties("service/upload");
		ObjectLoader.digester.addSetNext("service/upload", "putObject");

		final String[] files = Constant.getValue("XML.Upload", "upload.xml").split(",");
		return ObjectLoader.parseXml(files, ObjectType.UPLOAD);
	}

	/**
	 * XML 파일을 파싱하여 오브젝트에 담아내는 메소드이다.
	 * 
	 * @param files
	 *            - XML 파일명 배열
	 * @param type
	 *            - 파싱할 오브젝트 타입
	 * @return - 파싱성공여부
	 */
	private static boolean parseXml (final String[] files, final ObjectType type)
	{
		boolean returnValue = true;
		File xmlFile = null;
		for (final String element : files)
		{
			xmlFile = new File(Constant.getValue("Path.WebRoot", "/home") + Constant.getValue("Path.ConfigFile", "/WEB-INF/xml/") + element);
			if (ObjectLoader.isObjectChanged(type, xmlFile))
			{
				try
				{
					ObjectLoader.digester.parse(xmlFile);
					ObjectLoader.setModifiedTime(type, xmlFile);
				}
				catch (final Exception e)
				{
					Constant.getLogger().error("{}Object 파싱 중에 {} 예외 발생 : {}", new String[] { type.getSymbol(), e.getClass().getName(), e.getLocalizedMessage() }, e);
					returnValue = false;
				}
			}
		}
		ObjectLoader.digester.clear();
		Constant.getLogger().info("등록된 {}Object는 총 {}개 입니다.", new Object[] { type.getSymbol(), ObjectFactory.size(type) });
		return returnValue;
	}

	/**
	 * XML 파일의 변경일자를 제일 최신의 변경일자로 변경해 주는 메소드이다.
	 * 
	 * @param type
	 *            - 오브젝트 타입
	 * @param file
	 *            - XML파일 오브젝트
	 */
	private static synchronized void setModifiedTime (final ObjectType type, final File file)
	{
		final Map<String, Long> hm = ObjectLoader.OBJECT_MODIFED_TIME.get(type);
		final String fileName = file.getName();
		final long fileModifiedTime = file.lastModified();

		if (hm == null)
		{
			final Map<String, Long> hmTemp = new HashMap<String, Long>();
			hmTemp.put(fileName, fileModifiedTime);
			ObjectLoader.OBJECT_MODIFED_TIME.put(type, hmTemp);
		}
		else
		{
			hm.put(fileName, fileModifiedTime);
			ObjectLoader.OBJECT_MODIFED_TIME.put(type, hm);
		}
	}

	private ObjectLoader ()
	{
	}
}