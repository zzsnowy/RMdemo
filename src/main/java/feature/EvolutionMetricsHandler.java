package feature;

import util.CommitUtil;
import util.CsvUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static feature.Main.logger;

public class EvolutionMetricsHandler extends MetricsHandler{

    String type;

    public EvolutionMetricsHandler(String type) {
        this.type = type;
    }

    public void readAndHandleLabelDependenciesDataByCommitId(String pro, String commitId) throws IOException {

        logger.info("正在处理：{}", commitId);

        List<String[]> evolutionMetricsLists = readEvolutionMetricslists(pro, commitId);

        List<String[]> entityEvolutionMetricsList = new ArrayList<>();

        //String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";
        String path = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/allLabelDependencies/" + pro + "/" + commitId + ".txt";


        File filename = new File(path);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String labelDependency = br.readLine();

        while (labelDependency != null) {

            String[] s = findEvolutionMetricsByDependency(pro, commitId, labelDependency, evolutionMetricsLists);
            entityEvolutionMetricsList.add(s);
            labelDependency = br.readLine();
        }
        writeCsv(pro, commitId, type, entityEvolutionMetricsList);
        writeIntoLabelFeatureCsv(pro, commitId, type, entityEvolutionMetricsList);
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
                        logger.error("错误：{}", labelDependency);
                    }
                }
                num ++;
            }

        }

        if(num == 1){
            System.out.println(num + " " + labelDependency);
        }

        //2 3 5
        sortConfidence(s);
        return s;
    }

    private static void sortConfidence(String[] s) {
        double a = Double.parseDouble(s[2]);
        double b = Double.parseDouble(s[3]);
        if(a > b){
            s[2] = String.valueOf(b);
            s[3] = String.valueOf(a);
        }
    }

}

