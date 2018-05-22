package com.example.satellite.util.excel;

import com.example.satellite.util.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 读取Excel文件（支持“XLS”和“XLSX”格式）:需要加入对多个sheet的支持！！！
 * >>>>有时间加入通过sheetIndex的方法而不是sheetName获取sheet的方法
 * Created by Gaoxinwen on 2016/6/13.
 */
public class ReadExcel {
    private static Logger logger = LoggerFactory.getLogger(ReadExcel.class);

    /**
     * 工作簿对象
     */
    private Workbook wb;

    /**
     * 当前sheet
     */
    private Sheet sheet;

    /**
     * 第一个数据行号
     */
    private int firstDataRow;

    public ReadExcel(String fileName) throws IOException {
        this(new File(fileName));
    }

    public ReadExcel(File file) throws IOException {
        this(file.getName(), new FileInputStream(file));
    }

    public ReadExcel(MultipartFile multipartFile) throws IOException {
        this(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
    }

    public ReadExcel(String fileName, InputStream inputStream) throws IOException {
        if (this.wb == null) {
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("导入文档为空!");
            } else if (fileName.toLowerCase().endsWith("xls")) {
                wb = new HSSFWorkbook(inputStream);
            } else if (fileName.toLowerCase().endsWith("xlsx")) {
                wb = new XSSFWorkbook(inputStream);
            } else {
                throw new RuntimeException("文档格式不正确!");
            }
        }
        logger.debug("Initialize success.");
    }

    public <E> List<E> read(String sheetName,int firstDataRow, Class<E> cls)
            throws IllegalAccessException, InstantiationException {
        logger.debug("Start reading " + sheetName + ".");
        this.sheet = wb.getSheet(sheetName);
        this.firstDataRow = firstDataRow;
        if (this.sheet == null) {
            throw new IllegalArgumentException("The sheet " + sheetName + " does not exist!");
        }

        return getDataList(cls);
    }

    public <E> List<E> read(int sheetIndex,int firstDataRow, Class<E> cls)
            throws IllegalAccessException, InstantiationException {
        logger.debug("Start reading sheet " + sheetIndex + ".");
        this.sheet = wb.getSheetAt(sheetIndex);
        this.firstDataRow = firstDataRow;

        return getDataList(cls);
    }

    public Object[][] read(String sheetName,int firstDataRow) {
        logger.debug("Start reading " + sheetName + ".");
        this.sheet = wb.getSheet(sheetName);
        this.firstDataRow = firstDataRow;
        if (this.sheet == null) {
            throw new IllegalArgumentException("The sheet " + sheetName + " does not exist!");
        }

        int rowNum = this.getLastDataRowNum() - firstDataRow + 1;
        int columnNum = this.getLastCellNum();
        Object[][] result = new Object[rowNum][columnNum];

        for (int i = firstDataRow; i < rowNum; i++) {
            Row row = this.getRow(i);
            for (int j = 0; j < this.getLastCellNum(); j++) {
                result[i][j] = this.getCellValue(row, j);
            }
        }

        logger.debug("Read success!");
        return result;
    }

    public Object[][] read(int sheetIndex,int firstDataRow) {
        logger.debug("Start reading " + sheetIndex + ".");
        this.sheet = wb.getSheetAt(sheetIndex);
        this.firstDataRow = firstDataRow;

        int rowNum = this.getLastDataRowNum() - firstDataRow + 1;
        int columnNum = this.getLastCellNum();
        Object[][] result = new Object[rowNum][columnNum];

        for (int i = 0; i < rowNum; i++) {
            Row row = this.getRow(i + firstDataRow);
            for (int j = 0; j < this.getLastCellNum(); j++) {
                result[i][j] = this.getCellValue(row, j);
            }
        }

        logger.debug("Read success!");
        return result;
    }

    /**
     * 获取行对象
     *
     * @param rownum 行号
     * @return
     */
    public Row getRow(int rownum) {
        return this.sheet.getRow(rownum);
    }


    /**
     * 获取最后一个数据行号（遍历的时候要小于等于该值）
     *
     * @return
     */
    public int getLastDataRowNum() {
        return this.sheet.getLastRowNum();
    }

    /**
     * 获取最后一个列号
     * @return
     */
    public int getLastCellNum(){
        return this.getRow(firstDataRow).getLastCellNum();
    }

    /**
     * 获取单元格值
     *
     * @param row    获取的行
     * @param column 获取单元格列号
     * @return 单元格值
     */
    public Object getCellValue(Row row, int column) {
        Object val = "";
        try {
            Cell cell = row.getCell(column);
            if (cell != null) {
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val = cell.getDateCellValue();
                    } else {
                        val = cell.getNumericCellValue();
                    }
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    val = cell.getStringCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                    val = cell.getCellFormula();
                } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                    val = cell.getBooleanCellValue();
                } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
                    val = cell.getErrorCellValue();
                }
            }
        } catch (Exception e) {
            return val;
        }
        return val;
    }

    /**
     * 获取导入数据列表
     *
     * @param cls 导入对象类型
     */
    public <E> List<E> getDataList(Class<E> cls) throws InstantiationException, IllegalAccessException {
        List<Object[]> annotationList = new ArrayList<>();
        // Get annotation field
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null) {
                annotationList.add(new Object[]{ef, f});
            }
        }

        // Get excel data
        List<E> dataList = new ArrayList<>();
        for (int i = this.firstDataRow; i <= this.getLastDataRowNum(); i++) {
            E e = cls.newInstance();
            int column = 0;
            Row row = this.getRow(i);
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                Object val = this.getCellValue(row, column++);
                if (val != null) {
                    // Get param type and type cast
                    Class<?> valType = Class.class;
                    if (os[1] instanceof Field) {
                        valType = ((Field) os[1]).getType();
                    }
                    //log.debug("Import value type: ["+i+","+column+"] " + valType);
                    try {
                        if (valType == String.class) {
                            String s = String.valueOf(val.toString());
                            if (StringUtils.endsWith(s, ".0")) {
                                val = StringUtils.substringBefore(s, ".0");
                            } else {
                                val = String.valueOf(val.toString());
                            }
                        } else if (valType == Integer.class) {
                            val = Double.valueOf(val.toString()).intValue();
                        } else if (valType == Long.class) {
                            val = Double.valueOf(val.toString()).longValue();
                        } else if (valType == Double.class) {
                            val = Double.valueOf(val.toString());
                        } else if (valType == Float.class) {
                            val = Float.valueOf(val.toString());
                        } else if (valType == Date.class) {
                            val = DateUtil.getJavaDate((Double) val);
                        }
                    } catch (Exception ex) {
                        logger.info("Get cell value [" + i + "," + column + "] error: " + ex.toString());
                        val = null;
                    }
                    // set entity value
                    if (os[1] instanceof Field) {
                        Reflections.invokeSetter(e, ((Field) os[1]).getName(), val);
                    }
                }
                sb.append(val).append(", ");
            }
            dataList.add(e);
            logger.debug("Read success: [" + i + "] " + sb.toString());
        }

        logger.debug("Read success!");
        return dataList;
    }

//    public static void main(String[] args) throws Exception {
//        ReadExcel readExcel = new ReadExcel("C:\\Users\\Dell Precision\\Desktop\\uploadOutput.xlsx");
//        Object[][] objects = readExcel.read(0, 1);
//        double[] output = new double[objects.length];
//        for (int i = 0; i < output.length; i++) {
//            output[i] = Double.parseDouble(objects[i][1].toString());
//        }
//        System.out.println(Arrays.toString(output));
//
//    }
}
