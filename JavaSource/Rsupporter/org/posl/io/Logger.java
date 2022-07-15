package org.posl.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.posl.util.SimpleQueue;

public class Logger{

    public static final Logger MASTER = new Logger();

    private final SimpleQueue<String> buf = new SimpleQueue<>();
    private final File out;

    private Logger(){
        this.out = new File("./org/posl/io/log.txt");
    }

    public Logger(File out){
        this.out = out;
    }

    public synchronized void get(String log){
        buf.enqueue(log);
    }

    public synchronized void flush(){
        try(FileWriter writer = new FileWriter(out)){
            while(!buf.isEmpty()){
                writer.write(buf.dequeue()+"\n");
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }

    @Deprecated
    public synchronized void logi(String log){
        try(FileWriter writer = new FileWriter(out)){
            writer.write(log);
        }catch(IOException e){
            System.out.println(e);
        }
    }

}
