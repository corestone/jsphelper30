package chk.jsphelper.service.controller;

import java.util.Map;

import chk.jsphelper.Constant;
import chk.jsphelper.ObjectFactory;
import chk.jsphelper.Parameter;
import chk.jsphelper.engine.InterfaceEngine;
import chk.jsphelper.engine.MailEngine;
import chk.jsphelper.object.Mail;
import chk.jsphelper.util.DateUtil;

public class MailController
{
	public boolean sendMail (final String objectID, final Parameter param)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("sendMail id:{} 시작", objectID);
		boolean result = false;
		try
		{
			final Mail mail = ObjectFactory.getMail(objectID);
			if (mail == null)
			{
				throw new Exception("해당 오브젝트가 존재하지 않습니다");
			}
			final InterfaceEngine ie = new MailEngine(mail, param);
			ie.execute();
			final Map<String, Object> hm = ie.getValueObject();
			result = Boolean.valueOf(hm.get("RESULT").toString());
		}
		catch (final Exception e)
		{
			Constant.getLogger().error("sendMail id:{} 작업중 에러 - {}", new String[] { objectID, e.getLocalizedMessage() }, e);
		}
		finally
		{
			Constant.getLogger().debug("sendMail id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return result;
	}
}