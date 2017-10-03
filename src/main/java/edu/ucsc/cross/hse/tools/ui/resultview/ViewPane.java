package edu.ucsc.cross.hse.tools.ui.resultview;

import bs.gui.components.menu.OneClickSelectionMenu;
import com.be3short.data.cloning.ObjectCloner;
import com.be3short.jfx.event.menu.ActionDefinition;
import com.be3short.jfx.event.menu.ActionEventHandler;
import java.util.Arrays;
import java.util.Collections;
import edu.ucsc.cross.hse.core.obj.config.DataSettings;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ViewPane extends ActionEventHandler
{

	public ChartView getChart()
	{
		return chart;
	}

	public SplitPane getParentPane()
	{
		return parentPane;
	}

	public void setParentPane(SplitPane parent)
	{
		parentPane = parent;
	}

	DataSet environmentOperator;
	private BorderPane settingsPane;

	public BorderPane mainPane;
	private ViewWindow parent;

	public ViewWindow getParent()
	{
		return parent;
	}

	public VisualizationType viewType;
	private SplitPane parentPane;
	private MenuBar settingsMenu;
	private DataView dataView;
	private ChartView chart;

	public ViewPane(DataSet environment_operator, ViewWindow parent, SplitPane parent_pane)
	{
		super(VisualizationPaneActions.CLEAR_CONSOLE.rootItems());
		environmentOperator = environment_operator;
		super.loadMenuDefinitions(true, ChartActions.EDIT.rootItems());
		super.loadMenuDefinitions(true, VisualizationPaneActions.CLOSE.rootItems());
		this.parent = parent;
		parentPane = parent_pane;
		viewType = VisualizationType.defaultVisualizationType();

		setupComponents(viewType);
	}

	@Override
	public Object respondToEvent(Object event)
	{
		try
		{
			VisualizationType action = (VisualizationType) this.menuItemSelected.get();

			// chart.selectChartType(action);
		} catch (Exception eeee)
		{
			try
			{
				VisualizationPaneActions action = (VisualizationPaneActions) this.menuItemSelected.get();

				handleVisualizationPaneEvent(action, event);
			} catch (Exception e)
			{
				try
				{
					// System.out.println(this.menuItemSelected.get());
					dataView.handleAction((ActionDefinition) this.menuItemSelected.get(), event);
				} catch (Exception ee)
				{
					// try
					// {
					//
					// File file = (new FileChooser()).showSaveDialog(new Stage());
					//
					// {
					// parent.captureSVG(file);
					// // SVGGraphics2D graphics = new SVGGraphics2D(1000, 1000);
					// // Rectangle r = new Rectangle(0, 0, 1000, 1000);
					// // chart.drawChartSVG(graphics, r);
					// // File f = new File("SVGChartOutput.svg");
					// // SVGUtils.writeToSVG(f, graphics.getSVGElement());
					// // parent.captureImage(file, format);
					// }
					// } catch (Exception notSave)
					// {
					//
					// }
					// ee.printStackTrace();
				}
				// e.printStackTrace();
			}
		}
		this.menuItemSelected.set(null);
		return null;
	}

	public BorderPane getMainPane()
	{
		return mainPane;
	}

	private void setupContainers()
	{
		settingsPane = new BorderPane();
		settingsMenu = new MenuBar();
		mainPane = new BorderPane();

	}

	private void setupComponents(VisualizationType chart_type)
	{
		setupContainers();
		Platform.runLater(() ->
		{
			try
			{
				setupMenus();
				setupOptionsFlag();
				// settingsPane.setLeft(axisSelectBox);
				// Pane);
				// mainPane.setBottom(null);
				// setupOptionsFlag();
			} catch (Exception ex)
			{
				// ex.printStackTrace();
			}
		});
		settingsPane.setCenter(settingsMenu);
		chart = new ChartView(environmentOperator);
		dataView = chart;
		mainPane.setCenter(chart.getPane());
		mainPane.setBottom(settingsPane);
		// ResizeHelper.addListenerDeeply(mainPane, new
		// BorderPaneResizeListener(mainPane));

	}

	private void setupMenus()
	{
		// setupAxisMenu();
		ArrayList<ActionDefinition> defs = new ArrayList<ActionDefinition>(
		// Arrays.asList(ChartActions.AXIS.rootItems()));
		// defs.add(0, VisualizationPaneActions.FILE);
		// defs.addAll(
		Arrays.asList(new ActionDefinition[]
		{ VisualizationPaneActions.FILE }));

		// settingsMenu.getMenus().addAll(xAxisSelection, yAxisSelection);
		for (ActionDefinition def : defs)
		{
			settingsMenu.getMenus().add((Menu) getMenuItemFromDefinition(def));
		}
		for (MenuItem menuItem : dataView.getMenuItems())
		{
			try
			{
				Menu menu = (Menu) menuItem;
				settingsMenu.getMenus().add(menu);
			} catch (Exception e)
			{

			}
		}
	}

	private void setupAxisMenu()
	{
		ArrayList<String> axisOptions = chart.getFieldNames();// new ArrayList<String>();
		// environmentOperator.getLoadedEnvironment().get().getData().getAllStateNames();
		axisOptions.add(ChartProperties.simulationTimeVarName);
		OneClickSelectionMenu xAxisSelection = new OneClickSelectionMenu("X Axis", "X",
		ObjectCloner.deepInstanceClone(axisOptions), this, ChartActions.SELECT_X_AXIS);
		OneClickSelectionMenu yAxisSelection = new OneClickSelectionMenu("Y Axis", "Y",
		ObjectCloner.deepInstanceClone(axisOptions), this, ChartActions.SELECT_Y_AXIS);
		Menu axis = (Menu) getMenuItemFromDefinition(ChartActions.AXIS);// (xAxisSelection, ChartActions.SELECT_X_AXIS);
		ArrayList<MenuItem> remove = new ArrayList<MenuItem>();
		for (MenuItem axisItem : axis.getItems())
		{
			if (axisItem.getText().equals("Select X") || axisItem.getText().equals("Select Y"))
			{
				remove.add(axisItem);
			}
		}
		axis.getItems().removeAll(remove);
		axis.getItems().add(0, yAxisSelection);
		axis.getItems().add(1, xAxisSelection);

	}

	private void setupOptionsFlag()
	{
		mainPane.getCenter().setOnMouseClicked(new EventHandler<MouseEvent>()
		{

			@Override
			public void handle(MouseEvent mouseEvent)
			{
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
				{
					if (mouseEvent.getClickCount() == 2)
					{
						if (mainPane.getBottom() == null)
						{
							mainPane.setBottom(settingsPane);
						} else
						{
							mainPane.setBottom(null);
						}
					}
				}
			}
		});
	}

	public void displayMenu(boolean visible)
	{
		if (visible)

		{
			mainPane.setBottom(settingsPane);
		} else
		{
			mainPane.setBottom(null);
		}

	}

	public Object handleVisualizationPaneEvent(VisualizationPaneActions action, Object choice)
	{
		try
		{
			switch (action)
			{
			case SPLIT_HORIZONTALLY:
			{
				parent.split(this, Orientation.HORIZONTAL);
				break;
			}
			case SPLIT_VERTICALLY:
			{
				parent.split(this, Orientation.VERTICAL);
				break;
			}
			case CLOSE:
			{
				parentPane.getItems().remove(this.getMainPane());
				break;
			}
			case SAVE:
			{
				parent.captureSVG(new FileChooser().showSaveDialog(new Stage()));
				break;
			}

			case SAVE_WINDOW:
			{
				parent.captureSVG(new FileChooser().showSaveDialog(new Stage()));
				break;
			}

			}
		} catch (Exception notPane)
		{

		}
		return null;
	}

}
