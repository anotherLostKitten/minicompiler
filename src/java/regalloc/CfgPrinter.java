package regalloc;
import gen.asm.Instruction;
import gen.asm.Register;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
public class CfgPrinter{
  private final PrintWriter w;
  public CfgPrinter(PrintWriter w){
	this.w=w;
  }
  private void printv(int n,String l){
	w.println("N_"+n+" [label=\""+l+"\", shape=box];");
  }
  private void printe(int n,int c){
	w.println("N_"+n+" ->  N_"+c+";");
  }
  public void visit(List<Cfg>cfgs){
	int nc=0;
	w.println("digraph ast {");
	for(Cfg cfg:cfgs){
	  HashMap<Cfgnode,Integer>vs=new HashMap<Cfgnode,Integer>();
	  for(Cfgnode n:cfg.nodes){
		vs.put(n,nc);
		String res="[",tmp="";
		for(Register.Virtual vr:n.livein){
		  res+=tmp+vr.toString();
		  tmp=" ";
		}
		res+="]\\n";
		for(Instruction in:n.ins)
		  res+=in.toString()+"\\n";
		res+="[";
		tmp="";
		for(Register.Virtual vr:n.liveout){
		  res+=tmp+vr.toString();
		  tmp=" ";
		}
		res+="]";
		printv(nc++,res);
	  }
	  for(Cfgnode n:cfg.nodes){
		int vn=vs.get(n);
		for(Cfgnode s:n.succs)
		  printe(vn,vs.get(s));
	  }
	  if(cfg.func!=null)
		printv(nc,"FUNCITON: "+cfg.func.toString());
	  else
		printv(nc,"INITTER");
	  printe(nc,vs.get(cfg.nodes.get(0)));
	  nc++;
	}
	w.println("}");
	w.flush();
  }
}
