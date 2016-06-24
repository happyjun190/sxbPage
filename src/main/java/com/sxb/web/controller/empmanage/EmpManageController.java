package com.sxb.web.controller.empmanage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sxb.commons.constants.ReturnCode;
import com.sxb.commons.json.JsonResult;
import com.sxb.service.empmanage.IEmpManageService;

@RestController
@RequestMapping("/servlet/empmanage")
public class EmpManageController {
	private static final Logger logger = LoggerFactory.getLogger(EmpManageController.class);
	
	@Autowired
	private IEmpManageService empmanageService;
	
	
	@RequestMapping(value = "/getUserKqInfoList", method = RequestMethod.POST)
	public JsonResult getUserKqInfoList(HttpServletRequest request, @RequestBody Map<String, Object> map) {
		try {
			logger.info("=====================get user kq info success===========");
			return empmanageService.getUserKqInfoList(map);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return new JsonResult(ReturnCode.EXCEPTION, "获取员工考勤记录失败！", null);
		}
	}

}
