package com.gudong.config;

import com.alibaba.dubbo.config.annotation.Reference;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
public class DynamicClassGenerator {
    public static Class<?> generateImplClass(Class<?> interfaceClass) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("com.alibaba.dubbo.config.annotation.Reference"); // 导入注解包

        // 生成实现类
        CtClass ctInterface = pool.get(interfaceClass.getName());
        CtClass implClass = pool.makeClass(interfaceClass.getName() + "Impl");
        implClass.addInterface(ctInterface);

        // 添加 @Reference 注解的字段
        CtField referenceField = new CtField(
            pool.get("org.apache.dubbo.samples.generic.call.api.HelloService"), // 假设目标接口为 IIaaService
            "iiaaService",
            implClass
        );

        // 创建 @Reference 注解并添加到字段
        AnnotationsAttribute attr = new AnnotationsAttribute(
            implClass.getClassFile().getConstPool(),
            AnnotationsAttribute.visibleTag
        );
        Annotation referenceAnnotation = new Annotation(Reference.class.getName(), implClass.getClassFile().getConstPool());
        attr.addAnnotation(referenceAnnotation);
        referenceField.getFieldInfo().addAttribute(attr);

        implClass.addField(referenceField);

        // 为接口方法生成默认实现（调用 IIaaService）
        for (CtMethod method : ctInterface.getDeclaredMethods()) {
            CtMethod implMethod = CtNewMethod.make(
                "public " + method.getReturnType().getName() + " " + method.getName() + "() { " +
                "   // 调用 IIaaService 的方法\n" +
                "   return iiaaService.sayHello();\n" + // 假设 IIaaService 有 process() 方法
                "}",
                implClass
            );
            implClass.addMethod(implMethod);
        }

        return implClass.toClass();
    }
}
