<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>预约课程信息</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link rel="stylesheet" href="../layui/css/layui.css">
		<style>
			.layui-card {margin: 35px 45px 45px 0; border-radius: 10px; text-align: center;}
			.layui-card-header {
				font-size: 25px; 
				font-weight: 600;
				background-color: antiquewhite; 
				border-top-left-radius: 10px;
				border-top-right-radius: 10px;
				text-align: left;
			}
			.layui-icon {font-size: 21px;}
			.thead {font-weight: 600; font-size: 18px;}
  			.layui-card-body {margin-top: 2%; padding: 0px 40px 10px 40px;}
		    #a_reservationAdd {text-decoration: none;font-size: 17px; color: #808080;}
		    #selectStyle {display: inline-block; margin-left: 20px; font-size: 16px; font-weight: 500;}
		    td {text-align: center;}
			img {width: 80px; height: 80px;}
			button {border: none; background-color: transparent;}
			body {font-family: "宋体"; background-color: #F8F8FF;}
  		</style>
		<script type="text/javascript">
            function IFrameResize()
            {
                    //得到父页面的iframe框架的对象
                var obj = parent.document.getElementById("myFrame");
                    //把当前页面内容的高度动态赋给iframe框架的高
                obj.height = this.document.html.height;
            } 
        </script>
	</head>

	<body class="layui-layout-body" onload="IFrameResize();">
		<div class="layui-layout layui-layout-admin" style="margin: 0px 50px 0px 0px;">
			<!-- 内容主体区域 -->
			<div class="layui-card">
				<div class="layui-card-header">
					<span>预约课程信息表</span>
					<div id="selectStyle">
						<select id="branch" name="branch" >    
						    <option value="1">实训中心</option> 
					      	<option value="2">艺术中心</option>   
					      	<option value="3">恒大名都</option>    
					    </select>
				    </div>
					<span style="float: right;">
						<a href="reservationAdd.jsp" id="a_reservationAdd">
							<i class="layui-icon layui-icon-add-1"></i>添加预约课程
						</a>
					</span>
				</div>
				<div class="layui-card-body">
					<table style="border-collapse: separate; width: 100%; border-spacing: 10px 20px;">
						<thead>
							<tr>
						        <th class="td1" width="23%">预约课程标题</th>
						        <th class="td1" width="23%">预约课程图片</th>
						        <th class="td2" width="15%">预约课程状态</th>                       
						        <th class="td1" width="20%">预约课程发布时间</th>
						        <th>操作</th>
						    </tr> 
						</thead>
						<tbody style="font-size: 15px;">
							<tr>
						      <td>大数据免费试听课</td>
						      <td >    	 
						        <img src="../images/bigdata.jpg" id="img" />
						      </td>
							  <td>进行中</td>
							  <td>2018-6-2 06:25:16</td>
							  <td>
								<button><a href="reservationDetail.jsp"><i class="layui-icon layui-icon-right"></i>&nbsp;</a></button>
								<button><a href="reservationModify.jsp"><i class="layui-icon layui-icon-edit" ></i>&nbsp;</a></button>
								<button><a href="#"><i class="layui-icon layui-icon-delete"></i></a></button>
							  </td>
						    </tr> 
						    <tr>
							    <td>人工智能AI免费试听课</td>
							    <td>	      	 
							        <img src="../images/dp.png" id="img" /> 	
							    </td>
							    <td>进行中</td>
							    <td>2018-6-26 10:25:16</td>
							    <td>
							    	<button><a href="reservationDetail.jsp"><i class="layui-icon layui-icon-right"></i>&nbsp;</a></button>
									<button><a href="reservationModify.jsp"><i class="layui-icon layui-icon-edit" ></i>&nbsp;</a></button>
									<button><a href="#"><i class="layui-icon layui-icon-delete"></i></a></button>
							    </td>
						    </tr> 
						    <tr>
							    <td>深度学习：CNN神经网络试听课</td>
							    <td >	      	 
							        <img src="../images/ai.png" id="img" />	
							    </td>
							    <td>进行中</td>
							    <td>2018-6-28 10:25:16</td>
							    <td>
							    	<button><a href="reservationDetail.jsp"><i class="layui-icon layui-icon-right"></i>&nbsp;</a></button>
									<button><a href="reservationModify.jsp"><i class="layui-icon layui-icon-edit" ></i>&nbsp;</a></button>
									<button><a href="#"><i class="layui-icon layui-icon-delete"></i></a></button>
							    </td>
						    </tr>
						</tbody>
					</table>
					<div id="test1" style="margin-top: 40px; margin-left: 8%"></div>
				</div>
			</div>	
		</div>
	</body>
	<script src="../layui/layui.js"></script>
	<script>
	layui.use('laypage', function(){
	  var laypage = layui.laypage;
	  
	  //执行一个laypage实例
	  laypage.render({
	    elem: 'test1' //注意，这里的 test1 是 ID，不用加 # 号
	    ,count: 50 //数据总数，从服务端得到
	  });
	});
	</script>
</html>