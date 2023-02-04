package parser;
import ast.*;
import ast.BinOp.Op;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
 * @author cdubach
 */
public class Parser{
  private Token token;
  private Queue<Token>buffer=new LinkedList<>();
  private final Tokeniser tokeniser;
  private int error=0;
  private Token lastErrorToken;
  public Parser(Tokeniser tokeniser){
	this.tokeniser=tokeniser;
  }
  public Program parse(){
	nextToken();//get the first token
	return parseProgram();
  }
  public int getErrorCount(){
	return error;
  }
  private void error(TokenClass...expected){
	if(lastErrorToken==token)return;//skip error, same token
	StringBuilder sb=new StringBuilder();
	String sep="";
	for(TokenClass e:expected){
	  sb.append(sep);
	  sb.append(e);
	  sep="|";
	}
	System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position);
	error++;
	lastErrorToken=token;
  }
  private Token lookAhead(int i){//look ahead i elements in token stream; i>0
	while(buffer.size()<i)//ensures the buffer has the element we want
	  buffer.add(tokeniser.nextToken());
	int cnt=1;
	for(Token t:buffer)
	  if(cnt++==i)
		return t;
	assert false;//should never reach this
	return tokeniser.nextToken();
  }
  //consume next token from tokeniser, or buffer if not empty
  private void nextToken(){
	if(!buffer.isEmpty())
	  token=buffer.remove();
	else
	  token=tokeniser.nextToken();
  }
  //next token if cur is the expected, otherwise error
  private void expect(TokenClass...expected){
	for(TokenClass e:expected)
	  if(e==token.tokenClass){
		nextToken();
		return;
	  }
	error(expected);
	nextToken();
  }
  //return whether current token is equal to any expected one
  private boolean accept(TokenClass...expected){
	for(TokenClass e:expected)
	  if(e==token.tokenClass)
		return true;
	return false;
  }
  private Program parseProgram(){
	parseIncludes();
	List<StructTypeDecl>ss=new ArrayList<StructTypeDecl>();
	List<VarDecl>vs=new ArrayList<VarDecl>();
	List<FunDecl>fs=new ArrayList<FunDecl>();
	while(!accept(TokenClass.EOF))
	  if(accept(TokenClass.STRUCT)&&lookAhead(1).tokenClass==TokenClass.IDENTIFIER&&lookAhead(2).tokenClass==TokenClass.LBRA)
		ss.add(parseStructDecl());
	  else{
		Type t=parseType();
		String s=token.data;
		expect(TokenClass.IDENTIFIER);
		if(!accept(TokenClass.LPAR))
		  vs.add(parseVararr(t,s));
		else{
		  List<VarDecl>ps=parseFunprams();
		  fs.add(new FunDecl(t,s,ps,parseBlock()));
		}
	  }
	return new Program(ss,vs,fs);
  }
  private void parseIncludes(){//includes ignored, so needn't return AST node
	while(accept(TokenClass.INCLUDE)){
	  nextToken();
	  expect(TokenClass.STRING_LITERAL);
	}
  }
  private StructTypeDecl parseStructDecl(){
	expect(TokenClass.STRUCT);
	StructType t=new StructType(token.data);
	expect(TokenClass.IDENTIFIER);
	List<VarDecl>vs=new ArrayList<VarDecl>();
	expect(TokenClass.LBRA);
	do
	  vs.add(parseVardecl());
	while(!accept(TokenClass.RBRA,TokenClass.EOF));
    expect(TokenClass.RBRA);
	expect(TokenClass.SC);
	return new StructTypeDecl(t,vs);
  }
  private VarDecl parseVardecl(){
	Type t=parseType();
	String s=token.data;
	expect(TokenClass.IDENTIFIER);
	return parseVararr(t,s);
  }
  private VarDecl parseVararr(Type t,String s){
	while(accept(TokenClass.LSBR)){
	  nextToken();
	  int n=Integer.MIN_VALUE;//can't be negative b/c "-" seperate token
	  if(accept(TokenClass.INT_LITERAL))
		n=Integer.parseInt(token.data);
	  expect(TokenClass.INT_LITERAL);
	  expect(TokenClass.RSBR);
	  t=new ArrayType(t,n);
	}
	expect(TokenClass.SC);
	return new VarDecl(t,s);
  }
  private Type parseType(){
	Type t=BaseType.UNKNOWN;
	switch(token.tokenClass){
	case STRUCT:
	  nextToken();
	  t=new StructType(token.data);
	  expect(TokenClass.IDENTIFIER);
	  break;
	case INT:
	  nextToken();
	  t=BaseType.INT;
	  break;
	case CHAR:
	  nextToken();
	  t=BaseType.CHAR;
	  break;
	case VOID:
	  nextToken();
	  t=BaseType.VOID;
	  break;
	default:
	  error();
	  nextToken();
	  return t;
	}
	for(;accept(TokenClass.ASTERIX);nextToken())
	  t=new PointerType(t);
	return t;
  }
  private List<VarDecl> parseFunprams(){
	List<VarDecl>vs=new ArrayList<VarDecl>();
	expect(TokenClass.LPAR);
	if(!accept(TokenClass.RPAR)){
	  Type t=parseType();
	  vs.add(new VarDecl(t,token.data));
	  expect(TokenClass.IDENTIFIER);
	  while(accept(TokenClass.COMMA)){
		nextToken();
		t=parseType();
		vs.add(new VarDecl(t,token.data));
		expect(TokenClass.IDENTIFIER);
	  }
	}
	expect(TokenClass.RPAR);
	return vs;
  }
  private Stmt parseStmt(){
	switch(token.tokenClass){
	case LBRA:
	  return parseBlock();
	case WHILE:
	  nextToken();
	  expect(TokenClass.LPAR);
	  Expr c=parseExp();
	  expect(TokenClass.RPAR);
	  return new While(c,parseStmt());
	case IF:
	  nextToken();
	  expect(TokenClass.LPAR);
	  Expr ic=parseExp();
	  expect(TokenClass.RPAR);
	  Stmt iy=parseStmt(),in=null;
	  if(accept(TokenClass.ELSE)){
		nextToken();
		in=parseStmt();
	  }
	  return new If(ic,iy,in);
	case RETURN:
	  nextToken();
	  Expr rv=null;
	  if(!accept(TokenClass.SC))
		rv=parseExp();
	  expect(TokenClass.SC);
	  return new Return(rv);
	default:
	  Expr e=parseExp();
	  expect(TokenClass.SC);
	  return new ExprStmt(e);
	}
  }
  private Block parseBlock(){
	List<VarDecl>vs=new ArrayList<VarDecl>();
	List<Stmt>ss=new ArrayList<Stmt>();
	expect(TokenClass.LBRA);
	while(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT))
	  vs.add(parseVardecl());
	while(!accept(TokenClass.RBRA,TokenClass.EOF))
	  ss.add(parseStmt());
	expect(TokenClass.RBRA);
	return new Block(vs,ss);
  }
  private Expr parseExp(){
	Expr l=parsePr8();
	if(accept(TokenClass.ASSIGN)){
	  nextToken();
	  return new Assign(l,parseExp());
	}
	return l;
  }
  private Expr parsePr8(){
	Expr l=parsePr7();
	while(accept(TokenClass.LOGOR)){
	  nextToken();
	  l=new BinOp(l,parsePr7(),Op.OR);
	}
	return l;
  }
  private Expr parsePr7(){
	Expr l=parsePr6();
	while(accept(TokenClass.LOGAND)){
	  nextToken();
	  l=new BinOp(l,parsePr6(),Op.AND);
	}
	return l;
  }
  private Expr parsePr6(){
	Expr l=parsePr5();
	while(true){
	  if(accept(TokenClass.EQ)){
		nextToken();
		l=new BinOp(l,parsePr5(),Op.EQ);
	  }else if(accept(TokenClass.EQ)){
		nextToken();
		l=new BinOp(l,parsePr5(),Op.NE);
	  }else
		return l;
	}
  }
  private Expr parsePr5(){
	Expr l=parsePr4();
	while(true){
	  if(accept(TokenClass.GT)){
		nextToken();
		l=new BinOp(l,parsePr4(),Op.GT);
	  }else if(accept(TokenClass.LT)){
		nextToken();
		l=new BinOp(l,parsePr4(),Op.LT);
	  }else if(accept(TokenClass.GE)){
		nextToken();
		l=new BinOp(l,parsePr4(),Op.GE);
	  }else if(accept(TokenClass.LE)){
		nextToken();
		l=new BinOp(l,parsePr4(),Op.LE);
	  }else
		return l;
	}
  }
  private Expr parsePr4(){
	Expr l=parsePr3();
	while(true){
	  if(accept(TokenClass.PLUS)){
		nextToken();
		l=new BinOp(l,parsePr3(),Op.ADD);
	  }else if(accept(TokenClass.MINUS)){
		nextToken();
		l=new BinOp(l,parsePr3(),Op.SUB);
	  }else
		return l;
	}
  }
  private Expr parsePr3(){
	Expr l=parsePr2();
	while(true){
	  if(accept(TokenClass.ASTERIX)){
		nextToken();
		l=new BinOp(l,parsePr2(),Op.MUL);
	  }else if(accept(TokenClass.DIV)){
		nextToken();
		l=new BinOp(l,parsePr2(),Op.DIV);
	  }else if(accept(TokenClass.REM)){
		nextToken();
		l=new BinOp(l,parsePr2(),Op.MOD);
	  }else
		return l;
	}
  }
  private Expr parsePr2(){
	if(accept(TokenClass.PLUS)){
	  nextToken();
	  return new BinOp(new IntLiteral(0),parsePr2(),Op.ADD);
	}if(accept(TokenClass.MINUS)){
	  nextToken();
	  return new BinOp(new IntLiteral(0),parsePr2(),Op.SUB);
	}if(accept(TokenClass.ASTERIX)){
	  nextToken();
	  return new ValueAtExpr(parsePr2());
	}if(accept(TokenClass.AND)){
	  nextToken();
	  return new AddressOfExpr(parsePr2());
	}if(accept(TokenClass.LPAR)){
	  switch(lookAhead(1).tokenClass){
	  case INT:
	  case CHAR:
	  case VOID:
	  case STRUCT:
		nextToken();
		Type t=parseType();
		expect(TokenClass.RPAR);
		return new TypecastExpr(t,parsePr2());
	  }
	}
	return parsePr1();
  }
  private Expr parsePr1(){
	Expr l=parsePr0();
	while(true){
	  if(accept(TokenClass.LSBR)){
		nextToken();
		l=new ArrayAccessExpr(l,parseExp());
		expect(TokenClass.RSBR);
	  }else if(accept(TokenClass.DOT)){
		nextToken();
		l=new FieldAccessExpr(l,token.data);
		expect(TokenClass.IDENTIFIER);
	  }else
		return l;
	}
  }
  private Expr parsePr0(){
	switch(token.tokenClass){
	case LPAR:
	  nextToken();
	  Expr e=parseExp();
	  expect(TokenClass.RPAR);
	  return e;
    case IDENTIFIER:
	  if(lookAhead(1).tokenClass==TokenClass.LPAR)
		return parseFuncall();
	  VarExpr v=new VarExpr(token.data);
	  nextToken();
	  return v;
    case INT_LITERAL:
	  IntLiteral i=new IntLiteral(Integer.parseInt(token.data));
	  nextToken();
	  return i;
	case STRING_LITERAL:
	  StrLiteral s=new StrLiteral(token.data);
	  nextToken();
	  return s;
	case CHAR_LITERAL:
	  ChrLiteral c=new ChrLiteral(token.data.charAt(0));
	  nextToken();
	  return c;
	case SIZEOF:
	  nextToken();
	  expect(TokenClass.LPAR);
	  Type t=parseType();
	  expect(TokenClass.RPAR);
	  return new SizeOfExpr(t);
	}
	error();
	nextToken();
	return null;
  }
  private FunCallExpr parseFuncall(){
	String f=token.data;
	expect(TokenClass.IDENTIFIER);
	expect(TokenClass.LPAR);
	List<Expr>args=new ArrayList<Expr>();
	if(!accept(TokenClass.RPAR)){
	  args.add(parseExp());
	  while(accept(TokenClass.COMMA)){
		nextToken();
		args.add(parseExp());
	  }
	}
	expect(TokenClass.RPAR);
	return new FunCallExpr(f,args);
  }
}
