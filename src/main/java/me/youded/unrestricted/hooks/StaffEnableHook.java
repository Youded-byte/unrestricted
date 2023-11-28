package me.youded.unrestricted.hooks;

import net.weavemc.loader.api.Hook;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class StaffEnableHook extends Hook {
    @Override
    public void transform(@NotNull ClassNode classNode, @NotNull AssemblerConfig assemblerConfig) {
        for (MethodNode method : classNode.methods) {
            if (!(method.access == Opcodes.ACC_PUBLIC && method.desc.endsWith(";FFFFFFZ)V")))
                continue;
            
            boolean isStaffEnableClass = Arrays.stream(method.instructions.toArray())
                    .filter(MethodInsnNode.class::isInstance)
                    .map(MethodInsnNode.class::cast)
                    .map(it -> it.name)
                    .anyMatch("getName"::equals);
            
            if (isStaffEnableClass) {
                for (MethodNode methoda : classNode.methods) {
                    if (methoda.access == Opcodes.ACC_PUBLIC && methoda.desc.equals("()Z")) {
                        methoda.instructions.clear();
                        methoda.localVariables.clear();
                        methoda.exceptions.clear();
                        methoda.tryCatchBlocks.clear();
                        methoda.instructions.add(new InsnNode(Opcodes.ICONST_1));
                        methoda.instructions.add(new InsnNode(Opcodes.IRETURN));
                    }
                }
            }
        }
    }
}
