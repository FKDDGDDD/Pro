var branch_name="选择分部地址"

function a(aid,branch){
	 sessionStorage.setItem("branch_name", branch);
	 sessionStorage.setItem("branch_id", aid);
//	branch = escape(branch);
	 var click2 = document.getElementById("back");
	
	 click2.click();
	//window.location.href = "buy.html?branch="+branch;
}

function getUrlParam(name) {
		   var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 构造一个含有目标参\数的正则表达式对象		  
		   var r = window.location.search.substr(1).match(reg);  // 匹配目标参数
		   if (r != null) return unescape(r[2]); return null; // 返回参数值
		}
function getUrl(name){
	var href = window.location.href;
	alert(href);
	return null;
}
function buy_init(){
	//alert("hello");
		//var branch=getUrlParam("branch");
		//alert(branch);
	/*	if(branch==null){
				//document.getElementById("branch").innerHTML="选择分部地址";
		}else{
			document.getElementById("branch").innerHTML=branch;
		}*/
		 var value = sessionStorage.getItem("branch_name");
		 if(value!=null){
			 branch_name = value;
		 }
		document.getElementById("branch").innerHTML=branch_name;
		document.getElementById("branchname").value=branch_name;
}
