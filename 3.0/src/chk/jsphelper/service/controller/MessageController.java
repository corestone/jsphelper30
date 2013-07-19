package chk.jsphelper.service.controller;

import chk.jsphelper.Constant;
import chk.jsphelper.ObjectFactory;
import chk.jsphelper.Parameter;
import chk.jsphelper.module.mapper.ParameterMapper;
import chk.jsphelper.object.Message;

public class MessageController
{
	public String getMessage (final String objectID)
	{
		final String language = Constant.getValue("Message.InitLanguage", "ko-KR");
		final Message message = ObjectFactory.getMessage(objectID);
		try
		{
			if (message == null)
			{
				return "";
			}
			if (!message.existsLang(language))
			{
				Constant.getLogger().warn("[{}]에 '{}'에 해당하는 메시지 언어가 정의되어 있지 않습니다.", new Object[] { objectID, language });
				return "";
			}
		}
		catch (final Exception e)
		{
			Constant.getLogger().error("메시지를 가지고 올 때 문제가 발생하였습니다.", e);
		}
		return message.getText(language);
	}

	public String getMessage (final String objectID, final Parameter param)
	{
		Constant.getLogger().debug("getMessage id:{} 시작", objectID);
		String language = "";
		// 브라우저의 언어설정에서 언어코드를 읽어 옴
		if (param.getRequest() != null)
		{
			language = param.getRequest().getHeader("Accept-Language");
		}
		final Message message = ObjectFactory.getMessage(objectID);
		final ParameterMapper pm = new ParameterMapper(param);
		try
		{
			if (message == null)
			{
				return "";
			}
			if (!message.existsLang(language))
			{
				Constant.getLogger().warn("[{}]에 '{}'에 해당하는 메시지 언어가 정의되어 있지 않습니다.", new Object[] { objectID, language });
				language = Constant.getValue("Message.InitLanguage", "ko-KR");
			}
		}
		catch (final Exception e)
		{
			Constant.getLogger().error("메시지를 가지고 올 때 문제가 발생하였습니다.", e);
		}
		return pm.convertMappingText(message.getText(language));
	}

	public String getMessage (final String objectID, final Parameter param, final String language)
	{
		// 브라우저의 언어설정을 읽어와야 하는데 리퀘스트 객체가 없는 경우
		String lang = language;
		final Message message = ObjectFactory.getMessage(objectID);
		final ParameterMapper pm = new ParameterMapper(param);
		try
		{
			if (message == null)
			{
				return "";
			}
			if (!message.existsLang(language))
			{
				Constant.getLogger().warn("[{}]에 '{}'에 해당하는 메시지 언어가 정의되어 있지 않습니다.", new Object[] { objectID, language });
				lang = Constant.getValue("Message.InitLanguage", "ko-KR");
			}
		}
		catch (final Exception e)
		{
			Constant.getLogger().error("메시지를 가지고 올 때 문제가 발생하였습니다.", e);
		}
		return pm.convertMappingText(message.getText(lang));
	}
}