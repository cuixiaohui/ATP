package com.atp.domain;


import com.atp.objectrerepository.BasePage;
import com.atp.objectrerepository.EditBox;
import com.atp.objectrerepository.IObject;
import com.atp.objectrerepository.TestObject;
import com.atp.utils.LogUtils;

public class UiModel {
	
	
	
	/**
	 * @author Cuixiaohui
	 * Method Name: enterValueInEditField
	 * Description: 在编辑框输入值
	 * Applicable Objects: Edit Box
	 * @param strObjectLogicalName: 逻辑上的对象名
	 * @param searchContent: 输入的值
	 * @return 如果输入成功返回true，如果失败返回false
	 * Example: enterValueInEditField("USERNAME","Tester")
	 * Applicable To:  数据驱动或关键字驱动
	 * 
	 * */
	public static boolean enterValueInEditField(String strObjectLogicalName, String searchContent)
	{
		boolean IsEnterValueInEditField = false;
		//从对象库中获取EditField对象
		IObject objectTmp = TestObject.getTestObject(strObjectLogicalName);
		//如果对象存在则输入值
		if(objectTmp.exist())
		{
			objectTmp.setValue("");
			objectTmp.setValue(searchContent);
			IsEnterValueInEditField = true;
		}
		//如果输入失败则记入结果中
		else
		{
			LogUtils.LogResultAndCaptureImage(strObjectLogicalName, "fail",strObjectLogicalName +"doesn't exist!");
		}
		return IsEnterValueInEditField;
	}

	/**
	 * @author Cuixiaohui
	 * Method Name: clickObject
	 * Description: 对象的单击操作
	 * Applicable Objects: Button, Image, Link 
	 * @param strObjectLogicalName: 逻辑上的对象名
	 * @return 如果输入成功返回true，如果失败返回false
	 * Example: clickObject("login_ok")
	 * Applicable To:  数据驱动或关键字驱动
	 * 
	 * */
	public static boolean clickObject(String strObjectLogicalName)
	{
		boolean IsClickObject = false;
		//从对象库中获取EditField对象
		IObject objectTmp = TestObject.getTestObject(strObjectLogicalName);
		//如果对象存在则输入值
		if(objectTmp.exist())
		{
			objectTmp.click();
			IsClickObject = true;
		}
		//如果输入失败则记入结果中
		else
		{
			LogUtils.LogResultAndCaptureImage(strObjectLogicalName, "fail",strObjectLogicalName +"doesn't exist!");
		}
		return IsClickObject;
	}
}
