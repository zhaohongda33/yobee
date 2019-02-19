package com.ccby.yobee.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.json.JSONObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ccby.api.yobee.service.RestYobeeService;
import com.ccby.core.applicationcontext.SpringContextUtil;
import com.ccby.core.base.util.BusinessException;
import com.ccby.core.base.util.HttpClientHandlerCallback;
import com.ccby.yobee.model.coverages.AXAICAccount;
import com.ccby.yobee.model.coverages.Account;
import com.ccby.yobee.model.coverages.CICPAccount;
import com.ccby.yobee.model.coverages.CPICNAccount;
import com.ccby.yobee.model.coverages.GPICAccount;
import com.ccby.yobee.model.coverages.LBICAccount;
import com.ccby.yobee.model.coverages.PICCAccount;
import com.ccby.yobee.model.coverages.PINGANAccount;
import com.ccby.yobee.model.coverages.SLICAccount;
import com.ccby.yobee.model.coverages.TAIPINGAccount;
import com.ccby.yobee.model.coverages.YABXAccount;
import com.ccby.yobee.model.coverages.YGICAccount;
import com.ccby.yobee.model.coverages.YZICAccount;
import com.ccby.yobee.model.coverages.ZMBXAccount;

/**
 * @author zhaohongda
 *
 */
public class YobeeUtils {

	private static String host = "";
	private static String url = "";

	static {
		new ClassPathXmlApplicationContext("classpath*:spring/spring-context.xml");

		Properties pro = SpringContextUtil.getBean("propertiesConfig", Properties.class);
		host = pro.getProperty("yobee.host");
		url = pro.getProperty("yobee.path");
	}

	/**
	 * 获取保险公司列表
	 * 
	 * @return
	 */
	public static final JSONObject getPolicyCompanyList() {
		return HttpUtils.httpGetToJson(host, url + "/accounts", null);
	}

	/**
	 * 获取元数据
	 * 
	 * @return
	 */
	public static final JSONObject getMeta() {
		return HttpUtils.httpGetToJson(host, url + "/meta/130100", null);
	}

	/**
	 * 获取元数据
	 * 
	 * @return
	 */
	public static final JSONObject putCompanyAccount(Map<String, Object> params) {

		try {
			String res = HttpUtils.httpPost(url + "/accounts", params);
			return new JSONObject(res);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取元数据
	 * 
	 * @return
	 */
	public static final JSONObject deleteCompanyAccount(String accountId) {

		try {
			String res = HttpUtils.httpDelete(url + "/accounts/" + accountId, null, new HttpClientHandlerCallback() {

				/**
				 * @see com.ccby.core.base.util.HttpClientHandlerCallback#execute(org.apache.commons.httpclient.HttpClient,
				 *      org.apache.commons.httpclient.HttpMethod)
				 */
				@Override
				public Object execute(HttpClient client, HttpMethod method) throws Exception {

					String urlStr = url + "/accounts/" + accountId;
					method.setRequestHeader("Host", host);
					method.setRequestHeader("User-Agent", "curl/7.49.1");
					String accept = "application/vnd.botpy.v1+json";
					method.setRequestHeader("Accept", accept);
					method.setRequestHeader("Content-Type", "application/json");
					method.setRequestHeader("Authorization", "appid " + HttpUtils.APP_ID);
					method.setRequestHeader("X-Yobee-Timestamp", System.currentTimeMillis() / 1000 + "");
					String StringToSign = (System.currentTimeMillis() / 1000) + HttpUtils.APP_KEY + accept + urlStr + HttpUtils.APP_ID;
					method.setRequestHeader("X-Yobee-Signature", HttpUtils.sha256_HMAC(StringToSign, HttpUtils.APP_KEY));
					return null;
				}
			});

			return new JSONObject(res);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 修改账户信息
	 * 
	 * @return
	 */
	public static final JSONObject updateCompanyAccount(String accountId, Map<String, Object> data) {
		
		try {
			String res = HttpUtils.httpPatch(url + "/accounts/" + accountId, data);
			
			return new JSONObject(res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * 生成保险公司账户信息
	 * 
	 * @param icCode
	 * @param provCode
	 * @param cityCode
	 * @param isDefault
	 * @param username
	 * @param password
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final Map<String, Object> generateAccount(String icCode, String provCode, String cityCode, boolean isDefault, Account info) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ic_code", icCode);// 保险公司代码
		params.put("prov_code", provCode);// 省代码
		params.put("city_code", cityCode);// 城市代码
		params.put("is_default", isDefault);// 是否默认帐号

		try {
			Map<String, Object> account = (Map<String, Object>) net.sf.json.JSONObject.fromObject(new JSONObject(info).toString());
			params.put("account", account);// 帐号信息

			List<Map> contacts = new ArrayList<Map>();
			Map<String, Object> zhaohongda = new HashMap<String, Object>();
			zhaohongda.put("name", "赵宏达");
			zhaohongda.put("email", "290734310@qq.com");
			zhaohongda.put("phone", "15383917935");
			contacts.add(zhaohongda);

			params.put("contacts", contacts);
			return params;
		} catch (Exception e) {
			e.printStackTrace();
		}	

		return null;
	}

	public static void main(String[] args) {
//		 System.out.println("【获取元数据】" + getMeta());
		// System.out.println("【删除帐号】" +
//		 deleteCompanyAccount("57llncmnzpv04yeg");

//		Map<String, Object> pingan = generateAccount("pingan", "130000", "130100", true, "HBQL-00002", "Mu1G72iP");
//		Map<String, Object> czPICC = generateAccount("epicc", "130000", "130900", true, new PICCAccount("1213044799", "rb1213044799", "13099939", "1213044799", "10062824", "2", "130021100193", "10062824", "2130992", "13099939", "91130900806603142A"));// 人保沧州
//		Map<String, Object> sjzPICC = generateAccount("epicc", "130000", "130100", true, new PICCAccount("1213012308", "sanying201901", "13015205", "1213042093", "10071677", "2", "130021100401", "10071677", "2130104", "13015205", "1213012312"));// 人保石家庄
//		Map<String, Object> tsPICC = generateAccount("epicc", "130000", "130200", true, new PICCAccount("1213015843", "password4", "13129606", "1113016025", "81323181", "2", "130021100181", "13029754", "2130225", "13129600", ""));// 人保唐山
//		Map<String, Object> sjzGPIC = generateAccount("gpic", "130000", "130100", true, new Account("130503198901081824", "baoyide/123", null, null, "1399919001", "91130100560493640W"));// 国寿财石家庄
//		Map<String, Object> sjzCICP = generateAccount("cicp", "130000", "130100", true, new Account("ex_daywxk001", "654321Aa", null, null, null, null, "13000528", "13121402", "A0106", "2017O00054",
//				"1312D4000020"));// 中华联合石家庄（保易得）
//		Map<String, Object> sjzCPICN = generateAccount("cpicn", "130000", "130100", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)石家庄
//		Map<String, Object> czCPICN = generateAccount("cpicn", "130000", "130900", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)沧州
//		Map<String, Object> tsCPICN = generateAccount("cpicn", "130000", "130200", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)唐山
//		Map<String, Object> qhdCPICN = generateAccount("cpicn", "130000", "130300", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)秦皇岛
//		Map<String, Object> hdCPICN = generateAccount("cpicn", "130000", "130400", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)邯郸
//		Map<String, Object> xtCPICN = generateAccount("cpicn", "130000", "130500", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)邢台
//		Map<String, Object> bdCPICN = generateAccount("cpicn", "130000", "130600", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)保定
//		Map<String, Object> zjkCPICN = generateAccount("cpicn", "130000", "130700", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)张家口
//		Map<String, Object> cdCPICN = generateAccount("cpicn", "130000", "130800", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)承德
//		Map<String, Object> lfCPICN = generateAccount("cpicn", "130000", "131000", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)廊坊
//		Map<String, Object> hsCPICN = generateAccount("cpicn", "130000", "131100", true, new CPICNAccount("w_02045", "Cpic123456", "4S556259", "K16648"));// 太平洋保险(新)衡水
//		Map<String, Object> zjkTSBX = generateAccount("tsbx", "130000", "130700", true, new Account("710773", "JIn#710773", null, null, null, null));// 泰山保险，张家口
//		Map<String, Object> czPINGAN = generateAccount("pingan", "130000", "130900", true, new Account("HBQL-00002", "Mu1G72iP", null, null, null, null));// 平安沧州
//		Map<String, Object> czCICP = generateAccount("cicp", "130000", "130900", true, new Account("ex_daywxk001", "654321Aa", null, null, null, null, "13000528", "13121402", "A0106", "2017O00054",
//				"1312D4000020"));// 中华联合石家庄（保易得）沧州
		
		
		// 沧州阳光保险
//		Map<String, Object> czYGIC = generateAccount("ygic", "130000", "130900", true, new YGICAccount("15551377", "Aa000000.", "15550101", "15550101", "15550302", "A15550111", "2", "U15502000206", "0.00", "0.00"));
		 
		 // 人保张家口
//		Map<String, Object> zjkPICC = generateAccount("epicc", "130000", "130700", true, new PICCAccount("1213012308", "sanying201901", "13015205", "1213042093", "10071677", "2", "130021100401", "10071677", "2130104", "13015205", "1213012312"));
		
		// 人保衡水
//		Map<String, Object> hsPICC = generateAccount("epicc", "130000", "131100", true, new PICCAccount("1213012308", "sanying201901", "13015205", "1213042093", "10071677", "2", "130021100401", "10071677", "2130104", "13015205", "1213012312"));
		 
		// 平安石家庄
//		Map<String, Object> sjzPINGAN = generateAccount("pingan", "130000", "130100", true, new YZICAccount("SHRXSL-00001", "GsZ7x16y"));
		
		// 沧州平安
//		Map<String, Object> czPINGAN = generateAccount("pingan", "130000", "130900", true, new PINGANAccount("CZGHTY-00002", "G9smY69a", ""));
		
		// 中华联合石家庄
//		Map<String, Object> sjzCICP = generateAccount("cicp", "130000", "130100", true, new CICPAccount("ex_gengzhiguang001", "Blh123456", "13010117", "13010402", "D0101", "2017O00054", "1301D6000003", "13010413", "0.00", "0.00"));
		
		// 中华联合沧州
//		Map<String, Object> czCICP = generateAccount("cicp", "130000", "130900", true, new CICPAccount("ex_gengzhiguang001", "Blh123456", "13010117", "13010402", "D0101", "2017O00054", "1301D6000003", "13010413", "0.00", "0.00"));

		// 富德石家庄
//		Map<String, Object> sjzSLIC = generateAccount("slic", "130000", "130100", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
		
		// 富德张家口
//		Map<String, Object> zjkSLIC = generateAccount("slic", "130000", "130700", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
		
		// 富德邯郸
//		Map<String, Object> hdSLIC = generateAccount("slic", "130000", "130400", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
//		
//		// 富德承德
//		Map<String, Object> cdSLIC = generateAccount("slic", "130000", "130800", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
//		
//		// 富德邢台
//		Map<String, Object> xtSLIC = generateAccount("slic", "130000", "130500", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
//		
//		// 富德廊坊
//		Map<String, Object> lfSLIC = generateAccount("slic", "130000", "131000", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
//		
//		// 富德衡水
//		Map<String, Object> hcSLIC = generateAccount("slic", "130000", "131100", true, new SLICAccount("sjzql@chinacriagent.com", "Sinolife2008", "10", "15"));
//		
//		// 富德唐山
		Map<String, Object> tsSLIC = generateAccount("slic", "130000", "130200", true, new SLICAccount("tsglxj@chinacriagent.com", "Sinolife2008", "0", "0"));
		
//		// 富德沧州
//		Map<String, Object> czSLIC = generateAccount("slic", "130000", "130900", true, new SLICAccount("czkdsh@chinacriagent.com", "Sinolife2008", "0", "0"));
		
//		// 天安石家庄（保易得）
//		Map<String, Object> sjzTAIC = generateAccount("taic", "130000", "130100", true, new TAICAccount("0182002499", "ta6666", "0182313004", "018231B001380001"));
//		
		// 太平保险邯郸
//		Map<String, Object> hdTAIPING = generateAccount("taiping", "130000", "130400", true, new TAIPINGAccount("8205640", "Abc123456", "038206000000200026", "0382060000002000260002", "8200002813", "0.00", "0.00"));
		
		// 中煤保险沧州
//		Map<String, Object> czZMBX = generateAccount("zmbx", "130000", "130900", true, new ZMBXAccount("13000201", "9999", null, "13412700", "中煤保险(13000201)", "1341N190115", "13A00158", "130100", "130127"));
		
		// 太平保险承德
//		Map<String, Object> cdTAIPING = generateAccount("taiping", "130000", "130800", true, new TAIPINGAccount("8203759", "Bb123456", "038201000000200064", "0382010000002000640006", "8200002762", "", ""));

		// 太平保险张家口
//		Map<String, Object> zjkTAIPING = generateAccount("taiping", "130000", "130700", true, new TAIPINGAccount("8203759", "Bb123456", "038201000000200064", "0382010000002000640006", "8200002762", "", ""));

		// 石家庄太平保险
//		Map<String, Object> sjzTAIPING = generateAccount("taiping", "130000", "130100", true, new TAIPINGAccount("8203759", "Bb123456", "038201000000200064", "0382010000002000640006", "8200002762", "", ""));
		
		// 太平保险沧州
//		Map<String, Object> czTAIPING = generateAccount("taiping", "130000", "130900", true, new TAIPINGAccount("8203759", "Bb123456", "038201000000200064", "0382010000002000640006", "8200002762", "", ""));
		
		// 太平保险衡水
//		Map<String, Object> hsTAIPING = generateAccount("taiping", "130000", "131100", true, new TAIPINGAccount("8203759", "Bb123456", "038201000000200064", "0382010000002000640006", "8200002762", "", ""));

		// 利宝保险邯郸
//		Map<String, Object> hdLBIC = generateAccount("lbic", "130000", "130400", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
//		
//		// 利宝保险沧州
//		Map<String, Object> czLBIC = generateAccount("lbic", "130000", "130900", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
//		
//		// 安盛天平保险沧州
//		Map<String, Object> czAXATP = generateAccount("axatp", "130000", "130900", true, new AXAICAccount("hd_meilian201_dl", "666666", "", "1340", "20", "安盛天平车险(hd_meilian201_dl)", "0", "0"));
//		
//		// 利宝保险邢台
//		Map<String, Object> xtLBIC = generateAccount("lbic", "130000", "130500", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
//		
//		// 利宝保险保定
//		Map<String, Object> bdLBIC = generateAccount("lbic", "130000", "130600", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
//		
//		// 利宝保险衡水
//		Map<String, Object> hsLBIC = generateAccount("lbic", "130000", "131100", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
//		
//		// 利宝保险张家口
//		Map<String, Object> zjkLBIC = generateAccount("lbic", "130000", "130700", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
		
		// 中华联合承德
//		Map<String, Object> cdCICP = generateAccount("cicp", "130000", "130800", true, new CICPAccount("yanyan006", "123456Aa", "13080184", "130802", "A0102", "2016A24772", "1308G1200043", "13080201", "0.00", "0.00", "", ""));
		
//		// 燕赵财险石家庄（保易得）
//		Map<String, Object> sjzYZIC = generateAccount("yzic", "130000", "130100", true, new YZICAccount("1308009044", "!qaz2wsx"));
		
		// 石家庄阳光保险
//		Map<String, Object> sjzYGIC = generateAccount("ygic", "130000", "130100", true, new YGICAccount("15501539", "Hh999999*", "15501700", "15501700", "15500412", "A15500082", "2", "U15502000011", "0.00", "0.00"));
		
		// 张家口阳光保险
//		Map<String, Object> zjkYGIC = generateAccount("ygic", "130000", "130700", true, new YGICAccount("15500141", "Aa123456!", "15501790", "15501700", "15501224", "A15500064", "A", "U1507A000146", "0.00", "0.00"));
		
		// 石家庄利宝保险
//		Map<String, Object> sjzLBIC = generateAccount("lbic", "130000", "130100", true, new LBICAccount("U13012000057", "Asdcvb123.", "HBS000437"));
		
		// 燕赵财险承德（保易得）
//		Map<String, Object> cdYZIC = generateAccount("yzic", "130000", "130800", true, new YZICAccount("1308009044", "!qaz2wsx"));
		
		// 国寿财石家庄（保易得）
//		Map<String, Object> sjzGPIC = generateAccount("gpic", "130000", "130100", true, new GPICAccount("130503198901081824", "081824", "1399919001", "91130100560493640w", "09", "1399919001", "341227198710125702", "2", "911301056690604354"));
		
		// 国寿财张家口（保易得）
//		Map<String, Object> zjkGPIC = generateAccount("gpic", "130000", "130700", true, new GPICAccount("130503198901081824", "081824", "1399919001", "91130100560493640w", "09", "1399919001", "341227198710125702", "2", "911301056690604354"));
		
		// 国寿财衡水（保易得）
//		Map<String, Object> hsGPIC = generateAccount("gpic", "130000", "131100", true, new GPICAccount("130503198901081824", "081824", "1399919001", "91130100560493640w", "09", "1399919001", "341227198710125702", "2", "911301056690604354"));
		
		// 中华联合衡水（保易得）
//		Map<String, Object> hsCICP = generateAccount("cicp", "130000", "131100", true, new CICPAccount("ex_tianmeiling001", "150022", "13070007", "13073721", "D0101", "2017O00054", "1307D4000015", "13073721", "0.00", "0.00", "衡水市桃城区人民法院", "007001"));

		// 中华联合张家口
//		Map<String, Object> zjkCICP = generateAccount("cicp", "130000", "130700", true, new CICPAccount("ex_gaoge001", "181841", "13111168", "13111335", "B0103", "2017O00054", "1311D4517001", "13111335", "0.00", "0.00", "张家口仲裁委员会", "007002"));


//		 永安保险石家庄
//		Map<String, Object> sjzYABX = generateAccount("yaicn", "130000", "130100", true, new YABXAccount("10018307", "110528", "13010141", "10073323", "1900201", "1900201001", "02130101020005", "02130101020005201601", "10018754", "13010141", "10073323", "0.00", "0.00"));
//		
//		putCompanyAccount(zjkSLIC);
//		putCompanyAccount(hdSLIC);
//		putCompanyAccount(cdSLIC);
//		putCompanyAccount(xtSLIC);
//		putCompanyAccount(lfSLIC);
//		putCompanyAccount(hcSLIC);
//		putCompanyAccount(tsSLIC);
		
//		deleteCompanyAccount("51vvvc3ylz0dxl65");
//		deleteCompanyAccount("gm001t0n1kpydvw2");
//		
//		putCompanyAccount(sjzCPICN);
//		putCompanyAccount(czCPICN);
//		putCompanyAccount(tsCPICN);
//		putCompanyAccount(qhdCPICN);
//		putCompanyAccount(hdCPICN);
//		putCompanyAccount(xtCPICN);
//		putCompanyAccount(bdCPICN);
//		putCompanyAccount(zjkCPICN);
//		putCompanyAccount(cdCPICN);
//		putCompanyAccount(lfCPICN);
//		putCompanyAccount(tsPICC);
		
//		deleteCompanyAccount("");
//		deleteCompanyAccount("");
//		deleteCompanyAccount("");
//		deleteCompanyAccount("");
//		deleteCompanyAccount("");
//		deleteCompanyAccount("");
//		updateCompanyAccount("2pkklaex3kkqvy9g", sjzPICC);// 石家庄人保三营
//		updateCompanyAccount("2nnnzfj16rey1yl2", zjkPICC);// 张家口人保三营
//		updateCompanyAccount("2qmovte0mkmo1645", hsPICC);// 人保衡水
//		updateCompanyAccount("gkplvb7qnr6n41w2", tsPICC);// 人保唐山
//		updateCompanyAccount("580ewipojozq06x2", sjzYABX);// 石家庄永安
//		updateCompanyAccount("2pkd4bexw3o33eyg", sjzPINGAN);// 石家庄平安
//		updateCompanyAccount("5xw3zf69vm0md1pg", czPINGAN);// 沧州平安
//		updateCompanyAccount("gvd9ximk1lj9lv1g", czPICC);// 沧州人保
//		updateCompanyAccount("gv0dntmq7krnl8y2", zjkCICP);// 张家口中华联合
//		updateCompanyAccount("gkj3ltl4dnqqdq62", zjkTAIPING);// 张家口太平
//		updateCompanyAccount("g444vckqndp1o6wg", hdTAIPING);// 邯郸太平
//		updateCompanyAccount("gjrzls187omp87w2", sjzLBIC);// 石家庄利宝
//		updateCompanyAccount("genr3h4wmxlzw1ng", hdLBIC);// 邯郸利宝
//		updateCompanyAccount("5ywjpilpyklzv7k2", sjzSLIC);// 石家庄富德
//		updateCompanyAccount("5op64awv4rrpmv02", tsSLIC);// 唐山富德
//		updateCompanyAccount("gvmmyaoxe1jq84xg", czZMBX);// 沧州中煤保险
//		updateCompanyAccount("2dnkqikry9zy3nxg", czYGIC);// 沧州阳光保险
//		updateCompanyAccount("517znh3o8xl7vqd5", sjzYGIC);// 石家庄阳光保险
//		updateCompanyAccount("51vqdi3j843qo9x5", hsGPIC);
//		updateCompanyAccount("gv6m1um0qoz37nx2", zjkGPIC);
//		 System.out.println("【新增保险公司帐号】" + putCompanyAccount(zjkCICP));
//		 System.out.println("【新增保险公司帐号】" + putCompanyAccount(sjzTAIC));
//		 System.out.println("【新增保险公司帐号】" + putCompanyAccount(sjzTAIPING));
//		 System.out.println("【新增保险公司帐号】" + putCompanyAccount(sjzYZIC));
		// System.out.println("【获取帐号】" + getPolicyCompanyList());
		RestYobeeService service = SpringContextUtil.getBean("restYobeeService", RestYobeeService.class);
		try {
			service.updatePolicyAccount();
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
}
