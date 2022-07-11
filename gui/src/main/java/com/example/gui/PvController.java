package com.example.gui;

import com.example.mutual.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class PvController {

    protected String otherPerson;
    protected String currentUser;
    protected UserShort meShort;
    protected UserShort youShort;
    protected ArrayList<UserShort> directChats = new ArrayList<>();
    protected ArrayList<String> servers= new ArrayList<>();
    protected ArrayList<Message> messages= new ArrayList<>();
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    protected Boolean isMessageReader=false;

    @FXML
    protected GridPane messages_grid;

    @FXML
    protected GridPane title_grid;

    @FXML
    protected ScrollPane messages_scroll;

    @FXML
    protected GridPane directs_grid;

    @FXML
    protected TextArea message_textField;

    @FXML
    protected Button send_button;

    @FXML
    protected Button file_button;

    @FXML
    protected GridPane servers_grid;

    @FXML
    protected Label pv_name;

    @FXML
    protected Label server_name;

    public PvController(ObjectInputStream in,ObjectOutputStream out,ObjectInputStream fin,ObjectOutputStream fout,String currentUser,String otherPerson){
        this.out = out;
        this.in = in;
        this.fin = fin;
        this.fout = fout;
        this.currentUser = currentUser;
        this.otherPerson = otherPerson;
    }

    @FXML
    public void initialize() {
        new GoToPv(this).restart();
    }

    public void addnewServer(){
        ;
    }


    void addDirects() {
        directs_grid.getChildren().clear();
        directs_grid.setVgap(5);
        directs_grid.setAlignment(Pos.CENTER);
        for (UserShort user : directChats) {
            Node pic = user.profileStatus(25.0);
            directs_grid.addColumn(0, pic);
            Button name = new Button(user.getUsername());
            name.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("pv-view.fxml"));
                    PvController pvController = new PvController(in,out,fin,fout,currentUser,((Button)event.getSource()).getText());
                    fxmlLoader.setController(pvController);
                    Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                    Scene scene = null;
                    try {
                        scene = new Scene(fxmlLoader.load(), 1000, 600);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stage.setScene(scene);
                    stage.show();
                }
            });
            name.setPrefSize(150,40);
            directs_grid.addColumn(1, name);
        }
    }


    public void addServers(){
        servers_grid.getChildren().clear();
        servers_grid.setVgap(5);
        Button btn = new Button("Discord");
        PvController pcOld = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isMessageReader) {
                    try {
                        out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in,out,fin,fout,currentUser);
                fxmlLoader.setController(friendsController);
                Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scene);
                stage.show();
            }
        });

        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);
        PvController pc = this;
        for(String s : servers){
            btn = new Button(s);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("channel-view.fxml"));
                    ChannelController channelController = new ChannelController(in,out,fin,fout,currentUser,((Button)(event.getSource())).getText(),"general");
                    fxmlLoader.setController(channelController);
                    Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                    Scene scene = null;
                    try {
                        scene = new Scene(fxmlLoader.load(), 1000, 600);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stage.setScene(scene);
                    stage.show();

                }
            });
            btn.setPrefHeight(40);
            btn.setPrefWidth(servers_grid.getPrefWidth());
            servers_grid.addColumn(0,btn);
        }

        btn = new Button("add server");
        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);

    }

    public void addMessages(){
        messages_grid.getChildren().clear();
        messages_grid.setVgap(5);
        for (Message message : messages){
            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().add(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
            if(message.getSourceInfo().get(0).equals(currentUser)){
                textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px;");
                messages_grid.addColumn(0,new Text(""));
                messages_grid.addColumn(2, meShort.profileStatus(25.0));
            }
            else {
                textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
                messages_grid.addColumn(0, youShort.profileStatus(25.0));
                messages_grid.addColumn(2, new Text(""));
            }
            if(message instanceof FileMessage){
                RadioButton download = new RadioButton("download");
                download.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ((RadioButton) event.getSource()).setDisable(true);
                        downloadFile(message.getText());
                    }
                });
                textFlow.getChildren().add(download);
            }

            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(Border.stroke(Color.BLACK));
            textFlow.setPrefWidth(430);
            messages_grid.addColumn(1, textFlow);
        }
        messages_scroll.setVvalue(1.0);
    }

    public void addNewMessage(Message message){
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
        if(message.getSourceInfo().get(0).equals(currentUser)){
            textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px;");
            messages_grid.addColumn(0,new Text(""));
            messages_grid.addColumn(2, meShort.profileStatus(25.0));
        }
        else {
            textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
            messages_grid.addColumn(0, youShort.profileStatus(25.0));
            messages_grid.addColumn(2, new Text(""));
        }
        if(message instanceof FileMessage){
            RadioButton download = new RadioButton("download");
            download.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ((RadioButton) event.getSource()).setDisable(true);
                    downloadFile(message.getText());
                }
            });
            textFlow.getChildren().add(download);
        }

        textFlow.setPadding(new Insets(5));
        textFlow.setBorder(Border.stroke(Color.BLACK));
        textFlow.setPrefWidth(430);
        messages_grid.addColumn(1, textFlow);
        messages_scroll.setVvalue(1.0);
    }

    public void sendTextMessage(){
        new SendTextPv(this,message_textField.getText()).restart();
        message_textField.clear();
    }

    public void sendFileMessage(){
        Command cmd = null;
        cmd = Command.upload(otherPerson,null,null,false);
        try {
            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> senderInfo = new ArrayList<>(Arrays.asList(currentUser));
        FileUploader fileUploader = new FileUploader(fout, senderInfo);
        fileUploader.start();
    }

    private void downloadFile(String filename) {
        try {
            Command  cmd = Command.download(currentUser,otherPerson ,filename, false);

            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileDownloader fileDownloader = new FileDownloader(fin);
        fileDownloader.start();
    }
}


class SendTextPv extends Service<Void>{
    private PvController pc;
    private String body;

    public SendTextPv(PvController pc,String body){
        this.pc= pc;
        this.body = body;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TextMessage msg = new TextMessage(pc.currentUser,body,LocalDateTime.now());
                Command cmd = Command.newPvMsg(pc.currentUser,pc.otherPerson,msg);
                pc.out.writeObject(cmd);
                return null;
            }
        };
    }
}

class GoToPv extends Service<Void>{

    private PvController pc;

    public GoToPv(PvController pc){
        this.pc = pc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getDirectChats(pc.currentUser);
                pc.out.writeObject(cmd);
                Data dt = (Data) pc.in.readObject();
                System.out.println(dt.getKeyword());
                pc.directChats = (ArrayList<UserShort>) dt.getPrimary();
                pc.out.writeObject(Command.userServers(pc.currentUser));
                dt = (Data) pc.in.readObject();
                pc.servers = (ArrayList<String>)dt.getPrimary();
                pc.out.writeObject(Command.getUser(pc.currentUser));
                dt = (Data) pc.in.readObject();
                System.out.println(dt.getKeyword()+"!");
                pc.meShort =(UserShort) dt.getSecondary();
                pc.out.writeObject(Command.getUser(pc.otherPerson));
                dt =(Data) pc.in.readObject();
                pc.youShort = (UserShort) dt.getSecondary();
                pc.out.writeObject(Command.getPvMsgs(pc.currentUser,pc.otherPerson,5));
                dt =(Data) pc.in.readObject();
                pc.messages = (ArrayList<Message>) dt.getPrimary();
                pc.out.writeObject(Command.tellPv(pc.currentUser,pc.otherPerson));
                pc.in.readObject();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        pc.pv_name.setText(pc.otherPerson);
        pc.title_grid.addColumn(0,pc.youShort.profileStatus(25.0));
        pc.addServers();
        pc.addMessages();
        pc.addDirects();
        MessageReaderPv mr = new MessageReaderPv(pc);
        mr.setDaemon(true);
        mr.start();
    }
}


class MessageReaderPv extends Thread {

    private PvController pc;

    public MessageReaderPv(PvController pc) {
        this.pc = pc;
    }

    @Override
    public void run() {
        System.out.println("message reader started");;
        pc.isMessageReader=true;
        Message message = null;
        Data data= null ;

        while (true) {
            System.out.print(" ");
            try {
                Object obj = pc.in.readObject();
                if(  obj instanceof String){
                    System.out.println("data is string "+obj);
                }
                else {
                    data = (Data) obj;
                    System.out.println(data.getKeyword() + " message reader");
                }
                if(data.getKeyword().equals("fake")){
                    continue;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                pc.isMessageReader=false;
                break;
            }
            if (data.getKeyword().equals("exitChat")) {
                System.out.println("got the exit chat");
                pc.isMessageReader=false;
                return;
            }
            if(data.getKeyword().equals("newPvMsg")){
                message = (Message) data.getPrimary();
                pc.messages.add(message);
                final Message msg = message;
                Platform.runLater(()->{
                    pc.addNewMessage(msg);
                });
            }

        }

    }

}
