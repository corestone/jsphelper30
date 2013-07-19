package chk.jsphelper;

import chk.jsphelper.module.wrapper.MapListAdapter;
import chk.jsphelper.util.DateUtil;
import chk.jsphelper.util.StringUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * 이 클래스는 Java의 ResultSet을 대신하는 클래스로써 SqlValue, TransactionValue에서 불러올 수 있다.
 * 
 * @author Corestone H. Kang
 * @version 1.0
 * @since 1.1
 * @see chk.jsphelper.value.SqlValue
 * @see chk.jsphelper.value.TransactionValue
 */
public class DataList extends MapListAdapter
{
	private int currentRow = -1;
	private int rowSize = 0;

	/**
	 * @param fields
	 */
	public DataList (final String[] fields)
	{
		for (final String fieldName : fields)
		{
			this.m.put(fieldName, new ArrayList<String>());
		}
	}

	/**
	 * 데이타셋의 특정 row로 이동하는 메소드이다.
	 * 
	 * @param row
	 *            - 이동할 row 수 (첫번째 행은 1)
	 * @return - 이동 성공 여부
	 */
	public final boolean absolute (final int row)
	{
		final int absRow = row < 0 ? -row : row;
		if ((absRow < 1) || (this.rowSize < absRow))
		{
			return false;
		}
		this.currentRow = (row > 0) ? (row - 1) : (this.m.size() + row);
		return true;
	}

	public void addData (final Map<String, String> data)
	{
		for (String key : this.m.keySet())
		{
			this.m.get(key).add(StringUtil.trim(data.get(key)));
		}
		this.rowSize++;
		setRows();
	}

	/**
	 * DataList의 데이타셋을 제일 처음으로 이동하고 그 결과값을 반환한다.
	 * 
	 * @return - 데이타셋 이동 성공 여부
	 */
	public final boolean first ()
	{
		if (this.m.size() == 0)
		{
			return false;
		}
		this.currentRow = 0;
		return true;
	}

	/**
	 * 레코드의 특정 인덱스(1부터 시작)의 내용을 boolean형으로 변환하여 가지고 오는 메소드<br />
	 * 값이 "Y", "1"인 경우에는 true 나머지 경우에는 false를 반환한다.
	 * 
	 * @param i
	 *            - 특정인덱스(1부터 시작)
	 * @return - 레코드 해당 인덱스의 값의 boolean형
	 */
	public final boolean getBoolean (final int i)
	{
		final String key = this.getFieldName(i);
		return ("Y".equals(this.get(key, this.currentRow)) || "1".equals(this.get(key, this.currentRow)));
	}

	/**
	 * 레코드의 특정 필드의 내용을 boolean형으로 변환하여 가지고 오는 메소드<br />
	 * 값이 "Y", "1"인 경우에는 true 나머지 경우에는 false를 반환한다.
	 * 
	 * @param key
	 *            - 특정 필드명(JDBC 기본인 대문자로 사용)
	 * @return - 레코드 해당 필드의 값의 boolean형
	 */
	public boolean getBoolean (final String key)
	{
		return ("Y".equals(this.get(key, this.currentRow)) || "1".equals(this.get(key, this.currentRow)));
	}

	/**
	 * 레코드의 특정 인덱스(1부터 시작)의 내용을 Date형으로 변환하여 가지고 오는 메소드이다.
	 * 
	 * @param i
	 *            - 특정인덱스(1부터 시작)
	 * @return - 레코드 해당 인덱스의 값의 Date형
	 */
	public Date getDate (final int i)
	{
		final String key = this.getFieldName(i);
		return DateUtil.string2Date(StringUtil.trimDefault(this.get(key, this.currentRow), "20000101"));
	}

	/**
	 * 레코드의 특정 필드의 내용을 Date형으로 변환하여 가지고 오는 메소드이다.
	 * 
	 * @param key
	 *            - 특정 필드명(JDBC 기본인 대문자로 사용)
	 * @return - 레코드 해당 필드의 값의 Date형
	 */
	public Date getDate (final String key)
	{
		return DateUtil.string2Date(StringUtil.trimDefault(this.get(key, this.currentRow), "20000101"));
	}

	/**
	 * 레코드의 특정 인덱스(1부터 시작)의 내용을 double형으로 변환하여 가지고 오는 메소드이다.
	 * 
	 * @param i
	 *            - 특정인덱스(1부터 시작)
	 * @return - 레코드 해당 인덱스의 값의 double형
	 */
	public double getDouble (final int i)
	{
		try
		{
			final String key = this.getFieldName(i);
			return Double.parseDouble(StringUtil.trimDefault(this.get(key, this.currentRow), "0"));
		}
		catch (final NumberFormatException nfe)
		{
			Constant.getLogger().error("해당 값을 double형으로 변환하는데 문제가 발생하였습니다.", nfe);
			return 0.0d;
		}
	}

	/**
	 * 레코드의 특정 필드의 내용을 double형으로 변환하여 가지고 오는 메소드
	 * 
	 * @param key
	 *            - 특정 필드명(JDBC 기본인 대문자로 사용)
	 * @return - 레코드 해당 필드의 값의 double형
	 */
	public double getDouble (final String key)
	{
		try
		{
			return Double.parseDouble(StringUtil.trimDefault(this.get(key, this.currentRow), "0"));
		}
		catch (final NumberFormatException nfe)
		{
			Constant.getLogger().error("해당 값을 double형으로 변환하는데 문제가 발생하였습니다.", nfe);
			return 0.0d;
		}
	}

	/**
	 * 레코드셋의 필드갯수를 반환하는 메소드이다.
	 * 
	 * @return - 필드갯수
	 */
	public int getFieldCount ()
	{
		return this.m.size();
	}

	/**
	 * 특정필드의 레코드값들을 문자열 배열로 받는 메소드이다.
	 * 
	 * @param index
	 *            - 필드의 인덱스 (1부터 시작한다)
	 * @return -
	 */
	public String[] getFieldData (final int index)
	{
		return this.m.get(this.getFieldName(index)).toArray(new String[] {});
	}

	/**
	 * 필드의 데이타들을 문자열 배열로 반환하는 메소드이다.
	 * 
	 * @param field
	 *            - 가져올 필드의 이름
	 * @return - 해당 필드의 전체 값을 담은 문자열 배열
	 */
	public String[] getFieldDatas (final String field)
	{
		return this.m.get(field).toArray(new String[] {});
	}

	/**
	 * 해당 인덱스에 있는 필드명을 반환한다.
	 * 
	 * @param index
	 *            - 필드명을 알고 싶은 인덱스
	 * @return - 필드명(만약 인덱스가 범위를 벗어나면 ""를 반환한다.)
	 */
	public String getFieldName (final int index)
	{
		int i = 1;
		for (String key : this.m.keySet())
		{
			if (i == index)
			{
				return key;
			}
			i++;
		}
		Constant.getLogger().warn("해당 DataList의 필드보다 더 큰 인덱스 수로 검색하였습니다");
		return "";
	}

	/**
	 * 필드명을 문자열 배열로 반환하는 메소드이다.
	 * 
	 * @return - 필드명을 담은 문자열 배열
	 */
	public String[] getFieldNames ()
	{
		final String[] fieldNames = new String[this.m.size()];
		final Iterator<String> keys = this.m.keySet().iterator();
		int i = 0;
		while (keys.hasNext())
		{
			fieldNames[i++] = keys.next();
		}
		return fieldNames;
	}

	/**
	 * 레코드의 특정 인덱스(1부터 시작)의 내용을 int형으로 변환하여 가지고 오는 메소드이다.
	 * 
	 * @param i
	 *            - 특정인덱스(1부터 시작)
	 * @return - 레코드 해당 인덱스의 값의 int형
	 */
	public int getInt (final int i)
	{
		try
		{
			final String key = this.getFieldName(i);
			return Integer.parseInt(StringUtil.trimDefault(this.get(key, this.currentRow), "0"));
		}
		catch (final NumberFormatException nfe)
		{
			Constant.getLogger().error("해당 값을 int형으로 변환하는데 문제가 발생하였습니다.", nfe);
			return 0;
		}
	}

	/**
	 * 레코드의 특정 필드의 내용을 int형으로 변환하여 가지고 오는 메소드이다.
	 * 
	 * @param key
	 *            - 특정 필드명 (자체적으로 대문자로 변환함)
	 * @return - 레코드 해당 필드의 값의 int형
	 */
	public int getInt (final String key)
	{
		try
		{
			return Integer.parseInt(StringUtil.trimDefault(this.get(key, this.currentRow), "0"));
		}
		catch (final NumberFormatException nfe)
		{
			Constant.getLogger().error("해당 값을 int형으로 변환하는데 문제가 발생하였습니다.", nfe);
			return 0;
		}
	}

	/**
	 * 현재 데이타셋의 row 을 반환하는 메소드이다.
	 * 
	 * @return - 현재 데이타셋의 row
	 */
	public int getRow ()
	{
		return this.currentRow + 1;
	}

	/**
	 * 레코드의 특정 인덱스(1부터 시작)의 내용을 가지고 오는 메소드이다.
	 * 
	 * @param i
	 *            - 특정인덱스(1부터 시작)
	 * @return - 레코드 해당 인덱스의 값
	 */
	public String getString (final int i)
	{
		final String key = this.getFieldName(i);
		return this.get(key, this.currentRow);
	}

	/**
	 * 레코드의 특정 필드의 내용을 가지고 오는 메소드이다.
	 * 
	 * @param key
	 *            - 특정 필드명(소문자로 사용해야 함)
	 * @return - 레코드 해당 필드의 값
	 */
	public String getString (final String key)
	{
		return this.get(key, this.currentRow);
	}

	/**
	 * 해당 필드명에 대한 데이타 중에서 특정 값이 있는지를 체크하여 첫번째 일치하는 레코드 인덱스를 반환하는 메소드이다.
	 * 
	 * @param fieldName
	 *            - 찾을 필드명
	 * @param data
	 *            - 찾을 데이타값
	 * @return - 해당 필드에서 데이타 값이 일치하는 첫번째 레코드 인덱스(만약 일치하는 값이 없으면 -1을 반환)
	 */
	public int indexOf (final String fieldName, final String data)
	{
		final String[] fieldData = this.getFieldDatas(fieldName);
		return ArrayUtils.indexOf(fieldData, data);
	}

	/**
	 * 현재 데이타셋이 제일 처음인지 아닌지 여부를 반환한다.
	 * 
	 * @return - 현재 데이타셋의 최초여부
	 */
	public boolean isFirst ()
	{
		return (this.currentRow == 0);
	}

	/**
	 * 현재 데이타셋이 제일 마지막인지 아닌지 여부을 반환한다.
	 * 
	 * @return - 현재 데이타셋의 마지막여부
	 */
	public boolean isLast ()
	{
		return (this.currentRow == (this.rowSize - 1));
	}

	/**
	 * DataList의 데이타셋을 제일 마지막으로 이동하고 그 결과값을 반환한다.
	 * 
	 * @return - 데이타셋 이동 성공 여부
	 */
	public boolean last ()
	{
		if (this.rowSize == 0)
		{
			return false;
		}
		this.currentRow = this.rowSize - 1;
		return true;
	}

	/**
	 * DataList의 데이타셋을 다음 데이타셋으로 이동한다.
	 * 
	 * @return - 데이타셋 이동 성공 여부
	 */
	public boolean next ()
	{
		if ((this.currentRow + 1) < this.rowSize)
		{
			this.currentRow++;
			return true;
		}
		return false;
	}

	/**
	 * DataList의 데이타셋을 이전 데이타셋으로 이동한다.
	 * 
	 * @return - 데이타셋 이동 성공 여부
	 */
	public boolean previous ()
	{
		if (0 < this.currentRow)
		{
			this.currentRow--;
			return true;
		}
		return false;
	}

	/**
	 * DataList의 로우 갯수를 반환하는 메소드이다.
	 * 
	 * @return 데이타의 로우 갯수
	 */
	@Override
	public int size ()
	{
		return this.rowSize;
	}

	private void setRows()
	{
		for (String key : this.m.keySet())
		{
			for (int i = this.m.get(key).size(); i < this.rowSize; i++)
			{
				this.m.get(key).add("");
			}
		}
	}
}