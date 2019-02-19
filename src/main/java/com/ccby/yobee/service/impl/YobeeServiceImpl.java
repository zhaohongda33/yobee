package com.ccby.yobee.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.ccby.api.base.service.InsuranceLastQuoteService;
import com.ccby.api.base.service.InsuranceService;
import com.ccby.api.base.service.ProductService;
import com.ccby.api.base.service.UserCarService;
import com.ccby.api.core.dto.UserCarDto;
import com.ccby.api.core.dto.insurance.InsuranceQuoteDTO;
import com.ccby.api.yobee.service.YobeeService;
import com.ccby.core.base.dao.BaseDAO;
import com.ccby.core.base.util.BusinessException;
import com.ccby.core.base.util.Constant;
import com.ccby.core.base.util.JSONUtils;
import com.ccby.core.base.util.ModelTransformUtils;
import com.ccby.dto.yobee.BizInfo;
import com.ccby.dto.yobee.ClaimsDTO;
import com.ccby.dto.yobee.ForceInfo;
import com.ccby.dto.yobee.ICAccount;
import com.ccby.dto.yobee.ICDTO;
import com.ccby.dto.yobee.ICModel;
import com.ccby.dto.yobee.ICModelResult;
import com.ccby.dto.yobee.ICModelsQueryDTO;
import com.ccby.dto.yobee.InsuranceResultsDTO;
import com.ccby.dto.yobee.PrivyDTO;
import com.ccby.dto.yobee.ProposeDTO;
import com.ccby.dto.yobee.QuotationsDTO;
import com.ccby.dto.yobee.Renewal;
import com.ccby.dto.yobee.Renewal.Person;
import com.ccby.dto.yobee.ResultsQuotationsDTO;
import com.ccby.dto.yobee.SelectionDTO;
import com.ccby.dto.yobee.VehicleDTO;
import com.ccby.dto.yobee.VehicleModelsDTO;
import com.ccby.enums.enumtypes.DataType;
import com.ccby.module.base.user.UserCar;
import com.ccby.module.base.user.UserCarImpl;
import com.ccby.module.insurance.InsuranceCompany;
import com.ccby.module.insurance.InsuranceImpl;
import com.ccby.module.product.Product;
import com.ccby.module.product.ProductImpl;
import com.ccby.module.product.attribute.ProductAttribute;
import com.ccby.module.product.attribute.ProductAttributeImpl;
import com.ccby.yobee.utils.HttpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("yobeeService")
public class YobeeServiceImpl implements YobeeService {

	private Logger LOGER = Logger.getLogger(YobeeServiceImpl.class);

	@Resource(name = "defaultDAO")
	private BaseDAO baseDAO;

	@Resource(name = "insuranceServiceApi")
	private InsuranceService insuranceService;
	
	@Resource(name = "userCarService")
	private UserCarService userCarService;
	
	@Resource(name = "insuranceLastQuoteService")
	private InsuranceLastQuoteService lastQuoteService;
	
	@Resource(name = "productService")
	private ProductService productService;

	/** 线程池 */
	private ExecutorService yobee = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Value("#{propertiesConfig['yobee.path']}")
	private String path;

	@Value("#{propertiesConfig['yobee.host']}")
	private String host;
	
	/** 自动报价服务的域名地址 */
	@Value("#{propertiesConfig['yobee.domain']}")
	private String domain;

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#quoteNew(java.util.ArrayList, java.util.ArrayList, com.ccby.module.base.user.UserCarImpl, com.ccby.module.insurance.InsuranceImpl, com.ccby.dto.yobee.QuotationsDTO, com.ccby.dto.yobee.SelectionDTO)
	 */
	@Override
	public String quoteNew(ArrayList<InsuranceCompany> companies, ArrayList<ICDTO> icsList, UserCarImpl userCar, InsuranceImpl insurance, QuotationsDTO quotations, SelectionDTO selections)
			throws BusinessException {
		return quote(companies, icsList, userCar, insurance, quotations, selections);
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#quote(java.util.ArrayList, java.util.ArrayList, com.ccby.module.base.user.UserCarImpl, com.ccby.module.insurance.InsuranceImpl, com.ccby.dto.yobee.QuotationsDTO, com.ccby.dto.yobee.SelectionDTO)
	 */
	@Override
	public String quote(ArrayList<InsuranceCompany> companies, ArrayList<ICDTO> icsList, UserCarImpl userCar, InsuranceImpl insurance, QuotationsDTO quotations, SelectionDTO selections)
			throws BusinessException {

		LOGER.info(String.format("【金斗云自动报价】询价：%s，进入金斗云自动报价阶段！", insurance.getId()));
		
		transferInsuranceField(insurance, selections);// 转换字段
		
		if (!StringUtils.startsWith(StringUtils.defaultString(quotations.getCityCode(), "130100"), "13")){
			// 如果选择了非河北省的市区代码，则使用石家庄代码
			quotations.setCityCode("130100");
		}
		
		LOGER.info(String.format("【金斗云自动报价】车牌号：%s，VIN：%s，发动机号：%s，选择险种：%s", userCar.getPlatesNumber(), userCar.getVehicleIdentificationNumber(), userCar.getEngineNumber(), JSONUtils.toJsonStr(selections)));

		quotations.setFuelType("0"); // 燃油性质 写死 非电
		quotations.setVehicleClass("A01");// 车辆类型 客车
		quotations.setVehicleNature("211");// 车辆使用性质家庭自用
		quotations.setInsurantType("01");// 持有人类型个人
		quotations.setSelection(selections);
		
		VehicleDTO vehicleDTO = null;
		
		if (StringUtils.isNotBlank(quotations.getIcModelId()) && quotations.getVehicle() != null && StringUtils.isNotBlank(quotations.getVehicle().getVehicle_id())){
			vehicleDTO = quotations.getVehicle();
			vehicleDTO.setLicense_no(userCar.getPlatesNumber());
			vehicleDTO.setLicense_type("02");
			vehicleDTO.setEngine_no(userCar.getEngineNumber());
			vehicleDTO.setEnroll_date(userCar.getRegistTime());
			vehicleDTO.setFrame_no(userCar.getVehicleIdentificationNumber());
			Integer carSeat = userCar.getCarSeat();
			if (carSeat == null || carSeat.intValue() <= 0){
				carSeat = 5;
			}
			
			vehicleDTO.setSeat_count(carSeat.toString());
			vehicleDTO.setOwner(userCar.getHolderName());
		}
		
		if (vehicleDTO == null){
			
			if (userCar.getNewCar() != null && userCar.getNewCar().booleanValue() == true){
				// 新车使用VIN与发动机号查询
				
				if (StringUtils.isBlank(userCar.getVehicleIdentificationNumber()) || StringUtils.isBlank(userCar.getEngineNumber())){
					throw new BusinessException(Constant.FAIL.getCode(), "新车必须填写车架号与发动机号！");
				}
				
				vehicleDTO = vehiclesByVinAndEngine(quotations.getCityCode(), userCar.getVehicleIdentificationNumber(), userCar.getEngineNumber());
			} else {
				
				vehicleDTO = vehicles(quotations.getCityCode(), userCar.getPlatesNumber(), "02");
			}
		}
		
		if (StringUtils.isEmpty(vehicleDTO.getLicense_no()) || StringUtils.isEmpty(vehicleDTO.getEngine_no())
				|| StringUtils.isEmpty(vehicleDTO.getEnroll_date()) || StringUtils.isEmpty(vehicleDTO.getFrame_no())
				|| StringUtils.isEmpty(vehicleDTO.getOwner())) {
			vehicleDTO = null;
		}

		if (userCar.getTransferVehicle() != null && userCar.getTransferVehicle().intValue() == 1){
			// 过户车，设置过户时间
			Map<String, Object> extend = new HashMap<String, Object>();
			extend.put("is_transfer", true);
			extend.put("transfer_date", userCar.getTransferDate());
			
			quotations.setExtend(extend);
		}

		// if (vehicleDTO == null){
		// vehicleDTO =
		// yobeeService.vehiclesByVinAndEngine(quotations.getCityCode(),
		// userCar.getVehicleIdentificationNumber(), userCar.getEngineNumber());
		// }

		if (vehicleDTO == null) {
			// 根据车牌号在金斗云查询不到车辆信息
			vehicleDTO = new VehicleDTO();
			vehicleDTO.setLicense_no(userCar.getPlatesNumber());
			vehicleDTO.setLicense_type("02");
			vehicleDTO.setEngine_no(userCar.getEngineNumber());
			vehicleDTO.setEnroll_date(userCar.getRegistTime());
			vehicleDTO.setFrame_no(userCar.getVehicleIdentificationNumber());
			
			Integer carSeat = userCar.getCarSeat();
			if (carSeat == null || carSeat.intValue() <= 0){
				carSeat = 5;
			}
			
			vehicleDTO.setSeat_count(carSeat.toString());
			vehicleDTO.setOwner(userCar.getHolderName());

			LOGER.info(String.format("【金斗云自动报价】城市代码：%s，车牌号：%s\n识别参数：%s\n结果：在金斗云系统查询不到，使用识别的车辆信息进行报价！", quotations.getCityCode(), userCar.getPlatesNumber(),
					new org.json.JSONObject(vehicleDTO).toString()));
		}

		if (vehicleDTO != null) {
			quotations.setVehicle(vehicleDTO);

			SelectionDTO selection = quotations.getSelection();
			if (StringUtils.isNotBlank(selection.getFactory()) && !StringUtils.equals(selection.getFactory(), "0")) {
				// 投保了指定专修

				if (vehicleDTO.getFrame_no().startsWith("L")) {
					// L开头是国产车，如果投保了指定专修则费率是10%，进口15%
					selection.setFactory("1");
					quotations.setFactory_rate(10);
				} else {

					selection.setFactory("2");
					quotations.setFactory_rate(15);
				}
			}
		}

		quotations.setCityCode(StringUtils.defaultIfBlank(quotations.getCityCode(), "130100"));
		quotations.setProvCode(StringUtils.defaultIfBlank(quotations.getProvCode(), "130000"));
		
		for (ICDTO icdto : icsList) {

			quotations.setIcs(Collections.singletonList(icdto));

			try {
				String requestId = createYobeeQuotations(quotations);// 创建报价
				String remark = quotations.getVehicleText();// 报价车型的描述
				
				if (StringUtils.isBlank(remark)){
					remark = quotations.getVehicle().getModels().get(0).getRemark();
				}
				
				// 更新询价的报价车型信息
				try {
					insurance.setVehicleType(remark);
					insuranceService.updateInsuranceVehicleType(insurance);
				} catch (Exception e2) {
					// 容错处理，这里更新报价车型信息，即使报错也不会影响业务，所以不做处理
					LOGER.info("【金斗云自动报价】更新询价的车型信息出错，不影响业务，不做处理！");
				}
				
				if (StringUtils.isBlank(requestId)) {
					// 报价失败
					LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，报价失败！", icdto.getCode(), userCar.getPlatesNumber()));
					generateInsuranceResult(companies, userCar, insurance, icdto, "报价失败，请转人工报价。");
					continue;
				}
				
				if (StringUtils.contains(requestId, "error")){
					// 出现错误
					LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，报价失败！", icdto.getCode(), userCar.getPlatesNumber()));
					generateInsuranceResult(companies, userCar, insurance, icdto, JSONObject.fromObject(requestId).getString("error"));
					continue;
				}

				LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，报价请求ID：%s开始进入报价阶段。", icdto.getCode(), userCar.getPlatesNumber(), requestId));

				yobee.execute(new Runnable() {
					
					/**
					 * @see java.lang.Runnable#run()
					 */
					@Override
					public void run() {
						try {
							if (StringUtils.isBlank(requestId)) {
								LOGER.error("【金斗云自动报价】报价后返回的requestId为空，退出报价！");
								return;
							}
							InsuranceResultsDTO insuranceResultsDTO = getQuotations(requestId);
							
							if (insuranceResultsDTO == null) {
								LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，金斗云接口调用超时！", icdto.getCode(), userCar.getPlatesNumber()));
								generateInsuranceResult(companies, userCar, insurance, icdto, "报价超时，请重新尝试报价。");
								return;
							}
							
							userCar.setVehicleId(quotations.getVehicle().getVehicle_id());// 设置报价车辆ID
							insuranceService.saveInsuranceProudctByYobee(companies, userCar, insurance, insuranceResultsDTO, quotations);

							String msg = "报价成功";
							if (insuranceResultsDTO == null || insuranceResultsDTO.getQuotations() == null) {
								// 报价超时
								LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，金斗云接口调用超时！", icdto.getCode(), userCar.getPlatesNumber()));
								generateInsuranceResult(companies, userCar, insurance, icdto, "报价超时，请重新尝试报价。");
								return;
							}

							ResultsQuotationsDTO result = insuranceResultsDTO.getQuotations().get(0);
							if (StringUtils.equalsIgnoreCase(result.getIs_success(), Boolean.FALSE.toString())) {
								msg = String.format("消息[%s]，备注[%s]", result.getMessage(), result.getComment());
							}

							LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，报价请求ID：%s，报价结果：%s", icdto.getCode(), userCar.getPlatesNumber(), requestId, msg));
						} catch (Exception e) {
							if (e instanceof TimeoutException || e.getCause() instanceof TimeoutException) {
								// 接口链接超时
								LOGER.info(String.format("【金斗云自动报价】保险公司：%s，车牌号：%s，金斗云接口调用超时！", icdto.getCode(), userCar.getPlatesNumber()));
								generateInsuranceResult(companies, userCar, insurance, icdto, "报价超时，请重新尝试报价。");
								return;
							}

							e.printStackTrace();
						}
					}
				});

			} catch (Exception e) {
				generateInsuranceResult(companies, userCar, insurance, icdto, "报价失败，请转人工报价。");
				
				e.printStackTrace();
				continue;
			}
		}

		return null;
	}

	/**
	 * @param insurance
	 * @param selections
	 */
	private void transferInsuranceField(InsuranceImpl insurance, SelectionDTO selections) {
		
		if (selections.getDamage() != null || selections.getForce() != null){
			// 不是老版APP穿过来的参数
			return;
		}
		
		LOGER.info("【金斗云自动报价】开始转换老保险字段与金斗云字段转换！");
		
		String force = insurance.getInsuranceType().intValue() != 1 ? "1" : "0";// 交强
		String damage = insurance.getIns1() != null ? insurance.getIns1().toString() : "0";// 车损
		String exempt_damage = insurance.getNoi1() != null ? insurance.getNoi1().toString() : "0";// 车损不计免赔
		
		String third = insurance.getIns2() != null ? insurance.getIns2().toString() : "0";// 三者
		String exempt_third = insurance.getNoi2() != null ? insurance.getNoi2().toString() : "0";// 三者不计免赔
		
		String driver = insurance.getIns3() != null ? insurance.getIns3().toString() : "0";// 司机座位险
		String exempt_driver = insurance.getNoi3() != null ? insurance.getNoi3().toString() : "0";// 司机座位险不计免赔
		
		String passenger = insurance.getIns4() != null ? insurance.getIns4().toString() : "0";// 乘客座位险
		String exempt_passenger = insurance.getNoi4() != null ? insurance.getNoi4().toString() : "0";// 乘客座位险不计免赔
		
		String pilfer = insurance.getIns5() != null ? insurance.getIns5().toString() : "0";// 盗抢险
		String exempt_pilfer = insurance.getNoi5() != null ? insurance.getNoi5().toString() : "0";// 盗抢险不计免赔
		
		String glass = insurance.getAttach1() != null ? insurance.getAttach1().toString() : "0";// 玻璃破碎险 1 国产 2 进口
		String scratch = insurance.getAttach2() != null ? insurance.getAttach2().toString() : "0";// 划痕险
		String combust = insurance.getAttach3() != null ? insurance.getAttach3().toString() : "0";// 自燃险
		String water = insurance.getAttach4() != null ? insurance.getAttach4().toString() : "0";// 涉水险
		String factory = insurance.getAttach5() != null ? insurance.getAttach5().toString() : "0";// 指定专修厂特约险
		
		selections.setDamage(damage);
		selections.setExempt_damage(exempt_damage);
		selections.setThird(third);
		selections.setExempt_third(exempt_third);
		selections.setDriver(driver);
		selections.setExempt_driver(exempt_driver);
		selections.setPassenger(passenger);
		selections.setExempt_passenger(exempt_passenger);
		selections.setPilfer(pilfer);
		selections.setExempt_pilfer(exempt_pilfer);
		selections.setGlass(glass);
		selections.setScratch(scratch);
		selections.setCombust(combust);
		selections.setWater(water);
		selections.setFactory(factory);
		selections.setForce(force);
	}

	private void generateInsuranceResult(List<InsuranceCompany> companies, UserCarImpl userCar, InsuranceImpl insurance, ICDTO icdto, String message) {
		InsuranceResultsDTO insuranceResultsDTO = new InsuranceResultsDTO();
		insuranceResultsDTO.setIs_done(true);
		ResultsQuotationsDTO resultQuote = new ResultsQuotationsDTO();
		resultQuote.setIs_done(true);
		resultQuote.setIs_success("false");
		resultQuote.setIc_code(icdto.getCode());
		resultQuote.setMessage(message);

		insuranceResultsDTO.setQuotations(Collections.singletonList(resultQuote));
		insuranceService.saveInsuranceProudctByYobee(new ArrayList<InsuranceCompany>(companies), userCar, insurance, insuranceResultsDTO, new QuotationsDTO());
	}
	
	/**
	 * 封装车辆信息
	 * 
	 * @param quotations
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ICModel packageVehicle(QuotationsDTO quotations, Map params) throws BusinessException{
		
		ICDTO icdto = quotations.getIcs().get(0);
		VehicleDTO prototype = quotations.getVehicle();
		
		ICModelsQueryDTO query = new ICModelsQueryDTO();
		query.setProv_code(quotations.getProvCode());
		query.setCity_code(quotations.getCityCode());

		List<ICAccount> cityAccount = getPolicyAccountByCityCodeAndIcCode(quotations.getCityCode(), icdto.getCode());

		if (cityAccount != null && !cityAccount.isEmpty()) {
			quotations.getIcs().get(0).setAccount_id(cityAccount.get(0).getAccount_id());
		} else {
			LOGER.info(String.format("【金斗云创建报价】保险公司：%s，车牌号：%s，该保险公司不存在账户信息！", icdto.getCode(), prototype.getLicense_no()));
			return null;
		}
		
		VehicleDTO vehicle = new VehicleDTO();

		if (StringUtils.isBlank(quotations.getVehicle().getVehicle_id())) {
			vehicle = prototype;
		} else {

			vehicle.setVehicle_id(prototype.getVehicle_id());
		}

		query.setIcs(quotations.getIcs());
		query.setValue(vehicle.getVehicle_id());
		query.setEnroll_date(prototype.getEnroll_date());

		ICModelResult icModel = getICModel(query, prototype);

		if (icModel == null) {
			LOGER.info(String.format("【金斗云创建报价】保险公司：%s，车牌号：%s，无法查询到车型信息！", icdto.getCode(), prototype.getLicense_no()));
			return null;
		}
		
		List<ICModel> models = icModel.getModels();
		ICModel policyModel = null;// 报价车型

		if (models != null && !models.isEmpty()) {

			List<ICModel> temp = new ArrayList<ICModel>();// 用来放置支持的保险车型
			String supportIC = quotations.getIcs().get(0).getCode();// 当前报价的保险公司代码

			for (int i = 0; i < models.size(); i++) {

				ICModel ic = models.get(i);
				if (ic.getSupport_ic_codes().contains(supportIC)) {

					temp.add(ic);
					continue;
				}
			}

			models = temp;// 覆盖只支持的保险车型

			Collections.sort(models, new Comparator<ICModel>() {

				/**
				 * @see java.util.Comparator#compare(java.lang.Object,
				 *      java.lang.Object)
				 */
				@Override
				public int compare(ICModel o1, ICModel o2) {

					Double o1Price = new Double(o1.getPurchase_price());
					Double o2Price = new Double(o2.getPurchase_price());

					return o1Price.compareTo(o2Price);
				}
			});

			
			
			if (models.isEmpty()) {
				params.put("model_type", null);
				params.put("ic_model_id", null);
			} else {

				params.put("model_type", "ic-model");
				params.put("ic_model_id", models.get(0).getId());
				
				policyModel = models.get(0);
			}
			
			for (ICModel m : models) {

				boolean renewal = (boolean) m.getTags().get("renewal");

				if (renewal == true) {
					// 如果存在续保车型，则按续保车报价
					params.put("model_type", "ic-model");
					params.put("ic_model_id", m.getId());
					
					policyModel = m;
					break;
				}

				boolean lowest = (boolean) m.getTags().get("lowest");

				if (lowest == true) {
					// 先走最低价车型
					params.put("model_type", "ic-model");
					params.put("ic_model_id", m.getId());
					
					policyModel = m;
				}
			}
		}
		
		return policyModel;
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#quotations(com.ccby.dto.yobee.QuotationsDTO)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String createYobeeQuotations(QuotationsDTO quotations) throws BusinessException {
		LinkedHashMap map = new LinkedHashMap();
		if (StringUtils.isEmpty(quotations.getProvCode()) || StringUtils.isEmpty(quotations.getCityCode()) || quotations.getVehicle() == null || StringUtils.isEmpty(quotations.getFuelType())
				|| StringUtils.isEmpty(quotations.getVehicleClass()) || StringUtils.isEmpty(quotations.getVehicleNature()) || StringUtils.isEmpty(quotations.getInsurantType())
				|| quotations.getSelection() == null || quotations.getIcs().size() == 0) {
			throw new BusinessException(Constant.MISSING_REQUIRED_PARAMS);
		}

		VehicleDTO prototype = quotations.getVehicle();
		ICDTO icdto = quotations.getIcs().get(0);
		ICModel policyModel = null;

		map.put("prov_code", quotations.getProvCode());
		map.put("city_code", quotations.getCityCode());
		
		if (prototype != null && StringUtils.isNotBlank(quotations.getIcModelId()) && StringUtils.isNotBlank(prototype.getVehicle_id())){
			map.put("model_type", "ic-model");
			map.put("ic_model_id", quotations.getIcModelId());
			
			policyModel = new ICModel();
			policyModel.setText(quotations.getVehicleText());
			policyModel.setId(quotations.getIcModelId());
			
		} else {
			
			// 如果没有传过来模型ID与车辆ID，则使用车牌号或者车辆信息去查询
			policyModel = packageVehicle(quotations, map);
		}
		
		if (policyModel == null){
			return StringUtils.EMPTY;
		}

		VehicleDTO vehicle = new VehicleDTO();

		if (prototype == null || StringUtils.isBlank(prototype.getVehicle_id())) {
			vehicle = prototype;
		} else {

			vehicle.setVehicle_id(prototype.getVehicle_id());
		}

		map.put("vehicle", vehicle);
		map.put("factory_rate", quotations.getFactory_rate());
		map.put("fuel_type", quotations.getFuelType());
		map.put("vehicle_class", quotations.getVehicleClass());
		map.put("vehicle_nature", quotations.getVehicleNature());
		map.put("insurant_type", quotations.getInsurantType());
		map.put("selection", quotations.getSelection());
		map.put("ics", quotations.getIcs());
		
		
		
		if (StringUtils.isNotBlank(quotations.getTaxPaid()) && Boolean.parseBoolean(quotations.getTaxPaid()) == true) {
			Map<String, Object> extend = quotations.getExtend() != null ? quotations.getExtend() : new HashMap<String, Object>();
			extend.put("tax_paid", Boolean.parseBoolean(quotations.getTaxPaid()));
			extend.put("tax_paid_proof", quotations.getTaxPaidProof());
			
			quotations.setExtend(extend);
		}
		
		if (quotations.getExtend() != null && !quotations.getExtend().isEmpty())map.put("extend", quotations.getExtend());// 扩展信息
		if (StringUtils.isNotBlank(quotations.getBizStartDate()))map.put("biz_start_date", quotations.getBizStartDate());// 商业险起保日期
		if (StringUtils.isNotBlank(quotations.getForceStartDate()))map.put("force_start_date", quotations.getForceStartDate());// 交强险起保日期

		try {
			if (StringUtils.isBlank(quotations.getVehicleText()) && policyModel != null){
				LOGER.info(String.format("【金斗云创建报价】报价车型信息：%s", new org.json.JSONObject(policyModel).toString()));
				
				prototype = quotations.getVehicle();
				List<VehicleModelsDTO> modelList = prototype.getModels();
				
				StringBuffer text = new StringBuffer(policyModel.getText());
				Map<String, Object> tags = policyModel.getTags();
				boolean renewal = Boolean.parseBoolean(tags.get("renewal").toString());
				boolean lowest = Boolean.parseBoolean(tags.get("lowest").toString());
				String ics = tags.get("ics").toString().replace("[", "").replace("]", "").replace(",", "／");
				
				if (renewal == true && lowest == true){
					text.append(String.format("（续保且最低，%s）", ics));
					
				} else if (renewal == true && lowest == false){
					text.append(String.format("（续保车型，%s）", ics));
					
				} else if (renewal == false && lowest == true){
					text.append(String.format("（最低价车型，%s）", ics));
					
				} else {
					text.append(String.format("（%s）", ics));
				}
				
				text.append(tags.get("ics"));
				
				if (modelList == null || modelList.isEmpty()){
					
					VehicleModelsDTO m = new VehicleModelsDTO();
					m.setRemark(text.toString());
					prototype.setModels(Collections.singletonList(m));
				} else {
					
					modelList.get(0).setRemark(text.toString());
				}
			}
			
			LOGER.info(String.format("【金斗云创建报价】请求创建报价，报文：%s", com.alibaba.fastjson.JSONObject.toJSONString(map)));
			String reStr = HttpUtils.httpPost(path + "/requests/quotations", map);
			String ditseuqet = JSONObject.fromObject(reStr).getString("request_id");
			
			// TODO 请求创建投保单
			return ditseuqet;
		} catch (Exception e) {
			e.printStackTrace();

			LOGER.error(e.getMessage(), e.getCause());
			LOGER.error(String.format("【金斗云创建报价】保险公司：%s，车牌号：%s，异常信息：%s，调用接口报错", icdto.getCode(), prototype.getLicense_no(), e.getMessage()));
			
			return e.getMessage();
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#vehicles(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public VehicleDTO vehicles(String cityCode, String licenseNo, String licenseType) throws BusinessException {
		try {
			Map<String, Object> map = new TreeMap<String, Object>(new Comparator<String>() {

				/**
				 * @see java.util.Comparator#compare(java.lang.Object,
				 *      java.lang.Object)
				 */
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			map.put("city_code", StringUtils.defaultIfBlank(cityCode, "130100"));
			map.put("license_no", licenseNo);
			map.put("license_type", licenseType);

			String reStr = HttpUtils.httpGet(host, path + "/vehicles", map, 20000);
			
			if (StringUtils.isBlank(reStr)){
				LOGER.error(String.format("[金斗云--车辆查询]城市：%s，车牌号：%s，车辆类型：%s，未查询到车辆信息！", cityCode, licenseNo, licenseType));
				return null;
			}
			
			VehicleDTO vehicleDTO = com.alibaba.fastjson.JSONObject.parseObject(reStr, VehicleDTO.class);
			
			return vehicleDTO;
		} catch (Exception e) {
			LOGER.error(String.format("[金斗云--车辆查询]城市：%s，车牌号：%s，车辆类型：%s出现错误", cityCode, licenseNo, licenseType));
			LOGER.error(e.getMessage(), e.getCause());
			return null;
		}
	}
	
	/**
	 * @see com.ccby.api.yobee.service.YobeeService#getCarByVinAndEngine(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public UserCarDto getCarByVinAndEngine(String cityCode, String vin, String engine) {
		try {
			// 查询数据库中的车辆信息
			UserCarDto car = this.userCarService.getCarInfoByVinAndEngine(vin, engine);
			
			if (car != null){
				return car;
			}
			
			// 如果本地不存在数据，则查询金斗云
			VehicleDTO vehicleDTO = vehiclesByVinAndEngine(cityCode, vin, engine);
			
			if (vehicleDTO == null){
				return null;
			}
			
			UserCar userCar = new UserCarImpl();
			userCar.setVehicleIdentificationNumber(vehicleDTO.getFrame_no());
			userCar.setEngineNumber(vehicleDTO.getEngine_no());
			userCar.setRegistTime(vehicleDTO.getEnroll_date());
			userCar.setPlatesNumber(vehicleDTO.getLicense_no());
			VehicleModelsDTO models = vehicleDTO.getModels().get(0);
			JSONObject obj = JSONObject.fromObject(models);
			userCar.setBrand(obj.getString("brand_name"));
			userCar.setVehicleType(obj.getString("vehicle_class"));
			userCar.setHolderId("******************");
			userCar.setHolderName(vehicleDTO.getOwner());
			userCar.setCarSeat(models.getVehicle_seat());
			userCar.setPayload(Integer.parseInt(StringUtils.defaultString(vehicleDTO.getPo_weight(), "0")));
			
			return ModelTransformUtils.transform(userCar, UserCarDto.class);
			
		} catch (BusinessException e) {
			LOGER.error(e.getMessage(), e.getCause());
			return null;
			
		} catch (Exception e){
			LOGER.error(e.getMessage(), e.getCause());
			return null;
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#vehiclesByVinAndEngine(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public VehicleDTO vehiclesByVinAndEngine(String cityCode, String frame_no, String engine_no) throws BusinessException {
		try {
			Map<String, Object> map = new TreeMap<String, Object>(new Comparator<String>() {

				/**
				 * @see java.util.Comparator#compare(java.lang.Object,
				 *      java.lang.Object)
				 */
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});

			map.put("city_code", cityCode);
			map.put("engine_no", engine_no);
			map.put("frame_no", frame_no);

			String reStr = HttpUtils.httpGet(host, path + "/vehicles/fae", map);
			if(reStr.isEmpty()){
				return null; 
			}
			VehicleDTO vehicleDTO = com.alibaba.fastjson.JSONObject.parseObject(reStr, VehicleDTO.class);
			return vehicleDTO;
		} catch (Exception e) {
			
			LOGER.error(e.getMessage(), e.getCause());
			throw new BusinessException(Constant.FAIL.getCode(), "车辆查询失败，请稍后重试！");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public InsuranceResultsDTO getQuotations(String requestId) throws BusinessException {
		try {
			boolean isDone = false;
			InsuranceResultsDTO insuranceResultsDto = new InsuranceResultsDTO();
			int count = 0;

			while (isDone == false) {
				++count;
				LinkedHashMap map = new LinkedHashMap();
				map.put("request_id", requestId);
				String reStr = HttpUtils.httpGet(host, path + "/requests/quotations/" + requestId, null);
				insuranceResultsDto = com.alibaba.fastjson.JSONObject.parseObject(reStr, InsuranceResultsDTO.class);
				isDone = insuranceResultsDto.getIs_done();

				if (count == 60) {
					// 轮询60次后还没有查到结果，则退出
					LOGER.info(String.format("【金斗云自动报价】轮询报价结果超出60次，退出轮询。报价请求ID：%s", requestId));
					return null;
				}

				Thread.sleep(1000);// 方便观察结果 1秒轮询一次
			}
			LOGER.info(String.format("【金斗云自动报价】报价ID：%s报价完成，报价结果：%s", requestId, JSONUtils.toJsonStr(insuranceResultsDto)));
			return insuranceResultsDto;
		} catch (Exception e) {
			e.printStackTrace();
			LOGER.error("【金斗云自动报价】" + requestId + "-轮询金斗云异常," + e.getMessage());
			return null;
		}
	}

	@Override
	public UserCarDto getVehiclesCar(String cityCode, String plateNumber) {
		try {
			if (!StringUtils.startsWith(StringUtils.defaultString(cityCode, "130100"), "13")){
				// 如果选择了非河北省的市区代码，则使用石家庄代码
				cityCode = "130100";
			}
			
			VehicleDTO vehicleDTO = this.vehicles(StringUtils.defaultIfBlank(cityCode, "130100"), plateNumber, "02");
			
			if (vehicleDTO == null){
				return null;
			}
			
			UserCar userCar = new UserCarImpl();
			userCar.setVehicleIdentificationNumber(vehicleDTO.getFrame_no());
			userCar.setEngineNumber(vehicleDTO.getEngine_no());
			userCar.setRegistTime(vehicleDTO.getEnroll_date());
			// userCar.setIssueTime(IssueTime);

			// userCar.setVehicleProperty(vehicleDTO.getLicense_type());
			userCar.setPlatesNumber(vehicleDTO.getLicense_no());

			// 车辆类型
			// userCar.setVehicleType(vehicleDTO.getLicense_type());
			VehicleModelsDTO models = vehicleDTO.getModels().get(0);
			JSONObject obj = JSONObject.fromObject(models);
			userCar.setBrand(obj.getString("brand_name"));
			
			if (StringUtils.isBlank(userCar.getBrand())){
				userCar.setBrand(obj.getString("vehicle_name"));
			}
			
			userCar.setVehicleType(obj.getString("vehicle_class"));
			userCar.setHolderId("******************");
			userCar.setHolderName(vehicleDTO.getOwner()); 
			userCar.setCarSeat(models.getVehicle_seat());
			userCar.setPayload(Integer.parseInt(StringUtils.defaultString(vehicleDTO.getPo_weight(), "0")));
			UserCarDto car = new UserCarDto(userCar);
			
			return car;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#getICModel(com.ccby.dto.yobee.ICModelsQueryDTO,
	 *      com.ccby.dto.yobee.VehicleDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ICModelResult getICModel(ICModelsQueryDTO query, VehicleDTO vehicle) throws BusinessException {
		Map<String, Object> params = JSONObject.fromObject(query);

		try {
			// 创建续保查询请求
			List<ICAccount> icAccounts = getPolicyAccountByCityCodeAndIcCode("130100", null);
			List<ICDTO> icList = new ArrayList<ICDTO>();

			for (ICAccount icdto : icAccounts) {
				ICDTO ic = new ICDTO();
				ic.setAccount_id(icdto.getAccount_id());
				ic.setCode(icdto.getIc_code());

				icList.add(ic);
			}
			
			VehicleDTO vehicleParam = new VehicleDTO();

			if (StringUtils.isNotBlank(query.getValue())) {
				vehicleParam.setVehicle_id(query.getValue());
			} else {
				// 车牌号为空，使用填写的车辆信息查询
				vehicleParam = vehicle;
				query.setType("frame_no");// 使用车架号
				query.setValue_type(null);
				query.setValue(vehicleParam.getFrame_no());

				params.put("type", "frame_no");
				params.put("value", vehicleParam.getFrame_no());
				params.put("value_type", null);
				params.put("vehicle_id", null);
			}
			
			
			try {
				String renewalsRequest = createRenewalQuery(query.getProv_code(), query.getCity_code(), null, vehicleParam, null);
				params.put("renewal_request_id", renewalsRequest);// 续保ID
			} catch (Exception e) {
				LOGER.error(e.getMessage(), e.getCause());
			}
			
			// 创建车型查询
			params.put("ics", icList);
			String str = HttpUtils.httpPost(path + "/requests/ic-models", params);

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

			return com.alibaba.fastjson.JSONObject.parseObject(result.toString(), ICModelResult.class);
		} catch (Exception e) {
			LOGER.error(e.getMessage(), e.getCause());
		}

		return null;
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#getPolicyAccountByCityCodeAndIcCode(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ICAccount> getPolicyAccountByCityCodeAndIcCode(String cityCode, String icCode) throws BusinessException {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cityCode", StringUtils.defaultString(cityCode, "130100"));

		if (StringUtils.isNotBlank(icCode)) {
			params.put("icCode", icCode);
		}

		List<ICAccount> data = (List<ICAccount>) baseDAO.findData("getPolicyAccount", params);
		
		if (data == null || data.isEmpty()){
			LOGER.info(String.format("[查询报价账户]查询城市：%s没有配置金斗云报价账户，使用石家庄账户查询！", cityCode));
			params.put("cityCode", "130100");
		}
		
		data = (List<ICAccount>) baseDAO.findData("getPolicyAccount", params);
		return data;
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#createRenewalQuery(java.lang.String, java.lang.String, java.lang.String, com.ccby.dto.yobee.VehicleDTO, java.lang.String)
	 */
	@Override
	public String createRenewalQuery(String provCode, String cityCode, String icCode, VehicleDTO vehicle, String policyNo) {
		
		if (vehicle == null){
			LOGER.info("【创建续保查询】车辆信息为空！");
			return null;
		}
		
		Map<String, Object> renewals = new HashMap<String, Object>();
		renewals.put("prov_code", StringUtils.defaultIfBlank(provCode, "130000"));
		renewals.put("city_code", StringUtils.defaultIfBlank(cityCode, "130100"));
		try {
			List<ICAccount> icAccounts = getPolicyAccountByCityCodeAndIcCode(cityCode, icCode);
			List<ICDTO> icList = new ArrayList<ICDTO>();

			for (ICAccount icdto : icAccounts) {
				ICDTO ic = new ICDTO();
				ic.setAccount_id(icdto.getAccount_id());
				ic.setCode(icdto.getIc_code());

				icList.add(ic);
			}

			renewals.put("ics", icList);
			renewals.put("vehicle", vehicle);
			
			if (StringUtils.isNotBlank(policyNo)){
				renewals.put("policy_no", policyNo);
			}

			String str = HttpUtils.httpPost(path + "/requests/renewals", renewals);
			String requestId = JSONObject.fromObject(str).getString("request_id");
			
			return requestId;
		} catch(Exception e){
			LOGER.error(e.getMessage(), e.getCause());
		}
		return null;
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#getRenewal(java.lang.String, java.lang.String, java.lang.String, com.ccby.dto.yobee.VehicleDTO, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Renewal getRenewal(String provCode, String cityCode, String icCode, VehicleDTO vehicle, String policyNo) {
		
		Map<String, Object> renewals = new HashMap<String, Object>();
		renewals.put("prov_code", StringUtils.defaultIfBlank(provCode, "130000"));
		renewals.put("city_code", StringUtils.defaultIfBlank(cityCode, "130100"));
		try {
			// 创建续保查询
			String requestId = createRenewalQuery(provCode, cityCode, icCode, vehicle, policyNo);
			
			if (StringUtils.isBlank(requestId)){
				LOGER.info("【续保查询】创建续保查询时出错，无法查询续保！");
				return null;
			}
			
			JSONObject result = null;
			String str = null;
			
			while (true) {

				// 轮询续保查询结果
				str = HttpUtils.httpGet(host, path + "/requests/renewals/" + requestId, null);
				result = JSONObject.fromObject(str);
				boolean done = result.getBoolean("is_done");

				if (done == true) {
					break;
				}

				Thread.sleep(500);
			}
			
			ObjectMapper objMapper = new ObjectMapper();
			final Renewal renewal = new Renewal();
			
			// 封装基本信息
			
			String renewalStr = result.getString("renewal");
			if (StringUtils.isBlank(renewalStr) || StringUtils.equals(renewalStr, "null")){
				return null;
			}
			
			JSONObject json = JSONObject.fromObject(renewalStr);
			renewal.setIcCode(json.getString("ic_code"));
			renewal.setIcName(json.getJSONObject("ic_info").getString("name"));
			renewal.setChannel(json.getString("channel").replace("null", StringUtils.EMPTY));
			
			// 封装商业险信息
			JSONObject biz = json.getJSONObject("biz_info");
			
			if (!biz.isEmpty()){
				BizInfo bizInfo = objMapper.readValue(biz.toString(), BizInfo.class);
				
				bizInfo.setIcCode(biz.getJSONObject("ic").getString("code"));
				bizInfo.setIcName(biz.getJSONObject("ic").getString("name"));
				renewal.setBizInfo(bizInfo);
			} else {
				
				renewal.setBizInfo(new BizInfo());
			}

			// 封装交强险信息
			JSONObject force = json.getJSONObject("force_info");
			
			if (!force.isEmpty()){
				ForceInfo forceInfo = objMapper.readValue(force.toString(), ForceInfo.class);
				
				forceInfo.setIcCode(force.getJSONObject("ic").getString("code"));
				forceInfo.setIcName(force.getJSONObject("ic").getString("name"));
				renewal.setForceInfo(forceInfo);
			} else {
				
				renewal.setForceInfo(new ForceInfo());
			}
			
			// 封装续保人员信息
			JSONObject renewalPerson = json.getJSONObject("renewal");
			renewalPerson.keySet().forEach((a) -> {
				String key = a.toString();
				
				if (StringUtils.equals(key, "vehicle") || StringUtils.equals(key, "model")){
					return;
				}
				
				JSONObject obj = renewalPerson.getJSONObject(key);
				try {
					Person person = objMapper.readValue(obj.toString(), Person.class);
					
					// 车主信息
					if (StringUtils.equals(key, "owner"))renewal.setOwner(person);
					// 投保人信息
					if (StringUtils.equals(key, "applicant"))renewal.setOwner(person);
					// 被保人信息
					if (StringUtils.equals(key, "insurant"))renewal.setOwner(person);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			// 封装车辆信息
			JSONObject vehicleJSON = renewalPerson.getJSONObject("vehicle");
			VehicleDTO renewalVehicle = objMapper.readValue(vehicleJSON.toString(), VehicleDTO.class);
			
			JSONObject model = vehicleJSON.getJSONObject("model");
			VehicleModelsDTO modelDto = objMapper.readValue(model.toString(), VehicleModelsDTO.class);
			renewalVehicle.setModels(Collections.singletonList(modelDto));
			renewal.setVehicle(renewalVehicle);
			
			return renewal;
		} catch (Exception e) {
			e.printStackTrace();
			LOGER.error(e.getMessage(), e.getCause());
			return null;
		}
	}

	@Override
	public List<ClaimsDTO> claims(String vehicleId,String license_no, String license_type,String frame_no,String engine_no) throws BusinessException {
		try {
			Map<String, Object> map = new TreeMap<String, Object>(new Comparator<String>() {

				/**
				 * @see java.util.Comparator#compare(java.lang.Object,
				 *      java.lang.Object)
				 */
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			if(StringUtils.isEmpty(vehicleId) == true){
				map.put("license_no", license_no);
				map.put("license_type", license_type);
				map.put("frame_no", frame_no);
				map.put("engine_no", engine_no);
			}else{
				map.put("vehicle_id", vehicleId);
			}

			String reStr = HttpUtils.httpGet(host, path + "/claims", map);
			if(StringUtils.isEmpty(reStr) == false){
				JSONObject json = JSONObject.fromObject(reStr);
				List<ClaimsDTO> vehicleDTO = com.alibaba.fastjson.JSONArray.parseArray(json.getString("claims"), ClaimsDTO.class);
				return vehicleDTO;
			}
			return null;
		} catch (Exception e) {
			LOGER.error(e.getMessage(), e.getCause());
			throw new BusinessException(Constant.FAIL.getCode(), "车辆查询失败，请稍后重试！");
		}
	}

	@Override
	public List<ClaimsDTO> quotationsClaims(String quotationId) throws BusinessException {
		try {
			String reStr = HttpUtils.httpGet(host, path + "/quotations/"+quotationId+"/claims", null);
			if(StringUtils.isEmpty(reStr) == false){
				JSONObject json = JSONObject.fromObject(reStr);
				List<ClaimsDTO> vehicleDTO = com.alibaba.fastjson.JSONArray.parseArray(json.getString("claims"), ClaimsDTO.class);
				return vehicleDTO;
			}
			return null;
		} catch (Exception e) {
			LOGER.error(e.getMessage(), e.getCause());
			throw new BusinessException(Constant.FAIL.getCode(), "车辆查询失败，请稍后重试！");
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#updateProposeInfo(java.lang.String, com.ccby.dto.yobee.ProposeDTO)
	 */
	@Override
	public void updateProposeInfo(String notifyId, ProposeDTO propose) throws BusinessException {
		
		if (propose == null){
			LOGER.info(String.format("[更新投保单] 通知ID：%s，投保单信息为空，不做任何操作！", notifyId));
			return;
		}
		
		boolean isFail = false;
		String errorFailMsg = null;
		if (StringUtils.equals(propose.getProposalStatus(), "FAIL") || StringUtils.equals(propose.getProposalStatus(), "UW_FAIL")
				|| StringUtils.equals(propose.getProposalStatus(), "REFUSED")){
			LOGER.info(String.format("[更新投保单] 通知ID：%s，投保单创建出错：%s", notifyId, propose.getComment()));
			isFail = true;
			errorFailMsg = propose.getComment();
		}
		
		try {
			List<ProductAttribute> list = productService.queryAttributesByAttrNameAndStringValue("notifyId", notifyId);
			
			if (list == null || list.isEmpty()){
				LOGER.info(String.format("[更新投保单] 通知ID：%s的产品属性不存在，无法更新投保单信息！", notifyId));
				return;
			}
			
			ProductAttribute attribute = list.get(0);
			
			if (attribute.getProduct() == null){
				LOGER.info(String.format("[更新投保单] 通知ID：%s的产品信息不存在，无法更新投保单信息！", notifyId));
				return;
			}
			
			Product product = new ProductImpl();
			product.setId(attribute.getProduct().getId());
			
			List<ProductAttributeImpl> productAttrList = new ArrayList<ProductAttributeImpl>();
			
			ProductAttributeImpl productAttr = null;
			
			if (isFail == true){
				// 创建投保单失败
				productAttr = new ProductAttributeImpl();
				productAttr.setProduct(product);
				productAttr.setDataType(DataType.STRING);
				productAttr.setAttributeName("compulsoryCertificate");
				productAttrList.add(productAttr);
				productAttr.setStringValue(errorFailMsg);
			}
			
			if (propose.getIcNos() != null && StringUtils.isNotBlank(propose.getIcNos().getForceProp())){
				// 投保了交强险，设置交强险投保单号
				productAttr = new ProductAttributeImpl();
				productAttr.setProduct(product);
				productAttr.setDataType(DataType.STRING);
				productAttr.setAttributeName("compulsoryCertificate");
				productAttrList.add(productAttr);
				
				if (isFail){
					productAttr.setStringValue(errorFailMsg);
				} else {
					productAttr.setStringValue(propose.getIcNos().getForceProp());
				}
			}
			
			if (propose.getIcNos() != null && StringUtils.isNotBlank(propose.getIcNos().getBizProp())){
				// 投保了商业险，设置商业险投保单号
				productAttr = new ProductAttributeImpl();
				productAttr.setProduct(product);
				productAttr.setDataType(DataType.STRING);
				productAttr.setAttributeName("commercialCertificate");
				productAttrList.add(productAttr);
				
				if (isFail){
					productAttr.setStringValue(errorFailMsg);
				} else {
					productAttr.setStringValue(propose.getIcNos().getBizProp());
				}
			}
			
			for (ProductAttributeImpl attr : productAttrList) {
				
				// 先删除已存在的属性，再保存
				productService.deleteProductAttributeByPidAndName(product.getId(), attr.getAttributeName());
				productService.addProductAttribute(attr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new BusinessException(Constant.FAIL.getCode(), "设置报价投保单号失败！");
		}
	}

	/**
	 * @see com.ccby.api.yobee.service.YobeeService#createPropose(com.ccby.api.core.dto.insurance.InsuranceQuoteDTO, java.lang.String, com.ccby.dto.yobee.PrivyDTO, com.ccby.dto.yobee.PrivyDTO, com.ccby.dto.yobee.PrivyDTO)
	 */
	@Override
	public void createPropose(InsuranceQuoteDTO quote, String serialNumber, PrivyDTO insurant, PrivyDTO applicant, PrivyDTO owner) throws BusinessException {
		
		if (quote == null || StringUtils.isBlank(quote.getQuoteId())){
			throw new BusinessException(Constant.FAIL.getCode(), "金斗云报价ID为空，无法创建投保单！");
		}
		
		if (StringUtils.isBlank(serialNumber)){
			throw new BusinessException(Constant.FAIL.getCode(), "订单编号为空，无法创建投保单！");
		}
		
		if (insurant == null || applicant == null || owner == null){
			throw new BusinessException(Constant.FAIL.getCode(), "投保单的被保人，投保人，车主信息为空，无法创建投保单！");
		}
		
		LOGER.info(String.format("[提交投保单] 报价ID：%s，订单号：%s开始提交投保单信息！", quote.getId(), serialNumber));
		Map<String, Object> req = new HashMap<String, Object>();
		req.put("notify_url", domain + "/yobee/rest/notification");// 回调地址
		req.put("quotation_id", quote.getQuoteId());// 金斗云的报价ID
		req.put("out_trade_no", serialNumber);// 订单号
		req.put("insurant", insurant);// 被保人信息
		req.put("applicant", applicant);// 投保人信息
		req.put("owner", owner);// 车主信息
		req.put("is_uw", false);// 是否自动核保，默认false
		req.put("biz_start_date", quote.getCommercialDateBegin());// 商业险起保日期, 格式为: YYYY-mm-dd
		req.put("force_start_date", quote.getCompulsoryDateBegin());// 交强险起保日期, 格式为: YYYY-mm-dd
		if (StringUtils.isNotBlank(quote.getExtend())){
			req.put("extend", JSONObject.fromObject(quote.getExtend()));// 扩展信息（"is_transfer": false, 是否过户车；"transfer_date": null,过户日期，格式 YYYY-mm-dd；"tax_paid": true, 是否已完税；"tax_paid_proof": "4008100111", 完税证明）
		}
		
		try {
			String response = HttpUtils.httpPost(path + "/proposals", req);
			JSONObject result = JSONObject.fromObject(response);
			String notifyId = result.getString("notification_id");// 通知ID
			String proposalId = result.getString("proposal_id");// 投保单号
			
			// 添加投保单属性
			addProductAttribute(quote.getId(), proposalId, notifyId);
			LOGER.info(String.format("[提交投保单] 报价ID：%s的投保单属性：%s添加完成", quote.getId(), proposalId, notifyId));
			
		} catch (Exception e) {
			
			try {
				JSONObject obj = JSONObject.fromObject(e.getMessage());
				if (!obj.containsKey("proposal_id")){
					e.printStackTrace();
					return;
				}
				
				// 添加投保单属性
				addProductAttribute(quote.getId(), obj.getString("proposal_id"), obj.getString("notification_id"));
				
				LOGER.info(String.format("[提交投保单] 报价ID：%s的投保单属性：%s添加完成", quote.getId(), obj.getString("proposal_id"), obj.getString("notification_id")));
				return;
			} catch (Exception e2) {
			}
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 给报价单添加投保单属性信息
	 * 
	 * @param pid 产品ID（报价单ID）
	 * @param proposalId 投保单号
	 * @param notifyId 通知ID
	 */
	private void addProductAttribute(long pid, String proposalId, String notifyId){
		
		if (StringUtils.isBlank(proposalId) || StringUtils.isBlank(notifyId)){
			LOGER.info(String.format("[添加投保单属性] 报价ID：%s，投保单号与通知ID为空，不添加属性信息！", pid));
			return;
		}
		
		Product product = new ProductImpl();
		product.setId(pid);
		
		List<ProductAttributeImpl> productAttrList = new ArrayList<ProductAttributeImpl>();
		
		ProductAttributeImpl productAttr = new ProductAttributeImpl();
		productAttr.setProduct(product);
		productAttr.setDataType(DataType.STRING);
		productAttr.setAttributeName("notifyId");
		productAttr.setStringValue(notifyId);
		productAttrList.add(productAttr);
		
		productAttr = new ProductAttributeImpl();
		productAttr.setProduct(product);
		productAttr.setDataType(DataType.STRING);
		productAttr.setAttributeName("proposalId");
		productAttr.setStringValue(proposalId);
		productAttrList.add(productAttr);
		
		for (ProductAttributeImpl attr : productAttrList) {
			
			// 先删除已存在的属性，再保存
			try {
				productService.deleteProductAttributeByPidAndName(pid, attr.getAttributeName());
				productService.addProductAttribute(attr);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}
}
