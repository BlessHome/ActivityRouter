package com.bless.router.compiler;

import com.bless.router.annotation.RouterName;
import com.bless.router.annotation.RouterParam;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    private static final String DEFAULT_PACKAGE_NAME = "com.bless.router";
    private Elements elementUtils;
    private String routerClassPackageName = DEFAULT_PACKAGE_NAME;
    private String routerModuleName = "";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(RouterName.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        System.out.println("handle RouterProcessor"+annotations.size());
        if (annotations.size() == 0) {
            return false;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RouterName.class);
        ClassName activityRouteTableInitializer = ClassName.get(routerClassPackageName, "RouterInitializer");
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(routerModuleName + "RouterInitializer")
                .addSuperinterface(activityRouteTableInitializer)
                .addModifiers(Modifier.PUBLIC)
                .addStaticBlock(CodeBlock.of(String.format("Router.register(new %sRouterInitializer());", routerModuleName)));

        TypeElement activityRouteTableInitializerTypeElement = elementUtils.getTypeElement(activityRouteTableInitializer.toString());
        List<? extends Element> members = elementUtils.getAllMembers(activityRouteTableInitializerTypeElement);
        MethodSpec.Builder bindViewMethodSpecBuilder = null;
        for (Element element : members) {
//            System.out.println(element.getSimpleName());
            if ("init".equals(element.getSimpleName().toString())) {
                bindViewMethodSpecBuilder = MethodSpec.overriding((ExecutableElement) element);
                break;
            }
        }
        if (bindViewMethodSpecBuilder == null) {
            return false;
        }
        ClassName activityHelperClassName = ClassName.get(routerClassPackageName, "ActivityHelper");

        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (Element element : elements) {
            RouterName routerName = element.getAnnotation(RouterName.class);
            TypeElement typeElement = (TypeElement) element;
            for (String key : routerName.value()) {
                bindViewMethodSpecBuilder.addStatement("arg0.put($S, $T.class)", key, typeElement.asType());
            }
            ClassName className = buildActivityHelper(routerName.value()[0], activityHelperClassName, (TypeElement) element);

            MethodSpec methodSpec = MethodSpec.methodBuilder("get" + className.simpleName())
                    .addStatement("return new $T()", className)
                    .returns(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .build();
            methodSpecs.add(methodSpec);
        }
        TypeSpec typeSpecRouterHelper = TypeSpec.classBuilder(routerModuleName + "RouterHelper")
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs)
                .build();
        JavaFile javaFileRouterHelper = JavaFile.builder(routerClassPackageName, typeSpecRouterHelper).build();


        JavaFile javaFile = JavaFile.builder(routerClassPackageName, typeSpec.addMethod(bindViewMethodSpecBuilder.build()).build()).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            javaFileRouterHelper.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //TODO 支持在gradle关闭该功能
    private ClassName buildActivityHelper(String routerActivityName, ClassName activityHelperClassName, TypeElement typeElement) {
        List<? extends Element> members = elementUtils.getAllMembers(typeElement);
        List<MethodSpec> methodSpecs = new ArrayList<>();
        ClassName className = ClassName.get(routerClassPackageName, typeElement.getSimpleName() + "Helper");
        for (Element element : members) {
            RouterParam routerParam = element.getAnnotation(RouterParam.class);
            if (routerParam == null) {
                continue;
            }
            String name = element.getSimpleName().toString();
            if (name.length() >= 2 && name.charAt(0) == 'm' && Character.isUpperCase(name.charAt(1))) {
                name = name.substring(1, 2).toLowerCase() + name.substring(2);
            }
            String upperName = name.substring(0, 1).toUpperCase() + name.substring(1);
            MethodSpec methodSpec = MethodSpec.methodBuilder("with" + upperName)
                    .addParameter(TypeName.get(element.asType()), name)
                    .addStatement(String.format("put(\"%s\",%s )", routerParam.value()[0], name))
                    .addStatement("return this")
                    .returns(className)
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            methodSpecs.add(methodSpec);
        }
        MethodSpec methodSpec = MethodSpec.constructorBuilder()
                .addStatement("super($S)", routerActivityName)
                .addModifiers(Modifier.PUBLIC)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "Helper")
                .superclass(activityHelperClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs)
                .addMethod(methodSpec)
                .build();
        JavaFile javaFile = JavaFile.builder(routerClassPackageName, typeSpec).build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return className;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        Map<String, String> map = processingEnv.getOptions();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if ("routerModuleName".equals(key)) {
                this.routerModuleName = map.get(key);
            }
            System.out.println(key + " = " + map.get(key));
        }
        if (routerModuleName.length() > 0) {
            routerClassPackageName = DEFAULT_PACKAGE_NAME + "." + routerModuleName.toLowerCase();
        } else {
            routerModuleName = "Apt";
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}