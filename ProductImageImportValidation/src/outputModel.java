import java.util.Date;

public class outputModel {
	private String code;
	private boolean inApex;
	private boolean inEcommerce;
	private Date ApexCreationTS;
	private Date EcommmceCreationTS;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isInApex() {
		return inApex;
	}
	public void setInApex(boolean inApex) {
		this.inApex = inApex;
	}
	public boolean isInEcommerce() {
		return inEcommerce;
	}
	public void setInEcommerce(boolean inEcommerce) {
		this.inEcommerce = inEcommerce;
	}
	public Date getApexCreationTS() {
		return ApexCreationTS;
	}
	public void setApexCreationTS(Date apexCreationTS) {
		ApexCreationTS = apexCreationTS;
	}
	public Date getEcommmceCreationTS() {
		return EcommmceCreationTS;
	}
	public void setEcommmceCreationTS(Date ecommmceCreationTS) {
		EcommmceCreationTS = ecommmceCreationTS;
	}

}
