import gr.uom.java.xmi.diff.MoveClassRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import org.eclipse.jgit.lib.Repository;

import java.util.List;
import java.util.Objects;

import static org.refactoringminer.api.RefactoringType.*;

public class Main {
    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.cloneIfNotExists(
                "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/litemall",
                "https://github.com/linlinjava/litemall.git");
        miner.detectAll(repo, "master", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                if(refactorings.isEmpty()){
                    return;
                }
                boolean flag = false;
                for (Refactoring ref : refactorings) {
                    boolean isRef = false;
                    if(ref.getRefactoringType() == MOVE_OPERATION){
                        isRef = true;
                        /*
                        System.out.println(ref.getName());
                        MoveOperationRefactoring tmp = (MoveOperationRefactoring) ref;
                        System.out.println(
                                "getBodyMapper == " + tmp.getBodyMapper() + "\n"
                                + "getReplacements == " + tmp.getReplacements() + "\n"
                                + "getSourceOperationCodeRangeBeforeMove == " + tmp.getSourceOperationCodeRangeBeforeMove() + "\n"
                                + "getTargetOperationCodeRangeAfterMove == " + tmp.getTargetOperationCodeRangeAfterMove() + "\n"+ "getMovedOperation == " + tmp.getMovedOperation() + "\n"
                                + "getOriginalOperation == " + tmp.getOriginalOperation() + "\n"
                                + "leftSide == " + tmp.leftSide() + "\n"
                                + "rightSide == " + tmp.rightSide() + "\n"
                        );
                        */

                    } else if(ref.getRefactoringType() == MOVE_ATTRIBUTE){
                        isRef = true;
                    } else if(ref.getRefactoringType() == MOVE_AND_RENAME_OPERATION){
                        isRef = true;
                    } else if(ref.getRefactoringType() == MOVE_RENAME_ATTRIBUTE){
                        isRef = true;
                    }
                    {
                        if(!flag){
                            System.out.println("Refactorings at " + commitId);
                            flag = true;
                        }
                        System.out.println(ref);
                        System.out.println(ref.getName() + "    " + ref.getRefactoringType());
                    }
                }


            }
        });
    }
}
