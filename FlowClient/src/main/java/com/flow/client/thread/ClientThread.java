package com.flow.client.thread;

import com.flow.client.controller.Chat;
import com.flow.client.controller.FriendList;
import com.flow.client.file.ReceiveFrame;
import com.flow.client.util.ManagerChat;
import com.flow.client.util.ManagerFriendList;
import com.flow.common.Message;
import com.flow.common.MessageType;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket socket;
    private ReceiveFrame receiveFrame;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                //客户端从服务端读取信息,读不到就一直等待
                ObjectInputStream ois = new ObjectInputStream(this.socket.getInputStream());
                Message message = (Message) ois.readObject();
                if (message.getMesType().equals(MessageType.message_comm_mes)) {
                    /** 普通消息 */
                    //显示在聊天框中
                    Chat chat = ManagerChat.get(message.getGetter() + " " + message.getSender());
                    chat.showMessage(message);
                } else if (message.getMesType().equals(MessageType.message_ret_onLineFriend)) {
                    /** 好友列表 */
                    String content = message.getCon();
                    System.out.println("好友上线通知：" + content);
                    FriendList friendList = ManagerFriendList.get(message.getGetter());
                    friendList.updateFriendList(content.trim());

                } else if (message.getMesType().equals(MessageType.message_sendfile)) {
                    /** 接收文件 */
                    if (receiveFrame == null) {
                        receiveFrame = new ReceiveFrame();
                        receiveFrame.setMs(message);
                        receiveFrame.setVisible(true);
                    } else {
                        receiveFrame.setMs(message);
                        receiveFrame.saveFile();
                        receiveFrame.setVisible(false);
                        receiveFrame = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }


    public Socket getSocket() {
        return socket;
    }

}
