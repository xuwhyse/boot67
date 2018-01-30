package boot67.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

	static DateFormat formatDate=new SimpleDateFormat("yyyyMMdd");
	static DateFormat fadeLoginFormatDate=new SimpleDateFormat("yyyy/MM/dd");//"yyyy/MM/dd HH:mm"
	static DateFormat formatYear=new SimpleDateFormat("yyyy");
	public static DateFormat formatDate2 = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat formatTime=new SimpleDateFormat("HH:mm:ss");
	static DateFormat formatTime2=new SimpleDateFormat("HHmmss");

	/**
	 * @param args
	 * author:xumin 
	 * 2016-4-22 下午8:01:21
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "2017-06-01 09:00:01";
		Date now = new Date();
		Date newer = new Date(System.currentTimeMillis()+5);
		try {
			Date date = formatDate2.parse(str);
			System.err.println(date.toString());
			System.err.println(newer.after(now));
		} catch (ParseException e) {

		}

	}
	public static String getYear() {
		Date date=new Date();
		String time=formatYear.format(date);
		return time;
	}
	public static String getToday() {
		Date date=new Date();
		String time=formatDate.format(date);
		return time;
	}

	public static String getTodayTime() {
		Date date=new Date();
		String time=formatTime.format(date);
		return time;
	}
	/**
	 * 之前或者之后的日子
	 * @param days
	 * @return
	 * author:xumin 
	 * 2016-5-25 上午10:36:42
	 */
	public static String getDay(int days) {
		Date dat = null;
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DATE, days);
		dat = cd.getTime();
		String time = formatDate.format(dat);
		return time;
	}

	public static String transferAlertDate(String date) {
		String sfstr = "";
		try {
			sfstr = formatDate2.format(formatDate.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sfstr;
	}

	public static String transferAlertTime(String time) {
		String sfstr = "";
		try {
			sfstr = formatTime.format(formatTime2.parse(time));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sfstr;
	}
	/**
	 * 今天是否是周末
	 * @return
	 * author:xumin 
	 * 2016-8-3 下午2:44:43
	 */
	public static boolean isWeekDayCTP() {
		Date bdate=new Date();
		int timeNow = TimeUtils.getTodayTimeInt();
		boolean flag = (0<=timeNow && timeNow<=23000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(bdate);
		if((cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY && !flag) || cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
		   return true; 
		return false;
	}

	/**
	 * 判断时间是否是周日，周日不该记分时线
	 * @param timeStamp
	 * @return
	 */
	public static boolean isSunday(Date timeStamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timeStamp);
		if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
			return true;
		}
		return false;
	}

	public static int getTodayTimeInt() {
		Date date=new Date();
		String time=formatTime2.format(date);
		return Integer.parseInt(time);
	}
	public static String getFadeLoginTimeByLong(long fadeLoginTime) {
		Date date=new Date(fadeLoginTime);
		String time=fadeLoginFormatDate.format(date);
		return time;
	}
	public static String getDayAndTimeNow() {
		Date date=new Date();
		String day=formatDate2.format(date);
		String time=formatTime.format(date);//211451
		return day+" "+time;
	}
	public static String getAppTimeNow() {
		//20160523-211451
		Date date=new Date();
		String day=formatDate.format(date);
		String time=formatTime2.format(date);//211451
		int temp = Integer.parseInt(time)+4600;//增加这个数字应该比交易时间大了
		return day+"-"+temp;
	}


}
