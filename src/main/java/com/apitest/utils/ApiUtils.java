import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ezijing.qa.common.TestBase;
import com.ezijing.qa.learncentre.LearnPlanScenario;
import com.ezijing.qa.vo.ResponseVo;
import com.ezijing.qa.vo.RequestVo;



/**
 * @author cuixiaohui
 *
 */

public class ApiUtils {

	/**
	 * get request
	 * 
	 * @param RequestVo  	通过hashmap赋值获得参数键值对，以及请求头信息
	 * @param headers	    http请求头信息
	 * @return InterfaceReturnVo 接口调用返回结果数据对象，包括cookie信息及json串
	 * @throws Exception
	 */

	public static ResponseVo httpGet(RequestVo requestvo,HashMap<String,String> headers)
			throws Exception {

		//接口返回对象
		ResponseVo interfacereturnvo = new ResponseVo();
		//response cookie 列表
		List<Cookie> responsecookie = null;
		// response json串
		JSONObject json = new JSONObject();
		
		HashMap<String,String> params = new HashMap<String,String>();
		
		String rUrl = requestvo.getRequesturl()+"?";
		
		HttpClient client = new DefaultHttpClient();
		
		CookieStore cookieStore = new BasicCookieStore();
		
		HttpContext localContext = new BasicHttpContext();
		
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		//设置参数列表
		
		if(requestvo != null) {

			if(!requestvo.getRequestparams().isEmpty())
			{
				params = requestvo.getRequestparams();
				rUrl += prepareParam(params);
				requestvo.setRequesturl(rUrl);
			}else
			{
				rUrl = rUrl.substring(0,rUrl.length()-1);
			}
		}

	
		System.out.println("request url is : "+rUrl);

		System.out.println("Resuest method : GET");
		
		//执行http请求
		try {

			HttpGet httpget = new HttpGet(requestvo.getRequesturl());

			//设置请求header
			
			if(!headers.isEmpty()){
				
				Set entrySet = headers.entrySet();
				
				for(Iterator itor = entrySet.iterator();itor.hasNext();)
				{
					Map.Entry entry = (Entry) itor.next();
					httpget.addHeader(entry.getKey().toString(),entry.getValue().toString());
				}
			}
			
			long startTime = System.currentTimeMillis();
			HttpResponse httpResponse = client.execute(httpget,localContext);
			long endTime = System.currentTimeMillis();
			System.out.println("Response time is :"+ (endTime-startTime) +"ms");
	
			
			responsecookie = cookieStore.getCookies();
			
			//response cookies
			interfacereturnvo.setCookies(responsecookie);
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();

			if (responseCode == 200) {
				
				HttpEntity entity = httpResponse.getEntity();
				
				if (entity != null) {
					
					json = JSON.parseObject(EntityUtils.toString(entity));

					System.out.println("Response is :"+json);
					
					interfacereturnvo.setJson(json);
				} else {
					System.out.println("http response is null");
				}
			} else {
				
				System.out.println("http GET request error, error code is "+ responseCode);
				json.put("status", responseCode);
				interfacereturnvo.setJson(json);
				return interfacereturnvo;
			}

			httpget.abort();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return interfacereturnvo;
	}

	/**
 	* post request
	* @param requestvo
	* @param cookiename 本次接口调用需用到的其他接口cookie名
	* @return InterfaceReturnVo 接口调用返回结果数据对象，包括cookie信息及json串
	*/
	public static ResponseVo httpPost(RequestVo requestvo,HashMap<String,String> headers) {

	
	ResponseVo interfacereturnvo = new ResponseVo();

	@SuppressWarnings({ "deprecation", "resource" })
	HttpClient client = new DefaultHttpClient();
	
	CookieStore cookieStore = new BasicCookieStore();
	
	HttpContext localContext = new BasicHttpContext();
	
	localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	
	List<Cookie> responsecookie = null;
	
	JSONObject json = new JSONObject();

	try {
		HttpPost httppost = new HttpPost(requestvo.getRequesturl());
		
		if(headers!=null){
			
			Set entrySet = headers.entrySet();
			
			for(Iterator itor = entrySet.iterator();itor.hasNext();)
			{
				Map.Entry entry = (Entry) itor.next();
				httppost.addHeader(entry.getKey().toString(),entry.getValue().toString());
			}
			
		}
		if(requestvo != null) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();

			HashMap<String, String> hashmap = requestvo.getRequestparams();
			
			Set set = hashmap.entrySet();
			
			Iterator iterator = set.iterator();
			
			while(iterator.hasNext())
			{
				Map.Entry mapentry = (Map.Entry) iterator.next();
				parameters.add(new BasicNameValuePair(mapentry.getKey().toString(),mapentry.getValue().toString()));
			}

			System.out.println("Request url: "+requestvo.getRequesturl());
			
			System.out.println("Request par: "+parameters);
			
			System.out.println("Resuest method : POST");

			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
					parameters, "UTF-8");
			
			httppost.setEntity(formEntiry);
		}
		
		long startTime = System.currentTimeMillis();
		HttpResponse httpResponse = client.execute(httppost,localContext);
		long endTime = System.currentTimeMillis();
		System.out.println("Response time is :"+ (endTime-startTime) +"ms");
		
		responsecookie = cookieStore.getCookies();
		
		interfacereturnvo.setCookies(responsecookie);
		
		int responseCode = httpResponse.getStatusLine().getStatusCode();

		
		if (responseCode == 200 || responseCode == 201) {
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				
				//System.out.println(entity.toString());
				//System.out.println(entity.getContent().toString());

				json = JSON.parseObject(EntityUtils.toString(entity));
				
				interfacereturnvo.setJson(json);

				System.out.println("Response is :"+json);
			} else {
				System.out.println("entity is null");
			}
		} else {
			System.out.println(EntityUtils.toString(httpResponse.getEntity()));
			System.out
					.println("http POST request return error, error code is "
							+ responseCode);
		}
		httppost.abort();

	} catch (Exception e) {
		e.printStackTrace();
	}

	return interfacereturnvo;
}

	/**
 	* delete request 使用httpurlconnect
	* @param requestvo
	* @param header 需要设置的header头信息
	* @return InterfaceReturnVo 接口调用返回结果数据对象，包括cookie信息及json串
	*/
	public static ResponseVo httpdelete(RequestVo requestvo,HashMap<String,String> headers) {
		// TODO Auto-generated method stub
		
		ResponseVo interfacereturnvo = new ResponseVo();
		JSONObject json = new JSONObject();
		
		try {
			URL url = new URL(requestvo.getRequesturl());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("DELETE");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			//urlConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			//urlConnection.setRequestProperty("Cookie",cookieinfo+";");
			
			if(headers!=null){
				
				Set entrySet = headers.entrySet();
				
				for(Iterator itor = entrySet.iterator();itor.hasNext();)
				{
					Map.Entry entry = (Entry) itor.next();
					urlConnection.setRequestProperty(entry.getKey().toString(),entry.getValue().toString());
				}
				
			}
			System.out.println("Resuest url is : "+requestvo.getRequesturl());
				
			System.out.println("Resuest method : DELETE");
			
			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(in));
			StringBuffer temp = new StringBuffer();
			
			String line = bufferedreader.readLine();
			while (line!=null)
			{
				temp.append(line).append("");
				line = bufferedreader.readLine();
			}
			
			json = JSONObject.parseObject(temp.toString());
			
			System.out.println("Response is :"+json);
			
			interfacereturnvo.setJson(json);
			
			//System.out.println("json is : "+json);
			
			urlConnection.disconnect();
					
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return interfacereturnvo;
	}
	
	/**
 	* delete request 使用httpclient
	* @param requestvo
	* @param header 需要设置的header头信息
	* @return InterfaceReturnVo 接口调用返回结果数据对象，包括cookie信息及json串
	*/
	public static ResponseVo httpDeleteWithBody(RequestVo requestvo,HashMap<String,String> headers)
	{
		// 接口返回信息类
		ResponseVo interfacereturnvo = new ResponseVo();
		
		CookieStore cookieStore = new BasicCookieStore();
		
		HttpContext localContext = new BasicHttpContext();
		
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		List<Cookie> responsecookie = null;
		
		JSONObject json = new JSONObject();
		
		HttpClient httpClient = new DefaultHttpClient();
		
		try{
			
			MyHttpDelete httpdelete = new MyHttpDelete(requestvo.getRequesturl()); 

			if(headers!=null||!headers.isEmpty()){
				
				Set entrySet = headers.entrySet();
				
				for(Iterator itor = entrySet.iterator();itor.hasNext();)
				{
					Map.Entry entry = (Entry) itor.next();
					
					httpdelete.addHeader(entry.getKey().toString(),entry.getValue().toString());
				}
				
			}
			if(requestvo!=null){
		
				List<NameValuePair> parameters = new ArrayList<NameValuePair>(); 

				
				HashMap<String, String> hashmap = requestvo.getRequestparams();
				
				Set set = hashmap.entrySet();
				
				Iterator iterator = set.iterator();
				
				while(iterator.hasNext())
				{
					Map.Entry mapentry = (Map.Entry) iterator.next();
					parameters.add(new BasicNameValuePair(mapentry.getKey().toString(),mapentry.getValue().toString()));
				}
		
				System.out.println("Request url: "+requestvo.getRequesturl());
				
				System.out.println("Request par: "+parameters);
				
				System.out.println("Resuest method : DELETE");
				
				httpdelete.setEntity(new UrlEncodedFormEntity(parameters)); 

				}
			
				long startTime = System.currentTimeMillis();
				HttpResponse response = httpClient.execute(httpdelete,localContext);
				long endTime = System.currentTimeMillis();
				System.out.println("Response time is :"+ (endTime-startTime) +"ms");
				
				
				responsecookie = cookieStore.getCookies();
				
				interfacereturnvo.setCookies(responsecookie);
				
				int responseCode = response.getStatusLine().getStatusCode();

		
				if (responseCode == 200 || responseCode == 201) {
					
					HttpEntity entity = response.getEntity();

				if (entity != null) {
					
					json = JSON.parseObject(EntityUtils.toString(entity));
					
					System.out.println("Response is :"+json);
					
					interfacereturnvo.setJson(json);
	
				} else {
					System.out.println("entity is null");
				}
			} else {
				System.out.println(EntityUtils.toString(response.getEntity()));
				System.out
						.println("http POST request return error, error code is "
								+ responseCode);
			}
				
			httpdelete.abort();
			
		}catch(Exception e)
		{
				e.printStackTrace();
		}
		return interfacereturnvo;
	}
	
	/**
 	* put request 使用httpurlconnect
	* @param requestvo
	* @param header 需要设置的header头信息
	* @return InterfaceReturnVo 接口调用返回结果数据对象，包括cookie信息及json串
	*/
	public static ResponseVo httpPut(RequestVo requestvo,HashMap<String,String> headers) {
		// TODO Auto-generated method stub
		
		ResponseVo interfacereturnvo = new ResponseVo();
		JSONObject json = new JSONObject();
		
		try {
			URL url = new URL(requestvo.getRequesturl());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("PUT");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			if(headers!=null){
				
				Set entrySet = headers.entrySet();
				
				for(Iterator itor = entrySet.iterator();itor.hasNext();)
				{
					Map.Entry entry = (Entry) itor.next();
					urlConnection.setRequestProperty(entry.getKey().toString(),entry.getValue().toString());
				}
				
			}
			
			String strUpdateParam = "";
					
			if(requestvo != null) {

				HashMap<String, String> hashmap = requestvo.getRequestparams();
				
				Set set = hashmap.entrySet();
				
				Iterator iterator = set.iterator();
				
				while(iterator.hasNext())
				{
					Map.Entry mapentry = (Map.Entry) iterator.next();
					strUpdateParam += mapentry.getKey().toString()+"="+mapentry.getValue().toString()+"&";
				}

				strUpdateParam = strUpdateParam.substring(0, strUpdateParam.length()-1);
				
				System.out.println("Resuest url is : "+requestvo.getRequesturl());
				
				System.out.println("Request params is : "+strUpdateParam);
				
				System.out.println("Resuest method : PUT");
				
			}
			PrintWriter out = new PrintWriter(new OutputStreamWriter(urlConnection.getOutputStream(),"utf-8"));

			long startTime = System.currentTimeMillis();
			out.write(strUpdateParam);
			out.flush();
			out.close();
			
			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(in));
			StringBuffer temp = new StringBuffer();
			
			String line = bufferedreader.readLine();
			long endTime = System.currentTimeMillis();
			System.out.println("Response time is :"+ (endTime-startTime) +"ms");
			while (line!=null&&(endTime=System.currentTimeMillis())!=0)
			{
				temp.append(line).append("");
				line = bufferedreader.readLine();
			}
			
			json = JSONObject.parseObject(temp.toString());
			
			System.out.println("Response is :"+json);
			
			interfacereturnvo.setJson(json);
			
			//System.out.println("json is : "+json);
			
			urlConnection.disconnect();
					
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return interfacereturnvo;
	}

	/**
	 * get cookiestore value
	 * 
	 * @param cookies list
	 * @param cookiename
	 * @return cookie value of cookiename
	 */
	
	public static String getCookieValue(List<Cookie> cookies, String cookiename)
	{
		String cookievalue = "";
		for(Iterator<Cookie> i = cookies.iterator();i.hasNext();)
		{
			String cookiestr = i.next().toString();
			if(cookiestr.contains(cookiename)){
				String[] strarray = cookiestr.split("\\]\\[");
				for(int j = 0; j < strarray.length; j++)
				{
					if(strarray[j].contains("value"))
					{
						String[] tmp = strarray[j].split(":");
						cookievalue = tmp[1].trim();
					}
				}
			}

		}
		return cookievalue;
	}
		
	/**
	 * 获取参数列表 （已不用）
	 * 
	 * @param cookies list
	 * @param cookiename
	 * @return cookie value of cookiename
	 */
	
	public static List<NameValuePair> getParamList(Object object)
			throws Exception {
		Class objClass = object.getClass();
		StringBuffer sb = new StringBuffer();

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();

		while (objClass != null && !objClass.equals(Object.class)) {
			Field fields[] = objClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					if (null == field.get(object)) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				String value = "";
				value = field.get(object).toString();
				parameters.add(new BasicNameValuePair(field.getName(), value));
			}
			objClass = objClass.getSuperclass();
		}
		return parameters;
	}
	
	/**
	 * 获取对象field（暂时不用）
	 * 
	 * @param Object object
	 * @return String URI
	 * @throws Exception
	 */
	public static String get(Object object) throws Exception {
		Class objClass = object.getClass();
		StringBuffer sb = new StringBuffer();
		while (objClass != null && !objClass.equals(Object.class)) {
			Field fields[] = objClass.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				try {
					if (null == field.get(object)) {
						continue;
					}
				} catch (Exception e) {
					continue;
				}
				String value = "";
				value = field.get(object).toString();
				sb.append(field.getName() + "=" + value + "&");
			}
			objClass = objClass.getSuperclass();
		}
		String s = sb.toString();
		// System.out.println(s);
		s = s.substring(0, s.length() - 1);
		return s;
	}

	/**
	 * 设置参数URI
	 * 
	 * @param Object object
	 * @return String URI
	 * @throws Exception
	 */
	public static String prepareParam(Map<String,String> paramMap)
	{
		StringBuffer sb = new StringBuffer();
		if(paramMap.isEmpty())
		{
			return "";
		}else
		{
			for(String key: paramMap.keySet())
			{
				String value = (String)paramMap.get(key);
				if(sb.length()<1)
				{
					sb.append(key).append("=").append(value);
				}
				else{
					sb.append("&").append(key).append("=").append(value);
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * get randomSting
	 * 
	 *	    
	 * @return Random String  
	 * @throws Exception
	 */
	
	public static String randomString()
	{
		String randomstring = "";
		Random rand = new Random();
		int randnum = rand.nextInt(100);
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date nowTime = new Date();
		randomstring = format.format(nowTime).toString()+String.valueOf(randnum);		
		return randomstring;
	}

	public static boolean validateJson(JSONObject json) {
		// TODO Auto-generated method stub
		if(json.containsKey("errorCode")&&json.containsKey("errorMsg"))
		{
			return true;
		}
		return false;
	}
	
	public static String randomTelNumber()
	{
		String randomstring = "";
		SimpleDateFormat format = new SimpleDateFormat("ddHHmmss");
		Date nowTime = new Date();
		randomstring = "13"+format.format(nowTime).toString();		
		return randomstring;
	}
	public static boolean isSSL(String requesturl)
	{
		
		if(requesturl.split(":")[0].equals("https"))
		{
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public static String dataNow()
	{
		String dateNowStr = "";
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 1);
		date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateNowStr = sdf.format(date);
		return dateNowStr;
	}
}
