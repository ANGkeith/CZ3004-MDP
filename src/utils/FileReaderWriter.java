package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
