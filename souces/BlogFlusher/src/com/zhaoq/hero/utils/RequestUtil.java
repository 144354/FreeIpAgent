package com.zhaoq.hero.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.zhaoq.hero.Constants;
import com.zhaoq.hero.domain.IPAgentEntity;
import com.zhaoq.hero.intfs.RequestCallBack;

/**
 * ����  ����
 * author:zhaoq
 * github:https://github.com/zqHero
 * csdn:http://blog.csdn.net/u013233097
 * 2017��12��13��
 */
public class RequestUtil {
	
	private static List<String> murls;
	private static RequestCallBack mcallback;

	static Thread rThread ;
	/**
	 * to  Request Data:
	 * @param urls
	 * @param requestCallBack
	 */
	public static void requestData(List<String> urls, RequestCallBack requestCallBack) {
		// TODO Auto-generated method stub
		murls = urls;
		mcallback = requestCallBack;
		
		rThread = RequesThread.getInstance(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				reques();
			}
		});
		rThread.start();
	}
	//
	//1.��http�����ַapi�������󣬻����Ҫ�Ĵ���ip��ַ
	static List<IPAgentEntity> ipList = getIp(Constants.IPAgentUrl);
	
	private static void reques() {
		// TODO Auto-generated method stub
		if (murls == null || murls.size()==0) {
			return;
		}
		for(String url :murls){
			if (url == null || url.equals("")) {
				continue;
			}
			int count = 0;
			//Ĭ�� ÿ��  ���µ�ַ����     10000 �Σ�
			for(int i=0; i< 10000; i++){
				IPAgentEntity myIpAgentEntity = ipList.get((int) (Math.random() * ipList.size()));
				
				System.setProperty("http.maxRedirects", "50");
		        System.getProperties().setProperty("proxySet", "true");
		        System.getProperties().setProperty("http.proxyHost", myIpAgentEntity.getAddress());
		        System.getProperties().setProperty("http.proxyPort", myIpAgentEntity.getPort());
	
		        try {
					Document doc = Jsoup.connect(url)
					  				.userAgent("Mozilla")
					  				.cookie("auth", "token")
					  				.timeout(3000)
					  				.get();
					if(doc != null) {
						count++;
						if(mcallback != null)mcallback.requesCallBack(
								url + "--�ɹ�ˢ�´���: " + count);
					}
				} catch (IOException e) {
					if(mcallback != null)mcallback.requesCallBack(
							myIpAgentEntity.getAddress() + ":" + myIpAgentEntity.getPort() + "����");
				}		
			}
		}
	}


	/**
	 * ��ȡ  ip  �����ַ��
	 * @param url
	 * @return
	 */
	public static List<IPAgentEntity> getIp(String url) {
        List<IPAgentEntity> ipList = new ArrayList<IPAgentEntity>();
        try {
            //1.��ip�����ַ����get�����õ������ip
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();

            //ƥ��������ʽ��
            Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+:(\\d)*");
            Matcher matcher = pattern.matcher(doc.toString());

            ArrayList<String> ips = new ArrayList<String>();
            while (matcher.find()) {
                ips.add(matcher.group());
            }
			for( String ip : ips) {
				IPAgentEntity myIp = new IPAgentEntity();
				String[] temp = ip.split(":");
				myIp.setAddress(temp[0].trim());
				myIp.setPort(temp[1].trim());
				ipList.add(myIp);
			}
        } catch (IOException e) {
        	if(mcallback != null)mcallback.requesCallBack("���ش���:" + e.toString() + "\r\n����  ����ip��ַ����:\r\n"
        			+ "���Ʋ���https://github.com/zqHero/FreeIpAgent/blob/master/Ips.txt  ����Ƿ����");
        }
        return ipList;
    }


	@SuppressWarnings("deprecation")
	public static void exitCurrentThread() {
		// TODO Auto-generated method stub
		if (rThread!=null) {
			rThread.stop();
		}
	}
	
	//��֤   �߳�   ��ȫ��
	static class RequesThread extends Thread{
		
		private RequesThread(Runnable runnable){
			super(runnable);
		}
		
		static RequesThread thread;
		
		@SuppressWarnings("deprecation")
		public synchronized static RequesThread getInstance(Runnable runnable){
			if (thread!=null) {
				thread.stop();
			}
			thread = new RequesThread(runnable);
			return thread;
		}
		
	}

}
