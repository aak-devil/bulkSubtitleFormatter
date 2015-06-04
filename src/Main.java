import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{
    static String directoryPath;
    static int reNameCount=0;
    public static void main(String[] args) throws IOException
    {
        directoryPath=args[0];

        Files.walk(Paths.get(directoryPath)).forEach(filePath ->
        {
            if (Files.isRegularFile(filePath))
            {
                String fileName=filePath.getFileName().toString();
                int indexOfLastPeriod=fileName.lastIndexOf('.');
                String extension=fileName.substring(indexOfLastPeriod+1);
                if(extension.equalsIgnoreCase("srt"))
                {
                    int indexOfFirstPeriod=fileName.indexOf('.');
                    String newFileName=fileName.substring(0,indexOfFirstPeriod+1)+extension;
                    if(reNameFunction(fileName, newFileName))
                    {
                        incrementCount();
                    }
                }
            }
        });
        System.out.println(reNameCount+" files renamed");
    }

    static boolean reNameFunction(String fileName, String newFileName)
    {
        return false;
    }

    static void incrementCount()
    {
        reNameCount++;
    }
}