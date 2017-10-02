package edu.ucsc.cross.hse.tools.ui.resultview;

import edu.ucsc.cross.hse.core.obj.data.DataSet;
import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ResultWindow extends Application
{

	public static HashMap<String, DataSet> results = new HashMap<String, DataSet>();

	private BorderPane pane;
	private DataSet data;
	private ViewWindow window;

	public ResultWindow(DataSet data)
	{
		this.data = data;
		initialize();
	}

	public ResultWindow()
	{

	}

	private void initialize()
	{
		pane = new BorderPane();
		Platform.runLater(() ->
		{
			try
			{
				pane.setCenter(window.getMainPane());
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		});
	}

	public void loadData(DataSet data)
	{
		this.data = data;
	}

	public BorderPane getPane()
	{
		return pane;
	}

	@Override
	public void init()
	{
		try
		{
			String result = this.getParameters().getRaw().get(0);
			data = results.get(result);
		} catch (Exception e)
		{
			// e.printStackTrace();
		}

		Platform.runLater(() ->
		{
			try
			{
				window = new ViewWindow(data);
			} catch (Exception ex)
			{
				// ex.printStackTrace();
			}
		});

	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		try
		{
			initialize();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		Scene results = new Scene(pane, 500, 500);
		primaryStage.setScene(results);
		primaryStage.show();

	}

	public ViewWindow getWindow()
	{
		return window;
	}
}
