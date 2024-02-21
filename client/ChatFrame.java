package client;

import pak.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

public class ChatFrame extends MyFrame {

    private static final long serialVersionUID = -4868301746666412584L;

    private static JPanel chatPanel = new JPanel();
    final static int WIDTH = 792;
    final static int HEIGHT = 772;

    JTextArea textArea;
    MyTextPane textAreaLog;
    //AttributeSetUtil attribute=new AttributeSetUtil();
    private HTMLDocument text_html;
    private HTMLEditorKit htmledit;
    Element body = null;
    int fontName=calres(16);
    int fontText=calres(19);

    String friend, friendID;

    User user;

    public ChatFrame(String f, User u) {
        friend = f;
        user=u;
        friendID=new SearchInMySQL().getIDbyName(f);
        htmledit=new HTMLEditorKit(); //Used to edit and parse content used to display in jtextpane.
        text_html=(HTMLDocument) htmledit.createDefaultDocument();
        Element[] roots = text_html.getRootElements();
        for (int i = 0; i < roots[0].getElementCount(); i++) {
            Element element = roots[0].getElement(i);
            if (element.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.BODY) {
                body = element;
                break;
            }
        }
        if(friend=="多人聊天室")
        {
            try {
                text_html.insertBeforeEnd(body, "<p align=\"center\" style=\"font-family:微软雅黑;color:rgb(165,165,165)font-size:"+calres(12)+"px;margin:0px 0;\"> 欢迎进入多人聊天室");
            } catch (BadLocationException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                ObjectOutputStream oos = new ObjectOutputStream(C2SThreadHashMap.getC2SThread(user.getID()).getSocket().getOutputStream());
                Message message=new Message();
                message.setMessageType(MessageType.MESSAGE_TEST);
                message.setMessage(user.getID()+"进入聊天室");
                message.setSender(user.getID());
                message.setReceiver(friendID);
                message.setDate(new Date());
                oos.writeObject(message);
                //System.out.println(message.getMessage());
                //System.out.println("消息已发送");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public JPanel chatInit()
    {
        try {
            initializeData();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        chatPanel.setSize(calres(WIDTH),calres(HEIGHT));
        chatPanel.setLocation(calres(490),calres(92));
        chatPanel.setBackground(new Color(245, 245, 245));
        chatPanel.setOpaque(false);
        chatPanel.setLayout(null);

        JButton sendButton=new JButton(setIcon("btn_normal.png",calres(150),calres(48)));
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
		sendButton.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
		sendButton.setBounds(calres(644),calres(705),calres(150),calres(48));
		sendButton.setText(langLocal.send);
		sendButton.setHorizontalTextPosition(SwingConstants.CENTER);
        sendButton.setVerticalTextPosition(SwingConstants.CENTER);
		sendButton.setRolloverIcon(setIcon("btn_hover.png",calres(150),calres(48)));
		sendButton.setPressedIcon(setIcon("btn_press.png",calres(150),calres(48)));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message message = new Message();
                String mess = textArea.getText();
                message.setMessage(mess);
                message.setDate(new Date());
                //if the message in the text area is empty
                if (mess.length()==0) {
                    MyDialogFrame dialogFrame=new MyDialogFrame();
                    SwingUtilities.invokeLater(() -> {
                        dialogFrame.dialogInit("small",langLocal.messageCannotBeEmpty);
                    });
                }
                else
                {
                    //Sync information to user's chat window
                    //Document docs = textAreaLog.getDocument();//get the current document
                    try {
                        text_html.insertBeforeEnd(body, "<p align=\"right\" style=\"font-family:微软雅黑;color:rgb(6,203,104);font-size:"+fontName+"px;margin:0px 0;\">"+user.getName()+" "+message.getDate());
                        text_html.insertBeforeEnd(body, "<p align=\"right\" style=\"font-family:微软雅黑;font-size:"+fontText+"px;margin:"+calres(5)+"px 0;\">"+textArea.getText());
                    } catch (BadLocationException | IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(C2SThreadHashMap.getC2SThread(user.getID()).getSocket().getOutputStream());
                        message.setMessageType(MessageType.MESSAGE_COMM_AVG);
                        message.setSender(user.getID());
                        message.setReceiver(friendID);
                        oos.writeObject(message);
                        //clear the text area
                        textArea.setText("");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        chatPanel.add(sendButton);

        JScrollPane scrollpaneLog = new JScrollPane();// create a scroll pane to put the textarea
        scrollpaneLog.setBounds(calres(0),calres(0),calres(792),calres(518));
		scrollpaneLog.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrollpaneLog.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		textAreaLog=new MyTextPane();
		textAreaLog.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
		textAreaLog.setBounds(calres(0),calres(0),calres(792),calres(518));
		textAreaLog.setBackground(new Color(245,245,245));
        textAreaLog.setBorder(null);
        textAreaLog.setBorder(new CompoundBorder(textAreaLog.getBorder(),new EmptyBorder(calres(5),calres(15),0,0)));
        textAreaLog.setEditable(false);

        textAreaLog.setEditorKit(htmledit);//support html
        //Set the content type of document to be processed by the editor, there are three types: text/html, text/rtf.text/plain.
        textAreaLog.setContentType("text/html");
        textAreaLog.setDocument(text_html);//Sets a document associated with the editor.

		scrollpaneLog.setViewportView(textAreaLog);//put textarea to the scroll pane
		chatPanel.add(scrollpaneLog);

        JScrollPane scrollpaneText=new JScrollPane();//create a scroll pane to put the textarea
		scrollpaneText.setBounds(calres(0),calres(579),calres(792),calres(125));
		scrollpaneText.getVerticalScrollBar().setUI(new MyScrollBarUI());
        scrollpaneText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		textArea=new JTextArea();
		textArea.setFont(new Font("微软雅黑",Font.PLAIN,calres(20)));
		textArea.setBounds(calres(0),calres(579),calres(792),calres(125));
		textArea.setBackground(new Color(245,245,245));
        textArea.setBorder(null);
        textArea.setBorder(new CompoundBorder(textArea.getBorder(),new EmptyBorder(calres(5),calres(15),0,0)));
		textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollpaneText.setViewportView(textArea);//put textarea to the scroll pane
		chatPanel.add(scrollpaneText);

        return chatPanel;
    }

    public void updateText(Message m)
    {
        String senderName=new SearchInMySQL().getNamebyID(m.getSender());
        try {
            text_html.insertBeforeEnd(body, "<p align=\"left\" style=\"font-family:微软雅黑;color:rgb(0,0,255);font-size:"+fontName+"px;margin:0px 0;\">"+senderName+" "+m.getDate());
            text_html.insertBeforeEnd(body, "<p align=\"left\" style=\"font-family:微软雅黑;font-size:"+fontText+"px;margin:"+calres(5)+"px 0;\">"+m.getMessage());
        } catch (BadLocationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
