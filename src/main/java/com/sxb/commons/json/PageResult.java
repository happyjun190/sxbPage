package com.sxb.commons.json;

import java.util.List;

import com.sxb.commons.mybatis.interceptor.Page;

public class PageResult<T> {
	
	private int totalRecord;//总记录数
    //private int totalPage;//总页数
    private int pageNo = 1;//页码，默认是第一页
    private int pageSize = 5;//每页显示的记录数，默认是5
    
    public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	private List<T> results;//对应的当前页记录 
	
	public PageResult() {
	}
    
    public PageResult(Page<T> page){
    	//this.totalPage = page.getTotalPage();
    	this.totalRecord = page.getTotalRecord();
    	this.pageNo = page.getPageNo();
    	this.pageSize = page.getPageSize();
    	this.results = page.getResults();
    }

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getTotalPage() {
		return (this.totalRecord-1)/this.pageSize+1;
		//return totalPage;
	}

	/*public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}*/

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}
}
