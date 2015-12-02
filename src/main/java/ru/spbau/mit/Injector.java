package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
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
        if (returnObject == null) {
            throw new ImplementationNotFoundException();
        }
        return returnObject;
    }

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        isTake = new HashMap<>();
        instancesClasses  = new HashMap<>();
        List<String> allImplementationClassName = new LinkedList<>(implementationClassNames);
        if ((Class.forName(rootClassName).getModifiers() & (Modifier.INTERFACE | Modifier.ABSTRACT)) == 0) {
            allImplementationClassName.add(rootClassName);
        }
        return findImplementation(Class.forName(rootClassName), allImplementationClassName);
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
        assert (rootConstructor != null);
        Class[] rootParamTypes = rootConstructor.getParameterTypes();
        LinkedList<Object> args = new LinkedList<>();

        for (Class curParameter : rootParamTypes) {
            args.add(findImplementation(curParameter, implementationClassNames));
        }

        isTake.put(rootClass, false);
        Object returnValue = rootConstructor.newInstance(args.toArray());
        instancesClasses.put(rootClass, returnValue);
        return returnValue;
    }
}