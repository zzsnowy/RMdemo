package refactoring;

import gr.uom.java.xmi.UMLType;
import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.refactoringminer.api.RefactoringType.MOVE_ATTRIBUTE;
import static org.refactoringminer.api.RefactoringType.MOVE_OPERATION;
import static refactoring.MoveRefactoringHandler.logger;

public class demo {
    public static void main(String[] args) throws Exception {

        String pro = "litemall";

        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.cloneIfNotExists(
                "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/" + pro,
                "");

        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/" + pro + "/" + pro + ".txt";
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        line = br.readLine();

        Map<String, List<String>[]> dependencyClassifyMap = new HashMap<>();
        while (line != null) {
            String coarse = line.split(" ")[0];
            try {
                miner.detectAtCommit(repo, coarse, new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        logger.info("Refactorings at {}", commitId);
                        for (Refactoring ref : refactorings) {
                            // logger.info("{}", ref.toString());
                            if (ref.getRefactoringType() == MOVE_OPERATION) {

                                logger.info("重构操作为：{}",ref);

                                MoveOperationRefactoring tmp = (MoveOperationRefactoring) ref;

                                String pathBeforeMove = tmp.getSourceOperationCodeRangeBeforeMove().getFilePath();
                                String pathAfterMove = tmp.getTargetOperationCodeRangeAfterMove().getFilePath();
                                String methodName = tmp.getMovedOperation().getName();
                                List<UMLType> parameterType = tmp.getMovedOperation().getParameterTypeList();

                                logger.info(
                                        "getSourceOperationCodeRangeBeforeMove == {}, getTargetOperationCodeRangeAfterMove == {}, getMovedOperation == {}",
                                        tmp.getSourceOperationCodeRangeBeforeMove(), tmp.getTargetOperationCodeRangeAfterMove(), tmp.getMovedOperation() + "\n"
                                );
                                logger.info("pathBeforeMove:{}\npathAfterMove:{}\nmethodName:{} \nparameterType:{}\n",
                                        pathBeforeMove, pathAfterMove, methodName, parameterType);





                            } else if (ref.getRefactoringType() == MOVE_ATTRIBUTE) {


                                logger.info("重构操作为：{}",ref);

                                MoveAttributeRefactoring tmp = (MoveAttributeRefactoring) ref;

                                String pathBeforeMove = tmp.getSourceAttributeCodeRangeBeforeMove().getFilePath();
                                String pathAfterMove = tmp.getTargetAttributeCodeRangeAfterMove().getFilePath();
                                String attName = tmp.getMovedAttribute().getName();

                                logger.info(
                                        "getSourceAttributeCodeRangeBeforeMove == {}, getTargetAttributeCodeRangeAfterMove == {}, getMovedAttribute == {}",
                                        tmp.getSourceAttributeCodeRangeBeforeMove(), tmp.getTargetAttributeCodeRangeAfterMove(), tmp.getMovedAttribute() + "\n"
                                );

                                logger.info("pathBeforeMove:{}\npathAfterMove:{}\nattName:{}\n", pathBeforeMove, pathAfterMove, attName);



                            }




                        }
                    }
                });

            } catch (Exception e) {
            }
            line = br.readLine();
        }

        /*miner.detectAtCommit(repo, "e2be9f908cd732efd6808fb0805b676c46ec4c98", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactorings at " + commitId);
                for (Refactoring ref : refactorings) {
                    System.out.println(ref.toString());
                }
            }
        });*/
        /*miner.detectAll(repo, "master", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                System.out.println("Refactorings at " + commitId);
                for (Refactoring ref : refactorings) {
                    System.out.println(ref.toString());
                }
            }
        });*/
    }
}
