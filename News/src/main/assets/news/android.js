//客户端传参识别，close_lazyload 和 url_prefix可由客户端在<head>内插入<script></script>代码来定义
if(typeof close_lazyload == 'undefined'){
	//图片懒加载开关
	close_lazyload = false;
}

function largeImg(){
 	window.imagelistner.openImage(this.src);
 	return true;
}
/****************************************************************
* 页面初始化入口
*****************************************************************/
function initPage(){
	var images = document.getElementsByTagName("img");
	for(var i=0;i<images.length;i++){
		images[i].onclick=largeImg;
	}
	return;
	//通知客户端domReady
	location.href = "hotnews://domReady";
}

// TODO
document.addEventListener("DOMContentLoaded",initPage,false);

