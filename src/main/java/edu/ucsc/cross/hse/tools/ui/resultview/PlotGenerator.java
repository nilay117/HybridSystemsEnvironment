package edu.ucsc.cross.hse.tools.ui.resultview;

import edu.ucsc.cross.hse.core.obj.data.DataSet;
import javafx.application.Application;
import javafx.stage.Stage;

public class PlotGenerator extends Application
{

	private DataSet data;

	public PlotGenerator(DataSet data)
	{
		this.data = data;
	}

	public PlotGenerator()
	{
		this.data = null;
	}

	public static void openNewResultWindow(DataSet env_data)
	{
		// ResultWindow window = new ResultWindow(env_data);
		ResultWindow.results.put(env_data.toString(), env_data);
		// ResultWindow app = new ResultWindowApplication(window);
		Application.launch(ResultWindow.class, env_data.toString());
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		// TODO Auto-generated method stub

	}

}
