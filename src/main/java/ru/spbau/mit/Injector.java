package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Injector {
    static Map<Class, Boolean> isTake = new HashMap<Class, Boolean>();
    static Map<Class, Object> instancesClasses = new HashMap<>();
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */
    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class rootClass = Class.forName(rootClassName);
        if (isTake.containsKey(rootClass) && isTake.get(rootClass)) {
            throw new InjectionCycleException();
        }
        isTake.put(rootClass, true);
        Constructor rootConstructor = rootClass.getConstructors()[0];

        Class[] rootParamTypes = rootConstructor.getParameterTypes();
        if (rootParamTypes.length == 0) {
            Object instance = rootClass.newInstance();
            instancesClasses.put(rootClass, instance);
            isTake.put(rootClass, false);
            return instance;
        } else {
            Map<Class, Class> classWithcRealizationInter = new HashMap<>();
            for (Class needRealization: rootParamTypes) {
                classWithcRealizationInter.put(needRealization, null);
            }
            for (String implemClassesName: implementationClassNames) {
                Class curImplemClass = Class.forName(implemClassesName);
                Class[] curInterface = curImplemClass.getInterfaces();
                for (Class curParameter: rootParamTypes) {
                    for (Class realizInterface: curInterface) {
                        if (curParameter == realizInterface) {
                            if (classWithcRealizationInter.get(curParameter) != null) {
                                throw new AmbiguousImplementationException();
                            } else {
                                classWithcRealizationInter.put(curParameter, curImplemClass);
                            }
                        }
                    }
                    if (curParameter == curImplemClass) {
                        if (classWithcRealizationInter.get(curParameter) != null) {
                            throw new AmbiguousImplementationException();
                        } else {
                            classWithcRealizationInter.put(curParameter, curImplemClass);
                        }
                    }
                }
            }
            LinkedList<Class> paramTypes = new LinkedList<>();
            LinkedList<Object> args = new LinkedList<>();
            for (Class needRealization: rootParamTypes) {
                if (classWithcRealizationInter.get(needRealization) == null) {
                    throw new ImplementationNotFoundException();
                } else {
                    paramTypes.add(classWithcRealizationInter.get(needRealization));
                    args.add(initialize(classWithcRealizationInter.get(needRealization).getName() ,implementationClassNames));
                }
            }
            isTake.put(rootClass, false);
            return rootConstructor.newInstance(args.toArray());
        }
    }
}