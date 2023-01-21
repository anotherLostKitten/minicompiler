package lexer;
import lexer.Token.TokenClass;
import java.io.EOFException;
import java.io.IOException;
/**
 * @author cdubach
 */
public class Tokeniser{
  private Scanner scanner;
  private int error=0;
  public int getErrorCount(){
	return this.error;
  }
  public Tokeniser(Scanner scanner){
	this.scanner=scanner;
  }
  private void error(char c,int line,int col){
	System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
  }
  public Token nextToken(){
	Token result;
	try{
	  result=next();
	}catch(EOFException eof){
	  // end of file, nothing to worry about, just return EOF token
	  return new Token(TokenClass.EOF,scanner.getLine(),scanner.getColumn());
	}catch(IOException ioe){
	  ioe.printStackTrace();
	  // something went horribly wrong, abort
	  System.exit(-1);
	  return null;
	}
	return result;
  }
  // todo
  private Token next()throws IOException{
	int line=scanner.getLine();
	int column=scanner.getColumn();
	char c=scanner.next();
	if(Character.isWhitespace(c)) //todo don't recurse, cringe
	  return next();
	//todo switch, cringe
	if(c=='+')
	  return new Token(TokenClass.PLUS,line,column);
	// if we reach this point, it means we did not recognise a valid token
	error(c,line,column);
	return new Token(TokenClass.INVALID,line,column);
  }
}
