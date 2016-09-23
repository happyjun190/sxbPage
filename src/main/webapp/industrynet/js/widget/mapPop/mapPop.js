/*
 * Copyright (c) 速道信息科技
 *  Date : 2016.
 * Author : llz
 */

function MapPop(){
    this._options = {
        width: 900,
        height: 540,
        top: 230,
        confirmHandler: null,
        city:"",    //药店城市
        storeAddress:"",    //用户输入的药店名称
        storeName:""
    };


};


MapPop.prototype = {

    /**
     * @param options,
     * {
     *   storeName:"速道大药房",                            ------->用户输入的药店名，必需
     *   city:"广州",    //药店城市                        ------->用户选择的药店所在城市，必需
     *   storeAddress:"广州市岗顶",                        ------->用户输入的药店地址，必需
     *   confirmHandler:function(result){  }               ------->按确认加入的回调函数，执行结果在参数result中，必需
     *    width:900,                                       ------->弹框宽度，默认900，非必需
     *   height:500,                                       ------->弹框高度，默认500，非必需
     *   top:100                                           ------->弹框距离顶部高度，默认100，非必需
     *
     * }
     *
     */
    showMapPop:function(options){
        var cfg = $.extend(this._options,options);
        var result={
            "isauto":0,   //0 自动定位，1 手动定位
            "lon": 0,
            "lat": "",
            "address":cfg.storeAddress
        };
        var searchResult=null;
        var windowWidth = sys.getWindowWidth();
        var windowHeight = sys.getWindowHeight();

        /**添加遮罩**/
        var mappopMask = $('<div class="mappop_mask"></div>');
        mappopMask.css({
            width:windowWidth+"px",
            height:windowHeight+"px"
        })
        mappopMask.appendTo("body");

        /**
         * 构建弹框框架
         * */
        //最外层
        var mappopWrapper = $('<div class="mappop_wrapper"></div>');
        mappopWrapper.css({
            'width':cfg.width+"px",
            'height':cfg.height+"px",
            'left':(this.getWindowWidth() - cfg.width)/2 + "px",
            'top':cfg.top + "px"
        })

        // 拖动标注提示框
        var maskNote = $('<span>拖动标注可调整位置</span>');
        maskNote.css({
            position:"absolute",
            bottom:"25px",
            opacity: "0.8",
            filter: "alpha(opacity=20)",
            backgroundColor:"#333333",
            width:"200px",
            height:"35px",
            lineHeight:"35px",
            left:cfg.width/2-100,
            zIndex:"1001",
            fontWeight:"bold",
            fontSize:"16px",
            color:"#f2f2f2",
            textAlign:"center",
            borderRadius:"4px"
        });
        maskNote.appendTo(mappopWrapper);
        //head
        var mappopHead = $(
            '<div class="mappop_title">'+
                '<span>选择药店位置</span>'+
                '<button type="button" id="mappop_btn_close"><img src="images/delete_icon.png"></img></button>'+
            '</div>'
        );
        mappopHead.appendTo(mappopWrapper);
        $(mappopHead).find("#mappop_btn_close").on("click",function(){
            mappopWrapper.remove();
            mappopMask.remove();
        });

        //content title
        var mappopContentTitle = $(
            '<div class="mappop_content_title">'
                +'<div class="mappop_search_wrapper">'
                    +'<input id="mappop_search_input" type="text" size="20"  placeholder="请输入您要定位的药店位置">'
                    +'<div id="mappop_btn_search">'

                    +'</div>'
                +'</div>'
                +'<span id="mappop_error_tip"></span>'
                +'<input type="button" class="mappop_btn_confirm" value="确认添加">'+
            '</div>'
        );
        /*搜索框的初始值*/
        $(mappopContentTitle).find("#mappop_search_input").attr('placeholder',cfg.storeAddress);

        /*输入框输入药店地址时，开启智能搜索*/
        $(mappopContentTitle).find("#mappop_search_input").on("input propertychange",function(){

            var userInputAddress = $(this).val();
            if(!userInputAddress)
                return;
            $("#mappop_btn_search").empty();
            var local = new BMap.LocalSearch(cfg.city, { //智能搜索
                onSearchComplete: myFun
            });
            local.search(userInputAddress);


            function myFun(){
                if(!local.getResults){
                    return;
                }
                searchResult = local.getResults();

                var length = searchResult.getCurrentNumPois();
                for(var i=0;i<length;i++){
                    var html = '<p class="search_result_item" data-index='+i+'>'+searchResult.getPoi(i).address+'</p>'
                    $("#mappop_btn_search").append(html);
                }

                //点击搜索结果重新定位
                $(mappopContentTitle).find(".mappop_search_wrapper").on('click','.search_result_item',function(){
                    //移除之前的搜索结果
                    $("#mappop_btn_search").empty();
                    //移除之前的标注
                    map.clearOverlays();
                    //移除定位失败的提示
                    $(mappopContentTitle).find("#mappop_error_tip").text("")


                    var index = $(this).data("index");
                    var pp = searchResult.getPoi(index).point;
                    //将选择的结果填写到输入框中
                    $(mappopContentTitle).find("#mappop_search_input").val("");
                    $(mappopContentTitle).find("#mappop_search_input").attr("placeholder",searchResult.getPoi(index).address);

                    map.centerAndZoom(pp, 18);
                    //maker = new BMap.Marker(pp,{icon:myIcon});
                    //map.addOverlay(maker);    //添加标注
                    //maker.setAnimation(BMAP_ANIMATION_BOUNCE); //跳动的动画
                    setLabel(map,pp,cfg.storeName);
                    setMarker(map,pp);
                    //选择搜索结果后的返回值
                    result.isauto = 1;  //手动定位
                    result.lon = pp.lng;
                    result.lat = pp.lat;
                    result.address = searchResult.getPoi(index).address;
                });
            }

        });

        /**
         * 点击确认添加按钮，回调并返回定位结果
         * */
        $(mappopContentTitle).find(".mappop_btn_confirm").on("click",function(){
            cfg.confirmHandler(JSON.stringify(result));
            mappopWrapper.remove();
            mappopMask.remove();
        });
        mappopContentTitle.appendTo(mappopWrapper);

        /**
         *百度地图显示区
         */

        var mappopMap = $('<div id="baidumap"></div>');
        $(mappopMap).css({
            "height":cfg.height-52-35+"px"
        });
        mappopMap.appendTo(mappopWrapper);
        mappopWrapper.appendTo("body");


        /*百度地图*/
        var map=null,
            maker=null,
            myIcon=null,
            myGeo=null,
            label = null;
        map = new BMap.Map("baidumap",{"enableHighResolution":false,minZoom:4,maxZoom:22,"enableMapClick":false});
        //myIcon = new BMap.Icon("images/store_marker.png", new BMap.Size(22,25));
        myGeo = new BMap.Geocoder();
        map.enableScrollWheelZoom(true)
        /**
         *第一次加载时，将用户传入的药店地址解析结果显示在地图上
          */
        myGeo.getPoint(cfg.storeAddress, function(point){
            if (point) {
                map.centerAndZoom(point, 17);
                setLabel(map,point,cfg.storeName);
                setMarker(map,point);

                result.isauto = 0;
                result.lon = point.lng;
                result.lat = point.lat;
            }else{

                //todo 如果解析失败，浏览器定位到用户当前位置
                var geolocation = new BMap.Geolocation();
                geolocation.getCurrentPosition(function(r){
                    if(this.getStatus() == BMAP_STATUS_SUCCESS){
                        map.centerAndZoom(r.point, 14);
                        setLabel(map, r.point,cfg.storeName);
                        setMarker(map, r.point)

                        $(mappopContentTitle).find("#mappop_error_tip").text("无法定位当前查询的地址，请输入正确的地址")
                    } else {
                        alert('获取当前位置信息失败：'+this.getStatus());
                    }
                },{enableHighAccuracy: true})
                //关于状态码
                //BMAP_STATUS_SUCCESS	检索成功。对应数值“0”。
                //BMAP_STATUS_CITY_LIST	城市列表。对应数值“1”。
                //BMAP_STATUS_UNKNOWN_LOCATION	位置结果未知。对应数值“2”。
                //BMAP_STATUS_UNKNOWN_ROUTE	导航结果未知。对应数值“3”。
                //BMAP_STATUS_INVALID_KEY	非法密钥。对应数值“4”。
                //BMAP_STATUS_INVALID_REQUEST	非法请求。对应数值“5”。
                //BMAP_STATUS_PERMISSION_DENIED	没有权限。对应数值“6”。(自 1.1 新增)
                //BMAP_STATUS_SERVICE_UNAVAILABLE	服务不可用。对应数值“7”。(自 1.1 新增)
                //BMAP_STATUS_TIMEOUT	超时。对应数值“8”。(自 1.1 新增)
            }
        }, cfg.city);


        var top_right_navigation = new BMap.NavigationControl({anchor: BMAP_ANCHOR_TOP_RIGHT,
            type: BMAP_NAVIGATION_CONTROL_SMALL});
        map.addControl(top_right_navigation);


        function setLabel(map,point,content){
            var labelStyle = {
                color : "#2f3132",
                fontSize : "16px",
                height : "20px",
                fontWeight:"bold",
                display:"inline-table",
                width:"180px",
                lineHeight : "20px",
                fontFamily:"微软雅黑",
                padding:"5px",
                border:"none",
                textAlign:"center"
            }
            var opts = {
                position : point,    // 指定文本标注所在的地理位置
                offset   : new BMap.Size(-90, -50)    //设置文本偏移量
            }
            label = new BMap.Label(content, opts);
            label.setStyle(labelStyle);
            map.addOverlay(label);
        };
        function setMarker(map,point){
            myIcon = new BMap.Icon("images/store_marker.png", new BMap.Size(22,25));
            maker = new BMap.Marker(point,{icon:myIcon});
            map.addOverlay(maker);
            maker.setAnimation(BMAP_ANIMATION_DROP); //跳动的动画
            maker.enableDragging();
            maker.addEventListener("dragend",function(event){

                //移除定位失败的提示
                $(mappopContentTitle).find("#mappop_error_tip").text("")

                //返回定位的坐标信息：
                result.isauto = 1;
                result.lon = event.point.lng;
                result.lat = event.point.lat;

                label.setPosition(event.point);
                myGeo.getLocation(event.point, function(rs){
                    if(rs){
                        //在地址框中显示当前位置
                        $(mappopContentTitle).find("#mappop_search_input").attr('placeholder',rs.address);
                        //返回定位的位置信息
                        result.address = rs.address;
                    }else{
                        alert("获取地理位置信息失败，请检查网络");
                    }
                });
            });
        }

    },
    getWindowWidth:function (){
        var windowWidth;
        if(document.documentElement.scrollWidth<document.documentElement.clientWidth+document.documentElement.scrollLeft){
            windowWidth=document.documentElement.clientWidth+document.documentElement.scrollLeft;
        }
        else{
            windowWidth=document.documentElement.scrollWidth;
        }

        return windowWidth;
    },


}