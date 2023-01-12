package feature;

import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.*;

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

                if(methodNameTmp.contains("\"")){
                    methodNameTmp = methodNameTmp.substring(1);
                }
                if(methodNameTmp.equals(methodName)) {
                    String[] methodMetrics = usageMethodMetricslists.get(i);
                    return new String[]{methodMetrics[methodMetrics.length - 17] + methodMetrics[methodMetrics.length - 18]
                    + methodMetrics[methodMetrics.length - 19]};
                }

            }

        }
        Set<String> set = new HashSet<>();
        set.add("5a0eac472202a3dff06e753f8f7fb852da032ca5src_main_java_run_halo_app_utils_XmlTransferMapUtils.java");
        set.add("fe816e68431e738eb40e8a6c250f53d7b2949274src_main_java_run_halo_app_utils_XmlTransferMapUtils.java");
        set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_ThrowawayConnection.java");
        set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_ConnectionProxy.java");
        set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_HikariClassLoader.java");
        set.add("baf55387f4b0908b7c36442f65cb063ddb2df590zuul-core_src_main_java_com_netflix_zuul_http_HttpServletRequestWrapper.java");
        set.add("83ec33f04015796ff0b285498e37fea5c45d24d4zuul-core_src_main_java_com_netflix_zuul_http_HttpServletRequestWrapper.java");
        set.add("ec6ec09ceec846c9334d4d32a1e19b9857c5ec16broker_src_main_java_org_dna_mqtt_moquette_messaging_spi_impl_ProtocolProcessor.java");
        set.add("6bacce6cec1be5e6ae185a6e7801ad3f4eaab1f1broker_src_main_java_org_dna_mqtt_moquette_messaging_spi_impl_ProtocolProcessor.java");

        if(!set.contains(CommitUtil.getCoarseVer(pro, commitId) + node.split("/")[0])){
            //System.out.println(CommitUtil.getCoarseVer(pro, commitId) + " " + node + " " + fileName);
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

            //if(usageFieldMetricslists.get(i)[0].equals(fileName)){
                String fieldNameTmp = usageFieldMetricslists.get(i)[usageFieldMetricslists.get(i).length - 2];
                if(fieldNameTmp.equals(fieldName)) {
                    flag = true;
                    sum += Integer.parseInt(usageFieldMetricslists.get(i)[usageFieldMetricslists.get(i).length - 1]);
                }

            //}

        }

        if(flag){
            return new String[]{String.valueOf(sum)};
        } else {
            System.out.println(pro + " 0 " + CommitUtil.getCoarseVer(pro, commitId) + " " + node + " " + fileName);
            return null;
        }

    }

    private String[] calEntityLocMetrics(String labelDependency, String[] s1, String[] s2) {
        String[] s = new String[1];

        if("NaN".equals(s1[0]) || "NaN".equals(s2[0])){
            s[0] = "NaN";
        } else {
            //System.out.println(labelDependency + " " + s1[0] + " " + s2[0]);
            double value = Math.abs(Double.parseDouble(s1[0]) - Double.parseDouble(s2[0]));
            s[0] = String.valueOf(value);
        }

        String[] d = new String[]{"\"" + labelDependency + "\""};
        return ArrayUtils.addAll(d, s);
    }


}
