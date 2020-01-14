package bgu.spl.mics.application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class FilePrinter {
    private static class SingletonHolder {
        private static FilePrinter instance = new FilePrinter();
    }

    private FilePrinter() {

    }

    public static FilePrinter getInstance() {
        return SingletonHolder.instance;
    }

    public void SerializeObject(Object obj, String filename) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(filename);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
