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
	//private static String solrApi = "http://test.ysbang.cn/ysb/servlet/yaomaimai/caigou/v3/getWholesaleListV3/v3100";
	private static String solrApi = "http://test.ysbang.cn/ysb/servlet/yaomaimai/caigou/v3/getWholesaleListV3/v3100";
	
	public static void main(String[] args) {
		String filePath = "/home/shenjun/Desktop/7月热词-搜索联想调整结果.xlsx";
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("content-type", "application/json");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("format", "json");
		paramMap.put("operationtype", 1);
		paramMap.put("page", 1);
		paramMap.put("pageNo", 1);
		paramMap.put("pagesize", 30);
		paramMap.put("usertoken", "5e4cccd0086a4c96ab815e74c59f09f5");
    	paramMap.put("authcode", 123456);
		
		openWorkbook(filePath);

		String[] cells = null;
		Sheet sheet = null;
		boolean first = true;
		
		//获取第一个sheet页数据
		sheet = workbook.getSheetAt(0);
		
		System.out.println("搜索关键字,活动id,药品id,供应商,药品名,规格,单价(unit_price),平均价格(druginfo_price),用户浏览量(uv),页面访问量(pv),月销量(近期),促销类型,爆款类型,库存数量,库存状态");
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
						if(StringUtil.isNotBlank(cells[0])) {
							paramMap.put("searchkey", cells[0]);
							List<Map<String, Object>> solrData = getSolrWholesaleList(solrApi,paramMap);
							printDruginfo(solrData, cells[0]);
						}
						if(StringUtil.isNotBlank(cells[1])) {
							paramMap.put("searchkey", cells[1]);
							List<Map<String, Object>> solrData = getSolrWholesaleList(solrApi,paramMap);
							printDruginfo(solrData, cells[1]);
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
	private static void printDruginfo(List<Map<String, Object>> solrData, String searchKey) {
		
		int id = 0;//活动id
		int drugId = 0;//药品id
		String abbreviation = null;//供应商
		String drugname = null;//药品名称
		//String manufacturer = null;//厂商
		//int joinstatus = 0;//加入购物车状态  0未加入  1已加入
		//String control_info= null;//控销语
		//int is_control = 0;//0非控销 1控销
		String specification = null;//规格
		/*String minprice = null;//最低价
		String maxprice = null;//最高价*/		
		String unitPice = null;//unit_price
		String druginfo_price = null;//平均价格
		int uv = 0;//用户浏览量
		int pv = 0;//页面访问量
		int order_amount = 0;//订单量
		String dis_type = "0";//促销类型(0 无促销  1满额促销 2限量促销 3赠品)
		int providerType = 0;//0 爆款 1非爆款
		int stockAvailable= 0;//库存数量 joinCarMap.stockAvailable
		String stockStatus = null;//库存状态 joinCarMap.stockStatus
		Map<String, Object> joinCarMap = null;
		
		for(Map<String, Object> wholesaleMap : solrData) {
			System.out.print(searchKey+",");
    		/*for(String mapKey:wholesaleMap.keySet()) {
    			if(wholesaleMap.get(mapKey)!=null&&!"joinCarMap".equals(mapKey)) {
    				System.out.print(wholesaleMap.get(mapKey).toString()+",");
    				//logger.info("mapKey:{},=====mapValue:{} ", mapKey,wholesaleMap.get(mapKey).toString());
    			} else {
    				System.out.print("暂无数据"+",");
    			}
    		}*/
			id = wholesaleMap.get("wholesaleid")!=null?Integer.parseInt(wholesaleMap.get("wholesaleid").toString()):0;
			drugId = wholesaleMap.get("drug_id")!=null?Integer.parseInt(wholesaleMap.get("drug_id").toString()):0;
			abbreviation = wholesaleMap.get("abbreviation")!=null?wholesaleMap.get("abbreviation").toString():"~";
			drugname = wholesaleMap.get("drugname")!=null?wholesaleMap.get("drugname").toString():"~";
			//manufacturer = wholesaleMap.get("manufacturer")!=null?wholesaleMap.get("manufacturer").toString():"~";
			//joinstatus = wholesaleMap.get("joinstatus")!=null?Integer.parseInt(wholesaleMap.get("joinstatus").toString()):0;
			//control_info = wholesaleMap.get("control_info")!=null?wholesaleMap.get("control_info").toString():"~";
			//is_control = wholesaleMap.get("is_control")!=null?Integer.parseInt(wholesaleMap.get("is_control").toString()):0;
			specification = wholesaleMap.get("specification")!=null?wholesaleMap.get("specification").toString():"~";
			unitPice = wholesaleMap.get("unit_price")!=null?String.valueOf(wholesaleMap.get("unit_price")):"~";
			/*minprice = wholesaleMap.get("minprice")!=null?String.valueOf(wholesaleMap.get("minprice")):"~";
			maxprice = wholesaleMap.get("maxprice")!=null?String.valueOf(wholesaleMap.get("maxprice")):"~";*/
			druginfo_price = wholesaleMap.get("druginfo_price")!=null?String.valueOf(wholesaleMap.get("druginfo_price")):"~";
			uv = wholesaleMap.get("uv")!=null?Integer.parseInt(wholesaleMap.get("uv").toString()):0;
			pv = wholesaleMap.get("pv")!=null?Integer.parseInt(wholesaleMap.get("pv").toString()):0;
			order_amount = wholesaleMap.get("month_ws")!=null?Integer.parseInt(wholesaleMap.get("month_ws").toString()):0;
			//dis_type = (String) wholesaleMap.getOrDefault("dis_type","0");
			dis_type = wholesaleMap.get("dis_type")!=null?wholesaleMap.get("dis_type").toString():"0";
			providerType = wholesaleMap.get("providerType")!=null?Integer.parseInt(wholesaleMap.get("providerType").toString()):0;
			
			joinCarMap = wholesaleMap.get("joinCarMap")!=null?(Map<String, Object>) wholesaleMap.get("joinCarMap"):new HashMap<String, Object>();
			stockStatus = joinCarMap.get("stockStatus")!=null?String.valueOf(joinCarMap.get("stockStatus")):"~";
			stockAvailable = joinCarMap.get("stockAvailable")!=null?Integer.parseInt(joinCarMap.get("stockAvailable").toString()):0;
			
			System.out.print(id+",");
			System.out.print(drugId+",");
			System.out.print(abbreviation+",");
			System.out.print(drugname+",");
			/*System.out.print(manufacturer+",");
			//加入购物车状态  0未加入  1已加入
			if(joinstatus==0) {
				System.out.print("未加入,");
			} else {
				System.out.print("已加入,");
			}
			System.out.print(control_info+",");
			if(is_control==0) {
				System.out.print("非控销,");
			} else {
				System.out.print("控销,");
			}*/
			System.out.print(specification+",");
			/*System.out.print(minprice+",");
			System.out.print(maxprice+",");*/
			System.out.print(unitPice+",");
			System.out.print(druginfo_price+",");
			System.out.print(uv+",");
			System.out.print(pv+",");
			System.out.print(order_amount+",");
			//促销类型(0 无促销  1满额促销 2限量促销 3赠品)
			if("0".equals(dis_type)) {
				System.out.print("无促销,");
			} else {
				System.out.print("有促销,");
			}
			
			//0 爆款 1非爆款
			if(providerType==0) {
				System.out.print("爆款,");
			} else {
				System.out.print("非爆款,");
			}
			System.out.print(stockAvailable+",");
			System.out.print(stockStatus+",");
    		System.out.println();
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
