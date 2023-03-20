package regalloc;
import java.io.PrintWriter;
import java.util.List;
import java.util.HashMap;
public class CfgPrinter{
  private final PrintWriter w;
  public CfgPrinter(PrintWriter w){
	this.w=w;
  }
  private void printv(int n,String l){
	w.println("N_"+n+" [label=\""+l+"\"];");
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
		printv(nc++,n.ins.toString());
	  }
	  for(Cfgnode n:cfg.nodes){
		int vn=vs.get(n);
		for(Cfgnode p:n.preds)
		  printe(vs.get(p),vn);
	  }
	}
	w.println("}");
	w.flush();
  }
}
