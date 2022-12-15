import gr.uom.java.xmi.diff.MoveAttributeRefactoring;
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
                "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/lilishop",
                "https://github.com/lilishop/lilishop.git");
        miner.detectAll(repo, "master", new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {

                if(refactorings.isEmpty()){
                    return;
                }
                boolean flag = false;
                for (Refactoring ref : refactorings) {
                    if(ref.getRefactoringType() == MOVE_OPERATION){
                        if(!flag){
                            System.out.println("Refactorings at " + commitId);
                            flag = true;
                        }
                        System.out.println(ref);
                        MoveOperationRefactoring tmp = (MoveOperationRefactoring) ref;
                        System.out.println(
                                "getSourceOperationCodeRangeBeforeMove == " + tmp.getSourceOperationCodeRangeBeforeMove() + "\n"
                                        + "getTargetOperationCodeRangeAfterMove == " + tmp.getTargetOperationCodeRangeAfterMove() + "\n"
                                        + "getMovedOperation == " + tmp.getMovedOperation() + "\n"
                        );
                    } else if(ref.getRefactoringType() == MOVE_ATTRIBUTE){
                        if(!flag){
                            System.out.println("Refactorings at " + commitId);
                            flag = true;
                        }
                        System.out.println(ref);
                        MoveAttributeRefactoring tmp = (MoveAttributeRefactoring) ref;
                        System.out.println(
                                "getSourceAttributeCodeRangeBeforeMove == " + tmp.getSourceAttributeCodeRangeBeforeMove() + "\n"
                                        + "getTargetAttributeCodeRangeAfterMove == " + tmp.getTargetAttributeCodeRangeAfterMove() + "\n"
                                        + "getMovedAttribute == " + tmp.getMovedAttribute() + "\n"
                        );
                    }

                }
            }
        });
    }
}
