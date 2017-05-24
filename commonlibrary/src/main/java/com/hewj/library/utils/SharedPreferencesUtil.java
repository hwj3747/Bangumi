package com.hewj.library.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

/**
 * 两种数据存储方式，
 * 1.放在SharePreference 里面，
 * 2.放在data/data/<PackageName>/file/ 里面。 read/Write方法
 * @author geolo
 *
 */
// TODO 在key前面加上用户唯一标识
public class SharedPreferencesUtil {

	public static final String SHARED_TENANTCODE = "tenantCode";

	private static SharedPreferencesUtil mInstance;
	private SharedPreferences sp;
	private Editor editor;
	private final static String SP_NAME = "SP_ShareData";


	private SharedPreferencesUtil(Context context) {
		try {
			// 构建其它应用程序的上下文对象，用于取得应用的共享配置信息，第二个参数意思是忽略安全检查
			context = context.createPackageContext(context.getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
		} catch (Exception e) {
			Log.e("SharedPreferencesUtil", "SharedPreferencesUtil() -- 配置初始化异常" + e.getMessage()) ;
		}
		// 得到其它应用程序的共享参数配置对象
		sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public static SharedPreferencesUtil getInstance(Context context){
		if(mInstance == null){
			mInstance = new SharedPreferencesUtil(context);
		}
		return mInstance;
	}

	public boolean putString(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public String getString(String key) {
		String str = null;
		str = sp.getString(key, null);
		return str;
	}

	public String getString(String key, String def) {
		String str = def;
		str = sp.getString(key, def);
		return str;
	}

	public boolean putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public boolean getBoolean(String key) {
		boolean res = false;
		res = sp.getBoolean(key, false);
		return res;
	}

	public boolean putInt(String key, int value) {
		editor.putInt(key, value);
		return editor.commit();
	}

	public int getInt(String key, int def) {
		int str = -1;
		str = sp.getInt(key, def);
		return str;
	}
	public boolean putFloat(String key, float value) {
		editor.putFloat(key, value);
		return editor.commit();
	}

	public float getFloat(String key,float def) {
		float str = -1;
		str = sp.getFloat(key, def);
		return str;
	}
	public boolean putLong(String key, long value) {
		editor.putLong(key, value);
		return editor.commit();
	}

	public long getLong(String key, long def) {
		long str = -1;
		str = sp.getLong(key, def);
		return str;
	}

    public void remove(String s) {
        editor.remove(s);
        editor.commit();
    }

    @TargetApi(android.os.Build.VERSION_CODES.HONEYCOMB)
	public boolean putStringSet(String key, Set<String> setValue){
		editor.putStringSet(key, setValue);
		return editor.commit();
	}

	// 默认
	public boolean getBoolean(String key, boolean def) {
		boolean res = def;
		res = sp.getBoolean(key, def);
		return res;
	}

	public boolean contains(String key) {
		return sp.contains(key);
	}

	Object key = new Object();

	/**
	 * 不要使用,推荐使用 getInstance();
	 * 默认建立在data/data/xxx/file/
	 * @param values
	 */
	@Deprecated
	public static final void WriteSettings(Context context, String key , byte[] values) {
		synchronized (key) {
			FileOutputStream fOut = null;
			try {
				//默认建立在data/data/xxx/file/
				fOut = context.openFileOutput(key + ".dat", Context.MODE_PRIVATE);
				fOut.write(values);
				fOut.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fOut.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static final Object ReadSettings(Context context , String key) {


		synchronized (key) {


			try {
				FileInputStream inStream = context.openFileInput(key + ".dat");
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inStream.read(buffer)) != -1) {
					stream.write(buffer, 0, length);
				}
				stream.close();
				inStream.close();

				return ObjectAndClass.ByteToObject(stream.toByteArray());

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {

			} catch (Exception e) {
			}
			return null;
//			FileInputStream fIn = null;
//			byte[] buffer = new byte[1024];
//			try {
//				//此处调用并没有区别，但context此时是从程序A里面获取的
//				fIn = context.openFileInput(key + ".dat");
//				fIn.read(buffer);
//				return
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if(fIn != null){
//						fIn.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			return null;
		}

	}

	public static final void WriteString(Context context, String key , String value){
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try {
			//默认建立在data/data/xxx/file/
			fOut = context.openFileOutput(key + ".dat", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(value);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static final String ReadString(Context context , String key) {
		FileInputStream fIn = null;
		InputStreamReader isr = null;
		String data = null;
		try {
			//此处调用并没有区别，但context此时是从程序A里面获取的
			File file = context.getFileStreamPath(key + ".dat");
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String readline = "";
		    StringBuffer sb = new StringBuffer();
		    while ((readline = br.readLine()) != null) {
		        sb.append(readline);
		    }
		    br.close();
		    data = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(isr != null){
					isr.close();
				}
				if(fIn != null){
					fIn.close();
				}
			} catch (IOException e) {
				Log.e("SharedPreferencesUtil", "ReadString 执行失败" + e.getMessage());
			}
		}
		return data;
	}

}
