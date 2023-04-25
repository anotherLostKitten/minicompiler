package ast;
public sealed abstract class Expr implements ASTNode permits IntLiteral,StrLiteral,ChrLiteral,VarExpr,FunCallExpr,ClassFunCallExpr,BinOp,ArrayAccessExpr,FieldAccessExpr,ValueAtExpr,AddressOfExpr,SizeOfExpr,TypecastExpr,ClassInstantiationExpr,Assign{
  public Type type;
}
