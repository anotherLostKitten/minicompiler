package parser;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;
import java.util.LinkedList;
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
  public void parse(){
	nextToken();//get the first token
	parseProgram();
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
	nextToken();
	error(expected);
  }
  //return whether current token is equal to any expected one
  private boolean accept(TokenClass...expected){
	for(TokenClass e:expected)
	  if(e==token.tokenClass)
		return true;
	return false;
  }
  private void parseProgram(){
	parseIncludes();
	while(!accept(TokenClass.EOF))
	  parseDecl();
  }
  private void parseIncludes(){//includes ignored, so needn't return AST node
	while(accept(TokenClass.INCLUDE)){
	  nextToken();
	  expect(TokenClass.STRING_LITERAL);
	}
  }
  private void parseDecl(){
    if(accept(TokenClass.STRUCT)&&lookAhead(1).tokenClass==TokenClass.IDENTIFIER&&lookAhead(2).tokenClass==TokenClass.LBRA)
	  parseStructDecl();
	else
	  parseVfdecl();
  }
  private void parseStructDecl(){
	expect(TokenClass.STRUCT);
	expect(TokenClass.IDENTIFIER);
	expect(TokenClass.LBRA);
	do
	  parseVardecl();
	while(!accept(TokenClass.RBRA,TokenClass.EOF));
    expect(TokenClass.RBRA);
	expect(TokenClass.SC);
  }
  private void parseVfdecl(){
	parseType();
	expect(TokenClass.IDENTIFIER);
	if(accept(TokenClass.LPAR))
	  parseFunprams();
	else
	  parseVararr();
  }
  private void parseVardecl(){
	parseType();
	expect(TokenClass.IDENTIFIER);
	parseVararr();
  }
  private void parseVararr(){
	while(accept(TokenClass.LSBR)){
	  nextToken();
	  expect(TokenClass.INT_LITERAL);
	  expect(TokenClass.RSBR);
	}
	expect(TokenClass.SC);
  }
  private void parseType(){
	if(accept(TokenClass.STRUCT)){
	  nextToken();
	  expect(TokenClass.IDENTIFIER);
	}else
	  expect(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID);
	while(accept(TokenClass.ASTERIX))
	  nextToken();
  }
  private void parseFunprams(){
	expect(TokenClass.LPAR);
	if(!accept(TokenClass.RPAR)){
	  parseType();
	  expect(TokenClass.IDENTIFIER);
	  while(accept(TokenClass.COMMA)){
		nextToken();
		parseType();
		expect(TokenClass.IDENTIFIER);
	  }
	}
	expect(TokenClass.RPAR);
	parseBlock();
  }
  private void parseStmt(){
	if(accept(TokenClass.LBRA))
	  parseBlock();
	else if(accept(TokenClass.WHILE)){
	  nextToken();
	  expect(TokenClass.LPAR);
	  parseExp();
	  expect(TokenClass.RPAR);
	  parseStmt();
	}else if(accept(TokenClass.IF)){
	  nextToken();
	  expect(TokenClass.LPAR);
	  parseExp();
	  expect(TokenClass.RPAR);
	  parseStmt();
	  if(accept(TokenClass.ELSE)){
		nextToken();
		parseStmt();
	  }
	}else if(accept(TokenClass.RETURN)){
	  nextToken();
	  if(!accept(TokenClass.SC))
		parseExp();
	  expect(TokenClass.SC);
	}else{
	  parseExp();
	  expect(TokenClass.SC);
	}
  }
  private void parseBlock(){
	expect(TokenClass.LBRA);
	while(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT))
	  parseVardecl();
	while(!accept(TokenClass.RBRA,TokenClass.EOF))
	  parseStmt();
	expect(TokenClass.RBRA);
  }
  private void parseExp(){
	if(accept(TokenClass.MINUS,TokenClass.PLUS,TokenClass.ASTERIX,TokenClass.AND)){
	  nextToken();
	  parseExp();
	}else if(accept(TokenClass.LPAR)){
	  nextToken();
	  if(accept(TokenClass.INT,TokenClass.CHAR,TokenClass.VOID,TokenClass.STRUCT)){
		parseType();
		expect(TokenClass.RPAR);
		parseExp();
	  }else{
		parseExp();
		expect(TokenClass.RPAR);
		parseExpp();
	  }
	}else if(accept(TokenClass.IDENTIFIER)){
	  if(lookAhead(1).tokenClass==TokenClass.LPAR){
		parseFuncall();
		parseExpp();
	  }else{
		nextToken();
		parseExpp();
	  }
	}else if(accept(TokenClass.INT_LITERAL,TokenClass.STRING_LITERAL,TokenClass.CHAR_LITERAL)){
	  nextToken();
	  parseExpp();
	}else{
	  expect(TokenClass.SIZEOF);
	  expect(TokenClass.LPAR);
	  parseType();
	  expect(TokenClass.RPAR);
	  parseExpp();
	}
  }
  private void parseExpp(){
	if(accept(TokenClass.ASSIGN,TokenClass.GT,TokenClass.LT,TokenClass.GE,TokenClass.LE,TokenClass.NE,TokenClass.EQ,TokenClass.PLUS,TokenClass.MINUS,TokenClass.DIV,TokenClass.ASTERIX,TokenClass.REM,TokenClass.LOGOR,TokenClass.LOGAND)){
	  nextToken();
	  parseExp();
	  parseExpp();
	}else if(accept(TokenClass.LSBR)){
	  nextToken();
	  parseExp();
	  expect(TokenClass.RSBR);
	  parseExpp();
	}else if(accept(TokenClass.DOT)){
	  nextToken();
	  expect(TokenClass.IDENTIFIER);
	  parseExpp();
	}
  }
  private void parseFuncall(){
	expect(TokenClass.IDENTIFIER);
	expect(TokenClass.LPAR);
	if(!accept(TokenClass.RPAR)){
	  parseExp();
	  while(accept(TokenClass.COMMA)){
		nextToken();
		parseExp();
	  }
	}
	expect(TokenClass.RPAR);
  }
}
