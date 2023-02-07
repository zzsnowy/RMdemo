package feature;


import org.apache.commons.lang3.ArrayUtils;
import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static feature.Main.logger;

public class ClassMetricsHandler extends MetricsHandler{

    String type;

    public static final String LEVEL = "class";

    public ClassMetricsHandler(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {

        Set<String> set = new HashSet<>();
        //set.add("5a0eac472202a3dff06e753f8f7fb852da032ca5src_main_java_run_halo_app_utils_XmlTransferMapUtils.java");
        //set.add("fe816e68431e738eb40e8a6c250f53d7b2949274src_main_java_run_halo_app_utils_XmlTransferMapUtils.java");
        //set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_ThrowawayConnection.java");
        //set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_ConnectionProxy.java");
        //set.add("2bf2269606aad85ced281b7da30175f417a76fc4src_main_java_com_zaxxer_hikari_HikariClassLoader.java");
        //set.add("baf55387f4b0908b7c36442f65cb063ddb2df590zuul-core_src_main_java_com_netflix_zuul_http_HttpServletRequestWrapper.java");
        //set.add("83ec33f04015796ff0b285498e37fea5c45d24d4zuul-core_src_main_java_com_netflix_zuul_http_HttpServletRequestWrapper.java");
        //set.add("ec6ec09ceec846c9334d4d32a1e19b9857c5ec16broker_src_main_java_org_dna_mqtt_moquette_messaging_spi_impl_ProtocolProcessor.java");
        //set.add("6bacce6cec1be5e6ae185a6e7801ad3f4eaab1f1broker_src_main_java_org_dna_mqtt_moquette_messaging_spi_impl_ProtocolProcessor.java");

        List<String[]> classMetricsLists = readLabelDependenciesMetricsCsv(pro, commitId, LEVEL);
        List<String[]> entityClassMetricsList = new ArrayList<>();

        //String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";
        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/allLabelDependencies/" + pro + "/" + commitId + ".txt";

        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String node1 = labelDependency.split("\t")[1];
            String node2 = labelDependency.split("\t")[2];

            String[] s1 = findClassMetricsByNode(pro, commitId, node1, classMetricsLists);
            String[] s2 = findClassMetricsByNode(pro, commitId, node2, classMetricsLists);

            if(s1 == null || s2 == null){
                if(!set.contains(CommitUtil.getCoarseVer(pro, commitId) + node1.split("/")[0]) && s1 == null){
                    logger.info("commit = {}, {}, 类指标不存在", CommitUtil.getCoarseVer(pro, commitId), node1);
                }
                if(!set.contains(CommitUtil.getCoarseVer(pro, commitId) + node2.split("/")[0]) && s2 == null){
                    logger.info("commit = {}, {}, 类指标不存在", CommitUtil.getCoarseVer(pro, commitId), node2);
                }

                labelDependency = br.readLine();
                continue;
            }

            String[] s = calEntityClassMetrics(labelDependency, s1, s2);

            entityClassMetricsList.add(s);

            labelDependency = br.readLine();
        }

        writeCsv(pro, commitId, type, entityClassMetricsList);
        writeIntoLabelFeatureCsv(pro, commitId, type, entityClassMetricsList);

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

}
