package chk.jsphelper.module.pool;

/**
 * Created with IntelliJ IDEA.
 * User: Corestone
 * Date: 13. 7. 17
 * Time: 오후 2:03
 * To change this template use File | Settings | File Templates.
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import chk.jsphelper.ObjectFactory;
import chk.jsphelper.ObjectLoader;
import chk.jsphelper.module.wrapper.ConnWrapper;

public class ConnectionPoolTest
{
	@Before
	public void setUp () throws Exception
	{
		ObjectLoader.isSuccess();
	}

	@Test
	public void testConnectionPool ()
	{
		ConnectionPool cp = new ConnectionPool(ObjectFactory.getDataSource("db"));
		assertNotNull(cp);
		assertTrue(cp.size() > 0);
		try
		{
			ConnWrapper conn = cp.getConnection("Test");
			System.out.println(conn.getMetaData().getDriverName() + " size : " + cp.size());
			cp.releaseConnection(conn, "Test", true);
			System.out.println(conn.getMetaData().getDriverName() + " size : " + cp.size());
			cp.releaseConnection(conn, "Test", false);
			System.out.println(conn.getMetaData().getDriverName() + " size : " + cp.size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
