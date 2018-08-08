<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>教师添加页</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link rel="stylesheet" href="<%=request.getContextPath() %>/BackEnd_final/layui/css/layui.css">
		<script src="<%=request.getContextPath() %>/BackEnd_final/js/jquery-3.2.0.min.js"></script>
		<style>
			.layui-card {margin: 35px 45px 45px 0; text-align: center; border-radius: 10px;}
			.layui-card-header {
				font-size: large; 
				background-color: antiquewhite; 
				border-top-left-radius: 10px;
				border-top-right-radius: 10px;
			}
			body {font-family: '宋体'; background-color: #F8F8FF;}
			#demo1 {width: 100%; height: 450px; margin-bottom: 3%;}
			#demo {width: 100%; height: 450px; margin-bottom: 3%;}
		</style>
		<script type="text/javascript">
            function IFrameResize()
            {
                    //得到父页面的iframe框架的对象
                var obj = parent.document.getElementById("myFrame");
                    //把当前页面内容的高度动态赋给iframe框架的高
                var height = getComputedStyle(window.parent.document.getElementsByClassName("layui-body")[0],null).height;
                console.log(height); 
                obj.height = height;
            } 
        </script>
	</head>

	<body class="layui-layout-body" onload="IFrameResize();">
		<div class="layui-layout layui-layout-admin" style="margin: 0px 50px 0px 0px;">
			<div class="layui-col-md12">
				<div class="layui-row">
					<div class="layui-collapse">
						<div class="layui-col-md12 layui-colla-item">
							<div class="layui-card">
								<div class="layui-card-header layui-colla-title">每日签到积分</div>
								<div class="layui-card-body layui-colla-content layui-show" 
										style="height: 50px; line-height: 50px;">
										<input type="text" class="layui-input" id="oneintegral" value=""/>
								</div>										
							</div>
						</div>
						<div class="layui-col-md12 layui-colla-item">
							<div class="layui-card">
								<div class="layui-card-header layui-colla-title">补签消耗积分数量</div>
								<div class="layui-card-body layui-colla-content layui-show" 
										style="height: 50px; line-height: 50px;">
										<input type="text" class="layui-input" id="buintegral"/>
								</div>										
							</div>
						</div>
						<div class="layui-col-md12 layui-colla-item">
							<div class="layui-card">
								<div class="layui-card-header layui-colla-title">连续签到七天奖励数量</div>
								<div class="layui-card-body layui-colla-content layui-show" 
										style="height: 50px; line-height: 50px;">
										<input type="text" class="layui-input" id="sevenintegral" />
								</div>										
							</div>
						</div>
						<div class="layui-form-item">
							<button class="layui-btn" type="button" style="margin: 10% 0 0 45%;" id="updateIntegral">
								保存积分信息
							</button>
						</div>	
					</div>
				</div>
			</div>
		</div>

		<script type="text/javascript" src="<%=request.getContextPath() %>/BackEnd_final/layui/layui.js"></script>
	
		<script>
		
		$(function(){
			$.ajax({
	  			url:"../../test/QianDao/getIntegral",
	  			type:"post",
				dataType:"json",
				async:false,
				success:function(data){		
					console.log(data);
					$("#oneintegral").attr('value',data.oneintegral);
					$("#buintegral").attr('value',data.buintegral);
					$("#sevenintegral").attr('value',data.sevenintegral);					
				},
				error:function(data){
					alert(data.result);
					//alert("error")
				}
	  		});
			$("#updateIntegral").click(function(){
				var oneintegral = $("#oneintegral").val();
				var buintegral = $("#buintegral").val();
				var sevenintegral = $("#sevenintegral").val();
				console.log(oneintegral+" "+buintegral+" "+sevenintegral);
				$.ajax({
		  			url:"../../test/QianDao/updateIntegral",
		  			type:"post",
					dataType:"json",
					async:false,
					data:{
						oneintegral : parseInt(oneintegral),
						buintegral : parseInt(buintegral),
						sevenintegral : parseInt(sevenintegral)
					},
					success:function(data){		
						alert(data.result);
					},
					error:function(data){
						alert(data.result);
						//alert("error")
					}
		  		});
			});
		});
		
		</script>
	</body>
</html>