// Authors: Jonathan Van der Cruysse, Christophe Dubach
package gen.asm;
import java.util.Objects;
/**
 * A comment in an assembly program. Comments do not change the meaning of programs, but may aid humans in their
 * understanding of programs.
 */
public final class Comment extends AssemblyItem{
  public final String comment;
  public Comment(String comment){
	this.comment=comment;
  }
  public String toString(){
	return "# "+comment;
  }
  @Override
  public boolean equals(Object o){
	if(this==o)return true;
	if(o==null||getClass()!=o.getClass())return false;
	Comment comment1=(Comment)o;
	return comment.equals(comment1.comment);
  }
  @Override
  public int hashCode(){
	return Objects.hash(comment);
  }
}
