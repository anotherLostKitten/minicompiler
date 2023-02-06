package ast;
public sealed interface Type extends ASTNode permits BaseType,PointerType,StructType,ArrayType{
  public boolean equals(Type t2);
}
