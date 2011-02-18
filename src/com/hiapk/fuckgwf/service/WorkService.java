package com.hiapk.fuckgwf.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.text.TextUtils;

import com.cooler.schema.service.IService;
import com.cooler.schema.task.mark.ATaskMark;
import com.hiapk.fuckgwf.R;
import com.hiapk.fuckgwf.taskmark.UpdateHostTaskMark;
import com.hiapk.fuckgwf.utils.ContentUtil;
import com.hiapk.fuckgwf.utils.HelperUtils;

public class WorkService implements IService {
	private Context mContext;

	public WorkService() {

	}

	/**
	 * 更新 Host 文件
	 * 
	 * @param taskMark
	 */
	public String updateHost(ATaskMark taskMark) {
		if (taskMark instanceof UpdateHostTaskMark) {
			boolean mShowNotification = false;

			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(ContentUtil.HOST_URL);
			BufferedReader httpBr = null;
			HttpResponse httpResponse;

			try {
				httpResponse = client.execute(get);
				InputStream httpIs = httpResponse.getEntity().getContent();
				httpBr = new BufferedReader(new InputStreamReader(httpIs));

				// 无内容可更新
				if (httpResponse.getEntity().getContentLength() == 1
						&& httpBr.read() == '0') {
					return mContext.getString(R.string.notification_no_update);
				}

				HashMap<String, String> hpNewHosts = new HashMap<String, String>();
				String host = null;

				while ((host = httpBr.readLine()) != null) {
					if (TextUtils.isEmpty(host) || host.trim().startsWith("#"))
						continue;
					String[] pair = host.split("\\s+");
					hpNewHosts.put(pair[1], pair[0]);
				}

				Set<String> keySet = hpNewHosts.keySet();
				// 无内容
				if (keySet.size() <= 0)
					return mContext.getString(R.string.notification_no_update);

				String lastUpdate = "";
				for (String key : keySet) {
					lastUpdate += key + " ";
				}

				// 挂载系统读写失败
				if (HelperUtils.rootCMD(mContext
						.getString(R.string.cmd_remount_system)) != 0) {

					return mContext
							.getString(R.string.notification_update_failed);
				}

				// 创建临时文件失败
				String tmpFileName = ContentUtil.HostsPath + ".tmp";
				if (HelperUtils.rootCMD("echo '' > " + tmpFileName) != 0) {
					return mContext
							.getString(R.string.notification_update_failed);
				}
				File mHostFile = new File(ContentUtil.HostsPath);
				File tmpFile = new File(tmpFileName);
				if (!tmpFile.exists()) {
					return mContext
							.getString(R.string.notification_update_failed);
				}

				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(mHostFile));
				FileOutputStream fos = new FileOutputStream(tmpFile);
				byte[] buff = new byte[(int) mHostFile.length()];
				bis.read(buff);
				String[] origFileCont = new String(buff).split("\n");
				for (String line : origFileCont) {
					if (TextUtils.isEmpty(line) || line.trim().startsWith("#"))
						continue;
					String[] aPair = line.split("\\s+");
					if (hpNewHosts.containsKey(aPair[1])) {
						if (!hpNewHosts.get(aPair[1]).equals(aPair[0])) {
							fos.write((hpNewHosts.get(aPair[1]) + "\t\t"
									+ aPair[1] + "\n").getBytes());
							mShowNotification = true;
						} else {
							fos.write((line + "\n").getBytes());
						}
						hpNewHosts.remove(aPair[1]);
					} else {
						fos.write((line + "\n").getBytes());
					}
				}

				for (String key : hpNewHosts.keySet()) {
					fos.write((hpNewHosts.get(key) + "\t\t" + key + "\n")
							.getBytes());
					mShowNotification = true;
				}

				fos.flush();
				fos.close();
				bis.close();

				// 重命名失败
				if (HelperUtils.rootCMD("mv " + tmpFileName + " "
						+ ContentUtil.HostsPath) != 0) {
					return mContext
							.getString(R.string.notification_update_failed);
				}

				if (mShowNotification) {
					return mContext
							.getString(R.string.notification_update_successfully);
				}

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return mContext.getString(R.string.notification_update_failed);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return mContext.getString(R.string.notification_update_failed);
			} catch (Exception e) {
				e.printStackTrace();
				return mContext.getString(R.string.notification_update_failed);
			}
		}
		return null;
	}
}
