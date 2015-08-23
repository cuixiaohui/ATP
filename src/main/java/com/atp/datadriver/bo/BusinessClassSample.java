package com.atp.datadriver.bo;

import com.atp.domain.UiModel;
import com.atp.objectrerepository.BasePage;

public class BusinessClassSample {

	
	/**
	 * @author Cuixiaohui
	 * Method Name: baiduSearch
	 * Description: 在百度搜索输入框输入test，进行搜索
	 * @param searchContent: 搜索框对象
	 * @param baiduyixia：百度一下按钮对象
	 * @return None
	 * Example: baiduSearch("test",)
	 * Applicable To:  数据驱动或关键字驱动
	 * 
	 * */
	public void baiduSearch(String searchContent)
	{
		//输入要搜索的内容为test
		if(!UiModel.enterValueInEditField(BasePage.searchEditBox,searchContent))
			return;
		//点击百度一下按钮
		if(!UiModel.clickObject(BasePage.search_btn))
			return;
	}
}
