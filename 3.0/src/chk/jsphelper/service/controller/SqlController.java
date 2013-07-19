package chk.jsphelper.service.controller;

import java.util.Map;

import chk.jsphelper.Constant;
import chk.jsphelper.DataList;
import chk.jsphelper.ObjectFactory;
import chk.jsphelper.Parameter;
import chk.jsphelper.engine.InterfaceEngine;
import chk.jsphelper.engine.SqlEngine;
import chk.jsphelper.module.mapper.DataMapper;
import chk.jsphelper.module.pool.ConnectionPoolManager;
import chk.jsphelper.module.wrapper.ConnWrapper;
import chk.jsphelper.object.Sql;
import chk.jsphelper.util.DateUtil;
import chk.jsphelper.value.SqlValue;
import chk.jsphelper.value.setter.SqlValueSetter;

public class SqlController
{
	public SqlValue executeSql (final String objectID, final Parameter param)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("executeSql id:{} 시작", objectID);
		final Sql sql = ObjectFactory.getSql(objectID);
		SqlValueSetter svs = null;
		ConnWrapper conn = null;
		try
		{
			conn = ConnectionPoolManager.getInstance().getConnection(sql.getDatasourceName(), objectID);
			final InterfaceEngine ie = new SqlEngine(conn, sql, param, null);
			ie.execute();

			svs = this.setValue(sql, ie.getValueObject());
			svs.setSuccess(true);
		}
		catch (final Exception e)
		{
			svs = new SqlValueSetter(objectID);
			svs.setSuccess(false);
			Constant.getLogger().error(e.getLocalizedMessage(), e);
		}
		finally
		{
			ConnectionPoolManager.getInstance().releaseConnection(sql.getDatasourceName(), conn, objectID, svs.isSuccess());
			Constant.getLogger().debug("executeSql id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return svs;
	}

	public SqlValue executeSql (final String objectID, final Parameter param, final ConnWrapper conn)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("executeSql id:{} 시작", objectID);
		final Sql sql = ObjectFactory.getSql(objectID);
		SqlValueSetter svs = null;
		try
		{
			final InterfaceEngine ie = new SqlEngine(conn, sql, param, null);
			ie.execute();

			svs = this.setValue(sql, ie.getValueObject());
			svs.setSuccess(true);
		}
		catch (final Exception e)
		{
			svs = new SqlValueSetter(objectID);
			svs.setSuccess(false);
			Constant.getLogger().error(e.getLocalizedMessage(), e);
		}
		finally
		{
			Constant.getLogger().debug("executeSql id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return svs;
	}

	private SqlValueSetter setValue (final Sql sql, final Map<String, Object> m) throws Exception
	{
		DataList dl = null;
		final SqlValueSetter svs = new SqlValueSetter(sql.getId());
		@SuppressWarnings ("unchecked")
		final Map<String, String> outParameter = (Map<String, String>) m.get("outParameter");
		final DataMapper dm = (DataMapper) m.get("DataMapper");
		if (dm != null)
		{
			if (outParameter == null)
			{
				dl = dm.createDataList();
			}
			else
			{
				dl = dm.setOutParameter(outParameter);
			}
		}
		m.put("DataList", dl);
		svs.setValueObject(m);
		return svs;
	}
}