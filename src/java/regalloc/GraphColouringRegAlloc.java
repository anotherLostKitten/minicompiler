package regalloc;
import gen.asm.*;
import java.util.ArrayList;
import java.util.List;
public class GraphColouringRegAlloc implements AssemblyPass{
  public static final GraphColouringRegAlloc INSTANCE=new GraphColouringRegAlloc();
  public List<Cfg>cfgs;
  public List<Infrg>infrgs;
  public GraphColouringRegAlloc(){
	this.cfgs=new ArrayList<Cfg>();
	this.infrgs=new ArrayList<Infrg>();
  }
  @Override
  public AssemblyProgram apply(AssemblyProgram prog){
	AssemblyProgram np=new AssemblyProgram();
	for(AssemblyProgram.Section s:prog.sections)
	  if(s.type==AssemblyProgram.Section.Type.TEXT){
		Cfg cfg=new Cfg(s);
		cfgs.add(cfg);//so i can print them
		Infrg infrg=new Infrg(cfg);
		infrgs.add(infrg);//so i can print them
	  }else
		np.emitSection(s);
	return np;
  }
}
