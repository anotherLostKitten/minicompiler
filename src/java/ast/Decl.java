package ast;
public abstract sealed class Decl implements ASTNode permits FunDecl,StructTypeDecl,ClassDecl,VarDecl{
  public Type type;
  public String name;
  public boolean cdl=false;
}
