package ast;
public sealed interface Type extends ASTNode permits BaseType,PointerType,StructType,ArrayType{
  public int size();
  public boolean equals(Type t2);
}
