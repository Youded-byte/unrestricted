package me.youded.unrestricted.hooks;

import net.weavemc.loader.api.Hook;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ReachUnrestrictHook extends Hook {
    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        for (MethodNode method : classNode.methods) {
            if (!(method.access == Opcodes.ACC_PUBLIC && method.desc.equals("()Ljava/lang/String;")))
                continue;

            boolean isReachDisplayClass = Arrays.stream(method.instructions.toArray())
                    .filter(LdcInsnNode.class::isInstance)
                    .map(LdcInsnNode.class::cast)
                    .map(it -> it.cst)
                    .anyMatch("[1.3 blocks]"::equals);

            if (isReachDisplayClass) {
                for (MethodNode methoda : classNode.methods) {
                    for (AbstractInsnNode insn : methoda.instructions) {
                        if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.getClass() == Double.class
                                && (Double) ((LdcInsnNode) insn).cst == 3.0D) {
                            methoda.instructions.set(insn, new LdcInsnNode(300.00D));
                        }
                    }
                    if (methoda.name.equals("<clinit>") && methoda.desc.equals("()V")) {
                        for (AbstractInsnNode insn : methoda.instructions) {
                            if (insn.getOpcode() == Opcodes.LDC && ((LdcInsnNode) insn).cst.getClass() == String.class
                                    && ((String) ((LdcInsnNode) insn).cst).equals("#.##")) {
                                methoda.instructions.set(insn, new LdcInsnNode("0.00"));
                            }
                        }
                    }
                }
            }
        }
    }
}
