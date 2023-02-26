// Authors: Jonathan Van der Cruysse, Christophe Dubach
package gen.asm;
import java.util.Objects;
/**
 * An assembler directive in a MIPS assembly program.
 */
public final class Directive extends AssemblyItem{
  private final String name;
  public Directive(String name){
	this.name=name;
  }
  public String toString(){
	return "."+name;
  }
  @Override
  public boolean equals(Object o){
	if(this==o)return true;
	if(o==null||getClass()!=o.getClass())return false;
	Directive directive=(Directive)o;
	return Objects.equals(name,directive.name);
  }
  @Override
  public int hashCode(){
	return Objects.hash(name);
  }
}
