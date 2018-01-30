package boot67.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author xumin 2015-6-17 上午11:57:20
 *
 */
public class NetUtil {

    public static void main(String[] args) {
//        requestThenReturn("http://10.139.100.2/rosefinch/menu-1?code=yhcui");
//    	String str = sendGet("http://10.139.100.2/rosefinch/menu-1","code=yhcui");
    	String str = sendPost("http://10.139.100.2/rosefinch/menu-1","code=yhcui");
    	System.err.println(str);
//    	BillingsOperImpl ob = new BillingsOperImpl();
//		BillingsOperBean bean =new BillingsOperBean();
//		bean.setAccount("3");
//		bean.setGoodLuckCoinNum(12);
//		bean.setScoreNum(45);
//		Map<String, String> map = ob.raise(bean);
        String strs = "{\"tradedate\":\"2017-05-18\",\"keytime\":\"May 18, 2017 4:09:00 PM\",\"datatime\":\"May 18, 2017 4:09:59 PM\",\"symbol\":\"00700.HK\",\"open\":264.0,\"high\":264.0,\"low\":264.0,\"close\":264.0,\"volume\":1400400,\"turnover\":1.436683415E10,\"totalVolume\":5.4039295E7}";
//		System.err.println(map.toString());
    }
    /**
     * 根据URL 获取返回的输入流
     * xumin  2015-6-17 上午11:55:49
     * @param path
     * @return
     */
	public static InputStream getInputStreamByURL(String path) {
		 try {
	            URL url = new URL(path);
	            URLConnection URLconnection = url.openConnection();// 连接
	            // http强制转化
	            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
	            int responseCode = httpConnection.getResponseCode();

	            if (responseCode == HttpURLConnection.HTTP_OK) {
	                // System.err.println("成功");
	                InputStream urlStream = httpConnection.getInputStream();
	                return urlStream;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return null;
	}
    /**
     * 请求URL，并返回服务器响应的消息
     * 例如path = http://127.0.0.1:8080/rosefinch/menu-1?code=yhcui
     * @param path
     * @return
     */
    public static String requestThenReturn(String path) {
        // String urlStr =
        // "http://127.0.0.1:8080/intequery/index.jsp?name=yhcui";
        String sTotalString = "";
        try {
            URL url = new URL(path);
            URLConnection URLconnection = url.openConnection();// 连接
            // http强制转化
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // System.err.println("成功");
                InputStream urlStream = httpConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlStream));
                String sCurrentLine = "";

                while ((sCurrentLine = bufferedReader.readLine()) != null) {
                    sTotalString += sCurrentLine;
                }
//                System.err.println(sTotalString);
                // 假设该url页面输出为"OK"
                if (sTotalString.equals("OK")) {
                } else {
                }
            } else {
                System.err.println("失败URL Code:"+responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sTotalString;
    }

    /**
     * @param url
     * @param param
     * @param typeProxy
     * @return
     */
    public static String sendGet(String url, String param, Proxy typeProxy) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection;
            if(typeProxy==null){
            	connection = realUrl.openConnection();
            }else{
            	connection = realUrl.openConnection(typeProxy);
            }
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
            		new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
    /**
     * 获取当前时间yy-mm-dd hh:mm:ss
     * @return
     */
    public static String getNowLongDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
        String str = df.format(new Date());
        // System.out.println(str);// new Date()为获取当前系统时间
        return str;
    }


}
