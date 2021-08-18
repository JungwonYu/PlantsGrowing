package application.main.Controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.sound.midi.ControllerEventListener;

import application.dao.DBConnector;
import application.firstLogin.Controller.LoginController;
import application.firstLogin.Users.UserInfo;
import application.main.weather.weatherCrawl;
import application.main.weather.weatherVO;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.TextArea;

public class Controller implements Initializable, ControlInterface {
	
	/////////////// ImageView = �׸� Ʋ
	@FXML
	private ImageView myPlantView, waterEffect, chatBubbleView, lightEffect,loveEffect, snailEffect,helpView;
	@FXML
	private Button btn_play, btn_pause, btn_water, btn_sun, btn_heart, btn_snail, btn_weather, btnShowSlide,
			btnHideSlide, btn_serverOn, btn_serverOff, btn_previouspage,btn_helpPrev, btn_helpPrev1, btn_helpPrev2, 
			btn_helpPrev3, btn_helpPrev4, btn_helpNxt2,btn_helpNxt3, btn_helpNxt4, btn_helpNxt5, btn_helpNxt6;
	@FXML 
	private Label lbl_plantName, lbl_wthr_location, lbl_wthr_temperature, lbl_wthr_status;
	
	@FXML
	private AnchorPane scenePane;

	
	private File directory;
	private File[] files;
	private ArrayList<File> songs;

	// db, ���� ������ ��������Ʈ�� ����
	UserInfo user = LoginController.UserList.get(0);
	DBConnector db = new DBConnector();
	Socket socket;
	

	private Media bgm;
	private MediaPlayer mP;
	
	// user ��ü�� ����Ǿ��ִ� ������ ������ �޾ƿ��� ����
	private int loveCount = user.getCaring();
	private int lightCount = user.getTanning();
	private int waterCount = user.getWatering();
	private int snailCount = user.getNutrition();
	private int level = user.getLevel();
	private int randRate = 0;

	@FXML
	private TextField TextField;
	@FXML
	private TextArea TextArea;
	@FXML
	private AnchorPane paneslide;
	
	@FXML
	private AnchorPane panehelp;
	
	
	// ����ȭ�鿡�� PlantName �Ѿ���Բ� ����� �޼���
	// ������ ���ÿ� �Ѿ���鼭 �ٷ� ���� �� ���� ����
	public void setPname(String pName) {
		lbl_plantName.setText(user.getPlantName());
		if(user.getLevel()>=3) {
			myPlantView.setImage(plantLevel3);
		}
		else if(user.getLevel() == 2) {
			myPlantView.setImage(plantLevel2);
		}
	}
	
	// ������, � ����� dataRefresh
	public void dataRefresh() throws SQLException {
		
		loveCount = 0;
		waterCount = 0;
		lightCount = 0;
		snailCount = 0;
		
		user.setCaring(0);
		user.setNutrition(0);
		user.setTanning(0);
		user.setWatering(0);
		db.updateAll(user);
	
	}
	
	
	/////////////// ������ �Ĺ�, ȿ��, ���� �̹���
	Image plantLevel2 = new Image(getClass().getResourceAsStream("../View/css/ct2.png"));
	Image plantLevel3 = new Image(getClass().getResourceAsStream("../View/css/ct3.png"));
	Image waterEffect1 = new Image(getClass().getResourceAsStream("../View/css/waterEffect.png"));
	Image lightEffect1 = new Image(getClass().getResourceAsStream("../View/css/lightEffect.png"));
	Image loveEffect1 = new Image(getClass().getResourceAsStream("../View/css/loveEffect.png"));
	Image snailEffect1 = new Image(getClass().getResourceAsStream("../View/css/snailEffect.png"));
	Image chatBubble1 = new Image(getClass().getResourceAsStream("../View/css/chatBubble_smile.png"));
	Image chatBubble2 = new Image(getClass().getResourceAsStream("../View/css/chatBubble_soso.png"));
	Image chatBubble3 = new Image(getClass().getResourceAsStream("../View/css/chatBubble_angery.png"));
	Image helpPage1 = new Image(getClass().getResourceAsStream("../View/css/help_1.png"));
	Image helpPage2 = new Image(getClass().getResourceAsStream("../View/css/help_2.png"));
	Image helpPage3 = new Image(getClass().getResourceAsStream("../View/css/help_3.png"));
	Image helpPage4 = new Image(getClass().getResourceAsStream("../View/css/help_4.png"));
	Image helpPage5 = new Image(getClass().getResourceAsStream("../View/css/help_5.png"));
	
	
	
	@FXML
	public void hideSlide(MouseEvent event) {
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(0.4));
		slide.setNode(paneslide);
		slide.setToX(700);
		slide.play();

		paneslide.setTranslateX(0);
	}

	@FXML
	public void showSlide(MouseEvent event) {
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(0.4));
		slide.setNode(paneslide);
		slide.setToX(0);
		slide.play();

		paneslide.setTranslateX(0);
	}

	/////////////// ��� �� ȿ����
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		songs = new ArrayList<File>();
		directory = new File("music");
		files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				songs.add(file);
			}
		}

		{
			paneslide.setTranslateX(650);

		}
		{
			panehelp.setTranslateX(1000);
		}

		/////////////////// ��� �� ȿ����
		bgm = new Media(songs.get(4).toURI().toString());
		mP = new MediaPlayer(bgm);

	}
	
	public void fadeAction() {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), panehelp);
		fadeTransition.setFromValue(0.0f);
		fadeTransition.setToValue(1.0f);
		// fadeTransition.setCycleCount(2);
		fadeTransition.setAutoReverse(true);
		fadeTransition.play();
	}

	public void helpAction(ActionEvent e) {
		//helpView.setImage(helpPage1);
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(0.1));
		slide.setNode(panehelp);
		slide.setToX(0);
		slide.play();
		System.out.println("hello");
		btn_helpPrev.setVisible(true);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(true);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
		
	}
	
	public void helpPrev(ActionEvent e) {
		//fadeAction();
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(0.4));
		slide.setNode(panehelp);
		slide.setToX(1000);
		slide.play();
		panehelp.setTranslateX(0);
	}

	public void helpTo2Action(ActionEvent e) {
		helpView.setImage(helpPage2);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(true);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(true);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
		
	}

	public void helpTo3Action(ActionEvent e) {
		helpView.setImage(helpPage3);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(true);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(true);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
	}

	public void helpTo4Action(ActionEvent e) {
		helpView.setImage(helpPage4);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(true);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(true);
		btn_helpNxt6.setVisible(false);
	}

	public void helpTo5Action(ActionEvent e) {
		helpView.setImage(helpPage5);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(true);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(true);
	}
	
	public void helpTo6Action(ActionEvent e) {
		
		
		TranslateTransition slide = new TranslateTransition();
		slide.setDuration(Duration.seconds(0.01));
		slide.setNode(panehelp);
		slide.setToX(1000);
		slide.play();
		panehelp.setTranslateX(0);
	}
	public void helpTo1Prev(ActionEvent e) {
		helpView.setImage(helpPage1);
		btn_helpPrev.setVisible(true);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(true);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
	}

	public void helpTo2Prev(ActionEvent e) {
		helpView.setImage(helpPage2);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(true);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(true);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
	}

	public void helpTo3Prev(ActionEvent e) {
		helpView.setImage(helpPage3);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(true);
		btn_helpPrev3.setVisible(false);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(true);
		btn_helpNxt5.setVisible(false);
		btn_helpNxt6.setVisible(false);
	}

	public void helpTo4Prev(ActionEvent e) {
		helpView.setImage(helpPage4);
		btn_helpPrev.setVisible(false);
		btn_helpPrev1.setVisible(false);
		btn_helpPrev2.setVisible(false);
		btn_helpPrev3.setVisible(true);
		btn_helpPrev4.setVisible(false);
		btn_helpNxt2.setVisible(false);
		btn_helpNxt3.setVisible(false);
		btn_helpNxt4.setVisible(false);
		btn_helpNxt5.setVisible(true);
		btn_helpNxt6.setVisible(false);
	}


	public void playMedia(ActionEvent e) {
		mP.setVolume(0.1);
		mP.setOnEndOfMedia(new Runnable() {
			public void run() {
				mP.seek(Duration.ZERO);
			}
		});
		mP.play();
	}

	public void pauseMedia(ActionEvent e) {
		mP.pause();
	}
	
	
	// ��ưŬ�� �� ���� Ȯ���� ������Ű ����
	public void fortunecookie() throws SQLException {

		randRate = (int)(Math.random()*10 +1);	
		
		if(randRate < 3) {
			
			Alert oonseAlert = new Alert(AlertType.INFORMATION);
			oonseAlert.setTitle("������ �Ѹ���");
			oonseAlert.setContentText(db.pickFortune());
			oonseAlert.showAndWait();

		}
	}
	
	//light , water ��ư�� ���������� ����Ǵ� �޼���
	//�, ������, ê���� ����
	public void buttonPressing(int eachCount, String element, PauseTransition pause) throws SQLException {

		int numberOfClicks = 0;
		
		if (element.equals("water")) {
			numberOfClicks = 3;
		} else if (element.equals("light")) {
			numberOfClicks = 2;
		}
		
		if (eachCount == numberOfClicks) {

			chatBubbleView.setImage(chatBubble1);
			pause.setOnFinished(a -> chatBubbleView.setImage(null));
			pause.play();

		} else if (eachCount == numberOfClicks+1) {

			chatBubbleView.setImage(chatBubble2);
			pause.setOnFinished(a -> chatBubbleView.setImage(null));
			pause.play();

		} else if (eachCount == numberOfClicks+2) {

			chatBubbleView.setImage(chatBubble3);
			pause.setOnFinished(a -> chatBubbleView.setImage(null));
			pause.play();

		} else if (20 < waterCount + lightCount + snailCount) {
			dataRefresh();
		} 
		
		// ������ ����
		if (waterCount == 3 && lightCount == 2 && loveCount >= 2 && snailCount == 1) {
			
			Alert oonseAlert = new Alert(AlertType.INFORMATION);
			oonseAlert.setTitle("���� ��!");
			if(level < 3) {
				level++;
				user.setLevel(level);
				db.updateAll(user);
				dataRefresh();

				oonseAlert.setContentText("����"+ level + " �� �����̽��ϴ�!");
				oonseAlert.showAndWait();
			}else if(level >= 3) {	
				dataRefresh();
				
				oonseAlert.setTitle("����");
				oonseAlert.setContentText("�Ĺ��� �� �ڶ����!!!! �Ĺ��� �Բ� ��ſ� ���� ��������");
				oonseAlert.showAndWait();
				
			}

		} 
		
		switch (level) {
		case 2:
			myPlantView.setImage(plantLevel2);
			break;
		case 3:
			myPlantView.setImage(plantLevel3);
			break;
		}
		
	}
	
	public void audioClipping() {
		AudioClip m1 = new AudioClip(songs.get(1).toURI().toString());
		m1.setVolume(0.1);
		m1.play();
	}

	////////////////////////// 4���� ��ư �׼�
	public void waterAction(ActionEvent e) throws SQLException {

		audioClipping();

		waterCount++;
		waterEffect.setImage(waterEffect1);
		
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(a -> waterEffect.setImage(null));
		pause.play();
		
		fortunecookie();
		buttonPressing(waterCount, "water", pause);
		System.out.println(waterCount);
		
		
		user.setWatering(waterCount);
		db.updateAll(user);

		
	}
	
	
	public void lightAction(ActionEvent e) throws SQLException {
		audioClipping();

		lightEffect.setImage(lightEffect1);

		lightCount++;
		System.out.println(lightCount);
		
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(a -> lightEffect.setImage(null));
		pause.play();
		
		fortunecookie();
		buttonPressing(lightCount, "light", pause);
		
		user.setTanning(lightCount);
		db.updateAll(user);
		
		
	}
	// �����ư
	public void loveAction(ActionEvent e) throws SQLException {

		audioClipping();

		loveCount++;
		
		System.out.println(loveCount);
		chatBubbleView.setImage(chatBubble1);
		
		loveEffect.setImage(loveEffect1);
		
		PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
		pause1.setOnFinished(a -> loveEffect.setImage(null));
		pause1.play();
		
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(a -> chatBubbleView.setImage(null));
		pause.play();

		fortunecookie();
		
		user.setCaring(loveCount);
		db.updateAll(user);

		if (waterCount == 3 && lightCount == 2 && loveCount >= 2 && snailCount == 1) {
			dataRefresh();
			
			Alert oonseAlert = new Alert(AlertType.INFORMATION);
			oonseAlert.setTitle("���� ��!");
			if(level < 3) {
				level++;
				user.setLevel(level);
				db.updateAll(user);
				dataRefresh();

				oonseAlert.setContentText("����"+ level + " �� �����̽��ϴ�!");
				oonseAlert.showAndWait();
			}else if(level >= 3) {	
				dataRefresh();
				
				oonseAlert.setTitle("����");
				oonseAlert.setContentText("�Ĺ��� �� �ڶ����!!!! �Ĺ��� �Բ� ��ſ� ���� ��������");
				oonseAlert.showAndWait();
				
			}
			
		} else if (20 < waterCount + lightCount + snailCount) {
			dataRefresh();
			
		}
		
///////////////////���� ������ �̹��� ����
		switch (level) {
		case 2:
			myPlantView.setImage(plantLevel2);
			break;
		case 3:
			myPlantView.setImage(plantLevel3);
			break;
		
		}
		
	}

	public void snailAction(ActionEvent e) throws SQLException {
		audioClipping();

		snailCount++;
		
		snailEffect.setImage(snailEffect1);
		
		PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
		pause1.setOnFinished(a -> snailEffect.setImage(null));
		pause1.play();
		
		fortunecookie();
		
		user.setNutrition(snailCount);
		db.updateAll(user);
		
		
		
		if (waterCount == 3 && lightCount == 2 && loveCount >= 2 && snailCount == 1) {

			dataRefresh();
			
			Alert oonseAlert = new Alert(AlertType.INFORMATION);
			oonseAlert.setTitle("���� ��!");
			if(level < 3) {
				level++;
				user.setLevel(level);
				db.updateAll(user);
				dataRefresh();

				oonseAlert.setContentText("����"+ level + " �� �����̽��ϴ�!");
				oonseAlert.showAndWait();
			}else if(level >= 3) {	
				dataRefresh();
				
				oonseAlert.setTitle("����");
				oonseAlert.setContentText("�Ĺ��� �� �ڶ����!!!! �Ĺ��� �Բ� ��ſ� ���� ��������");
				oonseAlert.showAndWait();
				
			}
			
		} else if (snailCount == 2) {
			
			snailCount = 1;
			System.out.println("�� ���� ��ҷ���");
			chatBubbleView.setImage(chatBubble2);
			PauseTransition pause = new PauseTransition(Duration.seconds(2));
			pause.setOnFinished(a -> chatBubbleView.setImage(null));
			pause.play();
			
		} else if (10 < waterCount + lightCount + snailCount) {
			
			dataRefresh();
			
		} 
///////////////////���� ������ �̹��� ����
		switch (level) {
		case 2:
			myPlantView.setImage(plantLevel2);
			break;
		case 3:
			myPlantView.setImage(plantLevel3);
			break;

		}
		
	}
	
	public void goPrevious(ActionEvent e) throws IOException {
		Stage stage;
		Scene scene;
		Parent root;
		
		root = FXMLLoader.load(getClass().getResource("../../firstLogin/View/login.fxml"));
		stage = (Stage)((Node)e.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();

	}
	
	
	//127.0.0.1
	//���� on > thread������ > server ���� �޽��� ����
	@FXML
	public void serverOn(MouseEvent event) {
		btn_serverOn.setVisible(false);
		btn_serverOff.setVisible(true);
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket("127.0.0.1",user.getPort());
					System.out.println("[���� ����]");
					receive();	
				} catch (Exception e) {
					if(!socket.isClosed()) {
						try {
							socket.close();
							System.out.println("[�������� ����]");
							Platform.exit();
						} catch (IOException e1) {
						
							e1.printStackTrace();
						}
					}
				}
			}
		};
		thread.start();
	}
	
	// ���� ���� ��ư
	@FXML
	public void serverOff(MouseEvent event) {
		btn_serverOn.setVisible(true);
		btn_serverOff.setVisible(false);
		stopClient();
	}

	// �������� �����ؿ��� �޽���
	// send (thread) > thread > thread > receive
	// receive�ϰ� UTF-8�� TextArea�� append
	public void receive() {
		while(true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[1024];
				int length = in.read(buffer);
				while(length == -1) throw new IOException();
				String message = new String(buffer, 0 , length, "UTF-8");			
				System.out.println("[Ŭ���̾�Ʈ �޽��� ���� ����] : " +  message);
				Platform.runLater(()->{
					TextArea.appendText(message);
				});
			}catch (Exception e) {
				stopClient();
				break;
			}
		}
		
	}
	
	// ���� ��ư
	// sending �޼����� �Ű������� ������ �޽����� ����ش�.
	public void chatAction(ActionEvent e) {
		
		sending(user.getPlantName() + " : " + TextField.getText() + "\n");
		System.out.println("[Ŭ���̾�Ʈ �޽��� ���� ����] : " + user.getPlantName() +" : "+  TextField.getText());
		TextField.setText("");
		TextField.requestFocus();
		
	}
	
	// ���� �޽��� ���� �޼���
	// send (thread) > thread(serverClient in) > thread(serverClient out) > receive
	// �Էµ� �޽����� ������ ����(UTF-8)
	public void sending(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
					
				}catch (Exception e) {
					
					if(socket != null && !socket.isClosed()) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}	
			}
		};
		thread.start();
	}
	
	//Ŭ���̾�Ʈ ���α׷� ���� �޼���
	public void stopClient() {
		try {
			if(socket != null && !socket.isClosed()) {
				socket.close();
				System.out.println("[Ŭ���̾�Ʈ ���� ����]");
			}
		} catch (Exception e) {
			System.out.println("[Ŭ���̾�Ʈ ���� ����]");
			e.printStackTrace();
		}
		
		
	}
	
	// ���� �ҷ����� ��ư
	// ������ ũ���ؿͼ� lbl text�� set ����.
	public void weatherAction(ActionEvent e) {
		weatherVO wVO = new weatherVO();
		try {
			weatherCrawl.Crawling(wVO);
			
			lbl_wthr_location.setText(wVO.getLocation());
			lbl_wthr_temperature.setText(String.valueOf(wVO.getTemperature()));
			lbl_wthr_status.setText(wVO.getStatus());
		} catch (IOException ie) {
			
			ie.printStackTrace();
		}

	}

}