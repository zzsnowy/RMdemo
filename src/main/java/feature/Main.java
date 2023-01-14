package feature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class Main {

    static Logger logger = LoggerFactory.getLogger(ClassMetricsHandler.class);

    public static final String CLASS = "class";

    public static final String EVOLUTION = "evolution";

    public static final String LOC = "loc";
    public static final String USAGE = "usage";
    public static final String FEATURES = "features";

    public static void main(String[] args) throws IOException {

        String proListPath = "/Users/zzsnowy/IdeaProjects/RMdemo/src/main/resources/proTmpList";

        File filename = new File(proListPath);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);

        String pro = br.readLine();


        while (pro != null) {

            ClassMetricsHandler classMetricsHandler = new ClassMetricsHandler(CLASS);
            classMetricsHandler.readAndHandleLabelDependenciesData(pro);

            EvolutionMetricsHandler evolutionMetricsHandler = new EvolutionMetricsHandler(EVOLUTION);
            evolutionMetricsHandler.readAndHandleLabelDependenciesData(pro);

            LocMetricsHandler locMetricsHandler = new LocMetricsHandler(LOC);
            locMetricsHandler.readAndHandleLabelDependenciesData(pro);

            UsageMetricsHandler usageMetricsHandler = new UsageMetricsHandler(USAGE);
            usageMetricsHandler.readAndHandleLabelDependenciesData(pro);

            FeatureCombiner featureCombiner = new FeatureCombiner(FEATURES);
            featureCombiner.readAndHandleLabelDependenciesData(pro);

            pro = br.readLine();
        }

    }
}
