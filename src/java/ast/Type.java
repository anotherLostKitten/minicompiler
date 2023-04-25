package ast;
public sealed interface Type extends ASTNode permits BaseType,PointerType,StructType,ClassType,ArrayType{
  public int size();
  public boolean equals(Type t2);
}
