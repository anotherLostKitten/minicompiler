package ast;
import java.util.List;
public sealed interface ASTNode permits Decl,Expr,Program,Stmt,Type{
  abstract List<ASTNode>children();
}
