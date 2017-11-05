package edu.ucsc.cross.hse.core.task;

import javafx.application.Application;
import javafx.stage.Stage;

public abstract class TaskManager extends Application implements TaskQueue
{

	public Stage getNewStage()
	{
		return new Stage();
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		manager = this;
		taskQueue();
	}

	public TaskManager()
	{

	}

	private static TaskManager manager;

	public static Stage createStage()
	{
		if (manager != null)
		{
			return manager.getNewStage();
		} else
		{
			return null;
		}
	}

	public static void runOp(TaskQueue item)
	{
		// itemm = item;
		launch();
	}

}
