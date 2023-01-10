package feature;

import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class LocMetricsHandler extends MetricsHandler{

    String type;

    public LocMetricsHandler(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {

        List<String[]> locMetricslists = readLabelDependenciesLocMetricsCsv(pro, commitId);
        List<String[]> entityLocMetricsList = new ArrayList<>();

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String node1 = labelDependency.split("\t")[1];
            String node2 = labelDependency.split("\t")[2];

            String[] s1 = findLocMetricsByNode(pro, commitId, node1, locMetricslists);
            String[] s2 = findLocMetricsByNode(pro, commitId, node2, locMetricslists);

            if(s1 == null || s2 == null){

                //logger.info("commit:{}, {}实体Loc指标不存在", CommitUtil.getCoarseVer(pro, commitId), labelDependency);
                String[] s = new String[]{"\"" + labelDependency + "\"", "NaN"};
                entityLocMetricsList.add(s);
                labelDependency = br.readLine();
                continue;
            }

            String[] s = calEntityLocMetrics(labelDependency, s1, s2);

            entityLocMetricsList.add(s);

            labelDependency = br.readLine();
        }
        writeCsv(pro, commitId, type, entityLocMetricsList);
    }


    private List<String[]> readLabelDependenciesLocMetricsCsv(String pro, String commitId) throws IOException {

        String coarseCommitId = CommitUtil.getCoarseVer(pro, commitId);

        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/metrics/" + pro + "/" + coarseCommitId + "/" + "method.csv";

        List<String[]> lists = CsvUtil.readCsv(path);

        return lists;
    }

    private String[] findLocMetricsByNode(String pro, String commitId, String node, List<String[]> locMetricslists) {

        String[] s = new String[1];
        if(node.contains("[FE]")){
            s[0] = "1";
            return s;
        }

        String[] data = node.split("/");
        String className = data[0];
        className = className.replace("__", "+");//im-server依赖的路径中存在__,原因是其文件名中有下划线，因此获取依赖时变成了两个_
        className = className.replace("_","/");
        className = className.replace("+","_");

        String fileName = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/" + pro + "/" + className;

        String[] classNameArray = fileName.split("/");
        String onlyClassName = classNameArray[classNameArray.length - 1].split("\\.")[0];

        String methodName = data[data.length - 1].split("\\(")[0];

        for (int i = 1; i < locMetricslists.size(); i++) {

            if(locMetricslists.get(i)[0].equals(fileName) && !locMetricslists.get(i)[1].contains("$")){
                String onlyClassNameTmp = locMetricslists.get(i)[1].split("\\.")[locMetricslists.get(i)[1].split("\\.").length - 1];
                String methodNameTmp = locMetricslists.get(i)[2].split("/")[0];
                if(onlyClassNameTmp.equals(onlyClassName) && methodNameTmp.equals(methodName)) {
                    s[0] = locMetricslists.get(i)[11];
                    return s;
                }

            }

        }

        return null;
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
