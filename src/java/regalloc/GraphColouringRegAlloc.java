package regalloc;
import gen.asm.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		AssemblyProgram.Section spills=np.newSection(AssemblyProgram.Section.Type.DATA);//todo
		AssemblyProgram.Section func=np.newSection(AssemblyProgram.Section.Type.TEXT);
		for(Cfgnode n:cfg.nodes){
		  for(Instruction i:n.ins){
			List<AssemblyItem>misc=n.miscs.get(i);
			if(misc!=null)
			  for(AssemblyItem ai:misc)
				switch(ai){
				case Comment aaii->func.emit(aaii);
				case Instruction aaii->
				  throw new IllegalStateException("unreachable not misc");
				case Label aaii->func.emit(aaii);
				case Directive aaii->func.emit(aaii);
				};
			if(i.opcode==OpCode.PUSH_REGISTERS){
			  func.emit("pushing regs");//todo spilling
			  func.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,-4*infrg.numRegs);
			  for(int o=0;o<infrg.numRegs;o++)
				func.emit(OpCode.SW,infrg.regs[o],Register.Arch.sp,4*o);
			}else if(i.opcode==OpCode.POP_REGISTERS){
			  func.emit("popping regs");//todo spilling
			  for(int o=0;o<infrg.numRegs;o++)
				func.emit(OpCode.SW,infrg.regs[o],Register.Arch.sp,4*o);
			  func.emit(OpCode.ADDI,Register.Arch.sp,Register.Arch.sp,4*infrg.numRegs);
			}else{
			  Map<Register,Register>repl=new HashMap<Register,Register>();
			  for(Register reg:i.registers())
				if(reg instanceof Register.Virtual vr){
				  int r=infrg.allocs.get(reg);
				  switch(r){
				  case-2:
					repl.put(reg,Register.Arch.zero);
					break;
				  case-1:
					System.out.println("spilling!");
					break;
				  default:
					repl.put(reg,Infrg.regs[r]);
				  }
				}
			  //todo spilling
			  func.emit(i.rebuild(repl));
			}
		  }
		  if(n.lastmisc!=null)
			for(AssemblyItem ai:n.lastmisc)
			  switch(ai){
			  case Comment aaii->func.emit(aaii);
			  case Instruction aaii->
				throw new IllegalStateException("unreachable not misc");
			  case Label aaii->func.emit(aaii);
			  case Directive aaii->func.emit(aaii);
			  };
		}
	  }else
		np.emitSection(s);
	return np;
  }
}
