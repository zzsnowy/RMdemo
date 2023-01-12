package feature;


import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class ClassMetricsHandler extends MetricsHandler{

    String type;

    public ClassMetricsHandler(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {


        List<String[]> classMetricslists = readLabelDependenciesClassMetricsCsv(pro, commitId);
        List<String[]> entityClassMetricsList = new ArrayList<>();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String node1 = labelDependency.split("\t")[1];
            String node2 = labelDependency.split("\t")[2];

            String[] s1 = findClassMetricsByNode(pro, commitId, node1, classMetricslists);
            String[] s2 = findClassMetricsByNode(pro, commitId, node2, classMetricslists);

            if(s1 == null || s2 == null){
                if(s1 == null){
                    logger.info("commit = {}, {}, 类指标不存在", CommitUtil.getCoarseVer(pro, commitId), node1);
                }
                if(s2 == null){
                    logger.info("commit = {}, {}, 类指标不存在", CommitUtil.getCoarseVer(pro, commitId), node2);
                }

                labelDependency = br.readLine();
                continue;
            }

            String[] s = calEntityClassMetrics(labelDependency, s1, s2);

            entityClassMetricsList.add(s);

            labelDependency = br.readLine();
        }
        //writeCsv(pro, commitId, type, entityClassMetricsList);
    }


    private static String[] findClassMetricsByNode(String pro, String commitId, String node, List<String[]> classMetricslists) throws IOException {

        String className = node.split("/")[0];
        className = className.replace("__", "+");//im-server依赖的路径中存在__,原因是其文件名中有下划线，因此获取依赖时变成了两个_
        className = className.replace("_","/");
        className = className.replace("+","_");

        String fileName = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/" + pro + "/" + className;

        String[] classNameArray = fileName.split("/");
        String onlyClassName = classNameArray[classNameArray.length - 1].split("\\.")[0];

       // int num = 0;
        for (int i = 1; i < classMetricslists.size(); i++) {

            if(classMetricslists.get(i)[0].equals(fileName) && !classMetricslists.get(i)[1].contains("$")){
                String onlyClassNameTmp = classMetricslists.get(i)[1].split("\\.")[classMetricslists.get(i)[1].split("\\.").length - 1];
                if(onlyClassNameTmp.equals(onlyClassName)) {
                    return classMetricslists.get(i);
                    //num ++;
                }

            }

        }
/*
        if(num == 0){
            //System.out.println(pro + " 0 " + CommitUtil.getCoarseVer(pro, commitId) + " " + node + " " + fileName);
        }else if(num > 1){
            System.out.println(pro + " " + CommitUtil.getCoarseVer(pro, commitId) + " " + node + " " + fileName);
        }
*/
        return null;

    }

    private static String[] calEntityClassMetrics(String labelDependency, String[] s1, String[] s2) {

        String[] s = new String[49];
        int cnt = 0;

        for(int i = 3; i <= 51; i ++){

            if("NaN".equals(s1[i]) || "NaN".equals(s2[i])){
                s[cnt ++] = "NaN";
                continue;
            }

            double value = Math.abs(Double.parseDouble(s1[i]) - Double.parseDouble(s2[i]));
            s[cnt ++] = String.valueOf(value);
        }

        String[] d = new String[]{"\"" + labelDependency + "\""};
        s = ArrayUtils.addAll(d, s);

        return s;
    }

    private static List<String[]> readLabelDependenciesClassMetricsCsv(String pro, String commitId) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/metrics/" + pro + "/" + coarseCommitId + "/" + "class.csv";

        return CsvUtil.readCsv(path);
    }
}
