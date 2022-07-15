package org.posl.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.posl.data.file.ProjectUnit;
import org.posl.data.tree.TreeDriver;


public class Test{

    public static File debug = new File("./org/posl/test/debug.txt");

    public static void main(String[] args){
        var project = new ProjectUnit(new File("./"));
        // TreeDriver driver = new TreeDriver();
        // try(FileWriter f = new FileWriter(debug)){
        //     driver.drive(System.out::print);
        // }catch(IOException e){
        //     System.out.println(e);
        // }
    }

    public static File debugFile(){
        return new File("./org/posl/test/TestSource.java");
    }

    @FunctionalInterface
    public interface IOConsumer<T>{
        public void accept(T t) throws IOException;
    }

}
