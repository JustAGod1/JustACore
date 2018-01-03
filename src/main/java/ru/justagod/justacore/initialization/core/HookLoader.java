package ru.justagod.justacore.initialization.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by JustAGod on 28.12.2017.
 */
@SuppressWarnings("unused")
public class HookLoader implements IFMLLoadingPlugin, IClassTransformer {

    static Set<String> toModify = new LinkedHashSet<>();

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{HookLoader.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("cpw.mods.fml.common.Loader")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor ca = new ClassVisitor(Opcodes.ASM5, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("loadMods")) {
                        return new LoadMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
                    } else return super.visitMethod(access, name, desc, signature, exceptions);
                }
            };
            cr.accept(ca, ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        } else if (transformedName.equals("cpw.mods.fml.common.LoadController")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor ca = new ClassVisitor(Opcodes.ASM4, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    if (name.equals("sendEventToModContainer")) {
                        return new SendMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
                    } else return super.visitMethod(access, name, desc, signature, exceptions);
                }
            };
            cr.accept(ca, ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        } else if (toModify.contains(transformedName)) {
            ClassReader cr = new ClassReader(basicClass);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
                @Override
                public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                    return super.visitField((access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL && (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC ? access ^ Opcodes.ACC_FINAL : access, name, desc, signature, value);
                }
            };
            cr.accept(cv, ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        } else return basicClass;
    }
}
