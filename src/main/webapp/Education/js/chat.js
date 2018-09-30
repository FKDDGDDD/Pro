/*获取路径*/
function getRootPath(){
	//获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
	var curWwwPath=window.document.location.href;
	//获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
	var pathName=window.document.location.pathname;
	var pos=curWwwPath.indexOf(pathName);
	//获取主机地址，如： http://localhost:8083
	var localhostPaht=curWwwPath.substring(0,pos);
	//获取带"/"的项目名，如：/uimcardprj
	var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	return(localhostPaht+projectName);
}
/*发送消息*/
function send(str){
	var html="<div class='send'><div class='msg'>"+
	"<p>"+str+"</p></div></div>";
	upView(html);
	//setTimeout("upView(html)",1000);
}
/*发送课程消息*/
function sendWithList(html){
	upView(html);
	//setTimeout("upView(html)",2000);
}
/*给名称过短的课程补空格*/
function fillWithBlank(str){
	var n=0;   
	for(var i=0;i<str.length;i++)   
	{   
		//charCodeAt()可以返回指定位置的unicode编码,这个返回值是0-65535之间的整数   
		if(str.charCodeAt(i)<128) { 
			n++;    
		}   
		else {  
			n+=2;   
		}   
	}   
	if(n<=16){
		for(var i=0; i<8; i++)
			str += '&#12288';
			//str += 'u';
		//alert(str);
	}
	return str;
}   
/*比较大小*/
function min(a,b){
	if(a < b)
		return a;
	else
		return b;
}

/*接受消息，处理纯文本*/
function show(str){
	var html="<div class='show'><div class='msg'>"+
	"<p id='p_cus'>"+str+"</p></div></div>";
	upView(html);
	$.ajax({
		url: getRootPath()+"/FrontEnd/chat/reply",
		type: "post",
		data: {msg:str},
		dataType: "json",
		
		success:function(data){
			
			console.log(data);
			switch(data[0]){
			case '-1':
				send(data[1]);
				
				var html = "<div class='send'><div class='msg_img'>";
				for(var i=2; i<min(5,data.length); i++){
					html += "<p class='p_img'>"+"<a href='course_detail.html?lid="+data[i].lid+"><img src='../img/python.png'><span class='span_lname'>"+fillWithBlank(data[i].lname)+
					"</span><span class='span_lprice'>￥"+data[i].lprice+"</span></a>"+
					"</p>";
				}
				html += "<p class='p_img'><a href='#' onclick='show(&quot;换一批&quot;)'><img class='img_refresh' src='../img/refresh2.png'>" +
						"<span class='span_lname span_refresh'>换一批</span><span class='span_lname span_ad'>遇见所爱</span>" +
						"</a></p></div></div>";
				sendWithList(html);
				break;
			default:
				for(var i=0; i<data.length; i++){
					//alert(data[i]);
					send(data[i]);
				}
				break;
			}
		}
	});
}

/*更新视图*/
function upView(html){
	$('.message').append(html);
	//$('body').animate({scrollTop:$('.message').outerHeight()-window.innerHeight},200);
	$('html, body').animate({scrollTop: $(document).height()}, 50);
	//$('body').scrollTop(20);
	//alert($('body').scrollTop()); 
}
function sj(){
	return parseInt(Math.random()*10)
}
$(function(){
	setTimeout("send('Hi，大写的贤子，下午好，挑选课程有问题记得找小蜜哟~')",800);
	$('.footer').on('keyup','input',function(){
//		if($(this).val().length>0){
//			$(this).next().css('background','#FF7171').prop('disabled',true);
//		
//		}else{
//			$(this).next().css('background','#ddd').prop('disabled',false);
//		}
	})
	$('#send_btn').click(function(){
		show($(this).prev().val());
		$('#msg_input').val("");
		//test();
	})
	$('#msg_input').bind('keyup', function(event) {
	
		if (event.keyCode == "13") {
			show($('#msg_input').val());
			$('#msg_input').val("");
		}
	}); 
})
