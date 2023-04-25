package lexer;
import util.Position;
/**
 * @author cdubach
 */
public class Token{
  public enum TokenClass{
	IDENTIFIER,//('a'-'z'|'A'-'Z'|'_')('0'-'9'|'a'-'z'|'A'-'Z'|'_')*
	ASSIGN,//'='
	LBRA,//'{'
	RBRA,//'}'
	LPAR,//'('
	RPAR,//')'
	LSBR,//'['
	RSBR,//']'
	SC,//';'
	COMMA,
	INT,
	VOID,
	CHAR,
	IF,
	ELSE,
	WHILE,
	RETURN,
	STRUCT,
	SIZEOF,
	CLASS,
	NEW,
	EXTENDS,
	INCLUDE,//"#include"
	STRING_LITERAL,//\".*\"
	INT_LITERAL,//('0'|...|'9')+
	CHAR_LITERAL,//\'('a'-'z'|'A'-'Z'|'\t'|'\b'|'\n'|'\r'|'\f'|'\''|'\"'|'\\'|'\0'|'.'|','|'_'|...)\'
	LOGAND,//"&&"
	LOGOR, //"||"
	EQ,//"=="
	NE,//"!="
	LT,//'<'
	GT,//'>'
	LE,//"<="
	GE,//">="
	PLUS,
	MINUS,
	ASTERIX,//'*'--can be used for multiplication or pointers
	DIV,//'/'
	REM,//'%'
	AND,//'&'
	DOT,//'.'
	EOF,//signal end of file
	INVALID//in case we cannot recognise a character as part of a valid token
  }
  public final TokenClass tokenClass;
  public final String data;
  public final Position position;
  public Token(TokenClass type,int lineNum,int colNum){
	this(type,"",lineNum,colNum);
  }
  public Token(TokenClass tokenClass,String data,int lineNum,int colNum){
	assert(tokenClass!=null);
	this.tokenClass=tokenClass;
	this.data=data;
	this.position=new Position(lineNum,colNum);
  }
  @Override
  public String toString(){
	if(data.equals(""))
	  return tokenClass.toString();
	else
	  return tokenClass.toString()+"("+data+")";
  }
}
