package com.ccby.yobee.model.coverages;

/**
 * 平安保险账户信息
 * 
 * @author zhaohongda
 */
public class PINGANAccount implements Account {

	/** serialVersionUID */
	private static final long serialVersionUID = 2743601354042713183L;

	private String user;
	private String pass;
	private String proxy_id;// 代理ID


	/**
	 * @param user
	 * @param pass
	 * @param proxy_id
	 */
	public PINGANAccount(String user, String pass, String proxy_id) {
		super();
		this.user = user;
		this.pass = pass;
		this.proxy_id = proxy_id;
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
	 * @param proxy_id the proxy_id to set
	 */
	public void setProxy_id(String proxy_id) {
		this.proxy_id = proxy_id;
	}

}
