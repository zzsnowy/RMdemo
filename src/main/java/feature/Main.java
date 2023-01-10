package feature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Main {

    static Logger logger = LoggerFactory.getLogger(ClassMetricsHandler.class);

    public static void main(String[] args) throws IOException {

        String proListPath = "/Users/zzsnowy/IdeaProjects/RMdemo/src/main/resources/proTmpList";

        File filename = new File(proListPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String pro = br.readLine();

        while (pro != null) {

            //ClassMetricsHandler.readAndHandleLabelDependenciesData(pro);
            EvolutionMetricsHandler.readAndHandleLabelDependenciesData(pro);
            pro = br.readLine();
        }

    }
}
