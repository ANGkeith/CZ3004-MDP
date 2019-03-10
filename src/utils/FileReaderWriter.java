package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static models.Constants.LOG_PATH;

public class FileReaderWriter {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private Path file;
    private OutputStreamWriter writer;
    private BufferedReader reader;

    public FileReaderWriter(Path file) throws IOException {
        this.file = file;
        if (!Files.exists(this.file)) {
            Files.createDirectories(this.file.getParent());
            Files.createFile(this.file);
        }
    }

    public void write(String str) throws IOException {
        OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        writer = new OutputStreamWriter(out, DEFAULT_ENCODING);
        writer.write(str);
        writer.close();
    }

    public void logMsg(String str, Boolean append) {
        try {
            OutputStream output = new FileOutputStream(LOG_PATH, append);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
            outputStreamWriter.write(str);
            outputStreamWriter.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public String read() throws IOException {
        InputStream in = Files.newInputStream(file);
        reader = new BufferedReader(new InputStreamReader(in, DEFAULT_ENCODING));
        String text = "";
        String line;

        while ((line = reader.readLine()) != null) {
            text += line;
        }
        reader.close();
        return text;
    }

}
