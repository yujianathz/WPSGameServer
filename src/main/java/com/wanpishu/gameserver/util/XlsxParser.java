package com.wanpishu.gameserver.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.wanpishu.gameserver.db.Purchase;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class XlsxParser {
	public final static Map<Integer, Purchase> PurchaseDatas = new HashMap<Integer, Purchase>();
	
	public static void parsePurchaseData() throws Exception {
		Workbook book = Workbook.getWorkbook(new File("conf/AllData.xls"));
		Sheet sheet = book.getSheet(0);
		
		int rows = sheet.getRows();
		for (int i = 2; i < rows; i++){
			Cell[] row = sheet.getRow(i);
			
			int type = Integer.valueOf(row[0].getContents());
			int price = Integer.valueOf(row[2].getContents());
			int gold = Integer.valueOf(row[4].getContents());
			int wave = Integer.valueOf(row[5].getContents());
			int shield = Integer.valueOf(row[6].getContents());
			int stop = Integer.valueOf(row[7].getContents());
			int bomb = Integer.valueOf(row[8].getContents());
			int tip = Integer.valueOf(row[9].getContents());
			Purchase p = new Purchase(type, price, gold, wave, shield, stop, bomb, tip);
			PurchaseDatas.put(type, p);
		}
		book.close();
	}
}
