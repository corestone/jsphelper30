package chk.jsphelper.module.mapper;

/**
 * Created with IntelliJ IDEA.
 * User: Corestone
 * Date: 13. 7. 17
 * Time: 오후 2:02
 * To change this template use File | Settings | File Templates.
 */

import chk.jsphelper.DataList;
import chk.jsphelper.ObjectLoader;
import chk.jsphelper.module.pool.ConnectionPoolManager;
import chk.jsphelper.module.wrapper.ConnWrapper;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DataMapperTest
{
	private static DataMapper dm = null;

	@Before
	public void setUp () throws Exception
	{
		ObjectLoader.isSuccess();
		ConnWrapper conn = ConnectionPoolManager.getInstance().getConnection("db", "Test");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM test");
		dm = new DataMapper(rs);
		conn.close();
	}

	@Test
	public void testCreateDataList ()
	{
		try
		{
			DataList dl = dm.createDataList();
			System.out.println(dl.toString());
			Map<String, String> m = new HashMap<String, String>();
			m.put("TEST_OUT", "OUT");
			dl = dm.setOutParameter(m);
			System.out.println(dl.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testSetOutParameter ()
	{
// fail("Not yet implemented");
	}

	@Test
	public void testSetReturnParam ()
	{
// fail("Not yet implemented");
	}

}
