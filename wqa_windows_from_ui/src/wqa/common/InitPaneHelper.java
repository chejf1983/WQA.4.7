/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wqa.common;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author jiche
 */
public abstract class InitPaneHelper {

    private static String LastPath = "";

    public static void main(String... args) {
        System.out.println(InitPaneHelper.GetDirPath());
        System.out.println(InitPaneHelper.GetDirPath());
    }

    public static String GetFilePath(final String filend) {
        JFileChooser dialog = new JFileChooser(LastPath);
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return filend;
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                return f.getName().endsWith(filend);
            }
        });

        if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            LastPath = dialog.getSelectedFile().getParent();
            String file_path = dialog.getSelectedFile().getAbsolutePath();
            if (!file_path.endsWith(filend)) {
                file_path += filend;
            }
            return file_path;
        } else {
            return null;
        }
    }

    public static String GetDirPath() {
        JFileChooser dialog = new JFileChooser(LastPath);
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = dialog.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            LastPath = dialog.getSelectedFile().getAbsolutePath();
            return dialog.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }
}
