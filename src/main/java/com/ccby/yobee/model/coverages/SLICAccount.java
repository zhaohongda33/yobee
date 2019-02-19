package com.ccby.yobee.model.coverages;

/**
 * 富德车险账户信息
 * 
 * @author zhaohongda
 */
public class SLICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率

	/**
	 * @param user
	 * @param pass
	 * @param domestic_vehicle_rate
	 * @param imported_vehicle_rate
	 */
	public SLICAccount(String user, String pass, String domestic_vehicle_rate, String imported_vehicle_rate) {
		super();
		this.user = user;
		this.pass = pass;
		this.domestic_vehicle_rate = domestic_vehicle_rate;
		this.imported_vehicle_rate = imported_vehicle_rate;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the domestic_vehicle_rate
	 */
	public String getDomestic_vehicle_rate() {
		return domestic_vehicle_rate;
	}

	/**
	 * @param domestic_vehicle_rate
	 *            the domestic_vehicle_rate to set
	 */
	public void setDomestic_vehicle_rate(String domestic_vehicle_rate) {
		this.domestic_vehicle_rate = domestic_vehicle_rate;
	}

	/**
	 * @return the imported_vehicle_rate
	 */
	public String getImported_vehicle_rate() {
		return imported_vehicle_rate;
	}

	/**
	 * @param imported_vehicle_rate
	 *            the imported_vehicle_rate to set
	 */
	public void setImported_vehicle_rate(String imported_vehicle_rate) {
		this.imported_vehicle_rate = imported_vehicle_rate;
	}

}
