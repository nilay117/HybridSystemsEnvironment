package edu.ucsc.cross.hse.tools.ui.resultview;

import java.util.HashMap;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ResultWindowApplication extends Application
{

	public static HashMap<String, ResultWindow> results = new HashMap<String, ResultWindow>();

	@Override
	public void init()
	{
		String result = this.getParameters().getRaw().get(0);
		System.out.println(result);
		resultWindow = results.get(result);
	}

	ResultWindow resultWindow;
	BorderPane pane;

	public ResultWindowApplication(ResultWindow result_window)
	{
		this.resultWindow = result_window;
	}

	public ResultWindowApplication()
	{

	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		System.out.println("here");
		pane = new BorderPane();
		pane.setCenter(resultWindow.getPane());
		Scene results = new Scene(pane);
		primaryStage.setScene(results);
		primaryStage.show();

	}

}
