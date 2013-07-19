package chk.jsphelper.engine.ext.upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.ProgressListener;

import chk.jsphelper.Parameter;
import chk.jsphelper.util.ConvertUtil;

/**
 * @author Corestone
 */
public class FileUploadListener implements ProgressListener
{
	private volatile long bytesRead;
	private volatile long contentLength;
	private volatile String errMsg;
	private volatile Map<String, List<String>> inputName;
	private volatile long item;
	private volatile Map<String, List<String>> savedName;

	/**
	 * 
	 */
	public FileUploadListener ()
	{
		this.inputName = new HashMap<String, List<String>>();
		this.savedName = new HashMap<String, List<String>>();
		this.bytesRead = 0;
		this.contentLength = 0;
		this.item = 0;
	}

	/**
	 * @param formName
	 * @param fileName
	 */
	public void addInputName (final String formName, final String fileName)
	{
		List<String> inputFileName = this.inputName.get(formName);
		if (inputFileName == null)
		{
			inputFileName = new ArrayList<String>();
		}
		inputFileName.add(fileName);
		this.inputName.put(formName, inputFileName);
	}

	/**
	 * @param formName
	 * @param fileName
	 */
	public void addSavedName (final String formName, final String fileName)
	{
		List<String> savedFileName = this.savedName.get(formName);
		if (savedFileName == null)
		{
			savedFileName = new ArrayList<String>();
		}
		savedFileName.add(fileName);
		this.savedName.put(formName, savedFileName);
	}

	/**
	 * @return - 읽은 바이트 수
	 */
	public long getBytesRead ()
	{
		return this.bytesRead;
	}

	/**
	 * @return - 파일의 전체 크기
	 */
	public long getContentLength ()
	{
		return this.contentLength;
	}

	/**
	 * @return - 에러메시지
	 */
	public String getErrMsg ()
	{
		return this.errMsg;
	}

	/**
	 * @return - input 값에 대한 JSON 형식의 문자열
	 */
	public String getInputName2JSON ()
	{
		final Parameter param = Parameter.getInstance();
		final Iterator<String> keys = this.inputName.keySet().iterator();
		while (keys.hasNext())
		{
			final String key = keys.next();
			final String[] values = new String[this.inputName.get(key).size()];
			for (int i = 0, z = values.length; i < z; i++)
			{
				values[i] = this.inputName.get(key).get(i);
			}
			param.put(key, values);
		}
		return ConvertUtil.map2JSON(param);
	}

	/**
	 * @return - 파일 아이템 index
	 */
	public long getItem ()
	{
		return this.item;
	}

	/**
	 * @return - 세이브 되는 파일명의 JSON형식의 문자열
	 */
	public String getSavedName2JSON ()
	{
		final Parameter param = Parameter.getInstance();
		final Iterator<String> keys = this.savedName.keySet().iterator();
		while (keys.hasNext())
		{
			final String key = keys.next();
			final String[] values = new String[this.savedName.get(key).size()];
			for (int i = 0, z = values.length; i < z; i++)
			{
				values[i] = this.savedName.get(key).get(i);
			}
			param.put(key, values);
		}
		return ConvertUtil.map2JSON(param);
	}

	/**
	 * @param errMsg
	 */
	public void setErrMsg (final String errMsg)
	{
		this.errMsg = errMsg;
	}

	public void update (final long i_bytesRead, final long i_contentLength, final int i_item)
	{
		this.bytesRead = i_bytesRead;
		this.contentLength = i_contentLength;
		this.item = i_item;
	}
}