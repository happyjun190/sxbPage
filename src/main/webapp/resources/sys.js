/**
 * 定义一些全局参数：
 */
var loginUrl = getRootPath() + "/login.html";
var loginErrorCode = "40020";
var successReturnCode = "40001";

/**
 * 获取API URL的根，比如http://api.ysbang.cn/ysb
 */
function getApiRoot(){
	return getRootPath();
}


/**
 * 发送ajax POST请求，请求参数为JSON String格式
 * 
 * 一般只需要前面三个参数，如：
 * ajaxRequest("/servlet/getUserInfo", {id : "abc"}, function callback(jsonResult){
 * 		//doSomething...
 * });
 * 
 * @Param: url绝对路径或相对路径，比如http://locolhost:8080/ysb/servlet/login 或 /servlet/login
 * @Param: params：参数（JSON String类型、或对象类型都可以），自动添加authcode
 * @Param: successCallback：成功的回调处理函数
 * @Param: errorCallback：失败的回调处理函数
 * @Param: async：是否异步请求true/false
 */
function ajaxRequest(url, params, successCallback, errorCallback, async) {
	
	var _params = (typeof params == "string") ? JSON.parse(params) : params;
	if(isEmpty(_params.authcode)){
		_params.authcode = "123456";
	}
	_params=JSON.stringify(_params);
	
	var _url = (url.indexOf("http") == 0) ? url : getApiRoot() + url;
	var _async = (async == undefined) ? true : async;
	
	$.ajax({
		method : "POST",
		url : _url,
		async : _async,
		contentType : "application/json;charset=utf-8", // 必须有
		//dataType : "json", // 表示返回值类型
		data : _params,
		success : successCallback,
		error : errorCallback
	});
}

/**
 * 同上，只是参数改为form data格式
 */
function ajaxRequestForm(url, params, successCallback, errorCallback, async) {
	
	var _params = (typeof params == "string") ? JSON.parse(params) : params;
	_params.authcode = "123456";
	
	var _url = (url.indexOf("http") == 0) ? url : getApiRoot() + url;
	var _async = (async == undefined) ? true : async;
	
	$.ajax({
		type : "POST",
		url : _url,
		async : _async,
		dataType : "json", //表示返回值类型
		data : _params,
		success :  function _successCallback(data) {
			if(data.code == loginErrorCode){
				if(confirm(data.message)){
					location.href = loginUrl;
					return;
				}
			}
			
			if(data.code && data.code != successReturnCode){
				alert(data.message);
				return;
			}
			
			if(successCallback){
				successCallback(data);
			}
		},
		error : errorCallback
	});
}


/**
 * 获取项目根路径，如： http://localhost:8083/ysb
 */
function getRootPath() {
	//获取当前网址，如： http://localhost:8083/ysb/web/index.html
	var curWwwPath = window.document.location.href;
	//获取主机地址之后的目录，如：ysb/web/index.html
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	//获取主机地址，如： http://localhost:8083
	var localhostPath = curWwwPath.substring(0, pos);
	//获取带"/"的项目名，如：/ysb
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return (localhostPath + projectName);
}

/**
 * 获取URL中参数的值
 * 
 * 例子：http://abc.com?action=update&id=987654321789
 * var action = getUrlParam("action"); //返回action的值为"update"
 * 
 * @Author: 许继俊
 * 
 * @Param: name: 要获取的参数名字
 * @param: _location：可选参数，页面的URL，在弹出窗口中使用
 * @return：返回参数的值
 */
function getUrlParam(name, _location){
	var _location_url =_location || window.location.search; //window.location.search：URL中问号及其后面的内容
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = decodeURI(_location_url.substr(1).match(reg)); //匹配目标参数
	
	return (r == null) ? null : unescape(r[2]);
}

/**
 * 判断字符串是否为空
 */
function isEmpty(str) {
	return ""==str||"undefined"==typeof(str)||null==str;
}

/**
 * 把html保留符号（<、>、'、"）替换为html编码
 * @param input
 * @returns {String}
 */
function replaceHtml(input){
	if(input==null){
		return "";
	}
	var result = ""; 
	var reg1 = new RegExp("<","gi");
	var reg2 = new RegExp(">","gi");
	var reg3 = new RegExp("'","gi");
	var reg4 = new RegExp("\"","gi");
	result = input.replace(reg1,"&lt;");
	result = result.replace(reg2, "&gt;");
	result = result.replace(reg3, "&#39;");
	result = result.replace(reg4, "&#34;");
	return result;
}

/**
 * 把form中的所有name和value拼装在一个对象里面。可以用来自动拼装Ajax调用的参数。
 * 用法：params = $("form").serializeObject();
 */
$.fn.serializeObject = function() {
	/**
	 * 原理：serializeArray()把form转换成一个数组：
	 * [ { name: "myName",
	 *     value: 6
	 *   }
	 *   ...
	 * ]
	 * 
	 * Array.prototype.reduce()再循环上面这个数组，比如把第一个元素变成：{myName: 6}
	 * reduce的最后那个参数{}作为回调函数的第一个参数（a）的初始值。
	 * 参考：https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/Reduce
	 */
	return this.serializeArray().reduce(function(a, x) { a[x.name] = x.value; return a; }, {});
}

