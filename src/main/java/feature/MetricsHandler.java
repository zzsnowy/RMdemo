package feature;

import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.List;

import static feature.Main.logger;

public class MetricsHandler {

    public void readAndHandleLabelDependenciesData(String pro) throws IOException {

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

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException{
    }

    public void writeCsv(String pro, String commitId, String type, List<String[]> metricsList) throws IOException {

        String dirPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/feature/" + type + "/" + pro;

        File dir = new File(dirPath);
        dir.mkdir();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/feature/" + type + "/" + pro + "/"
                + CommitUtil.getCoarseVer(pro, commitId) + ".csv";

        // 写入csv 制表符消失
        CsvUtil.writeCsv(metricsList, "sheet0", path);

        logger.info("pro:{}, coarseCommitId:{}, {}Metrics写入成功", pro, CommitUtil.getCoarseVer(pro, commitId), type);

    }

    public static List<String[]> readLabelDependenciesMetricsCsv(String pro, String commitId, String level) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/metrics/" + pro + "/" + coarseCommitId + "/" + level + ".csv";

        return CsvUtil.readCsv(path);
    }


}
