/**
 * 
 */
package com.atp.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * @author cuixiaohui
 *
 */
public class PoiUtils {
	
	public static String filename = System.getProperty("user.dir")+"//data//TestData.xlsx";
	public String path;
	public FileInputStream fis = null;
	public FileOutputStream fos = null;
	private XSSFWorkbook workBook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;
	
	public PoiUtils(String path)
	{
		this.path = path;
		try{
			fis = new FileInputStream(path);
			workBook = new XSSFWorkbook(fis);
			sheet = workBook.getSheetAt(0);
			fis.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//returns the row count in a sheet
	public int getRowCount(String sheetName){
        int index = workBook.getSheetIndex(sheetName);
        if(index==-1)
            return 0;
        else{
            sheet = workBook.getSheetAt(index);
            int number=sheet.getLastRowNum()+1;
            return number;
        }
      }
	
	// returns the data from a cell
	public String getCellData(String sheetName,int colNum,int rowNum){
        try{
            if(rowNum <=0)
                return "";
            int index = workBook.getSheetIndex(sheetName);
            if(index==-1)
                return "";
            sheet = workBook.getSheetAt(index);
            row = sheet.getRow(rowNum-1);
            if(row==null)
                return "";
            cell = row.getCell(colNum);
            if(cell==null)
                return "";
            if(cell.getCellType()==Cell.CELL_TYPE_STRING)
                return cell.getStringCellValue();
            else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC || cell.getCellType()==Cell.CELL_TYPE_FORMULA ){
                String cellText  = String.valueOf(cell.getNumericCellValue());
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // format in form of M/D/YY
                    double d = cell.getNumericCellValue();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(HSSFDateUtil.getJavaDate(d));
                    cellText =
                            (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
                    cellText = cal.get(Calendar.MONTH)+1 + "/" +
                            cal.get(Calendar.DAY_OF_MONTH) + "/" +
                            cellText;
                    // System.out.println(cellText);
                }
                return cellText;
            }else if(cell.getCellType()==Cell.CELL_TYPE_BLANK)
                return "";
            else
                return String.valueOf(cell.getBooleanCellValue());
        }
        catch(Exception e){
            e.printStackTrace();
            return "row "+rowNum+" or column "+colNum +" does not exist  in xls";
        }
    }
	
	//returns the data from cell
	public String getCellData(Cell cell)
	{
		String value = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数值型
			if (DateUtil.isCellDateFormatted(cell)) {
				// 如果是date类型则 ，获取该cell的date值
				value = DateUtil.getJavaDate(cell.getNumericCellValue()).toString();
			} else {// 纯数字
				value = String.valueOf(cell.getNumericCellValue());
			}
			break;
		/* 此行表示单元格的内容为string类型 */
		case Cell.CELL_TYPE_STRING: // 字符串型
			value = cell.getRichStringCellValue().toString();
			break;
		case Cell.CELL_TYPE_FORMULA:// 公式型
			// 读公式计算值
			value = String.valueOf(cell.getNumericCellValue());
			if (value.equals("NaN")) {// 如果获取的数据值为非法值,则转换为获取字符串
				value = cell.getRichStringCellValue().toString();
			}
			// cell.getCellFormula();读公式
			break;
		case Cell.CELL_TYPE_BOOLEAN:// 布尔
			value = " " + cell.getBooleanCellValue();
			break;
		/* 此行表示该单元格值为空 */
		case Cell.CELL_TYPE_BLANK: // 空值
			value = "";
			break;
		case Cell.CELL_TYPE_ERROR: // 故障
			value = "";
			break;
		default:
			value = cell.getRichStringCellValue().toString();
		}
		return value;
	}
	
	// returns the data from a cell
    public String getCellData(String sheetName,String colName,int rowNum){
        try{
            if(rowNum <=0)
                return "";

            int index = workBook.getSheetIndex(sheetName);
            int col_Num=-1;
            if(index==-1)
                return "";

            sheet = workBook.getSheetAt(index);
            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){
                //System.out.println(row.getCell(i).getStringCellValue().trim());
                if(row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                    col_Num=i;
            }
            if(col_Num==-1)
                return "";

            sheet = workBook.getSheetAt(index);
            row = sheet.getRow(rowNum-1);
            if(row==null)
                return "";
            cell = row.getCell(col_Num);

            if(cell==null)
                return "";
            //System.out.println(cell.getCellType());
            if(cell.getCellType()==Cell.CELL_TYPE_STRING)
                return cell.getStringCellValue();
            else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC || cell.getCellType()==Cell.CELL_TYPE_FORMULA ){
                String cellText  = String.valueOf(cell.getNumericCellValue());
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // format in form of M/D/YY
                    double d = cell.getNumericCellValue();

                    Calendar cal =Calendar.getInstance();
                    cal.setTime(HSSFDateUtil.getJavaDate(d));
                    cellText =
                            (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
                    cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" +
                            cal.get(Calendar.MONTH)+1 + "/" +
                            cellText;

                    //System.out.println(cellText);

                }



                return cellText;
            }else if(cell.getCellType()==Cell.CELL_TYPE_BLANK)
                return "";
            else
                return String.valueOf(cell.getBooleanCellValue());

        }
        catch(Exception e){

            e.printStackTrace();
            return "row "+rowNum+" or column "+colName +" does not exist in xls";
        }
    }
	// returns true if data is set successfully else false
	
	public boolean setCellData(String sheetName,String colName,int rowNum, String data){
        try{
            fis = new FileInputStream(path);
            workBook = new XSSFWorkbook(fis);

            if(rowNum<=0)
                return false;

            int index = workBook.getSheetIndex(sheetName);
            int colNum=-1;
            if(index==-1)
                return false;


            sheet = workBook.getSheetAt(index);


            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){
                //System.out.println(row.getCell(i).getStringCellValue().trim());
                if(row.getCell(i).getStringCellValue().trim().equals(colName))
                    colNum=i;
            }
            if(colNum==-1)
                return false;

            sheet.autoSizeColumn(colNum);
            row = sheet.getRow(rowNum-1);
            if (row == null)
                row = sheet.createRow(rowNum-1);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            // cell style
            //CellStyle cs = workbook.createCellStyle();
            //cs.setWrapText(true);
            //cell.setCellStyle(cs);
            cell.setCellValue(data);

            fos = new FileOutputStream(path);

            workBook.write(fos);

            fos.close();

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	// returns true if data is set successfully else false
	public boolean setCellData(String sheetName,String colName,int rowNum, String data,String url){
        //System.out.println("setCellData setCellData******************");
        try{
            fis = new FileInputStream(path);
            workBook = new XSSFWorkbook(fis);

            if(rowNum<=0)
                return false;

            int index = workBook.getSheetIndex(sheetName);
            int colNum=-1;
            if(index==-1)
                return false;


            sheet = workBook.getSheetAt(index);
            //System.out.println("A");
            row=sheet.getRow(0);
            for(int i=0;i<row.getLastCellNum();i++){
                //System.out.println(row.getCell(i).getStringCellValue().trim());
                if(row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
                    colNum=i;
            }

            if(colNum==-1)
                return false;
            sheet.autoSizeColumn(colNum); //ashish
            row = sheet.getRow(rowNum-1);
            if (row == null)
                row = sheet.createRow(rowNum-1);

            cell = row.getCell(colNum);
            if (cell == null)
                cell = row.createCell(colNum);

            cell.setCellValue(data);
            XSSFCreationHelper createHelper = workBook.getCreationHelper();

            //cell style for hyperlinks
            //by default hypelrinks are blue and underlined
            CellStyle hlink_style = workBook.createCellStyle();
            XSSFFont hlink_font = workBook.createFont();
            hlink_font.setUnderline(XSSFFont.U_SINGLE);
            hlink_font.setColor(IndexedColors.BLUE.getIndex());
            hlink_style.setFont(hlink_font);
            //hlink_style.setWrapText(true);

            XSSFHyperlink link = createHelper.createHyperlink(XSSFHyperlink.LINK_FILE);
            link.setAddress(url);
            cell.setHyperlink(link);
            cell.setCellStyle(hlink_style);

            fos = new FileOutputStream(path);
            workBook.write(fos);

            fos.close();

        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
	
	// returns true if sheet is created successfully else false
    public boolean addSheet(String sheetname){

        FileOutputStream fileOut;
        try {
            workBook.createSheet(sheetname);
            fileOut = new FileOutputStream(path);
            workBook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
 // returns true if sheet is removed successfully else false if sheet does not exist
    public boolean removeSheet(String sheetName){
        int index = workBook.getSheetIndex(sheetName);
        if(index==-1)
            return false;

        FileOutputStream fileOut;
        try {
        	workBook.removeSheetAt(index);
            fileOut = new FileOutputStream(path);
            workBook.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // returns true if column is created successfully
    public boolean addColumn(String sheetName,String colName){
        //System.out.println("**************addColumn*********************");

        try{
            fis = new FileInputStream(path);
            workBook = new XSSFWorkbook(fis);
            int index = workBook.getSheetIndex(sheetName);
            if(index==-1)
                return false;

            XSSFCellStyle style = workBook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            sheet=workBook.getSheetAt(index);

            row = sheet.getRow(0);
            if (row == null)
                row = sheet.createRow(0);

            //cell = row.getCell();
            //if (cell == null)
            //System.out.println(row.getLastCellNum());
            if(row.getLastCellNum() == -1)
                cell = row.createCell(0);
            else
                cell = row.createCell(row.getLastCellNum());

            cell.setCellValue(colName);
            cell.setCellStyle(style);

            fos = new FileOutputStream(path);
            workBook.write(fos);
            fos.close();

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;


    }
    // removes a column and all the contents
    public boolean removeColumn(String sheetName, int colNum) {
        try{
            if(!isSheetExist(sheetName))
                return false;
            fis = new FileInputStream(path);
            workBook = new XSSFWorkbook(fis);
            sheet=workBook.getSheet(sheetName);
            XSSFCellStyle style = workBook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
            XSSFCreationHelper createHelper = workBook.getCreationHelper();
            style.setFillPattern(HSSFCellStyle.NO_FILL);



            for(int i =0;i<getRowCount(sheetName);i++){
                row=sheet.getRow(i);
                if(row!=null){
                    cell=row.getCell(colNum);
                    if(cell!=null){
                        cell.setCellStyle(style);
                        row.removeCell(cell);
                    }
                }
            }
            fos = new FileOutputStream(path);
            workBook.write(fos);
            fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }
    // find whether sheets exists
    public boolean isSheetExist(String sheetName){
        int index = workBook.getSheetIndex(sheetName);
        if(index==-1){
            index=workBook.getSheetIndex(sheetName.toUpperCase());
            if(index==-1)
                return false;
            else
                return true;
        }
        else
            return true;
    }

    // returns number of columns in a sheet
    public int getColumnCount(String sheetName){
        // check if sheet exists
        if(!isSheetExist(sheetName))
            return -1;

        sheet = workBook.getSheet(sheetName);
        row = sheet.getRow(0);

        if(row==null)
            return -1;

        return row.getLastCellNum();



    }
    //String sheetName, String testCaseName,String keyword ,String URL,String message
    public boolean addHyperLink(String sheetName,String screenShotColName,String testCaseName,int index,String url,String message){
        //System.out.println("ADDING addHyperLink******************");

        url=url.replace('\\', '/');
        if(!isSheetExist(sheetName))
            return false;

        sheet = workBook.getSheet(sheetName);

        for(int i=2;i<=getRowCount(sheetName);i++){
            if(getCellData(sheetName, 0, i).equalsIgnoreCase(testCaseName)){
                //System.out.println("**caught "+(i+index));
                setCellData(sheetName, screenShotColName, i+index, message,url);
                break;
            }
        }


        return true;
    }
    public int getCellRowNum(String sheetName,String colName,String cellValue){

        for(int i=2;i<=getRowCount(sheetName);i++){
            if(getCellData(sheetName,colName , i).equalsIgnoreCase(cellValue)){
                return i;
            }
        }
        return -1;

    }

//  traversal cell
	public void  traversalCell(String filePath) {
		try {
			Workbook workBook = null;
			try {
				workBook = new XSSFWorkbook(filePath); // 支持2007
			} catch (Exception ex) {
				workBook = new HSSFWorkbook(new FileInputStream(filePath)); // 支持2003及以前
			}

			// 获得Excel中工作表个数
			System.out.println("工作表个数 :" + workBook.getNumberOfSheets());

			// 循环每个工作表
			for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
				// 创建工作表
				Sheet sheet = workBook.getSheetAt(i);

				int rows = sheet.getPhysicalNumberOfRows(); // 获得行数
				
				System.out.println("工作表"+sheet.getSheetName()+" 行数 :" + sheet.getPhysicalNumberOfRows());
			
				if (rows > 0) {
					sheet.getMargin(Sheet.TopMargin);
					for (int r = 0; r < rows; r++) { // 行循环
						Row row = sheet.getRow(r);
						if (row != null) {

							int cells = row.getLastCellNum();// 获得列数
							for (short c = 0; c < cells; c++) { // 列循环
								Cell cell = row.getCell(c);

								if (cell != null) {
									String value = getCellData(cell);
									System.out.println("第" + r + "行 " + "第" + c + "列：" + value);
								}
							}
						}
					}
				}

				// 查询合并的单元格
				for (i = 0; i < sheet.getNumMergedRegions(); i++) {
					System.out.println("第" + i + "个合并单元格");
					CellRangeAddress region = sheet.getMergedRegion(i);
					int row = region.getLastRow() - region.getFirstRow() + 1;
					int col = region.getLastColumn() - region.getFirstColumn() + 1;
					System.out.println("起始行:" + region.getFirstRow());
					System.out.println("起始列:" + region.getFirstColumn());
					System.out.println("所占行:" + row);
					System.out.println("所占列:" + col);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public static void main(String[] args)
	{
		
		System.setProperty("WORKDIR", System.getProperty("user.dir"));
		String filePath = System.getProperty("user.dir")+"//data//TestData.xlsx";
		PoiUtils poi = new PoiUtils(filePath);
		poi.traversalCell(filePath);
	}

	public Object[][] getData(String caseName, String dataFile) {
		// TODO Auto-generated method stub
		Object[][] data = null;
		try {
			workBook = new XSSFWorkbook(dataFile);
			if(!isSheetExist("TestData"))
	            return null;

	        sheet = workBook.getSheet("TestData");
	        //获取表格总行数
	        int totalRows = getRowCount("TestData");
	        //获取表格总列数
	        int totalColumns = getColumnCount("TestData");
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public Object[][] getData(String caseName, String dataFile, int colNum) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object[][] getData(String caseName, String dataFile, int beginNum, int endNum) {
		// TODO Auto-generated method stub
		return null;
	}
}



//http://www.51testing.com/html/29/324829-858855.html