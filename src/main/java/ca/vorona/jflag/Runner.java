package ca.vorona.jflag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import one.elf.*;
import sun.misc.Unsafe;

public class Runner {
    
    private static long baseAddress = 0;
    static ElfSymbolTable symtab;
    static Unsafe unsafe;
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting the magic");
        unsafe = getUnsafe();
        String jvmLibraryString = findJvmMaps();
        System.out.println("JVM library string " + jvmLibraryString);
        String[] parts = jvmLibraryString.split("\\s");
        String jvmLibrary = parts[parts.length - 1];
        System.out.println("JVM library " + jvmLibrary);
        String baseAddressString = "0x" + parts[0];
        System.out.println("Base Address " + baseAddressString);
        baseAddress = Long.decode(baseAddressString.split("-")[0]);
        
        ElfReader elfReader = new ElfReader(jvmLibrary);
        symtab = (ElfSymbolTable) elfReader.section(".symtab");
        
        // Now it is time to do some fun!
        setBooleanFlag("PrintOopAddress", true);
        
        
        System.out.println("Reached the end");
    }
    
    private static Unsafe getUnsafe() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe;
    }

    private static String findJvmMaps() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/proc/self/maps"));
        try {
            for (String s; (s = reader.readLine()) != null; ) {
                if (s.endsWith("/libjvm.so")) {
                    return s;
                }
            }
            throw new IOException("libjvm.so not found");
        } finally {
            reader.close();
        }
    }

    private static ElfSymbol findSymbol(String name) {
        for (ElfSymbol symbol : symtab) {
            if (name.equals(symbol.name()) && symbol.type() == ElfSymbol.STT_OBJECT) {
                return symbol;
            }
        }
        throw new NoSuchElementException("Symbol not found: " + name);
    }

    public static void setIntFlag(String name, int value) throws Exception {
        if(baseAddress == 0) {
            throw new Exception("baseAddress is not intialized");
        }
        ElfSymbol symbol = findSymbol(name);
        unsafe.putInt(baseAddress + symbol.value(), value);
    }

    public static void setBooleanFlag(String name, boolean value) throws Exception {
        setIntFlag(name, value ? 1 : 0);
    }
}
