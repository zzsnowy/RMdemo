package util;

import java.io.*;

public class CountRatioUtil {

    public static final String MOVE_METHOD = "MOVE_METHOD";
    public static final String MOVE_FIELD = "MOVE_FIELD";
    public static final String NO_LABEL = "NO_LABEL";
    public static void main(String[] args) throws IOException {

        String proListPath = "/Users/zzsnowy/IdeaProjects/RMdemo/src/main/resources/proList";

        File filename = new File(proListPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String pro = br.readLine();

        int countMM = 0, countMF = 0, countNL = 0;
        while (pro != null) {
            int MM = count(pro, MOVE_METHOD);
            int MF = count(pro, MOVE_FIELD);
            int NL = count(pro, NO_LABEL);
            countMM += MM;
            countMF += MF;
            countNL += NL;
            System.out.print(pro + " " + MM + " " + MF + " " + NL + " ");
            System.out.println((double) NL / (double) MM + "   " + (double) NL / (double) MF + "   " + (double)NL / (double) (MM + MF));
            pro = br.readLine();
        }
        System.out.print(countMM + " " + countMF + " " + countNL + " ");
        System.out.println((double) countNL / (double) countMM + "   " + (double) countNL / (double) countMF + "   " + (double)countNL / (double) (countMM + countMF));

    }

    private static int count(String pro, String type) throws IOException {

        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MoveRefAtDepCommitId/" + pro + ".txt";

        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String commitId = br.readLine();

        int sum = 0;
        while (commitId != null) {

            sum += count(pro, commitId, type);
            commitId = br.readLine();
        }
        return sum;
    }

    private static int count(String pro, String commitId, String type) throws IOException {
        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro + "/" + commitId + ".txt";

        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String dependency = br.readLine();

        int sum = 0;
        while (dependency != null) {
            boolean flag = true;
            String node1 = dependency.split("\t")[1];
            String node2 = dependency.split("\t")[2];

//            if(node1.matches("[\\s\\S]*\\.py[\\s\\S]*")){
//                //System.out.println(node1);
//                flag = false;
//            }
//            if(node2.matches("[\\s\\S]*\\.py[\\s\\S]*")){
//                //System.out.println(node2);
//                flag = false;
//            }
            if(!flag){
                dependency = br.readLine();
                continue;
            }

            if(type.equals(dependency.split("\t")[0])){
                sum ++;
            }
            dependency = br.readLine();
        }
        return sum;
    }

}
