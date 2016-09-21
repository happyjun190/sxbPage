package com.sxb.commons.solranalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxb.commons.alipay.util.StringUtil;
import com.sxb.commons.utils.DateUtils;

public class SolrWholesaleListAnalysis {
	
	private static Logger logger = LoggerFactory.getLogger(SolrWholesaleListAnalysis.class);
	private static final ObjectMapper jsonMapper = new ObjectMapper();
	private static Workbook workbook = null;
	private static DataFormatter formatter = null;
	private static FormulaEvaluator evaluator = null;
	private static String solrApi = "http://test.ysbang.cn/ysb/servlet/yaomaimai/caigou/v3/getWholesaleListV3/v3100";
	
	public static void main(String[] args) {
		
		
		String filePath = "/home/shenjun/Desktop/7月热词-搜索联想调整结果.xlsx";
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("content-type", "application/json");
		//headerMap.put("Accept", "application/json");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//paramMap.put("authcode", "123456");
		paramMap.put("format", "json");
		//paramMap.put("usertoken", "b380586bb4fe411e8bcca5279484d86d");
		paramMap.put("operationtype", 1);
		paramMap.put("page", 1);
		paramMap.put("pageNo", 1);
		paramMap.put("pagesize", 10);
		paramMap.put("usertoken", "440d1af19d89407288c2db1cf4c15331");
    	paramMap.put("authcode", 123456);

//		
//		HttpHelper httpHelper = new HttpHelper();
//		httpHelper.setHeaderMap(headerMap);
		
		openWorkbook(filePath);
		
		

		String[] cells = null;
		Sheet sheet = null;
		boolean first = true;
		
		//获取第一个sheet页数据
		sheet = workbook.getSheetAt(0);
		
		//解析行数据
		if (sheet.getPhysicalNumberOfRows() > 0) {
			for (int i = 0; i <= sheet.getLastRowNum(); i++) {
				//将每一行解析成单元格数组
				cells = acceptRow(sheet.getRow(i));
				
				//2.1、判断是否是空
				if (cells == null) {
					continue;
				} else {
					if(cells.length>1) {
						if(StringUtil.isNotBlank(cells[1])) {
//							paramMap.put("searchkey", cells[1]);
//							httpHelper.setUri(solrApi);
//							httpHelper.setMapParam(paramMap);
//							CoreFuncReturn returnData = httpHelper.sendPost();
//							
//							logger.info(returnData.msg);
							paramMap.put("searchkey", cells[1]);
							List<Map<String, Object>> solrData = getSolrWholesaleList(solrApi,paramMap);
							
							
							for(Map<String, Object> wholesaleMap : solrData) {
								System.out.print(cells[1]+",");
		                		for(String mapKey:wholesaleMap.keySet()) {
		                			if(wholesaleMap.get(mapKey)!=null&&!"joinCarMap".equals(mapKey)) {
		                				System.out.print(wholesaleMap.get(mapKey).toString()+",");
		                				//logger.info("mapKey:{},=====mapValue:{} ", mapKey,wholesaleMap.get(mapKey).toString());
		                			} else {
		                				System.out.print("暂无数据"+",");
		                			}
		                		}
		                		System.out.println();
		                	}
		                	
//		                	logger.info("HTTP请求数据成功,服务器正常返回,code:"+code);
							
							
						}
					}
				}
				
				//2.2、判断是否是首行(文件开始的无效行数据,不一定是首行)
				if (first) {// 文件头
					first = false;
					continue;
				}
				
			}
		}
		
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getSolrWholesaleList(String url,
			Map<String, Object> paramMap) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost httppost = new HttpPost(url);
        List<Map<String, Object>> strResult = new ArrayList<Map<String, Object>>();
        try {
        	
        	// 设置大众版USERTOKEN到redis中以通过拦截器
        	/*if(StringUtil.isEmpty( redisOperator.get(RedisConstants.USERTOKENHEAD_PRO + FAKE_TOKEN_PUB) ) ) {
        		redisOperator.set(RedisConstants.USERTOKENHEAD_PRO + FAKE_TOKEN_PUB, "0");
        	}*/
        	
            String paramToString = jsonMapper.writeValueAsString(paramMap);
            StringEntity entity = new StringEntity(paramToString,HTTP.UTF_8);
            entity.setContentType("application/json");
            //entity.setContentEncoding("ISO-8859-1");//ISO-8859-1
            
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == 200) {
                /*读返回数据*/
                String conResult = EntityUtils.toString(response.getEntity());
                Map<String, Object> resultMap = (Map<String, Object>)jsonMapper.readValue(conResult, Map.class);
                String code = (String) resultMap.get("code");
                if("40001".equals(code)) {
                	Map<String, Object> dataMap = (Map<String, Object>)resultMap.get("data");
                	strResult = (ArrayList<Map<String, Object>>) dataMap.get("wholesales");
                } else {
                	//40020 userToken 过期
                	logger.info("HTTP请求数据失败,code:"+code);
                }
            } else {
            	//String err = response.getStatusLine().getStatusCode()+"";
                logger.info("HTTP请求数据失败,HTTP状态码:"+response.getStatusLine().getStatusCode());
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResult;
    }
	

	
	/**
	 * 使用workbook解析文件
	 * @param filePart
	 */
	private static void openWorkbook(String fileName) {
		File file = new File(fileName);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			workbook = WorkbookFactory.create(fis);
			evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			formatter = new DataFormatter(true);
		} catch (IOException | EncryptedDocumentException | InvalidFormatException  e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理行数据
	 * @param row
	 * @return
	 */
	private static String[] acceptRow(Row row) {
		if (row == null) {
			return null;
		}
		int lastCellNum = row.getLastCellNum();
		String[] cells = readCells(row, lastCellNum);
		if (cells == null || cells.length == 0) {
			return null;
		}
		for (String s : cells) {
			if (!StringUtil.isEmpty(s)) {
				return cells;
			}
		}
		return null;
	}
	
	
	/**
	 * 读取单元格数据
	 * @param row
	 * @param lastCellNum
	 * @return
	 */
	private static String[] readCells(Row row, int lastCellNum) {
		Cell cell = null;
		// int lastCellNum = 0;

		List<String> cells = new ArrayList<String>();
		// lastCellNum = row.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			cell = row.getCell(i);
			if (cell == null) {
				cells.add("");
				continue;
			}
			 if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
				 if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						Date date=HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
						String d=DateUtils.getStringTime(date, DateUtils.DATE_SMALL_STR);
						cells.add(d);
					} else {
						String temp = formatter.formatCellValue(cell);
						cells.add(temp == null ? "" : temp.trim());
					}
				} else {
					cells.add(formatter.formatCellValue(cell));
				}		
			} else {
				cells.add(formatter.formatCellValue(cell, evaluator));
			}
		}
		return cells.toArray(new String[0]);
	}
	
	
	
}