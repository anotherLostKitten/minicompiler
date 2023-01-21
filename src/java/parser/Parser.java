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

  //look ahead i elements in token stream; i>0
  private Token lookAhead(int i){
	while(buffer.size()<i)//ensures the buffer has the element we want
	  buffer.add(tokeniser.nextToken());
	int cnt=1;
	for(Token t:buffer){
	  if(cnt==i)
		return t;
	  cnt++;
	}
	assert false; // should never reach this
	return tokeniser.nextToken();
  }
  //Consumes the next token from the tokeniser or the buffer if not empty.
  private void nextToken(){
	if(!buffer.isEmpty())
	  token=buffer.remove();
	else
	  token=tokeniser.nextToken();
  }
  //next token if cur is the expected, otherwise error
  private void expect(TokenClass...expected){
	for(TokenClass e:expected){
	  if(e==token.tokenClass){
		nextToken();
		return;
	  }
	}
	error(expected);
  }
  //return whether current token is equal to any expected one
  private boolean accept(TokenClass...expected){
	for(TokenClass e : expected){
	  if (e == token.tokenClass)
		return true;
	}
	return false;
  }
  private void parseProgram(){
	parseIncludes();
	while(accept(TokenClass.STRUCT,TokenClass.INT,TokenClass.CHAR,TokenClass.VOID)){
	  if(token.tokenClass==TokenClass.STRUCT&&lookAhead(1).tokenClass==TokenClass.IDENTIFIER&&lookAhead(2).tokenClass==TokenClass.LBRA){
		parseStructDecl();
	  }else{
		//todo
		nextToken(); //todo this line should be modified/removed
	  }
	}
	//todo
	expect(TokenClass.EOF);
  }
  //includes are ignored, so does not need to return an AST node
  private void parseIncludes(){
	if(accept(TokenClass.INCLUDE)){
	  nextToken();
	  expect(TokenClass.STRING_LITERAL);
	  parseIncludes();
	}
  }
  private void parseStructDecl(){
	expect(TokenClass.STRUCT);
	expect(TokenClass.IDENTIFIER);
	expect(TokenClass.LBRA);
	//todo
  }
  //todo
}
