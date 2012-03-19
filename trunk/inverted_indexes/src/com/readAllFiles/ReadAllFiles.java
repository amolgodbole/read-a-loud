package com.readAllFiles;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class ReadAllFiles {

    static String inpath = "";

    public static void main(String[]args) throws IOException{
        File Folder = new File(inpath);
        File files[];
        files = Folder.listFiles();




        if(files.length>1)
        {
            for(int i = 0;i<files.length; i++){
                System.out.println("reading...");
                System.out.println(files[i]);
                Reader in = new FileReader(files[i]);
                
                
                
             

                // should be in finally block
                in.close();
       
            }
        }
        else{
            System.out.println("found only one file...");
            System.out.println(files);
        }


    }

}