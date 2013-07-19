package chk.jsphelper.module.runnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chk.jsphelper.Constant;
import chk.jsphelper.ObjectFactory;
import chk.jsphelper.module.pool.ConnectionPoolManager;
import chk.jsphelper.module.wrapper.ConnWrapper;
import chk.jsphelper.object.DataSource;

public class MaintainDBConn extends AbstractRunnable
{
	private final String poolName;
	private final String query;

	public MaintainDBConn (final int interval, final String query, final String poolName)
	{
		super(interval);
		this.query = query;
		this.poolName = poolName;
	}

	@Override
	public void run ()
	{
		final DataSource datasource = ObjectFactory.getDataSource(this.poolName);
		while (this.sleepInterval())
		{
			for (int i = 0; i < datasource.getIdlesize(); i++)
			{
				ConnWrapper conn = null;
				Statement stmt = null;
				try
				{
					conn = ConnectionPoolManager.getInstance().getConnection(datasource.getId(), datasource.getId() + " Repeat");
					stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					stmt.executeQuery(this.query);
				}
				catch (final Exception e)
				{
					Constant.getLogger().error("RepeatQuery 스레드에서 쿼리실행에 대한 에러 발생", e);
				}
				finally
				{
					if (stmt != null)
					{
						try
						{
							stmt.close();
							stmt = null;
						}
						catch (final SQLException sqle)
						{
						}
					}
					try
					{
						ConnectionPoolManager.getInstance().releaseConnection(datasource.getId(), conn, datasource.getId() + " Repeat", true);
					}
					catch (final Exception e)
					{
					}
				}
			}

		}
	}
}