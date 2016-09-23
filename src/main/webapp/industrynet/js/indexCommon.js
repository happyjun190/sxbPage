/**
 * Created by Administrator on 2016/5/18.
 */
//订单数据存储
var orderData;
var areaId;
//全局地图弹框
//var mapPop = new MapPop();
//全局通用弹框
var commonDialog = new DialogNoRequire();
//分类显示标记
var indexFirstClassId = "";
var indexSecondClassId = "";
var indexFirstClassName = "";
var indexSecondClassName = "";

//回到顶部
function sTop(){
    $('body,html').animate({scrollTop:0},10);//回到顶部
    iFrameHeightOnece();
}

function iFrameHeightOnece() {
    var ifm= document.getElementById("pageContent");
    var subWeb = window.frames ? window.frames["pageContent"].document : ifm.contentDocument;
    if(ifm && subWeb && subWeb.body) {
        if(subWeb.body.scrollHeight<1000){
            ifm.height=1000;
        }else{
            ifm.height = subWeb.body.scrollHeight+20;
        }
    }
}

/*iframe自动适应高度*/
var l=0;
function iFrameHeight() {
    var ifm= document.getElementById("pageContent");
    var subWeb = window.frames ? window.frames["pageContent"].document : ifm.contentDocument;
    if(l==0){
        $('body,html').animate({scrollTop:0},10);//回到顶部
        if(ifm != null && subWeb != null) {
            ifm.height = 800;//初始化高度为500px
        }
        //切换显示头部tab样式
        var iframeSrc = document.getElementById("pageContent").src;
        var s = iframeSrc.substring(iframeSrc.indexOf("?"),iframeSrc.length);
        var headTab = sys.getUrlParam("headTab", s);
        if(headTab!=null){
            switch (headTab){
                case "1":
                    $(".navbar ul li").each(function(index){
                        $(this).removeClass("active");
                    });
                    $('#onlineOrder').addClass("active");
                    break;
                case "2":
                    $(".navbar ul li").each(function(index){
                        $(this).removeClass("active");
                    });
                    $('#myOrder').addClass("active");
                    break;
                case "3":
                    $(".navbar ul li").each(function(index){
                        $(this).removeClass("active");
                    });
                    $('#myBill').addClass("active");
                    break;
            }
        }
    }else{
        if(ifm && subWeb && subWeb.body) {
            if(subWeb.body.scrollHeight<1000){
                ifm.height=1000;
            }else{
                ifm.height = subWeb.body.scrollHeight+20;
            }
        }
    }
    l++;
    if(l<10){// 执行10次，也就是3秒。
        setTimeout("iFrameHeight()", 300);//每0.3半秒执行一次。
    }else{
        //重置l
        l=0;
    }
}

function createMask(){
    if(document.documentElement.scrollWidth<document.documentElement.clientWidth+document.documentElement.scrollLeft){
        var windowWidth=document.documentElement.clientWidth+document.documentElement.scrollLeft;
    }
    else{
        var windowWidth=document.documentElement.scrollWidth;
    }
    if(document.documentElement.scrollHeight<document.documentElement.clientHeight+document.documentElement.scrollTop){
        var windowHeight=document.documentElement.clientHeight+document.documentElement.scrollTop;
    }
    else{
        var windowHeight=document.documentElement.scrollHeight;
    }
    //modalDiv的动作搞完了，搞那个灰色遮罩div，先创建一个节点。
    var bodyMask=document.createElement("div");
    bodyMask.id="bodyMask";
    bodyMask.style.position="absolute";
    //如果你的网页中还有其它zIndex，那么，这个只应该大于其它zindex，同时上面的模态框的zindex，应该比这个zindex大1
    bodyMask.style.zIndex="-1";
    //这个灰色遮罩层的长与宽就是当前窗口的长与宽。
    bodyMask.style.width=windowWidth+"px";
    bodyMask.style.height=windowHeight+"px";
    bodyMask.style.top="0px";
    bodyMask.style.left="0px";
    bodyMask.style.background="#000";
    //设置其透明度为40%
    bodyMask.style.filter="alpha(opacity=50)";
    bodyMask.style.opacity="0.50";
    //在body节点下添加这个div
    document.body.appendChild(bodyMask);
    $(".page-head").css("z-index",-1);
    $(".footer").css("z-index",-1);
}

function hideMask(){
    var bodyMask=$("#bodyMask");
    bodyMask.hide();
    $(".page-head").css("z-index",0);
    $(".footer").css("z-index",0);
}

/* 调用支付页面的付款方法    */
function gotoPay(){
    $('#payWarn').modal('hide');
    pageContent.window.payFromCommit()();
}
// 弹出遮罩样式
function maskshow() {
    var w1 = $(window).width();
    $('html').addClass('fancybox-lock-test');
    var w2 = $(window).width();
    $('html').removeClass('fancybox-lock-test');
    $('html').addClass('fancybox-lock-test');
    $('head').append(
        "<style type='text/css'>.fancybox-margin{margin-right:" + (w2 - w1) + "px;}</style>");
    $('html').addClass('fancybox-margin');
    $('#mask').show();
    $(".page-head").css("z-index",-1);
    $(".footer").css("z-index",-1);
}
// 	关闭遮罩样式
function maskhide() {
    /**隐藏遮罩显示滚动条**/
    $('html').css('overflow-y', 'scroll');
    /**$('html',parent.document).removeClass('compensate-for-scrollbar'); **/
    $('html').removeClass('fancybox-lock-test');
    $('html').removeClass('fancybox-margin');
    $('#mask').hide();
    $(".page-head").css("z-index",0);
    $(".footer").css("z-index",0);
}

/**打开订单详情**/
function orderDetail(orderId) {
    $('#payCommit').modal('hide');
    sys.gotoHtml("order-info.html?orderid=" + orderId);
}

function gotoSpcart(){
    $('#cartCommit').modal('hide');
    //document.getElementById("pageContent").src = "SPcart2.html";
    sys.gotoHtml("SPcart2.html");
}

//  退出登陆
function logout(){
    sys.ajax({url:"/servlet/auth/webLogout",
        callback: function(result){
            top.location.href = "login.html";
        },
        errorCallback: function(result){
            top.location.href = "login.html";
        }
    });
}


$(document).ready(function(){
    $('#cartCommit').on('show.bs.modal', function () {
        var windowScreen = document.documentElement;
        // 获取main的div元素
        var main_div = document.getElementById("tipbox");
        // 通过窗口宽高和div宽高计算位置
        var main_top = (windowScreen.clientHeight - main_div.clientHeight)/2.5 + "px";
        var main_left = (windowScreen.clientWidth - main_div.clientWidth)/2.5 + "px";
        // 位置赋值
        main_div.style.top = main_top;
        main_div.style.left = main_left;
    });

    $('#payCommit').on('show.bs.modal', function () {
        var windowScreen = document.documentElement;
        // 获取main的div元素
        var main_div = document.getElementById("tipbox1");
        // 通过窗口宽高和div宽高计算位置
        var main_top = (windowScreen.clientHeight - main_div.clientHeight)/3 + "px";
        var main_left = (windowScreen.clientWidth - main_div.clientWidth)/3 + "px";
        // 位置赋值
        main_div.style.top = main_top;
        main_div.style.left = main_left;
    });

    $('#payWarn').on('show.bs.modal', function () {
        var windowScreen = document.documentElement;
        // 获取main的div元素
        var main_div = document.getElementById("tipbox2");
        // 通过窗口宽高和div宽高计算位置
        var main_top = (windowScreen.clientHeight - main_div.clientHeight)/4 + "px";
        var main_left = (windowScreen.clientWidth - main_div.clientWidth)/3.5 + "px";
        // 位置赋值
        main_div.style.top = main_top;
        main_div.style.left = main_left;
    });

    $('#publicExplain').on('show.bs.modal', function () {
        var windowScreen = document.documentElement;
        // 获取main的div元素
        var main_div = document.getElementById("tipbox3");
        // 通过窗口宽高和div宽高计算位置
        var main_top = (windowScreen.clientHeight - main_div.clientHeight)/3 + "px";
        var main_left = (windowScreen.clientWidth - main_div.clientWidth)/3 + "px";
        // 位置赋值
        main_div.style.top = main_top;
        main_div.style.left = main_left;
    });
});

function classClick(a){
    $(".index-sort").hide();
    var classId = $(a).attr("id");
    var className = $(a).parent().attr("class");
    if( className == "index-sort-member-p" ){
        indexFirstClassId = classId;
        indexFirstClassName = $(a).text();
        indexSecondClassId = "";
        indexSecondClassName = "";
    }else{
        var firstClassId = $(a).parent().parent().children(".index-sort-member-p").children("a").attr("id");
        var firstClassName = $(a).parent().parent().children(".index-sort-member-p").children("a").text();
        indexFirstClassId = firstClassId;
        indexFirstClassName = firstClassName;
        indexSecondClassId = classId;
        indexSecondClassName = $(a).text();
    }
    var str = $("#pageContent").attr("src");
    var htmlName = str.split("?");
    if( htmlName[0] == "index-content.html"){
        $("#iframeContentDiv").find("iframe")[0].contentWindow.goSearchByClass(classId);
    }else{
        gotoSortListHtmlByClass(classId);
    }
}

function gotoSortListHtmlByClass(classId){
    sys.gotoHtml("index-content.html?classId="+classId);
};

/*搜索补全改变搜索框的值并搜索*/
function chooseKey(a){
    $(".search-input-group").removeClass("open");
    var value = $(a).attr("value");
    searchKeyTemp = value;
    $("#searchKey").val(value);
    var str = $("#pageContent").attr("src");
    var htmlName = str.split("?");
    initClassAll();
    if( htmlName[0] == "index-content.html"){
        $("#iframeContentDiv").find("iframe")[0].contentWindow.goSearch();
        $("#iframeContentDiv").find("iframe")[0].contentWindow.loadProvidersListBySearchKey();
    }else{
        gotoSortListHtmlByKey();
    }
}

/*全局活动标签点击*/
function tagClick(tagId,a){
    //更改样式
    $("#tags li a").each(function(){
        $(this).removeClass("active-a");
    });
    $(a).addClass("active-a");
    initClassAll();
    window.open("index.html?pageGo=drugListByTag&tagId=" + tagId );
}

function gotoSortListHtml(){
    sys.gotoHtml("index-content.html");
}

function gotoSortListHtmlByKey(){
    sys.gotoHtml("index-content.html?searchKey="+$("#searchKey").val());
}

function gotoSortListHtmlByTag(tagId){
    sys.gotoHtml("index-content.html?tagId="+tagId);
}

function initClassAll(){
    indexFirstClassId = "";
    indexFirstClassName = "";
    indexSecondClassId = "";
    indexSecondClassName = "";
}

function gotoSortHtmlBySearchKey(){
    $(".search-input-group").removeClass("open");
    var value = $("#searchKey").val();
    if(!!value){
        searchKeyTemp = value;
        $("#searchKey").val(value);
        var str = $("#pageContent").attr("src");
        var htmlName = str.split("?");
        initClassAll();
        if( htmlName[0] == "index-content.html"){
            $("#iframeContentDiv").find("iframe")[0].contentWindow.goSearch();
            $("#iframeContentDiv").find("iframe")[0].contentWindow.loadProvidersListBySearchKey();
        }else{
            gotoSortListHtmlByKey();
        }
    }
}