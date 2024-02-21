package client;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import java.awt.*;
import java.awt.event.*;

import pak.*;

import com.alibaba.fastjson.*;
import java.io.*;

public class ConfigFrame extends MyFrame{

    private static final long serialVersionUID = -1345646359894704159L;
    private static MyFrame configFrame = new MyFrame();
    final static int CONFIG_WIDTH = 420;
    final static int CONFIG_HEIGHT = 570;

    static StringBuffer dataPathStringBuffer;
    static String dataPathString;
    static File path;
    static String language;

    public static void configInit() throws IOException
    {
        firstInit(configFrame,CONFIG_WIDTH,CONFIG_HEIGHT);
        
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                configFrame.dispose();
            }
        });

        dataPathString=settingLocal.dataPath;
        language=settingLocal.language;

        JLabel pathText=new JLabel(langLocal.dataPath);
        pathText.setFont(new Font("微软雅黑",Font.PLAIN,calres(22)));
        pathText.setBounds(calres(155),calres(61),calres(112),calres(30));
        configFrame.add(pathText);

        JTextArea dataPath=new JTextArea();
        dataPath.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
        dataPath.setBounds(calres(76),calres(108),calres(270),calres(92));
        dataPath.setBorder(null);
        dataPath.setBorder(new CompoundBorder(dataPath.getBorder(),new EmptyBorder(calres(5),calres(15),0,0)));
		dataPath.setLineWrap(true);
        dataPath.setWrapStyleWord(true);
        dataPath.setEditable(false);
        dataPath.setText(settingLocal.dataPath);
        configFrame.add(dataPath);

        JButton changePath=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		changePath.setBorderPainted(false);
        changePath.setFocusPainted(false);
		changePath.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		changePath.setBounds(calres(47),calres(222),calres(150),calres(48));
		changePath.setText(langLocal.changePath);
		changePath.setHorizontalTextPosition(SwingConstants.CENTER);
        changePath.setVerticalTextPosition(SwingConstants.CENTER);
		changePath.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		changePath.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		changePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc=new JFileChooser();
                FileSystemView fsv = FileSystemView .getFileSystemView();
                jfc.setCurrentDirectory(fsv.getHomeDirectory());
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (jfc.showOpenDialog(configFrame)==JFileChooser.APPROVE_OPTION){ 
                    path=jfc.getSelectedFile();
                    if(path.isDirectory())
                    {
                        dataPathString=path.getPath();
                        //dataPathString.replaceAll("\\", "/");
                        dataPathStringBuffer=new StringBuffer(dataPathString);
                        dataPathStringBuffer.append("\\Chat_file\\");
                        dataPath.setText(dataPathStringBuffer.toString());
                        path=new File(dataPathStringBuffer.toString());
                        if(!path.exists()) path.mkdirs();
                    }
                    else
                    {
                        MyDialogFrame dialogFrame=new MyDialogFrame();
                        SwingUtilities.invokeLater(() -> {
                            dialogFrame.dialogInit("small",langLocal.dataPathHint);
                        });
                    }
                }
            }
        });
		configFrame.add(changePath);

        JButton open=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		open.setBorderPainted(false);
        open.setFocusPainted(false);
		open.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		open.setBounds(calres(224),calres(222),calres(150),calres(48));
		open.setText(langLocal.open);
		open.setHorizontalTextPosition(SwingConstants.CENTER);
        open.setVerticalTextPosition(SwingConstants.CENTER);
		open.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		open.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String[] exeString={"explorer.exe",dataPathStringBuffer.toString()};
                    Runtime.getRuntime().exec(exeString);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
		configFrame.add(open);

        JButton save=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
		save.setBorderPainted(false);
        save.setFocusPainted(false);
		save.setFont(new Font("微软雅黑",Font.PLAIN,calres(18)));
		save.setBounds(calres(224),calres(486),calres(150),calres(48));
		save.setText(langLocal.save);
		save.setHorizontalTextPosition(SwingConstants.CENTER);
        save.setVerticalTextPosition(SwingConstants.CENTER);
		save.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		save.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
		save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File oldPath=new File(settingLocal.dataPath);
                copyDir(oldPath, path);
                settingLocal.dataPath=dataPathStringBuffer.toString();
                settingLocal.language=language;
                File settingFile=new File("setting.json");
                if (!settingFile.exists()) {
                    try {
                        settingFile.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                try (
                    JSONWriter writer = new JSONWriter(new FileWriter(settingFile))) {
                    writer.writeValue(settingLocal);
                    MyDialogFrame dialogFrame=new MyDialogFrame();
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("small",langLocal.saveSuccess);
                    });
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
		configFrame.add(save);

        configFrame.setVisible(true);
    }

    public static void copyFile(File file, File file1) {
        try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(file1)) {
            byte[] bys = new byte[1024];
            int len;
            while ((len = fis.read(bys)) != -1) {
                fos.write(bys, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyDir(File file, File file1) {
        if (!file.isDirectory()) {
            return;
        }
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                copyDir(f, new File(file1.getPath(), f.getName()));
            } else if (f.isFile()) {
                copyFile(f, new File(file1.getPath(), f.getName()));
            }
        }
    }
}
