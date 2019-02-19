package com.ccby.yobee.model.coverages;

/**
 * 永安保险账户信息
 * 
 * @author zhaohongda
 */
public class YABXAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String belong_org;// 归属机构
	private String belong_person;// 归属业务员
	private String business_src;// 业务来源
	private String channel;// 渠道子类
	private String agent_point;// 代理/经纪名称
	private String agent_type;// 代理/经纪协议
	private String agent_no;// 代理业务员
	private String operator_no;// 服务机构
	private String staff_code;// 服务机构业务员
	private String domestic_vehicle_rate;// 国产车指定专修费率
	private String imported_vehicle_rate;// 进口车指定专修费率

	/**
	 * @param user
	 * @param pass
	 * @param belong_org
	 *            归属机构
	 * @param belong_person
	 *            归属业务员
	 * @param business_src
	 *            业务来源
	 * @param channel
	 *            渠道子类
	 * @param agent_point
	 *            代理/经纪名称
	 * @param agent_type
	 *            代理/经纪协议
	 * @param agent_no
	 *            代理业务员
	 * @param operator_no
	 *            服务机构
	 * @param staff_code
	 *            服务机构业务员
	 * @param domestic_vehicle_rate
	 *            国产车指定专修费率
	 * @param imported_vehicle_rate
	 *            进口车指定专修费率
	 */
	public YABXAccount(String user, String pass, String belong_org, String belong_person, String business_src, String channel, String agent_point, String agent_type, String agent_no,
			String operator_no, String staff_code, String domestic_vehicle_rate, String imported_vehicle_rate) {
		super();
		this.user = user;
		this.pass = pass;
		this.belong_org = belong_org;
		this.belong_person = belong_person;
		this.business_src = business_src;
		this.channel = channel;
		this.agent_point = agent_point;
		this.agent_type = agent_type;
		this.agent_no = agent_no;
		this.operator_no = operator_no;
		this.staff_code = staff_code;
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
	 * @return the belong_org
	 */
	public String getBelong_org() {
		return belong_org;
	}

	/**
	 * @param belong_org
	 *            the belong_org to set
	 */
	public void setBelong_org(String belong_org) {
		this.belong_org = belong_org;
	}

	/**
	 * @return the belong_person
	 */
	public String getBelong_person() {
		return belong_person;
	}

	/**
	 * @param belong_person
	 *            the belong_person to set
	 */
	public void setBelong_person(String belong_person) {
		this.belong_person = belong_person;
	}

	/**
	 * @return the business_src
	 */
	public String getBusiness_src() {
		return business_src;
	}

	/**
	 * @param business_src
	 *            the business_src to set
	 */
	public void setBusiness_src(String business_src) {
		this.business_src = business_src;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the agent_point
	 */
	public String getAgent_point() {
		return agent_point;
	}

	/**
	 * @param agent_point
	 *            the agent_point to set
	 */
	public void setAgent_point(String agent_point) {
		this.agent_point = agent_point;
	}

	/**
	 * @return the agent_type
	 */
	public String getAgent_type() {
		return agent_type;
	}

	/**
	 * @param agent_type
	 *            the agent_type to set
	 */
	public void setAgent_type(String agent_type) {
		this.agent_type = agent_type;
	}

	/**
	 * @return the agent_no
	 */
	public String getAgent_no() {
		return agent_no;
	}

	/**
	 * @param agent_no
	 *            the agent_no to set
	 */
	public void setAgent_no(String agent_no) {
		this.agent_no = agent_no;
	}

	/**
	 * @return the operator_no
	 */
	public String getOperator_no() {
		return operator_no;
	}

	/**
	 * @param operator_no
	 *            the operator_no to set
	 */
	public void setOperator_no(String operator_no) {
		this.operator_no = operator_no;
	}

	/**
	 * @return the staff_code
	 */
	public String getStaff_code() {
		return staff_code;
	}

	/**
	 * @param staff_code
	 *            the staff_code to set
	 */
	public void setStaff_code(String staff_code) {
		this.staff_code = staff_code;
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
