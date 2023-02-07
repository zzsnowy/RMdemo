package feature;

import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class MergeAllProFeatures {
    public static void main(String[] args) throws IOException{

        List<String[]> res = new ArrayList<>();
        String proListPath = "/Users/zzsnowy/IdeaProjects/RMdemo/src/main/resources/proList";

        File filename = new File(proListPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String pro = br.readLine();


        while (pro != null) {

            res.addAll(getDependencybyPro(pro));
            pro = br.readLine();
        }

        String outPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/features.csv";
        CsvUtil.writeCsv(res, "sheet0", outPath);
    }
    public static List<String[]> getDependencybyPro(String pro) throws IOException {

        logger.info("正在处理：{}", pro);

        List<String[]> res = new ArrayList<>();

        String commitIdPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MoveRefAtDepCommitId/" + pro + ".txt";

        File filename = new File(commitIdPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String commitId = br.readLine();

        while (commitId != null) {
            res.addAll(getDependencybyProAndCommidId(pro, commitId));
            commitId = br.readLine();
        }

        return res;
    }

    private static List<String[]> getDependencybyProAndCommidId(String pro, String commitId) throws IOException {
        return readLabelDependenciesLabelFeatureCsv(pro, commitId);
    }

    private static List<String[]> readLabelDependenciesLabelFeatureCsv(String pro, String commitId) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelFeature/features/" + pro + "/" + coarseCommitId + ".csv";

        return CsvUtil.readCsv(path);
    }



}
