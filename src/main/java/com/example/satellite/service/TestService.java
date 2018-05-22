package com.example.satellite.service;

import com.example.satellite.controller.InputCriteria;
import com.example.satellite.domain.Eotable;
import com.example.satellite.domain.Report;
import com.example.satellite.economicaloperation.DPUC;
import com.example.satellite.economicaloperation.Node;
import com.example.satellite.repository.EotableRepository;
import com.example.satellite.repository.ReportRepository;
import com.example.satellite.station.HydroStation;
import com.example.satellite.util.MathUtils;
import com.example.satellite.util.excel.WriteExcel;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:
 * Created by Gaoxinwen on 2016/9/29.
 */
@Service
public class TestService {

    @Autowired
    private EotableRepository eotableRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private HydroStation zhelin;

    public List<Node> test(InputCriteria input) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(input.getDate());

        List<List<Eotable>> allEotableList = new ArrayList<>();

        double[] heads = MathUtils.discreteToArray(27, 43, 0.5);
        for (double head : heads) {
            List<Eotable> eotableList = eotableRepository.findByHead(head);
            allEotableList.add(eotableList);
        }

        int periodCount = 96;

//        double[] load = {217, 194, 187, 214, 246, 241, 250, 213, 241, 213, 190, 220, 188, 250, 243, 191, 204, 252,
//                211, 206, 237, 186, 244, 238};
//        double[] loadnew = new double[96];
//        double[] inflow = new double[periodCount];
//        for (int i = 0; i < periodCount; i++) {
//            loadnew[i] = load[i / 4];
//            inflow[i] = 245.6;
//        }
//        double beginLevel = 64.17;
//        int[] schedule = {1, 1, 1, 1, 1, 1};

//        // PSO算法
//        PSOProcess psoProcess = new PSOProcess();
//        List<Node> nodeList = psoProcess.execute(inflow, beginLevel, load, allEotableList, zhelin);
//
//        // HS算法
//        HSProcess hsProcess = new HSProcess();
//        List<Node> nodeList = hsProcess.execute(inflow, beginLevel, load, allEotableList, zhelin);

        // DP
        List<Node> nodeList = DPUC.unitCommitment(periodCount, input.getOutput(), input.getInflow(), input.getInitialLevel(),
                input.getSchedule(),
                zhelin,
                allEotableList);




        // 输出结果
        WriteExcel ee = new WriteExcel();
        String[] headers = {"时段", "出力", "水位", "水头", "入库流量", "发电流量", "弃水",
                "#1出力", "#2出力", "#3出力", "#4出力", "#5出力", "#6出力", "#1流量", "#2流量", "#3流量", "#4流量", "#5流量", "#6流量"};
        ee.write(date + "厂内经济运行结果", headers, "结果");
        for (int i = 0; i < nodeList.size(); i++) {
            Row row = ee.addRow();
            ee.addCell(row, 0, i + 1);
            ee.addCell(row, 1, MathUtils.getSum(nodeList.get(i).getN()));
            if (i == 0) {
                ee.addCell(row, 2, input.getInitialLevel());
            } else {
                ee.addCell(row, 2, nodeList.get(i - 1).getFinalLevel());
            }
            ee.addCell(row, 3, nodeList.get(i).getHead());
            ee.addCell(row, 4, input.getInflow()[i]);
            ee.addCell(row, 5, MathUtils.getSum(nodeList.get(i).getQ()));
            ee.addCell(row, 6, nodeList.get(i).getAflow());
            // 机组出力
            for (int j = 0; j < 6; j++) {
                ee.addCell(row, j + 7, nodeList.get(i).getN()[j]);
            }
            // 机组耗流
            for (int j = 0; j < 6; j++) {
                ee.addCell(row, j + 13, nodeList.get(i).getQ()[j]);
            }

        }
        Row row = ee.addRow();
        ee.addCell(row, 2, nodeList.get(nodeList.size() - 1).getFinalLevel());

//        try {
//            ee.writeFile("C:\\Users\\Dell Precision\\Desktop\\result.xlsx");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 每次计算存储在数据库中
        Report report = new Report();
        report.setDispatchDate(input.getDate());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        report.setProducer(auth.getName());

        report.setName("柘林水电厂厂内经济运行结果" + format.format(report.getDispatchDate()) + ".xlsx");

        report.setCreateDate(new Date());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ee.write(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ee.dispose();
        //数据在bos中
        byte[] bytes = bos.toByteArray();
        report.setFile(bytes);
        reportRepository.save(report);


        return nodeList;
    }


}
