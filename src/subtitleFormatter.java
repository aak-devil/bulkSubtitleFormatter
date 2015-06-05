import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 *
 * bulk Subtitle Formatter v1.0
 *
 * subtitleFormatter Class
 *
 * Pass a directory path and it will format all files down to base names.
 *
 * Assuming subtitles are from addic7ed.com
 *
 * For more details read the inspiration inside the src folder
 *
 *
 * Usage: javac subtitleFormatter.java
 *        java  subtitleFormatter [directoryPath] [Yes/No]
 *
 *
 *
 * Params: 1. Directory in which raw addic7ed subtitles are there
 *         2. Yes or No, in case you want to call the VideoFileMappper as well
 *
 *         Video File Mapper will map renamed to correct Video File and then rename it to the format described in inspiration
 *
 * Example: java E:\TV Shows\Silicon Valley\Season 1 No
 * Output: 8 files renamed
 *         Video Mapper was not called
 *
 * Created by aak-devil on 04-06-2015
 *
 *
 */

public class subtitleFormatter
{
    static String directoryPath;
    static int reNameCount=0;
    public static void main(String[] args) throws IOException
    {
        directoryPath=args[0];
        char videoMapperChoice=args[1].charAt(0);
        System.out.println("This is Subtitle Formatter");
        /**
         * Using Java 8 Lambda Functions to iterate through the directory
         */

        Files.walk(Paths.get(directoryPath)).forEach(filePath ->
        {
            if (Files.isRegularFile(filePath))
            {
                String fileName = filePath.getFileName().toString();
                String extension = getExtension(fileName);
                if (extension.equalsIgnoreCase("srt"))
                {
                    int indexOfFirstPeriod = fileName.indexOf('.');
                    String newFileName = fileName.substring(0, indexOfFirstPeriod + 1) + extension;
                    if (!(fileName.equals(newFileName)))
                    {
                        if(reNameFunction(fileName, newFileName))
                        {
                            incrementCount();
                        }
                    }
                }
            }
        });
        if(reNameCount>0)
        {
            System.out.println(reNameCount + " srt files renamed");
        }
        else
        {
            System.out.println("No srt suitable files found");
        }
        if(videoMapperChoice=='Y'||videoMapperChoice=='y')
        {
            System.out.println("Moving control to Video Mapper");
            new videoFileMapper(directoryPath);
        }
        else
        {
            System.out.println("Video Mapper was not called");
        }
    }

    static boolean reNameFunction(String fileName, String newFileName)
    {
        File oldFile = new File(directoryPath+"\\"+fileName);
        File newFile = new File(directoryPath+"\\"+newFileName);

        System.out.println("oldFile = " + oldFile);
        System.out.println("newFile = " + newFile);

        return oldFile.renameTo(newFile);
    }

    static String getExtension(String z)
    {
        return z.substring(z.lastIndexOf('.') + 1);
    }
    static void incrementCount()
    {
        reNameCount++;
    }
}



/**
 *
 * videoFileMapper Class
 *
 * Created by aak-devil on 05-06-2015
 *
 * Takes a directory with formatted Subtitles and maps subtitle files to video files
 *
 * Assumes num(subtitleFiles) = num(videoFiles)
 *
 * Also assumes that raw video files will be alphabetically sorted. Something like this:
 *
 *  Silicon.Valley.S01E01.HDTV.x264-KILLERS
 *  Silicon.Valley.S01E02.HDTV.x264-2HD
 *  Silicon.Valley.S01E03.HDTV.x264-KILLERS
 *  Silicon.Valley.S01E04.HDTV.x264-KILLERS
 *  silicon.valley.s01e05.hdtv.x264-killers
 *  silicon.valley.s01e06.hdtv.x264-2hd
 *  silicon.valley.s01e07.hdtv.x264-killers
 *  Silicon.Valley.S01E08.HDTV.x264-KILLERS
 *
 *  Basically the general torrent nomenclature
 *
 */

class videoFileMapper
{
    static File workingDirectory;
    static String directoryWithFormattedSubtitles;
    static String allFilesInDirectory[];
    static String videoFiles[];
    static String subtitleFiles[];
    static String videoExtension;
    static subtitleFormatter newSubtitleFormatter;
    static int numOfFiles;

    videoFileMapper(String x)
    {
        this.newSubtitleFormatter = new subtitleFormatter();
        this.directoryWithFormattedSubtitles=x;
        this.fileCollection();
    }

    public static void fileCollection()
    {
        numOfFiles=quicklyTellMeNumberOfSubtitles();
        videoFiles=new String[numOfFiles];
        subtitleFiles=new String[numOfFiles];
        int subIndex=0;
        int vidIndex=0;
        for (String fileNameIterator : allFilesInDirectory)
        {
            String extension=new subtitleFormatter().getExtension(fileNameIterator);
            if(extension.equalsIgnoreCase("srt"))
            {
                subtitleFiles[subIndex++]=getRidOfFileExtension(fileNameIterator);
            }
            else if(extension.equalsIgnoreCase("mp4")||extension.equalsIgnoreCase("avi"))
            {
                if(vidIndex==0)
                {
                    videoExtension=extension;
                }
                videoFiles[vidIndex++]=getRidOfFileExtension(fileNameIterator);
            }
        }
        callerOfRenamer();
    }

    static void callerOfRenamer()
    {
        int count=0;
        for(int i=0;i<numOfFiles;i++)
        {
            if(newSubtitleFormatter.reNameFunction(attachVideoExtension(videoFiles[i]),attachVideoExtension(subtitleFiles[i])))
            {
                count++;
            }
        }
        System.out.println(count+" video files renamed");
    }

    static String attachVideoExtension(String z)
    {
        return z+"."+videoExtension;
    }

    static int quicklyTellMeNumberOfSubtitles()
    {
        int count=0;
        workingDirectory = new File(directoryWithFormattedSubtitles);
        allFilesInDirectory=workingDirectory.list();
        for (String fileName : allFilesInDirectory)
        {
            if((newSubtitleFormatter.getExtension(fileName).equalsIgnoreCase("srt")))
            {
                count++;
            }
        }
        return count;
    }

    public static String getRidOfFileExtension(String z)
    {
        return z.substring(0,z.length()-4);
    }
}