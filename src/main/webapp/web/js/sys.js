/**
 * 函数封装到sys对象中，避免全局变量污染
 * 注意：需要在引用这个文件之前引用jQuery库文件
 */
(function (_win, $) {
    "use strict";

    var projectName = "/sxb";
    var loginUrl = "login.html?t=" + Math.random();
    var htmlVersion = "1.0.0";			//版本号，每次升级后要更改
    var myVersion = "pc" + htmlVersion;

    var returnCode = {
        loginError: "40020",
        success: "40001"
    };

    /**
     * 获取项目根路径，如： http://localhost:8083
     */
    var getHost = function () {
        //获取当前网址，如： http://localhost:8083/sxb/web/index.html
        var curWwwPath = window.document.location.href;
        //获取主机地址之后的目录，如：sxb/web/index.html
        var pathName = window.document.location.pathname;
        var pos = curWwwPath.indexOf(pathName);
        //获取主机地址，如： http://localhost:8083
        return curWwwPath.substring(0, pos);
    };

    /**
     * 根据name获取cookie的值
     */
    var getCookie = function (name) {
        var value = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // 判断这个key是否等于name
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    value = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return value;
    };

    var _fn = (function () {
        /**
         * 把form中的所有name和value拼装在一个对象里面。可以用来自动拼装Ajax调用的参数。
         * 用法：params = $("form").serializeObject();
         */
        $.fn.serializeObject = function () {
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
            //return this.serializeArray().reduce(function(a, x) { a[x.name] = x.value; return a; }, {});

            //IE8不支持reduce()！！，只能改成下面这种了：
            var obj = new Object();
            $.each(this.serializeArray(), function (index, param) {
                if (!(param.name in obj)) {
                    obj[param.name] = param.value;
                }
            });

            return obj;
        };
    })();

    _win["sys"] = {
        /**
         * 判断是否连锁总部
         * 2015年12月17日
         */
        isChainHead: function () {
            return (getCookie("IsChainHead") === "true") ? true : false;
        },

        /**
         * 从cookie中获取token
         * 2016年1月7日
         */
        getTokenFromCookie: function () {
            return getCookie("Token");
        },

        /**
         * 获取API URL的根，比如http://api.sxbang.cn/sxb
         */
        getApiRoot: function () {
            return getHost() + projectName;
        },

        /**
         * 发送ajax请求，为了便于扩展，使用option对象作为入参
         * 用法：var options={url:"/servlet/abc", params: {}, callback: function myCallback(){}, ...}; sys.ajax(options);
         */
        ajax: function (options) {
            var _defaultOpts = {//定义缺省的参数和回调函数
                debug: false,
                method: "POST",
                url: "",
                async: true,
                params: {authcode: "123456", platform: "pc", version: myVersion},
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                cache: false,
                timeout: 350000,
                callback: function () {
                },
                errorCallback: function (result) {
                    if (result) {
                        alert(result.message);
                    }
                },
                completeCallback: function () {
                },
                //sysError: function() {},
                showLoadIcon: false
            };

            var _p = $.extend(_defaultOpts.params, options.params); //提前获取参数，以免被下一句覆盖掉
            var opts = $.extend(_defaultOpts, options);

            var _url = (opts.url.indexOf("http") == 0) ? opts.url : sys.getApiRoot() + opts.url;

            _p = JSON.stringify(_p);

            $.ajax({
                method: opts.method,
                url: _url,
                async: opts.async,
                contentType: opts.contentType,
                dataType: opts.dataType, // 表示返回值类型
                data: _p,
                success: function _successCallback(result) {
                    if (result.code == returnCode.loginError) {
                        //if(confirm(result.message)){
                        top.location.href = loginUrl;
                        return;
                        //}
                    }

                    if (result.code == returnCode.success) {
                        opts.callback(result);
                    } else {
                        opts.errorCallback(result);
                    }
                },
                error: function _error(jqXHR, status, errorThrown) {
                    if (opts.debug === true) {
                        alert("发生了错误：" + JSON.stringify(status) + "，" + JSON.stringify(errorThrown));
                    }
                    opts.errorCallback();
                },
                complete: function _completeCallback() {
                    opts.completeCallback();
                }
            });
        },

        /**
         * 获取URL中参数的值
         *
         * 例子：http://abc.com?action=update&id=987654321789
         * var action = getUrlParam("action"); //返回action的值为"update"
         *
         * @Param: name: 要获取的参数名字
         * @param: _location：可选参数，页面的URL，在弹出窗口中使用
         * @return：返回参数的值
         */
        getUrlParam: function (name, _location) {
            var _location_url = _location || window.location.search; //window.location.search：URL中问号及其后面的内容
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = decodeURI(_location_url.substr(1)).match(reg); //匹配目标参数

            return (r == null) ? null : unescape(r[2]);
        },
        /**
         * F5只刷新子iframe
         * @param iframeId
         * @param url
         */
        refresh: function (iframeId, url) {
            $(document).keydown(function (e) {
                var ev = window.event || e;
                var code = ev.keyCode || ev.which;
                if (code == 116) {
                    if (url != null && url != "" && typeof url != "undefined") {
                        window.parent.document.getElementById(iframeId).src = url;
                    } else {
                        window.parent.document.getElementById(iframeId).src = window.parent.document.getElementById(iframeId).src;
                    }
                    if (ev.preventDefault) {
                        ev.preventDefault();
                    } else {
                        ev.keyCode = 0;
                        ev.returnValue = false;
                    }
                }
                //监听F5键
                /* if(e.keyCode==116){
                 if(url!=null&&url!=""&& typeof url != "undefined"){
                 window.parent.document.getElementById(iframeId).src = url;
                 }else{
                 window.parent.document.getElementById(iframeId).src = window.parent.document.getElementById(iframeId).src;
                 }
                 return false;
                 }*/
            });
        },
        /**
         * 发送ajax表单请求，表单中包括file标签。url和formData是必填参数，其他参数（callback, progressCallback, etc）放在option对象中。
         *
         * 用法：
         * var formData = new FormData($('form')[0]);
         * var options={callback: function myCallback(){}, ...}; sys.ajax("/servlet/auth/info", formData, options);
         */
        ajaxFile: function (url, formData, options) {
            var _defaultOpts = { //定义缺省的参数和回调函数
                method: "POST",
                //data: {authcode: "123456", platform: "web", version: "appWeb1.0"},
                callback: function () {
                },
                progressCallback: function () {
                }, //进度
                errorCallback: function (result) {
                },
                completeCallback: function () {
                },
            };
            //var _p = $.extend(options.data,_defaultOpts.data); //提前获取参数，以免被下一句覆盖掉
            var opts = $.extend(_defaultOpts, options);

            var _url = (url.indexOf("http") == 0) ? url : sys.getApiRoot() + url;

            formData.append("authcode", "123456");
            formData.append("platform", "web");
            formData.append("version", "web1.0");
            //获取usertoken：
            $.ajax({
                method: opts.method,
                url: _url,
                data: formData,
                timeout: 1800000, //30分钟
                xhr: function () {
                    var myXhr = $.ajaxSettings.xhr();
                    if (myXhr.upload) { // check if upload property exists
                        // for handling the progress of the upload
                        myXhr.upload.addEventListener('progress', opts.progressCallback, false);
                    }
                    return myXhr;
                },
                success: function _successCallback(result) {
                    if (result.code == returnCode.loginError) {
                        if (confirm(result.message)) {
                            top.location.href = getHost() + loginUrl;
                            return;
                        }
                    }

                    if (result.code == returnCode.success) {
                        opts.callback(result);
                    } else {
                        alert(result.message);
                        opts.errorCallback(result);
                    }
                },
                error: function _error(jqXHR, status, errorThrown) {
                    alert("发生了错误：" + status + "，" + errorThrown);
                    opts.errorCallback();
                },
                complete: function _completeCallback() {
                    opts.completeCallback();
                },
                contentType: false,
                processData: false
            });
        },

        /**
         * 往url后面追加一个参数（key=value）
         */
        appendParamToUrl: function (url, key, value) {
            var _url = url;

            if (_url.indexOf("?") != -1) {
                _url += "&" + key + "=" + value;
            } else {
                _url += "?" + key + "=" + value;
            }

            return _url;
        },

        /**
         * 跳转页面,改变index.html中的iframe[pageContent]的src
         * @param url
         */
        gotoHtml: function (url) {
            if (url == null || url == "" || url == "undefined" || url == undefined) {
                return;
            }
            var _url = sys.appendParamToUrl(url, "v", htmlVersion);
            /*if(url.indexOf("?") != -1){
             url += "&v=" + htmlVersion;
             }else{
             url += "?v=" + htmlVersion;
             }*/
            top.document.getElementById("pageContent").src = _url;
        },

        /**
         * 把html保留符号（<、>、'、"）替换为html编码
         * @param input
         * @returns {String}
         */
        replaceHtml: function (input) {
            if (input == null) {
                return "";
            }
            var result = "";
            var reg1 = new RegExp("<", "gi");
            var reg2 = new RegExp(">", "gi");
            var reg3 = new RegExp("'", "gi");
            var reg4 = new RegExp("\"", "gi");
            result = input.replace(reg1, "&lt;");
            result = result.replace(reg2, "&gt;");
            result = result.replace(reg3, "&#39;");
            result = result.replace(reg4, "&#34;");
            return result;
        },

        getWindowWidth: function () {
            var windowWidth;
            if (document.documentElement.scrollWidth < document.documentElement.clientWidth + document.documentElement.scrollLeft) {
                windowWidth = document.documentElement.clientWidth + document.documentElement.scrollLeft;
            } else {
                windowWidth = document.documentElement.scrollWidth;
            }
            return windowWidth;
        },

        getWindowHeight: function () {
            var windowHeight;
            if (document.documentElement.scrollHeight < document.documentElement.clientHeight + document.documentElement.scrollTop) {
                windowHeight = document.documentElement.clientHeight + document.documentElement.scrollTop;
            } else {
                windowHeight = document.documentElement.scrollHeight;
            }
            return windowHeight;
        },

        /**
         * 保存userinfo到localStorage
         * @param userInfo
         * "{
		 * "userName":"李连政",
		 * "nickName":"药师83782",
		 * "phone":"13143683782",
		 * "gender":"男",
		 * "drugStore":"昌港药店",
		 * "point":"80",
		 * "monney":3.1,
		 * "job":"店员",
		 * "coupon":null,
		 * "headUrl":"http://test.sxbang.cn//data/img/pharmacy/user/2015/08/08/3728e658-6126-4a4c-b5ff-207739158429.jpg",
		 * "couponCount":0,
		 * "address":{"storetitle":"昌港药店",
		 * 			"consignee":"李连政",
		 * 			"areaname":"海珠区",
		 * 			"cityname":"广州市",
		 * 			"headurl":"http://test.sxbang.cn//data/img/pharmacy/user/2015/08/08/3728e658-6126-4a4c-b5ff-207739158429.jpg",
		 * 			"postcode":"",
		 * 			"cityid":31677,
		 * 			"storeid":1312034,
		 * 			"areaid":31725,
		 * 			"addressee":"广东省广州市海珠区晓港中马路131-8-9",
		 * 			"phone":"13143683782",
		 * 			"takeoverid":"22158",
		 * 			"is_allow":1,
		 * 			"gsp_certification":0},
		 * 	"orderCounts":{"unpayCount":0,
		 * 				"unComfirmCount":0,
		 * 				"allCount":35},
		 * 	"rName":"陈锋",
		 * 	"rPhone":"18218357258"
		 * 	}
         */
        setLocalUserInfo: function (userInfo) {
            localStorage.setItem("userInfo", userInfo)
        },
        /**
         * 返回一个对象可以直接通过dot调用属性
         */
        getLocalUserInfo: function () {
            if (localStorage.getItem("userInfo")) {
                return JSON.parse(localStorage.getItem("userInfo"));
            } else {
                alert("本地为存储用户信息")
            }

        },
        updateLocalUserInfo: function () {
            var r1, r2;
            sys.ajax({
                url: "/servlet/user/getUserInfo",
                async: false,
                callback: function (result) {
                    r1 = result;
                }
            });
            sys.ajax({
                url: "/servlet/yaomaimai/locationcommons/getLocationShopData",
                async: false,
                callback: function (result) {
                    r2 = result;
                }
            });
            $.extend(r1.data.address, r2.data);
            sys.setLocalUserInfo(JSON.stringify(r1.data));
        },
        /**
         * 根据name获取cookie的值
         */
        getCookie: function (name) {
            var value = null;
            if (document.cookie && document.cookie != '') {
                var cookies = document.cookie.split(';');
                for (var i = 0; i < cookies.length; i++) {
                    var cookie = jQuery.trim(cookies[i]);
                    // 判断这个key是否等于name
                    if (cookie.substring(0, name.length + 1) == (name + '=')) {
                        value = decodeURIComponent(cookie.substring(name.length + 1));
                        break;
                    }
                }
            }
            return value;
        },
        /**
         * 修改头部导航样式
         * parameter:
         *            headMode: 1---head1,主要导航头部；2---head2，次导航头部
         *            style-->样式 1192表示头部宽度为1192px，990表示头部宽度为990px
         *
         */
        changeHeadStyle: function (headMode, headStyle) {
            var isHead1Hidden = $(top.document.getElementsByClassName("page-head1")[0]).is(":hidden");	//head1是否隐藏
            var isHead2Hidden = $(top.document.getElementsByClassName("page-head2")[0]).is(":hidden");	//head2是否隐藏
            var currentHeadStyle = $(top.document.getElementById("headStyle")).attr("href").match(/\d+/); //当前所用头部的样式1192和990两种

            switch (headMode) {
                case 1:
                    if (!isHead1Hidden && headStyle == currentHeadStyle) {
                        break;
                    } else {
                        $(top.document.getElementsByClassName("page-head1")[0]).show();
                        $(top.document.getElementsByClassName("page-head2")[0]).hide();
                        if (!!top.document.getElementById("headStyle")) {
                            if (headStyle != currentHeadStyle) {
                                var hrefStr = "css/head" + headStyle + ".css"
                                top.document.getElementById("headStyle").href = hrefStr;
                            }
                        }
                    }
                    break;
                case 2:
                    if (!isHead2Hidden && headStyle == currentHeadStyle) {
                        break;
                    } else {
                        $(top.document.getElementsByClassName("page-head1")[0]).hide();
                        $(top.document.getElementsByClassName("page-head2")[0]).show();
                        if (!!top.document.getElementById("headStyle")) {
                            if (headStyle != currentHeadStyle) {
                                var hrefStr = "css/head" + headStyle + ".css"
                                top.document.getElementById("headStyle").href = hrefStr;
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        },
        //根据iframe里面body的高度设置iframe的高度
        updateFrameHeight: function () {
            var frame = window.frameElement;
            if (!frame) return false;
            var frameMinHeight = 800;
            var bodyHeight = document.body.clientHeight;
            frame.height = bodyHeight < frameMinHeight ? frameMinHeight : bodyHeight;
        }

    };
})(window, $);