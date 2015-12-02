package ru.spbau.mit;

import java.lang.reflect.Constructor;
import java.util.*;


public class Injector {
    static Map<Class, Boolean> isTake = new HashMap<Class, Boolean>();
    static Map<Class, Object> instancesClasses = new HashMap<>();
    /**
     * Create and initialize object of `rootClassName` class using classes from
     * `implementationClassNames` for concrete dependencies.
     */

    private static Object findImplementation(Class parameter, List<String> implementationClassNames) throws Exception {
        if (instancesClasses.containsKey(parameter)) {
            return instancesClasses.get(parameter);
        }

        Object returnObject = null;

        for (String nameClass: implementationClassNames) {
            Class curClass = Class.forName(nameClass);
            LinkedList<Class> interfacesCurClass = new LinkedList(Arrays.asList(curClass.getInterfaces()));
            interfacesCurClass.add(curClass);
            for (Class interfaceCurClass: interfacesCurClass) {
                if (interfaceCurClass == parameter && returnObject != null) {
                    throw new AmbiguousImplementationException();
                }
                if (interfaceCurClass == parameter) {
                    returnObject = doInstance(curClass.getName(), implementationClassNames);
                }
            }
        }

        return returnObject;
    }

    public static Object initialize(String rootClassName, List<String> implementationClassNames) throws Exception {
        LinkedList<String> allImplementationClassName = new LinkedList<>(implementationClassNames);
        allImplementationClassName.add(rootClassName);
        return doInstance(rootClassName, allImplementationClassName);
    }

    public static Object doInstance(String rootClassName, List<String> implementationClassNames) throws Exception
    {
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
            Object instance = rootClass.newInstance();
            instancesClasses.put(rootClass, instance);
            isTake.put(rootClass, false);
            return instance;
        } else {
            Map<Class, Class> classImplParameter = new HashMap<>();
            for (Class needRealization: rootParamTypes) {
                classImplParameter.put(needRealization, null);
            }

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
            return rootConstructor.newInstance(args.toArray());
        }
    }
}