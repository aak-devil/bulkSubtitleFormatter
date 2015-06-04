package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        String s=args[0];
        Files.walk(Paths.get(s)).forEach(filePath ->
        {
            if (Files.isRegularFile(filePath))
            {
                System.out.println(filePath);
            }
        });
        System.out.println(s);
    }
}