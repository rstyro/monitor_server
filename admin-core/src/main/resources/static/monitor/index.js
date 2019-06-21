var listenerUrl = "/monitor/server/listener";
//显示报警
function showBell(ip) {
    $("#warningIp").text(ip);
    $("#warningModal").modal("show");
}

//隐藏警报
function hideBell() {
    $("#warningModal").modal("hide");
}

// 获取失败服务器
function getFailedServer(){
    $.get(_ctx+listenerUrl,{t:new Date().getTime()},function(data){
        if(data.status == "200"){
            var arr = data.data.list;
            var ips = "";
            if(arr.length > 0 ){
                for (var i = 0; i < arr.length; i++) {
                    var item = arr[i];
                    ips = ips+item.ip+"、";
                }
                ips = ips.substring(0,ips.length-1);
                showBell(ips);
            }else{
                var refresh =  data.data.refresh;
                if(refresh == "1"){
                    //通过后后看是否刷新页面
                    window.location.href=window.location.href;
                }
            }
        }else{
            console.log(data.message);
        }
    });
}

//轮训，暂时先这么搞
var timer = setInterval(function () {
    console.log("date",new Date());
    getFailedServer();
},10*1000);


$(function () {
    //模态框隐藏时回调
    $('#warningModal').on('hide.bs.modal', function() {
        // ap1.toggle();
        ap1.pause();
        //隐藏的时候刷新一下
        window.location.href=window.location.href;
    });
    //模态框显示时回调
    $('#warningModal').on('show.bs.modal', function () {
        ap1.play();
    })
})

