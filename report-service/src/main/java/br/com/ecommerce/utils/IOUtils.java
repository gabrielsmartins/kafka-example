package br.com.ecommerce.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class IOUtils {

    public static void copyTo(Path source, File target) {
        try{
            target.getParentFile().mkdir();
            Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public static void append(File target, String content) {
        try{
            Files.write(target.toPath(), content.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
