package gen;
import ast.Block;
import ast.Stmt;
import gen.asm.AssemblyProgram;
public class StmtCodeGen extends CodeGen{
    public StmtCodeGen(AssemblyProgram asmProg){
        this.asmProg=asmProg;
    }
    void visit(Stmt s){
        switch (s){
            case Block b->{
                //need not handle varDecl (memory allocator takes care of them)
                b.stmts.forEach((innerStmt)->{
                    visit(innerStmt);
                });
            }
            //todo
        }
    }
}
