package ps.config;

public class HybrisJob {
	private String code;
	private String name;
	private String result;
	private String starttime;

	@Override
	public String toString() {
		return "HybrisJob [code=" + code + ", name=" + name + ", result=" + result + ", starttime=" + starttime
				+ ", endtime=" + endtime + "]";
	}

	private String endtime;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

}
