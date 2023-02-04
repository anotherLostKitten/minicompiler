package sem;
import java.util.Map;
public class Scope{
  private Scope outer;
  private Map<String,Symbol>symbolTable;
  public Scope(Scope outer){ 
	this.outer=outer; 
  }
  public Scope(){
	this(null);
  }
  public Symbol lookup(String name){
	//todo
	return null;
  }
  public Symbol lookupCurrent(String name){
	//todo
	return null;
  }
  public void put(Symbol sym){
	symbolTable.put(sym.name,sym);
  }
}
