import java.io.*;
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
    static PrinterClass printer;
    /**
     *
     * @param args Console Line Arguments
     * @throws IOException
     * Main Driver Function
     *
     */
    public static void main(String[] args) throws IOException
    {
        directoryPath=args[0];
        char videoMapperChoice=args[1].charAt(0);
        printer=new PrinterClass(directoryPath);
        printer.dualPrinter("This is Subtitle Formatter");
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
                        try
                        {
                            if(reNameFunction(fileName, newFileName))
                            {
                                printer.dualPrinter("This pair was a success\n");
                                incrementCount();
                            }
                            else
                            {
                                printer.dualPrinter("This pair was not a success\n");
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        if(reNameCount>0)
        {
            printer.dualPrinter(reNameCount + " srt files renamed");
        }
        else
        {
            printer.dualPrinter("No srt suitable files found");
        }
        if(videoMapperChoice=='Y'||videoMapperChoice=='y')
        {
            printer.dualPrinter("Moving control to Video Mapper");
            new videoFileMapper(directoryPath);
        }
        else
        {
            printer.dualPrinter("Video Mapper was not called");
        }
    }

    /**
     *
     * @param fileName oldFileName
     * @param newFileName Our target File Name
     * @return true of false depending whether the renaming was successful or not
     */

    static boolean reNameFunction(String fileName, String newFileName) throws FileNotFoundException
    {
        File oldFile = new File(directoryPath+"\\"+fileName);
        File newFile = new File(directoryPath+"\\"+newFileName);

        printer.dualPrinter("oldFile = " + oldFile);
        printer.dualPrinter("newFile = " + newFile);

        return oldFile.renameTo(newFile);
    }

    /**
     *
     * @param z File Name for which we need extension
     * @return extension from the FileName
     *
     */

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
    static PrinterClass printer;

    /**
     *
     * @param x directoryPath
     */
    videoFileMapper(String x) throws IOException
    {
        this.newSubtitleFormatter = new subtitleFormatter();
        this.directoryWithFormattedSubtitles=x;
        this.fileCollection();
        this.printer=new PrinterClass(this.directoryWithFormattedSubtitles);
    }

    /**
     * Collects all the files in the current directory and sorts them into two arrays, one for srt and one for video
     */
    public static void fileCollection() throws FileNotFoundException
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
            else if(extension.equalsIgnoreCase("mp4")||extension.equalsIgnoreCase("avi")||extension.equalsIgnoreCase("mkv"))
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

    /**
     * Calls the helper function reNameFunction in subtitleFormatter
     */
    static void callerOfRenamer() throws FileNotFoundException
    {
        int count=0;
        for(int i=0;i<numOfFiles;i++)
        {
            if(newSubtitleFormatter.reNameFunction(attachVideoExtension(videoFiles[i]),attachVideoExtension(subtitleFiles[i])))
            {
                printer.dualPrinter("This pair was a success\n");
                count++;
            }
            else
            {
                printer.dualPrinter("This pair was not a success\n");
            }
        }
        printer.dualPrinter(count + " video files renamed");
    }

    /**
     *
     * @param z Base File Name
     * @return File Name with attached Extension
     */
    static String attachVideoExtension(String z)
    {
        return z+"."+videoExtension;
    }

    /**
     *
     * @return number of formatted srt files in the directory
     */
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

    /**
     *
     * @param z Full File Name
     * @return Base File Name without extension
     */
    public static String getRidOfFileExtension(String z)
    {
        return z.substring(0, z.length() - 4);
    }
}

/**
 * Helper Class to print the log on the console as well as in a File
 */
class PrinterClass
{
    static String workingDirectory;
    static File file;
    static PrintWriter fileWriter;

    /**
     * Constructor to initialize PrintWriter Object and the File
     * @param z Working Directory
     * @throws IOException
     */
    PrinterClass(String z) throws IOException
    {
        workingDirectory=z;
        file = new File(workingDirectory+"/subtitleFormatterLog.txt");
        fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    }

    /**
     *
     * @param z Any String to be printed from the videoFileMapper and subtitleFormatter Class
     */
    static void dualPrinter(String z)
    {
        fileWriter.println(z);
        fileWriter.flush();
        System.out.println(z);
    }
}