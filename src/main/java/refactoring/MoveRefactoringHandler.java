package refactoring;

import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.refactoringminer.api.RefactoringType.MOVE_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.MOVE_OPERATION;

public class MoveRefactoringHandler extends RefactoringHandler {
    static Logger logger = LoggerFactory.getLogger(MoveRefactoringHandler.class);

    private String fine;
    private String lastFineCommitId;
    private BufferedWriter out;
    private Map<String, List<String>[]> dependencyClassifyMap;
    public MoveRefactoringHandler(String fine, String lastFineCommitId, BufferedWriter out, Map<String, List<String>[]> dependencyClassifyMap) {
        this.fine = fine;
        this.lastFineCommitId = lastFineCommitId;
        this.out = out;
        this.dependencyClassifyMap = dependencyClassifyMap;
    }

    @Override
    public void handle(String commitId, List<Refactoring> refactorings) {
        if (refactorings.isEmpty()) {
            return;
        }
        boolean flag = false;
        for (Refactoring ref : refactorings) {
            if (ref.getRefactoringType() == MOVE_OPERATION) {
                if (!flag) {
                    flag = isFlag(commitId, fine, lastFineCommitId, out, dependencyClassifyMap);
                }

                logger.info("重构操作为：{}",ref);

                MoveOperationRefactoring tmp = (MoveOperationRefactoring) ref;

                String pathBeforeMove = tmp.getSourceOperationCodeRangeBeforeMove().getFilePath();
                String pathAfterMove = tmp.getTargetOperationCodeRangeAfterMove().getFilePath();
                String methodName = tmp.getMovedOperation().getName();
                List<UMLType> parameterType = tmp.getMovedOperation().getParameterTypeList();

                logger.info("pathBeforeMove:{}\npathAfterMove:{}\nmethodName:{} \nparameterType:{}\n",
                        pathBeforeMove, pathAfterMove, methodName, parameterType);

                if (!pathAfterMove.equals(pathBeforeMove)) {
                    getMoveMethodRef(pathBeforeMove, parameterType, pathAfterMove, methodName, dependencyClassifyMap, lastFineCommitId);
                }



            } else if (ref.getRefactoringType() == MOVE_ATTRIBUTE) {
                if (!flag) {
                    flag = isFlag(commitId, fine, lastFineCommitId, out, dependencyClassifyMap);
                }

                logger.info("重构操作为：{}",ref);

                MoveAttributeRefactoring tmp = (MoveAttributeRefactoring) ref;

                String pathBeforeMove = tmp.getSourceAttributeCodeRangeBeforeMove().getFilePath();
                String pathAfterMove = tmp.getTargetAttributeCodeRangeAfterMove().getFilePath();
                String attName = tmp.getMovedAttribute().getName();

                logger.info("pathBeforeMove:{}\npathAfterMove:{}\nattName:{}\n", pathBeforeMove, pathAfterMove, attName);

                if (!pathAfterMove.equals(pathBeforeMove)) {
                    getMoveAttRef(pathBeforeMove, attName, pathAfterMove, dependencyClassifyMap, lastFineCommitId);

                }

            }

        }
    }
    private static boolean isFlag(String commitId, String fine, String lastFineCommitId, BufferedWriter out, Map<String, List<String>[]> dependencyClassifyMap) {
        boolean flag;
        logger.info("Refactorings at {} {} {}", commitId, fine, lastFineCommitId);
        try {
            out.write(lastFineCommitId + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(dependencyClassifyMap.containsKey(lastFineCommitId)){
            logger.error("重复的lastFineCommitId: {}", lastFineCommitId);
        } else {
            List<String>[] lists = new ArrayList[2];
            for (int i = 0; i < lists.length; i++) {
                lists[i] = new ArrayList<>();
            }
            dependencyClassifyMap.put(lastFineCommitId, lists);
        }
        flag = true;
        return flag;
    }

    private static void getMoveAttRef(String pathBeforeMove, String attName, String pathAfterMove, Map<String, List<String>[]> dependencyClassifyMap, String lastFineCommitId) {
        String pathBeforeMoveRe = pathBeforeMove.replace("/","_");
        String pathAfterMoveRe = pathAfterMove.replace("/","_");

        pathBeforeMoveRe = transformSpecial(pathBeforeMoveRe);
        pathAfterMoveRe = transformSpecial(pathAfterMoveRe);
        attName = transformSpecial(attName);

        String dependency1 = "[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + attName + "[\\s\\S]*" + pathAfterMoveRe + "[\\s\\S]*";
        String dependency2 = "[\\s\\S]*" + pathAfterMoveRe + "[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + attName + "[\\s\\S]*";

        logger.info("dependency1:{}", dependency1);
        logger.info("dependency2:{}", dependency2);

        dependencyClassify(dependency1, dependency2, dependencyClassifyMap, lastFineCommitId, "MF");
    }

    private static String transformSpecial(String dependency) {
        dependency = dependency.replace(".","\\.");
        dependency = dependency.replace("(","\\(");
        dependency = dependency.replace(")","\\)");
        dependency = dependency.replace("[","\\[");
        dependency = dependency.replace("]","\\]");
        return dependency;
    }

    private static void getMoveMethodRef(String pathBeforeMove, List<UMLType> parameterType, String pathAfterMove, String methodName, Map<String, List<String>[]> dependencyClassifyMap, String lastFineCommitId) {
        String pathBeforeMoveRe = pathBeforeMove.replace("/","_");
        String methodSignature = methodName + "(";
        for(int i = 0; i < parameterType.size(); i ++){
            String type = parameterType.get(i).toString();
            methodSignature += type;
            if(i != parameterType.size() - 1){
                methodSignature += ",";
            }
        }
        methodSignature += ")" ;
        String pathAfterMoveRe = pathAfterMove.replace("/","_");

        pathBeforeMoveRe = transformSpecial(pathBeforeMoveRe);
        pathAfterMoveRe = transformSpecial(pathAfterMoveRe);
        methodSignature = transformSpecial(methodSignature);

        String dependency1 = "[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + methodSignature + "[\\s\\S]*" + pathAfterMoveRe + "[\\s\\S]*";
        String dependency2 = "[\\s\\S]*" + pathAfterMoveRe + "[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + methodSignature + "[\\s\\S]*";


        logger.info("dependency1:{}", dependency1);
        logger.info("dependency2:{}", dependency2);

        dependencyClassify(dependency1, dependency2, dependencyClassifyMap, lastFineCommitId, "MM");

    }

    private static void dependencyClassify(String dependency1, String dependency2, Map<String, List<String>[]> dependencyClassifyMap, String lastFineCommitId, String moveOpe) {
        if(!dependencyClassifyMap.containsKey(lastFineCommitId)){
            logger.error("lastFineCommitId不存在：{}", lastFineCommitId);
        } else {
            List<String>[] tmpList = dependencyClassifyMap.get(lastFineCommitId);
            if(moveOpe.equals("MM")){
                tmpList[0].add(dependency1);
                tmpList[0].add(dependency2);
            } else {
                tmpList[1].add(dependency1);
                tmpList[1].add(dependency2);
            }
        }
    }
}
