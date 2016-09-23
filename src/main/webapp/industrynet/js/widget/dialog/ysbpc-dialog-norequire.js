/*
 * Copyright (c) 速道信息科技
 *  Date : 2016.
 * Author : llz
 * 不需要require，直接通过script标签引入即可
 */

    function DialogNoRequire(){
        this._options = {
            width:350,
            height:175,
            top:255,
            title:"",
            isCloseButtonHide:false,
            hasFooter:true,
            content:"",     //不能为空
            okText:"确定",    //
            okHandler:null,
            cancelText:"取消",
            cancelHandler:null
        }
    };

    /**
     *
     * @type {{showModal: Dialog.showModal}}
     * 数据字典options包含以下内容：[]包裹内容表示非必须，其它是必须
     * [width]:弹窗宽度， 默认是350px
     * [height]：弹窗高度， 默认是175px
     * [top]:弹窗离页面顶部距离，默认255px。如果不传入，则默认弹框居中显示
     * [title]:弹窗titile，默认是不显示，只有传入非空title时，才会显示,高度固定60px
     * [isCloseButtonHide]:弹窗关闭按钮，默认是显示，传入true时，隐藏按钮
     * [hasFooter]:默认有按钮，传入false时，隐藏
     * content:弹窗内容，必须项
     * [okText]：最左边的按钮的文字，默认“确定”。高度固定30px
     * [okHandler]:最左边按钮（一般是确定按钮）的回调函数。
     * [cancelText]:最右边的按钮的文字，默认“取消”。若传空字符串，取消按钮不显示。高度固定30px
     * [cancelHandler]:最右边按钮（一般是取消按钮）的回调函数。
     */
    DialogNoRequire.prototype = {
        showModal:function(options){
            var temp=$.extend({},this._options);
            var cfg = $.extend(temp,options);
            var hasTitle = true;
            var windowWidth = sys.getWindowWidth();
            var windowHeight = sys.getWindowHeight();
            /**添加遮罩**/
            var diaglogMask = $('<div class="ysbpcdialog_mask"></div>')
            diaglogMask.css({
                width:windowWidth+"px",
                height:windowHeight+"px"
            })
            diaglogMask.appendTo("body");

            /**
             * 构建弹框框架
             * */
            //最外层
            var dialogWrapper = $('<div class="ysbpcdialog_wrapper"></div>');
            dialogWrapper.css({
                'width':cfg.width+"px",
                'height':cfg.height+"px",
                'left':(this.getWindowWidth() - cfg.width)/2 + "px",
                'top':cfg.top + "px"
            })

            //dialog的title
            var titleHeight = 40;
            if(cfg.title){
                var dialogTitle = $('<div class="ysbpcdialog_title">'
                    +'<span class="title-content">'+cfg.title+'</span>'
                    +'<a class="title-close"><img src="images/delete_icon.png"></a>'
                    + '</div>');
                dialogTitle.css({
                    'height':titleHeight+"px",
                    'line-height':titleHeight+"px"
                });
                $(dialogTitle).find(".title-close").on('click',function () {
                    dialogWrapper.remove();
                    diaglogMask.remove();
                });

                if(cfg.isCloseButtonHide){
                    $(dialogTitle).find(".title-close").hide();
                }
                
                dialogTitle.appendTo(dialogWrapper)
            }else {
                hasTitle=false;
            }

            //内容，支持任意格式
            var ysbpcdialog_content_wrapper = $('<div class="ysbpcdialog_content_wrapper"></div>');
            var dialogContent = $('<div class="ysbpcdialog_content"></div>')    //辅助定位
            if(!cfg.content){
                throw new Error("提示内容不能为空");
            }

            var footerHeight =60;
            var wrapperHeight;
            if(hasTitle){
                wrapperHeight = cfg.height-footerHeight-titleHeight-2;
            }else{
                wrapperHeight = cfg.height-footerHeight;
            }
            ysbpcdialog_content_wrapper.css({
                'height':wrapperHeight+"px",
            });

            dialogContent.css({
                //待完善
            });

            dialogContent.html(cfg.content);
            dialogContent.appendTo(ysbpcdialog_content_wrapper)
            ysbpcdialog_content_wrapper.appendTo(dialogWrapper);

            /**底部按钮**/
            var dialogFooter = $('<div class="ysbpcdialog_footer"></div>');
            var btnOk = $(
                '<a class="ysbpcdialog_button ysbpcdialog_button_ok"  href="javascript:;">'
                + cfg.okText
                +'</a>'
            );
            var btnCancel = $(
                '<a class="ysbpcdialog_button ysbpcdialog_button_cancel" href="javascript:;">'
                +cfg.cancelText
                +'</a>'
            );
            

            // if(hasTitle){
            //     footerHeight = cfg.height/4-20;
            // }else {
            //     footerHeight = cfg.height*75/175-21;
            // }
            if(cfg.hasFooter == false){//无按钮则隐藏dialogFooter
            	dialogFooter.hide();
            }

            var btnOkMarginRight;
            if(cfg.cancelText){
                btnOkMarginRight = cfg.width/8 ;
            }else{
                btnOkMarginRight=0
            }
            btnOk.css({
                'width':cfg.width/5+"px",
                // 'line-height':(footerHeight+20)*5/12+"px",
                // 'height':(footerHeight+20)*5/12+"px",
                'height':30+"px",
                'line-height':30+"px",
                'margin-right':btnOkMarginRight+"px"
            })

            btnCancel.css({
                'width':cfg.width/5+"px",
                'height':30+"px",
                'line-height':30+"px",
            })
            dialogFooter.css({
                'height':footerHeight+"px",
                'line-height':footerHeight+"px"

            });

            /**按钮回调**/
            btnOk.click(function(){
                cfg.okHandler && cfg.okHandler();
                dialogWrapper.remove();
                diaglogMask.remove();
            });

            btnCancel.click(function(){
                cfg.cancelHandler && cfg.cancelHandler()
                dialogWrapper.remove();
                diaglogMask.remove();

            });

            if(cfg.okText){
                btnOk.appendTo(dialogFooter);
            }
            if(cfg.cancelText){
                btnCancel.appendTo(dialogFooter);
            }

            dialogFooter.appendTo(dialogWrapper);


            /**显示**/
            dialogWrapper.appendTo('body');
            var setDialogPositionFlag;
            if("top" in options){
            	setDialogPositionFlag = 0;
            } else {
            	setDialogPositionFlag = 1;
            }
            if(setDialogPositionFlag === 1){
            	setDialogPositionCenter();
            }
            
            function  setDialogPositionCenter(){
            	var obj = $(".ysbpcdialog_wrapper");
            	var objHeight = $(".ysbpcdialog_wrapper").height();
            	var windowHeight =  $(window).height();
            	$(".ysbpcdialog_wrapper").css({
            		top:(windowHeight-objHeight)/2+$(window.parent.document).scrollTop()
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

        getWindowHeight:function(){
            var windowHeight;
            if(document.documentElement.scrollHeight<document.documentElement.clientHeight+document.documentElement.scrollTop){
                windowHeight=document.documentElement.clientHeight+document.documentElement.scrollTop;
            }
            else{
                windowHeight=document.documentElement.scrollHeight;
            }
            return windowHeight;
        },

        hideModal:function(){
        	$('.ysbpcdialog_wrapper').remove();
        	$('.ysbpcdialog_mask').remove();
        }

    };


