package label;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static label.Main.logger;

public class DependencyHandler {

    public String pro;
    public String commitId;
    public List<String> moveMethod;
    public List<String> moveField;

    public List<String> noLabel;

    BufferedWriter outDep;

    public static final String MOVE_METHOD = "MOVE_METHOD";
    public static final String MOVE_FIELD = "MOVE_FIELD";
    public static final String NO_LABEL = "NO_LABEL";


    public DependencyHandler(String pro, String commitId, List<String> moveMethod, List<String> moveField, List<String> noLabel, BufferedWriter outDep) {
        this.pro = pro;
        this.commitId = commitId;
        this.moveMethod = moveMethod;
        this.moveField = moveField;
        this.noLabel = noLabel;
        this.outDep = outDep;
    }

    public void handle() throws IOException {

        filterAndDeduplicate(MOVE_METHOD, moveMethod);
        filterAndDeduplicate(MOVE_FIELD, moveField);

        if(moveMethod.isEmpty() && moveField.isEmpty()){
            logger.info("已移除commitId = {}, 不存在MM&MF样本", commitId);
            return;
        }

        filterAndDeduplicate(NO_LABEL, noLabel);

        BufferedWriter out = getBufferedWriter();

        dependencyFileGenerate(out, MOVE_METHOD, moveMethod);
        dependencyFileGenerate(out, MOVE_FIELD, moveField);
        dependencyFileGenerate(out, NO_LABEL, noLabel);

        outDep.write(commitId + "\n");

        out.flush(); // 把缓存区内容压入文件
        out.close(); // 关闭文件
    }

    private void filterAndDeduplicate(String name, List<String> list) {
        dependencyFilter(name, list);
        dependencyDeduplicate(name, list);
    }



    public void dependencyFilter(String name, List<String> list){

        logger.info("commitId = {}, 过滤前有{}条{}依赖", commitId, list.size(), name);

        int supportCount;
        double confidence;


        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {

            String dependency = iterator.next();

            logger.info("正在过滤依赖，当前处理的依赖为：{}", dependency);

            String[] data = dependency.split("\t");
            supportCount = Integer.parseInt(data[2]);
            confidence = Double.parseDouble(data[3]);

            //过滤不符合阈值的数据、测试的相关代码、.gitkeep的代码
            if(supportCount < 1 || confidence < 0.4 || dependency.matches("[\\s\\S]*src_test[\\s\\S]*") ||
                    dependency.matches("[\\s\\S]*\\.gitkeep[\\s\\S]*") || dependency.matches("[\\s\\S]*\\.py[\\s\\S]*")){
                iterator.remove();
                logger.info("当前依赖被过滤，supper count = {}, confidence = {}", supportCount, confidence);
            }

        }

        logger.info("commitId = {}, 过滤后有{}条{}依赖", commitId, list.size(), name);

    }

    public void dependencyDeduplicate(String name, List<String> list){

        logger.info("commitId = {}, 去重前有{}条{}依赖", commitId, list.size(), name);

        String node1;
        String node2;
        Set<EvolutionaryDependency> set = new HashSet<>();

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {

            String dependency = iterator.next();

            //logger.info("正在去重依赖，当前处理的依赖为：{}", dependency);

            String[] data = dependency.split("\t");
            node1 = data[0];
            node2 = data[1];
            EvolutionaryDependency evolutionaryDependency1 = new EvolutionaryDependency(node1, node2);
            EvolutionaryDependency evolutionaryDependency2 = new EvolutionaryDependency(node2, node1);

            if(set.contains(evolutionaryDependency1) || set.contains(evolutionaryDependency2)){
                iterator.remove();
                //logger.info("当前依赖被去重，node1 = {}, node2 = {}", node1, node2);
            } else {
                set.add(evolutionaryDependency1);
            }
        }

        logger.info("commitId = {}, 去重后有{}条{}依赖", commitId, list.size(), name);
    }

    public void dependencyFileGenerate(BufferedWriter out, String name, List<String> list) throws IOException {

        logger.info("commitId = {}, 共有{}条{}依赖, 正在写入...", commitId, list.size(), name);

        for (String dependency : list) {
            String[] data = dependency.split("\t");
            out.write(name + "\t" + data[0] + "\t" +  data[1] + "\n");
        }

        logger.info("commitId = {}, 共有{}条{}依赖, 写入成功。", commitId, list.size(), name);
    }

    private BufferedWriter getBufferedWriter() throws IOException {

        String dirPath = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/labelDependenciesData/" + pro;

        File dir = new File(dirPath);
        dir.mkdir();

        File writename = new File( dirPath + "/" + commitId + ".txt");
        writename.createNewFile();

        return new BufferedWriter(new FileWriter(writename));
    }

}
