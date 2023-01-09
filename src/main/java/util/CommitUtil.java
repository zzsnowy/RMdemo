package util;

import java.io.*;

public class CommitUtil {
    public static void main(String[] args) throws IOException {

        String proListPath = "/Users/zzsnowy/IdeaProjects/RMdemo/src/main/resources/proList";

        File filename = new File(proListPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String pro = br.readLine();

        while (pro != null) {
            System.out.println(pro);
            getMetricsCommitId(pro);
            pro = br.readLine();
        }


        //getMatchCommitId(pro);
        //getMetricsCommitId(pro);

    }

    private static void getMatchCommitId(String pro) throws IOException {
        File writename = new File("/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/" + pro + "/" + pro + ".txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        String pathnameF = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/" + pro + "/" + pro + "-fine.txt";
        String pathnameC = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/" + pro + "/" + pro + "-coarse.txt";
        File filenameF = new File(pathnameF);
        InputStreamReader readerF = new InputStreamReader(
                new FileInputStream(filenameF));
        BufferedReader brF = new BufferedReader(readerF);

        File filenameC = new File(pathnameC);
        InputStreamReader readerC = new InputStreamReader(
                new FileInputStream(filenameC));
        BufferedReader brC = new BufferedReader(readerC);

        String lineF = "";
        lineF = brF.readLine();

        String lineC = "";
        lineC = brC.readLine();

        String tmp = "";
        while (lineF != null) {
            if(!"".equals(tmp)){

                out.write(tmp + " " + lineF + "\n");
            }
            tmp = lineC + " " + lineF;
            System.out.println(tmp);
            lineF = brF.readLine();
            lineC = brC.readLine();
        }

        out.flush(); // 把缓存区内容压入文件
        out.close(); // 关闭文件
    }

    private static void getMetricsCommitId(String pro) throws IOException{

        File writename = new File("/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MetricsCommitId/" + pro + ".txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MoveRefAtDepCommitId/" + pro + ".txt";

        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String line = br.readLine();

        while (line != null) {
            System.out.println(line);
            String coarseVer = getCoarseVer(pro, line);
            out.write( coarseVer + " " + line + "\n");
            line = br.readLine();
        }

        out.flush(); // 把缓存区内容压入文件
        out.close(); // 关闭文件
    }

    private static String getCoarseVer(String pro, String fineVer) throws IOException {

        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/" + pro + "/" + pro + ".txt";

        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String line = br.readLine();

        while (line != null) {

            String fineId = line.split(" ")[1];
            if(fineId.equals(fineVer)){
                return line.split(" ")[0];
            }
            line = br.readLine();
        }

        return null;
    }
}
