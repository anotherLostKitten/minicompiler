package sem;
import java.util.Map;
import java.util.HashMap;
public class Scope{
  public Scope outer;
  private Map<String,Symbol>symbolTable;
  public Scope(Scope outer){ 
	this.outer=outer;
	this.symbolTable=new HashMap<String,Symbol>();
  }
  public Scope(){
	this(null);
  }
  public Symbol lookup(String name){
	Symbol s=symbolTable.get(name);
	return s!=null?s:outer!=null?outer.lookup(name):null;
  }
  public Symbol lookupCurrent(String name){
	return symbolTable.get(name);
  }
  public void put(Symbol sym){
	symbolTable.put(sym.name,sym);
  }
}
