package chk.jsphelper.service.servlet;

import java.lang.reflect.Method;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chk.jsphelper.Constant;
import chk.jsphelper.Parameter;
import chk.jsphelper.ServiceCaller;
import chk.jsphelper.object.Servlet;
import chk.jsphelper.util.DateUtil;
import chk.jsphelper.value.UploadValue;

abstract public class AbstractServlet
{
	protected ServletContext context;
	protected Parameter param;
	protected HttpServletRequest req;
	protected HttpServletResponse res;
	protected final ServiceCaller sc;
	protected UploadValue upload;

	public AbstractServlet ()
	{
		this.sc = new ServiceCaller();
	}

	/**
	 * 서블릿의 해당 메소드를 실행한 다음에 실행한다.<br>
	 * 이 메소드를 오버라이드 하기 위해서는 xml에서 해당 태그에 after="true" 속성을 주어야 한다.<br>
	 * 또한 두번째 인자를 통해서 before메소드의 성공여부에 따른 후처리 작업을 할 수 있다.
	 * 
	 * @param method
	 *            - xml에 세팅한 메소드명
	 * @param isBeforeSucc
	 *            - doBefore 메소드의 성공여부
	 * @throws Exception
	 */
	abstract protected void doAfter (final String method, final boolean isBeforeSucc) throws Exception;

	/**
	 * 서블릿의 해당 메소드를 실행하기 전에 실행한다.<br>
	 * 이 메소드를 오버라이드 하기 위해서는 xml에서 해당 태그에 before="true" 속성을 주어야 한다.
	 * 
	 * @param method
	 *            - xml에 세팅한 메소드명
	 * @return - true이면 실질적인 실행 메소드가 실행되고 false이면 중지된다. 후처리는 doAfter 메소드에서 하면 된다.
	 * @throws Exception
	 */
	abstract protected boolean doBefore (final String method) throws Exception;

	final void process (final HttpServletRequest req, final HttpServletResponse res, final Servlet object, final ServletContext context) throws Exception
	{
		String jspPath = null;
		this.req = req;
		this.res = res;
		this.context = context;
		final String method = object.getMethod();
		final String uploadID = object.getUpload();
		// 서블릿을 초기화 한다. 단 업로드 작업이 있으면 업로드 작업도 같이 한다.
		this.doInit(uploadID);
		boolean isBeforeSucc = true;
		// 사전 작업이 실행되도록 설정되어 있으면 사전 작업을 실행함 여기에서 리턴되는 값이 true이면 메인 메소드를 실행하고 false이면 스킵을 한다.
		if (object.isBefore())
		{
			if (this.doBefore(method))
			{
				jspPath = this.callMethod(method);
			}
			else
			{
				isBeforeSucc = false;
			}
		}
		else
		{
			jspPath = this.callMethod(method);
		}
		// 사후 작업이 실행되도록 설정되어 있으면 사후 작업을 실행함.
		if (object.isAfter())
		{
			this.doAfter(method, isBeforeSucc);
		}
		if (jspPath != null)
		{
			if (jspPath.startsWith("/"))
			{
				this.sendJSP(jspPath);
			}
			else
			{
				this.sendJSP(Constant.getValue("Path.JspRoot", "/WEB-INF/jsp/") + jspPath);
			}
		}
	}

	/**
	 * @return
	 */
	private String callMethod (final String method) throws Exception
	{
		String returnURI = null;
		try
		{
			final Class<?>[] args1 = new Class<?>[0];
			final Object[] args2 = new Object[0];
			final Method m = this.getClass().getMethod(method, args1);
			returnURI = (String) m.invoke(this, args2);
		}
		catch (final Exception e)
		{
			throw e;
		}
		return returnURI;
	}

	/**
	 * 서블릿 실행을 위해 초기값 세팅을 하는 메소드이다.<br>
	 * 멀티파트 form encoding을 사용하는 경우 이 메소드를 실행한다.
	 * 
	 * @param uploadID
	 *            - 업로드 오브젝트의 id 값
	 * @throws Exception
	 *             - 예외 처리를 위한 것
	 */
	private void doInit (final String uploadID) throws Exception
	{
		try
		{
			this.param = new Parameter(this.req);
			if ((uploadID != null) && !uploadID.equals(""))
			{
				this.upload = this.sc.executeUpload(uploadID, this.param);
			}
		}
		catch (final Exception e)
		{
			throw e;
		}
	}

	/**
	 * 파라미터 정보를 request에 담고 해당 jsp 파일로 forward하는 메소드이다.<br>
	 * 이 메소드에서 기본적으로 Request에 세팅되는 속성들이 있다.<br>
	 * _prvURI = 이 클래스를 호출한 경로<br>
	 * _thisURIs = 호출할 JSP페이지의 경로를 "/"로 구분한 배열<br>
	 * _today = 현재 서버 날자 (yyyy-MM-dd)<br>
	 * _param = 파라미터 객체<br>
	 * 
	 * @param jsp_file
	 *            - 포워드될 jsp파일의 절대 경로
	 */
	private void sendJSP (final String jsp_file) throws Exception
	{
		final String thisTime = DateUtil.getCurrentDateTime();
		this.req.setAttribute("_clientPath", Constant.getValue("Path.Client", "/jsphelper/"));
		this.req.setAttribute("_prvURI", this.req.getRequestURI() + "?" + this.param.getString());
		this.req.setAttribute("_today", thisTime.substring(0, 4) + "-" + thisTime.substring(4, 6) + "-" + thisTime.substring(6, 8));
		this.req.setAttribute("_thisURIs", jsp_file.split("/"));
		this.req.setAttribute("_param", this.param);
		final RequestDispatcher rd = this.req.getRequestDispatcher(jsp_file);
		Constant.getLogger().debug("서블릿에서 호출하는 JSP 파일 : {}", jsp_file);
		try
		{
			rd.forward(this.req, this.res);
		}
		catch (final Exception e)
		{
			Constant.getLogger().error("ServletID가 [{}]의 결과페이지 {}의 sendJSP() 에서 에러 발생\n{}", new String[] { this.param.getValue("id"), jsp_file, e.getLocalizedMessage() }, e);
			throw e;
		}
	}
}