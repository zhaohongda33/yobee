package com.ccby.yobee.model.coverages;

/**
 * 利宝车险账户信息
 * 
 * @author zhaohongda
 */
public class LBICAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String channel;

	/**
	 * @param user
	 * @param pass
	 * @param channel
	 *            所属公司
	 */
	public LBICAccount(String user, String pass, String channel) {
		super();
		this.user = user;
		this.pass = pass;
		this.channel = channel;
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

}
