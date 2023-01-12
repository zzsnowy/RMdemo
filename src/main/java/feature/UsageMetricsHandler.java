package feature;

import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class UsageMetricsHandler extends MetricsHandler{
    String type;

    public UsageMetricsHandler(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {

        List<String[]> usageMethodMetricslists = readLabelDependenciesMethodUsageMetricsCsv(pro, commitId);
        List<String[]> usageFieldMetricslists = readLabelDependenciesFieldUsageMetricsCsv(pro, commitId);
        List<String[]> entityUsageMetricsList = new ArrayList<>();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String node1 = labelDependency.split("\t")[1];
            String node2 = labelDependency.split("\t")[2];

            String[] s1 = findUsageMetricsByNode(pro, commitId, node1, usageMethodMetricslists, usageFieldMetricslists);
            String[] s2 = findUsageMetricsByNode(pro, commitId, node2, usageMethodMetricslists, usageFieldMetricslists);

            if(s1 == null || s2 == null){

                //logger.info("commit:{}, {}实体Loc指标不存在", CommitUtil.getCoarseVer(pro, commitId), labelDependency);
                String[] s = new String[]{"\"" + labelDependency + "\"", "NaN"};
                entityUsageMetricsList.add(s);
                labelDependency = br.readLine();
                continue;
            }

            String[] s = calEntityLocMetrics(labelDependency, s1, s2);

            entityUsageMetricsList.add(s);

            labelDependency = br.readLine();
        }
        writeCsv(pro, commitId, type, entityUsageMetricsList);
    }




    private List<String[]> readLabelDependenciesMethodUsageMetricsCsv(String pro, String commitId) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/metrics/" + pro + "/" + coarseCommitId + "/" + "method.csv";

        return CsvUtil.readCsv(path);
    }

    private List<String[]> readLabelDependenciesFieldUsageMetricsCsv(String pro, String commitId) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/metrics/" + pro + "/" + coarseCommitId + "/" + "field.csv";

        return CsvUtil.readCsv(path);
    }
    private String[] findUsageMetricsByNode(String pro, String commitId, String node, List<String[]> usageMethodMetricslists, List<String[]> usageFieldMetricslists) throws IOException {

        // method 和 field 都有可能找不到对应指标
        if(node.matches("[\\s\\S]*\\[MT][\\s\\S]*")){
            return findMethodUsageMetricsByNode(pro, commitId, node, usageMethodMetricslists);
        } else if(node.matches("[\\s\\S]*\\[FE][\\s\\S]*")){
            return findFieldUsageMetricsByNode(pro, commitId, node, usageFieldMetricslists);
        } else{
            logger.error("{}查找使用次数出错！", node);
        }
        return null;
    }

    private String[] findMethodUsageMetricsByNode(String pro, String commitId, String node, List<String[]> usageMethodMetricslists) throws IOException {

        String[] data = node.split("/");
        String className = data[0];
        className = className.replace("__", "+");//im-server依赖的路径中存在__,原因是其文件名中有下划线，因此获取依赖时变成了两个_
        className = className.replace("_","/");
        className = className.replace("+","_");

        String fileName = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/" + pro + "/" + className;

        String methodName = data[data.length - 1].split("\\(")[0];

        for (int i = 1; i < usageMethodMetricslists.size(); i++) {

            if(usageMethodMetricslists.get(i)[0].equals(fileName)){
                String methodNameTmp = usageMethodMetricslists.get(i)[2].split("/")[0];
                if(methodNameTmp.equals(methodName)) {
                    return new String[]{usageMethodMetricslists.get(i)[15] + usageMethodMetricslists.get(i)[16]
                    + usageMethodMetricslists.get(i)[17]};
                }

            }

        }

        return null;
    }

    private String[] findFieldUsageMetricsByNode(String pro, String commitId, String node, List<String[]> usageFieldMetricslists) throws IOException {

        String[] data = node.split("/");
        String className = data[0];
        className = className.replace("__", "+");//im-server依赖的路径中存在__,原因是其文件名中有下划线，因此获取依赖时变成了两个_
        className = className.replace("_","/");
        className = className.replace("+","_");

        String fileName = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/" + pro + "/" + className;

        String fieldName = data[data.length - 1];

        int sum = 0;
        boolean flag = false;
        for (int i = 1; i < usageFieldMetricslists.size(); i++) {

            if(usageFieldMetricslists.get(i)[0].equals(fileName)){
                String fieldNameTmp = usageFieldMetricslists.get(i)[3];
                if(fieldNameTmp.equals(fieldName)) {
                    flag = true;
                    sum += Integer.parseInt(usageFieldMetricslists.get(i)[4]);
                }

            }

        }

        if(flag){
            return new String[]{String.valueOf(sum)};
        } else {
            //System.out.println(pro + " 0 " + CommitUtil.getCoarseVer(pro, commitId) + " " + node + " " + fileName);
            return null;
        }

    }

    private String[] calEntityLocMetrics(String labelDependency, String[] s1, String[] s2) {
        String[] s = new String[1];

        if("NaN".equals(s1[0]) || "NaN".equals(s2[0])){
            s[0] = "NaN";
        } else {
            double value = Math.abs(Double.parseDouble(s1[0]) - Double.parseDouble(s2[0]));
            s[0] = String.valueOf(value);
        }

        String[] d = new String[]{"\"" + labelDependency + "\""};
        return ArrayUtils.addAll(d, s);
    }


}
