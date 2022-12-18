import gr.uom.java.xmi.UMLParameter;
import gr.uom.java.xmi.UMLType;
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

import java.io.*;
import java.util.List;
import java.util.Objects;

import static org.refactoringminer.api.RefactoringType.*;

public class Main {
    public static void main(String[] args) throws Exception {
        GitService gitService = new GitServiceImpl();
        GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

        Repository repo = gitService.cloneIfNotExists(
                "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/projects/litemall",
                "https://github.com/lilishop/lilishop.git");

        File writename = new File("/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/MoveRefCommitId/litemall.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));

        String pathname = "/Users/zzsnowy/StudyDiary/MSA/graduationPro/experiment/commitId/litemall/litemall.txt";
        File filename = new File(pathname);
        InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filename));
        BufferedReader br = new BufferedReader(reader);
        String line = "";
        line = br.readLine();

        while (line != null) {
            String coarse = line.split(" ")[0];
            String fine = line.split(" ")[1];
            String lastFineCommitId = line.split(" ")[2];;
            miner.detectAtCommit(repo, coarse, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if(refactorings.isEmpty()){
                        return;
                    }
                    boolean flag = false;
                    for (Refactoring ref : refactorings) {
                        if(ref.getRefactoringType() == MOVE_OPERATION){
                            if(!flag){
                                isFlag(commitId, fine, lastFineCommitId, out);
                            }
                            System.out.println(ref);
                            MoveOperationRefactoring tmp = (MoveOperationRefactoring) ref;
                            /*System.out.println(
                                    "getSourceOperationCodeRangeBeforeMove == " + tmp.getSourceOperationCodeRangeBeforeMove() + "\n"
                                            + "getTargetOperationCodeRangeAfterMove == " + tmp.getTargetOperationCodeRangeAfterMove() + "\n"
                                            + "getMovedOperation == " + tmp.getMovedOperation() + "\n"
                            );*/
                            String pathBeforeMove = tmp.getSourceOperationCodeRangeBeforeMove().getFilePath();
                            String pathAfterMove = tmp.getTargetOperationCodeRangeAfterMove().getFilePath();
                            String methodName = tmp.getMovedOperation().getName();
                            List<UMLType> parameterType = tmp.getMovedOperation().getParameterTypeList();
                            System.out.println(
                                    "pathBeforeMove:" + pathBeforeMove + "\n"
                                            + "pathAfterMove:" + pathAfterMove + "\n"
                                            + "methodName:" + methodName + "\n"
                                            + "parameterType:" + parameterType
                            );

                            getMoveMethodRef(pathBeforeMove, parameterType, pathAfterMove, methodName);



                        } else if(ref.getRefactoringType() == MOVE_ATTRIBUTE){
                            if(!flag){
                                flag = isFlag(commitId, fine, lastFineCommitId, out);
                            }
                            System.out.println(ref);
                            MoveAttributeRefactoring tmp = (MoveAttributeRefactoring) ref;
                            /*System.out.println(
                                    "getSourceAttributeCodeRangeBeforeMove == " + tmp.getSourceAttributeCodeRangeBeforeMove() + "\n"
                                            + "getTargetAttributeCodeRangeAfterMove == " + tmp.getTargetAttributeCodeRangeAfterMove() + "\n"
                                            + "getMovedAttribute == " + tmp.getMovedAttribute() + "\n"
                            );*/
                            String pathBeforeMove = tmp.getSourceAttributeCodeRangeBeforeMove().getFilePath();
                            String pathAfterMove = tmp.getTargetAttributeCodeRangeAfterMove().getFilePath();
                            String attName = tmp.getMovedAttribute().getName();
                            System.out.println(
                                    "pathBeforeMove:" + pathBeforeMove + "\n"
                                            + "pathAfterMove:" + pathAfterMove + "\n"
                                            + "attName:" + attName
                            );

                            getMoveAttRef(pathBeforeMove, attName, pathAfterMove);

                        }

                    }
                }
            });
            line = br.readLine();
        }
        out.flush(); // 把缓存区内容压入文件
        out.close(); // 关闭文件
    }

    private static boolean isFlag(String commitId, String fine, String lastFineCommitId, BufferedWriter out) {
        boolean flag;
        System.out.println("Refactorings at " + commitId + " " + fine + " " + lastFineCommitId);
        try {
            out.write(lastFineCommitId + "\r\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        flag = true;
        return flag;
    }

    private static void getMoveAttRef(String pathBeforeMove, String attName, String pathAfterMove) {
        String pathBeforeMoveRe = pathBeforeMove.replace("/","_");
        String pathAfterMoveRe = pathAfterMove.replace("/","_");
        System.out.println("[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + attName + "[\\s\\S]*" +pathAfterMoveRe + "[\\s\\S]*");
    }

    private static void getMoveMethodRef(String pathBeforeMove, List<UMLType> parameterType, String pathAfterMove, String methodName) {
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
        System.out.println("[\\s\\S]*" + pathBeforeMoveRe + "[\\s\\S]*" + methodSignature + "[\\s\\S]*" +pathAfterMoveRe + "[\\s\\S]*");
    }
}
