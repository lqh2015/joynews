package com.hzpd.url;

public class Constant {
	public static enum TYPE{
		News("News"),Album("Album"),Html("Html"),Video("Video"),Magazine("Magazine")
		,NewsA("NewsA"),AlbumA("AlbumA"),HtmlA("HtmlA"),VideoA("VideoA")
		,MagazineA("MagazineA"),Shuoba("Shuoba");
		
		private String type;
		
		// 构造方法
        private TYPE(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
        	return type;
        }
	};

}
