package com.example.satellite.economicaloperation;

/**
 * Description:
 * Created by Gaoxinwen on 2016/6/18.
 */
public class UnitCommitment {

//    public static void test(HydroStation station, EotableRepository eotableRepository) {
//        /*
//         *  First step: 获取每日负荷
//         */
//        ReadExcel ie = new ReadExcel();
//        try {
//            ie.read("C:/Users/Administrator/Desktop/每日负荷.xlsx", 0, );
//        } catch (InvalidFormatException | IOException e) {
//            e.printStackTrace();
//        }
//        List<Double> dailyLoad = new ArrayList<>();
//        List<Double> inflow = new ArrayList<>();
//        // 小于等于最后一行！！！
//        for (int i = 0; i <= ie.getLastDataRowNum(); i++) {
//            Row row = ie.getRow(i);
//            dailyLoad.add((Double) ie.getCellValue(row, 1));
//            inflow.add((Double) ie.getCellValue(row, 2));
//        }
//
//        System.out.println(dailyLoad);
//
//        /*
//         *  Second step: 设定参数
//         */
//        // 2015年8月15日的数据
//        // 初始水位
//        double beginLevel = 64.17;
//        // 水位上下限
//        double minLevel = 50.0;
//        double maxLevel = 65.0;
//
//        /*
//         *  开始计算
//         */
//
//        int periodCount = 24;
//        int[] schedule = {1, 1, 1, 1, 1, 1};
//        double[] loads = ListUtils.listToArray(dailyLoad);
//        double[] inflows = ListUtils.listToArray(inflow);
////        List<Node> nodeList = DPUC.unitCommitment(periodCount, loads, inflows, beginLevel, schedule, station,
////                eotableRepository);
//
//
//
//        // 输出结果
////        WriteExcel ee = new WriteExcel();
////        String[] headers = {"时段", "末水位", "入库", "出库", "总负荷", "1#出力", "2#出力", "3#出力", "4#出力", "5#出力",
////                "6#出力", "总耗流量", "1#流量", "2#流量", "3#流量", "4#流量", "5#流量", "6#流量", "水头"};
////        ee.write("", headers, "负荷分配结果");
////        for (int i = 0; i < dailyLoad.size(); i++) {
////            Row row = ee.addRow();
////            ee.addCell(row, 0, i + 1);
////            ee.addCell(row, 1, upLevels[i + 1]);
////            ee.addCell(row, 2, inflow.get(i));
////            ee.addCell(row, 3, outflows[i]);
////            ee.addCell(row, 4, dailyLoad.get(i));
////            ee.addCell(row, 5, eotables[i].getN1());
////            ee.addCell(row, 6, eotables[i].getN2());
////            ee.addCell(row, 7, eotables[i].getN3());
////            ee.addCell(row, 8, eotables[i].getN4());
////            ee.addCell(row, 9, eotables[i].getN5());
////            ee.addCell(row, 10, eotables[i].getN6());
////            ee.addCell(row, 11, eotables[i].getSumQ());
////            ee.addCell(row, 12, eotables[i].getQ1());
////            ee.addCell(row, 13, eotables[i].getQ2());
////            ee.addCell(row, 14, eotables[i].getQ3());
////            ee.addCell(row, 15, eotables[i].getQ4());
////            ee.addCell(row, 16, eotables[i].getQ5());
////            ee.addCell(row, 17, eotables[i].getQ6());
////            ee.addCell(row, 18, heads[i]);
////        }
////        try {
////            ee.writeFile("C:/Users/Administrator/Desktop/result.xlsx");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        ee.dispose();
//    }


}
