package com.atp.ui.testcase;

import org.testng.annotations.Test;

import com.atp.datadriver.bo.BusinessClassSample;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

public class BaiduSearch {
	
	private String searchContent = "";
	  @DataProvider
	  public Object[][] dp() {
	    return new Object[][] {
	      new Object[] { 1, "a" },
	      new Object[] { 2, "b" },
	    };
	  }
	  

  @BeforeMethod
  public void beforeMethod() {
	  //读取测试数据
  }
  
  @Test(dataProvider = "dp")
  public void f(Integer n, String s) {
	  BusinessClassSample bcs = new BusinessClassSample();
	  bcs.baiduSearch(searchContent);
  }
  
  @AfterMethod
  public void afterMethod() {
  }
 
}
