package com.example.satellite.util.excel;

import com.example.satellite.util.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 导出Excel文件（导出“XLSX”格式，支持大数据量导出）
 * Created by Gaoxinwen on 2016/6/13.
 */
public class WriteExcel {
    private static Logger logger = LoggerFactory.getLogger(WriteExcel.class);

    /**
     * 工作薄对象
     */
    private SXSSFWorkbook wb;

    /**
     * 当前sheet
     */
    private List<Sheet> sheetList = new ArrayList<>();

    /**
     * 样式列表
     */
    private Map<String, CellStyle> styles = new HashMap<>();

    /**
     * 当前行号
     */
    private int rowIndex;

    /**
     * 当前注解列表（Object[]{ ExcelField, Field }）
     */
    private List<Object[]> annotationList = new ArrayList<>();

    public WriteExcel() {
    }

    /**
     * 构造函数
     *
     * @param title     表格标题，传“空值”，表示无标题
     * @param headers   表头数组
     * @param sheetName sheet名称
     */
    public void write(String title, String[] headers, String sheetName) {
        write(title, Arrays.asList(headers), sheetName);
    }

    /**
     * 构造函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头列表
     * @param sheetName  sheet名称
     */
    public void write(String title, List<String> headerList, String sheetName) {
        initialize(title, headerList, sheetName);
    }

    /**
     * 导出函数：针对使用ExcelField注解的实体类
     *
     * @param title     表格标题，传“空值”，表示无标题
     * @param cls       实体对象，通过annotation.ExportField获取每列的表头
     * @param sheetName sheet名称
     */
    public <E> void write(String title, Class<?> cls, String sheetName) {
        if (!annotationList.isEmpty()) {
            annotationList.clear();
        }
        // Get annotation field
        Field[] fs = cls.getDeclaredFields();
        for (Field f : fs) {
            ExcelField ef = f.getAnnotation(ExcelField.class);
            if (ef != null) {
                annotationList.add(new Object[]{ef, f});
            }
        }

        // Initialize
        List<String> headerList = new ArrayList<>();
        for (Object[] os : annotationList) {
            // 实体类当value属性不为空时，header为value,否则为Field的name
            String t = (!Objects.equals(((ExcelField) os[0]).value(), StringUtils.EMPTY)) ? ((ExcelField) os[0]).value()
                    : ((Field) os[1]).getName();
            headerList.add(t);
        }
        initialize(title, headerList, sheetName);
    }


    /**
     * 初始化函数
     *
     * @param title      表格标题，传“空值”，表示无标题
     * @param headerList 表头列表
     * @param sheetName  sheet名称
     */
    private void initialize(String title, List<String> headerList, String sheetName) {
        if (this.wb == null) {
            this.wb = new SXSSFWorkbook(500);
        }
        SXSSFSheet sheet = wb.createSheet(sheetName);
//        sheet.trackAllColumnsForAutoSizing(); //do this immediately

        sheetList.add(sheet);
        this.styles = createStyles(wb);
        // 每次初始化的时候都将rowIndex赋值为0
        this.rowIndex = 0;
        // Create title
        if (StringUtils.isNotBlank(title)) {
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(styles.get("title"));
            titleCell.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(),
                    titleRow.getRowNum(), titleRow.getRowNum(), headerList.size() - 1));
        }
        // Create header
        if (headerList == null) {
            throw new RuntimeException("headerList must not null!");
        }
        Row headerRow = sheet.createRow(rowIndex++);
        headerRow.setHeightInPoints(16);
        for (int i = 0; i < headerList.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(styles.get("header"));
            String[] ss = StringUtils.split(headerList.get(i), "**", 2);
            if (ss.length == 2) {
                cell.setCellValue(ss[0]);
                Comment comment = sheet.createDrawingPatriarch().createCellComment(
                        new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                comment.setString(new XSSFRichTextString(ss[1]));
                cell.setCellComment(comment);
            } else {
                cell.setCellValue(headerList.get(i));
            }
//            sheet.autoSizeColumn(i);
        }
//        for (int i = 0; i < headerList.size(); i++) {
//            int colWidth = sheet.getColumnWidth(i) * 2;
////            sheet.setColumnWidth(i, colWidth < 1000 ? 1000 : colWidth);
//            sheet.setColumnWidth(i, colWidth);
//        }
//        sheet.untrackAllColumnsForAutoSizing();
        logger.debug("Initialize success.");
    }

    /**
     * 创建表格样式
     *
     * @param wb 工作薄对象
     * @return 样式列表
     */
    private Map<String, CellStyle> createStyles(Workbook wb) {
        // style of the title
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font titleFont = wb.createFont();
        titleFont.setFontName("Arial");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(titleFont);
        styles.put("title", style);

        style = wb.createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        Font dataFont = wb.createFont();
        dataFont.setFontName("Arial");
        dataFont.setFontHeightInPoints((short) 10);
        style.setFont(dataFont);
        style.setWrapText(true);
        styles.put("data", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("data1", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("data2", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        styles.put("data3", style);

        style = wb.createCellStyle();
        style.cloneStyleFrom(styles.get("data"));
        // 设置自动换行
//		style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headerFont = wb.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 10);
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(headerFont);
        styles.put("header", style);

        return styles;
    }

    /**
     * 添加一行
     *
     * @return 行对象
     */
    public Row addRow() {
        return sheetList.get(sheetList.size() - 1).createRow(rowIndex++);
    }


    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val) {
        return this.addCell(row, column, val, 0, Class.class);
    }

    /**
     * 添加一个单元格
     *
     * @param row    添加的行
     * @param column 添加列号
     * @param val    添加值
     * @param align  对齐方式（1：靠左；2：居中；3：靠右）
     * @return 单元格对象
     */
    public Cell addCell(Row row, int column, Object val, int align, Class<?> fieldType) {
        Cell cell = row.createCell(column);
        CellStyle style = styles.get("data" + (align >= 1 && align <= 3 ? align : ""));
        try {
            if (val == null) {
                cell.setCellValue("");
            } else if (val instanceof String) {
                cell.setCellValue((String) val);
            } else if (val instanceof Integer) {
                cell.setCellValue((Integer) val);
            } else if (val instanceof Long) {
                cell.setCellValue((Long) val);
            } else if (val instanceof Double) {
                cell.setCellValue((Double) val);
            } else if (val instanceof Float) {
                cell.setCellValue((Float) val);
            } else if (val instanceof Date) {
                DataFormat format = wb.createDataFormat();
                style.setDataFormat(format.getFormat("yyyy-MM-dd"));
                cell.setCellValue((Date) val);
            } else {
                if (fieldType != Class.class) {
                    cell.setCellValue((String) fieldType.getMethod("setValue", Object.class).invoke(null, val));
                } else {
                    cell.setCellValue((String) Class.forName(this.getClass().getName().replaceAll(this.getClass().getSimpleName(),
                            "fieldtype." + val.getClass().getSimpleName() + "Type")).getMethod("setValue", Object.class).invoke(null, val));
                }
            }
        } catch (Exception ex) {
            logger.info("Set cell value [" + row.getRowNum() + "," + column + "] error: " + ex.toString());
            if (val != null) {
                cell.setCellValue(val.toString());
            }
        }
        cell.setCellStyle(style);
        return cell;
    }

    /**
     * 添加数据（通过annotation.ExportField添加数据）
     *
     * @return list 数据列表
     */
    public <E> WriteExcel setDataList(List<E> list) {
        int index = 0;
        for (E e : list) {
            int colunm = 0;
            Row row = this.addRow();
            StringBuilder sb = new StringBuilder();
            for (Object[] os : annotationList) {
                ExcelField ef = (ExcelField) os[0];
                Object val;
                // Get entity value
                try {
                    val = Reflections.invokeGetter(e, ((Field) os[1]).getName());
                } catch (Exception ex) {
                    // Failure to ignore
                    logger.info(ex.toString());
                    val = "";
                }
                this.addCell(row, colunm++, val, ef.align(), ef.fieldType());
                sb.append(val).append(", ");
            }
            logger.debug("Write success: [" + ++index + "] " + sb.toString());
        }
        return this;
    }

    /**
     * 输出数据流
     *
     * @param os 输出数据流
     */
    public WriteExcel write(OutputStream os) throws IOException {
        wb.write(os);
        return this;
    }

    /**
     * 输出到文件
     *
     * @param fileName 输出文件名
     */
    public WriteExcel writeFile(String fileName) throws IOException {
        FileOutputStream os = new FileOutputStream(fileName);
        this.write(os);
        return this;
    }

    /**
     * 清理临时文件
     */
    public WriteExcel dispose() {
        wb.dispose();
        return this;
    }


    /**
     * 导出测试
     */
//    public static void main(String[] args) throws Throwable {
//
//        List<String> headerList = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            headerList.add("表头"+i);
//        }
//
//        List<String> dataRowList = new ArrayList<>();
//        for (int i = 1; i <= headerList.size(); i++) {
//            dataRowList.add("数据"+i);
//        }
//
//        List<List<String>> dataList = new ArrayList<>();
//        for (int i = 1; i <=100; i++) {
//            dataList.add(dataRowList);
//        }
//
//        WriteExcel ee = new WriteExcel();
//
//        ee.write("表格标题", headerList, "aaa");
//
//        for (int i = 0; i < dataList.size(); i++) {
//            Row row = ee.addRow();
//            for (int j = 0; j < dataList.get(i).size(); j++) {
//                ee.addCell(row, j, dataList.get(i).get(j));
//            }
//        }
//
//
//        Employee e1 = new Employee("Bob1", 23, "HUST");
//        Employee e2 = new Employee("Bob2", 23, "HUST");
//        Employee e3 = new Employee("Bob3", 23, "HUST");
//        Employee e4 = new Employee("Bob4", 23, "HUST");
//        Employee e5 = new Employee("Bob5", 23, "HUST");
//        Employee e6 = new Employee("Bob6", 23, "HUST");
//        List<Employee> list = new ArrayList<>(Arrays.asList(e1,e2,e3,e4,e5,e6));
//
//        ee.write("表格标题", Employee.class, "bbb");
//
//        ee.setDataList(list);
//
//        ee.writeFile("C:\\Users\\Dell Precision\\Desktop\\eee.xlsx");
//
//        ee.dispose();
//
//        logger.debug("Export success.");
//
//    }

}
