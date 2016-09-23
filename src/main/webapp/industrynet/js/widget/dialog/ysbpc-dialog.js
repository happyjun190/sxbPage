/*
 * Copyright (c) 速道信息科技
 *  Date : 2016.
 * Author : llz
 */
requirejs.config({
    paths: {
        jquery: '/ysb/dian/js/jquery.min'
    }
});

define(['jquery'],function($){
    function Dialog(){
        this._options = {
            width:350,
            height:175,
            top:255,
            title:"",
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
     * [top]:弹窗离页面顶部距离，默认255px
     * [title]:弹窗titile，默认是不显示，只有传入非空title时，才会显示。
     * content:弹窗内容，必须项
     * [okText]：最左边的按钮的文字，默认“确定”
     * [okHandler]:最左边按钮（一般是确定按钮）的回调函数。
     * [cancelText]:最右边的按钮的文字，默认“取消”
     * [cancelHandler]:最右边按钮（一般是取消按钮）的回调函数。
     */
    Dialog.prototype = {
        showModal:function(options){

            var cfg = $.extend(this._options,options);
            var hasTitle = true;
            /**添加遮罩**/
            var diaglogMask = $('<div class="ysbpcdialog_mask"></div>')
            diaglogMask.appendTo("body");

            /**
             * 构建弹框框架
             * */
            //最外层
            var dialogWrapper = $('<div class="ysbpcdialog_wrapper"></div>');
            dialogWrapper.css({
                'width':cfg.width+"px",
                'height':cfg.height+"px",
                'left':(window.innerWidth - cfg.width)/2 + "px",
                'top':cfg.top + "px"
            })

            //dialog的title
            if(cfg.title){
                var dialogTitle = $('<div class="ysbpcdialog_title"></div>');
                dialogTitle.css({
                    'height':cfg.height/4-20+"px",
                    'margin':"auto",
                    'line-height':cfg.height/4-20+"px"
                })
                dialogTitle.html(cfg.title);
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

            var wrapperHeight;
            if(hasTitle){
                wrapperHeight = cfg.height/2-20;
            }else{
                wrapperHeight = cfg.height*100/175;
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
                /**样式**/
            var footerHeight;
            if(hasTitle){
                footerHeight = cfg.height/4-20;
            }else {
                footerHeight = cfg.height*75/170-20
            }
            btnOk.css({
                'width':cfg.width/5+"px",
                'height':(footerHeight+20)*5/12+"px",
                'line-height':(footerHeight+20)*5/12+"px",
                'margin-right':cfg.width/8+"px"
            })

            btnCancel.css({
                'width':cfg.width/5+"px",
                'height':(footerHeight+20)*5/12+"px",
                'line-height':(footerHeight+20)*5/12+"px"
            })
            dialogFooter.css({
                'height':footerHeight+"px",
                'margin':"auto",
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

        }
    };

    return {
        Dialog : Dialog
    };

});
