package com.ccby.yobee.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.rpc.RpcContext;
import com.ccby.api.base.service.InsuranceCompanyService;
import com.ccby.api.base.service.InsuranceLastQuoteService;
import com.ccby.api.base.service.InsuranceService;
import com.ccby.api.base.service.UserCarService;
import com.ccby.api.core.dto.UserCarDto;
import com.ccby.api.yobee.service.RestYobeeService;
import com.ccby.api.yobee.service.YobeeService;
import com.ccby.core.base.dao.BaseDAO;
import com.ccby.core.base.util.BusinessException;
import com.ccby.core.base.util.Constant;
import com.ccby.core.base.util.DateUtils;
import com.ccby.core.base.util.JSONUtils;
import com.ccby.core.base.util.Response;
import com.ccby.dto.yobee.BizInfo;
import com.ccby.dto.yobee.BizInfo.BIZCoverage;
import com.ccby.dto.yobee.ForceInfo;
import com.ccby.dto.yobee.ICAccount;
import com.ccby.dto.yobee.ICDTO;
import com.ccby.dto.yobee.ICModel;
import com.ccby.dto.yobee.ICModelResult;
import com.ccby.dto.yobee.ProposeDTO;
import com.ccby.dto.yobee.Renewal;
import com.ccby.dto.yobee.VehicleDTO;
import com.ccby.dto.yobee.request.NotificationData;
import com.ccby.dto.yobee.request.QueryCar;
import com.ccby.dto.yobee.request.QueryVehicleType;
import com.ccby.module.insurance.InsuranceCompany;
import com.ccby.module.insurance.InsuranceCompanyImpl;
import com.ccby.module.insurance.InsuranceLastQuote;
import com.ccby.yobee.model.requireaccountfields.RequireAccountFields;
import com.ccby.yobee.model.requireaccountfields.RequireAccountFieldsImpl;
import com.ccby.yobee.model.saleschannel.SalesChannel;
import com.ccby.yobee.model.saleschannel.SalesChannelImpl;
import com.ccby.yobee.utils.HttpUtils;

/**
 * Rest方式金斗云接口
 * 
 * @author zhaohongda
 * 
 * @see RestYobeeService
 */
public class RestYobeeServiceImpl implements RestYobeeService {

	private Logger LOGER = Logger.getLogger(RestYobeeServiceImpl.class);

	@Resource(name = "defaultDAO")
	private BaseDAO baseDAO;

	@Resource(name = "insuranceServiceApi")
	private InsuranceService insuranceService;

	@Resource(name = "userCarService")
	private UserCarService userCarService;

	@Resource(name = "yobeeService")
	private YobeeService yobeeService;

	@Resource(name = "insuranceLastQuoteService")
	private InsuranceLastQuoteService lastQuoteService;

	@Resource(name = "insuranceCompanyService")
	private InsuranceCompanyService companyService;

	@Value("#{propertiesConfig['yobee.path']}")
	private String path;

	@Value("#{propertiesConfig['yobee.host']}")
	private String host;
	
	/** 石家庄城市代码 */
	private final String SJZ_CITY_CODE = "130100";

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getPolicyCompany()
	 */
	@Override
	public Response getPolicyCompany() throws BusinessException {

		try {
			List<InsuranceCompany> result = this.companyService.getCompanyByChannel("yobee");

			return Response.generateSuccessResponse(result);
		} catch (Exception e) {
			return Response.generateExceptionResponse(e);
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getVehicleType(com.ccby.dto.yobee.request.QueryVehicleType)
	 */
	@Override
	public Response getVehicleType(QueryVehicleType query) throws BusinessException {

		String provCode = StringUtils.defaultIfBlank(query.getProvCode(), "130000");
		String cityCode = StringUtils.defaultIfBlank(query.getCityCode(), SJZ_CITY_CODE);

		if (!StringUtils.startsWith(cityCode, "13")) {
			// 如果选择了非河北省的市区代码，则使用石家庄代码
			cityCode = SJZ_CITY_CODE;
		}

		VehicleDTO vehicle = null;

		String plateNumber = query.getPlateNumber();
		String vin = query.getVin();
		String engine = query.getEngine();

		if (StringUtils.isBlank(plateNumber) && StringUtils.isBlank(vin) && StringUtils.isBlank(engine)) {
			// 如果车牌号为空，vin与发动机号其中一个为空的话，返回异常
			return Response.generateExceptionResponse(Constant.FAIL.getCode(), "请输入车牌号！");
		}

		if (StringUtils.isBlank(plateNumber) && (StringUtils.isBlank(vin) || StringUtils.isBlank(engine))) {
			// 如果车牌号为空，vin与发动机号其中一个为空的话，返回异常
			return Response.generateExceptionResponse(Constant.FAIL.getCode(), "请输入车架号与发动机号！");
		}

		try {
			if (StringUtils.isNotBlank(query.getPlateNumber())) {
				// 根据车牌号查询车辆信息
				vehicle = yobeeService.vehicles(cityCode, query.getPlateNumber(), "02");

				if (vehicle == null && (StringUtils.isBlank(vin) || StringUtils.isBlank(engine))) {
					// 如果根据车牌号查询不到车型，但是vin与发动机号没传，则提示错误
					return Response.generateExceptionResponse(Constant.FAIL.getCode(), "请输入车架号与发动机号！");
				}
			}

			if (vehicle == null && StringUtils.isNotBlank(vin) && StringUtils.isNotBlank(engine)) {
				// 如果根据车牌号查询不到车型，根据vin与发动机号查询
				vehicle = yobeeService.vehiclesByVinAndEngine(cityCode, vin, engine);
			}

			if (vehicle == null) {
				// 根据车牌号，vin，发动机号都查不到车型，则返回空数据
				return Response.generateSuccessResponse(StringUtils.EMPTY);
			}

			// 获取保险公司列表
			List<ICAccount> icAccounts = yobeeService.getPolicyAccountByCityCodeAndIcCode(cityCode, null);

			List<ICDTO> icList = new ArrayList<ICDTO>();

			for (ICAccount icdto : icAccounts) {
				ICDTO ic = new ICDTO();
				ic.setAccount_id(icdto.getAccount_id());
				ic.setCode(icdto.getIc_code());

				icList.add(ic);
			}

			VehicleDTO vehicleParam = new VehicleDTO();
			vehicleParam.setVehicle_id(vehicle.getVehicle_id());
			vehicleParam.setLicense_type(null);

			// 创建车型查询
			Map<String, Object> params = new HashMap<String, Object>();
			String str = null;

			try {
				// 创建续保查询
				String renewalsRequest = yobeeService.createRenewalQuery(provCode, cityCode, null, vehicleParam, null);
				params.put("renewal_request_id", renewalsRequest);// 续保ID
			} catch (Exception e) {
				LOGER.error(e.getMessage(), e.getCause());
			}

			params.put("prov_code", provCode);
			params.put("city_code", cityCode);
			params.put("type", "vehicle_name");
			params.put("value", vehicle.getVehicle_id());
			params.put("value_type", "vehicle_id");
			params.put("enroll_date", vehicle.getEnroll_date());

			params.put("ics", icList);

			str = HttpUtils.httpPost(path + "/requests/ic-models", params);

			JSONObject request = JSONObject.fromObject(str);
			JSONObject result = null;
			while (true) {

				// 轮询车型查询结果
				str = HttpUtils.httpGet(host, path + "/requests/ic-models/" + request.getString("request_id"), null);
				result = JSONObject.fromObject(str);
				boolean done = result.getBoolean("is_done");

				if (done == true) {
					break;
				}

				Thread.sleep(500);
			}

			ICModelResult modelResult = JSONUtils.toBean(result.toString(), ICModelResult.class);

			List<ICModel> models = modelResult.getModels();
			StringBuffer text = null;

			for (ICModel icm : models) {
				text = new StringBuffer(icm.getText());

				icm.setVehicleId(vehicle.getVehicle_id());
				Map<String, Object> tags = icm.getTags();
				boolean lowest = Boolean.parseBoolean(tags.get("lowest").toString());
				boolean renewal = Boolean.parseBoolean(tags.get("renewal").toString());
				String ics = tags.get("ics").toString().replace("[", "").replace("]", "").replace(",", "／");

				if (renewal == true && lowest == true) {
					text.append(String.format("（续保且最低，%s）", ics));

				} else if (renewal == true && lowest == false) {
					text.append(String.format("（续保车型，%s）", ics));

				} else if (renewal == false && lowest == true) {
					text.append(String.format("（最低价车型，%s）", ics));

				} else {
					text.append(String.format("（%s）", ics));
				}

				icm.setText(text.toString());
			}

			return Response.generateSuccessResponse(modelResult.getModels());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getCarByPlateNumber(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Response getCarByPlateNumber(String cityCode, String plateNumber, QueryCar query) {
		try {

			if (!StringUtils.startsWith(StringUtils.defaultString(cityCode, SJZ_CITY_CODE), "13")) {
				// 如果选择了非河北省的市区代码，则使用石家庄代码
				cityCode = SJZ_CITY_CODE;
			}

			UserCarDto vehicleCar = yobeeService.getVehiclesCar(StringUtils.defaultString(cityCode, SJZ_CITY_CODE), plateNumber);
			if (vehicleCar != null) {

				if (StringUtils.isBlank(vehicleCar.getEngineNumber()) || StringUtils.isBlank(vehicleCar.getVehicleIdentificationNumber()) || StringUtils.isBlank(vehicleCar.getHolderName())
						|| StringUtils.isBlank(vehicleCar.getRegistTime())) {
					// 如果VIN、发动机号、车主姓名、注册日期其中有任何一个数据为空，则需要用户上传信息
					return Response.generateSuccessResponse("");
				}

				return Response.generateSuccessResponse(vehicleCar);
			}

			UserCarDto car = userCarService.getLatestUserCarInfoByPlateNumber(plateNumber);
			// 如果 注册日期 车牌号 车架号 识别代码 车牌号有一个为空 那么返回空
			if (car != null) {
				if (car.getRegistTime() == null || StringUtils.isEmpty(car.getBrand()) == true || StringUtils.isEmpty(car.getPlatesNumber()) == true
						|| StringUtils.isEmpty(car.getVehicleIdentificationNumber()) == true || StringUtils.isEmpty(car.getEngineNumber()) == true) {
					return Response.generateSuccessResponse("");
				}

				return Response.generateSuccessResponse(car);
			}

			car = userCarService.getLatestQtUserCarInfoByPlateNumber(plateNumber);

			if (car != null) {
				if (car.getRegistTime() == null || StringUtils.isEmpty(car.getBrand()) == true || StringUtils.isEmpty(car.getPlatesNumber()) == true
						|| StringUtils.isEmpty(car.getVehicleIdentificationNumber()) == true || StringUtils.isEmpty(car.getEngineNumber()) == true) {
					return Response.generateSuccessResponse("");
				}
				return Response.generateSuccessResponse(car);
			}

			return Response.generateSuccessResponse("");
		} catch (Exception e) {

			e.printStackTrace();
			return Response.generateExceptionResponse(e);
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getCarByVinAndEngine(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public Response getCarByVinAndEngine(String cityCode, String vin, String engine, QueryCar query) {

		try {
			if (!StringUtils.startsWith(StringUtils.defaultString(cityCode, SJZ_CITY_CODE), "13")) {
				// 如果选择了非河北省的市区代码，则使用石家庄代码
				cityCode = SJZ_CITY_CODE;
			}

			UserCarDto userCar = yobeeService.getCarByVinAndEngine(cityCode, vin, engine);

			return Response.generateSuccessResponse(userCar);

		} catch (Exception e) {
			LOGER.error(e.getMessage(), e.getCause());

			return Response.generateExceptionResponse(e);
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getRenewal(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Response getRenewal(String provCode, String cityCode, String plateNumber, String vin, String engine) throws BusinessException {

		try {
			if (!StringUtils.startsWith(StringUtils.defaultString(cityCode, SJZ_CITY_CODE), "13")) {
				// 如果选择了非河北省的市区代码，则使用石家庄代码
				cityCode = SJZ_CITY_CODE;
			}

			QueryCar query = new QueryCar();
			query.setPlateNumber(plateNumber);
			query.setVin(vin);
			query.setEngine(engine);

			return getCarByPolicy(provCode, cityCode, null, plateNumber, null, query);
		} catch (Exception e) {

			return Response.generateExceptionResponse(e);
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#getCarByPolicy(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, com.ccby.dto.yobee.request.QueryCar)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Response getCarByPolicy(String provCode, String cityCode, String icCode, String plateNumber, String policyNo, QueryCar query) {

		Map map = new HashMap();
		try {
			String vin = query == null || StringUtils.isBlank(query.getVin()) ? null : query.getVin();
			String engine = query == null || StringUtils.isBlank(query.getEngine()) ? null : query.getEngine();

			if (StringUtils.isBlank(plateNumber) && (StringUtils.isBlank(vin) || StringUtils.isBlank(engine))) {
				return Response.generateExceptionResponse(Constant.FAIL.getCode(), "车牌号为空，请填写车架号与发动机号！");
			}

			VehicleDTO vehicles = null;

			if (query == null) {
				query = new QueryCar();
			}

			if (StringUtils.isBlank(query.getCarType())) {
				query.setCarType("02");
			}

			if (StringUtils.isBlank(query.getPlateType())) {
				query.setPlateType("A01");
			}

			if (!StringUtils.startsWith(StringUtils.defaultString(cityCode, SJZ_CITY_CODE), "13")) {
				// 如果选择了非河北省的市区代码，则使用石家庄代码
				cityCode = SJZ_CITY_CODE;
			}

			if (StringUtils.isNotBlank(plateNumber)) {

				vehicles = yobeeService.vehicles(cityCode, plateNumber, query.getCarType());

				if (vehicles == null && StringUtils.isNotBlank(vin) && StringUtils.isNotBlank(engine)) {
					vehicles = yobeeService.vehiclesByVinAndEngine(cityCode, vin, engine);
				}

				InsuranceLastQuote quote = lastQuoteService.getInsuranceLastQuote(plateNumber);

				if (quote != null) {

					Date today = DateUtils.getCurrentDate();
					Date commercialEndDate = quote.getCommercialEndDate();
					Date compulsoryEndDate = quote.getCompulsoryEndDate();
					boolean isRet = true;

					if (compulsoryEndDate != null && DateUtils.getAbsolutionDifferenceDay(compulsoryEndDate, today) <= 90){
						// 交强险到期时间如果小于90天到期，则走金斗云续保查询
						isRet = false;
					}
					
					if (commercialEndDate != null && DateUtils.getAbsolutionDifferenceDay(commercialEndDate, today) <= 90){
						// 商业险到期时间如果小于90天到期，则走金斗云续保查询
						isRet = false;
					}
					
					if (commercialEndDate != null && today.compareTo(commercialEndDate) > 0) {
						// 当前时间大于商业险保险止期，则走金斗云续保信息
						isRet = false;
					}

					if (compulsoryEndDate != null && today.compareTo(compulsoryEndDate) > 0) {
						// 当前时间大于交强险保险止期，则走金斗云续保信息
						isRet = false;
					}

					if (isRet) {
						// 本地数据的商业险与交强险的续保截至日期还有效，则返回本地数据
						map.put("lastQuote", quote);
						map.put("vehicles", vehicles);
						return Response.generateSuccessResponse(map);
					}
					if (vehicles == null) {
						LOGER.info("暂未查询到车辆信息！返回本地续保信息");
						map.put("lastQuote", quote);
						map.put("vehicles", vehicles);
						return Response.generateSuccessResponse(map);
					}
				}
			}

			if (vehicles == null) {
				return Response.generateExceptionResponse(Constant.FAIL.getCode(), "暂未查询到保单信息！");
			}

			VehicleDTO temp = new VehicleDTO();
			temp.setVehicle_id(vehicles.getVehicle_id());

			Renewal renewal = yobeeService.getRenewal(provCode, cityCode, icCode, temp, policyNo);

			if (renewal == null) {
				return Response.generateExceptionResponse(Constant.FAIL.getCode(), "暂未查询到保单信息！");
			}

			if (!StringUtils.equalsIgnoreCase(renewal.getVehicle().getLicense_no(), plateNumber)) {
				renewal.getVehicle().setLicense_no(plateNumber);
			}

			InsuranceLastQuote lastQuote = transformInsuranceQuote(renewal);
			lastQuoteService.saveInsuranceLastQuote(lastQuote);
			map.put("lastQuote", lastQuote);
			map.put("vehicles", vehicles);
			return Response.generateSuccessResponse(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.generateExceptionResponse(Constant.FAIL.getCode(), "暂未查询到保单信息，请使用车牌号查询！");
	}
	
	/**
	 * 把金斗云续保信息转换成本地数据格式
	 * 
	 * @param renewal
	 *            金斗云续保信息
	 * 
	 * @return
	 */
	private InsuranceLastQuote transformInsuranceQuote(Renewal renewal) {

		if (renewal == null) {
			return null;
		}

		InsuranceLastQuote quote = new InsuranceLastQuote();

		VehicleDTO vehicle = renewal.getVehicle();
		ForceInfo forceInfo = renewal.getForceInfo();
		BizInfo bizInfo = renewal.getBizInfo();

		quote.setRenewalChannel(renewal.getChannel());// 上年投保渠道
		quote.setHolderName(renewal.getOwner().getName());// 被保人姓名
		quote.setPlateNumber(vehicle.getLicense_no());// 车牌号
		quote.setSeatNumber(vehicle.getSeat_count());// 座位数
		quote.setLicenseType(vehicle.getLicense_type());// 车辆种类
		quote.setEnrollDate(vehicle.getEnroll_date());// 初登日期

		if (forceInfo != null && StringUtils.isNotBlank(forceInfo.getIcCode())) {
			try {
				quote.setCompulsoryPolicyNumber(forceInfo.getPolicyNo());// 交强保单号
				quote.setCompulsoryStartDate(DateUtils.getFormatDate(forceInfo.getStartDate(), DateUtils.FORMAT_DATE));
				quote.setCompulsoryEndDate(DateUtils.getFormatDate(forceInfo.getEndDate(), DateUtils.FORMAT_DATE));
				quote.setCompanyNameCpy(forceInfo.getIcName());// 保险公司名称
				quote.setCompulsory(StringUtils.isBlank(forceInfo.getPremium()) ? 0 : Double.parseDouble(forceInfo.getPremium()));// 交强险保费
				quote.setTravelTax(StringUtils.isBlank(forceInfo.getTax()) ? 0 : Double.parseDouble(forceInfo.getTax()));// 车船税

				// 设置保险公司ID
				InsuranceCompany company = this.companyService.getCompanyByCode(forceInfo.getIcCode());

				if (company != null && company.getId() > 0) {
					quote.setInsuranceCompanyCpy(company.getId());
				}

			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}

		if (bizInfo != null && StringUtils.isNotBlank(bizInfo.getIcCode())) {
			try {
				quote.setCompanyNameCml(bizInfo.getIcName());
				quote.setCommercialPolicyNumber(bizInfo.getPolicyNo());// 商业险保单号
				quote.setCommercialStartDate(DateUtils.getFormatDate(bizInfo.getStartDate(), DateUtils.FORMAT_DATE));
				quote.setCommercialEndDate(DateUtils.getFormatDate(bizInfo.getEndDate(), DateUtils.FORMAT_DATE));

				// 设置保险公司ID
				InsuranceCompany company = this.companyService.getCompanyByCode(bizInfo.getIcCode());

				if (company != null && company.getId() > 0) {
					quote.setInsuranceCompanyCml(company.getId());
				}

				Map<String, BIZCoverage> selection = bizInfo.getSelection();
				BigDecimal commercial = new BigDecimal(0);

				for (Entry<String, BIZCoverage> entry : selection.entrySet()) {

					double amount = Double.parseDouble(entry.getValue().getAmount());
					double premium = Double.parseDouble(entry.getValue().getPremium());

					commercial = commercial.add(new BigDecimal(entry.getValue().getPremium()));

					switch (entry.getKey()) {
					case "damage":// 机动车辆损失险
						quote.setIns1(1);
						quote.setIne1(amount);
						quote.setInp1(premium);

						break;
					case "exempt_damage":// 机动车辆损失险(不计免赔)
						quote.setNoi1(1);
						quote.setNop1(premium);
						break;

					case "third":// 第三者责任险
						quote.setIns2(amount);
						quote.setInp2(premium);
						break;
					case "exempt_third":// 第三者责任险(不计免赔)
						quote.setNoi2(1);
						quote.setNop2(premium);
						break;
					case "driver":// 司机险
						quote.setIns3(amount);
						quote.setInp3(premium);
						break;
					case "exempt_driver":// 司机险(不计免赔)
						quote.setNoi3(1);
						quote.setNop3(premium);
						break;
					case "passenger":// 乘客险
						quote.setIns4(amount);
						quote.setInp4(premium);
						break;
					case "exempt_passenger":// 乘客险(不计免赔)
						quote.setNoi4(1);
						quote.setNop4(premium);
						break;
					case "pilfer":// 盗抢险
						quote.setIns5(amount);
						quote.setInp5(premium);
						break;
					case "exempt_pilfer":// 盗抢险(不计免赔)
						quote.setNoi5(1);
						quote.setNop5(premium);
						break;
					case "glass":// 玻璃险
						quote.setAttach1(amount);
						quote.setAttachP1(premium);
						break;
					case "scratch":// 划痕险
						quote.setAttach2(amount);
						quote.setAttachP2(premium);
						break;
					case "combust":// 自燃险
						quote.setAttach3(1);
						quote.setAttachP3(premium);
						break;
					case "water":// 涉水险
						quote.setAttach4(1);
						quote.setAttachP4(premium);
						break;
					case "factory":// 指定专修厂

						quote.setAttachP5(premium);

						if (Double.parseDouble(entry.getValue().getAmount()) == 0) {
							quote.setAttach5(0);
							break;
						}

						if (StringUtils.isBlank(vehicle.getFrame_no()) || vehicle.getFrame_no().startsWith("L")) {
							// L开头是国产车，如果投保了指定专修则费率是10%，进口15%
							quote.setAttach5(1);
						} else {

							quote.setAttach5(2);
						}

						break;
					}
				}

				quote.setCommercial(commercial.doubleValue());

			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}

		return quote;
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#acquiringMeta(java.lang.String)
	 */
	@Override
	public String acquiringMeta(String cityCode) {

		try {
			String reStr = HttpUtils.httpGet(host, path + "/meta/130100", null);
			saveAcquiringMeta(reStr);

			return reStr;
		} catch (Exception e) {
			LOGER.error(e.getMessage());
			return "";
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#updatePolicyAccount()
	 */
	@Override
	public Response updatePolicyAccount() throws BusinessException {

		baseDAO.execute("deleteAllPolicyAccount");

		org.json.JSONObject result = HttpUtils.httpGetToJson(host, path + "/accounts", Collections.singletonMap("page", 1));
		try {
			int total = result.getInt("total");
			int size = result.getInt("size");
			int totalPage = total / size;

			if (total % size != 0) {
				totalPage = total / size + 1;
			}

			org.json.JSONArray accounts = result.getJSONArray("accounts");

			for (int page = 1; page <= totalPage; page++) {

				if (page > 1) {
					result = HttpUtils.httpGetToJson(host, path + "/accounts", Collections.singletonMap("page", page));
					accounts = result.getJSONArray("accounts");
				}

				ICAccount account = null;
				for (int i = 0; i < accounts.length(); i++) {
					org.json.JSONObject acc = accounts.getJSONObject(i);
					account = com.alibaba.fastjson.JSONObject.parseObject(acc.toString(), ICAccount.class);

					org.json.JSONObject info = acc.getJSONObject("info");

					if (info.isNull("user")) {
						continue;
					}

					account.setUser(info.getString("user"));
					account.setPass(info.getString("pass"));

					baseDAO.insert("savePolicyCompanyAccount", account);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveAcquiringMeta(String reStr) {
		// 清空所有表数据
		baseDAO.delete("deleteInsuranceCompany", null);
		baseDAO.delete("deleteSalesChannel", null);
		baseDAO.delete("deleteRequireAccountFields", null);
		baseDAO.delete("deleteVehicle", null);
		baseDAO.delete("deleteCoverages", null);

		JSONObject reStrJson = JSONObject.fromObject(reStr);
		// 支持的保险公司列表
		JSONArray icsJsonArray = reStrJson.getJSONArray("ics");
		for (int i = 0; i < icsJsonArray.size(); i++) {
			JSONObject icsJsonObject = JSONObject.fromObject(icsJsonArray.get(i));
			String code = icsJsonObject.getString("code");
			String name = icsJsonObject.getString("name");
			String logoUrl = icsJsonObject.getString("logo_url");
			Object isSupportRenewal = icsJsonObject.get("is_support_renewal");
			Object isRequireIdCheck = icsJsonObject.get("is_require_id_check");
			Object isSupportIdCode = icsJsonObject.get("is_support_id_code");

			InsuranceCompany insuranceCompany = new InsuranceCompanyImpl();
			insuranceCompany.setAutoQuoteTag(code);
			insuranceCompany.setName(name);
			insuranceCompany.setOriginalUrl(logoUrl);
			insuranceCompany.setDetailUrl(logoUrl);
			insuranceCompany.setAutoQuote(1);

			if (Boolean.FALSE.toString().equals(isSupportRenewal.toString())) {
				insuranceCompany.setIsSupportRenewal(0);
			} else if (Boolean.TRUE.toString().equals(isSupportRenewal.toString())) {
				insuranceCompany.setIsSupportRenewal(1);
			} else {
				insuranceCompany.setIsSupportRenewal(Integer.parseInt(isSupportRenewal.toString()));
			}

			if (Boolean.FALSE.toString().equals(isRequireIdCheck.toString())) {
				insuranceCompany.setIsRequireIdCheck(0);
			} else if (Boolean.TRUE.toString().equals(isRequireIdCheck.toString())) {
				insuranceCompany.setIsRequireIdCheck(1);
			} else {
				insuranceCompany.setIsRequireIdCheck(Integer.parseInt(isRequireIdCheck.toString()));
			}

			if (Boolean.FALSE.toString().equals(isSupportIdCode.toString())) {
				insuranceCompany.setIsSupportIdCode(0);
			} else if (Boolean.TRUE.toString().equals(isSupportIdCode.toString())) {
				insuranceCompany.setIsSupportIdCode(1);
			} else {
				insuranceCompany.setIsSupportIdCode(Integer.parseInt(isSupportIdCode.toString()));
			}
			insuranceCompany.setIcChannel("yobee");
			baseDAO.insert("saveJdyCompany", insuranceCompany);
			long id = insuranceCompany.getId();

			// 支持的销售渠道
			JSONArray salesChannels = icsJsonObject.getJSONArray("sales_channels");
			for (int j = 0; j < salesChannels.size(); j++) {
				JSONObject salesChannelsObject = JSONObject.fromObject(salesChannels.get(j));
				String text = salesChannelsObject.getString("text");
				String saleCode = salesChannelsObject.getString("code");
				SalesChannel salesChannel = new SalesChannelImpl();
				salesChannel.setName(text);
				salesChannel.setCode(saleCode);
				salesChannel.setIcId(id);
				baseDAO.insert("saveSalesChannel", salesChannel);
			}

			// 需要的账号字段
			JSONArray requireAccountFields = icsJsonObject.getJSONArray("require_account_fields");
			for (int j = 0; j < requireAccountFields.size(); j++) {
				JSONObject salesChannelsObject = JSONObject.fromObject(requireAccountFields.get(j));
				String fieldName = salesChannelsObject.getString("name");
				String text = salesChannelsObject.getString("text");
				String type = salesChannelsObject.getString("type");
				String description = salesChannelsObject.getString("description");
				Object required = salesChannelsObject.get("required");
				Object loginRequired = salesChannelsObject.get("login_required");
				Object allowNonAscii = salesChannelsObject.get("allow_non_ascii");
				int order = salesChannelsObject.getInt("order");

				RequireAccountFields accountFields = new RequireAccountFieldsImpl();
				accountFields.setName(fieldName);
				accountFields.setText(text);
				accountFields.setType(type);
				accountFields.setDescription(description);
				if (Boolean.FALSE.toString().equals(required.toString())) {
					accountFields.setRequired(0);
				} else if (Boolean.TRUE.toString().equals(required.toString())) {
					accountFields.setRequired(1);
				} else {
					accountFields.setRequired(Integer.parseInt(required.toString()));
				}

				if (Boolean.FALSE.toString().equals(loginRequired.toString())) {
					accountFields.setLoginRequired(0);
				} else if (Boolean.TRUE.toString().equals(loginRequired.toString())) {
					accountFields.setLoginRequired(1);
				} else {
					accountFields.setLoginRequired(Integer.parseInt(loginRequired.toString()));
				}

				if (allowNonAscii != null) {
					if (Boolean.FALSE.toString().equals(allowNonAscii.toString())) {
						accountFields.setAllowNonAscii(0);
					} else if (Boolean.TRUE.toString().equals(allowNonAscii.toString())) {
						accountFields.setAllowNonAscii(1);
					} else {
						accountFields.setAllowNonAscii(Integer.parseInt(allowNonAscii.toString()));
					}
				}

				accountFields.setOrder(order);
				accountFields.setIcId(id);
				baseDAO.insert("saveRequireAccountFields", accountFields);
			}
		}
	}
	
	/**
	 * @see com.ccby.api.yobee.service.RestYobeeService#notification(com.ccby.dto.yobee.request.NotificationData)
	 */
	@Override
	public void notification(NotificationData header) {
		
		if (header == null) {
			return;
		}

		JSONObject json = JSONObject.fromObject(header);

		LOGER.info(String.format("[异步通知] 接收到金斗云异步通知消息:%s，报文：%s", header.getNotification_id(), json.toString()));

		switch (header.getType()) {
		
		case "Propose":
			// 投保结果通知
			ProposeDTO propose = JSONUtils.toBean(json.getJSONObject("data").toString(), ProposeDTO.class);

			try {
				LOGER.info(String.format("[异步通知] 接收到投保单创建异步结果通知， 开始更新通知ID为：%s的投保单信息！", header.getNotification_id()));

				yobeeService.updateProposeInfo(header.getNotification_id(), propose);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

		LOGER.info(String.format("[异步通知] 接收到金斗云异步通知：%s处理完成！", header.getNotification_id()));
		
		HttpServletResponse response = (HttpServletResponse) RpcContext.getContext().getResponse();
		response.setStatus(202);
		
		try {
			response.getWriter().print("SUCCESS");
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
