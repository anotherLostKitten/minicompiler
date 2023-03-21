package regalloc;
import gen.asm.Instruction;
import gen.asm.Register;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
public class InfrgPrinter{
  private final PrintWriter w;
  public InfrgPrinter(PrintWriter w){
	this.w=w;
  }
  private void printe(String n,String c){
	w.println(n+" --  "+c+";");
  }
  public void visit(List<Infrg>infrgs){
	w.println("graph ast {");
	for(Infrg infrg:infrgs){
	  String name=infrg.func!=null?infrg.func.toString():"initter";
	  w.println("subgraph cluster_"+name+" {\nlabel=\""+name+"\";");
	  for(Register.Virtual vr:infrg.edges.keySet())
		for(Register.Virtual ur:infrg.edges.get(vr))
		  if(ur.toString().compareTo(vr.toString())>0)
			printe(vr.toString(),ur.toString());
	  w.println("}");
	}
	w.println("}");
	w.flush();
  }
}
