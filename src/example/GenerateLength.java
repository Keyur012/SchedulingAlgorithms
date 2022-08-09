package example;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GenerateLength {
	
	public static void main(String[] args) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet("Input");
		
		XSSFRow row = spreadsheet.createRow(0);
		row.createCell(0).setCellValue("Length");
		
		for(int i = 1;i<=10;i++) {
			Random rand = new Random();
			int len = rand.nextInt(1, 5)*1000 + rand.nextInt(0, 10)*100 + rand.nextInt(0, 10); 
			spreadsheet.createRow(i).createCell(0).setCellValue(len);
		}
		
		XSSFSheet spreadsheet2 = workbook.createSheet("Output");
		
		spreadsheet2.createRow(0).createCell(0).setCellValue("Number Of Cloudlet");
		for(int i = 1;i<=10;i++) {
			spreadsheet2.createRow(i).createCell(0).setCellValue(i);
		}
		
		FileOutputStream out = new FileOutputStream(
	            new File("E:/Asem8/cloud/input.xlsx"));
		workbook.write(out);
		workbook.close();
		out.close();
		System.out.println("Completed");
	}

}
