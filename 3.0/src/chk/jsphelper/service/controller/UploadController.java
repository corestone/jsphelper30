package chk.jsphelper.service.controller;

import chk.jsphelper.Constant;
import chk.jsphelper.ObjectFactory;
import chk.jsphelper.Parameter;
import chk.jsphelper.engine.InterfaceEngine;
import chk.jsphelper.engine.UploadEngine;
import chk.jsphelper.object.Upload;
import chk.jsphelper.util.DateUtil;
import chk.jsphelper.value.UploadValue;
import chk.jsphelper.value.setter.UploadValueSetter;

public class UploadController
{
	public UploadValue executeUpload (final String objectID, Parameter param)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("executeUpload id:{} 시작", objectID);
		UploadValueSetter uvs = null;
		try
		{
			uvs = new UploadValueSetter(objectID);
			final Upload upload = ObjectFactory.getUpload(objectID);
			final InterfaceEngine ie = new UploadEngine(upload, param);
			ie.execute();
			param = (Parameter) ie.getValueObject().get("Parameter");
			uvs.setValueObject(ie.getValueObject());
			uvs.setSuccess(true);
		}
		catch (final Exception e)
		{
			uvs.setSuccess(false);
			Constant.getLogger().error("파일 업로드 중에 에러가 발생하였습니다.", e);
		}
		finally
		{
			Constant.getLogger().debug("executeUpload id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return uvs;
	}
}