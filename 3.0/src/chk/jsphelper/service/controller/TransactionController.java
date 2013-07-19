package chk.jsphelper.service.controller;

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
import chk.jsphelper.object.Transaction;
import chk.jsphelper.util.DateUtil;
import chk.jsphelper.util.StringUtil;
import chk.jsphelper.value.TransactionValue;
import chk.jsphelper.value.setter.TransactionValueSetter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TransactionController
{
	private DataList[] dl;
	private int[] totalSize;
	private int[] updateCount;

	public TransactionValue executeTransaction (final String objectID, final Parameter param)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("executeTransaction id:{} 시작", objectID);
		final Transaction transaction = ObjectFactory.getTransaction(objectID);
		final int sqlSize = transaction.getSize();
		this.dl = new DataList[sqlSize];
		this.updateCount = new int[sqlSize];
		this.totalSize = new int[sqlSize];
		TransactionValueSetter tvs = null;
		final Map<String, ConnWrapper> conns = new HashMap<String, ConnWrapper>();
		String connKey = null;
		try
		{
			for (int i = 0; i < sqlSize; i++)
			{
				if (transaction.getPageIndex(i) > -1)
				{
					param.setPaging(transaction.getPageIndex(i));
				}
				for (int j = 0, y = transaction.getParamKeys(i).size(); j < y; j++)
				{
					param.put(transaction.getParamKeys(i).get(j), transaction.getParamValues(i).get(j));
				}
				final Sql object = transaction.getSql(i);
				if (!conns.containsKey(object.getDatasourceName()))
				{
					final ConnWrapper conn = ConnectionPoolManager.getInstance().getConnection(object.getDatasourceName(), objectID);
					conns.put(object.getDatasourceName(), conn);
				}
				final InterfaceEngine ie = new SqlEngine(conns.get(object.getDatasourceName()), object, param, objectID);
				ie.execute();
				this.convertData(i, transaction, param, ie.getValueObject());
			}
			tvs = this.setValue(transaction.getId(), sqlSize);
			tvs.setSuccess(true);
		}
		catch (final Exception e)
		{
			tvs = new TransactionValueSetter(transaction.getId(), sqlSize);
			tvs.setSuccess(false);
			Constant.getLogger().error(e.getLocalizedMessage(), e);
		}
		finally
		{
			final Iterator<String> elements = conns.keySet().iterator();
			while (elements.hasNext())
			{
				connKey = elements.next();
				ConnectionPoolManager.getInstance().releaseConnection(connKey, conns.get(connKey), objectID, tvs.isSuccess());
			}
			Constant.getLogger().debug("executeJdbc id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return tvs;
	}

	public TransactionValue executeTransaction (final String objectID, final Parameter param, final ConnWrapper conn)
	{
		final long stime = System.nanoTime();
		Constant.getLogger().debug("executeJdbc id:{} 시작", objectID);
		final Transaction transaction = ObjectFactory.getTransaction(objectID);
		final int sqlSize = transaction.getSize();
		this.dl = new DataList[sqlSize];
		this.updateCount = new int[sqlSize];
		this.totalSize = new int[sqlSize];
		TransactionValueSetter tvs = null;
		try
		{
			for (int i = 0; i < sqlSize; i++)
			{
				if (transaction.getPageIndex(i) != -1)
				{
					param.setPaging(transaction.getPageIndex(i));
				}
				for (int j = 0, y = transaction.getParamKeys(i).size(); j < y; j++)
				{
					param.put(transaction.getParamKeys(i).get(j), transaction.getParamValues(i).get(j));
				}
				final Sql object = transaction.getSql(i);
				final InterfaceEngine ie = new SqlEngine(conn, object, param, objectID);
				ie.execute();
				this.convertData(i, transaction, param, ie.getValueObject());
			}
			tvs = this.setValue(transaction.getId(), sqlSize);
			tvs.setSuccess(true);
		}
		catch (final Exception e)
		{
			tvs = new TransactionValueSetter(transaction.getId(), sqlSize);
			tvs.setSuccess(false);
			Constant.getLogger().error(e.getLocalizedMessage(), e);
		}
		finally
		{
			Constant.getLogger().debug("executeJdbc id:{} 마침 - {}", new Object[] { objectID, DateUtil.getExecutedTime(stime) });
		}
		return tvs;
	}

	private void convertData (final int index, final Transaction transaction, final Parameter param, final Map<String, Object> result) throws Exception
	{
		final DataMapper dm = (DataMapper) result.get("DataMapper");
		@SuppressWarnings ("unchecked")
		final Map<String, String> outParameter = (Map<String, String>) result.get("outParameter");
		final Sql sql = transaction.getSql(index);
		final String srcEnc = StringUtil.trimDefault(sql.getSrcenc(), transaction.getSrcenc());
		final String trgEnc = StringUtil.trimDefault(sql.getTrgenc(), transaction.getSrcenc());
		if (dm != null)
		{
			if (outParameter == null)
			{
				this.dl[index] = dm.createDataList();
			}
			else
			{
				this.dl[index] = dm.setOutParameter(outParameter);
			}
			dm.setReturnParam(transaction.getReturnKeys(index), transaction.getReturnFields(index), param, this.dl[index]);
		}
		this.updateCount[index] = (Integer) result.get("updateCount");
		this.totalSize[index] = (Integer) result.get("pagingTotalSize");
	}

	private TransactionValueSetter setValue (final String objectID, final int sqlSize) throws Exception
	{
		final TransactionValueSetter tvs = new TransactionValueSetter(objectID, sqlSize);
		final Map<String, Object> m = new HashMap<String, Object>();
		m.put("DataList", this.dl);
		m.put("updateCount", this.updateCount);
		m.put("pagingTotalSize", this.totalSize);
		tvs.setValueObject(m);
		return tvs;
	}
}