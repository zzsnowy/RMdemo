package feature;

import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class FeatureCombiner extends MetricsHandler{

    String type;
    public static final String CLASS = "class";
    public static final String EVOLUTION = "evolution";
    public static final String LOC = "loc";
    public static final String USAGE = "usage";

    public FeatureCombiner(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException{
        List<String[]> classMetricsLists = readLabelDependenciesFeaturesCsv(pro, commitId, CLASS);
        List<String[]> evolutionMetricsLists = readLabelDependenciesFeaturesCsv(pro, commitId, EVOLUTION);
        List<String[]> locMetricsLists = readLabelDependenciesFeaturesCsv(pro, commitId, LOC);
        List<String[]> usageMetricsLists = readLabelDependenciesFeaturesCsv(pro, commitId, USAGE);
        List<String[]> featuresLists = combineByDependency(pro, commitId, classMetricsLists, evolutionMetricsLists, locMetricsLists, usageMetricsLists);
        writeCsv(pro, commitId, type, featuresLists);

        List<String[]> classLabelMetricsLists = readLabelDependenciesLabelFeaturesCsv(pro, commitId, CLASS);
        List<String[]> evolutionLabelMetricsLists = readLabelDependenciesLabelFeaturesCsv(pro, commitId, EVOLUTION);
        List<String[]> locLabelMetricsLists = readLabelDependenciesLabelFeaturesCsv(pro, commitId, LOC);
        List<String[]> usageLabelMetricsLists = readLabelDependenciesLabelFeaturesCsv(pro, commitId, USAGE);
        List<String[]> labelFeatureLists = combineByLabel(pro, commitId, classLabelMetricsLists, evolutionLabelMetricsLists, locLabelMetricsLists, usageLabelMetricsLists);
        writeIntoLabelFeatureCsv(pro, commitId, type, labelFeatureLists);
    }

    private List<String[]> combineByDependency(String pro, String commitId, List<String[]> classMetricsLists, List<String[]> evolutionMetricsLists, List<String[]> locMetricsLists, List<String[]> usageMetricsLists) {

        List<String[]> res = new ArrayList<>();

        int size = classMetricsLists.size();

        if(evolutionMetricsLists.size() != size || locMetricsLists.size() != size || usageMetricsLists.size() != size){
            logger.error("pro:{}, commitId:{}, 各级特征csv文件长度不同！",pro, commitId);
        }

        for(int i = 0; i < size; i ++){

            String labelDependency = classMetricsLists.get(i)[0];

            if(!evolutionMetricsLists.get(i)[0].equals(labelDependency) || !locMetricsLists.get(i)[0].equals(labelDependency) ||
                    !usageMetricsLists.get(i)[0].equals(labelDependency)){
                logger.error("pro:{}, commitId:{}, labelDependency:{}, 各级特征无法对应！",pro, commitId, labelDependency);
            }

            String[] e = ArrayUtils.subarray(evolutionMetricsLists.get(i), evolutionMetricsLists.get(i).length - 4, evolutionMetricsLists.get(i).length);
            String[] l = ArrayUtils.subarray(locMetricsLists.get(i), locMetricsLists.get(i).length - 1, locMetricsLists.get(i).length);
            String[] u = ArrayUtils.subarray(usageMetricsLists.get(i), usageMetricsLists.get(i).length - 1, usageMetricsLists.get(i).length);
            res.add(ArrayUtils.addAll(ArrayUtils.addAll(classMetricsLists.get(i), e),
                    ArrayUtils.addAll(l, u)));

        }

        return res;
    }
    private List<String[]> combineByLabel(String pro, String commitId, List<String[]> classMetricsLists, List<String[]> evolutionMetricsLists, List<String[]> locMetricsLists, List<String[]> usageMetricsLists) {

        List<String[]> res = new ArrayList<>();

        int size = classMetricsLists.size();

        if(evolutionMetricsLists.size() != size || locMetricsLists.size() != size || usageMetricsLists.size() != size){
            logger.error("pro:{}, commitId:{}, 各级特征csv文件长度不同！",pro, commitId);
        }

        for(int i = 0; i < size; i ++){

            String label = classMetricsLists.get(i)[0];

            if(!evolutionMetricsLists.get(i)[0].equals(label) || !locMetricsLists.get(i)[0].equals(label) ||
                    !usageMetricsLists.get(i)[0].equals(label)){
                logger.error("pro:{}, commitId:{}, labelDependency:{}, 各级特征无法对应！",pro, commitId, label);
            }

            String[] e = ArrayUtils.subarray(evolutionMetricsLists.get(i), evolutionMetricsLists.get(i).length - 4, evolutionMetricsLists.get(i).length);
            String[] l = ArrayUtils.subarray(locMetricsLists.get(i), locMetricsLists.get(i).length - 1, locMetricsLists.get(i).length);
            String[] u = ArrayUtils.subarray(usageMetricsLists.get(i), usageMetricsLists.get(i).length - 1, usageMetricsLists.get(i).length);
            res.add(ArrayUtils.addAll(ArrayUtils.addAll(classMetricsLists.get(i), e),
                    ArrayUtils.addAll(l, u)));

        }

        return res;
    }
    public List<String[]> readLabelDependenciesFeaturesCsv(String pro, String commitId, String type) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/feature/" + type + "/" + pro + "/" + coarseCommitId + ".csv";

        return CsvUtil.readCsv(path);
    }

    public List<String[]> readLabelDependenciesLabelFeaturesCsv(String pro, String commitId, String type) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelFeature/" + type + "/" + pro + "/" + coarseCommitId + ".csv";

        return CsvUtil.readCsv(path);
    }
    public void writeIntoLabelFeatureCsv(String pro, String commitId, String type, List<String[]> metricsList) throws IOException{
        String ldirPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelFeature/" + type + "/" + pro;

        File ldir = new File(ldirPath);
        ldir.mkdir();

        String lpath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelFeature/" + type + "/" + pro + "/"
                + CommitUtil.getCoarseVer(pro, commitId) + ".csv";

        CsvUtil.writeCsv(metricsList, "sheet0", lpath);

        logger.info("pro:{}, coarseCommitId:{}, {}Metrics写入LabelFeature目录成功", pro, CommitUtil.getCoarseVer(pro, commitId), type);
    }

}
