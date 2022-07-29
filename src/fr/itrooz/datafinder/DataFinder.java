package fr.itrooz.datafinder;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class DataFinder {

    private static class Potencial{
        private final Object obj;
        private final String relativePath;

        public Potencial(Object obj, String relativePath) {
            this.obj = obj;
            this.relativePath = relativePath;
        }
    }

    private boolean checkFields = false;
    private boolean checkGetters = false;

    private final ArrayList<Object> ignored = new ArrayList<>();

    public DataFinder setCheckFields(boolean checkFields){
        this.checkFields = checkFields;
        return this;
    }

    public DataFinder setCheckGetters(boolean checkGetters) {
        this.checkGetters = checkGetters;
        return this;
    }

    public DataFinder ignore(Object obj){
        ignored.add(obj);
        return this;
    }

    public String find(Object root, Object wanted){
        try{
            List<String> found = findInternal(root, wanted, false);
            if(found.size()==0)return null;
            else return found.get(0);
        }catch(ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    public List<String> findAll(Object root, Object wanted){
        try{
            return findInternal(root, wanted, true);
        }catch(ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
    }

    private final Queue<Potencial> potencials = new ArrayDeque<>();

    private <T> List<String> findInternal(Object root, Object wanted, boolean findAll) throws IllegalAccessException {
        if(root==null||wanted==null)throw new IllegalArgumentException("Arguments cannot be null");

        ArrayList<Object> visited = new ArrayList<>();
        ArrayList<String> foundPaths = new ArrayList<>();

        potencials.add(new Potencial(root, ""));

        while(!potencials.isEmpty()){
            Potencial potencial = potencials.remove();

            if(_isEqual(wanted, potencial.obj)){
                foundPaths.add(potencial.relativePath);
                if(findAll)return foundPaths;
            }

            if(ignored.contains(potencial.obj))continue;
            if(visited.contains(potencial.obj))continue;
            visited.add(potencial.obj);


            if(checkFields)_addFieldsToLoop(potencial);
            if(checkGetters)_addGettersToLoop(potencial);
        }
        return foundPaths;
    }


    private <T> boolean _isEqual(T wanted, Object obj){
        return (wanted instanceof Integer && obj.hashCode()== (Integer) wanted) || (obj==wanted);
    }

    private void _addFieldsToLoop(Potencial potencial){
        for(Field f : potencial.obj.getClass().getDeclaredFields()){

            if(f.getType().getPackageName().startsWith("java.lang"))continue;
            if(Modifier.isStatic(f.getModifiers()))continue;


            String childRelativePath = potencial.relativePath + "." + f.getName();
            try {
                f.setAccessible(true);
                Object childObj = f.get(potencial.obj);

                if(childObj==null)continue;

                potencials.add(new Potencial(childObj, childRelativePath));
            }catch(ReflectiveOperationException|InaccessibleObjectException e){
                System.err.printf("Could not check field '%s'\n%s\n\n", childRelativePath, e.getMessage());
            }
        }
    }

    private void _addGettersToLoop(Potencial potencial){
        for(Method m : potencial.obj.getClass().getDeclaredMethods()){
            if(Modifier.isStatic(m.getModifiers()))continue;
            if(m.getReturnType().getPackageName().startsWith("java.lang"))continue;
            if(!m.getName().startsWith("get"))continue;

            String childRelativePath = potencial.relativePath + "." + m.getName()+"()";
            try {
                m.setAccessible(true);
                Object childObj = m.invoke(potencial.obj);

                if(childObj==null)continue;

                potencials.add(new Potencial(childObj, childRelativePath));
            }catch(ReflectiveOperationException|InaccessibleObjectException e){
                System.err.printf("Could not check field '%s'\n%s\n\n", childRelativePath, e.getMessage());
            }
        }
    }
}