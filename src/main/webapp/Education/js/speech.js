$(function(){
		var recorder;
		var count = 0;
		var audio = $("#reply")[0];
		$("#start").click(function(){
			count++;
			HZRecorder.get(function (rec) {
				if(count%2 == 1){
                    recorder = rec;
                    recorder.start();
                    //开始录音后就可以实时监视声音录制过程
                    recorder.onProgress(function(vol){
						console.log(vol)
					})
				}
				else{
					recorder.upload(function (data) {
						//data 对象为录音后的音频数据
			                var fd = new FormData();
			                var filename = generateUUID();
			                fd.append("speech", data); 
			                fd.append("filename", filename);
			                console.log(data);
			                $.ajax({
			                    url : getRootPath()+"/FrontEnd/chat/process",
			                    type : 'POST',
			                    data : fd,
			                    // 告诉jQuery不要去处理发送的数据
			                    processData : false,
			                    // 告诉jQuery不要去设置Content-Type请求头
			                    contentType : false,
			                    success : function(res) {
			                    	console.log(res);
			                    	show(res);
			                    },
			                    error : function(error) {
			                        console.log("error");
			                    }
			                });
			            });
				}
                });
			//开始录音，其中replay是一个音频对象
		
		})
		$("#stop").click(function(){
			//停止录音
			recorder.stop();
		})
		$("#play").click(function(){
			//播放录音
			recorder.play(audio);
		})
		$("#upload").click(function(){
			//上传音频数据，经过优化压缩过的
			recorder.upload(function (data) {
			//data 对象为录音后的音频数据
                var fd = new FormData();
                fd.append("speech", data); 
                console.log(data);
                $.ajax({
                    url : getRootPath()+"/FrontEnd/chat/process",
                    type : 'POST',
                    data : fd,
                    // 告诉jQuery不要去处理发送的数据
                    processData : false,
                    // 告诉jQuery不要去设置Content-Type请求头
                    contentType : false,
                    success : function(res) {console.log(res)},
                    error : function(error) {
                        console.log("error");
                    }
                });
            });
		})

	})
	
	function generateUUID() {
		var d = new Date().getTime();
		var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
			var r = (d + Math.random()*16)%16 | 0;
			d = Math.floor(d/16);
			return (c=='x' ? r : (r&0x3|0x8)).toString(16);
		});
		return uuid;
	};