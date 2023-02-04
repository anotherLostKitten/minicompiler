package ast;
public sealed abstract class Expr implements ASTNode permits VarExpr{
  public Type type;
}
