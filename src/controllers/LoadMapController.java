package controllers;

import utils.FileReaderWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.FileSystems;
import models.Arena;
import static models.Constants.*;


public class LoadMapController
        implements ActionListener
{
    Arena userDefinedArena;

    public LoadMapController(Arena userDefinedArena)
    {
        this.userDefinedArena = userDefinedArena;
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(userDefinedArena.obstacleToString());
        try {
            FileReaderWriter fileWriter = new FileReaderWriter(FileSystems.getDefault().getPath(ARENA_DESCRIPTOR_PATH, new String[0]));
            fileWriter.write(userDefinedArena.obstacleToString());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}