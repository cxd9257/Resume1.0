package com.harlan.resume.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.harlan.resume.util.ConstValue;

public class BlogManager {
	private final String TAG = "BlogManager";
	
	private static BlogManager mInstance = null;

	private static final String CSDN_MAIN = "http://blog.csdn.net/Singleton1900/article/list/10000";

	private static final String CSDN_HEAD = "http://blog.csdn.net/";
	
	private static final String CSDN_NAME = "singleton1900";
	
	private BlogLoadAsyncTask blogLoadAsyncTask;
	private Context mContext;
	private NetworkCallback mNetworkCallback;
	Article article;
	Category category;
	
	int articleCount = 0;
	int categoryCount = 0;
	
	String name = "";
	
	static List<Article> articleList;
	
	static List<Category> categoryList;

	public static BlogManager getInstance() {
		if (mInstance == null) {
			mInstance = new BlogManager();
		}
		return mInstance;
	}
	
	public void setContext(Context context,NetworkCallback netWorkCallback){
		mContext = context;
		mNetworkCallback = netWorkCallback;
		if (blogLoadAsyncTask!=null){
			blogLoadAsyncTask.cancel(true);
			blogLoadAsyncTask = null;
			blogLoadAsyncTask = new BlogLoadAsyncTask();
		}else {
			blogLoadAsyncTask = new BlogLoadAsyncTask();
		}
		if (!isNetWorkAvilable()){
			mNetworkCallback.handleResult(ConstValue.UPDATE_FAILED,ConstValue.UPDATE_FAILED_DISCONNECT);
			return;
		}
		blogLoadAsyncTask.execute();
	}
	
	
	private boolean isNetWorkAvilable(){
		ConnectivityManager conManager = 
				(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conManager == null)
			return false;
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo == null)
			return false;
		if (networkInfo.isConnected())
			return true;
		return false;
	}


	private static String untranslation(String original) {
		String result = null;
		if (original != null) {
			result = original.replaceAll("&amp;", "&")
					.replaceAll("&quot;", "\"").replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">");
		}
		return result;
	}

	
	private class BlogLoadAsyncTask extends AsyncTask<Void, Integer, Integer>{
//	AsyncTask<Void, Integer, Integer> httpAsyncTask = new AsyncTask<Void, Integer, Integer>(){

		@Override
		protected Integer doInBackground(Void... params) {
			initInternet();
			Log.d(TAG, "BlogLoadAsyncTask start");
			return null;
		}
		
		protected void onPostExecute(Integer result) {
		};
		
	};
	
	public void initInternet() {
		URL url = null;
		URLConnection conn = null;
		String nextLine = null;
		StringTokenizer tokenizer = null;
		try {
			// 获得网页资源
			url = new URL(CSDN_MAIN);
			// 获得资源连接
			conn = url.openConnection();
			conn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			conn.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			// 开始读取网页信息解析出网页中的超链接
			while ((nextLine = reader.readLine()) != null) {
				tokenizer = new StringTokenizer(nextLine);
				while (tokenizer.hasMoreTokens()) {
					String urlToken = tokenizer.nextToken();
					initArticleList(urlToken);
					initCategoryList(urlToken);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initCategoryList(String urlToken) {
		if (category == null) {
			category = new Category();
		}
		if (urlToken.contains("singleton1900/article/category/")
				&& urlToken.contains("</a>")) {
			int start = urlToken.indexOf("http:");
			int middle = urlToken.indexOf("\">");
			int middle2 = urlToken.indexOf("</a>");
			int middle3 = urlToken.indexOf("<span>(");
			int end = urlToken.indexOf(")</span>");
			String strTemp = urlToken.substring(start, middle);
			category.url = strTemp;
			strTemp = urlToken.substring(middle + 2, middle2);
			category.name = strTemp;
			strTemp = urlToken.substring(middle3 + 7, end);
			System.out.println(strTemp);
			category.number = strTemp;
			categoryList.add(category);
			category = null;
		} else if (urlToken.contains("singleton1900/article/category/")) {
			int start = urlToken.indexOf("http:");
			int end = urlToken.indexOf("\">");
			String strTemp = urlToken.substring(start, end);
			strTemp = untranslation(strTemp);
			category.url = strTemp;
			name = urlToken.substring(end + 2);
			categoryCount++;
		} else if (categoryCount == 1 && urlToken.contains("</a><span>")) {
			name = name + urlToken.substring(0, urlToken.indexOf("</a><span>"));
			category.name = name;
			category.number = urlToken.substring(urlToken.indexOf("</a><span>(")+11, urlToken.indexOf(")</span>"));
			categoryList.add(category);
			category = null;
			categoryCount = 0;
		} else if (categoryCount == 1) {
			name = name + urlToken;
		}
	}

	private void initArticleList(String urlToken) {
		if (article == null) {
			article = new Article();
		}
		if (articleCount == -1) {
			if (urlToken.contains("</a>")) {
				name = untranslation(name);
				article.name = name;
				articleList.add(article);
				article = null;
				name = "";
				articleCount++;
			} else {
				name += urlToken;
			}
		}
		if (urlToken.contains("span")) {
			articleCount++;
			if (articleCount > 1)
				articleCount = 1;
		} else if (urlToken.contains("link_title")) {
			articleCount++;
			if (articleCount > 2)
				articleCount = 0;
		} else if (urlToken.contains(CSDN_NAME)) {
			if (articleCount == 2) {
				article.url = name = untranslation(getArticleUrl(urlToken));
			}
			articleCount++;
		}
		if (articleCount == 3) {
			articleCount = -1;
		}
	}
	
	public String getArticleUrl(String urlToken){
		int start = urlToken.indexOf(CSDN_NAME);
		int end = urlToken.length();
		String tempStr = urlToken.substring(start,end);
		end = tempStr.indexOf("\">");
		if(end == -1){
			end = tempStr.length();
		}
		return CSDN_HEAD + tempStr.substring(0,end);
	}
	
	class Article{
		public String name;
		public String url;
	}
	
	class Category{
		public String name;
		public String url;
		public String number;
	}

}
