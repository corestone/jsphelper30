package chk.jsphelper.service.controller;

import chk.jsphelper.Constant;
import chk.jsphelper.Parameter;
import chk.jsphelper.object.enums.ObjectType;
import chk.jsphelper.util.DateUtil;

public abstract class AbstractController
{
	protected String objectID;
	protected Parameter param;
	protected ObjectType type;
	private long startTime = 0L;

	protected void afterExecute ()
	{
		Constant.getLogger().debug("execute{} id:{} 마침 - {}", new Object[] { this.type.getSymbol(), this.objectID, DateUtil.getExecutedTime(this.startTime) });
	}

	protected void beforeExecute ()
	{
		this.startTime = System.nanoTime();
		Constant.getLogger().debug("execute{} id:{} 시작", new Object[] { this.type.getSymbol(), this.objectID });
	}
}