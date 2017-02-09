package com.hzpd.modle.vote;


public class Vote_detailMultiPicBean {

	private String optionid;//":"330",
	private String subjectid;//":"59",
	private Vote_OptionBean option;//":
	//         "{
//             "name":"选项名称1",
//              "imgurls": [
//                 "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967461_59440.jpeg",
//                 "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967461_57542.jpeg",
//                 "http://www.99cms_hb.info/Public/Tipoffs/201412/19/s_1418967462_81751.jpeg"
//             ],
//             "description":"选项简介",
//             "status":"0"
//         }",
	private String sort_order;//":"0",
	private String type;//":"1",
	private String rate;//":"77.78%"

	public String getOptionid() {
		return optionid;
	}

	public String getSubjectid() {
		return subjectid;
	}

	public Vote_OptionBean getOption() {
		return option;
	}

	public String getSort_order() {
		return sort_order;
	}

	public String getType() {
		return type;
	}

	public String getRate() {
		return rate;
	}

	public void setOptionid(String optionid) {
		this.optionid = optionid;
	}

	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	public void setOption(Vote_OptionBean option) {
		this.option = option;
	}

	public void setSort_order(String sort_order) {
		this.sort_order = sort_order;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

}

