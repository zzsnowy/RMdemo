package util;

import java.io.*;

public class CommitUtil {
    public static void main(String[] args) throws IOException {

        File writename = new File("/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/litemall/litemall.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        String pathnameF = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/litemall/litemall-fine.txt";
        String pathnameC = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/litemall/litemall-coarse.txt";
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
            if(!tmp.equals("")){
                out.write(tmp + " " + lineF + "\r\n");
            }
            tmp = lineC + " " + lineF;
            System.out.println(tmp);
            lineF = brF.readLine();
            lineC = brC.readLine();
        }

        out.flush(); // 把缓存区内容压入文件
        out.close(); // 关闭文件
    }
}
