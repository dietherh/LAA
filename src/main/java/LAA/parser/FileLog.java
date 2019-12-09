package LAA.parser;

import java.util.Calendar;

public class FileLog {
	
	private String url;
	private long timeStamp;
	private String id;
	private Integer region;
	private Integer amount;
	private Integer day;
	private Integer week;
	private Integer year;
	
	public FileLog () {
		
	}
	
	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Integer getWeek() {
		return week;
	}

	public void setWeek(Integer week) {
		this.week = week;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.timeStamp);
		
		setDay(cal.get(Calendar.DAY_OF_MONTH));
		setWeek(cal.WEEK_OF_YEAR);
		setYear(cal.YEAR);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getRegion() {
		return region;
	}
	public void setRegion(Integer region) {
		this.region = region;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

}
