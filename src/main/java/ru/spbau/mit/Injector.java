package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.*;


public class Injector {
    static Map<Class, Boolean> isTake;
    static Map<Class, Object> instancesClasses;
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */

    private static Object findImplementation(Class<?> parameter, List<String> implementationClassNames) throws Exception {
        Object returnObject = null;

        for (String nameClass: implementationClassNames) {
            Class curClass = Class.forName(nameClass);
            if (parameter.isAssignableFrom(curClass) && returnObject != null) {
                throw new AmbiguousImplementationException();
            }
            if (parameter.isAssignableFrom(curClass)) {
                if (instancesClasses.containsKey(parameter)) {
                    returnObject = instancesClasses.get(parameter);
                } else {
                    returnObject = doInstance(curClass.getName(), implementationClassNames);
                }
            }
        }

        return returnObject;
    }

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        isTake = new HashMap<>();
        instancesClasses  = new HashMap<>();
        List<String> allImplementationClassName = new LinkedList<>(implementationClassNames);
        allImplementationClassName.add(rootClassName);
        return doInstance(rootClassName, allImplementationClassName);
    }

    public static Object doInstance(String rootClassName, List<String> implementationClassNames) throws Exception {
        Class rootClass = Class.forName(rootClassName);

        if (instancesClasses.containsKey(rootClass))
            return instancesClasses.get(rootClass);

        if (isTake.containsKey(rootClass) && isTake.get(rootClass)) {
            throw new InjectionCycleException();
        }

        isTake.put(rootClass, true);
        Constructor rootConstructor = rootClass.getConstructors()[0];
        Class[] rootParamTypes = rootConstructor.getParameterTypes();
        if (rootParamTypes.length == 0) {
            Object instance;
            try {
                instance = rootClass.newInstance();
            } catch (AssertionError e) {
                throw e;
//                throw new AmbiguousImplementationException();
            }
            instancesClasses.put(rootClass, instance);
            isTake.put(rootClass, false);
            return instance;
        } else {
            LinkedList<Object> args = new LinkedList<>();

            for (Class curParameter: rootParamTypes) {
                args.add(findImplementation(curParameter, implementationClassNames));
            }

            for (Object parameter: args) {
                if (parameter == null) {
                    throw new ImplementationNotFoundException();
                }
            }

            isTake.put(rootClass, false);
            Object returnValue = rootConstructor.newInstance(args.toArray());
            instancesClasses.put(rootClass, returnValue);
            return returnValue;
        }
    }
}