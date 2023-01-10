package feature;

import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class EvolutionMetricsHandler {

    static void readAndHandleLabelDependenciesData(String pro) throws IOException {

        logger.info("正在处理：{}", pro);

        String commitIdPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MoveRefAtDepCommitId/" + pro + ".txt";

        File filename = new File(commitIdPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String commitId = br.readLine();

        while (commitId != null) {
            readAndHandleLabelDependenciesDataByCommitId(pro, commitId);
            commitId = br.readLine();
        }

    }

    private static void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {

        logger.info("正在处理：{}", commitId);

        List<String[]> evolutionMetricslists = readEvolutionMetricslists(pro, commitId);

        List<String[]> entityEvolutionMetricsList = new ArrayList<>();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String[] s = findEvolutionMetricsByDependency(pro, commitId, labelDependency, evolutionMetricslists);
            entityEvolutionMetricsList.add(s);
            labelDependency = br.readLine();
        }
        writeCsv(pro, commitId, entityEvolutionMetricsList);
    }

    private static List<String[]> readEvolutionMetricslists(String pro, String commitId) throws IOException {

        List<String[]> evolutionMetricslists = new ArrayList<>();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/dependencies/" + pro + "/" + pro + "_" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String dependency = br.readLine();

        while (dependency != null) {

            evolutionMetricslists.add(dependency.split("\t"));
            dependency = br.readLine();
        }
        return evolutionMetricslists;
    }

    private static String[] findEvolutionMetricsByDependency(String pro, String commitId, String labelDependency, List<String[]> evolutionMetricslists) throws IOException {

        String[] s = new String[5];
        s[0] = "\"" + labelDependency + "\"";

        String node1 = labelDependency.split("\t")[1];
        String node2 = labelDependency.split("\t")[2];

        int num = 0;

        boolean flag = false;
        for (int i = 0; i < evolutionMetricslists.size(); i++) {

            String[] data = evolutionMetricslists.get(i);
            if((data[0].equals(node1) && data[1].equals(node2)) ||
                    (data[1].equals(node1) && data[0].equals(node2))){
                if(!flag){
                    s[1] = data[2];
                    s[2] = data[3];
                    s[4] = data[5];
                    flag = true;
                } else {
                    s[3] = data[3];
                    if(!data[2].equals(s[1]) || !data[5].equals(s[4])){
                        System.out.println("错误：" + labelDependency);
                    }
                }
                num ++;
            }

        }

        if(num == 1){
            System.out.println(num + " " + labelDependency);
        }

        //2 3 5
        return s;
    }


    private static void writeCsv(String pro, String commitId, List<String[]> entityEvolutionMetricsList) throws IOException {

        String dirPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/feature/evolution/" + pro;

        File dir = new File(dirPath);
        dir.mkdir();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/feature/evolution/" + pro + "/"
                + CommitUtil.getCoarseVer(pro, commitId) + ".csv";

        // 写入csv 制表符消失
        CsvUtil.writeCsv(entityEvolutionMetricsList, "sheet0", path);

        logger.info("pro:{}, coarseCommitId:{}, evolutionMetrics写入成功", pro, commitId);

    }
}
