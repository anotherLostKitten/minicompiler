// Authors: Jonathan Van der Cruysse, Christophe Dubach
package gen.asm;
/**
 * A single item in an {@link AssemblyProgram.Section}. This typically corresponds to a line in the textual
 * representation of an assembly program.
 */
sealed public abstract class AssemblyItem permits Comment,Directive,Instruction,Label{
}
