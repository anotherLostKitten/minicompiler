package lexer;
import java.io.EOFException;
import java.io.IOException;
import lexer.Token.TokenClass;
/**
 * @author cdubach
 */
public class Tokeniser{
  private Scanner sc;
  private int error=0;
  public int getErrorCount(){
	return this.error;
  }
  public Tokeniser(Scanner sc){
	this.sc=sc;
  }
  private void error(char c,int line,int col){
	System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	error++;
  }
  public Token nextToken(){
	Token result;
	try{
	  result=next();
	}catch(EOFException eof){//eof; not problem, just return EOF token
	  return new Token(TokenClass.EOF,sc.getLine(),sc.getColumn());
	}catch(IOException ioe){//something went horribly wrong, abort
	  ioe.printStackTrace();
	  System.exit(-1);
	  return null;
	}
	return result;
  }
  private Token next()throws IOException{
	char c=sc.next();
	while(Character.isWhitespace(c))
	  c=sc.next();
	int ln=sc.getLine(),cn=sc.getColumn();
	StringBuilder v;
	switch(c){
	case'{':
	  return new Token(TokenClass.LBRA,ln,cn);
	case'}':
	  return new Token(TokenClass.RBRA,ln,cn);
	case'(':
	  return new Token(TokenClass.LPAR,ln,cn);
	case')':
	  return new Token(TokenClass.RPAR,ln,cn);
	case'[':
	  return new Token(TokenClass.LSBR,ln,cn);
	case']':
	  return new Token(TokenClass.RSBR,ln,cn);
	case';':
	  return new Token(TokenClass.SC,ln,cn);
	case',':
	  return new Token(TokenClass.COMMA,ln,cn);
	case'=':
	  if(!sc.hasNext()||sc.peek()!='=')
		return new Token(TokenClass.ASSIGN,ln,cn);
	  sc.next();
	  return new Token(TokenClass.EQ,ln,cn);
	case'!':
	  if(!sc.hasNext()||sc.peek()!='='){//no unary not operation i guess?
		error(c,ln,cn);
		return new Token(TokenClass.INVALID,ln,cn);
	  }
	  sc.next();
	  return new Token(TokenClass.NE,ln,cn);
	case'<'://no bitshifts?
	  if(!sc.hasNext()||sc.peek()!='=')
		return new Token(TokenClass.LT,ln,cn);
	  sc.next();
	  return new Token(TokenClass.LE,ln,cn);
	case'>':
	  if(!sc.hasNext()||sc.peek()!='=')
		return new Token(TokenClass.GT,ln,cn);
	  sc.next();
	  return new Token(TokenClass.GE,ln,cn);
	case'+'://no increment, add assigns?
	  return new Token(TokenClass.PLUS,ln,cn);
	case'-'://no --, -=, ->
	  return new Token(TokenClass.MINUS,ln,cn);
	case'*':
	  return new Token(TokenClass.ASTERIX,ln,cn);
	case'%':
	  return new Token(TokenClass.REM,ln,cn);
	case'&':
	  if(!sc.hasNext()||sc.peek()!='&')
		return new Token(TokenClass.AND,ln,cn);
	  sc.next();
	  return new Token(TokenClass.LOGAND,ln,cn);
	case'|':
	  if(!sc.hasNext()||sc.peek()!='|'){//no bitwise or?
		error(c,ln,cn);
		return new Token(TokenClass.INVALID,ln,cn);
	  }
	  sc.next();
	  return new Token(TokenClass.LOGOR,ln,cn);
	case'.':
	  return new Token(TokenClass.DOT,ln,cn);
	case'/':
	  if(sc.hasNext())
		if(sc.peek()=='/'){
		  while(c!='\n')c=sc.next();//eof without newline means no more tokens
		  return next();
		}else if(sc.peek()=='*'){
		  sc.next();//need to consume the first one or we recognize /*/
		  while(sc.hasNext())
			if(sc.next()=='*'&&sc.hasNext()&&sc.peek()=='/'){//could have **/
			  sc.next();
			  return next();
			}
		  System.err.println("Reached eof without closing block comment");
		  error(c,ln,cn);
		  return new Token(TokenClass.INVALID,ln,cn);
		}
	  return new Token(TokenClass.DIV,ln,cn);
	case'#':
	  v=new StringBuilder();
	  while(sc.hasNext()&&(sc.peek()=='_'||Character.isLetterOrDigit(sc.peek())))
		v.append(sc.next());
	  if(v.toString().equals("include"))
		return new Token(TokenClass.INCLUDE,ln,cn);
	  System.err.println("Unrecognized preprosser (sp.) directive");
	  error(c,ln,cn);
	  return new Token(TokenClass.INVALID,ln,cn);
	case'"':
	  v=new StringBuilder();
	  while(sc.hasNext()){
		c=sc.next();
		switch(c){
		case'"':
		  return new Token(TokenClass.STRING_LITERAL,v.toString(),ln,cn);
		case'\\':
		  switch(sc.next()){
		  case't':
			v.append('\t');
			break;
		  case'b':
			v.append('\b');
			break;
		  case'n':
			v.append('\n');
			break;
		  case'f':
			v.append('\f');
			break;
		  case'\\':
			v.append('\\');
			break;
		  case'"':
			v.append('\"');
			break;
		  case'\'':
			v.append('\'');
			break;
		  case'0':
			v.append('\0');
			break;
		  default:
			System.err.println("Unrecognized escape code in string literal");
			error(c,ln,cn);
			return new Token(TokenClass.INVALID,ln,cn);
		  }
		  break;
		case'\n':
		  System.err.println("Multiline string literal");
		  error(c,ln,cn);
		  return new Token(TokenClass.INVALID,ln,cn);
		default:
		  v.append(c);
		}
	  }
	  System.err.println("Unclosed string");
	  error(c,ln,cn);
	  return new Token(TokenClass.INVALID,ln,cn);
	case'\'':
	  v=new StringBuilder();
	  while(sc.hasNext()){
		c=sc.next();
		switch(c){
		case'\'':
		  if(v.length()==1)
			return new Token(TokenClass.CHAR_LITERAL,v.toString(),ln,cn);
		  System.err.println("Char literal consisting of multiple characters");
		  error(c,ln,cn);
		  return new Token(TokenClass.INVALID,ln,cn);
		case'\\':
		  switch(sc.next()){
		  case't'://imagine decommposing things into functions couldn't be me
			v.append('\t');
			break;
		  case'b':
			v.append('\b');
			break;
		  case'n':
			v.append('\n');
			break;
		  case'f':
			v.append('\f');
			break;
		  case'\\':
			v.append('\\');
			break;
		  case'"':
			v.append('\"');
			break;
		  case'\'':
			v.append('\'');
			break;
		  case'0':
			v.append('\0');
			break;
		  default:
			System.err.println("Unrecognized escape code in char literal");
			error(c,ln,cn);
			return new Token(TokenClass.INVALID,ln,cn);
		  }
		  break;
		case'\n':
		  System.err.println("Multiline character literal");
		  error(c,ln,cn);
		  return new Token(TokenClass.INVALID,ln,cn);
		default:
		  v.append(c);
		}
	  }
	  System.err.println("Unclosed char literal");
	  error(c,ln,cn);
	  return new Token(TokenClass.INVALID,ln,cn);
	case'0':
	case'1':
	case'2':
	case'3':
	case'4':
	case'5':
	case'6':
	case'7':
	case'8':
	case'9':
	  v=new StringBuilder();
	  v.append(c);
	  while(sc.hasNext()&&Character.isJavaIdentifierPart(sc.peek())){
		c=sc.next();
		v.append(c);
		if(!Character.isDigit(c)){
		  System.err.println("Invalid number");
		  error(c,ln,cn);
		  return new Token(TokenClass.INVALID,v.toString(),ln,cn);
		}
	  }
	  return new Token(TokenClass.INT_LITERAL,v.toString(),ln,cn);
	default://identifier or keyword
	  if(c!='_'&&!Character.isLetter(c)){//unrecognized eg. ^
		error(c,ln,cn);
		return new Token(TokenClass.INVALID,ln,cn);
	  }
	  v=new StringBuilder();
	  v.append(c);
	  while(sc.hasNext()&&(sc.peek()=='_'||Character.isLetterOrDigit(sc.peek())))
		v.append(c=sc.next());
	  String s=v.toString();
	  if(s.equals("int"))
		return new Token(TokenClass.INT,ln,cn);
	  if(s.equals("void"))
		return new Token(TokenClass.VOID,ln,cn);
	  if(s.equals("char"))
		return new Token(TokenClass.CHAR,ln,cn);
	  if(s.equals("if"))
		return new Token(TokenClass.IF,ln,cn);
	  if(s.equals("else"))
		return new Token(TokenClass.ELSE,ln,cn);
	  if(s.equals("while"))
		return new Token(TokenClass.WHILE,ln,cn);
	  if(s.equals("return"))
		return new Token(TokenClass.RETURN,ln,cn);
	  if(s.equals("struct"))
		return new Token(TokenClass.STRUCT,ln,cn);
	  if(s.equals("sizeof"))
		return new Token(TokenClass.SIZEOF,ln,cn);
	  if(s.equals("class"))
		return new Token(TokenClass.CLASS,ln,cn);
	  if(s.equals("new"))
		return new Token(TokenClass.NEW,ln,cn);
	  if(s.equals("extends"))
		return new Token(TokenClass.EXTENDS,ln,cn);
	  return new Token(TokenClass.IDENTIFIER,s,ln,cn);
	}
  }
}
