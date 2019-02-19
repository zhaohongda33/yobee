package com.ccby.yobee.model.coverages;

/**
 * 安盛天平保险账户信息
 * 
 * @author zhaohongda
 */
public class AXAICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String proxy_id;// 代理ID
	private String belong_department;// 出单机构
	private String pay_way;// 付款方式
	private String short_name;// 账号简称
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率

	/**
	 * @param user
	 * @param pass
	 * @param proxy_id
	 *            代理ID
	 * @param belong_department
	 *            出单机构
	 * @param pay_way
	 *            付款方式
	 * @param short_name
	 *            账号简称
	 * @param domestic_vehicle_rate
	 *            国产车指定专修费率
	 * @param imported_vehicle_rate
	 *            进口车指定专修费率
	 */
	public AXAICAccount(String user, String pass, String proxy_id, String belong_department, String pay_way, String short_name, String domestic_vehicle_rate, String imported_vehicle_rate) {
		super();
		this.user = user;
		this.pass = pass;
		this.proxy_id = proxy_id;
		this.belong_department = belong_department;
		this.pay_way = pay_way;
		this.short_name = short_name;
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
	 * @return the proxy_id
	 */
	public String getProxy_id() {
		return proxy_id;
	}

	/**
	 * @param proxy_id
	 *            the proxy_id to set
	 */
	public void setProxy_id(String proxy_id) {
		this.proxy_id = proxy_id;
	}

	/**
	 * @return the belong_department
	 */
	public String getBelong_department() {
		return belong_department;
	}

	/**
	 * @param belong_department
	 *            the belong_department to set
	 */
	public void setBelong_department(String belong_department) {
		this.belong_department = belong_department;
	}

	/**
	 * @return the pay_way
	 */
	public String getPay_way() {
		return pay_way;
	}

	/**
	 * @param pay_way
	 *            the pay_way to set
	 */
	public void setPay_way(String pay_way) {
		this.pay_way = pay_way;
	}

	/**
	 * @return the short_name
	 */
	public String getShort_name() {
		return short_name;
	}

	/**
	 * @param short_name
	 *            the short_name to set
	 */
	public void setShort_name(String short_name) {
		this.short_name = short_name;
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
